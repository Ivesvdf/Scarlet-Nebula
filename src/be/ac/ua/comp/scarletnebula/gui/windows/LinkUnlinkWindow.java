package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;
import be.ac.ua.comp.scarletnebula.gui.CollapsablePanel;
import be.ac.ua.comp.scarletnebula.gui.ServerList;
import be.ac.ua.comp.scarletnebula.gui.ServerListModel;
import be.ac.ua.comp.scarletnebula.gui.ServerListModel.CreateNewServerServer;
import be.ac.ua.comp.scarletnebula.gui.ThrobberBarWithText;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.ChooseImagePage;
import be.ac.ua.comp.scarletnebula.misc.SwingWorkerWithThrobber;

public class LinkUnlinkWindow extends JDialog {
	private static final Log log = LogFactory.getLog(ChooseImagePage.class);

	private static final long serialVersionUID = 1L;
	final ServerListModel unlinkedServerListModel = new ServerListModel(
			CreateNewServerServer.NO_NEW_SERVER);
	final ServerListModel linkedServerListModel = new ServerListModel(
			CreateNewServerServer.NO_NEW_SERVER);
	private final CollapsablePanel throbberPanel;

	LinkUnlinkWindow(final JFrame parent) {
		super(parent, "Link/Unlink Providers", true);

		setSize(500, 400);
		setLocationByPlatform(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final ThrobberBarWithText throbber = new ThrobberBarWithText(
				"Loading unlinked servers");

		final JPanel borderedThrobber = getBorderedThrobber(throbber);
		throbberPanel = new CollapsablePanel(borderedThrobber, false);

		add(throbberPanel, BorderLayout.NORTH);

		final JPanel mainPanel = getMainPanel();
		add(mainPanel, BorderLayout.CENTER);

		final JPanel bottomPanel = getBottomPanel();
		add(bottomPanel, BorderLayout.SOUTH);

		fillLinkedList(linkedServerListModel);
		fillUnlinkedList(unlinkedServerListModel);

		setVisible(true);
	}

	private JPanel getBorderedThrobber(final ThrobberBarWithText throbber) {
		final JPanel borderedThrobber = new JPanel(new BorderLayout());
		borderedThrobber.add(throbber, BorderLayout.CENTER);
		borderedThrobber.setBorder(BorderFactory
				.createEmptyBorder(10, 0, 10, 0));
		return borderedThrobber;
	}

	private JPanel getBottomPanel() {
		final JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
		final JButton cancelButton = ButtonFactory.createCancelButton();
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				LinkUnlinkWindow.this.dispose();
			}
		});

		bottomPanel.add(cancelButton);
		bottomPanel.add(Box.createHorizontalStrut(10));
		final JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				actuallyLinkUnlink();
				LinkUnlinkWindow.this.dispose();
			}
		});

		bottomPanel.add(okButton);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		return bottomPanel;
	}

	private final JPanel getMainPanel() {
		final JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		final ServerList linkedServerList = new ServerList(
				linkedServerListModel);
		linkedServerList.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		final JScrollPane linkedServerScrollPane = new JScrollPane(
				linkedServerList);
		linkedServerScrollPane.setBorder(BorderFactory.createTitledBorder(
				new EmptyBorder(5, 20, 20, 20), "Linked Servers"));
		// Doesn't matter what this is set to, as long as it's the same as the
		// one for unlinkedServerScrollPane
		linkedServerScrollPane.setPreferredSize(new Dimension(10, 10));

		final GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 1.0;

		mainPanel.add(linkedServerScrollPane, c);

		final ServerList unlinkedServerList = new ServerList(
				unlinkedServerListModel);
		unlinkedServerList.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		final JScrollPane unlinkedServerScrollPane = new JScrollPane(
				unlinkedServerList);
		unlinkedServerScrollPane.setBorder(BorderFactory.createTitledBorder(
				new EmptyBorder(5, 20, 20, 20), "Unlinked Servers"));

		// Doesn't matter what this is set to, as long as it's the same as the
		// one for unlinkedServerScrollPane
		unlinkedServerScrollPane.setPreferredSize(new Dimension(10, 10));

		final JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		middlePanel.add(Box.createVerticalGlue());

		final JButton linkSelectionButton = new JButton("<");
		final JButton unlinkSelectionButton = new JButton(">");

		linkSelectionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				// Move selection from unlinked to linked list
				final int selection = unlinkedServerList.getSelectedIndex();

				if (selection < 0) {
					return;
				}

				final Server server = unlinkedServerListModel
						.getVisibleServerAtIndex(selection);

				unlinkedServerListModel.removeServer(server);

				linkedServerListModel.addServer(server);
			}
		});

		unlinkSelectionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				// Move selection from linked to unlinked list
				final int selection = linkedServerList.getSelectedIndex();

				if (selection < 0) {
					return;
				}

				final int answer = JOptionPane
						.showOptionDialog(
								LinkUnlinkWindow.this,
								"You are about to unlink a server. "
										+ "Unlinking a server will permanently remove \nall data associated with "
										+ "this server, but the server will keep running. "
										+ "\n\nAre you sure you wish to continue?",
								"Unlink Server", JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE, null, null, null);

				if (answer != JOptionPane.YES_OPTION) {
					return;
				}

				final Server server = linkedServerListModel
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

		mainPanel.add(middlePanel, c);

		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 1.0;

		mainPanel.add(unlinkedServerScrollPane, c);
		return mainPanel;
	}

	protected void actuallyLinkUnlink() {
		// Walk over all servers in the linked serverlist and link those that
		// aren't linked
		for (final Server server : linkedServerListModel.getVisibleServers()) {
			if (!server.getCloud().isLinked(server)) {
				server.store();
				server.getCloud().linkServer(server);
			}
		}
		// ///////// TODO: Removals and additions need to be reflected in the
		// GUI's list!!

		// Walk over all servers in the unlinked serverlist and unlink those
		// that are linked
		for (final Server server : unlinkedServerListModel.getVisibleServers()) {
			if (server.getCloud().isLinked(server)) {
				server.getCloud().unlink(server);
			}
		}
	}

	private void fillUnlinkedList(final ServerListModel unlinkedServerListModel) {
		final SwingWorkerWithThrobber<ServerListModel, Server> fillUnlinkedListWorker = new SwingWorkerWithThrobber<ServerListModel, Server>(
				throbberPanel) {
			@Override
			protected ServerListModel doInBackground() throws Exception {
				for (final CloudProvider prov : CloudManager.get()
						.getLinkedCloudProviders()) {
					try {
						for (final Server server : prov.listUnlinkedServers()) {
							unlinkedServerListModel.addServer(server);
							if (isCancelled()) {
								break;
							}
						}
					} catch (final Exception e) {
						log.error("Error while querying unlinked servers.");
					}
				}
				return unlinkedServerListModel;
			}
		};
		fillUnlinkedListWorker.execute();
	}

	private void fillLinkedList(final ServerListModel linkedServerListModel) {
		for (final CloudProvider prov : CloudManager.get()
				.getLinkedCloudProviders()) {
			for (final Server server : prov.listLinkedServers()) {
				linkedServerListModel.addServer(server);
			}
		}
	}

	@Override
	protected void processWindowEvent(final WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			actuallyLinkUnlink();
			LinkUnlinkWindow.this.dispose();
		}
	}
}
