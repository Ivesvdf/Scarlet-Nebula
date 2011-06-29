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

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.BorderFactory;

import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.TaggingPanel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class TaggingPage extends WizardPage {
	private static final long serialVersionUID = 1L;
	TaggingPanel taggingPanel = new TaggingPanel();

	TaggingPage() {
		setLayout(new BorderLayout());

		final BetterTextLabel lbl = new BetterTextLabel(
				"Enter some labels that describe the functionality of this server. E.g. dns, webserver, ...");
		lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(lbl, BorderLayout.NORTH);
		taggingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		add(taggingPanel, BorderLayout.CENTER);

	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		// Extract tags
		taggingPanel.simulateEnter();
		final Collection<String> tags = taggingPanel.getTags();
		final AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;
		rec.tags = tags;
		return new FinalServerAddPage(rec);
	}

}
