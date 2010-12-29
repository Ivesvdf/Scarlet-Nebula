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
	GUI gui;

	public ServerList(ServerListModel serverListModel, GUI gui)
	{
		super(serverListModel);
		this.serverListModel = serverListModel;
		this.gui = gui;
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setSelectedIndex(0);

		addMouseListener(new ServerListMouseListener(gui, serverListModel));
		addListSelectionListener(gui);
		setCellRenderer(new JLabelCellRenderer());
	}

	public void clearSelection()
	{
		setSelectedIndices(new int[0]);
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

}
