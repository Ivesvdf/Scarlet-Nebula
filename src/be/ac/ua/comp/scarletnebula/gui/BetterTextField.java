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

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.misc.Colors;

/**
 * A derivative of a JTextField that provides extra functionality such as easy
 * placeholders.
 * 
 * @author ives
 */
public class BetterTextField extends JTextField {
	private static final long serialVersionUID = 1L;
	private boolean originalPlaceholderText = true;
	private boolean placeholderDisplayed = false;
	private static Log log = LogFactory.getLog(BetterTextField.class);

	/**
	 * @see JTextField
	 */
	public BetterTextField(final int columns) {
		super(columns);
	}

	/**
	 * @see JTextField
	 */
	public BetterTextField() {
		super();
	}

	/**
	 * Enables the JTextField to have a placeholder and sets the initial text to
	 * placeholder. Call this somewhere before showing the textfield.
	 * 
	 * Concretly this placeholder will take the shape of gray text that will
	 * appear when the field contains no text and is not focussed and that will
	 * disappear when the field receives focus.
	 * 
	 * @param placeholder
	 */
	public void setPlaceHolder(final String placeholder) {
		setText(placeholder);
		final Color originalTextColor = getForeground();
		setForeground(Colors.Gray.color());
		placeholderDisplayed = true;

		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent e) {
				if (getText().length() == 0) {
					setForeground(Colors.Gray.alpha(1.0f));
					setText(placeholder);
					placeholderDisplayed = true;
					originalPlaceholderText = true;
				}
			}

			@Override
			public void focusGained(final FocusEvent e) {
				log.info("Focus gained");
				if (originalPlaceholderText) {
					log.info("Original text");
					originalPlaceholderText = false;
					setForeground(originalTextColor);
					setText("");
					placeholderDisplayed = false;
				}
			}
		});
	}

	/**
	 * @return The field's content if there is any, the empty string if the
	 *         BetterTextField has a placeholder displayed.
	 */
	@Override
	public String getText() {
		String result = null;

		if (placeholderDisplayed) {
			result = "";
		} else {
			result = super.getText();
		}

		return result;
	}
}
