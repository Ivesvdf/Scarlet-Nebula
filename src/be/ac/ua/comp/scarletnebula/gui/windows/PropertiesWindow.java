package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.compute.Platform;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.AllGraphsPanel;
import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;
import be.ac.ua.comp.scarletnebula.gui.ChangeableLabel;
import be.ac.ua.comp.scarletnebula.gui.DecoratedCommunicationPanel;
import be.ac.ua.comp.scarletnebula.gui.LabelEditSwitcherPanel;
import be.ac.ua.comp.scarletnebula.gui.ServernameInputVerifier;
import be.ac.ua.comp.scarletnebula.misc.Executable;
import be.ac.ua.comp.scarletnebula.misc.Utils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class PropertiesWindow extends JDialog
{

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(PropertiesWindow.class);

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
	private JLabel architectureLabel = new JLabel();
	private JLabel platformLabel = new JLabel();

	private boolean statisticsTabIsFilled = false;

	GUI gui;
	Collection<Server> selectedServers;

	private ChangeableLabel sshLabel;

	private DecoratedCommunicationPanel decoratedCommunicationPanel = null;

	public PropertiesWindow(GUI gui, Collection<Server> selectedServers)
	{
		super(gui, true);
		this.selectedServers = selectedServers;
		this.gui = gui;

		setLayout(new BorderLayout());
		setSize(550, 500);
		// This really needs to be here...
		enableEvents(AWTEvent.KEY_EVENT_MASK);

		if (selectedServers.size() > 1)
		{
			setTitle("Server Properties - Scarlet Nebula");
		}
		else
		{
			setTitle(selectedServers.iterator().next().getFriendlyName()
					+ " Properties - Scarlet Nebula");
		}

		add(createTopPartition(selectedServers), BorderLayout.CENTER);

		add(getBottomPanel(), BorderLayout.SOUTH);

		updateOverviewTab(selectedServers);
		setLocationRelativeTo(gui);
		setLocationByPlatform(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private JPanel getBottomPanel()
	{
		JPanel bottomPanel = new JPanel();
		JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				PropertiesWindow.this.dispose();
			}
		});

		getRootPane().setDefaultButton(okButton);

		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(okButton);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		return bottomPanel;
	}

	private JPanel createTopPartition(final Collection<Server> servers)
	{
		final JPanel total = new JPanel();
		total.setLayout(new BorderLayout());

		final JTabbedPane tabbedPane = new JTabbedPane();

		createOverviewPanel(servers);
		createCommunicationPanel();

		tabbedPane.addTab("Overview", overviewTab);
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
					PropertiesWindow.this.communicationTabGotFocus(servers);
				if (selectedPanel == statisticsTab)
					PropertiesWindow.this.statisticsTabGotFocus(servers);
			}

		});

		total.add(tabbedPane);
		return total;
	}

	protected void statisticsTabGotFocus(Collection<Server> servers)
	{
		if (statisticsTabIsFilled)
			return;
		else
		{
			statisticsTabIsFilled = true;
			statisticsTab.setLayout(new BorderLayout());
			statisticsTab.add(getStatisticsPanel(servers), BorderLayout.CENTER);

		}
	}

	private void createCommunicationPanel()
	{
	}

	private void createOverviewPanel(final Collection<Server> servers)
	{
		overviewTab.setLayout(new BorderLayout());

		FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, min(50dlu;p):grow, 7dlu, "
						+ "right:max(40dlu;p), 4dlu, min(50dlu;p):grow", "");
		// add rows dynamically
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.appendSeparator("General Information");

		Component servernameComponent = null;
		Component servertagComponent = null;
		Component sshLoginMethodComponent = null;

		if (servers.size() == 1)
		{
			final Server server = servers.iterator().next();

			servernameComponent = getSingleServerServerNameComponent(server);
			servertagComponent = getSingleServerTagComponent(server);
			sshLoginMethodComponent = getSingleServerSshLoginMethodComponent(server);

		}
		else
		{
			servernameComponent = getMultipleServerServerNameComponent(servers);
			servertagComponent = new JLabel("...");
			sshLoginMethodComponent = new JLabel("...");
		}

		builder.append("Name", servernameComponent);
		builder.append("Tags", servertagComponent);
		builder.nextLine();

		builder.append("SSH Login", sshLoginMethodComponent);
		builder.append("Status", statusLabel);

		builder.nextLine();

		builder.append("Provider", cloudLabel);
		builder.nextLine();

		builder.append("Architecture", architectureLabel);
		builder.append("Platform", platformLabel);
		builder.nextLine();

		builder.append("DNS Address", dnsLabel);

		builder.append("IP Address", ipLabel);
		builder.nextLine();

		builder.appendSeparator("Cloud Specific Information");
		builder.append("Name", unfriendlyNameLabel);
		builder.append("Size", sizeLabel);
		builder.nextLine();

		builder.append("Image", imageLabel);
		builder.nextLine();

		final JScrollPane bodyScrollPane = new JScrollPane(builder.getPanel());
		bodyScrollPane.setBorder(null);
		overviewTab.add(bodyScrollPane);
	}

	private JScrollPane getStatisticsPanel(final Collection<Server> servers)
	{
		JPanel statisticsPanel = new JPanel(new BorderLayout());
		JPanel propertiesPart = new JPanel();
		propertiesPart.setLayout(new BoxLayout(propertiesPart,
				BoxLayout.LINE_AXIS));
		propertiesPart.add(Box.createHorizontalGlue());
		final JButton statisticsPropertiesButton = new JButton("Properties",
				Utils.icon("modify16.png"));
		statisticsPropertiesButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				StatisticsPropertiesWindow win = new StatisticsPropertiesWindow(
						PropertiesWindow.this, servers);
				win.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{

					}
				});
				win.setVisible(true);
			}
		});
		propertiesPart.add(statisticsPropertiesButton);
		propertiesPart.add(Box.createHorizontalStrut(10));
		propertiesPart.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		final JPanel graphCollection = new JPanel(new GridBagLayout());

		if (servers.size() > 1)
			log.fatal("more than one server in statisticspanel");

		Server server = servers.iterator().next();

		AllGraphsPanel allGraphsPanel = new AllGraphsPanel(server);

		JPanel graphCollectionWrapper = new JPanel(new BorderLayout());
		graphCollectionWrapper.setBorder(BorderFactory.createEmptyBorder(10,
				20, 10, 20));
		graphCollectionWrapper.add(allGraphsPanel, BorderLayout.CENTER);
		statisticsPanel.add(propertiesPart, BorderLayout.NORTH);
		statisticsPanel.add(graphCollectionWrapper, BorderLayout.CENTER);

		final JScrollPane scrollPanel = new JScrollPane(statisticsPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return scrollPanel;
	}

	private Component getSingleServerSshLoginMethodComponent(final Server server)
	{
		sshLabel = new ChangeableLabel(
				getTextRepresentationOfSshSituation(server),
				new Executable<JLabel>()
				{
					@Override
					public void run(final JLabel text)
					{
						ChangeServerSshLoginMethodWindow window = new ChangeServerSshLoginMethodWindow(
								PropertiesWindow.this, server);
						window.addActionListener(new ActionListener()
						{
							@Override
							public void actionPerformed(ActionEvent e)
							{
								text.setText(getTextRepresentationOfSshSituation(server));
							}

						});
						window.setVisible(true);
					}
				});
		sshLabel.setBorder(null);
		return sshLabel;
	}

	private String getTextRepresentationOfSshSituation(Server server)
	{
		String rv = "";

		if (server.usesSshPassword())
		{
			rv = "Username & Password";
		}
		else
		{
			rv = "Keypair: " + server.getKeypair();
		}
		return rv;
	}

	private Component getSingleServerTagComponent(final Server server)
	{
		ChangeableLabel tagLabel = new ChangeableLabel(Utils.implode(
				new ArrayList<String>(server.getTags()), ", "),
				new Executable<JLabel>()
				{
					@Override
					public void run(final JLabel label)
					{
						TaggingWindow win = new TaggingWindow(
								PropertiesWindow.this, server.getTags());
						win.addWindowClosedListener(new TaggingWindow.WindowClosedListener()
						{
							@Override
							public void windowClosed(Collection<String> newTags)
							{
								for (String t : newTags)
									log.warn(t);

								label.setText(Utils.implode(
										new ArrayList<String>(newTags), ", "));
								server.setTags(newTags);
								server.store();
							}
						});
						win.setVisible(true);
					}
				});
		return tagLabel;
	}

	private Component getMultipleServerServerNameComponent(
			final Collection<Server> servers)
	{
		Component servernameComponent;
		List<String> names = new ArrayList<String>(servers.size());
		for (Server server : servers)
		{
			names.add(server.getFriendlyName());
		}
		servernameComponent = new JLabel(Utils.implode(names, ", "));
		return servernameComponent;
	}

	private Component getSingleServerServerNameComponent(final Server server)
	{
		JTextField servernameTextField = new JTextField();
		servernameTextField.setInputVerifier(new ServernameInputVerifier(
				servernameTextField));
		final LabelEditSwitcherPanel servername = new LabelEditSwitcherPanel(
				server.getFriendlyName(), servernameTextField);
		servername
				.addContentChangedListener(new LabelEditSwitcherPanel.ContentChangedListener()
				{
					@Override
					public void changed(String newContents)
					{
						server.setFriendlyName(newContents);
						server.store();
					}
				});
		return servername;
	}

	protected void communicationTabGotFocus(Collection<Server> selectedServers)
	{
		if (decoratedCommunicationPanel == null)
		{
			decoratedCommunicationPanel = new DecoratedCommunicationPanel(this,
					selectedServers);

			communicationTab.setLayout(new BorderLayout());
			communicationTab.add(decoratedCommunicationPanel,
					BorderLayout.CENTER);
		}
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

		String ipString = "";

		for (String ip : selectedServer.getPublicIpAddresses())
			ipString += ip + "\n";

		ipLabel.setText(ipString);
		cloudLabel.setText(selectedServer.getCloud().getName());
		sizeLabel.setText(selectedServer.getSize());
		unfriendlyNameLabel.setText(selectedServer.getUnfriendlyName());
		imageLabel.setText(selectedServer.getImage());
		architectureLabel.setText(selectedServer.getArchitecture().toString());
		if (selectedServer.getPlatform() != Platform.UNKNOWN)
			platformLabel.setText(selectedServer.getPlatform().toString());

	}

}
