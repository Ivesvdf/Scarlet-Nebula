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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.AllGraphsPanel;
import be.ac.ua.comp.scarletnebula.misc.Utils;

public class StatisticsWindow extends JDialog {
	private static final long serialVersionUID = 1L;

	public StatisticsWindow(final JFrame parent,
			final Collection<Server> servers) {
		super(parent, false);

		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		final List<String> servernames = new ArrayList<String>(servers.size());
		for (final Server server : servers) {
			servernames.add(server.getFriendlyName());
		}
		setTitle("Statistics for " + Utils.implode(servernames, ","));

		final Server server = servers.iterator().next();

		final AllGraphsPanel allGraphsPanel = new AllGraphsPanel(server);
		add(new JScrollPane(allGraphsPanel));

		setSize(500, 180 * (int) Math.ceil(1.0 * allGraphsPanel
				.getComponentCount() / 2.0));

		setVisible(true);

	}

}
