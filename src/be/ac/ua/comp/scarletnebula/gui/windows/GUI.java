package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.compute.VmState;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.core.ServerChangedObserver;
import be.ac.ua.comp.scarletnebula.core.ServerDisappearedException;
import be.ac.ua.comp.scarletnebula.core.ServerLinkUnlinkObserver;
import be.ac.ua.comp.scarletnebula.gui.CollapsablePanel;
import be.ac.ua.comp.scarletnebula.gui.GraphPanelCache;
import be.ac.ua.comp.scarletnebula.gui.SearchField;
import be.ac.ua.comp.scarletnebula.gui.ServerList;
import be.ac.ua.comp.scarletnebula.gui.ServerListModel;
import be.ac.ua.comp.scarletnebula.gui.ServerListModel.CreateNewServerServer;
import be.ac.ua.comp.scarletnebula.gui.ServerListMouseListener;
import be.ac.ua.comp.scarletnebula.gui.ThrobberFactory;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizard;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.AddServerWizard;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.AddServerWizardDataRecorder;
import be.ac.ua.comp.scarletnebula.gui.welcomewizard.WelcomeWizard;
import be.ac.ua.comp.scarletnebula.misc.SwingWorkerWithThrobber;
import be.ac.ua.comp.scarletnebula.misc.Utils;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;

