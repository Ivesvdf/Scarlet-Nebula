package be.ac.ua.comp.scarletnebula.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.dasein.cloud.compute.VmState;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.AddServerWizard;
import be.ac.ua.comp.scarletnebula.gui.windows.GUI;
import be.ac.ua.comp.scarletnebula.gui.windows.PropertiesWindow;
import be.ac.ua.comp.scarletnebula.misc.Utils;

public class ServerListMouseListener implements MouseListener
{
	private GUI gui;
	private ServerListModel serverListModel;
	private ServerList serverlist;

	public ServerListMouseListener(GUI gui, ServerList serverlist,
			ServerListModel serverListModel)
	{
		super();
		this.gui = gui;
		this.serverlist = serverlist;
		this.serverListModel = serverListModel;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			ServerList list = (ServerList) e.getSource();
			int indexOfSelectedServer = list.locationToIndex(e.getPoint());

			// TODO:
			// When no servers OR a single server is selected, choose what
			// server this will take effect on based on the current mouse
			// position. If more servers are selected, apply action to all
			// selected servers!
			if (list.getSelectedIndices().length <= 1)
			{
				list.setSelectedIndex(indexOfSelectedServer);
			}
			final Server clickedServer = serverListModel
					.getVisibleServerAtIndex(indexOfSelectedServer);

			final Collection<Server> allSelectedServers = list
					.getSelectedServers();
			if (!allSelectedServers.contains(clickedServer))
			{
				list.setSelectedIndices(new int[0]);
			}
			if (clickedServer == null)
				return;

			final Collection<Server> selectedServers = list
					.getSelectedServers();

			VmState status = clickedServer.getStatus();

			JPopupMenu popup = new JPopupMenu();
			JMenuItem pauseResume = new JMenuItem(
					(status == VmState.PAUSED) ? "Resume" : "Pause",
					Utils.icon("paused.png"));

			pauseResume.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					gui.pauseSelectedServers();
				}
			});

			JMenuItem reboot = new JMenuItem("Reboot",
					Utils.icon("restarting.png"));
			reboot.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					gui.rebootSelectedServers();
				}
			});

			JMenuItem terminate = new JMenuItem("Terminate",
					Utils.icon("terminated.png"));
			terminate.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					gui.terminateSelectedServers();
				}
			});

			JMenuItem refresh = new JMenuItem("Refresh",
					Utils.icon("refresh16.png"));
			refresh.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					gui.refreshSelectedServers();
				}
			});

			JMenuItem unlink = new JMenuItem("Unlink Instance",
					Utils.icon("unlink16.png"));
			unlink.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					gui.unlinkSelectedServers();
				}
			});

			JMenuItem console = new JMenuItem("Start terminal",
					Utils.icon("console16.png"));
			console.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					new SSHTerminalWindow(gui, selectedServers);
				}
			});

			JMenuItem statistics = new JMenuItem("View statistics",
					Utils.icon("statistics16.png"));
			statistics.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					new StatisticsWindow(gui, selectedServers);
				}
			});

			if (status != VmState.RUNNING && status != VmState.PAUSED)
			{
				pauseResume.setEnabled(false);
				reboot.setEnabled(false);
				terminate.setEnabled(false);
			}
			if (status != VmState.RUNNING)
			{
				console.setEnabled(false);
				statistics.setEnabled(false);
			}

			popup.add(pauseResume);
			popup.add(reboot);
			popup.add(terminate);
			popup.add(refresh);
			popup.addSeparator();
			popup.add(console);
			popup.add(statistics);
			popup.addSeparator();
			popup.add(unlink);

			popup.show(e.getComponent(), e.getX(), e.getY());
		}
		else if (e.getClickCount() == 2)
		{
			// If the user double clicked a null server, this is taken as a hint
			// to create a new server!
			if (serverlist.startNewServerServerSelected())
			{
				new AddServerWizard(gui, gui);
			}
			else if (serverlist.getSelectedServers().size() > 0)
			// Normal double click on a server
			{
				new PropertiesWindow(gui, serverlist.getSelectedServers());
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

}