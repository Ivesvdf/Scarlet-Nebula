package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizard;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.ProviderAddedListener;
import be.ac.ua.comp.scarletnebula.misc.Utils;

public class ManageProvidersWindow extends JDialog
{
	private static final long serialVersionUID = 1L;

	ManageProvidersWindow(final JFrame parent)
	{
		super(parent, "Manage Providers", true);

		setSize(400, 400);
		setLocationByPlatform(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final DefaultListModel providerListModel = new DefaultListModel();
		final JList providerList = new JList(providerListModel);
		providerList.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		fillProviderList(providerListModel);
		providerList.setSelectedIndex(0);

		final JScrollPane providerListScrollPane = new JScrollPane(providerList);
		providerListScrollPane.setBorder(BorderFactory.createEmptyBorder(20,
				20, 20, 20));

		setLayout(new BorderLayout());

		final JButton addButton = new JButton(Utils.icon("add22.png"));
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final AddProviderWizard wiz = new AddProviderWizard();

				wiz.addProviderAddedListener(new ProviderAddedListener()
				{
					@Override
					public void providerWasAdded(final String name)
					{
						providerListModel.addElement(name);
					}
				});

				wiz.startModal(ManageProvidersWindow.this);
			}
		});

		final JButton modifyButton = new JButton(Utils.icon("modify22.png"));
		modifyButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final int index = providerList.getSelectedIndex();

				if (index < 0)
				{
					return;
				}

				final String provname = (String) providerListModel.get(index);
				final CloudProvider provider = CloudManager.get()
						.getCloudProviderByName(provname);

				new ProviderPropertiesWindow(ManageProvidersWindow.this,
						provider);
			}
		});

		final JButton removeButton = new JButton(Utils.icon("remove22.png"));
		removeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final int index = providerList.getSelectedIndex();

				// No selection
				if (index < 0)
				{
					return;
				}

				final int answer = JOptionPane
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

				final String provname = (String) providerListModel.get(index);
				providerListModel.remove(index);
				CloudManager.get().deleteCloudProvider(provname);
			}
		});

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		buttonPanel.add(addButton);
		buttonPanel.add(modifyButton);
		buttonPanel.add(removeButton);

		add(providerListScrollPane);
		add(buttonPanel, BorderLayout.SOUTH);
		setVisible(true);
	}

	private void fillProviderList(final DefaultListModel providerListModel)
	{
		for (final String name : CloudManager.get()
				.getLinkedCloudProviderNames())
		{
			providerListModel.addElement(name);
		}
	}
}
