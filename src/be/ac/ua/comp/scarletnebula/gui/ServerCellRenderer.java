package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.Server;

class ServerCellRenderer implements ListCellRenderer
{
	private static final long serialVersionUID = 1L;
	public static HashMap<Server, JPanel> panelMapping = new HashMap<Server, JPanel>();

	ServerCellRenderer()
	{
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
	{
		// Dirty hack: the last item in the serverlist is always a fake server
		// that when double clicked produces an "add new server" wizard.
		if (value == null)
			return getNewServerServer(list, index, isSelected);

		Server server = (Server) value;

		JPanel p = createServerPanel(list, index, isSelected);

		final Color background = getBackgroundColor(list, index, isSelected);
		final Color foreground = getForegroundColor(list, index, isSelected);

		JLabel label = new JLabel(server.getFriendlyName(),
				getServerIcon(server), SwingConstants.LEFT);
		label.setOpaque(true);

		label.setBackground(background);
		label.setForeground(foreground);

		JLabel tags = new JLabel();

		Font tagFont = new Font(tags.getFont().getName(), Font.PLAIN, 10);
		tags.setFont(tagFont);
		tags.setText("dns, webserver");
		tags.setBackground(background);
		tags.setForeground(foreground);

		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 1, 5);
		c.anchor = GridBagConstraints.FIRST_LINE_START;

		p.add(label, c);
		c.insets = new Insets(0, 5, 5, 5);
		c.gridy = 1;
		p.add(tags, c);

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.gridy = 2;
		p.add(new JLabel(), c);

		return p;

	}

	Color getBackgroundColor(JList list, int index, boolean isSelected)
	{
		Color background;

		// check if this cell represents the current DnD drop location
		JList.DropLocation dropLocation = list.getDropLocation();
		if (dropLocation != null && !dropLocation.isInsert()
				&& dropLocation.getIndex() == index)
		{
			background = Color.RED;
		}
		else if (isSelected)
		{
			background = UIManager.getColor("Tree.selectionBackground");
		}
		else
		{
			background = Color.WHITE;
		}

		return background;
	}

	Color getForegroundColor(JList list, int index, boolean isSelected)
	{
		Color foreground;

		// check if this cell represents the current DnD drop location
		JList.DropLocation dropLocation = list.getDropLocation();
		if (dropLocation != null && !dropLocation.isInsert()
				&& dropLocation.getIndex() == index)
		{
			foreground = Color.WHITE;
		}
		else if (isSelected)
		{
			foreground = UIManager.getColor("Tree.selectionForeground");
		}
		else
		{
			foreground = Color.BLACK;
		}
		return foreground;
	}

	private JPanel createServerPanel(JList list, int index, boolean isSelected)
	{
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());

		Color background = getBackgroundColor(list, index, isSelected);

		p.setBackground(background);

		if (isSelected)
		{
			p.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(
							UIManager.getColor("List.background"), 2),
					BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
		}
		else
		{
			p.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(2, 2, 2, 2),
					BorderFactory.createEtchedBorder()));

		}

		return p;
	}

	private Component getNewServerServer(JList list, int index,
			boolean isSelected)
	{
		JPanel p = createServerPanel(list, index, isSelected);
		JLabel label = new JLabel("Start a new server", new ImageIcon(
				getClass().getResource("/images/add.png")), JLabel.LEFT);
		label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 16));
		// Border for better horizontal alignment
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
		p.add(label);

		return p;
	}

	/**
	 * Returns the icon that represents this server's status.
	 * 
	 * @param server
	 * @return
	 */
	public ImageIcon getServerIcon(Server server)
	{
		String filename = null;

		switch (server.getStatus())
		{
			case PAUSED:
				filename = "/images/paused.png";
				break;
			case PENDING:
				filename = "/images/pending.png";
				break;
			case RUNNING: // This needs to be made load-dependent...
				filename = "/images/running_ok.png";
				break;
			case REBOOTING:
				filename = "/images/restarting.png";
				break;
			case STOPPING:
				filename = "/images/stopping.png";
				break;
			case TERMINATED:
				filename = "/images/terminated.png";
				break;

		}

		ImageIcon icon = new ImageIcon(getClass().getResource(filename));
		return icon;
	}
}