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
import java.beans.ExceptionListener;
import java.util.Collection;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.SSHPanel;
import be.ac.ua.comp.scarletnebula.misc.Utils;

public class SSHTerminalWindow extends JDialog {
	private static final long serialVersionUID = 1L;

	public SSHTerminalWindow(final JFrame parent,
			final Collection<Server> servers) {
		super(parent, false);
		setIconImage(Utils.icon("console16.png").getImage());
		setSize(600, 500);
		setLocationRelativeTo(parent);
		setLocationByPlatform(true);

		if (servers.size() == 1) {
			final Server server = servers.iterator().next();
			setTitle(server.getFriendlyName() + " terminal");
			setLayout(new BorderLayout());

			final SSHPanel sshPanel = new SSHPanel(server);
			sshPanel.addExceptionListener(new ExceptionListener() {
				@Override
				public void exceptionThrown(final Exception e) {
					JOptionPane.showMessageDialog(SSHTerminalWindow.this,
							e.getLocalizedMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});

			add(sshPanel);
		}

		setVisible(true);
	}
}
