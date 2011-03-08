package be.ac.ua.comp.scarletnebula.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.VmState;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.core.ServerChangedObserver;
import be.ac.ua.comp.scarletnebula.core.ServerDisappearedException;
import be.ac.ua.comp.scarletnebula.core.ServerLinkUnlinkObserver;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizard;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.AddServerWizard;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.AddServerWizardDataRecorder;
import be.ac.ua.comp.scarletnebula.gui.welcomewizard.WelcomeWizard;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class GUI extends JFrame implements ListSelectionListener,
		ServerChangedObserver, ServerLinkUnlinkObserver
{
	private static Log log = LogFactory.getLog(GUI.class);

	private static final long serialVersionUID = 1L;
	private ServerList serverList;
	private ServerListModel serverListModel;

	private JPanel configurationTab = new JPanel();
	private JPanel overviewTab = new JPanel();
	private JPanel statisticsTab = new JPanel();
	private JPanel communicationTab = new JPanel();

	private JLabel statusLabel = new JLabel();
	private JLabel dnsLabel = new JLabel();
	private JLabel ipLabel = new JLabel();
	private JLabel cloudLabel = new JLabel();
	private JLabel unfriendlyNameLabel = new JLabel();
	private JLabel sizeLabel = new JLabel();
	private JLabel imageLabel = new JLabel();

	private Statusbar statusbar = new Statusbar();

	public GUI()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedLookAndFeelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JPanel leftPartition = setupLeftPartition();
		JPanel rightPartition = setupRightPartition();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPartition, rightPartition);
		splitPane.setDividerSize(4);
		splitPane.setDividerLocation(160);

		// setLayout(new BorderLayout());
		add(splitPane);
		add(statusbar, BorderLayout.SOUTH);

		adjustStatusbar();

		setTitle("Scarlet Nebula");
		setSize(700, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		ImageIcon icon = new ImageIcon(getClass().getResource(
				"/images/icon48.png"));
		setIconImage(icon.getImage());

		addMenubar();

		// Last but not least, we construct a cloudmanager object, which will
		// cause it to load all providers and thus servers.
		// We also register ourselves as an observer for linking and unlinking
		// servers so we can update the serverlist.
		CloudManager.get().addServerLinkUnlinkObserver(this);
		try
		{
			CloudManager.get().loadAllLinkedServers();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// If there are no linked cloudproviders, start the new provider wizard
		if (CloudManager.get().getLinkedCloudProviders().size() == 0)
		{
			Thread t = new Thread()
			{
				@Override
				public void run()
				{
					new WelcomeWizard(GUI.this);
				}
			};

			t.start();
		}
	}

	private void adjustStatusbar()
	{
		statusbar.setText("x servers running");
	}

	private void addMenubar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu providerMenu = new JMenu("Providers");
		providerMenu.setMnemonic(KeyEvent.VK_P);
		providerMenu.getAccessibleContext().setAccessibleDescription(
				"Managing cloud providers.");

		JMenuItem manageProvidersItem = new JMenuItem("Manage Providers");
		manageProvidersItem.setMnemonic(KeyEvent.VK_M);
		manageProvidersItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new ManageProvidersWindow(GUI.this);
			}
		});

		providerMenu.add(manageProvidersItem);

		JMenuItem detectAllUnlinkedInstances = new JMenuItem(
				"Link/Unlink Instances");
		detectAllUnlinkedInstances.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new LinkUnlinkWindow(GUI.this);
				// detectAllUnlinkedInstances();
			}
		});
		providerMenu.add(detectAllUnlinkedInstances);

		menuBar.add(providerMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);

		// Pick a random message to display in the help menu
		String messages[] = { "(You won't find any help here)",
				"(Nobody can help you)", "(Keep on lookin' if you need help)",
				"(Heeeeelp!)", "(You might want to try google for help)",
				"(Try yelling loudly if you need help)" };

		Random generator = new Random(System.currentTimeMillis());

		JMenuItem noHelpItem = new JMenuItem(
				messages[generator.nextInt(messages.length)]);
		noHelpItem.setEnabled(false);
		helpMenu.add(noHelpItem);

		JMenuItem aboutItem = new JMenuItem("About...");
		aboutItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				openAboutBox();
			}
		});

		helpMenu.add(aboutItem);

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	protected void clearSelection()
	{
		serverList.clearSelection();
		fillRightPartition();
	}

	/**
	 * The method you should call when you want to keep refreshing until Server
	 * "server" has state "state".
	 * 
	 * TODO Keep some kind of a map for each state, which can be checked whem
	 * manually refreshing. Suppose a server is refreshed and it's state is
	 * PAUSED. The user can then resume. Later, the server's timer that checks
	 * server state will fire, and the state will show as RUNNING. The timer
	 * will keep on firing until the count is high enough and it gives up, which
	 * sucks. Therefore, when manually refreshing a server S, all timers for
	 * that server should be checked. If there's a timer waiting for S's current
	 * state, that timer should be cancelled.
	 * 
	 * @param server
	 * @param state
	 */
	private void refreshUntilServerHasState(final Server server,
			final VmState state)
	{
		refreshUntilServerHasState(server, state, 1);
	}

	private void refreshUntilServerHasState(final Server server,
			final VmState state, final int attempt)
	{
		if (server.getStatus() == state || attempt > 20)
			return;

		try
		{
			server.refresh();
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
		catch (ServerDisappearedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (server.getStatus() == state)
			return;

		// If the server's state still isn't the one we want it to be, try
		// again, but only after waiting
		// a logarithmic amount of time.
		double wait = 15.0 * (Math.log10(attempt) + 1.0);

		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new java.util.TimerTask()
		{
			@Override
			public void run()
			{
				refreshUntilServerHasState(server, state, attempt + 1);
				log.debug("Refreshing state for server "
						+ server.getFriendlyName()
						+ " because timer fired, waiting for state "
						+ state.toString());
				cancel();
			}
		}, (long) (wait * 1000));

	}

	// TODO: Remove this ftion?
	protected void detectAllUnlinkedInstances()
	{
		Collection<CloudProvider> providers = CloudManager.get()
				.getLinkedCloudProviders();

		for (final CloudProvider prov : providers)
		{
			try
			{
				Collection<Server> unlinkedServers = prov.listUnlinkedServers();
				log.debug("Provider " + prov.getName() + " has "
						+ unlinkedServers.size() + " unlinked instances.");

				for (Server server : unlinkedServers)
				{
					prov.linkUnlinkedServer(server);
					addServer(server);
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		fillRightPartition();
	}

	private void openAboutBox()
	{
		AboutWindow aboutWindow = new AboutWindow(this);
		aboutWindow.setVisible(true);
	}

	private JPanel setupRightPartition()
	{
		JPanel total = new JPanel();
		total.setLayout(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane();

		/*
		 * ImageIcon stopIcon = new ImageIcon(getClass().getResource(
		 * "/images/stop.png")); JButton terminateButton = new
		 * JButton("Terminate Server", stopIcon);
		 * terminateButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * terminateSelectedServers(); } });
		 * configurationTab.add(terminateButton);
		 */

		createOverviewPanel();
		createCommunicationPanel();

		tabbedPane.addTab("Overview", overviewTab);
		tabbedPane.addTab("Configuration", configurationTab);
		tabbedPane.addTab("Communication", communicationTab);
		tabbedPane.addTab("Statistics", statisticsTab);

		tabbedPane.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				JTabbedPane tabSource = (JTabbedPane) e.getSource();
				JPanel selectedPanel = (JPanel) tabSource
						.getSelectedComponent();

				if (selectedPanel == communicationTab)
					GUI.this.communicationTabGotFocus();
			}

		});

		total.add(tabbedPane);
		return total;
	}

	protected void communicationTabGotFocus()
	{
		Collection<Server> selectedServers = getSelectedServers();

		// This really needs to be here...
		enableEvents(AWTEvent.KEY_EVENT_MASK);

		// Remove all components on there
		communicationTab.invalidate();
		communicationTab.removeAll();

		communicationTab.setLayout(new BorderLayout());

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
			txt.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			communicationTab.add(txt, BorderLayout.NORTH);
			communicationTab.validate();
			communicationTab.repaint();
			return;
		}

		final Server connectServer = selectedServers.iterator().next();

		communicationTab.add(new SSHPanel(connectServer), BorderLayout.CENTER);

		communicationTab.validate();
		communicationTab.repaint();

	}

	private void createCommunicationPanel()
	{
	}

	private void createOverviewPanel()
	{
		overviewTab.setLayout(new BorderLayout());

		FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, min(50dlu;p):grow, 7dlu, "
						+ "right:max(40dlu;p), 4dlu, min(50dlu;p):grow", "");
		// add rows dynamically
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.appendSeparator("General Information");

		builder.append("Status", statusLabel);
		builder.append("Provider", cloudLabel);
		builder.nextLine();

		builder.append("DNS Address", dnsLabel);
		builder.append("IP Address", ipLabel);
		builder.nextLine();

		builder.appendSeparator("Cloud Specific Information");
		builder.append("Name", unfriendlyNameLabel);
		builder.append("Size", sizeLabel);
		builder.nextLine();

		builder.append("Image", imageLabel);

		overviewTab.add(builder.getPanel());
	}

	protected void terminateSelectedServers()
	{
		Collection<Server> servers = getSelectedServers();

		for (Server server : servers)
		{
			try
			{
				server.terminate();
				refreshUntilServerHasState(server, VmState.TERMINATED);
			}
			catch (CloudException e)
			{
				e.printStackTrace();
			}
			catch (InternalException e)
			{
				e.printStackTrace();
			}
		}
	}

	private JPanel setupLeftPartition()
	{
		// Create the list and put it in a scroll pane.
		serverListModel = new ServerListModel();
		serverList = new ServerList(serverListModel);
		serverList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		serverList.addMouseListener(new ServerListMouseListener(this,
				serverListModel));
		serverList.addListSelectionListener(this);

		JScrollPane serverScrollPane = new JScrollPane(serverList);

		ImageIcon addIcon = new ImageIcon(getClass().getResource(
				"/images/add.png"));

		JButton addButton = new JButton(addIcon);
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				startAddServerWizard();
			}
		});
		addButton.setBounds(10, 10, addIcon.getIconWidth(),
				addIcon.getIconHeight());

		ImageIcon refreshIcon = new ImageIcon(getClass().getResource(
				"/images/refresh.png"));
		JButton refreshButton = new JButton(refreshIcon);
		refreshButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				refreshSelectedServers();
			}
		});
		refreshButton.setBounds(10, 10, refreshIcon.getIconWidth(),
				refreshIcon.getIconHeight());

		JTextField searchField = new JTextField(10);
		SearchFieldListener searchFieldListener = new SearchFieldListener(
				searchField, serverListModel);
		searchField.addActionListener(searchFieldListener);
		searchField.getDocument().addDocumentListener(searchFieldListener);

		JPanel topLeftPane = new JPanel();
		topLeftPane.setLayout(new BoxLayout(topLeftPane, BoxLayout.LINE_AXIS));
		topLeftPane.add(searchField);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());

		leftPanel.add(topLeftPane, BorderLayout.PAGE_START);
		leftPanel.add(serverScrollPane, BorderLayout.CENTER);

		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));

		// The button need to take up the full width of the bar on the left
		// In a boxlayout, this is apparently computed from their maximum
		// widths.
		addButton.setMaximumSize(new Dimension(50000000, 500));
		refreshButton.setMaximumSize(new Dimension(50000000, 500));

		bottom.add(addButton, BorderLayout.WEST);
		bottom.add(Box.createHorizontalGlue());
		bottom.add(refreshButton, BorderLayout.EAST);
		leftPanel.add(bottom, BorderLayout.PAGE_END);

		return leftPanel;
	}

	protected void refreshSelectedServers()
	{
		int indices[] = serverList.getSelectedIndices();

		Collection<Server> servers = serverListModel
				.getVisibleServersAtIndices(indices);

		fillRightPartition();

		for (Server server : servers)
		{
			try
			{
				server.refresh();
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
			catch (ServerDisappearedException e)
			{
				log.info(e.toString());
				removeServer(server);
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting() == false)
		{
			fillRightPartition();
		}
	}

	private void fillRightPartition()
	{
		int selectedIndex = serverList.getSelectedIndex();

		// When nothing is selected, return

		log.debug("Filling right partition");
		Collection<Server> selectedServers = new ArrayList<Server>();

		if (selectedIndex >= 0)
			selectedServers.add(serverListModel
					.getVisibleServerAtIndex(selectedIndex));

		updateOverviewTab(selectedServers);
		updateCommunicationTab(selectedServers);
	}

	private void updateCommunicationTab(Collection<Server> selectedServers)
	{

	}

	private void updateOverviewTab(Collection<Server> selectedServers)
	{
		if (selectedServers.size() == 0)
		{
			log.info("No selected servers. Not filling overview tab.");
			statusLabel.setText("");
			dnsLabel.setText("");
			ipLabel.setText("");
			cloudLabel.setText("");
			sizeLabel.setText("");
			unfriendlyNameLabel.setText("");
			imageLabel.setText("");
			return;
		}

		// Until multiple selected servers are supported, pick the last server
		Server selectedServer = null;

		for (Server s : selectedServers)
			selectedServer = s;

		statusLabel.setText(selectedServer.getStatus().toString());
		dnsLabel.setText(selectedServer.getPublicDnsAddress());

		String ipString = new String();

		for (String ip : selectedServer.getPublicIpAddresses())
			ipString += ip + "\n";

		ipLabel.setText(ipString);
		cloudLabel.setText(selectedServer.getCloud().getName());
		sizeLabel.setText(selectedServer.getSize());
		unfriendlyNameLabel.setText(selectedServer.getUnfriendlyName());
		imageLabel.setText(selectedServer.getImage());
	}

	void startAddServerWizard()
	{
		if (CloudManager.get().getLinkedCloudProviders().size() == 0)
		{
			JOptionPane
					.showMessageDialog(
							this,
							"A CloudProvider account is required to add a new server, \n"
									+ "and there don't seem to be any. Please add one before continuing.",
							"No CloudProvider accounts found",
							JOptionPane.ERROR_MESSAGE);

			AddProviderWizard wiz = new AddProviderWizard();
			wiz.startModal(null);
			return;
		}
		new AddServerWizard(this, this);
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				GUI ex = new GUI();
				ex.setVisible(true);
			}
		});
	}

	public void error(String errorMessage)
	{
		JOptionPane.showMessageDialog(this, errorMessage,
				"An unexpected error occured.", JOptionPane.ERROR_MESSAGE);
	}

	public void addServerWizardClosed(AddServerWizardDataRecorder rec)
	{
		final String instancename = rec.instanceName;
		final String instancesize = rec.instanceSize;
		final CloudProvider provider = rec.provider;

		if (Server.exists(instancename))
		{
			JOptionPane.showMessageDialog(this,
					"A server with this name already exists.",
					"Server already exists", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try
		{
			Server server = provider.startServer(instancename, instancesize);
			refreshUntilServerHasState(server, VmState.RUNNING);
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

	private void addServer(Server server)
	{
		serverListModel.addServer(server);
		server.addServerChangedObserver(this);
	}

	@Override
	public void serverChanged(Server server)
	{
		// Update the list on the left
		serverListModel.refreshServer(server);
		fillRightPartition();
	}

	public void pauseSelectedServers()
	{
		Collection<Server> selectedServers = getSelectedServers();

		try
		{
			for (Server server : selectedServers)
			{
				server.pause();
				refreshUntilServerHasState(server, VmState.PAUSED);
			}
		}
		catch (CloudException e)
		{
			error(e.getMessage());
		}
		catch (InternalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void rebootSelectedServers()
	{
		Collection<Server> selectedServers = getSelectedServers();

		try
		{
			for (Server server : selectedServers)
				server.reboot();
		}
		catch (CloudException e)
		{
			e.printStackTrace();
		}
		catch (InternalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Collection<Server> getSelectedServers()
	{
		int indices[] = serverList.getSelectedIndices();
		return serverListModel.getVisibleServersAtIndices(indices);
	}

	public void unlinkSelectedServers()
	{
		Collection<Server> selectedServers = getSelectedServers();

		for (Server server : selectedServers)
		{
			// Server will be automatically removed from the view on the left
			// because of the hooked observers
			server.unlink();
		}

		fillRightPartition();
	}

	private void removeServer(Server server)
	{
		serverListModel.removeServer(server);
	}

	@Override
	public void serverLinked(CloudProvider cloudProvider, Server srv)
	{
		addServer(srv);
	}

	@Override
	public void serverUnlinked(CloudProvider cloudProvider, Server srv)
	{
		removeServer(srv);
	}
}
