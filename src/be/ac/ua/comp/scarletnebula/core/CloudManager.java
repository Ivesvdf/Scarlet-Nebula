package be.ac.ua.comp.scarletnebula.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import be.ac.ua.comp.scarletnebula.core.CloudProvider.CloudProviderName;
import be.ac.ua.comp.scarletnebula.gui.CloudProviderTemplate;

/**
 * Singleton class that can be accessed through CloudManager.get(). E.g.
 * CloudManager.get().load() to load.
 * 
 * This class manages all clouds.
 * 
 * @author ives
 * 
 */
public class CloudManager
{
	HashMap<String, CloudProvider> providers = new HashMap<String, CloudProvider>();
	Collection<CloudProviderTemplate> providerTemplates = new ArrayList<CloudProviderTemplate>();

	private CloudManager()
	{
		populateCloudProviderTemplates();

		try
		{
			providers.put("Amazon Web Services", new CloudProvider(
					CloudProviderName.AWS));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void populateCloudProviderTemplates()
	{
		// AWS
		CloudProviderTemplate aws = new CloudProviderTemplate(
				"Amazon Elastic Compute Cloud", "Amazon EC2",
				"org.dasein.cloud.aws.AWSCloud");
		aws.addEndPoint("EU (Ireland)", "EU",
				"http://ec2.eu-west-1.amazonaws.com");
		aws.addEndPoint("Asia Pacific (Singapore)", "Asia",
				"http://ec2.ap-southeast-1.amazonaws.com");
		aws.addEndPoint("US-West (Northern California)", "US-West",
				"http://ec2.us-west-1.amazonaws.com");
		aws.addEndPoint("US-East (Northern Virginia)", "US-East",
				"http://ec2.us-east-1.amazonaws.com");

		providerTemplates.add(aws);

		// Rackspace
		CloudProviderTemplate rackspace = new CloudProviderTemplate(
				"Rackspace (not implemented)", "Rackspace",
				"org.dasein.cloud.aws.AWSCloud");
		providerTemplates.add(rackspace);
	}

	/**
	 * Bill Pugh's singleton implementation for Java. Because the class is not
	 * referenced before the first call to get, the instance will be lazily
	 * created. This is also perfectly threadsafe.
	 */
	private static class CloudManagerHolder
	{
		public static final CloudManager INSTANCE = new CloudManager();
	}

	/**
	 * Returns the singleton instance
	 * 
	 * @return
	 */
	public static CloudManager get()
	{
		return CloudManagerHolder.INSTANCE;
	}

	/**
	 * Returns the names of all linked CloudProviders
	 * 
	 * @return
	 */
	public Collection<String> getLinkedCloudProviderNames()
	{
		return providers.keySet();
	}

	/**
	 * Returns all linked CloudProviders
	 * 
	 * @return
	 */
	public Collection<CloudProvider> getLinkedCloudProviders()
	{
		return providers.values();
	}

	/**
	 * Returns the CloudProvider with name "name"
	 * 
	 * @param name
	 * @return
	 */
	public CloudProvider getCloudProviderByName(String name)
	{
		return providers.get(name);
	}

	/**
	 * Returns true if the server with "name" exists in one of the
	 * CloudProviders, false otherwise
	 * 
	 * @param name
	 * @return
	 */
	public boolean serverExists(String name)
	{
		for (CloudProvider prov : providers.values())
			if (prov.hasServer(name))
				return true;

		return false;
	}

	public Collection<CloudProviderTemplate> getTemplates()
	{
		return providerTemplates;
	}
}
