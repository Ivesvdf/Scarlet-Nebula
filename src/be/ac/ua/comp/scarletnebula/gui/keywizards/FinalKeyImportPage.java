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

import java.awt.BorderLayout;
import java.io.File;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;

public class FinalKeyImportPage extends AbstractFinalKeyWizardPage {
	private static final long serialVersionUID = 1L;
	private final CloudProvider provider;
	private final String keyname;
	private final File keyFile;

	public FinalKeyImportPage(final CloudProvider provider,
			final String keyname, final File keyFile) {
		super(provider, "Click Finish to import the key" + keyname, "");
		this.provider = provider;
		this.keyname = keyname;
		this.keyFile = keyFile;
		setLayout(new BorderLayout());
		add(new BetterTextLabel("Press finish to create a new SSH key named "
				+ keyname + " from file " + keyFile.getName() + "."),
				BorderLayout.NORTH);

	}

	@Override
	protected void performAction(final KeyRecorder recorder) {
		recorder.keyname = keyname;
		recorder.makeDefault = makeKeyDefault();

		provider.importKey(keyname, keyFile, makeKeyDefault());
	}

}
