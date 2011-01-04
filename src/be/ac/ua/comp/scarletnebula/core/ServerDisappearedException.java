package be.ac.ua.comp.scarletnebula.core;

/**
 * The exception that will be thrown when a Server is refreshed and the Server
 * is gone (either because it was deleted outside of Scarlet Nebula or because
 * it was terminated and disappeared in the mean while.
 * 
 * @author ives
 * 
 */
public class ServerDisappearedException extends Exception
{
	private static final long serialVersionUID = 1L;

	ServerDisappearedException(Server server)
	{
		super("Server " + server.getFriendlyName()
				+ " disappeared when refreshing.");
	}
}
