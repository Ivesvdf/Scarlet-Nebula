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

package be.ac.ua.comp.scarletnebula.wizard;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class SimpleWizardTemplate extends WizardTemplate {
	@Override
	void setupWindow(final JDialog window) {
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(previousButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(nextButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(finishButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(cancelButton);

		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		window.setLayout(new BorderLayout());

		container.setLayout(new BorderLayout());

		window.add(container, BorderLayout.CENTER);
		window.add(buttonPanel, BorderLayout.SOUTH);
	}

}
