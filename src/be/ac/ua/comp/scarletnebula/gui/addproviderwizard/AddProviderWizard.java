package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.util.Collection;

import be.ac.ua.comp.scarletnebula.gui.CloudProviderTemplate;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;

public class AddProviderWizard extends Wizard implements WizardListener
{

	public AddProviderWizard(Collection<CloudProviderTemplate> templates)
	{
		super(new SelectProviderTemplatePage(),
				new AddProviderWizardDataRecorder(), new SimpleWizardTemplate());
		start("Add a new Cloud Provider", 400, 300, null);
		addWizardListener(this);
	}

	@Override
	public void onFinish(DataRecorder recorder)
	{

	}

	@Override
	public void onCancel(DataRecorder recorder)
	{

	}

}
