package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

class ChooseProviderPage extends WizardPage
{
	private static final long serialVersionUID = 1L;
	final JList providerList = new JList(CloudManager.get()
			.getLinkedCloudProviderNames().toArray());

	ChooseProviderPage()
	{
		// Create the combo box, select item at index 4.
		// Indices start at 0, so 4 specifies the pig.
		providerList.setName("provider");
		providerList.setSelectedIndex(0);
		providerList.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		final JScrollPane providerListScrollPane = new JScrollPane(providerList);
		providerListScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20,
				10, 20));

		final BetterTextLabel toptext = new BetterTextLabel(
				"Select the Cloud Provider in whose cloud you'd like to start this server.");
		toptext.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new BorderLayout());
		add(toptext, BorderLayout.NORTH);
		add(providerListScrollPane, BorderLayout.CENTER);
	}

	@Override
	public WizardPage next(final DataRecorder recorder)
	{
		final AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;

		final String providername = (String) providerList.getSelectedValue();
		final CloudProvider provider = CloudManager.get()
				.getCloudProviderByName(providername);

		rec.provider = provider;

		return new InstanceInformationPage(provider);// InstanceInformationPage(provider);
	};
};