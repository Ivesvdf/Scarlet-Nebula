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

package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.SelectKeyList;

public class ManageKeysDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	CloudProvider provider;
	GUI gui;

	ManageKeysDialog(final GUI gui, final CloudProvider provider) {
		super(gui, "Manage Keys", true);
		this.provider = provider;
		this.gui = gui;
		setSize(400, 400);
		setLocationByPlatform(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final SelectKeyList keyList = new SelectKeyList(provider);

		final JScrollPane keyListScrollPane = new JScrollPane(keyList);
		keyListScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20,
				20));

		setLayout(new BorderLayout());

		final JButton addButton = new JButton(new ImageIcon(getClass()
				.getResource("/images/add22.png")));
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {

			}
		});

		final JButton loadButton = new JButton("Import");

		final JButton removeButton = new JButton(new ImageIcon(getClass()
				.getResource("/images/remove22.png")));
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final int index = keyList.getSelectedIndex();

				// No selection
				if (index < 0) {
					return;
				}

				keyList.remove(index);
			}
		});

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		buttonPanel.add(addButton);
		buttonPanel.add(loadButton);
		buttonPanel.add(removeButton);

		add(keyListScrollPane);
		add(buttonPanel, BorderLayout.SOUTH);
	}

}
