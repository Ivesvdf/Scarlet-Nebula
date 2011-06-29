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

package be.ac.ua.comp.scarletnebula.gui.keywizards;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.ChooseImagePage;

public class FinalNewKeyPage extends AbstractFinalKeyWizardPage {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ChooseImagePage.class);

	private final CloudProvider provider;
	private final String keyname;

	public FinalNewKeyPage(final CloudProvider provider, final String keyname) {
		super(provider, "Click Finish to create a new SSH key named " + keyname
				+ " for provider " + provider.getName(), keyname);
		this.provider = provider;
		this.keyname = keyname;
	}

	@Override
	protected void performAction(final KeyRecorder recorder) {
		recorder.keyname = keyname;
		recorder.makeDefault = makeKeyDefault();

		(new SwingWorker<Exception, Object>() {
			@Override
			protected Exception doInBackground() throws Exception {
				try {
					provider.createKey(keyname, makeKeyDefault());
				} catch (final Exception e) {
					return e;
				}
				return null;
			}

			@Override
			public void done() {
				try {
					final Exception result = get();

					if (result != null) {
						log.error("Could not create key", result);
						JOptionPane
								.showMessageDialog(FinalNewKeyPage.this,
										result.getLocalizedMessage(),
										"Error creating key",
										JOptionPane.ERROR_MESSAGE);
					}
				} catch (final Exception ignore) {
				}
			}
		}).execute();
	}
}
