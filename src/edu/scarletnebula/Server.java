package edu.scarletnebula;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Server extends Instance
{
	org.dasein.cloud.services.server.Server serverImpl;
	String friendlyName;
	String keypair;
	String providerClassName;
	
	public Server(org.dasein.cloud.services.server.Server server, String inputProviderClassName, String inputKeypair, String inputFriendlyName)
	{
		providerClassName = inputProviderClassName;
		keypair = inputKeypair;
		serverImpl = server;
		friendlyName = inputFriendlyName; 
	}
	

	public void sendFile(String filename)
	{
		
	}
	
	
	public ServerStatisticsManager getServerStatistics()
	{
		return null;
	}
	
	CommandConnection getCommandConnection()
	{
		try
		{
			return new SSHCommandConnection(serverImpl.getPublicDnsAddress(), KeyManager.getKeyFilename(providerClassName, keypair));
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
	static Server load(org.dasein.cloud.services.server.Server server, String providerClassName)
	{
		String propertiesfilename = getSaveFilename(providerClassName, server.getName());
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
				providerClassName,
				props.getProperty("keypair") != null ? props.getProperty("keypair") : "default",
				props.getProperty("friendlyName") != null ? props.getProperty("friendlyName") : server.getName());
	}
	
	static String getSaveFilename(String providerClassName, String instanceName)
	{
		return getSaveFileDir(providerClassName) + instanceName; 
	}
	
	static String getSaveFileDir(String providerClassName)
	{		
		return "servers/" + providerClassName + "/";
	}
	
	void store(String providerClassName)
	{
		// Write key to file
		String dir = getSaveFileDir(providerClassName);
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
			properties.setProperty("friendlyName", friendlyName);
			properties.setProperty("keypair", keypair);
			properties.setProperty("providerClassName", providerClassName);
			
			FileOutputStream outputstream = new FileOutputStream(getSaveFilename(providerClassName, serverImpl.getName()));
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

	public String toString()
	{
		String rv = serverImpl.getProviderServerId() + " (" + serverImpl.getCurrentState() + ") @ " + serverImpl.getPublicDnsAddress();
		return rv; 
	}
}
