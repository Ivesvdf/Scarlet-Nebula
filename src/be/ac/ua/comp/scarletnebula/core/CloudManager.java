package be.ac.ua.comp.scarletnebula.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;

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
	Collection<ServerLinkUnlinkObserver> linkUnlinkObservers = new ArrayList<ServerLinkUnlinkObserver>();

	private CloudManager()
	{
		populateCloudProviderTemplates();

		// Load all providers and put them in the list
		for (final String provname : CloudProvider.getProviderNames())
		{
			final CloudProvider cloudProvider = new CloudProvider(provname);

			for (final ServerLinkUnlinkObserver obs : linkUnlinkObservers)
			{
				cloudProvider.addServerLinkUnlinkObserver(obs);
			}

			providers.put(provname, cloudProvider);
		}
	}

	public void addServerLinkUnlinkObserver(final ServerLinkUnlinkObserver obs)
	{
		linkUnlinkObservers.add(obs);

		// Also add this observer to the cloudproviders that are already in the
		// system.
		for (final CloudProvider prov : providers.values())
		{
			prov.addServerLinkUnlinkObserver(obs);
		}
	}

	private void populateCloudProviderTemplates()
	{
		// AWS
		final CloudProviderTemplate aws = new CloudProviderTemplate(
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
		final CloudProviderTemplate rackspace = new CloudProviderTemplate(
				"Rackspace (not implemented)", "Rackspace",
				"org.dasein.cloud.aws.AWSCloud");
		providerTemplates.add(rackspace);

		// Radix
		final CloudProviderTemplate radix = new CloudProviderTemplate("Radix",
				"Radix", "be.ac.ua.comp.scarletnebula.misc.RadixCloudProvider");
		radix.addEndPoint("default", "default", "radix.cmi.ua.ac.be");
		providerTemplates.add(radix);

		final CloudProviderTemplate cloudSigma = new CloudProviderTemplate(
				"CloudSigma", "CloudSigma",
				"org.dasein.cloud.jclouds.cloudsigma.CloudSigma");

		providerTemplates.add(cloudSigma);
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
	public CloudProvider getCloudProviderByName(final String name)
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
	public boolean serverExists(final String name)
	{
		for (final CloudProvider prov : providers.values())
		{
			if (prov.hasServer(name))
			{
				return true;
			}
		}

		return false;
	}

	public Collection<CloudProviderTemplate> getTemplates()
	{
		return providerTemplates;
	}

	public void registerNewCloudProvider(final String name,
			final String classname, final String endpoint, final String apiKey,
			final String apiSecret)
	{
		final CloudProvider prov = new CloudProvider(name, classname, endpoint,
				apiKey, apiSecret, "");
		prov.store();

		for (final ServerLinkUnlinkObserver obs : linkUnlinkObservers)
		{
			prov.addServerLinkUnlinkObserver(obs);
		}

		providers.put(name, prov);
	}

	public void deleteCloudProvider(final String provname)
	{
		final CloudProvider provider = providers.get(provname);
		providers.remove(provname);
		System.out.println(getLinkedCloudProviders().size());

		// Remove all of his servers
		final Collection<Server> linkedServers = provider.listLinkedServers();
		for (final Server server : linkedServers)
		{
			server.unlink();
		}

		// Remove his server directory
		final File serverdir = removeDirContainingFiles("servers/" + provname);

		// Should the provider's keyfiles be removed??

		// Now delete the dir itself
		serverdir.delete();

		// And delete his configfile
		final File config = new File(CloudProvider.getConfigfileName(provname));
		config.delete();
	}

	private File removeDirContainingFiles(final String dirname)
	{
		final File dir = new File(dirname);

		if (dir.list() != null)
		{
			// If there are still files in the directory, delete them
			for (final String file : dir.list())
			{
				final File f = new File(file);
				f.delete();
			}
		}
		return dir;
	}

	public void loadAllLinkedServers() throws InternalException,
			CloudException, IOException
	{
		for (final CloudProvider prov : providers.values())
		{
			prov.loadLinkedServers();
		}
	}
}
