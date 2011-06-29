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
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProviderTemplate;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class SelectProviderTemplatePage extends WizardPage {
	private static final long serialVersionUID = 9100766686174798829L;

	JList providerlist = null;

	SelectProviderTemplatePage() {
		final Collection<String> names = new ArrayList<String>();
		for (final CloudProviderTemplate t : CloudManager.get().getTemplates()) {
			names.add(t.getName());
		}
		providerlist = new JList(names.toArray());
		providerlist.setSelectedIndex(0);
		providerlist.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		final JScrollPane scrollPane = new JScrollPane(providerlist);
		scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));

		setLayout(new BorderLayout());
		add(scrollPane);
	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		final String name = (String) providerlist.getSelectedValue();
		CloudProviderTemplate template = null;

		// Retreive the classname from name
		for (final CloudProviderTemplate t : CloudManager.get().getTemplates()) {
			if (t.getName().equals(name)) {
				template = t;
			}
		}

		final AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		rec.setTemplate(template);

		if (template.getEndPoints().isEmpty()) {
			return new ProvideAccessPage(rec);
		} else {
			return new SelectEndpointPage(template);
		}
	}
}
