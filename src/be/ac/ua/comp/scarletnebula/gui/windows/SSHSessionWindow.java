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
import java.util.Collection;

import javax.swing.JDialog;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.DecoratedCommunicationPanel;

public class SSHSessionWindow extends JDialog {
	private static final long serialVersionUID = 1L;

	SSHSessionWindow(final JDialog parent, final Collection<Server> servers) {
		super(parent, "SSH", false);

		if (servers.size() == 1) {
			setTitle("SSH to " + servers.iterator().next().getFriendlyName());
		}

		final DecoratedCommunicationPanel panel = new DecoratedCommunicationPanel(
				this, servers);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		setVisible(true);
	}
}
