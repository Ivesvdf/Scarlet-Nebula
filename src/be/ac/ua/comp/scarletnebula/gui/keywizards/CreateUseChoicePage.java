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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class CreateUseChoicePage extends WizardPage {
	private static final long serialVersionUID = 1L;

	final JRadioButton createButton = new JRadioButton("Create a new key");
	final JRadioButton useButton = new JRadioButton("Use an existing key");
	ButtonGroup group = new ButtonGroup();
	CloudProvider provider;

	CreateUseChoicePage(final CloudProvider provider) {
		setLayout(new BorderLayout());
		this.provider = provider;
		// The text on top
		final BetterTextLabel txt = new BetterTextLabel(
				"After starting new servers, you'll connect to these servers through an SSH connection. "
						+ "Before you can securely connect to a server, you need an SSH key. \n\n"
						+ "Would you like to create a new key or use an existing key?");

		txt.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
		add(txt, BorderLayout.PAGE_START);

		group.add(createButton);
		group.add(useButton);
		group.setSelected(createButton.getModel(), true);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.add(createButton);
		buttonPanel.add(Box.createVerticalStrut(10));
		buttonPanel.add(useButton);
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		add(buttonPanel, BorderLayout.CENTER);
	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		if (group.getSelection() == createButton.getModel()) {
			return new SelectNewKeynamePage(provider);
		} else {
			return new UseExistingKeyPage(provider);
		}

	}

}
