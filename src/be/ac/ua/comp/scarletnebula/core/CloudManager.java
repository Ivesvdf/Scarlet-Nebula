package be.ac.ua.comp.scarletnebula.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import be.ac.ua.comp.scarletnebula.core.CloudProvider.CloudProviderName;

/**
 * Singleton class that can be accessed through CloudManager.get(). E.g.
 * CloudManager.get().load() to load.
 * 
 * This class manages all clouds. 
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

	// Bill Pugh's singleton implementation for Java. Because the class is not
	// referenced before the first call to get, the instance will be lazily
	// created. This is also perfectly threadsafe.
	private static class CloudManagerHolder
	{
		public static final CloudManager INSTANCE = new CloudManager();
	}

	public static CloudManager get()
	{
		return CloudManagerHolder.INSTANCE;
	}

	public void load()
	{
		// TODO: Move ctor here, only load when a config file is present.
	}

	public Set<String> getLinkedCloudProviderNames()
	{
		return providers.keySet();
	}

	public Collection<CloudProvider> getLinkedCloudProviders()
	{
		return providers.values();
	}

	ArrayList<CloudProvider> getAllCloudProviders()
	{
		return null;
	}

	public CloudProvider getCloudProviderByName(String name)
	{
		return providers.get(name);
	}

	public boolean serverExists(String name)
	{
		for(CloudProvider prov : providers.values())
			if(prov.hasServer(name))
				return true;
		
		return false;
	}
}
