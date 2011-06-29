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

package be.ac.ua.comp.scarletnebula.gui.inputverifiers;

import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTextField;

public final class PortRangeInputVerifier extends LoudInputVerifier {
	public PortRangeInputVerifier(final JTextField textField) {
		super(textField,
				"This field must contain single port like 22 or a range e.g. 100-110.");
	}

	@Override
	public boolean verify(final JComponent input) {
		final String text = ((JTextField) input).getText();
		boolean good = true;
		if (Pattern.matches("[0-9]+|[0-9]+-[0-9]+", text)) {
			final String parts[] = text.split("-");

			for (final String part : parts) {
				final int port = Integer.decode(part);
				if (port > 65536) {
					good = false;
					break;
				}
			}
		} else {
			good = false;
		}

		return good;
	}
}