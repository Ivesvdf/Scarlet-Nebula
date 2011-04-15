package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.GUI;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizard;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class AddServerWizard implements WizardListener
{
	private final GUI gui;
	private static final long serialVersionUID = 1L;

	public AddServerWizard(JFrame parent, final GUI gui)
	{
		this.gui = gui;

		// Only show the choose provider page if more than one provider is
		// available.
		AddServerWizardDataRecorder rec = new AddServerWizardDataRecorder();

		WizardPage firstPage = null;

		switch (CloudManager.get().getLinkedCloudProviderNames().size())
		{
			case 0:
				JOptionPane
						.showMessageDialog(
								parent,
								"Please add a new CloudProvider before starting new servers.",
								"First add Provider", JOptionPane.ERROR_MESSAGE);
				AddProviderWizard wiz = new AddProviderWizard();
				wiz.startModal(parent);
				return;
			case 1: // One provider -- user has to pick this one so skip the
					// page
				CloudProvider prov = (CloudProvider) CloudManager.get()
						.getLinkedCloudProviders().toArray()[0];
				rec.provider = prov;
				firstPage = new InstanceInformationPage(prov);
				break;
			default:
				firstPage = new ChooseProviderPage();
				break;
		}
		Wizard wiz = new Wizard(firstPage, rec, new SimpleWizardTemplate());
		wiz.addWizardListener(this);
		wiz.startModal("Start new server", 400, 300, gui);
	}

	@Override
	public void onFinish(DataRecorder recorder)
	{
		gui.addServerWizardClosed((AddServerWizardDataRecorder) recorder);
	}

	@Override
	public void onCancel(DataRecorder recorder)
	{
		// TODO Auto-generated method stub

	}
}
