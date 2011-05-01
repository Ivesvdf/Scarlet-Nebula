package be.ac.ua.comp.scarletnebula.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class SearchFieldListener implements ActionListener, DocumentListener
{
	private final JTextField searchField;
	ServerListModel serverListModel;

	public SearchFieldListener(final JTextField inputSearchField,
			final ServerListModel serverListModel)
	{
		this.searchField = inputSearchField;
		this.serverListModel = serverListModel;
	}

	// Search on ENTER press
	@Override
	public void actionPerformed(final ActionEvent e)
	{
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
	public void removeUpdate(final DocumentEvent e)
	{
	}

	@Override
	public void insertUpdate(final DocumentEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void changedUpdate(final DocumentEvent e)
	{
		// TODO Auto-generated method stub

	}
}
