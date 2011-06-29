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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import be.ac.ua.comp.scarletnebula.misc.Utils;

public class CopyableLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public CopyableLabel() {
		super();
		init();
	}

	public CopyableLabel(final String text) {
		super(text);
		init();
	}

	private void init() {
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				mousePressed(e);
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					final JPopupMenu popup = new JPopupMenu();
					final JMenuItem copy = new JMenuItem("Copy",
							Utils.icon("copy16.png"));
					copy.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							final StringSelection stringSelection = new StringSelection(
									CopyableLabel.this.getText());
							final Clipboard clipboard = Toolkit
									.getDefaultToolkit().getSystemClipboard();
							clipboard.setContents(stringSelection, null);
						}
					});
					popup.add(copy);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseExited(final MouseEvent e) {
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
			}

			@Override
			public void mouseClicked(final MouseEvent e) {
			}
		});
	}
}
