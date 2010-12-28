package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.services.server.ServerState;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.core.ServerDisappearedException;

/**
 * JList that displays JLabels as items in the list. This will allow me to
 * display an image next to the text (for server status).
 * 
 * @author ives
 * 
 */
public class ServerList extends javax.swing.JList
{
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(ServerList.class);

	ServerListModel serverListModel;

	public ServerList(ServerListModel serverListModel)
	{
		super(serverListModel);
		this.serverListModel = serverListModel;
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setSelectedIndex(0);

		addMouseListener(new ServerListMouseListener());
		setCellRenderer(new JLabelCellRenderer());
	}

	class JLabelCellRenderer extends JLabel implements ListCellRenderer
	{

		private static final long serialVersionUID = 1L;

		JLabelCellRenderer()
		{
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			Color background;
			Color foreground;

			// check if this cell represents the current DnD drop location
			JList.DropLocation dropLocation = list.getDropLocation();
			if (dropLocation != null && !dropLocation.isInsert()
					&& dropLocation.getIndex() == index)
			{
				background = Color.RED;
				foreground = Color.WHITE;
			}
			else if (isSelected)
			{
				background = Color.decode("#ADADAD");
				foreground = Color.WHITE;
			}
			else
			{
				background = Color.WHITE;
				foreground = Color.BLACK;
			}

			JLabel label = (JLabel) value;
			label.setOpaque(true);

			label.setBackground(background);
			label.setForeground(foreground);

			return label;

		}
	}

	class ServerListMouseListener implements MouseListener
	{

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
				JPopupMenu popup = new JPopupMenu();
				ServerList list = (ServerList) e.getSource();
				int indexOfSelectedServer = list.locationToIndex(e.getPoint());
				list.setSelectedIndex(indexOfSelectedServer);
				final Server selectedServer = serverListModel
						.getVisibleServerAtIndex(indexOfSelectedServer);

				if (selectedServer == null)
					return;

				ServerState status = selectedServer.getStatus();

				JMenuItem pauseResume = new JMenuItem(
						(status == ServerState.PAUSED) ? "Resume" : "Pause");
				pauseResume.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							selectedServer.pause();
						}
						catch (InternalException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (CloudException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

				JMenuItem reboot = new JMenuItem("Reboot");
				reboot.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							selectedServer.reboot();
						}
						catch (CloudException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (InternalException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

				JMenuItem terminate = new JMenuItem("Terminate");
				terminate.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							selectedServer.terminate();
						}
						catch (InternalException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (CloudException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

				JMenuItem refresh = new JMenuItem("Refresh");
				refresh.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							selectedServer.refresh();
						}
						catch (InternalException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (CloudException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (ServerDisappearedException e1)
						{
							log.info(e.toString());
							serverListModel.removeServer(selectedServer);
						}
					}
				});

				JMenuItem unlink = new JMenuItem("Unlink Instance");
				unlink.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						serverListModel.removeServer(selectedServer);
						selectedServer.getCloud().unlink(selectedServer);
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
}
