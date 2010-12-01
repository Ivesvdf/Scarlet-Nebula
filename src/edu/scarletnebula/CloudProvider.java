package edu.scarletnebula;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

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

	//TODO: Make this a class hierarchy
	CloudProvider(CloudProviderName name) throws Exception
	{
		String providerClassName;

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
			//providerSpecificProperties.setProperty("apikey", "i-48526f3f");
			providerSpecificProperties.load(new FileInputStream(providerClassName + ".properties"));	
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		
		providerImpl = 
			(org.dasein.cloud.CloudProvider) Class.forName(providerClassName).newInstance();
	
		providerImpl.connect(getCurrentContext());
		
		
	
		assureSSHOnlyFirewall();
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
			System.out.println(fw.getName());

			if(fw.getName().equals("sshonly"))
			{
				sshOnly = fw;
			}
			
			
		}
		
		if(sshOnly == null)
		{
			System.out.println("Creating sshonly");
			String sshonlyId = fws.create("sshonly", "Allow only ssh traffic");
			fws.authorize(sshonlyId, "0.0.0.0/0", org.dasein.cloud.services.firewall.Protocol.TCP, 22, 22);
		}
		else
			System.out.println("Found sshonly");
	}


	public ArrayList<Server> listUnlinkedServers() throws Exception 
	{
		ArrayList<Server> rv = new ArrayList<Server>();
		
		try
		{
			org.dasein.cloud.services.server.ServerServices services = providerImpl.getServerServices();

			if(services == null)
			{
				System.out.println(providerImpl.getCloudName()
						+ " does not support compute instances.");
				return null;
			}
			

			for (org.dasein.cloud.services.server.Server server : services
					.list())
			{
				// Create a new SNC Server from a Dasein Server object
				rv.add(new Server(server));
				System.out.println("Server #"
						+ server.getProviderServerId() + ": "
						+ server.getName());
			}

		} catch (org.dasein.cloud.CloudException e)
		{
			System.err
					.println("Error in the cloud provider processing your request: "
							+ e.getMessage());
			e.printStackTrace();
		} catch (org.dasein.cloud.InternalException e)
		{
			System.err
					.println("Configuration error or error in the Dasein implementation: "
							+ e.getMessage());
			e.printStackTrace();
		}
		
		return rv;
		
	}

	Server addServer() throws InternalException, CloudException
	{
		String imageId = "ami-d80a3fac";
		String size = "m1.small";
		String dataCenterId = "eu-west-1b";
		String serverName = "lovelyserver";
		String keypairOrPassword = "default";
		String vlan = "";
		String[] firewalls = new String[]{ "sshonly" }	;

		ServerServices services = providerImpl.getServerServices();

		if (services != null)
		{
			org.dasein.cloud.services.server.Server server = services.launch(
					imageId, new ServerSize(size), dataCenterId, serverName,
					keypairOrPassword, vlan, false, firewalls);

			System.out
					.println("New server ID: " + server.getProviderServerId());	
			System.out.println("Is available at: "+ server.getPublicDnsAddress());
		}
		else
		{
			System.out.println(providerImpl.getCloudName()
					+ " does not support compute instances.");
		}

		return null;
	}
	

	org.dasein.cloud.services.server.ServerServices getServerServices()
	{
		return providerImpl.getServerServices();
	}
	
	String getPreferredEndpoint() 
	{
		return "http://ec2.eu-west-1.amazonaws.com";
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
		String apiKey = getAPIKey("aws");
		String privateKey = getPrivateKey("aws");

		org.dasein.cloud.ProviderContext context = new org.dasein.cloud.ProviderContext();

		context.setAccountNumber(accountNumber);
		context.setAccessPublic(apiKey.getBytes());
		context.setAccessPrivate(privateKey.getBytes());
        context.setEndpoint(getPreferredEndpoint());
        
        return context;
	}
	
	private String getAPIKey(String cloudProvider)
	{
		// TODO : Read from file, make provider specific & cache this in a hashtable or whatever
		return providerSpecificProperties.getProperty("apikey");
	}

	private String getPrivateKey(String cloudProvider)
	{
		return providerSpecificProperties.getProperty("apisecret");
	}
}
