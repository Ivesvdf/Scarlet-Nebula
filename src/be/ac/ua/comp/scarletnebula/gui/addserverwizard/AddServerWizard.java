package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import javax.swing.JDialog;
import javax.swing.JFrame;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.GUI;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class AddServerWizard extends JDialog implements WizardListener
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
				// TODO: handle zero cloudproviders
				// throw new Exception("No CloudProviders linked!");
				break;
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
		setVisible(true);
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
