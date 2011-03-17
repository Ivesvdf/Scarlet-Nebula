package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.KeyManager;

public class KeyList extends JList
{
	private static final long serialVersionUID = 1L;
	DefaultListModel model;

	KeyList(CloudProvider provider)
	{
		super(new DefaultListModel());
		model = (DefaultListModel) getModel();

		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setCellRenderer(new LabelCellRenderer());

		for (String keyname : KeyManager.getKeyNames(provider.getName()))
			add(keyname);
	}

	public void add(String keyname)
	{
		model.addElement(new JLabel(keyname, new ImageIcon(getClass()
				.getResource("/images/key16.png")), SwingConstants.LEFT));
	}

	public String getSelectedKey()
	{
		int selection = getSelectedIndex();

		if (selection < 0)
			return null;

		JLabel label = (JLabel) model.get(selection);
		return label.getText();
	}

	class LabelCellRenderer implements ListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{

			JLabel renderer = (JLabel) value;

			Color foreground;
			Color background;

			if (isSelected)
			{
				background = UIManager.getColor("List.selectionBackground");
				foreground = UIManager.getColor("List.selectionForeground");
			}
			else
			{
				background = Color.WHITE;
				foreground = Color.BLACK;
			}
			if (!isSelected)
			{
				renderer.setForeground(foreground);
				renderer.setBackground(background);
			}

			return renderer;
		}
	}
}
