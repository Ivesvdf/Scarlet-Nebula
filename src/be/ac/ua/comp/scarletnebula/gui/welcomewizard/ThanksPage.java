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

package be.ac.ua.comp.scarletnebula.gui.welcomewizard;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class ThanksPage extends WizardPage {
	private static final long serialVersionUID = 1L;

	ThanksPage() {
		setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		final JLabel thanks = new JLabel(
				"<html><font size=\"+3\" color=blue>Thanks!</font></html>");
		thanks.setAlignmentX(LEFT_ALIGNMENT);
		thanks.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		c.gridx = 0;
		c.gridy = 0;
		add(thanks, c);
		final JLabel txt = new JLabel(
				"<html>... for trying Scarlet Nebula. "
						+ "<br /><br />"
						+ "Before you can start managing servers, a new CloudProvider account needs to be added to Scarlet Nebula. "
						+ "This CloudProvider account will allow Scarlet Nebula to talk to a cloud."
						+ "<br /><br />"
						+ "Press the Next button to create a new CloudProvider account.</html>");
		txt.setFont(new Font("Dialog", Font.PLAIN, txt.getFont().getSize()));
		txt.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
		txt.setAlignmentX(LEFT_ALIGNMENT);

		c.gridx = 0;
		c.gridy = 1;
		add(txt, c);

	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		return new CreateCloudProviderPage();
	}

}
