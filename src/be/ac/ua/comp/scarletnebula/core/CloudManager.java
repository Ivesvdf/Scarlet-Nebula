package be.ac.ua.comp.scarletnebula.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;

import be.ac.ua.comp.scarletnebula.core.CloudProviderTemplate.AccessMethod;

/**
 * Singleton class that can be accessed through CloudManager.get(). E.g.
 * CloudManager.get().load() to load.
 * 
 * This class manages all clouds.
 * 
 * @author ives
 * 
 */
public final class CloudManager {
	private final HashMap<String, CloudProvider> providers = new HashMap<String, CloudProvider>();
	private final Collection<CloudProviderTemplate> providerTemplates = new ArrayList<CloudProviderTemplate>();
	private final Collection<ServerLinkUnlinkObserver> linkUnlinkObservers = new ArrayList<ServerLinkUnlinkObserver>();

	/**
	 * Private constructor.
	 */
	private CloudManager() {
		populateCloudProviderTemplates();

		// Load all providers and put them in the list
		for (final String provname : CloudProvider.getProviderNames()) {
			final CloudProvider cloudProvider = new CloudProvider(provname);

			for (final ServerLinkUnlinkObserver obs : linkUnlinkObservers) {
				cloudProvider.addServerLinkUnlinkObserver(obs);
			}

			providers.put(provname, cloudProvider);
		}
	}

	/**
	 * Adds an observer that will be notified when a server is linked &
	 * unlinked.
	 * 
	 * @param obs
	 *            The observer to add
	 */
	public void addServerLinkUnlinkObserver(final ServerLinkUnlinkObserver obs) {
		linkUnlinkObservers.add(obs);

		// Also add this observer to the cloudproviders that are already in the
		// system.
		for (final CloudProvider prov : providers.values()) {
			prov.addServerLinkUnlinkObserver(obs);
		}
	}

	/**
	 * Fills the available cloud provider templates.
	 */
	private void populateCloudProviderTemplates() {
		// AWS
		final CloudProviderTemplate aws = new CloudProviderTemplate(
				"Amazon Elastic Compute Cloud", "Amazon EC2",
				"org.dasein.cloud.aws.AWSCloud", AccessMethod.KEY);
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
				"org.dasein.cloud.aws.AWSCloud", AccessMethod.KEY);
		providerTemplates.add(rackspace);

		// Radix
		final CloudProviderTemplate radix = new CloudProviderTemplate(
				"Mock Cloud Provider", "Radix",
				"be.ac.ua.comp.scarletnebula.misc.RadixCloudProvider",
				AccessMethod.EMAILPASSWD);
		radix.addEndPoint("default", "default", "radix.cmi.ua.ac.be");
		providerTemplates.add(radix);

		final CloudProviderTemplate cloudSigma = new CloudProviderTemplate(
				"CloudSigma", "CloudSigma",
				"org.dasein.cloud.jclouds.cloudsigma.CloudSigma",
				AccessMethod.EMAILPASSWD);

		providerTemplates.add(cloudSigma);
	}

	/**
	 * Bill Pugh's singleton implementation for Java. Because the class is not
	 * referenced before the first call to get, the instance will be lazily
	 * created. This is also perfectly threadsafe.
	 */
	private static class CloudManagerHolder {
		public static final CloudManager INSTANCE = new CloudManager();
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return The instance.
	 */
	public static CloudManager get() {
		return CloudManagerHolder.INSTANCE;
	}

	/**
	 * Returns the names of all linked CloudProviders.
	 * 
	 * @return Collection of all the names of linked cloud providers.
	 */
	public Collection<String> getLinkedCloudProviderNames() {
		return providers.keySet();
	}

	/**
	 * Returns all linked CloudProviders.
	 * 
	 * @return The actual linked cloudproviders.
	 */
	public Collection<CloudProvider> getLinkedCloudProviders() {
		return providers.values();
	}

	/**
	 * Returns the CloudProvider with name "name".
	 * 
	 * @param name
	 *            The name of the cloud provider to get.
	 * @return The cloudprovider with name "name"
	 */
	public CloudProvider getCloudProviderByName(final String name) {
		return providers.get(name);
	}

	/**
	 * Returns true if the server with "name" exists in one of the
	 * CloudProviders, false otherwise.
	 * 
	 * @param name
	 *            The friendly name of the server if it exists
	 * @return True if a server by that name exists, false otherwise.
	 */
	public boolean serverExists(final String name) {
		for (final CloudProvider prov : providers.values()) {
			if (prov.hasServer(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return A Collection of all available CloudProviderTemplates
	 */
	public Collection<CloudProviderTemplate> getTemplates() {
		return providerTemplates;
	}

	/**
	 * Registers a new Cloudprovider.
	 * 
	 * @param name
	 *            Friendly name of the new cloudprovider
	 * @param classname
	 *            The dasein classname for this cloudprovider
	 * @param endpoint
	 *            Endpoint to connect to.
	 * @param apiKey
	 *            Access id or login
	 * @param apiSecret
	 *            Key or password
	 */
	public void registerNewCloudProvider(final String name,
			final String classname, final String endpoint, final String apiKey,
			final String apiSecret) {
		final CloudProvider prov = new CloudProvider(name, classname, endpoint,
				apiKey, apiSecret, "");
		prov.store();

		for (final ServerLinkUnlinkObserver obs : linkUnlinkObservers) {
			prov.addServerLinkUnlinkObserver(obs);
		}

		providers.put(name, prov);
	}

	/**
	 * Removes a cloudprovider, all of his servers and their respective
	 * savefiles.
	 * 
	 * @param provname
	 *            The provider's name that should be removed.
	 */
	public void deleteCloudProvider(final String provname) {
		final CloudProvider provider = providers.get(provname);
		providers.remove(provname);
		System.out.println(getLinkedCloudProviders().size());

		// Remove all of his servers
		final Collection<Server> linkedServers = provider.listLinkedServers();
		for (final Server server : linkedServers) {
			server.unlink();
		}

		// Remove his server directory
		final File serverdir = removeFilesInDir("servers/" + provname);

		// Should the provider's keyfiles be removed??

		// Now delete the dir itself
		serverdir.delete();

		// And delete his configfile
		final File config = new File(CloudProvider.getConfigfileName(provname));
		config.delete();
	}

	/**
	 * Removes all files in a certain directory.
	 * 
	 * @param dirname
	 *            The directory whose contents to remove
	 * @return A File describing the hopefully empty directory.
	 */
	private File removeFilesInDir(final String dirname) {
		final File dir = new File(dirname);

		if (dir.list() != null) {
			// If there are still files in the directory, delete them
			for (final String file : dir.list()) {
				final File f = new File(file);
				f.delete();
			}
		}
		return dir;
	}

	/**
	 * Sends a request to each CloudProvider to load all of its servers.
	 * 
	 * @throws InternalException
	 * @throws CloudException
	 * @throws IOException
	 */
	public void loadAllLinkedServers() throws InternalException,
			CloudException, IOException {
		for (final CloudProvider prov : providers.values()) {
			prov.loadLinkedServers();
		}
	}
}
