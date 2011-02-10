package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizard;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizardDataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;

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

		JScrollPane providerListScrollPane = new JScrollPane(providerList);
		providerListScrollPane.setBorder(BorderFactory.createEmptyBorder(20,
				20, 20, 20));

		setLayout(new BorderLayout());

		JButton addButton = new JButton("+");
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				AddProviderWizard wiz = new AddProviderWizard(CloudManager
						.get().getTemplates());

				wiz.addWizardListener(new WizardListener()
				{

					@Override
					public void onFinish(DataRecorder recorder)
					{
						AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;

						CloudManager.get().registerNewCloudProvider(
								rec.getName(),
								rec.getTemplate().getClassname(),
								rec.getEndpoint().getURL(), rec.getApiKey(),
								rec.getApiSecret());

						providerListModel.addElement(rec.getName());
					}

					@Override
					public void onCancel(DataRecorder recorder)
					{
						// Do nothing...
					}
				});

				wiz.startModal(ManageProvidersWindow.this);
			}
		});

		JButton modifyButton = new JButton("?");
		JButton removeButton = new JButton("-");

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
