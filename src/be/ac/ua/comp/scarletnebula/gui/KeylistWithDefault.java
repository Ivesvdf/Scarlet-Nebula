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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.KeyManager;

public class KeylistWithDefault extends JTable {
	private static final long serialVersionUID = 1L;
	private final ButtonGroup radioButtonGroup;
	private final CloudProvider provider;
	private final DefaultTableModel datamodel = new DefaultTableModel();

	class RadioButtonRenderer implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(final JTable table,
				final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			if (value == null) {
				return null;
			}

			final JRadioButton radioButton = (JRadioButton) value;
			radioButton.setOpaque(true);

			if (isSelected) {
				radioButton.setBackground(selectionBackground);
				radioButton.setForeground(selectionForeground);
			} else {
				radioButton.setForeground(Color.black);

				// Fix for the row background colors.
				if (row % 2 == 1) {
					radioButton.setBackground(Color.WHITE);
				} else {
					radioButton.setBackground(table.getBackground());
				}
			}
			return (Component) value;
		}
	}

	class RadioButtonEditor extends DefaultCellEditor implements ItemListener {
		private static final long serialVersionUID = 1L;
		private JRadioButton button;

		public RadioButtonEditor(final JCheckBox checkBox) {
			super(checkBox);
		}

		@Override
		public Component getTableCellEditorComponent(final JTable table,
				final Object value, final boolean isSelected, final int row,
				final int column) {
			if (value == null) {
				return null;
			}
			button = (JRadioButton) value;
			button.addItemListener(this);
			return (Component) value;
		}

		@Override
		public Object getCellEditorValue() {
			button.removeItemListener(this);
			return button;
		}

		@Override
		public void itemStateChanged(final ItemEvent e) {
			super.fireEditingStopped();
		}
	}

	public KeylistWithDefault(final CloudProvider provider) {
		this.provider = provider;

		datamodel.addColumn("Default");
		datamodel.addColumn("Name");

		radioButtonGroup = new ButtonGroup();

		final String defaultKey = provider.getDefaultKeypair();
		final Collection<String> keys = KeyManager.getKeyNames(provider
				.getName());
		for (final String keyname : keys) {
			add(keyname, keyname.equals(defaultKey));
		}

		setModel(datamodel);

		getColumn("Default").setCellRenderer(new RadioButtonRenderer());
		getColumn("Default").setCellEditor(
				new RadioButtonEditor(new JCheckBox()));
		getColumn("Default").setMaxWidth(65);

		setCellSelectionEnabled(false);
		setRowSelectionAllowed(true);

		if (!keys.isEmpty()) {
			setRowSelectionInterval(0, 0);
		}
	}

	public void add(final String keyname, final boolean defaultKey) {
		final JRadioButton radioButton = new JRadioButton();

		radioButtonGroup.add(radioButton);

		// The default should obviously be checked...
		if (defaultKey) {
			radioButton.setSelected(true);
		}

		// As soon as the user changes the default, it immediately changes
		// on the provider's side.
		radioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				provider.setDefaultKeypair(keyname);
				provider.store();
				repaint(); // Slight hack, acts really weirdly otherwise...
			}
		});
		datamodel.addRow(new Object[] { radioButton, keyname });
	}

	/**
	 * @return A collection containing all selected keynames.
	 */
	public Collection<String> getSelection() {
		final int[] selectedRows = getSelectedRows();

		final Collection<String> keynames = new ArrayList<String>();
		for (final int rowIndex : selectedRows) {
			keynames.add((String) datamodel.getValueAt(rowIndex, 1));
		}
		return keynames;
	}

	/**
	 * Removes a key from the table
	 * 
	 * @param key
	 *            The key to remove
	 */
	public void removeKey(final String key) {
		for (int row = 0; row < datamodel.getRowCount(); row++) {
			if (datamodel.getValueAt(row, 1).equals(key)) {
				datamodel.removeRow(row);
			}
		}
	}

	/**
	 * Checks to see if there is a default key present. If no such key is
	 * present, the first one in the list is made default.
	 */
	public void assureDefaultKey() {
		boolean noneSelected = true;

		for (int row = 0; row < datamodel.getRowCount(); row++) {
			if (((JRadioButton) datamodel.getValueAt(row, 0)).isSelected()) {
				noneSelected = false;
			}
		}

		// If no keys are default, make the first one in the list default...
		if (noneSelected && datamodel.getRowCount() > 0) {
			((JRadioButton) datamodel.getValueAt(0, 0)).setSelected(true);
		}
	}
}
