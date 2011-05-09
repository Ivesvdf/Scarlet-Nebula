package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.InteractiveFirewallPanel;

public class ProviderPropertiesWindow extends JDialog
{
	private static final long serialVersionUID = 1L;

	public ProviderPropertiesWindow(final JDialog parent,
			final CloudProvider provider)
	{
		super(parent, provider.getName() + " Properties", true);

		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		setSize(550, 400);

		final JTabbedPane tabbedPane = new JTabbedPane();
		final InteractiveKeyPanel keyPanel = new InteractiveKeyPanel(provider);
		tabbedPane.addTab("Key Management", keyPanel);
		final InteractiveFirewallPanel firewallPanel = new InteractiveFirewallPanel(
				provider);
		tabbedPane.addTab("Firewall Management", firewallPanel);

		if (!provider.supportsSSHKeys())
		{
			keyPanel.setEnabled(false);
		}

		if (!provider.supportsFirewalls())
		{
			firewallPanel.setEnabled(false);
		}

		add(tabbedPane, BorderLayout.CENTER);
		setVisible(true);
	}
}
