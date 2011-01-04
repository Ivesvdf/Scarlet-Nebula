package be.ac.ua.comp.scarletnebula.cli;

import java.util.List;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.CommandConnection;
import be.ac.ua.comp.scarletnebula.core.Server;


public class Main
{
	public static void main(String[] args)
	{
		try
		{
			CloudProvider c = new CloudProvider(
					CloudProvider.CloudProviderName.AWS);

			if (args.length == 1 && args[0].equals("start"))
			{
				c.startServer("srv", "m1.small");
				System.out.println("Instance started.");
			}
			else if(args.length == 2 && args[0].equals("stop"))
			{
				c.terminateServer(args[1]);
				System.out.println("Instance "+ args[1]+ " is terminated.");
			}
			else if (args.length == 1 && args[0].equals("list"))
			{
				List<Server> servers = c.listUnlinkedServers();

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
			else if (args.length == 3 && args[0].equals("cmd"))
			{
				String instancename = args[1];
				String command = args[2];
				
				Server srv = c.loadServer(instancename);
				CommandConnection cmd = srv.newCommandConnection();
				System.out.println("System response:");
				System.out.println(cmd.executeCommand(command));
				cmd.close();
			}
			else
			{
				System.out.println("* * * Scarlet Nebula * * *");
				System.out.println("USAGE: ");
				System.out.println("Listing instances: \t\t scarletnebula list");
				System.out
						.println("Starting a new instance: \t scarletnebula start");
				System.out.println("Terminating an instance: \t scarletnebula stop <instanceid>");
				System.out.println("Executing command on instance: \t scarletnebula cmd <instanceid> \"some command\"");

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
