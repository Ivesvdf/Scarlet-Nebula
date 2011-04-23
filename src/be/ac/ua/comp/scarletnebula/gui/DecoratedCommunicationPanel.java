package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.compute.VmState;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.windows.ChangeServerSshLoginMethodWindow;
import be.ac.ua.comp.scarletnebula.misc.Utils;

public class DecoratedCommunicationPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	final JDialog parent;
	private static Log log = LogFactory
			.getLog(DecoratedCommunicationPanel.class);

	public DecoratedCommunicationPanel(JDialog parent,
			Collection<Server> selectedServers)
	{
		this.parent = parent;
		clearAndFill(selectedServers);
	}

	final public void clearAndFill(final Collection<Server> selectedServers)
	{

		// Remove all components on there
		invalidate();
		removeAll();

		setLayout(new BorderLayout());

		// If there are no servers, or none of the servers are running, do not
		// display the ssh console
		Collection<Server> connectableServers = new ArrayList<Server>();
		for (Server s : selectedServers)
		{
			if (s.getStatus() == VmState.RUNNING
					&& s.getPublicDnsAddress() != null)
			{
				connectableServers.add(s);
			}
		}

		// If there are no servers to connect to, don't draw the ssh console
		if (connectableServers.size() == 0)
		{
			log.info("Connection tab clicked and no servers selected to connect to.");
			BetterTextLabel txt = new BetterTextLabel(
					"Please select at least one running server to connect to.");
			txt.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 20));
			add(txt, BorderLayout.CENTER);
			validate();
			repaint();
		}
		else
		{
			final Server connectServer = selectedServers.iterator().next();

			JPanel propertiesPanel = new JPanel();
			propertiesPanel.setLayout(new BoxLayout(propertiesPanel,
					BoxLayout.LINE_AXIS));
			propertiesPanel.add(Box.createHorizontalGlue());
			final JButton propertiesButton = new JButton("Properties",
					Utils.icon("modify16.png"));
			propertiesButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					ChangeServerSshLoginMethodWindow win = new ChangeServerSshLoginMethodWindow(
							parent, connectServer);
					win.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							clearAndFill(selectedServers);
						}
					});
					win.setVisible(true);
				}
			});

			final JButton restartButton = new JButton("Restart connection",
					Utils.icon("undo16.png"));
			restartButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					clearAndFill(selectedServers);
				}
			});
			propertiesPanel.add(restartButton);
			propertiesPanel.add(Box.createHorizontalStrut(10));
			propertiesPanel.add(propertiesButton);
			propertiesPanel.add(Box.createHorizontalStrut(20));
			propertiesPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0,
					0));
			add(propertiesPanel, BorderLayout.NORTH);

			final SSHPanel sshPanel = new SSHPanel(connectServer);
			sshPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));
			add(sshPanel, BorderLayout.CENTER);

			validate();
			repaint();
		}
	}
}
