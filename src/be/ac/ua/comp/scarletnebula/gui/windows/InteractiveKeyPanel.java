package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.newkeywizard.NewKeyWizard;
import be.ac.ua.comp.scarletnebula.misc.Utils;

public class InteractiveKeyPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel(new BorderLayout());

	public InteractiveKeyPanel(final CloudProvider provider)
	{
		super(new BorderLayout());

		JButton addButton = new JButton(Utils.icon("add22.png"));
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new NewKeyWizard((JDialog) Utils
						.findWindow(InteractiveKeyPanel.this), provider);
			}
		});

		JButton modifyButton = new JButton("Import Key");

		JButton removeButton = new JButton(Utils.icon("remove22.png"));
		removeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1.0;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;

		buttonPanel.add(addButton, c);

		c.gridx = 1;
		buttonPanel.add(modifyButton, c);

		c.gridx = 2;
		buttonPanel.add(removeButton, c);

		add(buttonPanel, BorderLayout.SOUTH);
	}
}
