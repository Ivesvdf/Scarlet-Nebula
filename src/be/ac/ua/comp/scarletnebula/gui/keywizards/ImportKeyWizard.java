package be.ac.ua.comp.scarletnebula.gui.keywizards;

import javax.swing.JDialog;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;

public class ImportKeyWizard extends Wizard
{
	private JDialog parent;

	public ImportKeyWizard(JDialog parent, CloudProvider provider)
	{
		super(new UseExistingKeyPage(provider), null,
				new SimpleWizardTemplate());
		this.parent = parent;

	}

	public void start()
	{
		startModal("Import an SSH key", 350, 250, parent);
	}
}
