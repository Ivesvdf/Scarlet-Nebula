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

import javax.swing.JComboBox;

import org.dasein.cloud.compute.Platform;

public class PlatformComboBox extends JComboBox {
	private static final long serialVersionUID = 1L;

	PlatformComboBox() {
		super();

		for (final Platform platform : org.dasein.cloud.compute.Platform
				.values()) {
			addItem(toProperCase(platform.name().replace('_', ' ')).replace(
					"Bsd", "BSD").replace("Os", "OS"));
		}
	}

	Platform getSelection() {
		String itemname = (String) getSelectedItem();
		itemname = itemname.replace(' ', '_').toUpperCase();

		return org.dasein.cloud.compute.Platform.valueOf(itemname);
	}

	public static String toProperCase(String input) {
		input = input.toLowerCase();
		String result = "";
		for (int i = 0; i < input.length(); i++) {
			final String next = input.substring(i, i + 1);
			if (i == 0 || (i > 0 && input.substring(i - 1, i).equals(" "))) {
				result += next.toUpperCase();
			} else {
				result += next;
			}
		}
		return result;
	}
}
