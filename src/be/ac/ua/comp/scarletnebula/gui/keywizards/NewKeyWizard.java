package be.ac.ua.comp.scarletnebula.gui.keywizards;

import javax.swing.JDialog;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;

public class NewKeyWizard extends Wizard
{
	private final JDialog parent;

	public NewKeyWizard(final JDialog parent, final CloudProvider provider)
	{
		super(new SelectNewKeynamePage(provider), new KeyRecorder(),
				new SimpleWizardTemplate());
		this.parent = parent;
	}

	public void start()
	{
		startModal("Create new SSH key", 350, 250, parent);
	}

}
