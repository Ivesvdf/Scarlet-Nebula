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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class FinishPage extends WizardPage {
	private static final long serialVersionUID = 1L;

	public FinishPage(final AddProviderWizardDataRecorder rec) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		String txt = "<html>Press the Finish button to create the CloudProvider "
				+ rec.getName()
				+ ".\n\nThis CloudProvider connects to "
				+ rec.getTemplate().getName()
				+ (rec.getEndpoint() != null ? " on its "
						+ rec.getEndpoint().getName() + " endpoint." : ".");

		final CloudProvider tmpProvider = new CloudProvider(rec.getName(), // name
				rec.getTemplate().getClassname(), // classname
				rec.getEndpoint() != null ? rec.getEndpoint().getURL() : "", // endpoint
				rec.getApiKey(), // api key
				rec.getApiSecret(), // api secret
				""); // default keypair

		final boolean credentialsOk = tmpProvider.test();

		if (credentialsOk) {
			txt += "<br/><br/><font color=\"green\"><b>Succesfully connected to this CloudProvider. </font></html>";
		} else {
			txt += "<br/><br/><font color=\"red\"><b>Warning!</b> Could not connect to this CloudProvider! "
					+ "Continue at your own risk or press the Previous button to try again. </font></html>";
		}
		final BetterTextLabel toptext = new BetterTextLabel(txt);

		toptext.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(toptext);
	}

	@Override
	public boolean nextIsEnabled() {
		return false;
	}

	@Override
	public boolean finishIsEnabled() {
		return true;
	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		return null;
	}
}
