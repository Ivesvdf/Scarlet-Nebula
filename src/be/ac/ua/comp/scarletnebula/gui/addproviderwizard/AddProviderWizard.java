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

package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JDialog;
import javax.swing.JFrame;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.gui.keywizards.KeyWizard;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;

public class AddProviderWizard extends Wizard implements WizardListener {
	Collection<ProviderAddedListener> providerAddedListeners = new ArrayList<ProviderAddedListener>();

	public AddProviderWizard() {
		super(new SelectProviderTemplatePage(),
				new AddProviderWizardDataRecorder(), new SimpleWizardTemplate());

		addWizardListener(this);
	}

	public void startModal(final JDialog parent) {
		startModal("Add a new Cloud Provider", 400, 300, parent);
	}

	public void startModal(final JFrame parent) {
		startModal("Add a new Cloud Provider", 400, 300, parent);
	}

	public void addProviderAddedListener(final ProviderAddedListener pal) {
		providerAddedListeners.add(pal);
	}

	@Override
	public void onFinish(final DataRecorder recorder) {
		final AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;

		CloudManager.get().registerNewCloudProvider(rec.getName(),
				rec.getTemplate().getClassname(), // classname
				rec.getEndpoint() != null ? rec.getEndpoint().getURL() : "", // endpoint
				rec.getApiKey(), rec.getApiSecret());

		for (final ProviderAddedListener p : providerAddedListeners) {
			p.providerWasAdded(rec.getName());
		}

		if (CloudManager.get().getCloudProviderByName(rec.getName())
				.supportsSSHKeys()) {
			new KeyWizard(null, CloudManager.get().getCloudProviderByName(
					rec.getName()));
		}
	}

	@Override
	public void onCancel(final DataRecorder recorder) {
		// Do nothing...
	}
}
