package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizard;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.ProviderAddedListener;

public class ManageProvidersWindow extends JDialog
{
	private static final long serialVersionUID = 1L;

	ManageProvidersWindow(JFrame parent)
	{
		super(parent, "Manage Providers", true);

		setSize(400, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final DefaultListModel providerListModel = new DefaultListModel();
		final JList providerList = new JList(providerListModel);
		providerList.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		fillProviderList(providerListModel);
		providerList.setSelectedIndex(0);

		JScrollPane providerListScrollPane = new JScrollPane(providerList);
		providerListScrollPane.setBorder(BorderFactory.createEmptyBorder(20,
				20, 20, 20));

		setLayout(new BorderLayout());

		JButton addButton = new JButton(new ImageIcon(getClass().getResource(
				"/images/add22.png")));
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				AddProviderWizard wiz = new AddProviderWizard(CloudManager
						.get().getTemplates());

				wiz.addProviderAddedListener(new ProviderAddedListener()
				{
					@Override
					public void providerWasAdded(String name)
					{
						providerListModel.addElement(name);
					}
				});

				wiz.startModal(ManageProvidersWindow.this);
			}
		});

		JButton modifyButton = new JButton(new ImageIcon(getClass()
				.getResource("/images/modify22.png")));
		modifyButton.setEnabled(false);

		JButton removeButton = new JButton(new ImageIcon(getClass()
				.getResource("/images/remove22.png")));
		removeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int index = providerList.getSelectedIndex();

				// No selection
				if (index < 0)
					return;

				int answer = JOptionPane
						.showConfirmDialog(
								ManageProvidersWindow.this,
								"Removing a CloudProvider will unlink all servers registered with "
										+ "this CloudProvider (without terminating them!). \n"
										+ "It will also permanently delete all configuration "
										+ "information associated with this CloudProvider.\n "
										+ "\nDo you wish to proceed?");

				// Only proceed if the user clicked yes
				if (answer != JOptionPane.YES_OPTION)
				{
					return;
				}

				String provname = (String) providerListModel.get(index);
				CloudManager.get().deleteCloudProvider(provname);

				providerListModel.remove(index);
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		buttonPanel.add(addButton);
		buttonPanel.add(modifyButton);
		buttonPanel.add(removeButton);

		add(providerListScrollPane);
		add(buttonPanel, BorderLayout.SOUTH);
		setVisible(true);
	}

	private void fillProviderList(DefaultListModel providerListModel)
	{
		for (String name : CloudManager.get().getLinkedCloudProviderNames())
		{
			providerListModel.addElement(name);
		}
	}
}
