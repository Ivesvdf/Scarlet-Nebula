package be.ac.ua.comp.scarletnebula.core;

public interface ServerChangedObserver
{
	/**
	 * This method will be called on all registered observers when the server in
	 * parameter changes.
	 * 
	 * @param server
	 *            The server that changed
	 */
	public void serverChanged(Server server);
}
