package be.ac.ua.comp.scarletnebula.core;

import java.util.Collection;
import java.util.HashMap;

import be.ac.ua.comp.scarletnebula.core.CloudProvider.CloudProviderName;

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
	HashMap<String, CloudProvider> providers;

	private CloudManager()
	{
		providers = new HashMap<String, CloudProvider>();
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
	 * @return
	 */
	public Collection<String> getLinkedCloudProviderNames()
	{
		return providers.keySet();
	}

	/**
	 * Returns all linked CloudProviders
	 * @return
	 */
	public Collection<CloudProvider> getLinkedCloudProviders()
	{
		return providers.values();
	}

	/**
	 * Returns the CloudProvider with name "name"
	 * @param name
	 * @return
	 */
	public CloudProvider getCloudProviderByName(String name)
	{
		return providers.get(name);
	}

	/**
	 * Returns true if the server with "name" exists in one of the CloudProviders, false otherwise
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
}
