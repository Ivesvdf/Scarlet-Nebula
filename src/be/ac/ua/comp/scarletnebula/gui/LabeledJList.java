package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * JList that displays JLabels as items in the list. This will allow me to
 * display an image next to the text (for server status).
 * 
 * @author ives
 * 
 */
public class LabeledJList extends javax.swing.JList
{
	private static final long serialVersionUID = 1L;

	public LabeledJList(ServerListModel serverListModel)
	{
		super(serverListModel);

		setCellRenderer(new CustomCellRenderer());
	}

	class CustomCellRenderer extends JLabel implements ListCellRenderer
	{

		private static final long serialVersionUID = 1L;

		CustomCellRenderer()
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
				background = Color.decode("#000066");
				foreground = Color.WHITE;
			}
			else
			{
				background = Color.WHITE;
				foreground = Color.BLACK;
			}
			
			JLabel label = (JLabel)value;
			label.setOpaque(true);
			
			label.setBackground(background);
			label.setForeground(foreground);

			return label;

		}
	}
}
