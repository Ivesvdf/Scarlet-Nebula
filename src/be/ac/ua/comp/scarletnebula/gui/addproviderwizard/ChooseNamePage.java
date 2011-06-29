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
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ChooseNamePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	JTextField name = new JTextField();

	ChooseNamePage(final AddProviderWizardDataRecorder recorder) {
		setLayout(new BorderLayout());
		final BetterTextLabel text = new BetterTextLabel(
				"What name would you like to use to describe this account with "
						+ recorder.getTemplate().getName() + "?");
		text.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
		add(text, BorderLayout.NORTH);

		// And the textfields below
		final FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, max(50dlu;p):grow, 7dlu", "");

		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.append("Account Name", name);
		add(builder.getPanel());

		// Prefill the textfield with something useful

		name.setText(recorder.getTemplate().getShortName()
				+ (recorder.getEndpoint() != null ? " ("
						+ recorder.getEndpoint().getShortName() + ")" : ""));
		name.setPreferredSize(name.getMinimumSize());

	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		final AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		final String providerName = name.getText();

		if (providerName.length() == 0) {
			JOptionPane.showMessageDialog(this,
					"Please enter a name for this provider.", "Enter name",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		// Check to see if this name hasn't been taken
		for (final String aProviderName : CloudManager.get()
				.getLinkedCloudProviderNames()) {
			if (aProviderName.equals(providerName)) {
				JOptionPane
						.showMessageDialog(
								this,
								"The name you provided for this CloudProvider is already in use. Please choose another one.",
								"CloudProvider name in use",
								JOptionPane.ERROR_MESSAGE);
				return null;
			}

		}

		rec.setName(providerName);

		return new FinishPage(rec);
	}
}
