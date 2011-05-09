package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
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
	private final class UsePasswordButtonActionListener implements
			ActionListener
	{
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			keyUsername.setEnabled(false);
			keypairCombo.setEnabled(false);
			normalUsername.setEnabled(true);
			normalPassword.setEnabled(true);
		}
	}

	private final class UseKeyButtonActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			keyUsername.setEnabled(true);
			keypairCombo.setEnabled(true);
			normalUsername.setEnabled(false);
			normalPassword.setEnabled(false);
		}
	}

	private static final long serialVersionUID = 1L;
	final private JRadioButton useLoginButton = new JRadioButton(
			"Username and password");
	final private JRadioButton useKeyButton = new JRadioButton(
			"Username and key authentication");
	final private ButtonGroup radioButtonGroup = new ButtonGroup();

	final private JPasswordField normalPassword = new JPasswordField();
	final private JTextField normalUsername = new JTextField();

	final private JTextField keyUsername = new JTextField();
	final private JComboBox keypairCombo;
	final private Server server;
	final private Collection<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private int actionId = 1;

	public ChangeServerSshLoginMethodWindow(final JDialog parent,
			final Server server)
	{
		super(parent, "Change login method", true);
		setSize(400, 350);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLocationRelativeTo(null);
		setLocationByPlatform(true);

		this.server = server;

		final char standardEchoChar = normalPassword.getEchoChar();

		normalPassword.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
			}

			@Override
			public void mousePressed(MouseEvent e)
			{

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				normalPassword.setEchoChar(standardEchoChar);

			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				normalPassword.setEchoChar('\0');
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
			}
		});

		useLoginButton.setMnemonic(KeyEvent.VK_P);
		useLoginButton.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

		useKeyButton.setMnemonic(KeyEvent.VK_K);
		useKeyButton.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

		// Group the radio buttons.
		radioButtonGroup.add(useLoginButton);
		radioButtonGroup.add(useKeyButton);

		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		final BetterTextLabel topText = new BetterTextLabel(
				"<html>Select the default way of logging into this server. "
						+ "<font color=\"red\">Warning: </font>the server itself needs to be configured to accept this login method.</html>");
		topText.setAlignmentX(Component.LEFT_ALIGNMENT);
		topText.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

		final String layoutString = "right:max(40dlu;p), 4dlu, max(20dlu;p):grow, 7dlu:grow";
		final FormLayout layout = new FormLayout(layoutString, "");
		// add rows dynamically
		final DefaultFormBuilder loginPanelsBuilder = new DefaultFormBuilder(
				layout);
		loginPanelsBuilder.setDefaultDialogBorder();
		loginPanelsBuilder.append("Username", normalUsername);
		loginPanelsBuilder.nextLine();
		loginPanelsBuilder.append("Password", normalPassword);
		final JPanel loginPanel = loginPanelsBuilder.getPanel();
		loginPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

		final FormLayout layout2 = new FormLayout(layoutString, "");
		final DefaultFormBuilder keyPanelsBuilder = new DefaultFormBuilder(
				layout2);
		keyPanelsBuilder.setDefaultDialogBorder();
		keyPanelsBuilder.append("Username", keyUsername);
		keyPanelsBuilder.nextLine();

		final Collection<String> keynames = KeyManager.getKeyNames(server
				.getCloud().getName());
		keypairCombo = new JComboBox(keynames.toArray(new String[0]));
		keyPanelsBuilder.append("Keypair", keypairCombo);

		final JPanel keyPanel = keyPanelsBuilder.getPanel();
		keyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		keyPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

		final UsePasswordButtonActionListener usePasswordButtonActionListener = new UsePasswordButtonActionListener();
		useLoginButton.addActionListener(usePasswordButtonActionListener);
		final UseKeyButtonActionListener useKeyButtonActionListener = new UseKeyButtonActionListener();
		useKeyButton.addActionListener(useKeyButtonActionListener);

		if (server.usesSshPassword())
		{
			useLoginButton.setSelected(true);
			usePasswordButtonActionListener.actionPerformed(null);
		}
		else
		{
			useKeyButton.setSelected(true);
			useKeyButtonActionListener.actionPerformed(null);
		}

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

		prefillTextfields(server);
	}

	private void prefillTextfields(final Server server)
	{
		normalUsername.setText(server.getSshUsername());
		normalPassword.setText(server.getSshPassword());
		keyUsername.setText(server.getSshUsername());
		keypairCombo.setSelectedItem(server.getKeypair());
	}

	public void saveAndClose()
	{
		if (radioButtonGroup.getSelection() == useKeyButton.getModel())
		{
			// Use key
			final String keyname = (String) keypairCombo.getSelectedItem();
			final String username = keyUsername.getText();
			server.assureKeypairLogin(username, keyname);
			server.store();
		}
		else
		{
			// Use login & password
			final String username = normalUsername.getText();
			final String password = new String(normalPassword.getPassword());
			server.assurePasswordLogin(username, password);
			server.store();
		}
		fireActionListeners();
		dispose();
	}

	private JPanel getButtonPanel()
	{
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());

		final JButton cancelButton = ButtonFactory.createCancelButton();
		cancelButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				dispose();
			}
		});

		final JButton okButton = ButtonFactory.createOkButton();
		okButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				saveAndClose();
			}
		});
		getRootPane().setDefaultButton(okButton);

		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(15));
		buttonPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return buttonPanel;
	}

	public void addActionListener(final ActionListener actionListener)
	{
		actionListeners.add(actionListener);
	}

	private void fireActionListeners()
	{
		for (final ActionListener actionListener : actionListeners)
		{
			actionListener.actionPerformed(new ActionEvent(this, actionId++,
					"Window closed"));
		}
	}
}
