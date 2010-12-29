package be.ac.ua.comp.scarletnebula.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.dasein.cloud.services.server.ServerState;

import be.ac.ua.comp.scarletnebula.core.Server;


	class ServerListMouseListener implements MouseListener
	{
		private GUI gui;
		private ServerListModel serverListModel;

		ServerListMouseListener(GUI gui, ServerListModel serverListModel)
		{
			super();
			this.gui = gui;
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
				list.setSelectedIndex(indexOfSelectedServer);
				final Server selectedServer = serverListModel
						.getVisibleServerAtIndex(indexOfSelectedServer);

				if (selectedServer == null)
					return;

				ServerState status = selectedServer.getStatus();

				JPopupMenu popup = new JPopupMenu();
				JMenuItem pauseResume = new JMenuItem(
						(status == ServerState.PAUSED) ? "Resume" : "Pause");
				pauseResume.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						gui.pauseSelectedServers();
					}
				});

				JMenuItem reboot = new JMenuItem("Reboot");
				reboot.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						gui.rebootSelectedServers();
					}
				});

				JMenuItem terminate = new JMenuItem("Terminate");
				terminate.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						gui.terminateSelectedServers();
					}
				});

				JMenuItem refresh = new JMenuItem("Refresh");
				refresh.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						gui.refreshSelectedServers();
					}
				});

				JMenuItem unlink = new JMenuItem("Unlink Instance");
				unlink.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						gui.unlinkSelectedServers();
					}
				});

				if (status != ServerState.RUNNING
						&& status != ServerState.PAUSED)
				{
					pauseResume.setEnabled(false);
					reboot.setEnabled(false);
					terminate.setEnabled(false);
				}

				popup.add(pauseResume);
				popup.add(reboot);
				popup.add(terminate);
				popup.add(refresh);
				popup.add(unlink);

				popup.show(e.getComponent(), e.getX(), e.getY());
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