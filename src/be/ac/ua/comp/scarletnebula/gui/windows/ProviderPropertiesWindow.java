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

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.FavoriteImagesPanel;
import be.ac.ua.comp.scarletnebula.gui.InteractiveFirewallPanel;
import be.ac.ua.comp.scarletnebula.gui.InteractiveKeyPanel;

public class ProviderPropertiesWindow extends JDialog {
	private static final long serialVersionUID = 1L;

	public ProviderPropertiesWindow(final JDialog parent,
			final CloudProvider provider) {
		super(parent, provider.getName() + " Properties", true);
		setLayout(new BorderLayout());
		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		setSize(550, 400);

		final JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Favorite Images", new FavoriteImagesPanel(provider));

		if (provider.supportsSSHKeys()) {
			final InteractiveKeyPanel keyPanel = new InteractiveKeyPanel(
					provider);
			tabbedPane.addTab("Key Management", keyPanel);
		}

		if (provider.supportsFirewalls()) {
			final InteractiveFirewallPanel firewallPanel = new InteractiveFirewallPanel(
					provider);
			tabbedPane.addTab("Firewall Management", firewallPanel);
		}

		add(tabbedPane, BorderLayout.CENTER);
		setVisible(true);
	}
}
