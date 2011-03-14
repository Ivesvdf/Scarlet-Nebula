package be.ac.ua.comp.scarletnebula.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VmState;

import com.jcraft.jsch.UserInfo;

public class Server
{
	private static Log log = LogFactory.getLog(Server.class);

	VirtualMachine serverImpl;
	Collection<ServerChangedObserver> serverChangedObservers;
	CloudProvider provider;
	private String friendlyName;
	String keypair;

	public Server(VirtualMachine server, CloudProvider inputProvider,
			String inputKeypair, String inputFriendlyName)
	{
		serverChangedObservers = new ArrayList<ServerChangedObserver>();
		provider = inputProvider;
		keypair = inputKeypair;
		serverImpl = server;
		setFriendlyName(inputFriendlyName);
	}

	public void sendFile(String filename)
	{
	}

	public ServerStatisticsManager getServerStatistics()
	{
		return null;
	}

	/**
	 * @return A new CommandConnection to this server
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public CommandConnection newCommandConnection(UserInfo ui)
			throws FileNotFoundException
	{
		try
		{
			return new SSHCommandConnection(serverImpl.getPublicDnsAddress(),
					KeyManager.getKeyFilename(provider.getName(), keypair), ui);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Factory method that will returns a server object. If we've seen this
	 * server before and there's saved data for him, this saved data will be
	 * loaded.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	static Server load(VirtualMachine server, CloudProvider provider)
	{
		String propertiesfilename = getSaveFilename(provider, server.getName());
		Properties props = new Properties();
		try
		{
			props.load(new FileInputStream(propertiesfilename));
		}
		catch (FileNotFoundException e)
		{
			// Just ignore if the file isn't found.
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Server(server, provider,
				props.getProperty("keypair") != null ? props
						.getProperty("keypair") : "default",
				props.getProperty("friendlyName") != null ? props
						.getProperty("friendlyName") : server.getName() + " ("
						+ provider.getName() + ")");
	}

	/**
	 * Returns the filename (with directory) a new instance with name
	 * "instanceName" for CloudProvider "provider" should get.
	 * 
	 * @param provider
	 * @param instanceName
	 * @return
	 */
	static String getSaveFilename(CloudProvider provider, String instanceName)
	{
		return provider.getSaveFileDir() + instanceName;
	}

	/**
	 * Saves this server to its savefile.
	 */
	void store()
	{
		// Write key to file
		String dir = provider.getSaveFileDir();
		File dirFile = new File(dir);

		// Check if the key dir already exists
		if (!dirFile.exists())
		{
			// If it does not exist, create the directory
			if (!dirFile.mkdirs())
			{
				log.fatal("Cannot make server directory!");
				return;
			}
		}

		// Write properties file.
		try
		{
			Properties properties = new Properties();
			properties.setProperty("friendlyName", getFriendlyName());
			properties.setProperty("keypair", keypair);
			properties.setProperty("providerClassName",
					provider.getUnderlyingClassname());

			FileOutputStream outputstream = new FileOutputStream(
					getSaveFilename(provider, serverImpl.getName()));
			properties.store(outputstream, null);
			outputstream.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Returns the server implementation's cloud specific (unfriendly) name.
	 * 
	 * @return
	 */
	public String getUnfriendlyName()
	{
		return serverImpl.getProviderVirtualMachineId();
	}

	@Override
	public String toString()
	{
		String rv = serverImpl.getProviderVirtualMachineId() + " ("
				+ serverImpl.getCurrentState() + ") @ "
				+ serverImpl.getPublicDnsAddress();
		return rv;
	}

	/**
	 * @return A public DNS address for this server, null if none is available.
	 */
	public String getPublicDnsAddress()
	{
		return serverImpl.getPublicDnsAddress();
	}

	/**
	 * @return A list of public IP address for this server, empty array if none
	 *         is available.
	 */
	public String[] getPublicIpAddresses()
	{
		String[] addresses = serverImpl.getPublicIpAddresses();

		if (addresses == null)
			return new String[0];
		else
			return addresses;
	}

	/**
	 * Sets the friendly name for this server
	 * 
	 * @param friendlyName
	 */
	final public void setFriendlyName(String friendlyName)
	{
		this.friendlyName = friendlyName;
	}

	/**
	 * @return This server's friendly name
	 */
	final public String getFriendlyName()
	{
		return friendlyName;
	}

	/**
	 * Terminates this server
	 * 
	 * @throws InternalException
	 * @throws CloudException
	 */
	public void terminate() throws InternalException, CloudException
	{
		provider.terminateServer(getUnfriendlyName());
	}

	/**
	 * @return This server's status
	 */
	public VmState getStatus()
	{
		return serverImpl.getCurrentState();
	}

	/**
	 * Pulls current information from the cloud this server is located in. This
	 * just replaces the the dasein server object stored in this server. I don't
	 * see any better way to do this in the Dasein API.
	 * 
	 * @throws CloudException
	 * @throws InternalException
	 * @throws ServerDisappearedException
	 */
	public void refresh() throws InternalException, CloudException,
			ServerDisappearedException
	{
		VirtualMachine refreshedServer = provider
				.getServerImpl(getUnfriendlyName());

		// If the sever disappeared in the mean while, throw an Exception
		if (refreshedServer == null)
			throw new ServerDisappearedException(this);

		serverImpl = refreshedServer;

		// if(!serverImpl.equals(refreshedServer))
		serverChanged();
	}

	/**
	 * @return The CloudProvider this server was started on
	 */
	public CloudProvider getCloud()
	{
		return provider;
	}

	/**
	 * @return This server's size string
	 */
	public String getSize()
	{
		return serverImpl.getProduct().getName();
	}

	/**
	 * @return This server's image id
	 */
	public String getImage()
	{
		return serverImpl.getProviderMachineImageId();
	}

	/**
	 * Pauses this server
	 * 
	 * @throws InternalException
	 * @throws CloudException
	 */
	public void pause() throws InternalException, CloudException
	{
		provider.pause(this);
	}

	/**
	 * Reboots this server
	 * 
	 * @throws CloudException
	 * @throws InternalException
	 */
	public void reboot() throws CloudException, InternalException
	{
		provider.reboot(this);
	}

	/**
	 * Add a ServerChangedObserver that will be notified when the server
	 * changes.
	 * 
	 * @param sco
	 *            The observer that will be notified when the server changes.
	 */
	public void addServerChangedObserver(ServerChangedObserver sco)
	{
		serverChangedObservers.add(sco);
	}

	/**
	 * Removes an observer from the list of observers
	 * 
	 * @param sco
	 *            The observer that will be deleted.
	 */
	public void removeServerChangedObserver(ServerChangedObserver sco)
	{
		serverChangedObservers.remove(sco);
	}

	/**
	 * Notify all observers the server has changed.
	 */
	private void serverChanged()
	{
		for (ServerChangedObserver obs : serverChangedObservers)
			obs.serverChanged(this);
	}

	/**
	 * Unlinks this server. This will remove the save file for this server and
	 * will remove it from the list of linked servers the cloudprovider
	 * maintains. This obviously does not affect the server's running state in
	 * any way.
	 */
	public void unlink()
	{
		getCloud().unlink(this);
	}

	/**
	 * Checks with the CloudManager if a server by this name is linked in *any*
	 * CloudProvider
	 * 
	 * @param name
	 * @return
	 */
	public static boolean exists(String name)
	{
		return CloudManager.get().serverExists(name);
	}
}
