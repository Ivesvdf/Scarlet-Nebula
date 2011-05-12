package be.ac.ua.comp.scarletnebula.gui.keywizards;

import java.awt.Frame;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;

public class KeyWizard extends Wizard {
	public KeyWizard(final Frame parent, final CloudProvider provider) {
		super(new CreateUseChoicePage(provider), new KeyRecorder(),
				new SimpleWizardTemplate());

		startModal("Manage SSH Keys", 400, 310, parent);
	}
}
