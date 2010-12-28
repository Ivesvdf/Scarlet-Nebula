package be.ac.ua.comp.scarletnebula.core;

public class ServerDisappearedException extends Exception
{

	private static final long serialVersionUID = 1L;

	ServerDisappearedException(Server server)
	{
		super("Server " + server.getFriendlyName() + " disappeared when refreshing.");
	}
}
