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

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.KeyManager;

public class SelectKeyList extends JList {
	private static final long serialVersionUID = 1L;
	DefaultListModel model;
	CloudProvider provider;

	public SelectKeyList(final CloudProvider provider) {
		super(new DefaultListModel());
		model = (DefaultListModel) getModel();
		this.provider = provider;

		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setCellRenderer(new LabelCellRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}

	public void add(final String keyname) {
		model.addElement(new JLabel(keyname, new ImageIcon(getClass()
				.getResource("/images/key16.png")), SwingConstants.LEFT));
	}

	public String getSelectedKey() {
		final int selection = getSelectedIndex();

		if (selection < 0) {
			return null;
		}

		final JLabel label = (JLabel) model.get(selection);
		return label.getText();
	}

	class LabelCellRenderer implements ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(final JList list,
				final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {

			final JLabel renderer = (JLabel) value;
			renderer.setOpaque(true);

			Color foreground;
			Color background;

			if (isSelected) {
				background = UIManager.getColor("Tree.selectionBackground");
				foreground = UIManager.getColor("Tree.selectionForeground");
			} else {
				foreground = Color.BLACK;
				background = Color.WHITE;
			}
			renderer.setBackground(background);
			renderer.setForeground(foreground);
			return renderer;
		}
	}

	public void fillWithKnownKeys() {
		for (final String keyname : KeyManager.getKeyNames(provider.getName())) {
			add(keyname);
		}
	}

}
