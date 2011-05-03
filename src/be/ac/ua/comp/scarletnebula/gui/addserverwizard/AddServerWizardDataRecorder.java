package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.util.ArrayList;
import java.util.Collection;

import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.VirtualMachineProduct;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;

public class AddServerWizardDataRecorder implements DataRecorder
{
	public CloudProvider provider = null;
	public String instanceName = "";
	public VirtualMachineProduct instanceSize = null;
	public MachineImage image = null;
	public Collection<String> tags = new ArrayList<String>();
	public String keypairOrPassword = "";
	public Collection<String> firewallIds = null;
}
