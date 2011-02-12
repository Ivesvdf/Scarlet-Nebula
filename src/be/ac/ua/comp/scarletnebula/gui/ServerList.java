package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

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

	ServerListModel serverListModel;

	public ServerList(ServerListModel serverListModel)
	{
		super(serverListModel);
		this.serverListModel = serverListModel;
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setSelectedIndex(0);
		setCellRenderer(new JLabelCellRenderer());
	}

	@Override
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
