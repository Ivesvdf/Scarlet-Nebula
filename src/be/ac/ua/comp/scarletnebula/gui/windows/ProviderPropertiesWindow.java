package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;

public class ProviderPropertiesWindow extends JDialog
{
	private static final long serialVersionUID = 1L;
	private final CloudProvider provider;

	public ProviderPropertiesWindow(final JDialog parent,
			final CloudProvider provider)
	{
		super(parent, provider.getName() + " Properties", true);
		this.provider = provider;

		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		setSize(450, 300);

		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Key Management", new InteractiveKeyPanel(provider));
		add(tabbedPane, BorderLayout.CENTER);
		setVisible(true);
	}
}
