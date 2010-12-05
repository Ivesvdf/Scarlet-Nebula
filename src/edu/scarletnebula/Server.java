package edu.scarletnebula;

public class Server extends Instance
{
	org.dasein.cloud.services.server.Server serverImpl;
	
	public Server(org.dasein.cloud.services.server.Server server)
	{
		serverImpl = server;
	}

	public void sendFile(String filename)
	{
		
	}
	
	
	public ServerStatisticsManager getServerStatistics()
	{
		return null;
	}
	
	CommandConnection getCommandConnection()
	{
		return null;		
	}
	
	public String toString()
	{
		String rv = serverImpl.getProviderServerId() + " (" + serverImpl.getCurrentState() + ") @ " + serverImpl.getPublicDnsAddress();
		return rv; 
	}
}
