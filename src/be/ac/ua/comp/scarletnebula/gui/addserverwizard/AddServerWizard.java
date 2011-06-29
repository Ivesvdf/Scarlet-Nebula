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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizard;
import be.ac.ua.comp.scarletnebula.gui.windows.GUI;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class AddServerWizard implements WizardListener {
	public static Log log = LogFactory.getLog(Server.class);
	private static final long serialVersionUID = 1L;
	public JFrame parent;
	private GUI gui;

	public AddServerWizard(final JFrame parent, final GUI gui) {
		this.parent = parent;
		this.gui = gui;

		// Only show the choose provider page if more than one provider is
		// available.
		final AddServerWizardDataRecorder rec = new AddServerWizardDataRecorder();

		WizardPage firstPage = null;

		switch (CloudManager.get().getLinkedCloudProviderNames().size())
		{
			case 0:
				JOptionPane
						.showMessageDialog(
								parent,
								"Please add a new CloudProvider before starting new servers.",
								"First add Provider", JOptionPane.ERROR_MESSAGE);
				final AddProviderWizard wiz = new AddProviderWizard();
				wiz.startModal(parent);
				return;
			case 1: // One provider -- user has to pick this one so skip the
					// page
				final CloudProvider prov = (CloudProvider) CloudManager.get()
						.getLinkedCloudProviders().toArray()[0];
				rec.provider = prov;
				firstPage = new ChooseImagePage(prov);
				break;
			default:
				firstPage = new ChooseProviderPage();
				break;
		}
		final Wizard wiz = new Wizard(firstPage, rec,
				new SimpleWizardTemplate());
		wiz.addWizardListener(this);
		wiz.startModal("Start new server", 500, 400, parent);
	}

	@Override
	public void onFinish(final DataRecorder recorder) {
		gui.startServer(this, recorder);
	}

	@Override
	public void onCancel(final DataRecorder recorder) {
	}
}
