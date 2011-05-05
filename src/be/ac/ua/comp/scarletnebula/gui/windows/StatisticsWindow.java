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

public class StatisticsWindow extends JDialog
{
	private static final long serialVersionUID = 1L;

	public StatisticsWindow(final JFrame parent,
			final Collection<Server> servers)
	{
		super(parent, false);

		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		final List<String> servernames = new ArrayList<String>(servers.size());
		for (final Server server : servers)
		{
			servernames.add(server.getFriendlyName());
		}
		setTitle("Statistics for " + Utils.implode(servernames, ","));
		setSize(500, 400);

		final Server server = servers.iterator().next();

		final AllGraphsPanel allGraphsPanel = new AllGraphsPanel(server);
		add(new JScrollPane(allGraphsPanel));

		setVisible(true);

	}

}
