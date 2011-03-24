package be.ac.ua.comp.scarletnebula.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.Platform;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.compute.VirtualMachineSupport;
import org.dasein.cloud.identity.ShellKeySupport;
import org.dasein.cloud.network.Firewall;
import org.dasein.cloud.network.FirewallSupport;
import org.dasein.cloud.network.Protocol;

/**
 * Class representing a cloud provider in Scarlet Nebula. This will contain a
 * Dasein cloudprovider object but also implement other stuff like making a
 * cloudprovider specific configuration.
 * 
 * @author ives
 */
public class CloudProvider
{
	private static Log log = LogFactory.getLog(CloudProvider.class);

	public enum CloudProviderName
	{
		AWS
	};

	private org.dasein.cloud.CloudProvider providerImpl;
	private ComputeServices computeServices;
	private VirtualMachineSupport virtualMachineServices;

	private String name;
	private String providerClassName;
	private String apiSecret;
	private String apiKey;
	private String endpoint;

	private ArrayList<Server> servers = new ArrayList<Server>();
	Collection<ServerLinkUnlinkObserver> linkUnlinkObservers = new ArrayList<ServerLinkUnlinkObserver>();

	/**
	 * Constructor for constructing a cloudprovider from file.
	 * 
	 * Don't forget to update the other ctor.
	 * 
	 * @param name
	 *            Name of the provider. Used to search for a savefile.
	 */
	public CloudProvider(String name)
	{
		load(name);

		connect();

		// TODO: place these somewhere? maybe a menu option
		// assureSSHOnlyFirewall();
		// assureSSHKey();

	}

	private void connect()
	{
		try
		{
			providerImpl = (org.dasein.cloud.CloudProvider) Class.forName(
					providerClassName).newInstance();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			log.fatal("Underlying cloud provider class " + providerClassName
					+ " failed creation.");
		}

		providerImpl.connect(getCurrentContext());

		computeServices = providerImpl.getComputeServices();
		if (computeServices == null)
		{
			log.error(providerImpl.getCloudName()
					+ " does not support compute instances.");
			return;
		}
		virtualMachineServices = computeServices.getVirtualMachineSupport();
		if (computeServices == null)
		{
			log.error(providerImpl.getCloudName()
					+ " does not support Virtual Machines.");
			return;
		}
	}

	private void notifyObserversBecauseServerLinked(Server srv)
	{

		for (ServerLinkUnlinkObserver obs : linkUnlinkObservers)
		{
			obs.serverLinked(this, srv);
			log.warn("Cloudprovider is updating his observers");
		}
	}

	private void notifyObserversBecauseServerUnlinked(Server srv)
	{
		for (ServerLinkUnlinkObserver obs : linkUnlinkObservers)
			obs.serverUnlinked(this, srv);
	}

