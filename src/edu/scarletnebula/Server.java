package edu.scarletnebula;

public class Server extends Instance
{
	public Server(org.dasein.cloud.services.server.Server server)
	{
		// TODO Auto-generated constructor stub
	}

	public void sendFile(String filename)
	{
		
	}
	
	public String getPublicIP()
	{
		return "0.0.0.0";
	}
	
	public ServerStatisticsManager getServerStatistics()
	{
		return null;
	}
	
	CommandConnection getCommandConnection()
	{
		return null;		
	}
}
