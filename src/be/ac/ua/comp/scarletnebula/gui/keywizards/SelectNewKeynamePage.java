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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.inputverifiers.PlainTextVerifier;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class SelectNewKeynamePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	private final CloudProvider provider;
	private final JTextField namefield = new JTextField();

	public SelectNewKeynamePage(final CloudProvider provider) {
		super(new BorderLayout());
		final BetterTextLabel toptext = new BetterTextLabel(
				"Choose a name for the SSH key you'll use to establish SSH connections to servers.");
		toptext.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		namefield
				.setInputVerifier(new PlainTextVerifier(
						namefield,
						"An SSH key name must be at least 1 character long and may only contain letters and digits."));
		final String username = System.getProperty("user.name");
		namefield.setText((username != null ? username : "default") + "key");

		final FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, min(50dlu;p):grow", "");
		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.append("Name", namefield);

		final JPanel namepanel = builder.getPanel();
		namepanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		add(namepanel, BorderLayout.CENTER);
		add(toptext, BorderLayout.NORTH);
		this.provider = provider;
	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		WizardPage finalPage;
		if (!namefield.getInputVerifier().verify(namefield)) {
			// do not proceed
			finalPage = null;
		} else {
			finalPage = new FinalNewKeyPage(provider, namefield.getText());
		}

		return finalPage;
	}

}
