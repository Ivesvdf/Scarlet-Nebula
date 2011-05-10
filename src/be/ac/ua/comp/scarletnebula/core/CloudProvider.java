package be.ac.ua.comp.scarletnebula.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
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
import org.dasein.cloud.network.FirewallRule;
import org.dasein.cloud.network.FirewallSupport;
import org.dasein.cloud.network.NetworkServices;
import org.dasein.cloud.network.Protocol;

import be.ac.ua.comp.scarletnebula.misc.Utils;

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

	private org.dasein.cloud.CloudProvider providerImpl;
	private ComputeServices computeServices = null;
	private VirtualMachineSupport virtualMachineServices = null;
	private FirewallSupport firewallSupport = null;

	private String name;
	private String providerClassName;
	private String apiSecret;
	private String apiKey;
	private String endpoint;
	private String defaultKeypair;

	private final Collection<Server> servers = new ArrayList<Server>();
	private Collection<MachineImage> favoriteImages = new LinkedList<MachineImage>();

	Collection<ServerLinkUnlinkObserver> linkUnlinkObservers = new ArrayList<ServerLinkUnlinkObserver>();

	/**
	 * Constructor for constructing a cloudprovider from file.
	 * 
	 * Don't forget to update the other ctor.
	 * 
	 * @param name
	 *            Name of the provider. Used to search for a savefile.
	 */
	public CloudProvider(final String name)
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
		catch (final InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final ClassNotFoundException e)
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

		final NetworkServices networkServices = providerImpl
				.getNetworkServices();
		if (networkServices == null)
		{
			log.error(providerImpl.getCloudName()
					+ " does not support network services.");
		}
		else
		{
			firewallSupport = networkServices.getFirewallSupport();
			if (firewallSupport == null)
			{
				log.error(providerImpl.getCloudName()
						+ " does not support firewalls.");
				return;
			}
		}
	}

	private void notifyObserversBecauseServerLinked(final Server srv)
	{

		for (final ServerLinkUnlinkObserver obs : linkUnlinkObservers)
		{
			obs.serverLinked(this, srv);
			log.warn("Cloudprovider is updating his observers");
		}
	}

	private void notifyObserversBecauseServerUnlinked(final Server srv)
	{
		for (final ServerLinkUnlinkObserver obs : linkUnlinkObservers)
		{
			obs.serverUnlinked(this, srv);
		}
	}

	/**
	 * Loads a CloudProvider from file based on his name
	 * 
	 * @param name
	 */
	private void load(final String name)
	{
		Properties properties = null;

		try
		{
			properties = new Properties();
			properties.load(new FileInputStream(getConfigfileName(name)));
		}
		catch (final IOException e)
		{
			log.error("IOException while loading provider from file.", e);
		}

		this.name = name;
		this.providerClassName = properties.getProperty("class");
		this.apiKey = properties.getProperty("apikey");
		this.apiSecret = properties.getProperty("apisecret");
		this.endpoint = properties.getProperty("endpoint");
		this.defaultKeypair = properties.getProperty("defaultKeypair", "");
		this.favoriteImages = deserialiseFavoriteImages(properties.getProperty(
				"favoriteImages", null));

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
	 * @param defaultKeypair
	 */
	public CloudProvider(final String name, final String classname,
			final String endpoint, final String apikey, final String apisecret,
			final String defaultKeypair)
	{
		this.name = name;
		this.providerClassName = classname;
		this.apiKey = apikey;
		this.apiSecret = apisecret;
		this.endpoint = endpoint;
		this.defaultKeypair = defaultKeypair;
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
	public Server loadServer(final String unfriendlyName)
			throws InternalException, CloudException, IOException
	{
		log.warn("Getting for name " + unfriendlyName);
		final VirtualMachine server = getServerImpl(unfriendlyName);

		if (server == null)
		{
			return null;
		}

		final Server rv = Server.load(server, this);
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
		final File dir = new File(getSaveFileDir());

		final String[] files = dir.list();

		if (files == null)
		{
			return servers;
		}

		for (final String file : files)
		{
			final Server server = loadServer(file);

			// If the server cannot be made it was deleted and the file
			// referencing it
			// should also be removed.
			if (server == null)
			{
				log.warn("Server from file " + file
						+ " cannot be loaded. Discarting save file.");
				deleteServerSaveFile(file);
			}
			else
			{
				addServer(server);
			}
		}

		return servers;
	}

	private void addServer(final Server server)
	{
		notifyObserversBecauseServerLinked(server);
		servers.add(server);
	}

	/**
	 * Delete's the server whose CloudProvider specific name is "unfriendlyName"
	 * 
	 * @param unfriendlyName
	 */
	private void deleteServerSaveFile(final String unfriendlyName)
	{
		final File toBeRemoved = new File(getSaveFileDir() + unfriendlyName);
		final boolean result = toBeRemoved.delete();

		if (!result)
		{
			log.error("Could not remove savefile for server " + unfriendlyName);
		}
	}

	/**
	 * Creates a new key with name "keyname", changes the default key if it has
	 * to and stores the server to file.
	 * 
	 * @param acs
	 * @param keyname
	 * @throws InternalException
	 * @throws CloudException
	 */
	public void createKey(final String keyname, final boolean makeDefault)
			throws InternalException, CloudException
	{
		final ShellKeySupport shellKeySupport = providerImpl
				.getIdentityServices().getShellKeySupport();

		KeyManager.addKey(getName(), keyname,
				shellKeySupport.createKeypair(keyname));

		if (makeDefault)
		{
			setDefaultKeypair(keyname);
			store();
		}
	}

	/**
	 * Adds a new firewall rule to an already existing firewall on this
	 * provider.
	 * 
	 * @param firewall
	 *            The firewall to add the rule to
	 * @param beginPort
	 *            The lower edge of the port range that should be allowed
	 * @param endPort
	 *            The upper edge of the port range that should be allowed
	 * @param protocol
	 *            The protocol, UDP or TCP which should be allowed
	 * @param CIDR
	 *            The IP CIDR that should be allowed.
	 * @throws CloudException
	 * @throws InternalException
	 */
	public void addFirewallRule(final Firewall firewall, final int beginPort,
			final int endPort, final Protocol protocol, final String CIDR)
			throws CloudException, InternalException
	{
		firewallSupport.authorize(firewall.getProviderFirewallId(), CIDR,
				protocol, beginPort, endPort);
	}

	public void deleteFirewallRule(final Firewall firewall,
			final int beginPort, final int endPort, final Protocol protocol,
			final String CIDR) throws CloudException, InternalException
	{
		firewallSupport.revoke(firewall.getProviderFirewallId(), CIDR,
				protocol, beginPort, endPort);
	}

	/**
	 * @return All firewalls for this CloudProvider
	 * @throws InternalException
	 * @throws CloudException
	 */
	public Collection<Firewall> getFirewalls() throws InternalException,
			CloudException
	{
		final FirewallSupport fws = providerImpl.getNetworkServices()
				.getFirewallSupport();

		if (fws == null)
		{
			return new ArrayList<Firewall>();
		}

		return fws.list();
	}

	public Collection<FirewallRule> getFirewallRules(final String firewall)
			throws InternalException, CloudException
	{
		return firewallSupport.getRules(firewall);
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
		final ArrayList<Server> rv = new ArrayList<Server>();
		// List all servers
		for (final VirtualMachine testServer : virtualMachineServices
				.listVirtualMachines())
		{
			// For each server, check if this server is already linked. Do
			// this based on his unfriendly id
			boolean found = false;
			for (final Iterator<Server> linkedServerIterator = servers
					.iterator(); linkedServerIterator.hasNext() && !found;)
			{
				if (linkedServerIterator.next().getUnfriendlyName()
						.equals(testServer.getName()))
				{
					found = true;
				}
			}

			if (!found)
			{
				final List<String> daseinTags = new ArrayList<String>();
				for (final String key : testServer.getTags().keySet())
				{
					daseinTags.add(key + ":" + testServer.getTags().get(key));
				}

				rv.add(new Server(testServer, // dasein server
						this, // cloud provider
						"", // keypair
						testServer.getName() + " (" + getName() + ")",// friendly
																		// name
						daseinTags, // tags
						true, // use password
						nullToEmpty(testServer.getRootUser()), // root user
						nullToEmpty(testServer.getRootPassword()), // root
																	// password
						nullToEmpty(testServer.getRootPassword()), // VNC passwd
						getDefaultStatisticsCommand(), // statistics command
						"CPU"));
			}
		}

		return rv;
	}

	private String nullToEmpty(final String input)
	{
		return (input == null) ? "" : input;
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
	public void terminateServer(final String unfriendlyName)
			throws InternalException, CloudException
	{
		virtualMachineServices.terminate(unfriendlyName);
	}

	/**
	 * Starts a new server.
	 * 
	 * @param serverName
	 * @param firewalls
	 *            TODO
	 * @param size
	 * @return
	 * @throws InternalException
	 * @throws CloudException
	 */
	public Server startServer(final String serverName,
			final VirtualMachineProduct product, final MachineImage image,
			final Collection<String> tags, final String keypairOrPassword,
			final Collection<String> firewalls) throws InternalException,
			CloudException
	{
		final String dataCenterId = "eu-west-1b";
		final String vlan = null;

		final Collection<org.dasein.cloud.Tag> daseinTags = new ArrayList<org.dasein.cloud.Tag>();
		int i = 0;

		for (final String tag : tags)
		{
			daseinTags.add(new org.dasein.cloud.Tag("tag" + (++i), tag));
		}

		final VirtualMachine daseinServer = virtualMachineServices.launch(
				image.getProviderMachineImageId(), // image id
				product, // vm product (size)
				dataCenterId, // data center id
				serverName, // friendly server name
				"", // server description
				keypairOrPassword, // keypair
				vlan, // vlan
				false, false,
				(firewalls != null ? firewalls.toArray(new String[0]) : null), // firewalls
				daseinTags.toArray(new org.dasein.cloud.Tag[0])); // tags

		String rootUser = daseinServer.getRootUser();
		if (rootUser == null || rootUser.isEmpty())
		{
			if (image.getName().toLowerCase().contains("ubuntu")
					|| image.getDescription().toLowerCase().contains("ubuntu"))
			{
				rootUser = "ubuntu";
			}
			else if (image.getPlatform() != Platform.WINDOWS)
			{
				rootUser = "root";
			}
			else
			{
				rootUser = "";
			}
		}

		final Server server = new Server(daseinServer, // Dasein server
														// implementation
				this, // Cloud provider
				keypairOrPassword, // Keypair used
				serverName, // Server's friendly name
				tags, // Tags this server was given
				!supportsSSHKeys(), // server uses password to SSH
				rootUser, // SSH login
				daseinServer.getRootPassword(), // SSH Password
				keypairOrPassword, // VNC password
				getDefaultStatisticsCommand(), // Statistics
												// command
				"CPU"); // preferred datastream

		linkUnlinkedServer(server);
		return server;
	}

	/**
	 * @return The name of the default keypair for this provider. If no default
	 *         keypair is set (or the default keypair is invalid) but there are
	 *         keys available, one of these will be chosen
	 */
	public String getDefaultKeypair()
	{
		// If no default keypair is entered in the cloudprovider, but there is a
		// key for this provider, make that the default.

		// Also do something similar when the default key is not in the set of
		// keys.
		final Collection<String> keys = KeyManager.getKeyNames(getName());

		if (defaultKeypair.isEmpty() || !keys.contains(defaultKeypair))
		{
			if (!keys.isEmpty())
			{
				final String newDefaultKey = keys.iterator().next();
				setDefaultKeypair(newDefaultKey);
				store();
			}
		}

		return defaultKeypair;
	}

	/**
	 * Sets the default keypair (without saving) on the condition that a keypair
	 * by this name exists.
	 * 
	 * @param newDefaultKeypair
	 *            The name of the new keypair that will become default.
	 */
	public void setDefaultKeypair(final String newDefaultKeypair)
	{
		if (KeyManager.getKeyNames(getName()).contains(newDefaultKeypair))
		{
			defaultKeypair = newDefaultKeypair;
		}
	}

	private String getDefaultStatisticsCommand()
	{
		final StringBuffer sb = new StringBuffer();

		try
		{
			BufferedReader br;

			br = new BufferedReader(new FileReader(
					Utils.internalFile("statistics.sh")));

			String nextLine = "";
			while ((nextLine = br.readLine()) != null)
			{
				sb.append(nextLine);
				sb.append("\n");
			}
		}
		catch (final Exception e)
		{
			log.error(
					"Could not read default statistics command, continuing with empty string",
					e);
		}
		return sb.toString();
	}

	public VirtualMachineProduct getVMProductWithName(final String name)
	{
		try
		{
			final Iterable<VirtualMachineProduct> products = virtualMachineServices
					.listProducts(Architecture.I32);

			for (final VirtualMachineProduct product : products)
			{
				if (name == product.getName())
				{
					return product;
				}
			}
			return null;
		}
		catch (final InternalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final CloudException e)
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
		final org.dasein.cloud.ProviderContext context = new org.dasein.cloud.ProviderContext();

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
	public Iterable<VirtualMachineProduct> getPossibleInstanceSizes(
			final Architecture architecture)
	{
		Iterable<VirtualMachineProduct> products = null;
		try
		{
			products = virtualMachineServices.listProducts(architecture);
		}
		catch (final InternalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final CloudException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return products;
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

	public VirtualMachine getServerImpl(final String unfriendlyName)
			throws InternalException, CloudException
	{
		return virtualMachineServices.getVirtualMachine(unfriendlyName);
	}

	public Iterable<MachineImage> getAvailableMachineImages(
			final Platform platform, final Architecture architecture)
	{
		Iterable<MachineImage> images = null;
		try
		{
			images = computeServices.getImageSupport().searchMachineImages("",
					platform, architecture);
		}
		catch (final CloudException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final InternalException e)
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
	public void linkUnlinkedServer(final Server server)
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
	void pause(final Server server) throws InternalException, CloudException
	{
		virtualMachineServices.pause(server.getUnfriendlyName());
	}

	/**
	 * Resumes a previously paused server.
	 * 
	 * @param server
	 *            The server to resume.
	 * @throws InternalException
	 * @throws CloudException
	 */
	void resume(final Server server) throws InternalException, CloudException
	{
		log.info("Resuming " + server);
		virtualMachineServices.boot(server.getUnfriendlyName());
	}

	/**
	 * Reboots the server in parameter. This method is only supposed to be
	 * called by Server.reboot()
	 * 
	 * @param server
	 * @throws CloudException
	 * @throws InternalException
	 */
	void reboot(final Server server) throws CloudException, InternalException
	{
		virtualMachineServices.reboot(server.getUnfriendlyName());
	}

	/**
	 * Unlinks the instance in parameter. After this call, this instance will no
	 * longer be linked to this CloudProvider
	 * 
	 * @param selectedServer
	 */
	public void unlink(final Server selectedServer)
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
	public boolean hasServer(final String friendlyName)
	{
		for (final Server s : servers)
		{
			if (s.getFriendlyName().equals(friendlyName))
			{
				return true;
			}
		}

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
	public void store()
	{
		// First assure the providers/ directory exists
		final File dir = new File("providers");

		if (!dir.exists() || !dir.isDirectory())
		{
			if (!dir.mkdir())
			{
				log.error("Could not create providers/ directory.");
			}
		}

		// Now write to the file properties file
		final Properties prop = new Properties();

		prop.setProperty("class", providerClassName);
		prop.setProperty("apikey", apiKey);
		prop.setProperty("apisecret", apiSecret);
		prop.setProperty("endpoint", endpoint);
		prop.setProperty("defaultKeypair", defaultKeypair);
		prop.setProperty("favoriteImages", getSerialisedFavoriteImages());

		try
		{
			prop.store(new FileOutputStream(getConfigfileName(name)), null);
		}
		catch (final FileNotFoundException e)
		{
			log.error("Properties file describing cloud provider could not be created.");
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getSerialisedFavoriteImages()
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream oout = null;
		String rv = "";
		try
		{
			oout = new ObjectOutputStream(outputStream);
			log.info("size of serialised images collection = "
					+ favoriteImages.size());
			oout.writeObject(favoriteImages);
			byte bytes[] = outputStream.toByteArray();

			byte encodedBytes[] = Base64.encodeBase64(bytes, false);
			rv = new String(encodedBytes);
		}
		catch (IOException e)
		{
			log.error("Exception while serialising favorite images.", e);
		}
		finally
		{
			try
			{
				oout.close();
				outputStream.close();
			}
			catch (IOException ignore)
			{
			}
		}

		return rv;
	}

	/**
	 * Converts a string containing base64 encoded favorite image data back to a
	 * collection of machine images. The suppress warnings needs to be here
	 * because of type erasure -- the runtime has no clue what's inside the
	 * collection...
	 * 
	 * @param input
	 *            String to be deserialised
	 * @return The collection of machine images contained in the input string.
	 */
	@SuppressWarnings("unchecked")
	private Collection<MachineImage> deserialiseFavoriteImages(String input)
	{
		if (input == null || input.isEmpty())
		{
			return new ArrayList<MachineImage>();
		}

		byte decodedBytes[] = Base64.decodeBase64(input.getBytes());

		if (decodedBytes != null)
		{
			try
			{
				ObjectInputStream objectIn = new ObjectInputStream(
						new ByteArrayInputStream(decodedBytes));

				final Object readObject = objectIn.readObject();

				return (Collection<MachineImage>) readObject;

			}
			catch (Exception e)
			{
				log.error("Exception while deserialising favorite images.", e);
			}
		}
		return new ArrayList<MachineImage>();
	}

	static String getConfigfileName(final String providername)
	{
		return "providers/" + providername + ".properties";
	}

	static Collection<String> getProviderNames()
	{
		final File dir = new File("providers");

		if (!dir.exists() || !dir.isDirectory())
		{
			return new ArrayList<String>();
		}

		final Collection<String> files = Arrays.asList(dir.list());
		final Collection<String> rv = new ArrayList<String>(files.size());

		for (final String file : files)
		{
			rv.add(file.replaceFirst(".properties$", ""));
		}
		return rv;
	}

	public boolean isLinked(final Server server)
	{
		// Not using servers.contains because this seems to use Server.equals
		// which isn't implemented...
		for (final Server linkedServer : servers)
		{
			if (server == linkedServer)
			{
				return true;
			}
		}

		return false;
	}

	public void addServerLinkUnlinkObserver(final ServerLinkUnlinkObserver obs)
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
		final ShellKeySupport shellKeySupport = providerImpl
				.getIdentityServices().getShellKeySupport();

		final Collection<String> keys = shellKeySupport.list();
		final Collection<String> knownKeys = KeyManager.getKeyNames(getName());
		keys.removeAll(knownKeys);
		return keys;
	}

	/**
	 * @return True if this provider supports SSH keys, false otherwise.
	 */
	public boolean supportsSSHKeys()
	{
		return providerImpl.getIdentityServices() != null
				&& providerImpl.getIdentityServices().getShellKeySupport() != null;
	}

	public boolean unlinkedKeyExists(final String checkKeyname)
	{
		final ShellKeySupport shellKeySupport = providerImpl
				.getIdentityServices().getShellKeySupport();

		boolean exists = false;
		try
		{
			exists = shellKeySupport.list().contains(checkKeyname);
		}
		catch (final Exception e)
		{
			log.error("Could not list keys.", e);
		}
		return exists;
	}

	/**
	 * Imports a key to the Scarlet Nebula system, and optionally makes it
	 * default and saves the server.
	 * 
	 * @param keyname
	 *            The key's name
	 * @param keyFile
	 *            The file to copy from
	 * @param makeDefault
	 *            True if it should become default, false otherwise.
	 */
	public void importKey(final String keyname, final File keyFile,
			final boolean makeDefault)
	{
		KeyManager.addKey(getName(), keyname, keyFile);

		if (makeDefault)
		{
			setDefaultKeypair(keyname);
			store();
		}
	}

	/**
	 * Deletes and SSH key, both locally and, if it exists, remotely.
	 * 
	 * @param key
	 * @throws CloudException
	 * @throws InternalException
	 */
	public void deleteKey(final String key) throws InternalException,
			CloudException
	{
		KeyManager.deleteKey(getName(), key);

		final ShellKeySupport shellKeySupport = providerImpl
				.getIdentityServices().getShellKeySupport();
		shellKeySupport.deleteKeypair(key);
	}

	/**
	 * Creates a firewall in the cloudprovider and returns a pseudo-firewall
	 * object. This object only contains a user friendly name and a provider id.
	 * 
	 * @param firewallName
	 *            User friendly name of the firewall
	 * @return A pseudo-firewall object. This object only contains a user
	 *         friendly name and a provider id.
	 * @throws InternalException
	 * @throws CloudException
	 */
	public Firewall createFirewall(final String firewallName)
			throws InternalException, CloudException
	{
		final String id = firewallSupport.create(firewallName, firewallName);
		final Firewall firewall = new Firewall();
		firewall.setName(firewallName);
		firewall.setProviderFirewallId(id);
		return firewall;
	}

	public void deleteFirewall(final Firewall firewall)
			throws InternalException, CloudException
	{
		firewallSupport.delete(firewall.getProviderFirewallId());
	}

	public boolean supportsFirewalls()
	{
		return firewallSupport != null;
	}

	public void addToFavorites(MachineImage image)
	{
		if (!favoriteImages.contains(image))
			favoriteImages.add(image);
	}

	public Collection<MachineImage> getFavoriteImages()
	{
		return favoriteImages;
	}

	public void removeFromFavorites(MachineImage image)
	{
		favoriteImages.remove(image);

	}
}
