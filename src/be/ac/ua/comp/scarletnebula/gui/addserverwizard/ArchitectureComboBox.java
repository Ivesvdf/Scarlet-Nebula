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

import org.dasein.cloud.compute.Architecture;

public class ArchitectureComboBox extends JComboBox {
	private static final long serialVersionUID = 1L;
	private final static String S32BIT = "32 bit";
	private final static String S64BIT = "64 bit";

	ArchitectureComboBox() {
		super();

		addItem(S32BIT);
		addItem(S64BIT);
	}

	public Architecture getSelection() {
		final String itemString = (String) getSelectedItem();
		Architecture rvArchitecture;

		if (itemString.equals(S64BIT)) {
			rvArchitecture = Architecture.I64;
		} else {
			rvArchitecture = Architecture.I32;
		}

		return rvArchitecture;
	}
}
