package be.ac.ua.comp.scarletnebula.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.services.server.ServerState;

public class Server extends Instance
{
	org.dasein.cloud.services.server.Server serverImpl;
	CloudProvider provider;
	private String friendlyName;
	String keypair;
	
	public Server(org.dasein.cloud.services.server.Server server, CloudProvider inputProvider, String inputKeypair, String inputFriendlyName)
	{
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
	
	public CommandConnection getCommandConnection()
	{
		try
		{
			return new SSHCommandConnection(serverImpl.getPublicDnsAddress(), KeyManager.getKeyFilename(provider.getUnderlyingClassname(), keypair));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	/**
	 * Factory method that will returns a server object. If we've seen this server before and 
	 * there's saved data for him, this saved data will be loaded. 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	static Server load(org.dasein.cloud.services.server.Server server, CloudProvider provider)
	{
		String propertiesfilename = getSaveFilename(provider, server.getName());
		Properties props = new Properties();
		try
		{
			props.load(new FileInputStream(propertiesfilename));
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
		return new Server(server, 
				provider,
				props.getProperty("keypair") != null ? props.getProperty("keypair") : "default",
				props.getProperty("friendlyName") != null ? props.getProperty("friendlyName") : server.getName());
	}
	
	static String getSaveFilename(CloudProvider provider, String instanceName)
	{
		return getSaveFileDir(provider) + instanceName; 
	}
	
	static String getSaveFileDir(CloudProvider provider)
	{		
		return "servers/" + provider.getUnderlyingClassname().toString() + "/";
	}
	
	void store()
	{
		// Write key to file
		String dir = getSaveFileDir(provider);
		File dirFile = new File(dir);

		// Check if the key dir already exists
		if (!dirFile.exists())
		{
			// If it does not exist, create the directory
			if (!dirFile.mkdirs())
			{
				System.out.println("Cannot make server directory!");
				return;
			}
		}

		// Write properties file.
		try
		{
			Properties properties = new Properties();
			properties.setProperty("friendlyName", getFriendlyName());
			properties.setProperty("keypair", keypair);
			properties.setProperty("providerClassName", provider.getUnderlyingClassname());
			
			FileOutputStream outputstream = new FileOutputStream(getSaveFilename(provider, serverImpl.getName()));
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

	public String getUnfriendlyName()
	{
		return serverImpl.getName();
	}
	
	public String toString()
	{
		String rv = serverImpl.getProviderServerId() + " (" + serverImpl.getCurrentState() + ") @ " + serverImpl.getPublicDnsAddress();
		return rv; 
	}

	public String getPublicDnsAddress()
	{
		return serverImpl.getPublicDnsAddress();
	}
	
	public String[] getPublicIpAddresses()
	{
		String[] addresses = serverImpl.getPublicIpAddresses();
		
		if(addresses == null)
			return new String[0];
		else
			return addresses;
	}

	public void setFriendlyName(String friendlyName)
	{
		this.friendlyName = friendlyName;
	}


	public String getFriendlyName()
	{
		return friendlyName;
	}
	
	public void terminate() throws InternalException, CloudException
	{
		provider.terminateServer(getUnfriendlyName());
	}


	public ServerState getStatus()
	{
		return serverImpl.getCurrentState();
	}
	
	/**
	 * Pulls current information from the cloud this server is located in.
	 * This just replaces the the dasein server object stored in this server.
	 * I don't see any better way to do this in the Dasein API. 
	 * @throws CloudException 
	 * @throws InternalException 
	 */
	public void refresh() throws InternalException, CloudException
	{
		serverImpl = provider.getServerImpl(getUnfriendlyName());
	}


	public CloudProvider getCloud()
	{
		return provider;
	}


	public String getSize()
	{
		return serverImpl.getSize();
	}


	public String getImage()
	{
		return serverImpl.getImageId();
	}
}
