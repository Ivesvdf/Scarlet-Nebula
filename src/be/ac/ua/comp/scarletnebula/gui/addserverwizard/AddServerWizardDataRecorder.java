package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.util.Collection;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;

public class AddServerWizardDataRecorder implements DataRecorder
{
	public CloudProvider provider;
	public String instanceName;
	public String instanceSize;
	public String image;
	public Collection<String> tags;
}
