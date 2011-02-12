package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
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

		getContentPane().setLayout(new GridBagLayout());

		ServerListModel linkedServerListModel = new ServerListModel();
		ServerList linkedServerList = new ServerList(linkedServerListModel);
		linkedServerList.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		JScrollPane linkedServerScrollPane = new JScrollPane(linkedServerList);
		linkedServerScrollPane.setBorder(BorderFactory.createTitledBorder(
				new EmptyBorder(20, 20, 20, 20), "Linked Servers"));
		linkedServerScrollPane.setPreferredSize(new Dimension(10, 10));

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 1.0;

		add(linkedServerScrollPane, c);

		ServerListModel unlinkedServerListModel = new ServerListModel();
		ServerList unlinkedServerList = new ServerList(unlinkedServerListModel);
		unlinkedServerList.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		JScrollPane unlinkedServerScrollPane = new JScrollPane(
				unlinkedServerList);
		unlinkedServerScrollPane.setBorder(BorderFactory.createTitledBorder(
				new EmptyBorder(20, 20, 20, 20), "Unlinked Servers"));
		unlinkedServerScrollPane.setPreferredSize(new Dimension(10, 10));

		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		middlePanel.add(Box.createVerticalGlue());
		middlePanel.add(new JButton(">"));
		middlePanel.add(Box.createVerticalStrut(10));
		middlePanel.add(new JButton("<"));
		middlePanel.add(Box.createVerticalGlue());

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		add(middlePanel, c);

		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 1.0;

		add(unlinkedServerScrollPane, c);

		fillLinkedList(linkedServerListModel);
		fillUnlinkedList(unlinkedServerListModel);

		setVisible(true);
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
