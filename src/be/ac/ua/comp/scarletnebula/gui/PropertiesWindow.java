package be.ac.ua.comp.scarletnebula.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.compute.Platform;
import org.dasein.cloud.compute.VmState;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.misc.Utils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class PropertiesWindow extends JDialog
{

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(PropertiesWindow.class);

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
	private JLabel architectureLabel = new JLabel();
	private JLabel platformLabel = new JLabel();

	GUI gui;
	Collection<Server> selectedServers;

	PropertiesWindow(GUI gui, Collection<Server> selectedServers)
	{
		super(gui, true);
		this.selectedServers = selectedServers;
		this.gui = gui;
		setLayout(new BorderLayout());
		add(createTopPartition(selectedServers), BorderLayout.CENTER);

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

		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(okButton);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		add(bottomPanel, BorderLayout.SOUTH);
		setSize(500, 300);
		setTitle("Server Properties - Scarlet Nebula");
		updateOverviewTab(selectedServers);
		setLocationByPlatform(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private JPanel createTopPartition(Collection<Server> servers)
	{
		final JPanel total = new JPanel();
		total.setLayout(new BorderLayout());

		final JTabbedPane tabbedPane = new JTabbedPane();

		createOverviewPanel(servers);
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
					PropertiesWindow.this.communicationTabGotFocus();
			}

		});

		total.add(tabbedPane);
		return total;
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

		if (servers.size() == 1)
		{
			final Server server = servers.iterator().next();

			servernameComponent = getSingleServerServerNameComponent(server);
			servertagComponent = getSingleServerTagComponent(server);
		}
		else
		{
			servernameComponent = getMultipleServerServerNameComponent(servers);
		}

		builder.append("Name", servernameComponent);
		builder.append("Tags", servertagComponent);
		builder.nextLine();

		builder.append("Status", statusLabel);
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

		overviewTab.add(builder.getPanel());
	}

	private Component getSingleServerTagComponent(final Server server)
	{
		ChangeableLabel tagLabel = new ChangeableLabel(Utils.implode(
				new ArrayList<String>(server.getTags()), ", "),
				new ChangeableLabel.Executable<JLabel>()
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
		Component servernameComponent;
		final LabelEditSwitcherPanel servername = new LabelEditSwitcherPanel(
				server.getFriendlyName());
		servername.setInputVerifier(new ServernameInputVerifier());
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
		servernameComponent = servername;
		return servernameComponent;
	}

	protected void communicationTabGotFocus()
	{
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
