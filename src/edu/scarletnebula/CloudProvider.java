package edu.scarletnebula;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.swing.ComboBoxModel;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.services.server.ServerServices;
import org.dasein.cloud.services.server.ServerSize;

/**
 * Class representing a cloud provider in Scarlet Nebula. This will contain a 
 * Dasein cloudprovider object but also implement other stuff like making 
 * a cloudprovider specific configuration.
 * @author ives
 */
public class CloudProvider
{
	public enum CloudProviderName { AWS };
	
	private org.dasein.cloud.CloudProvider providerImpl;
	private Properties providerSpecificProperties;
	private ServerServices serverServices;
	private String providerClassName;

	private ArrayList<Server> servers;
	
	//TODO: Make this a class hierarchy
	CloudProvider(CloudProviderName name) throws Exception
	{
		servers = new ArrayList<Server>();
		
		switch(name)
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
			providerSpecificProperties.load(new FileInputStream(providerClassName + ".properties"));	
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		
		providerImpl = 
			(org.dasein.cloud.CloudProvider) Class.forName(providerClassName).newInstance();
	
		providerImpl.connect(getCurrentContext());
		
		serverServices = providerImpl.getServerServices();
		if (serverServices == null)
		{
			System.out.println(providerImpl.getCloudName()
					+ " does not support compute instances.");
			return;
		}
		assureSSHOnlyFirewall();
		assureSSHKey();
	}
	
	Server getServer(String instancename) throws InternalException, CloudException, IOException
	{
		org.dasein.cloud.services.server.Server server = serverServices.getServer(instancename);
		
		if(server == null)
			return null;
		
		return Server.load(serverServices.getServer(instancename), providerClassName);
		
	}
	
	Collection<Server> loadLinkedServers() throws InternalException, CloudException, IOException
	{
		File dir = new File(Server.getSaveFileDir(providerClassName));

		String[] files = dir.list();
		
		for(String file : files)
		{
			Server server = getServer(file);
			
			if(server != null)
				servers.add(server);
		}
		
		return servers;
	}
	
	private void createKey(org.dasein.cloud.services.access.AccessServices acs, String keyname) throws InternalException, CloudException
	{
		KeyManager.addKey(providerClassName, keyname, acs.createKeypair(keyname));
		
	}
	
	private void assureSSHKey() throws InternalException, CloudException
	{
		org.dasein.cloud.services.access.AccessServices acs = providerImpl.getAccessServices();

		if (!acs.list().contains("sndefault"))
		{
			createKey(acs, "sndefault");
		}

	}
	private void assureSSHOnlyFirewall() throws InternalException, CloudException
	{
		org.dasein.cloud.services.firewall.FirewallServices fws = providerImpl.getFirewallServices();
		
		if(fws == null)
			return;
		
		Collection<org.dasein.cloud.services.firewall.Firewall> firewalls = fws.list();
		
		org.dasein.cloud.services.firewall.Firewall sshOnly = null;
		
		for(org.dasein.cloud.services.firewall.Firewall fw: firewalls)
		{
			if(fw.getName().equals("sshonly"))
			{
				sshOnly = fw;
				break;
			}
		}
		
		if(sshOnly == null)
		{
			System.out.println("Creating sshonly");
			String sshonlyId = fws.create("sshonly", "Allow only ssh traffic");
			fws.authorize(sshonlyId, "0.0.0.0/0", org.dasein.cloud.services.firewall.Protocol.TCP, 22, 22);
		}
	}


	public ArrayList<Server> listUnlinkedServers() throws Exception 
	{	
		ArrayList<Server> rv = new ArrayList<Server>();
		for(org.dasein.cloud.services.server.Server server: serverServices.list())
			rv.add(Server.load(server, providerClassName));
		
		return rv;
	}

	public void terminateServer(String serverId) throws InternalException, CloudException 
	{
		serverServices.stop(serverId);
	}
	
	public Server addServer(String serverName, String size) throws InternalException, CloudException
	{
		String imageId = "ami-15765c61";
		String dataCenterId = "eu-west-1b";
		String keypairOrPassword = "sndefault";
		String vlan = "";
		String[] firewalls = new String[]{ "sshonly" }	;

		org.dasein.cloud.services.server.Server daseinServer = serverServices
				.launch(imageId, new ServerSize(size), dataCenterId,
						serverName, keypairOrPassword, vlan, false, firewalls);

		Server server = new Server(daseinServer, providerClassName, keypairOrPassword, serverName);
		server.store(providerClassName);

		return null;
	}
	

	org.dasein.cloud.services.server.ServerServices getServerServices()
	{
		return providerImpl.getServerServices();
	}
	
	String getPreferredEndpoint() 
	{
		return providerSpecificProperties.getProperty("preferredendpoint");
	}
	
	String getCloudName()
	{
		return providerImpl.getCloudName();
	}
	
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

	public Collection<String> getPossibleInstanceSizes()
	{
		Collection<org.dasein.cloud.services.server.ServerSize> sizes = null;
		try
		{
			sizes = serverServices.getSupportedSizes(org.dasein.cloud.services.server.Architecture.I32);
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
		
		for(org.dasein.cloud.services.server.ServerSize size : sizes)
		{
			rv.add(size.getSizeId());
		}
		return rv;
	}

}
