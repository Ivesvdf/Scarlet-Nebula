package be.ac.ua.comp.scarletnebula.gui.keywizard;

import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizardDataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class AcceptKeyHandlerPage extends WizardPage
{

	private static final long serialVersionUID = 1L;

	enum Action
	{
		CREATE, USEEXISTING
	};

	AcceptKeyHandlerPage(Action action)
	{
		if (action == Action.CREATE)
		{
			// new CreateKeyWizard(provider);
		}
		else
		{
			// new
		}
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		return null;
	}

}
