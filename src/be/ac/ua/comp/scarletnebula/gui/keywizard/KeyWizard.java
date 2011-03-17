package be.ac.ua.comp.scarletnebula.gui.keywizard;

import java.awt.Frame;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;

public class KeyWizard extends Wizard
{
	public KeyWizard(Frame parent, CloudProvider provider)
	{
		super(new CreateUseChoicePage(), null, new SimpleWizardTemplate());

		startModal("Thanks for trying Scarlet Nebula", 400, 310, parent);
	}
}
