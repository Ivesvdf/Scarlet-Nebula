package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class AcceptKeyHandlerPage extends WizardPage
{
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
		return new ChooseNamePage(rec);
	}

}
