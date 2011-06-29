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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JLabel;

public class ToolbarStyleButton extends JLabel {
	private static final long serialVersionUID = 1L;
	private final Collection<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private int eventID = 0;

	public ToolbarStyleButton(final Icon icon, final Icon onHover) {
		super(icon);
		// setBounds(10, 10, icon.getIconWidth(), icon.getIconHeight() - 2);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(final MouseEvent e) {
				setIcon(onHover);
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				setIcon(icon);
			}

			@Override
			public void mousePressed(final MouseEvent e) {

				for (final ActionListener listener : actionListeners) {
					listener.actionPerformed(new ActionEvent(
							ToolbarStyleButton.this, eventID++, "Clicked"));
				}
			}
		});

	}

	public void addActionListener(final ActionListener listener) {
		actionListeners.add(listener);
	}
}
