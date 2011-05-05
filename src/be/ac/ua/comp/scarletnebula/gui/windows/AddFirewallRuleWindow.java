package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dasein.cloud.network.Firewall;
import org.dasein.cloud.network.Protocol;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;
import be.ac.ua.comp.scarletnebula.gui.InteractiveFirewallPanel;
import be.ac.ua.comp.scarletnebula.gui.InteractiveFirewallPanel.PortRange;
import be.ac.ua.comp.scarletnebula.gui.inputverifiers.IpAddressVerifier;
import be.ac.ua.comp.scarletnebula.gui.inputverifiers.LoudInputVerifier;
import be.ac.ua.comp.scarletnebula.gui.inputverifiers.PortRangeInputVerifier;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class AddFirewallRuleWindow extends JDialog
{
	public interface AddFirewallRuleWindowClosedListener
	{
		public void addRuleWindowClosed(Firewall firewall, int beginPort,
				int endPort, Protocol protocol, String CIDR);
	};

	private final class OkActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			final LoudInputVerifier inputVerifier = new IpAddressVerifier(
					ipField, "E.g. 84.5.40.160 or 0.0.0.0/0");
			ipField.setInputVerifier(inputVerifier);
			if (inputVerifier.shouldYieldFocus(ipField)
					&& portRangeField.getInputVerifier().shouldYieldFocus(
							portRangeField))
			{
				final InteractiveFirewallPanel.PortRange range = new InteractiveFirewallPanel.PortRange(
						portRangeField.getText());

				for (final AddFirewallRuleWindowClosedListener listener : listeners)
				{
					listener.addRuleWindowClosed(firewall, range.startPort,
							range.endPort, Protocol
									.valueOf((String) protocolDropdown
											.getSelectedItem()), ipField
									.getText());
				}

				dispose();
			}
		}
	}

	private final class CancelActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			dispose();
		}
	}

	private static final long serialVersionUID = 1L;
	private final Collection<AddFirewallRuleWindowClosedListener> listeners = new ArrayList<AddFirewallRuleWindowClosedListener>();
	private final JTextField portRangeField = new JTextField("22");
	private final JComboBox protocolDropdown = new JComboBox(new Object[] {
			"TCP", "UDP", "ICMP" });
	private final JTextField ipField = new JTextField("0.0.0.0/0");
	private final Firewall firewall;

	public AddFirewallRuleWindow(final JDialog parent,
			final CloudProvider provider, final Firewall firewall)
	{
		super(parent, "Add Rule", true);
		this.firewall = firewall;
		setLayout(new BorderLayout());
		setSize(400, 250);
		setLocationRelativeTo(null);
		setLocationByPlatform(true);

		final FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, min(50dlu;p):grow", "");
		// add rows dynamically
		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.appendSeparator("New Rule");
		portRangeField.setInputVerifier(new PortRangeInputVerifier(
				portRangeField));
		builder.append("Port range", portRangeField);

		builder.append("Protocol", protocolDropdown);

		builder.append("IP", ipField);

		final JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(builder.getPanel(), BorderLayout.CENTER);
		final JLabel bottomLabel = new JLabel(
				"<html><font size=\"6pt\" color=\"#657383\">Note: Use the IP address 0.0.0.0/0 to allow "
						+ "traffic from and to everyone. </font></html>");
		bottomLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		mainPanel.add(bottomLabel, BorderLayout.PAGE_END);

		add(mainPanel, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		final JButton cancelButton = ButtonFactory.createCancelButton();
		cancelButton.addActionListener(new CancelActionListener());
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		final JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new OkActionListener());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		add(buttonPanel, BorderLayout.PAGE_END);

	}

	public void addAddFirewallRuleWindowClosed(
			final AddFirewallRuleWindowClosedListener listener)
	{
		listeners.add(listener);
	}
}
