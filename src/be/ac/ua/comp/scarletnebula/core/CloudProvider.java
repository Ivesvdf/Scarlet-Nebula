package be.ac.ua.comp.scarletnebula.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.services.server.ServerServices;
import org.dasein.cloud.services.server.ServerSize;

/**
 * Class representing a cloud provider in Scarlet Nebula. This will contain a
 * Dasein cloudprovider object but also implement other stuff like making a
 * cloudprovider specific configuration.
 * 
 * @author ives
 */
public class CloudProvider
{
	public enum CloudProviderName
	{
		AWS
	};

	private org.dasein.cloud.CloudProvider providerImpl;
	private Properties providerSpecificProperties;
	private ServerServices serverServices;
	private String providerClassName;

	private ArrayList<Server> servers;

	// TODO: Make this a class hierarchy
	public CloudProvider(CloudProviderName name) throws Exception
	{
		servers = new ArrayList<Server>();

		switch (name)
		{
			case AWS:
				providerClassName = "org.dasein.cloud.aws.AWSCloud";
				break;
			default:
				throw new Exception("Unsupported cloudprovider.");
		}

		try
		{
			providerSpecificProperties = new Properties();
			providerSpecificProperties.load(new FileInputStream(
					providerClassName + ".properties"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		providerImpl = (org.dasein.cloud.CloudProvider) Class.forName(
				providerClassName).newInstance();

		providerImpl.connect(getCurrentContext());

		serverServices = providerImpl.getServerServices();
		if (serverServices == null)
		{
			System.out.println(providerImpl.getCloudName()
					+ " does not support compute instances.");
			return;
		}
		// TODO: place these somewhere? maybe a menu option
		assureSSHOnlyFirewall();
		assureSSHKey();
	}

	/**
	 * Loads a server (from file!) and returns it
	 * 
	 * @param unfriendlyName
	 * @return
	 * @throws InternalException
	 * @throws CloudException
	 * @throws IOException
	 */
	public Server loadServer(String unfriendlyName) throws InternalException,
			CloudException, IOException
	{
		org.dasein.cloud.services.server.Server server = getServerImpl(unfriendlyName);

		if (server == null)
			return null;

		return Server.load(server, this);

	}

	/**
	 * Loads all servers that have a saved representation
	 * 
	 * @return
	 * @throws InternalException
	 * @throws CloudException
	 * @throws IOException
	 */
	public Collection<Server> loadLinkedServers() throws InternalException,
			CloudException, IOException
	{
		File dir = new File(Server.getSaveFileDir(this));

		String[] files = dir.list();

		if (files == null)
			return servers;

		for (String file : files)
		{
			Server server = loadServer(file);

			// If the server cannot be made it was deleted and the file
			// referencing it
			// should also be removed.
			if (server == null)
			{
				deleteServerSaveFile(file);
			}
			else
			{
				servers.add(server);
			}
		}

		return servers;
	}

	/**
	 * Delete's the server whose CloudProvider specific name is "unfriendlyName"
	 * 
	 * @param unfriendlyName
	 */
	private void deleteServerSaveFile(String unfriendlyName)
	{
		File toBeRemoved = new File(Server.getSaveFileDir(this)
				+ unfriendlyName);
		toBeRemoved.delete();
	}

	/**
	 * Creates a new key with name "keyname"
	 * 
	 * @param acs
	 * @param keyname
	 * @throws InternalException
	 * @throws CloudException
	 */
	private void createKey(org.dasein.cloud.services.access.AccessServices acs,
			String keyname) throws InternalException, CloudException
	{
		KeyManager.addKey(providerClassName, keyname,
				acs.createKeypair(keyname));

	}

	/**
	 * Assures there's an SSH key with name "sndefault". If no such key exists,
	 * it will be created
	 * 
	 * @throws InternalException
	 * @throws CloudException
	 */
	private void assureSSHKey() throws InternalException, CloudException
	{
		org.dasein.cloud.services.access.AccessServices acs = providerImpl
				.getAccessServices();

		if (!acs.list().contains("sndefault"))
		{
			createKey(acs, "sndefault");
		}

	}

	/**
	 * Assures there's a rule that only allows SSH access. If no such rule
	 * exists, it will be created.
	 * 
	 * @throws InternalException
	 * @throws CloudException
	 */
	private void assureSSHOnlyFirewall() throws InternalException,
			CloudException
	{
		org.dasein.cloud.services.firewall.FirewallServices fws = providerImpl
				.getFirewallServices();

		if (fws == null)
			return;

		Collection<org.dasein.cloud.services.firewall.Firewall> firewalls = fws
				.list();

		org.dasein.cloud.services.firewall.Firewall sshOnly = null;

		for (org.dasein.cloud.services.firewall.Firewall fw : firewalls)
		{
			if (fw.getName().equals("sshonly"))
			{
				sshOnly = fw;
				break;
			}
		}

		if (sshOnly == null)
		{
			System.out.println("Creating sshonly");
			String sshonlyId = fws.create("sshonly", "Allow only ssh traffic");
			fws.authorize(sshonlyId, "0.0.0.0/0",
					org.dasein.cloud.services.firewall.Protocol.TCP, 22, 22);
		}
	}

	/**
	 * Lists all servers the underlying Cloud Provider manages, but that are
	 * *not* by listLinkedServers() i.e. the ones that aren't managed by Scarlet
	 * Nebula at this moment.
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Server> listUnlinkedServers() throws Exception
	{
		ArrayList<Server> rv = new ArrayList<Server>();
		// List all servers
		for (org.dasein.cloud.services.server.Server testServer : serverServices
				.list())
		{
			// For each server, check if this server is already registered. Do
			// this based on his unfriendly id
			boolean found = false;
			for (Iterator<Server> registeredServerIterator = servers.iterator(); registeredServerIterator
					.hasNext() && !found;)
			{
				if (registeredServerIterator.next().getUnfriendlyName()
						.equals(testServer.getName()))
				{
					found = true;
				}
			}

			if (!found)
			{
				rv.add(Server.load(testServer, this));
			}
		}

		return rv;
	}

	/**
	 * Returns all servers that are currently being managed by this
	 * CloudProvider
	 * 
	 * @return
	 */
	public Collection<Server> listLinkedServers()
	{
		return servers;
	}

	/**
	 * Terminates the server with unfriendlyName "unfriendlyName". This method
	 * will and should only be called by Server.terminate().
	 * 
	 * @param unfriendlyName
	 * @throws InternalException
	 * @throws CloudException
	 */
	public void terminateServer(String unfriendlyName)
			throws InternalException, CloudException
	{
		serverServices.stop(unfriendlyName);
	}

	/**
	 * Starts a new server.
	 * 
	 * @param serverName
	 * @param size
	 * @return
	 * @throws InternalException
	 * @throws CloudException
	 */
	public Server startServer(String serverName, String size)
			throws InternalException, CloudException
	{
		String imageId = "ami-15765c61";
		String dataCenterId = "eu-west-1b";
		String keypairOrPassword = "sndefault";
		String vlan = "";
		String[] firewalls = new String[] { "sshonly" };

		org.dasein.cloud.services.server.Server daseinServer = serverServices
				.launch(imageId, new ServerSize(size), dataCenterId,
						serverName, keypairOrPassword, vlan, false, firewalls);

		Server server = new Server(daseinServer, this, keypairOrPassword,
				serverName);

		registerUnlinkedServer(server);
		return server;
	}

	/**
	 * Returns the ServerServices for this CloudProvider
	 * 
	 * @return
	 */
	org.dasein.cloud.services.server.ServerServices getServerServices()
	{
		return providerImpl.getServerServices();
	}

	/**
	 * Returns the endpoint this CloudProvider uses.
	 * 
	 * @return
	 */
	String getPreferredEndpoint()
	{
		return providerSpecificProperties.getProperty("preferredendpoint");
	}

	/**
	 * Returns this CloudProvider's userdefined name (the one uniquely
	 * identifying the (Provider, Endpoint, Access) pair.
	 * 
	 * @return
	 */
	String getCloudName()
	{
		return providerImpl.getCloudName();
	}

	/**
	 * Closes this CloudProvider (call it before the program ends)
	 */
	void close()
	{
		providerImpl.close();
	}

	private org.dasein.cloud.ProviderContext getCurrentContext()
	{
		String accountNumber = "0";
		String apiKey = providerSpecificProperties.getProperty("apikey");
		String privateKey = providerSpecificProperties.getProperty("apisecret");

		org.dasein.cloud.ProviderContext context = new org.dasein.cloud.ProviderContext();

		context.setAccountNumber(accountNumber);
		context.setAccessPublic(apiKey.getBytes());
		context.setAccessPrivate(privateKey.getBytes());
		context.setEndpoint(getPreferredEndpoint());

		return context;
	}

	/**
	 * Returns a collection of instance sizes that are possible.
	 * 
	 * @return
	 */
	public Collection<String> getPossibleInstanceSizes()
	{
		Collection<org.dasein.cloud.services.server.ServerSize> sizes = null;
		try
		{
			sizes = serverServices
					.getSupportedSizes(org.dasein.cloud.services.server.Architecture.I32);
		}
		catch (InternalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (CloudException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<String> rv = new ArrayList<String>();

		for (org.dasein.cloud.services.server.ServerSize size : sizes)
		{
			rv.add(size.getSizeId());
		}
		return rv;
	}

	/**
	 * Returns the underlying class' name...
	 * 
	 * @return
	 */
	public String getUnderlyingClassname()
	{
		return providerClassName;
	}

	public org.dasein.cloud.services.server.Server getServerImpl(
			String unfriendlyName) throws InternalException, CloudException
	{
		return serverServices.getServer(unfriendlyName);
	}

	/**
	 * Makes the unlinked Server "server" linked.
	 * 
	 * @param server
	 */
	public void registerUnlinkedServer(Server server)
	{
		server.store();
		servers.add(server);
	}

	/**
	 * Pauses the server in parameter. This method is only supposed to be called
	 * by Server.pause()
	 * 
	 * @param server
	 * @throws InternalException
	 * @throws CloudException
	 */
	void pause(Server server) throws InternalException, CloudException
	{
		serverServices.pause(server.getUnfriendlyName());
	}

	/**
	 * Reboots the server in parameter. This method is only supposed to be
	 * called by Server.reboot()
	 * 
	 * @param server
	 * @throws CloudException
	 * @throws InternalException
	 */
	void reboot(Server server) throws CloudException, InternalException
	{
		serverServices.reboot(server.getUnfriendlyName());
	}

	/**
	 * Unlinks the instance in parameter. After this call, this instance will no
	 * longer be linked to this CloudProvider
	 * 
	 * @param selectedServer
	 */
	public void unlink(Server selectedServer)
	{
		servers.remove(selectedServer);
		deleteServerSaveFile(selectedServer.getUnfriendlyName());
	}

	/**
	 * Returns true if this cloudprovider owns an instance named "friendlyName"
	 * 
	 * @param friendlyName
	 *            The name of the instance
	 * @return True if a linked server with name "friendlyName" exists,
	 *         otherwise false
	 */
	public boolean hasServer(String friendlyName)
	{
		for (Server s : servers)
			if (s.getFriendlyName().equals(friendlyName))
				return true;

		return false;
	}

	/**
	 * Returns the identifier that uniquely identifies this CloudProvider
	 * instance.
	 * 
	 * @return
	 */
	public String getName()
	{
		// TODO: Change this to the CloudProvider's unique identifier
		return getUnderlyingClassname();
	}
}
