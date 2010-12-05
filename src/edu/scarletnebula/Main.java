package edu.scarletnebula;

import java.util.List;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			CloudProvider s = new CloudProvider(
					CloudProvider.CloudProviderName.AWS);

			if (args.length == 1 && args[0].equals("start"))
			{
				s.addServer();
			}
			else if(args.length == 2 && args[0].equals("stop"))
			{
				s.terminateServer(args[1]);
				System.out.println("Instance "+ args[1]+ " is terminated.");
			}
			else if (args.length == 1 && args[0].equals("list"))
			{
				List<Server> servers = s.listUnlinkedServers();

				if (servers.size() == 0)
				{
					System.out.println("No servers running.");
					return;
				}

				System.out.println("Servers");
				System.out.println("=======");

				for (Server server : servers)
				{
					System.out.println(server.toString());
				}
			}
			else
			{
				System.out.println("* * * Scarlet Nebula * * *");
				System.out.println("USAGE: ");
				System.out.println("Listing instances: \t\t scarletnebula list");
				System.out
						.println("Starting a new instance: \t scarletnebula start");
				System.out.println("Terminating an instance: \t scarletnebula stop <instanceid>");
				return;
			}

		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
