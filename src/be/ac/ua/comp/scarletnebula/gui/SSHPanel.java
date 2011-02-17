package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.SSHCommandConnection;
import be.ac.ua.comp.scarletnebula.core.Server;

import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.JCTermSwing;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class SSHPanel extends JPanel
{
	SSHPanel(final Server server)
	{
		super();

		final JCTermSwing term = new JCTermSwing();
		term.setCompression(7);
		term.setAntiAliasing(true);

		term.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		setLayout(new BorderLayout());

		addComponentListener(new ComponentListener()
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

		add(term, BorderLayout.CENTER);

		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 20, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		// For the time being, this only works on one server so pick the last
		// one

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
				SSHCommandConnection commandConnection = (SSHCommandConnection) server
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
	}
}
