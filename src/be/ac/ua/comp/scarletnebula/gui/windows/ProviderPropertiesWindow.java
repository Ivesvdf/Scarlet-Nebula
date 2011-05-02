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
		tabbedPane.addTab("Key Management", new InteractiveKeyPanel(provider));
		tabbedPane.addTab("Firewall Management", new InteractiveFirewallPanel(
				provider));
		add(tabbedPane, BorderLayout.CENTER);
		setVisible(true);
	}
}
