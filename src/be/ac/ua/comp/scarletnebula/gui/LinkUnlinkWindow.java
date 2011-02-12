package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.Server;

public class LinkUnlinkWindow extends JDialog
{
	private static final long serialVersionUID = 1L;

	LinkUnlinkWindow(JFrame parent)
	{
		super(parent, "Link/Unlink Providers", true);

		setSize(500, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());

		final ServerListModel linkedServerListModel = new ServerListModel();
		final ServerList linkedServerList = new ServerList(
				linkedServerListModel);
		linkedServerList.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		JScrollPane linkedServerScrollPane = new JScrollPane(linkedServerList);
		linkedServerScrollPane.setBorder(BorderFactory.createTitledBorder(
				new EmptyBorder(20, 20, 20, 20), "Linked Servers"));
		// Doesn't matter what this is set to, as long as it's the same as the
		// one for unlinkedServerScrollPane
		linkedServerScrollPane.setPreferredSize(new Dimension(10, 10));

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 1.0;

		topPanel.add(linkedServerScrollPane, c);

		final ServerListModel unlinkedServerListModel = new ServerListModel();
		final ServerList unlinkedServerList = new ServerList(
				unlinkedServerListModel);
		unlinkedServerList.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		JScrollPane unlinkedServerScrollPane = new JScrollPane(
				unlinkedServerList);
		unlinkedServerScrollPane.setBorder(BorderFactory.createTitledBorder(
				new EmptyBorder(20, 20, 20, 20), "Unlinked Servers"));

		// Doesn't matter what this is set to, as long as it's the same as the
		// one for unlinkedServerScrollPane
		unlinkedServerScrollPane.setPreferredSize(new Dimension(10, 10));

		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		middlePanel.add(Box.createVerticalGlue());

		JButton linkSelectionButton = new JButton("<");
		JButton unlinkSelectionButton = new JButton(">");

		linkSelectionButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Move selection from unlinked to linked list
				int selection = unlinkedServerList.getSelectedIndex();

				if (selection < 0)
					return;

				Server server = unlinkedServerListModel
						.getVisibleServerAtIndex(selection);

				unlinkedServerListModel.removeServer(server);

				linkedServerListModel.addServer(server);
			}
		});

		unlinkSelectionButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Move selection from linked to unlinked list
				int selection = linkedServerList.getSelectedIndex();

				if (selection < 0)
					return;

				int answer = JOptionPane
						.showOptionDialog(
								LinkUnlinkWindow.this,
								"You are about to unlink a server. "
										+ "Unlinking a server will permanently remove \nall data associated with "
										+ "this server, but the server will keep running. "
										+ "\n\nAre you sure you wish to continue?",
								"Unlink Server", JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE, null, null, null);

				if (answer != JOptionPane.YES_OPTION)
					return;

				Server server = linkedServerListModel
						.getVisibleServerAtIndex(selection);

				linkedServerListModel.removeServer(server);

				unlinkedServerListModel.addServer(server);
			}
		});

		middlePanel.add(unlinkSelectionButton);
		middlePanel.add(Box.createVerticalStrut(10));
		middlePanel.add(linkSelectionButton);
		middlePanel.add(Box.createVerticalGlue());

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		topPanel.add(middlePanel, c);

		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 1.0;

		topPanel.add(unlinkedServerScrollPane, c);

		add(topPanel);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				LinkUnlinkWindow.this.dispose();
			}
		});

		bottomPanel.add(cancelButton);
		bottomPanel.add(Box.createHorizontalStrut(10));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				actuallyLinkUnlink(linkedServerListModel,
						unlinkedServerListModel);
				LinkUnlinkWindow.this.dispose();
			}
		});

		bottomPanel.add(okButton);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

		add(bottomPanel, BorderLayout.SOUTH);

		fillLinkedList(linkedServerListModel);
		fillUnlinkedList(unlinkedServerListModel);

		setVisible(true);
	}

	protected void actuallyLinkUnlink(ServerListModel linkedServerListModel,
			ServerListModel unlinkedServerListModel)
	{
		// Walk over all servers in the linked serverlist and link those that
		// aren't linked
		for (Server server : linkedServerListModel.getVisibleServers())
		{
			if (!server.getCloud().isLinked(server))
			{
				server.getCloud().linkUnlinkedServer(server);
			}
		}
		// ///////// TODO: Removals and additions need to be reflected in the
		// GUI's list!!

		// Walk over all servers in the unlinked serverlist and unlink those
		// that are linked
		for (Server server : unlinkedServerListModel.getVisibleServers())
		{
			if (server.getCloud().isLinked(server))
			{
				server.getCloud().unlink(server);
			}
		}
	}

	private void fillUnlinkedList(ServerListModel unlinkedServerListModel)
	{
		for (CloudProvider prov : CloudManager.get().getLinkedCloudProviders())
		{
			try
			{
				for (Server server : prov.listUnlinkedServers())
				{
					unlinkedServerListModel.addServer(server);
				}
			}
			catch (InternalException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (CloudException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void fillLinkedList(ServerListModel linkedServerListModel)
	{
		for (CloudProvider prov : CloudManager.get().getLinkedCloudProviders())
		{
			for (Server server : prov.listLinkedServers())
			{
				linkedServerListModel.addServer(server);
			}
		}
	}
}
