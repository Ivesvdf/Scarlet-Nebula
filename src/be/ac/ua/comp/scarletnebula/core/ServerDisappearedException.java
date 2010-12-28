package be.ac.ua.comp.scarletnebula.core;

public class ServerDisappearedException extends Exception
{
	ServerDisappearedException(Server server)
	{
		super("Server " + server.getFriendlyName() + " disappeared when refreshing.");
	}
}
