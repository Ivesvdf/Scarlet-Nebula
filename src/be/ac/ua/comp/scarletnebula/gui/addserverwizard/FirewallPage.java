package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.InteractiveFirewallPanel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class FirewallPage extends WizardPage
{
	private static final long serialVersionUID = 1L;

	public FirewallPage(final CloudProvider provider)
	{
		super(new BorderLayout());
		add(new InteractiveFirewallPanel(provider), BorderLayout.CENTER);
	}

	@Override
	public WizardPage next(final DataRecorder recorder)
	{
		// TODO Auto-generated method stub
		return new TaggingPage();
	}

}