public class GUI extends JFrame implements ListSelectionListener,
		ServerChangedObserver, ServerLinkUnlinkObserver {
	private static Log log = LogFactory.getLog(GUI.class);

	private static final long serialVersionUID = 1L;
	private ServerList serverList;
	private ServerListModel serverListModel;

	private final JPanel searchPanel = new JPanel(new FlowLayout());
	private final JTextField filterTextField = new JTextField(15);

	private final JPanel throbberPanel = new JPanel(new GridBagLayout());
	private int throbberCount = 0;

	public GUI() {
		chooseLookAndFeel();

		setTitle("Scarlet Nebula");
		setSize(700, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		final ImageIcon icon = new ImageIcon(getClass().getResource(
				"/images/icon48.png"));
		setIconImage(icon.getImage());

		addToolbar();

		final JPanel mainPanel = getMainPanel();

		add(mainPanel);

		addMenubar();
		setKeyboardAccelerators();

		setLocationRelativeTo(null);
		setLocationByPlatform(true);

		setVisible(true);

		// Last but not least, we construct a cloudmanager object, which will
		// cause it to load all providers and thus servers.
		// We also register ourselves as an observer for linking and unlinking
		// servers so we can update the serverlist.
		CloudManager.get().addServerLinkUnlinkObserver(this);

		(new SwingWorkerWithThrobber<Object, Object>(
				newThrobber("Loading servers...")) {

			@Override
			protected Object doInBackground() throws Exception {
				try {
					CloudManager.get().loadAllLinkedServers();

					// now start refreshing all servers if necessary
					for (final CloudProvider cloudprovider : CloudManager.get()
							.getLinkedCloudProviders()) {
						{

							for (final Server server : cloudprovider
									.listLinkedServers()) {
								if (server.getStatus() == VmState.PENDING) {
									server.refreshUntilServerHasState(VmState.RUNNING);
								}
								if (server.getStatus() == VmState.REBOOTING) {
									server.refreshUntilServerHasState(VmState.RUNNING);
								}
								if (server.getStatus() == VmState.STOPPING) {
									server.refreshUntilServerHasState(VmState.TERMINATED);
								}
							}
						}

					}
				} catch (final Exception e) {
					log.error("Error while getting servers", e);
				}
				return null;
			}

		}).execute();

		// If there are no linked cloudproviders, start the new provider wizard
		if (CloudManager.get().getLinkedCloudProviders().size() == 0) {
			final SwingWorker<Object, Object> welcomeWizardWorker = new SwingWorker<Object, Object>() {
				@Override
				protected Object doInBackground() throws Exception {
					new WelcomeWizard(GUI.this);
					return null;
				}
			};

			welcomeWizardWorker.execute();
		}
	}

	private CollapsablePanel newThrobber(final String string) {
		final CollapsablePanel throbber = ThrobberFactory.getCollapsableThrobber(
				string, 2, 2);
		final GridBagConstraints c = new GridBagConstraints();

		c.weightx = 1.0;
		c.weighty = 0.0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = throbberCount++;

		throbberPanel.add(throbber, c);
		return throbber;
	}

	private JPanel getMainPanel() {
		final JPanel mainPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isOptimizedDrawingEnabled() {
				return false;
			}
		};
		mainPanel.setLayout(new OverlayLayout(mainPanel));

		final JPanel underlayPanel = new JPanel(new BorderLayout());
		underlayPanel.add(getServerListPanel(), BorderLayout.CENTER);
		underlayPanel.add(throbberPanel, BorderLayout.NORTH);
		final JPanel overlayPanel = getOverlayPanel();

		mainPanel.add(overlayPanel);
		mainPanel.add(underlayPanel);
		return mainPanel;
	}

	private JPanel getOverlayPanel() {
		final JPanel overlayPanel = new JPanel();
		overlayPanel.setOpaque(false);
		overlayPanel.setLayout(new GridBagLayout());
		filterTextField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(final KeyEvent e) {

			}

			@Override
			public void keyReleased(final KeyEvent e) {
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					hideFilter();
				}
			}
		});
		filterTextField.getDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void removeUpdate(final DocumentEvent e) {
						textChanged();

					}

					@Override
					public void insertUpdate(final DocumentEvent e) {
						textChanged();
					}

					@Override
					public void changedUpdate(final DocumentEvent e) {

					}

					private void textChanged() {
						serverListModel.filter(filterTextField.getText());
					}
				});

		final SearchField searchField = new SearchField(filterTextField);

		final ImageIcon closeIcon = Utils.icon("cross16.png");
		final JButton closeButton = new JButton(closeIcon);
		closeButton.setBounds(10, 10, closeIcon.getIconWidth(),
				closeIcon.getIconHeight());
		closeButton.setMargin(new Insets(0, 0, 0, 0));
		closeButton.setOpaque(false);
		closeButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				hideFilter();
			}
		});

		searchPanel.add(searchField);
		searchPanel.add(closeButton);
		// searchPanel.setBorder(BorderFactory
		// .createBevelBorder(BevelBorder.RAISED));
		searchPanel.setBorder(BorderFactory.createEtchedBorder());
		searchPanel.setVisible(false);
		searchPanel.setAlignmentX(RIGHT_ALIGNMENT);

		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(3, 3, 3, 3);
		overlayPanel.add(searchPanel, c);
		return overlayPanel;
	}

	protected void hideFilter() {
		searchPanel.setVisible(false);
		filterTextField.setText("");
	}

	private void showFilter() {
		searchPanel.setVisible(true);
		filterTextField.requestFocusInWindow();
	}

	private void chooseLookAndFeel() {
		try {
			boolean found = false;
			for (final LookAndFeelInfo info : UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					found = true;
					break;
				}
			}
			if (!found) {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			}
		} catch (final Exception e) {
			log.error("Cannot set look and feel", e);
		}
	}

	private void setKeyboardAccelerators() {
		serverList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("control F"), "search");
		serverList.getActionMap().put("search", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				showFilter();
			}
		});
	}

	private void addToolbar() {
		final JToolBar toolbar = new JToolBar();

		final Icon addIcon = Utils.icon("add16.png");

		final JButton addButton = new JButton(addIcon);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				startAddServerWizard();
			}
		});
		addButton.setBounds(10, 10, addIcon.getIconWidth(),
				addIcon.getIconHeight());

		final Icon refreshIcon = Utils.icon("refresh16.png");
		final JButton refreshButton = new JButton(refreshIcon);
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				refreshSelectedServers();
			}
		});
		refreshButton.setBounds(10, 10, refreshIcon.getIconWidth(),
				refreshIcon.getIconHeight());

		final Icon searchIcon = Utils.icon("search16.png");
		final JButton searchButton = new JButton(searchIcon);
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showFilter();
			}
		});
		searchButton.setBounds(10, 10, searchIcon.getIconWidth(),
				searchIcon.getIconHeight());

		toolbar.add(addButton);
		toolbar.add(refreshButton);
		toolbar.add(searchButton);
		toolbar.setFloatable(false);
		add(toolbar, BorderLayout.PAGE_START);
	}

	private void addMenubar() {
		final JMenuBar menuBar = new JMenuBar();
		final JMenu providerMenu = getProviderMenu();
		final JMenu serverMenu = getServerMenu();
		final JMenu helpMenu = getHelpMenu();

		menuBar.add(providerMenu);
		menuBar.add(serverMenu);

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	private JMenu getServerMenu() {
		final JMenu serverMenu = new JMenu("Servers");
		serverMenu.setMnemonic(KeyEvent.VK_S);

		final JMenuItem startServerItem = new JMenuItem("Start new server",
				Utils.icon("add16.png"));
		startServerItem.setMnemonic(KeyEvent.VK_S);
		startServerItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				startAddServerWizard();
			}
		});
		serverMenu.add(startServerItem);

		final JMenuItem searchServerItem = new JMenuItem("Filter servers",
				Utils.icon("search16.png"));
		searchServerItem.setMnemonic(KeyEvent.VK_F);
		searchServerItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showFilter();
			}
		});
		serverMenu.add(searchServerItem);
		return serverMenu;
	}

	private JMenu getHelpMenu() {
		final JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);

		// Pick a random message to display in the help menu
		final String messages[] = { "(You won't find any help here)",
				"(Nobody can help you)", "(Keep on lookin' if you need help)",
				"(Heeeeelp!)", "(You might want to try google for help)",
				"(Try yelling loudly if you need help)" };

		final Random generator = new Random(System.currentTimeMillis());

		final JMenuItem noHelpItem = new JMenuItem(
				messages[generator.nextInt(messages.length)]);
		noHelpItem.setEnabled(false);
		helpMenu.add(noHelpItem);

		return helpMenu;
	}

	private JMenu getProviderMenu() {
		final JMenu providerMenu = new JMenu("Providers");
		providerMenu.setMnemonic(KeyEvent.VK_P);
		providerMenu.getAccessibleContext().setAccessibleDescription(
				"Managing cloud providers.");

		final JMenuItem manageProvidersItem = new JMenuItem("Manage Providers");
		manageProvidersItem.setMnemonic(KeyEvent.VK_M);
		manageProvidersItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				new ManageProvidersWindow(GUI.this);
			}
		});

		providerMenu.add(manageProvidersItem);

		final JMenuItem detectAllUnlinkedInstances = new JMenuItem(
				"Link/Unlink Instances");
		detectAllUnlinkedInstances.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				new LinkUnlinkWindow(GUI.this);
				// detectAllUnlinkedInstances();
			}
		});
		providerMenu.add(detectAllUnlinkedInstances);
		return providerMenu;
	}

	protected void clearSelection() {
		serverList.clearSelection();
		// fillRightPartition();
	}

	public void terminateSelectedServers() {
		final Collection<Server> servers = serverList.getSelectedServers();

		if (servers.isEmpty()) {
			return;
		}

		final String deleteMessage = "You are about to terminate "
				+ servers.size()
				+ " server(s). \n"
				+ "Terminating a server will permanently destroy the server. This operation cannot be undone.\n\n"
				+ "Do you wish to proceed?";

		final String deleteTitle = "Terminate " + servers.size() + " server(s)";
		final String buttonString = deleteTitle;
		final int result = JOptionPane.showOptionDialog(this, deleteMessage,
				deleteTitle, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE, null,
				Arrays.asList(buttonString, "Cancel").toArray(), "Cancel");

		if (result != JOptionPane.OK_OPTION) {
			return;
		}

		(new SwingWorkerWithThrobber<Exception, Object>(
				newThrobber("Terminating server"
						+ ((servers.size() > 1) ? "s" : ""))) {
			@Override
			protected Exception doInBackground() throws Exception {
				for (final Server server : servers) {
					try {
						server.terminate();
						server.refreshUntilServerHasState(VmState.TERMINATED);
					} catch (final Exception e) {
						return e;
					}
				}
				return null;
			}

			@Override
			public void done() {
				try {
					final Exception result = get();

					if (result != null) {
						log.error(result);
						error(result);
					}
				} catch (final Exception ignore) {
				}
			}
		}).execute();

	}

	private JPanel getServerListPanel() {
		// Create the list and put it in a scroll pane.
		serverListModel = new ServerListModel(
				CreateNewServerServer.DISPLAY_NEW_SERVER);
		serverList = new ServerList(serverListModel);
		// serverList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		serverList.addMouseListener(new ServerListMouseListener(this,
				serverList, serverListModel));
		serverList.addListSelectionListener(this);
		serverList.requestFocusInWindow();

		final JScrollPane serverScrollPane = new JScrollPane(serverList);

		/*
		 * JTextField searchField = new JTextField(10); SearchFieldListener
		 * searchFieldListener = new SearchFieldListener( searchField,
		 * serverListModel); searchField.addActionListener(searchFieldListener);
		 * searchField.getDocument().addDocumentListener(searchFieldListener);
		 * 
		 * JPanel topLeftPane = new JPanel(); topLeftPane.setLayout(new
		 * BoxLayout(topLeftPane, BoxLayout.LINE_AXIS));
		 * topLeftPane.add(searchField);
		 */

		final JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());

		// leftPanel.add(topLeftPane, BorderLayout.PAGE_START);
		leftPanel.add(serverScrollPane, BorderLayout.CENTER);

		return leftPanel;
	}

	public void refreshSelectedServers() {
		final int indices[] = serverList.getSelectedIndices();

		final Collection<Server> servers = serverListModel
				.getVisibleServersAtIndices(indices);

		// fillRightPartition();

		(new SwingWorker<Exception, Object>() {
			@Override
			protected Exception doInBackground() throws Exception {
				for (final Server server : servers) {
					try {
						server.refresh();
					}

					catch (final ServerDisappearedException e) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								log.info("Server disappeared.", e);
								removeServer(server);
							}
						});
					} catch (final Exception e) {
						return e;
					}
				}
				return null;
			}

			@Override
			public void done() {
				try {
					final Exception result = get();

					if (result != null) {
						log.error("Error while refreshing");
						error(result.getLocalizedMessage());
					}
				} catch (final Exception ignore) {
				}
			}
		}).execute();
	}

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			// fillRightPartition();
		}
	}

	/*
	 * private void fillRightPartition() { int selectedIndex =
	 * serverList.getSelectedIndex();
	 * 
	 * // When nothing is selected, return
	 * 
	 * log.debug("Filling right partition"); Collection<Server> selectedServers
	 * = new ArrayList<Server>();
	 * 
	 * if (selectedIndex >= 0) selectedServers.add(serverListModel
	 * .getVisibleServerAtIndex(selectedIndex));
	 * 
	 * updateOverviewTab(selectedServers);
	 * updateCommunicationTab(selectedServers); }
	 */

	void startAddServerWizard() {
		if (CloudManager.get().getLinkedCloudProviders().size() == 0) {
			JOptionPane
					.showMessageDialog(
							this,
							"A CloudProvider account is required to add a new server, \n"
									+ "and there don't seem to be any. Please add one before continuing.",
							"No CloudProvider accounts found",
							JOptionPane.ERROR_MESSAGE);

			final AddProviderWizard wiz = new AddProviderWizard();
			wiz.startModal(this);
		} else {
			new AddServerWizard(this, this);
		}
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GUI();
			}
		});
	}

	public void error(final String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage,
				"An unexpected error occured.", JOptionPane.ERROR_MESSAGE);
	}

	public void error(final Exception e) {
		error(e.getLocalizedMessage());
	}

	private void addServer(final Server server) {
		serverListModel.addServer(server);
		serverList.setSelectedIndices(new int[0]);
		server.addServerChangedObserver(this);
	}

	@Override
	public void serverChanged(final Server server) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				serverListModel.refreshServer(server);
			}
		});
	}

	public void pauseSelectedServers() {
		final Collection<Server> selectedServers = serverList
				.getSelectedServers();

		(new SwingWorkerWithThrobber<Exception, Object>(
				newThrobber("Pausing server"
						+ ((selectedServers.size() > 1) ? "s" : ""))) {
			@Override
			protected Exception doInBackground() throws Exception {
				try {
					for (final Server server : selectedServers) {
						server.pause();
						server.refreshUntilServerHasState(VmState.PAUSED);
					}
				} catch (final Exception e) {
					return e;
				}

				return null;
			}

			@Override
			public void done() {
				try {
					final Exception result = get();

					if (result != null) {
						log.error("Error while pausing", result);
						error(result);
					}
				} catch (final Exception ignore) {
				}
			}
		}).execute();

	}

	public void resumeSelectedServers() {
		final Collection<Server> selectedServers = serverList
				.getSelectedServers();

		(new SwingWorkerWithThrobber<Exception, Object>(
				newThrobber("Resuming server"
						+ ((selectedServers.size() > 1) ? "s" : ""))) {
			@Override
			protected Exception doInBackground() throws Exception {
				try {
					for (final Server server : selectedServers) {
						server.resume();
						server.refreshUntilServerHasState(VmState.RUNNING);
					}
				} catch (final Exception e) {
					return e;
				}

				return null;
			}

			@Override
			public void done() {
				try {
					final Exception result = get();

					if (result != null) {
						log.error("Error while resuming", result);
						error(result);
					}
				} catch (final Exception ignore) {
				}
			}
		}).execute();
	}

	public void rebootSelectedServers() {
		final Collection<Server> selectedServers = serverList
				.getSelectedServers();

		(new SwingWorkerWithThrobber<Exception, Object>(
				newThrobber("Rebooting server"
						+ ((selectedServers.size() > 1) ? "s" : ""))) {
			@Override
			protected Exception doInBackground() throws Exception {
				try {
					for (final Server server : selectedServers) {
						server.reboot();
					}
				} catch (final Exception e) {
					return e;
				}
				return null;
			}

			@Override
			public void done() {
				try {
					final Exception result = get();

					if (result != null) {
						log.error("Error while rebooting", result);
						error(result);
					}
				} catch (final Exception ignore) {
				}
			}
		}).execute();

	}

	public void unlinkSelectedServers() {
		final Collection<Server> selectedServers = serverList
				.getSelectedServers();

		final String unlinkString = "Unlink " + selectedServers.size()
				+ " servers";
		final int result = JOptionPane
				.showOptionDialog(
						this,
						"You are about to unlink "
								+ selectedServers.size()
								+ " server(s).\n"
								+ "Unlinked servers are not terminated and will keep running.",
						unlinkString, JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE, null,
						Arrays.asList(unlinkString, "Cancel").toArray(),
						"Cancel");

		if (result != JOptionPane.OK_OPTION) {
			return;
		}

		for (final Server server : selectedServers) {
			server.unlink();
		}

	}

	private void removeServer(final Server server) {
		serverListModel.removeServer(server);
	}

	@Override
	public void serverLinked(final CloudProvider cloudProvider, final Server srv) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				addServer(srv);
			}
		});
	}

	@Override
	public void serverUnlinked(final CloudProvider cloudProvider,
			final Server srv) {
		removeServer(srv);
		GraphPanelCache.get().clearBareServerCache(srv);
	}

	public void startServer(final AddServerWizard addServerWizard,
			final DataRecorder recorder) {
		final AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;
		final String instancename = rec.instanceName;
		final VirtualMachineProduct instancesize = rec.instanceSize;
		final MachineImage image = rec.image;
		final CloudProvider provider = rec.provider;
		final Collection<String> tags = rec.tags;
		final String keypairOrPassword;

		if (provider.supportsSSHKeys()) {
			keypairOrPassword = rec.keypairOrPassword != null ? rec.keypairOrPassword
					: provider.getDefaultKeypair();
		} else {
			keypairOrPassword = Utils.getRandomString(8);
		}
		final Collection<String> firewallIds = rec.firewallIds;
		final int instanceCount = rec.instanceCount;

		if (Server.exists(instancename)) {
			JOptionPane.showMessageDialog(addServerWizard.parent,
					"A server with this name already exists.",
					"Server already exists", JOptionPane.ERROR_MESSAGE);
			return;
		}

		final CollapsablePanel throbber = newThrobber("Starting server"
				+ (instanceCount > 1 ? "s" : ""));

		final SwingWorkerWithThrobber<Exception, Server> worker = new SwingWorkerWithThrobber<Exception, Server>(
				throbber) {
			@Override
			protected Exception doInBackground() throws Exception {
				for (int serverStarted = 0; serverStarted < instanceCount; serverStarted++) {

					try {
						final String localServername;

						if (instanceCount == 1) {
							localServername = instancename;
						} else {
							localServername = instancename + " "
									+ serverStarted;
						}

						final Server server = provider.startServer(
								localServername, instancesize, image, tags,
								keypairOrPassword, firewallIds);
						server.refreshUntilServerHasState(VmState.RUNNING);
					} catch (final Exception e) {
						return e;
					}
				}
				return null;
			}

			@Override
			public void done() {
				try {
					final Exception result = get();

					if (result != null) {
						AddServerWizard.log.error("Could not start server",
								result);
						JOptionPane.showMessageDialog(null,
								result.getLocalizedMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} catch (final Exception ignore) {
				}

			}
		};

		worker.execute();

	}
}
