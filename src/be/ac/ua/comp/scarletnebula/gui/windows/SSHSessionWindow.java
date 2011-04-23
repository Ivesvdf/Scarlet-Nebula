package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JDialog;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.DecoratedCommunicationPanel;

public class SSHSessionWindow extends JDialog
{
	private static final long serialVersionUID = 1L;

	SSHSessionWindow(final JDialog parent, final Collection<Server> servers)
	{
		super(parent, "SSH", false);

		if (servers.size() == 1)
		{
			setTitle("SSH to " + servers.iterator().next().getFriendlyName());
		}

		DecoratedCommunicationPanel panel = new DecoratedCommunicationPanel(
				this, servers);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		setVisible(true);
	}
}
