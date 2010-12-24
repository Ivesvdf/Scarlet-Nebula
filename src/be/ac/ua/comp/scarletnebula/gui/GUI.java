package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.Server;

public class GUI extends JFrame implements ListSelectionListener
{
	private static final long serialVersionUID = 1L;
	private JList serverList;
	private ServerListModel serverListModel;
	private CloudManager cloudManager;

	private JPanel configurationTab;
	private JPanel overviewTab;
	private JPanel statisticsTab;
	private JPanel communicationTab;

	private JLabel statusLabel;
	private JLabel dnsLabel;
	private JLabel ipLabel;

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

		cloudManager = new CloudManager();

		JPanel leftPartition = setupLeftPartition();
		JPanel rightPartition = setupRightPartition();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPartition, rightPartition);
		splitPane.setDividerSize(4);
		splitPane.setDividerLocation(160);
		add(splitPane);

		setTitle("Scarlet Nebula");
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		addInitialServers();
	}

	private JPanel setupRightPartition()
	{
		JPanel total = new JPanel();
		total.setLayout(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane();

		overviewTab = new JPanel();
		configurationTab = new JPanel();
		communicationTab = new JPanel();
		statisticsTab = new JPanel();

		ImageIcon stopIcon = new ImageIcon(getClass().getResource(
				"/images/stop.png"));
		JButton terminateButton = new JButton("Terminate Server", stopIcon);
		terminateButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				terminateSelectedServers();
			}
		});
		configurationTab.add(terminateButton);

		overviewTab.setLayout(new BorderLayout());

		FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, min(50dlu;p):grow, 7dlu, "
						+ "right:max(40dlu;p), 4dlu, min(50dlu;p):grow", "");
		// add rows dynamically
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.appendSeparator("Server Information");

		statusLabel = new JLabel();
		builder.append("Status", statusLabel);
		builder.nextLine();

		dnsLabel = new JLabel();
		builder.append("DNS Address", dnsLabel);

		ipLabel = new JLabel();
		builder.append("IP Address", ipLabel);

		builder.nextLine();

		builder.append("PTI", new JTextField());
		builder.append("Power", new JTextField());

		overviewTab.add(builder.getPanel());

		tabbedPane.addTab("Overview", overviewTab);
		tabbedPane.addTab("Configuration", configurationTab);
		tabbedPane.addTab("Communication", communicationTab);
		tabbedPane.addTab("Statistics", statisticsTab);

		total.add(tabbedPane);
		return total;
	}

	protected void terminateSelectedServers()
	{
		int indices[] = serverList.getSelectedIndices();
		Collection<Server> servers = serverListModel
				.getVisibleServersAtIndices(indices);

		for (Server server : servers)
		{
			try
			{
				server.terminate();
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
		serverList = new JList(serverListModel);
		serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		serverList.setSelectedIndex(0);
		serverList.addListSelectionListener(this);
		// serverList.setVisibleRowCount(5);
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
				searchField);
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
		// In a boxlayout, this is apparently computed from their maximum widths.
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
		Collection<Server> servers = serverListModel.getVisibleServersAtIndices(serverList.getSelectedIndices());
		
		for(Server server : servers)
		{
			try
			{
				server.refresh();
				fillRightPartition(server);
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

	private void addInitialServers()
	{
		Collection<CloudProvider> providers = cloudManager
				.getLinkedCloudProviders();

		for (CloudProvider prov : providers)
		{
			try
			{
				Collection<Server> servers = prov.loadLinkedServers();

				if (servers == null)
					return;

				for (Server s : servers)
				{
					serverListModel.addServer(s);
				}
			}
			catch (InternalException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (CloudException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting() == false)
		{
			int indices[] = serverList.getSelectedIndices();

			for (int index : indices)
			{
				Server selectedServer = serverListModel
						.getVisibleServerAtIndex(index);
				fillRightPartition(selectedServer);
			}
		}
	}

	private void fillRightPartition(Server selectedServer)
	{
		statusLabel.setText(selectedServer.getStatus().toString());
		dnsLabel.setText(selectedServer.getPublicDnsAddress());

		String ipString = new String();

		for (String ip : selectedServer.getPublicIpAddresses())
			ipString += ip + "\n";

		ipLabel.setText(ipString);
	}

	void startAddServerWizard()
	{
		AddServerWizard wizard = new AddServerWizard(this, cloudManager, this);
		wizard.setVisible(true);
	}

	class SearchFieldListener implements ActionListener, DocumentListener
	{
		private JTextField searchField;

		public SearchFieldListener(JTextField inputSearchField)
		{
			this.searchField = inputSearchField;
		}

		// Search on ENTER press
		public void actionPerformed(ActionEvent e)
		{
			String servername = searchField.getText();

			// listModel.insertElementAt(searchField.getText(), index);
			// If we just wanted to add to the end, we'd do this:
			serverListModel.filter(servername);

			// Reset the text field.
			// searchField.requestFocusInWindow();
			searchField.setText("");

		}

		// Required by DocumentListener.
		@Override
		public void removeUpdate(DocumentEvent e)
		{
		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			// TODO Auto-generated method stub

		}
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				GUI ex = new GUI();
				ex.setVisible(true);
			}
		});
	}

	public void addWizardClosed(AddServerWizard wiz)
	{
		final String instancename = wiz.instancename;
		final String instancesize = wiz.instancesize;
		final CloudProvider provider = wiz.cloudProvider;

		try
		{
			Server server = provider.startServer(instancename, instancesize);
			serverListModel.addServer(server);
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
