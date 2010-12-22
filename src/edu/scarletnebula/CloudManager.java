package edu.scarletnebula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import edu.scarletnebula.CloudProvider.CloudProviderName;

public class CloudManager
{
	HashMap<String, CloudProvider> providers;
	
	CloudManager()
	{
		providers = new HashMap<String, CloudProvider>();
		try
		{
			providers.put("Amazon Web Services", new CloudProvider(CloudProviderName.AWS));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void load()
	{
		// TODO: Move ctor here, only load when a config file is present. 
	}
	
	Set<String> getLinkedCloudProviderNames()
	{
		return providers.keySet();
	}
	
	Collection<CloudProvider> getLinkedCloudProviders()
	{
		return providers.values();
	}
	
	ArrayList<CloudProvider> getAllCloudProviders()
	{
		return null;
	}
	
	CloudProvider getCloudProviderByName(String name)
	{
		return providers.get(name);
	}
}
