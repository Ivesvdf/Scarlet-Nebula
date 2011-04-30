package be.ac.ua.comp.scarletnebula.gui.newkeywizard;

import javax.swing.JDialog;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.keywizard.SelectNewKeynamePage;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;

public class NewKeyWizard extends Wizard
{

	public NewKeyWizard(JDialog parent, CloudProvider provider)
	{
		super(new SelectNewKeynamePage(provider), null,
				new SimpleWizardTemplate());

		startModal("Create new SSH key", 350, 250, parent);
	}

}
