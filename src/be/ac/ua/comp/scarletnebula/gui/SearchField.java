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

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.misc.Utils;

public class SearchField extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JTextField textfield;

	public SearchField(final JTextField inputfield) {
		super(new BorderLayout());
		textfield = inputfield;
		setOpaque(true);
		setBackground(Color.WHITE);
		textfield.setBackground(Color.WHITE);
		textfield.setBorder(null);
		final JLabel iconLabel = new JLabel(Utils.icon("search16.png"));
		iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		add(iconLabel, BorderLayout.WEST);
		add(textfield, BorderLayout.CENTER);
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
	}

	public JTextField getTextField() {
		return textfield;
	}
}
