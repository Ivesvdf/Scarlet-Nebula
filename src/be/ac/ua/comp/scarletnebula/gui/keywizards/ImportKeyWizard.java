package be.ac.ua.comp.scarletnebula.gui.keywizards;

import javax.swing.JDialog;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;

public class ImportKeyWizard extends Wizard {
	private final JDialog parent;

	public ImportKeyWizard(final JDialog parent, final CloudProvider provider) {
		super(new UseExistingKeyPage(provider), new KeyRecorder(),
				new SimpleWizardTemplate());
		this.parent = parent;

	}

	public void start() {
		startModal("Import an SSH key", 350, 250, parent);
	}
}
