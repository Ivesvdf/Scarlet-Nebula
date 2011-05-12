package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.FavoriteImagesPanel;
import be.ac.ua.comp.scarletnebula.gui.InteractiveFirewallPanel;

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
