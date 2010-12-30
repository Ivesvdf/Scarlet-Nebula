package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

class ChooseProviderPage extends WizardPage
{
	private static final long serialVersionUID = 1L;
	final JComboBox providerList = new JComboBox(CloudManager.get()
			.getLinkedCloudProviderNames().toArray());

	ChooseProviderPage()
	{

		// Create the combo box, select item at index 4.
		// Indices start at 0, so 4 specifies the pig.
		providerList.setName("provider");
		providerList.setSelectedIndex(0);

		add(new JLabel("Choose a Cloud Provider"));
		add(providerList);
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;

		String providername = (String) providerList.getSelectedItem();
		CloudProvider provider = CloudManager.get().getCloudProviderByName(
				providername);

		rec.provider = provider;

		return new InstanceInformationPage(provider);
	};
};