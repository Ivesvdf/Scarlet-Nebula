/*
 * Copyright (C) 2011  Ives van der Flaas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.util.ArrayList;
import java.util.Collection;

import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.VirtualMachineProduct;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;

public class AddServerWizardDataRecorder implements DataRecorder {
	public CloudProvider provider = null;
	public String instanceName = "";
	public VirtualMachineProduct instanceSize = null;
	public MachineImage image = null;
	public Collection<String> tags = new ArrayList<String>();
	public String keypairOrPassword = "";
	public Collection<String> firewallIds = null;
	public Integer instanceCount = 1;
}
