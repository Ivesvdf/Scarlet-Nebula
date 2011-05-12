package be.ac.ua.comp.scarletnebula.gui.welcomewizard;

import java.awt.Frame;

import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;

public class WelcomeWizard extends Wizard {

	public WelcomeWizard(final Frame parent) {
		super(new ThanksPage(), null, new SimpleWizardTemplate());

		startModal("Thanks for trying Scarlet Nebula", 400, 310, parent);
	}

}
