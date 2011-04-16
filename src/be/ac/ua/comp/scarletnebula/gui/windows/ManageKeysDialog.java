package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.KeyList;

public class ManageKeysDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	CloudProvider provider;
	GUI gui;

	ManageKeysDialog(GUI gui, CloudProvider provider)
	{
		super(gui, "Manage Keys", true);
		this.provider = provider;
		this.gui = gui;
		setSize(400, 400);
		setLocationByPlatform(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final KeyList keyList = new KeyList(provider);

		JScrollPane keyListScrollPane = new JScrollPane(keyList);
		keyListScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20,
				20));

		setLayout(new BorderLayout());

		JButton addButton = new JButton(new ImageIcon(getClass().getResource(
				"/images/add22.png")));
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

			}
		});

		JButton loadButton = new JButton("Import");

		JButton removeButton = new JButton(new ImageIcon(getClass()
				.getResource("/images/remove22.png")));
		removeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int index = keyList.getSelectedIndex();

				// No selection
				if (index < 0)
					return;

				keyList.remove(index);
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		buttonPanel.add(addButton);
		buttonPanel.add(loadButton);
		buttonPanel.add(removeButton);

		add(keyListScrollPane);
		add(buttonPanel, BorderLayout.SOUTH);
	}

}
