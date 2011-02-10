package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.util.Collection;

import javax.swing.JDialog;

import be.ac.ua.comp.scarletnebula.gui.CloudProviderTemplate;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;

public class AddProviderWizard extends Wizard
{

	public AddProviderWizard(Collection<CloudProviderTemplate> templates)
	{
		super(new SelectProviderTemplatePage(),
				new AddProviderWizardDataRecorder(), new SimpleWizardTemplate());
	}

	public void startModal(JDialog parent)
	{
		startModal("Add a new Cloud Provider", 400, 300, parent);
	}
}
