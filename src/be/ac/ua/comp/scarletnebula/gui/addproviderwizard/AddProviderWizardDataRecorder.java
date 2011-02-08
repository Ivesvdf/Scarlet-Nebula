package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import org.dasein.cloud.CloudProvider;

import be.ac.ua.comp.scarletnebula.gui.CloudProviderTemplate;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;

public class AddProviderWizardDataRecorder implements DataRecorder
{
	CloudProviderTemplate template;
	CloudProviderTemplate.Endpoint endpoint;
}
