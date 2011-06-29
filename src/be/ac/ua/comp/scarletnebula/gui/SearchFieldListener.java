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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class SearchFieldListener implements ActionListener, DocumentListener {
	private final JTextField searchField;
	ServerListModel serverListModel;

	public SearchFieldListener(final JTextField inputSearchField,
			final ServerListModel serverListModel) {
		this.searchField = inputSearchField;
		this.serverListModel = serverListModel;
	}

	// Search on ENTER press
	@Override
	public void actionPerformed(final ActionEvent e) {
		final String servername = searchField.getText();

		// listModel.insertElementAt(searchField.getText(), index);
		// If we just wanted to add to the end, we'd do this:
		serverListModel.filter(servername);

		// Reset the text field.
		// searchField.requestFocusInWindow();
		searchField.setText("");

	}

	// Required by DocumentListener.
	@Override
	public void removeUpdate(final DocumentEvent e) {
	}

	@Override
	public void insertUpdate(final DocumentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changedUpdate(final DocumentEvent e) {
		// TODO Auto-generated method stub

	}
}
