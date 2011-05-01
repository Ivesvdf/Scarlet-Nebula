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
import be.ac.ua.comp.scarletnebula.gui.windows.ServerPropertiesWindow;
import be.ac.ua.comp.scarletnebula.misc.Utils;

public class ServerListMouseListener implements MouseListener
{
	private final GUI gui;
	private final ServerListModel serverListModel;
	private final ServerList serverlist;

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
			final ServerList list = (ServerList) e.getSource();
			final int indexOfSelectedServer = list.locationToIndex(e.getPoint());

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

			final VmState status = clickedServer.getStatus();

			final JPopupMenu popup = new JPopupMenu();
			final JMenuItem pauseResume = new JMenuItem(
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

			final JMenuItem reboot = new JMenuItem("Reboot",
					Utils.icon("restarting.png"));
			reboot.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					gui.rebootSelectedServers();
				}
			});

			final JMenuItem terminate = new JMenuItem("Terminate",
					Utils.icon("terminated.png"));
			terminate.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					gui.terminateSelectedServers();
				}
			});

			final JMenuItem refresh = new JMenuItem("Refresh",
					Utils.icon("refresh16.png"));
			refresh.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					gui.refreshSelectedServers();
				}
			});

			final JMenuItem unlink = new JMenuItem("Unlink Instance",
					Utils.icon("unlink16.png"));
			unlink.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					gui.unlinkSelectedServers();
				}
			});

			final JMenuItem console = new JMenuItem("Start terminal",
					Utils.icon("console16.png"));
			console.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					new SSHTerminalWindow(gui, selectedServers);
				}
			});

			final JMenuItem statistics = new JMenuItem("View statistics",
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
			if (clickedServer.sshWillFail() || status != VmState.RUNNING)
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
		else if (e.getClickCount() >= 1
				&& serverlist.startNewServerServerSelected())
		{
			// If the user clicked a null server, this is taken as a hint
			// to create a new server!
			new AddServerWizard(gui, gui);
		}
		else if (e.getClickCount() == 2
				&& serverlist.getSelectedServers().size() > 0)
		// Normal double click on a server
		{
			new ServerPropertiesWindow(gui, serverlist.getSelectedServers());
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