	/**
	 * Loads a CloudProvider from file based on his name
	 * 
	 * @param name
	 */
	private void load(String name)
	{
		Properties properties = null;

		try
		{
			properties = new Properties();
			properties.load(new FileInputStream(getConfigfileName(name)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		this.name = name;
		this.providerClassName = properties.getProperty("class");
		this.apiKey = properties.getProperty("apikey");
		this.apiSecret = properties.getProperty("apisecret");
		this.endpoint = properties.getProperty("endpoint");
	}

	/**
	 * Basic constructor for use when *not* loading from file. A provider made
	 * in this fashion will probably not be linked with the cloudManager.
	 * 
	 * Don't forget to update the other constructor!
	 * 
	 * @param name
	 * @param classname
	 * @param endpoint
	 * @param apikey
	 * @param apisecret
	 */
	public CloudProvider(String name, String classname, String endpoint,
			String apikey, String apisecret)
	{
		this.name = name;
		this.providerClassName = classname;
		this.apiKey = apikey;
		this.apiSecret = apisecret;
		this.endpoint = endpoint;
		connect();
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
		VirtualMachine server = getServerImpl(unfriendlyName);

		if (server == null)
			return null;

		Server rv = Server.load(server, this);
		return rv;

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
		File dir = new File(getSaveFileDir());

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
				addServer(server);
			}
		}

		return servers;
	}

	private void addServer(Server server)
	{
		notifyObserversBecauseServerLinked(server);
		servers.add(server);
	}

	/**
	 * Delete's the server whose CloudProvider specific name is "unfriendlyName"
	 * 
	 * @param unfriendlyName
	 */
	private void deleteServerSaveFile(String unfriendlyName)
	{
		File toBeRemoved = new File(getSaveFileDir() + unfriendlyName);
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
	private void createKey(ShellKeySupport shellKeySupport, String keyname)
			throws InternalException, CloudException
	{
		KeyManager.addKey(providerClassName, keyname,
				shellKeySupport.createKeypair(keyname));

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
		ShellKeySupport shellKeySupport = providerImpl.getIdentityServices()
				.getShellKeySupport();

		if (!shellKeySupport.list().contains("sndefault"))
		{
			createKey(shellKeySupport, "sndefault");
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
		FirewallSupport fws = providerImpl.getNetworkServices()
				.getFirewallSupport();

		if (fws == null)
			return;

		Collection<Firewall> firewalls = fws.list();

		Firewall sshOnly = null;

		for (Firewall fw : firewalls)
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
			fws.authorize(sshonlyId, "0.0.0.0/0", Protocol.TCP, 22, 22);
		}
	}

	/**
	 * Lists all servers the underlying Cloud Provider manages, but that are
	 * *not* by listLinkedServers() i.e. the ones that aren't managed by Scarlet
	 * Nebula at this moment.
	 * 
	 * @return
	 * @throws CloudException
	 * @throws InternalException
	 * @throws Exception
	 */
	public ArrayList<Server> listUnlinkedServers() throws InternalException,
			CloudException
	{
		ArrayList<Server> rv = new ArrayList<Server>();
		// List all servers
		for (VirtualMachine testServer : virtualMachineServices
				.listVirtualMachines())
		{
			// For each server, check if this server is already linked. Do
			// this based on his unfriendly id
			boolean found = false;
			for (Iterator<Server> linkedServerIterator = servers.iterator(); linkedServerIterator
					.hasNext() && !found;)
			{
				if (linkedServerIterator.next().getUnfriendlyName()
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
		virtualMachineServices.terminate(unfriendlyName);
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
	public Server startServer(String serverName, String productName)
			throws InternalException, CloudException
	{
		String imageId = "ami-15765c61";
		String dataCenterId = "eu-west-1b";
		String keypairOrPassword = "sndefault";
		String vlan = "";
		String[] firewalls = new String[] { "sshonly" };

		VirtualMachine daseinServer = virtualMachineServices.launch(imageId,
				getVMProductWithName(productName), dataCenterId, serverName,
				"", keypairOrPassword, vlan, false, false, firewalls);

		Server server = new Server(daseinServer, this, keypairOrPassword,
				serverName);

		linkUnlinkedServer(server);
		return server;
	}

	public VirtualMachineProduct getVMProductWithName(String name)
	{
		try
		{
			Iterable<VirtualMachineProduct> products = virtualMachineServices
					.listProducts(Architecture.I32);

			for (VirtualMachineProduct product : products)
			{
				if (name == product.getName())
					return product;
			}
			return null;
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
		return null;
	}

	/**
	 * Returns the ServerServices for this CloudProvider
	 * 
	 * @return
	 */
	VirtualMachineSupport getVirtualMachineServices()
	{
		return virtualMachineServices;
	}

	/**
	 * Returns the endpoint this CloudProvider uses.
	 * 
	 * @return
	 */
	String getEndpoint()
	{
		return endpoint;
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

		org.dasein.cloud.ProviderContext context = new org.dasein.cloud.ProviderContext();

		context.setAccountNumber("000000000000");
		context.setAccessPublic(apiKey.getBytes());
		context.setAccessPrivate(apiSecret.getBytes());
		context.setEndpoint(getEndpoint());

		return context;
	}

	/**
	 * Returns a collection of instance sizes that are possible.
	 * 
	 * @return
	 */
	public Collection<String> getPossibleInstanceSizes()
	{
		Iterable<VirtualMachineProduct> products = null;
		try
		{
			products = virtualMachineServices.listProducts(Architecture.I32);
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

		for (VirtualMachineProduct product : products)
		{
			rv.add(product.getName());
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

	public VirtualMachine getServerImpl(String unfriendlyName)
			throws InternalException, CloudException
	{
		return virtualMachineServices.getVirtualMachine(unfriendlyName);
	}

	public Iterable<MachineImage> getAvailableMachineImages(Platform platform,
			Architecture architecture)
	{
		Iterable<MachineImage> images = null;
		try
		{
			images = computeServices.getImageSupport().searchMachineImages("",
					platform, architecture);
		}
		catch (CloudException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InternalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return images;
	}

	/**
	 * Makes the unlinked Server "server" linked.
	 * 
	 * @param server
	 */
	public void linkUnlinkedServer(Server server)
	{
		server.store();
		addServer(server);
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
		virtualMachineServices.pause(server.getUnfriendlyName());
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
		virtualMachineServices.reboot(server.getUnfriendlyName());
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
		notifyObserversBecauseServerUnlinked(selectedServer);
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
		return name;
	}

	/**
	 * @return The directory (ending in "/") all servers with this CloudProvider
	 *         should be saved in.
	 */
	String getSaveFileDir()
	{
		return "servers/" + getName() + "/";
	}

	/**
	 * Saves the file describing this CloudProvider. If a cloudprovider by the
	 * name "providername" already exists, the savefile will be overwritten.
	 * 
	 * @param providername
	 *            The provider's name
	 * @param providerclass
	 *            The provider's class (e.g. org.dasein.cloud.aws.AWSCloud)
	 * @param endpoint
	 *            The endpoint the connection is based on
	 * @param apikey
	 *            Access id
	 * @param apisecret
	 *            Access key
	 */
	static void store(String providername, String providerclass,
			String endpoint, String apikey, String apisecret)
	{
		// First assure the providers/ directory exists
		File dir = new File("providers");

		if (!dir.exists() || !dir.isDirectory())
		{
			if (!dir.mkdir())
			{
				log.error("Could not create providers/ directory.");
			}
		}

		// Now write to the file properties file
		Properties prop = new Properties();

		prop.setProperty("class", providerclass);
		prop.setProperty("apikey", apikey);
		prop.setProperty("apisecret", apisecret);
		prop.setProperty("endpoint", endpoint);

		try
		{
			prop.store(new FileOutputStream(getConfigfileName(providername)),
					null);
		}
		catch (FileNotFoundException e)
		{
			log.error("Properties file describing cloud provider could not be created.");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String getConfigfileName(String providername)
	{
		return "providers/" + providername + ".properties";
	}

	static Collection<String> getProviderNames()
	{
		File dir = new File("providers");

		if (!dir.exists() || !dir.isDirectory())
			return new ArrayList<String>();

		Collection<String> files = Arrays.asList(dir.list());
		Collection<String> rv = new ArrayList<String>(files.size());

		for (String file : files)
		{
			rv.add(file.replaceFirst(".properties$", ""));
		}
		return rv;
	}

	public boolean isLinked(Server server)
	{
		// Not using servers.contains because this seems to use Server.equals
		// which isn't implemented...
		for (Server linkedServer : servers)
		{
			if (server == linkedServer)
				return true;
		}

		return false;
	}

	public void addServerLinkUnlinkObserver(ServerLinkUnlinkObserver obs)
	{
		linkUnlinkObservers.add(obs);
	}

	/**
	 * Tests the current CloudProvider for connectivity.
	 * 
	 * @return True if a connection can be made, false otherwise.
	 */
	public boolean test()
	{
		return providerImpl.testContext() != null;
	}

	public Collection<String> getUnknownKeys() throws InternalException,
			CloudException
	{
		ShellKeySupport shellKeySupport = providerImpl.getIdentityServices()
				.getShellKeySupport();

		Collection<String> keys = shellKeySupport.list();
		Collection<String> knownKeys = KeyManager.getKeyNames(getName());
		keys.removeAll(knownKeys);
		return keys;

	}
}
