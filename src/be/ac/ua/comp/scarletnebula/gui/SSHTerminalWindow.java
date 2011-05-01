package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JDialog;
import javax.swing.JFrame;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.misc.Utils;

public class SSHTerminalWindow extends JDialog
{
	private static final long serialVersionUID = 1L;

	SSHTerminalWindow(JFrame parent, Collection<Server> servers)
	{
		super(parent, false);
		setIconImage(Utils.icon("console16.png").getImage());
		setSize(600, 500);
		setLocationRelativeTo(parent);
		setLocationByPlatform(true);

		if (servers.size() == 1)
		{
			final Server server = servers.iterator().next();
			setTitle(server.getFriendlyName() + " terminal");
			setLayout(new BorderLayout());

			add(new SSHPanel(server));
		}

		setVisible(true);
	}
}
