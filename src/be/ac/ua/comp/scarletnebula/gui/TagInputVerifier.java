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

import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.gui.inputverifiers.LoudInputVerifier;

public class TagInputVerifier extends LoudInputVerifier {

	public TagInputVerifier(final JTextField textField) {
		super(textField,
				"Only letters, numbers and spaces are allowed in a tag.");
	}

	@Override
	public boolean verify(final JComponent input) {
		return Pattern.matches("^[a-zA-Z0-9 :]*",
				((JTextField) input).getText());
	}

}
