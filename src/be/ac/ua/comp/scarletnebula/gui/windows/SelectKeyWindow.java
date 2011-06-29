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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;
import be.ac.ua.comp.scarletnebula.gui.SelectKeyList;

public abstract class SelectKeyWindow extends JDialog {

	private static final long serialVersionUID = 1L;
	private final SelectKeyList selectKeylist;

	public abstract void onOk(String keyname);

	public SelectKeyWindow(final JDialog parent, final CloudProvider provider) {
		super(parent, "Select key", true);
		setLayout(new BorderLayout());
		setSize(400, 300);

		selectKeylist = new SelectKeyList(provider);
		selectKeylist.fillWithKnownKeys();

		final JScrollPane keyScrollPane = new JScrollPane(selectKeylist);
		keyScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 20, 10),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		add(keyScrollPane, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		final JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				onOk(selectKeylist.getSelectedKey());
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		add(buttonPanel, BorderLayout.SOUTH);

		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		setVisible(true);
	}

}
