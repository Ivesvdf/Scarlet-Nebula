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

package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.dasein.cloud.network.Firewall;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.InteractiveFirewallPanel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class FirewallPage extends WizardPage {
	private static final long serialVersionUID = 1L;
	private final InteractiveFirewallPanel firewallPanel;

	public FirewallPage(final CloudProvider provider) {
		super(new BorderLayout());
		firewallPanel = new InteractiveFirewallPanel(provider);
		add(firewallPanel, BorderLayout.CENTER);
	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		final Collection<Firewall> selectedFirewalls = firewallPanel
				.getSelectedFirewalls();
		if (selectedFirewalls.size() < 1) {
			JOptionPane.showMessageDialog(this,
					"Please select one or more firewall rules.",
					"Select firewall", JOptionPane.ERROR_MESSAGE);

			return null;
		}

		if (!firewallPanel.selectedFirewallOpensPort(22)) {
			final int result = JOptionPane
					.showOptionDialog(
							this,
							"The firewall(s) you selected do not appear to have port 22 (SSH) open. \n "
									+ "You probably won't be able to connect with this server or receive statistics. \n\nDo you wish to proceed?",
							"No SSH rule found.", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE, null, new Object[] {
									"Proceed", "Cancel" }, "Cancel");

			if (result != JOptionPane.OK_OPTION) {
				return null;
			}
		}

		final AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;

		final Collection<String> ids = new ArrayList<String>(
				selectedFirewalls.size());
		for (final Firewall firewall : selectedFirewalls) {
			ids.add(firewall.getProviderFirewallId());
		}
		rec.firewallIds = ids;

		return new TaggingPage();
	}

}
