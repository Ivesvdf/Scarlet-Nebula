package be.ac.ua.comp.scarletnebula.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
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
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.services.server.ServerState;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.SSHCommandConnection;
import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.core.ServerChangedObserver;
import be.ac.ua.comp.scarletnebula.core.ServerDisappearedException;
import be.ac.ua.comp.scarletnebula.core.ServerLinkUnlinkObserver;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.AddServerWizard;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.AddServerWizardDataRecorder;

import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.JCTermSwing;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
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
		Collection<CloudProvider> providers = CloudManager.get()
				.getLinkedCloudProviders();

		for (final CloudProvider prov : providers)
		{
			JMenu providerSpecificSubMenu = new JMenu(prov.getName());
			JMenuItem detectUnlinkedItem = new JMenuItem(
					"Link/Unlink Instances");
			detectUnlinkedItem.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					System.out
							.println("Detecting unlinked instances for provider"
									+ prov.getName());
				}
			});
			providerSpecificSubMenu.add(detectUnlinkedItem);

			providerMenu.add(providerSpecificSubMenu);
		}
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
			final ServerState state)
	{
		refreshUntilServerHasState(server, state, 1);
	}

	private void refreshUntilServerHasState(final Server server,
			final ServerState state, final int attempt)
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

		total.add(tabbedPane);
		return total;
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
				refreshUntilServerHasState(server, ServerState.TERMINATED);
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
		// This really needs to be here...
		enableEvents(AWTEvent.KEY_EVENT_MASK);

		// Remove all components on there
		communicationTab.invalidate();
		communicationTab.removeAll();

		communicationTab.setLayout(new BorderLayout());

		final JCTermSwing term = new JCTermSwing();
		term.setCompression(7);
		term.setAntiAliasing(true);

		term.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		final JPanel communicationPanel = new JPanel();
		communicationPanel.setLayout(new BorderLayout());

		communicationPanel.addComponentListener(new ComponentListener()
		{

			@Override
			public void componentShown(ComponentEvent e)
			{
			}

			@Override
			public void componentResized(ComponentEvent e)
			{
				System.out.println(e);
				Component c = e.getComponent();
				int cw = c.getWidth();
				int ch = c.getHeight();
				System.out.println("ch:" + ch);

				JPanel source = ((JPanel) c);

				int cwm = source.getBorder() != null ? source.getBorder()
						.getBorderInsets(c).left
						+ source.getBorder().getBorderInsets(c).right : 0;
				int chm = source.getBorder() != null ? source.getBorder()
						.getBorderInsets(c).bottom
						+ source.getBorder().getBorderInsets(c).top : 0;
				cw -= cwm;
				ch -= chm;

				System.out.println("ch:" + ch);
				System.out.println("cwm:" + cwm);

				term.setSize(cw, ch);
				term.setPreferredSize(new Dimension(cw, ch));
				// term.setMinimumSize(new Dimension(cw, ch));
				term.setMaximumSize(new Dimension(cw, ch));
			}

			@Override
			public void componentMoved(ComponentEvent e)
			{ // TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent e)
			{ // TODO Auto-generated method stub

			}
		});

		communicationPanel.add(term, BorderLayout.CENTER);

		communicationPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 20, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		communicationTab.add(communicationPanel, BorderLayout.CENTER);

		// For the time being, this only works on one server so pick the last
		// one
		final Server selectedServer = selectedServers.iterator().next();

		class MyUserInfo implements UserInfo, UIKeyboardInteractive
		{
			@Override
			public boolean promptYesNo(String str)
			{
				return true;

				/*
				 * Object[] options = { "yes", "no" }; int foo =
				 * JOptionPane.showOptionDialog(null, str, "Warning",
				 * JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
				 * null, options, options[0]); return foo == 0;
				 */
			}

			String passwd = null;
			String passphrase = null;
			JTextField pword = new JPasswordField(20);

			@Override
			public String getPassword()
			{
				return passwd;
			}

			@Override
			public String getPassphrase()
			{
				return passphrase;
			}

			@Override
			public boolean promptPassword(String message)
			{
				Object[] ob = { pword };
				int result = JOptionPane.showConfirmDialog(null, ob, message,
						JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION)
				{
					passwd = pword.getText();
					return true;
				}
				else
				{
					return false;
				}
			}

			@Override
			public boolean promptPassphrase(String message)
			{
				return true;
			}

			@Override
			public void showMessage(String message)
			{
				JOptionPane.showMessageDialog(null, message);
			}

			final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1,
					1, 1, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
			private Container panel;

			@Override
			public String[] promptKeyboardInteractive(String destination,
					String name, String instruction, String[] prompt,
					boolean[] echo)
			{
				panel = new JPanel();
				panel.setLayout(new GridBagLayout());

				gbc.weightx = 1.0;
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.gridx = 0;
				panel.add(new JLabel(instruction), gbc);
				gbc.gridy++;

				gbc.gridwidth = GridBagConstraints.RELATIVE;

				JTextField[] texts = new JTextField[prompt.length];
				for (int i = 0; i < prompt.length; i++)
				{
					gbc.fill = GridBagConstraints.NONE;
					gbc.gridx = 0;
					gbc.weightx = 1;
					panel.add(new JLabel(prompt[i]), gbc);

					gbc.gridx = 1;
					gbc.fill = GridBagConstraints.HORIZONTAL;
					gbc.weighty = 1;
					if (echo[i])
					{
						texts[i] = new JTextField(20);
					}
					else
					{
						texts[i] = new JPasswordField(20);
					}
					panel.add(texts[i], gbc);
					gbc.gridy++;
				}

				if (JOptionPane.showConfirmDialog(null, panel, destination
						+ ": " + name, JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION)
				{
					String[] response = new String[prompt.length];
					for (int i = 0; i < prompt.length; i++)
					{
						response[i] = texts[i].getText();
					}
					return response;
				}
				else
				{
					return null; // cancel
				}
			}
		}

		Thread connectionThread = new Thread()
		{
			@Override
			public void run()
			{
				SSHCommandConnection commandConnection = (SSHCommandConnection) selectedServer
						.newCommandConnection(new MyUserInfo());
				Connection connection = null;
				try
				{
					connection = commandConnection.getJSchConnection();
				}
				catch (JSchException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				term.requestFocus();
				term.start(connection);
			}
		};

		connectionThread.start();

		// communicationTab.add();
		// communicationTab.add(new JTextArea());
		// communicationTab.add(new JButton("eeeey"));
		communicationTab.validate();
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
		AddServerWizard wizard = new AddServerWizard(this, this);
		wizard.setVisible(true);
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
			refreshUntilServerHasState(server, ServerState.RUNNING);
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
				refreshUntilServerHasState(server, ServerState.PAUSED);
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
