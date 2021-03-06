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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class NotPromptingJschUserInfo implements UserInfo,
		UIKeyboardInteractive {
	@Override
	public boolean promptYesNo(final String str) {
		return true;

		/*
		 * Object[] options = { "yes", "no" }; int foo =
		 * JOptionPane.showOptionDialog(null, str, "Warning",
		 * JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
		 * options, options[0]); return foo == 0;
		 */
	}

	String passwd = null;
	String passphrase = null;
	JTextField pword = new JPasswordField(20);

	@Override
	public String getPassword() {
		return passwd;
	}

	@Override
	public String getPassphrase() {
		return passphrase;
	}

	@Override
	public boolean promptPassword(final String message) {
		final Object[] ob = { pword };
		final int result = JOptionPane.showConfirmDialog(null, ob, message,
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			passwd = pword.getText();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean promptPassphrase(final String message) {
		return true;
	}

	@Override
	public void showMessage(final String message) {
		JOptionPane.showMessageDialog(null, message);
	}

	final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1,
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(
					0, 0, 0, 0), 0, 0);
	private Container panel;

	@Override
	public String[] promptKeyboardInteractive(final String destination,
			final String name, final String instruction, final String[] prompt,
			final boolean[] echo) {
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridx = 0;
		panel.add(new JLabel(instruction), gbc);
		gbc.gridy++;

		gbc.gridwidth = GridBagConstraints.RELATIVE;

		final JTextField[] texts = new JTextField[prompt.length];
		for (int i = 0; i < prompt.length; i++) {
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridx = 0;
			gbc.weightx = 1;
			panel.add(new JLabel(prompt[i]), gbc);

			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weighty = 1;
			if (echo[i]) {
				texts[i] = new JTextField(20);
			} else {
				texts[i] = new JPasswordField(20);
			}
			panel.add(texts[i], gbc);
			gbc.gridy++;
		}

		if (JOptionPane.showConfirmDialog(null, panel, destination + ": "
				+ name, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
			final String[] response = new String[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				response[i] = texts[i].getText();
			}
			return response;
		} else {
			return null; // cancel
		}
	}

}
