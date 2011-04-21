package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.KeyManager;
import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ChangeServerSshLoginMethodWindow extends JDialog
{
	public class ChangeSshServerWindowClosingListener implements WindowListener
	{

		@Override
		public void windowOpened(WindowEvent e)
		{
		}

		@Override
		public void windowClosing(WindowEvent e)
		{
		}

		@Override
		public void windowClosed(WindowEvent e)
		{
			ChangeServerSshLoginMethodWindow win = (ChangeServerSshLoginMethodWindow) e
					.getWindow();
			win.close();
		}

		@Override
		public void windowIconified(WindowEvent e)
		{
		}

		@Override
		public void windowDeiconified(WindowEvent e)
		{
		}

		@Override
		public void windowActivated(WindowEvent e)
		{
		}

		@Override
		public void windowDeactivated(WindowEvent e)
		{
		}

	}

	private static final long serialVersionUID = 1L;

	public ChangeServerSshLoginMethodWindow(JDialog parent, Server server)
	{
		super(parent, "Change login method", true);
		setSize(400, 350);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLocationRelativeTo(null);
		setLocationByPlatform(true);

		JRadioButton useLoginButton = new JRadioButton("Username and password");
		useLoginButton.setMnemonic(KeyEvent.VK_P);
		useLoginButton.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

		JRadioButton useKeyButton = new JRadioButton(
				"Username and key authentication");
		useKeyButton.setMnemonic(KeyEvent.VK_K);
		useKeyButton.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(useLoginButton);
		group.add(useKeyButton);

		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		final BetterTextLabel topText = new BetterTextLabel(
				"<html>Select the default way of logging into this server. "
						+ "<font color=\"red\">Warning: </font>the server itself needs to be configured to accept this login method.</html>");
		topText.setAlignmentX(Component.LEFT_ALIGNMENT);
		topText.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

		final String layoutString = "right:max(40dlu;p), 4dlu, max(20dlu;p):grow, 7dlu:grow";
		FormLayout layout = new FormLayout(layoutString, "");
		// add rows dynamically
		DefaultFormBuilder loginPanelsBuilder = new DefaultFormBuilder(layout);
		loginPanelsBuilder.setDefaultDialogBorder();
		final JTextField loginUsername = new JTextField();
		loginPanelsBuilder.append("Username", loginUsername);
		loginPanelsBuilder.nextLine();
		final JPasswordField loginPassword = new JPasswordField();
		loginPanelsBuilder.append("Password", loginPassword);
		final JPanel loginPanel = loginPanelsBuilder.getPanel();
		loginPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

		FormLayout layout2 = new FormLayout(layoutString, "");
		DefaultFormBuilder keyPanelsBuilder = new DefaultFormBuilder(layout2);
		keyPanelsBuilder.setDefaultDialogBorder();
		final JTextField keyUsername = new JTextField();
		keyPanelsBuilder.append("Username", keyUsername);
		keyPanelsBuilder.nextLine();

		Collection<String> keynames = KeyManager.getKeyNames(server.getCloud()
				.getName());
		final JComboBox keypairCombo = new JComboBox(
				keynames.toArray(new String[0]));
		keyPanelsBuilder.append("Keypair", keypairCombo);

		final JPanel keyPanel = keyPanelsBuilder.getPanel();
		keyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		keyPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

		final ActionListener useLoginButtonActionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				keyUsername.setEnabled(false);
				keypairCombo.setEnabled(false);
				loginUsername.setEnabled(true);
				loginPassword.setEnabled(true);
			}
		};
		useLoginButton.addActionListener(useLoginButtonActionListener);

		final ActionListener useKeyButtonActionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				keyUsername.setEnabled(true);
				keypairCombo.setEnabled(true);
				loginUsername.setEnabled(false);
				loginPassword.setEnabled(false);
			}
		};
		useKeyButton.addActionListener(useKeyButtonActionListener);

		useLoginButton.setSelected(server.usesSshPassword());
		useKeyButton.setSelected(!server.usesSshPassword());

		add(topText);
		add(useLoginButton);
		add(Box.createVerticalStrut(5));
		add(loginPanel);

		add(useKeyButton);
		add(Box.createVerticalStrut(5));
		add(keyPanel);

		add(Box.createVerticalGlue());

		final JPanel buttonPanel = getButtonPanel();
		add(buttonPanel);
		add(Box.createVerticalStrut(15));

		addWindowListener(new ChangeSshServerWindowClosingListener());
		setVisible(true);
	}

	public void close()
	{

		dispose();
	}

	private JPanel getButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		final JButton okButton = ButtonFactory.createOkButton();
		okButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				close();
			}
		});

		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(15));
		buttonPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return buttonPanel;
	}
}
