package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.network.Firewall;
import org.dasein.cloud.network.FirewallRule;
import org.dasein.cloud.network.Protocol;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.windows.AddFirewallRuleWindow;
import be.ac.ua.comp.scarletnebula.gui.windows.AddFirewallRuleWindow.AddFirewallRuleWindowClosedListener;
import be.ac.ua.comp.scarletnebula.misc.SwingWorkerWithThrobber;
import be.ac.ua.comp.scarletnebula.misc.Utils;

public class InteractiveFirewallPanel extends JPanel implements
		AddFirewallRuleWindowClosedListener {
	private final class AddRuleSwingWorker extends
			SwingWorkerWithThrobber<Exception, Object> {
		private final String cidr;
		private final int beginPort;
		private final Protocol protocol;
		private final int endPort;
		private final Firewall firewall;

		private AddRuleSwingWorker(final Collapsable throbber,
				final String cidr, final int beginPort,
				final Protocol protocol, final int endPort,
				final Firewall firewall) {
			super(throbber);
			this.cidr = cidr;
			this.beginPort = beginPort;
			this.protocol = protocol;
			this.endPort = endPort;
			this.firewall = firewall;
		}

		@Override
		protected Exception doInBackground() throws Exception {
			try {
				provider.addFirewallRule(firewall, beginPort, endPort,
						protocol, cidr);
			} catch (final Exception e) {
				return e;
			}
			return null;
		}

		@Override
		public void done() {
			try {
				final Exception result = get();

				if (result == null) {
					// Succeeded
					ruleList.addRule(new FirewallRule(firewall
							.getProviderFirewallId(), cidr, protocol,
							beginPort, endPort));
				} else {
					log.error("Exception was thrown while creating new Rule",
							result);
					JOptionPane.showMessageDialog(
							InteractiveFirewallPanel.this,
							result.getLocalizedMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (final Exception ignore) {

			}
		}
	}

	public static class PortRange {
		final public int startPort;
		final public int endPort;

		public PortRange(final String range) {
			final String portString = range;
			final String portParts[] = portString.split("-");
			final Collection<Integer> ports = new ArrayList<Integer>(
					portParts.length);

			for (final String portPart : portParts) {
				ports.add(Integer.decode(portPart));
			}

			startPort = Utils.min(ports);
			endPort = Utils.max(ports);
		}
	}

	private final class DeleteRuleActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			for (final Firewall firewall : getSelectedFirewalls()) {
				(new SwingWorker<Object, Object>() {
					@Override
					protected Object doInBackground() throws Exception {
						final int selectedRow = ruleList.getSelectedRow();
						final PortRange range = new PortRange(
								ruleList.getPort(selectedRow));
						final Protocol protocol = ruleList
								.getProtocol(selectedRow);
						final String ip = ruleList.getSource(selectedRow);

						ruleList.datamodel.removeRow(selectedRow);
						provider.deleteFirewallRule(firewall, range.startPort,
								range.endPort, protocol, ip);
						return null;
					}
				}).execute();
			}
		}
	}

	private final class AddRuleActionListener implements ActionListener {
		private final CloudProvider provider;

		private AddRuleActionListener(final CloudProvider provider) {
			this.provider = provider;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Collection<Firewall> firewalls = getSelectedFirewalls();
			if (firewalls.size() != 1) {
				return;
			}

			final AddFirewallRuleWindow win = new AddFirewallRuleWindow(
					(JDialog) Utils.findWindow(InteractiveFirewallPanel.this),
					provider, firewalls.iterator().next());
			win.addAddFirewallRuleWindowClosed(InteractiveFirewallPanel.this);
			win.setVisible(true);
		}
	}

	private final class AddFirewallActionListener implements ActionListener {
		private final CloudProvider provider;

		private AddFirewallActionListener(final CloudProvider provider) {
			this.provider = provider;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			boolean invalid = true;

			String name = "";
			while (invalid) {
				name = JOptionPane.showInputDialog(
						InteractiveFirewallPanel.this,
						"Enter a name for the new Firewall:", "New firewall",
						JOptionPane.QUESTION_MESSAGE);

				if (name == null) {
					return;
				} else {
					if (Pattern.matches("[a-zA-Z0-9 ]+", name)) {
						invalid = false;
					} else {
						JOptionPane
								.showMessageDialog(
										InteractiveFirewallPanel.this,
										"A firewall name can only contain letters, numbers and spaces.",
										"Incorrect Firewallname",
										JOptionPane.ERROR_MESSAGE);
					}
				}
			}

			final String firewallName = name;

			(new NewFirewallSwingWorkerWithThrobber(
					creatingFirewallThrobberPanel, firewallName, provider))
					.execute();

		}
	}

	private final class DeleteFirewallActionListener implements ActionListener {
		private final CloudProvider provider;

		private DeleteFirewallActionListener(final CloudProvider provider) {
			this.provider = provider;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final String selectedFirewall = (String) firewallList
					.getSelectedValue();

			final int result = JOptionPane.showOptionDialog(
					InteractiveFirewallPanel.this,
					"Are you sure you want to remove the firewall named "
							+ selectedFirewall + "?", "Remove firewall?",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
					null, new Object[] { "Delete Firewall", "Cancel" },
					"Cancel");

			if (result != JOptionPane.OK_OPTION) {
				return;
			}

			firewallListModel.removeElement(selectedFirewall);

			(new DeleteFirewallSwingWorker(selectedFirewall, provider))
					.execute();

		}
	}

	private final class LoadFirewallsWorkerWithThrobber extends
			SwingWorkerWithThrobber<Exception, Firewall> {
		private LoadFirewallsWorkerWithThrobber(final Collapsable throbber) {
			super(throbber);
		}

		@Override
		protected Exception doInBackground() throws Exception {
			try {
				for (final Firewall firewall : provider.getFirewalls()) {
					publish(firewall);
				}
			} catch (final Exception e) {
				return e;
			}
			return null;
		}

		@Override
		public void process(final List<Firewall> input) {
			for (final Firewall firewall : input) {
				addFirewall(firewall);
			}
		}

		@Override
		public void done() {
			try {
				final Exception result = get();

				if (result != null) {
					log.error("Something went wrong while querying firewalls",
							result);
					JOptionPane.showInternalMessageDialog(
							InteractiveFirewallPanel.this,
							result.getLocalizedMessage());
				}
			} catch (final Exception ignore) {
			}
		}
	}

	private final class NewFirewallSwingWorkerWithThrobber extends
			SwingWorkerWithThrobber<Exception, Firewall> {
		private final String firewallName;
		private final CloudProvider provider;

		private NewFirewallSwingWorkerWithThrobber(final Collapsable throbber,
				final String firewallName, final CloudProvider provider) {
			super(throbber);
			this.firewallName = firewallName;
			this.provider = provider;
		}

		@Override
		protected Exception doInBackground() throws Exception {
			try {
				final Firewall fw = provider.createFirewall(firewallName);
				publish(fw);
			} catch (final Exception e) {
				return e;
			}
			return null;
		}

		@Override
		public void process(final List<Firewall> theFirewall) {
			// There will only be 1 firewall in theFirewall...
			for (final Firewall fw : theFirewall) {
				addFirewall(fw);
				addRule(fw, 22, 22, Protocol.TCP, "0.0.0.0/0");
			}

			firewallList.setSelectedIndex(firewallListModel.getSize() - 1);
		}
	}

	private final class DeleteFirewallSwingWorker extends
			SwingWorker<Object, Object> {
		private final String selectedFirewall;
		private final CloudProvider provider;

		private DeleteFirewallSwingWorker(final String selectedFirewall,
				final CloudProvider provider) {
			this.selectedFirewall = selectedFirewall;
			this.provider = provider;
		}

		@Override
		protected Exception doInBackground() throws Exception {
			try {
				for (final Firewall firewall : firewalls) {
					if (firewall.getName().equals(selectedFirewall)) {
						provider.deleteFirewall(firewall);
						break;
					}
				}
			} catch (final Exception e) {
				return e;
			}
			return null;
		}
	}

	public class FirewallList extends JList {
		private static final long serialVersionUID = 1L;

		public FirewallList(final ListModel firewallListModel) {
			super(firewallListModel);
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}

	}

	private final class FirewallListSelectionListener implements
			ListSelectionListener {

		private final RuleList ruleList;
		private final JButton deleteRuleButton;
		private final JButton deleteFirewallButton;

		public FirewallListSelectionListener(final RuleList ruleList,
				final JButton addRuleButton, final JButton deleteRuleButton,
				final JButton deleteFirewallButton) {
			this.ruleList = ruleList;
			this.deleteRuleButton = deleteRuleButton;
			this.deleteFirewallButton = deleteFirewallButton;
		}

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			final ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			final int minIndex = lsm.getMinSelectionIndex();

			if (e.getValueIsAdjusting()) {
				return;
			}

			if (minIndex == -1) {
				ruleList.clear();
				ruleList.setEnabled(false);
				addRulebutton.setEnabled(false);
				deleteRuleButton.setEnabled(false);
				deleteFirewallButton.setEnabled(false);
			} else {
				ruleList.setEnabled(true);
				addRulebutton.setEnabled(true);
				deleteRuleButton.setEnabled(true);
				deleteFirewallButton.setEnabled(true);

				final String firewallName = (String) firewallListModel
						.getElementAt(minIndex);

				for (final Firewall firewall : firewalls) {
					if (firewall.getName().equals(firewallName)) {
						ruleList.displayFirewall(firewall);
						break;
					}
				}
			}
		}
	}

	public class RuleList extends JTable {
		private final class LoadRulesSwingWorkerWithThrobber extends
				SwingWorkerWithThrobber<Collection<FirewallRule>, Object> {
			private final Firewall firewall;

			private LoadRulesSwingWorkerWithThrobber(
					final Collapsable throbber, final Firewall firewall) {
				super(throbber);
				this.firewall = firewall;
			}

			@Override
			protected Collection<FirewallRule> doInBackground()
					throws Exception {
				final Collection<FirewallRule> rules = provider
						.getFirewallRules(firewall.getProviderFirewallId());

				return rules;
			}

			@Override
			public void done() {
				try {
					final Collection<FirewallRule> rules = get();

					for (final FirewallRule rule : rules) {
						addRule(rule);
					}
				} catch (final Exception ignore) {
				}
			}
		}

		private static final long serialVersionUID = 1L;
		private final DefaultTableModel datamodel = new DefaultTableModel();
		private final CloudProvider provider;

		public RuleList(final CloudProvider provider) {
			this.provider = provider;
			setCellSelectionEnabled(false);
			setRowSelectionAllowed(true);

			datamodel.addColumn("Port");
			datamodel.addColumn("Protocol");
			datamodel.addColumn("Source");

			setModel(datamodel);
			getColumn("Port").setMaxWidth(100);
			getColumn("Protocol").setMaxWidth(100);
		}

		public String getPort(final int row) {
			return (String) getValueAt(row, 0);
		}

		public Protocol getProtocol(final int row) {
			return Protocol.valueOf((String) getValueAt(row, 1));
		}

		public String getSource(final int row) {
			return (String) getValueAt(row, 2);
		}

		public void addRule(final FirewallRule rule) {
			final String portString = (rule.getStartPort() == rule.getEndPort()) ? Integer
					.toString(rule.getStartPort()) : rule.getStartPort() + "-"
					+ rule.getEndPort();
			datamodel.addRow(new Object[] { portString, rule.getProtocol(),
					rule.getCidr() });
		}

		public void clear() {
			datamodel.setNumRows(0);
		}

		public void displayFirewall(final Firewall firewall) {
			synchronized (this) {
				clear();

				(new LoadRulesSwingWorkerWithThrobber(
						loadingRulesThrobberPanel, firewall)).execute();
			}
		}
	}

	private static final long serialVersionUID = 1L;
	private final DefaultListModel firewallListModel = new DefaultListModel();
	private final FirewallList firewallList = new FirewallList(
			firewallListModel);
	private final CollapsablePanel loadingFirewallsThrobberPanel = ThrobberFactory
			.getCollapsableThrobber("Loading firewalls", 10, 10);
	private final CollapsablePanel loadingRulesThrobberPanel = ThrobberFactory
			.getCollapsableThrobber("Loading rules", 10, 10);
	private final CollapsablePanel creatingFirewallThrobberPanel = ThrobberFactory
			.getCollapsableThrobber("Creating firewall", 10, 10);
	private final CollapsablePanel creatingRuleThrobberPanel = ThrobberFactory
			.getCollapsableThrobber("Creating rule", 10, 10);

	private final CloudProvider provider;
	private static Log log = LogFactory.getLog(InteractiveFirewallPanel.class);
	private final Collection<Firewall> firewalls = new ArrayList<Firewall>();
	private final JButton addRulebutton = new JButton("Add Rule",
			Utils.icon("add16.png"));
	private final JButton deleteRuleButton = new JButton("Delete Rule",
			Utils.icon("remove16.png"));
	private final JButton addFirewallButton = new JButton("Add Firewall",
			Utils.icon("add16.png"));
	private final JButton deleteFirewallButton = new JButton("Delete Firewall",
			Utils.icon("remove16.png"));
	private final RuleList ruleList;

	public InteractiveFirewallPanel(final CloudProvider provider) {
		super(new BorderLayout());
		this.provider = provider;
		ruleList = new RuleList(provider);

		final JPanel mainPanel = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();

		c.weightx = 0.25;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 10, 10, 5);

		final JPanel firewallPanel = new JPanel(new BorderLayout());
		firewallPanel.add(firewallList, BorderLayout.CENTER);
		final JPanel firewallButtonPanel = new JPanel();
		firewallButtonPanel.setLayout(new BoxLayout(firewallButtonPanel,
				BoxLayout.PAGE_AXIS));
		firewallButtonPanel.setOpaque(true);
		firewallButtonPanel.setBackground(Color.white);
		addFirewallButton.setMaximumSize(new Dimension(500, 500));
		addFirewallButton.addActionListener(new AddFirewallActionListener(
				provider));
		firewallButtonPanel.add(addFirewallButton);
		deleteFirewallButton.setMaximumSize(new Dimension(500, 500));
		deleteFirewallButton
				.addActionListener(new DeleteFirewallActionListener(provider));

		firewallButtonPanel.add(deleteFirewallButton);
		firewallPanel.add(firewallButtonPanel, BorderLayout.PAGE_END);
		final JScrollPane firewallListScroller = new JScrollPane(firewallPanel);
		firewallPanel.setPreferredSize(new Dimension(10, 10));
		mainPanel.add(firewallListScroller, c);

		c.weightx = 0.75;
		c.gridx = 1;
		c.insets = new Insets(10, 5, 10, 10);

		ruleList.setFillsViewportHeight(true);
		ruleList.setEnabled(false);

		firewallList.getSelectionModel().addListSelectionListener(
				new FirewallListSelectionListener(ruleList, addRulebutton,
						deleteRuleButton, deleteFirewallButton));

		final JScrollPane ruleListScroller = new JScrollPane(ruleList);
		final JPanel ruleListPanel = new JPanel(new BorderLayout());
		ruleListPanel.add(ruleListScroller, BorderLayout.CENTER);
		final JPanel ruleListButtonPanel = new JPanel();
		addRulebutton.addActionListener(new AddRuleActionListener(provider));
		deleteRuleButton.addActionListener(new DeleteRuleActionListener());

		ruleListButtonPanel.add(addRulebutton);
		ruleListButtonPanel.add(deleteRuleButton);
		addRulebutton.setEnabled(false);
		deleteRuleButton.setEnabled(false);
		deleteFirewallButton.setEnabled(false);
		ruleListPanel.add(ruleListButtonPanel, BorderLayout.SOUTH);
		ruleListPanel.setPreferredSize(new Dimension(10, 10));

		mainPanel.add(ruleListPanel, c);

		add(mainPanel, BorderLayout.CENTER);

		final JPanel allThrobbersPanel = getThrobbersPanel();

		add(allThrobbersPanel, BorderLayout.NORTH);

		loadInitialFirewalls();
	}

	private JPanel getThrobbersPanel() {
		final JPanel allThrobbersPanel = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;

		c.gridy = 0;
		allThrobbersPanel.add(loadingFirewallsThrobberPanel, c);

		c.gridy = 1;
		allThrobbersPanel.add(loadingRulesThrobberPanel, c);

		c.gridy = 2;
		allThrobbersPanel.add(creatingFirewallThrobberPanel, c);

		c.gridy = 3;
		allThrobbersPanel.add(creatingRuleThrobberPanel, c);

		return allThrobbersPanel;
	}

	public Collection<Firewall> getSelectedFirewalls() {
		final Collection<Firewall> rv = new ArrayList<Firewall>();
		final int indices[] = firewallList.getSelectedIndices();

		for (final int index : indices) {
			final String firewall = (String) firewallListModel.get(index);

			for (final Firewall comp : firewalls) {
				if (comp.getName().equals(firewall)) {
					rv.add(comp);
				}
			}
		}

		return rv;
	}

	public boolean selectedFirewallOpensPort(final int port) {
		boolean found = false;
		for (int row = 0; row < ruleList.getRowCount(); row++) {
			final String portString = ruleList.getPort(row);
			final PortRange range = new PortRange(portString);

			if (port >= range.startPort && port <= range.endPort) {
				found = true;
				break;
			}
		}

		return found;
	}

	private void addFirewall(final Firewall firewall) {
		firewallListModel.addElement(firewall.getName());
		firewalls.add(firewall);
	}

	private void loadInitialFirewalls() {
		(new LoadFirewallsWorkerWithThrobber(loadingFirewallsThrobberPanel))
				.execute();
	}

	@Override
	public void addRuleWindowClosed(final Firewall firewall,
			final int beginPort, final int endPort, final Protocol protocol,
			final String cidr) {
		addRule(firewall, beginPort, endPort, protocol, cidr);
	}

	private void addRule(final Firewall firewall, final int beginPort,
			final int endPort, final Protocol protocol, final String cidr) {
		(new AddRuleSwingWorker(creatingRuleThrobberPanel, cidr, beginPort,
				protocol, endPort, firewall)).execute();
	}
}
