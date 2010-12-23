package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
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

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.Server;


public class GUI extends JFrame implements ListSelectionListener
{

	private JList serverList;
	private DefaultListModel serverListModel;
	private CloudManager cloudManager;

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

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPartition, rightPartition);
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
		JPanel overview = new JPanel();
		overview.add(new JButton("ey"));
		overview.removeAll();
		
		JPanel configuration = new JPanel();
		JPanel communication = new JPanel();
		
		tabbedPane.addTab("Overview", overview);
		tabbedPane.addTab("Configuration", configuration);
		tabbedPane.addTab("Communication", communication);
	
		total.add(tabbedPane);
		return total;
	}

	private JPanel setupLeftPartition()
	{
		// Create the list and put it in a scroll pane.
		serverListModel = new DefaultListModel();
		serverList = new JList(serverListModel);
		serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		serverList.setSelectedIndex(0);
		serverList.addListSelectionListener(this);
		//serverList.setVisibleRowCount(5);
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

		JTextField searchField = new JTextField(10);
		SearchFieldListener searchFieldListener = new SearchFieldListener(searchField);
		searchField.addActionListener(searchFieldListener);
		searchField.getDocument().addDocumentListener(searchFieldListener);

		JPanel topLeftPane = new JPanel();
		topLeftPane.setLayout(new BoxLayout(topLeftPane, BoxLayout.LINE_AXIS));
		topLeftPane.add(searchField);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		
		leftPanel.add(topLeftPane, BorderLayout.PAGE_START);
		leftPanel.add(serverScrollPane, BorderLayout.CENTER);
		leftPanel.add(addButton, BorderLayout.PAGE_END);
		
		return leftPanel;
	}

	private void addInitialServers()
	{
		Collection<CloudProvider> providers = cloudManager.getLinkedCloudProviders();
		
		for(CloudProvider prov : providers)
		{
			try
			{
				Collection<Server> servers = prov.loadLinkedServers();
				
				if(servers == null)
					return;
				
				for(Server s : servers)
				{
					String servername = null;
					if(s.getFriendlyName() != null)
						servername = s.getFriendlyName();
					else
						servername = s.getUnfriendlyName();
					
					serverListModel.addElement(servername);
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

			/*
			 * if (list.getSelectedIndex() == -1) { // No selection, disable
			 * fire button. fireButton.setEnabled(false);
			 * 
			 * } else { // Selection, enable the fire button.
			 * fireButton.setEnabled(true); }
			 */
		}
	}
	
	void startAddServerWizard()
	{
		  AddServerWizard test = new AddServerWizard(this, cloudManager, this);
	      test.setVisible(true);	
	}
	
	class SearchFieldListener implements ActionListener, DocumentListener
	{
		private boolean alreadyEnabled = false;
		private JTextField searchField;

		public SearchFieldListener(JTextField inputSearchField)
		{
			this.searchField = inputSearchField;
		}

		// Search on ENTER press
		public void actionPerformed(ActionEvent e)
		{
			String servername = searchField.getText();
			
			serverListModel.clear();
			
			//listModel.insertElementAt(searchField.getText(), index);
			// If we just wanted to add to the end, we'd do this:
			serverListModel.addElement(servername);

			// Reset the text field.
			searchField.requestFocusInWindow();
			searchField.setText("");

		}

		// This method tests for string equality. You could certainly
		// get more sophisticated about the algorithm. For example,
		// you might want to ignore white space and capitalization.
		protected boolean alreadyInList(String name)
		{
			return serverListModel.contains(name);
		}

		// Required by DocumentListener.
		public void insertUpdate(DocumentEvent e)
		{
			enableButton();
		}

		// Required by DocumentListener.
		public void removeUpdate(DocumentEvent e)
		{
			handleEmptyTextField(e);
		}

		// Required by DocumentListener.
		public void changedUpdate(DocumentEvent e)
		{
			if (!handleEmptyTextField(e))
			{
				enableButton();
			}
		}

		private void enableButton()
		{
			if (!alreadyEnabled)
			{
				searchField.setEnabled(true);
			}
		}

		private boolean handleEmptyTextField(DocumentEvent e)
		{
			if (e.getDocument().getLength() <= 0)
			{
				searchField.setEnabled(false);
				alreadyEnabled = false;
				return true;
			}
			return false;
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
			provider.addServer(instancename, instancesize);
			serverListModel.addElement(instancename);
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
