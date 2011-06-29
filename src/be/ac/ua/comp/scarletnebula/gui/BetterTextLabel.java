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

package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Font;

import javax.swing.JLabel;

public class BetterTextLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public BetterTextLabel(String label) {
		super("");

		final boolean isAlreayHTML = (label.indexOf("<html>") == 0);

		if (!isAlreayHTML) {
			label = "<html>" + label.replace("\n", "<br />") + "</html>";
		}

		setText(label);
		setFont(new Font("Dialog", Font.PLAIN, getFont().getSize()));
	}
}
