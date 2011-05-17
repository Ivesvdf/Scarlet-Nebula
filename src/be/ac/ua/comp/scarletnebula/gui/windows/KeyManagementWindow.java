package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;

import javax.swing.JDialog;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.InteractiveKeyPanel;

public class KeyManagementWindow extends JDialog {
	private static final long serialVersionUID = 1L;

	public KeyManagementWindow(final JDialog parent,
			final CloudProvider provider) {
		super(parent, "Manage keys for " + provider.getName(), true);

		setSize(400, 300);
		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		setLayout(new BorderLayout());
		add(new InteractiveKeyPanel(provider));
		setVisible(true);

	}
}
