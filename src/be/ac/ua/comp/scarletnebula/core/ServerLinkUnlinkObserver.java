package be.ac.ua.comp.scarletnebula.core;

public interface ServerLinkUnlinkObserver
{

	/**
	 * Method will be called when a server was linked with this run of Scarlet
	 * Nebula. This is indicative of one of three things: - A server was loaded
	 * from file - A server was created - An unlinked server was linked
	 * 
	 * @param cloudProvider
	 * @param srv
	 */
	void serverLinked(CloudProvider cloudProvider, Server srv);

	void serverUnlinked(CloudProvider cloudProvider, Server srv);

}
