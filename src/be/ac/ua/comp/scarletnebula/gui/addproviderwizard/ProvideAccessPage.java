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

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudProviderTemplate.AccessMethod;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ProvideAccessPage extends WizardPage {
	private static final long serialVersionUID = 1255938294405602870L;
	JTextField apiKey = new JTextField();
	JTextField apiSecret = new JTextField();

	public ProvideAccessPage(final AddProviderWizardDataRecorder rec) {
		setLayout(new BorderLayout());

		final String toptext;
		if (rec.getTemplate().getAccessMethod() == AccessMethod.KEY) {
			toptext = "Please enter the API Access Key that identifies your account and the API Secret that represents your password.";
		} else {
			toptext = "Enter the email address and password you used to register with "
					+ rec.getTemplate().getName() + ".";
		}

		// The text on top
		final BetterTextLabel txt = new BetterTextLabel(toptext);

		txt.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

		// And the textfields below
		final FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, max(50dlu;p):grow", "");

		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		final String loginLabel;
		final String passwordLabel;
		if (rec.getTemplate().getAccessMethod() == AccessMethod.KEY) {
			loginLabel = "API Key";
			passwordLabel = "API Secret";
		} else {
			loginLabel = "Email address";
			passwordLabel = "Password";
		}
		builder.append(loginLabel, apiKey);
		builder.nextLine();
		builder.append(passwordLabel, apiSecret);

		add(txt, BorderLayout.NORTH);
		add(builder.getPanel());
	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		final AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		rec.setApiKey(apiKey.getText().trim());
		rec.setApiSecret(apiSecret.getText().trim());

		return new ChooseNamePage(rec);
	}
}
