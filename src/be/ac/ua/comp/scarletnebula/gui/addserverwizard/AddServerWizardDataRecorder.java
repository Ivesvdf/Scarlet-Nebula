package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;

public class AddServerWizardDataRecorder implements DataRecorder
{
	public CloudProvider provider;
	public String instanceName;
	public String instanceSize;
}
