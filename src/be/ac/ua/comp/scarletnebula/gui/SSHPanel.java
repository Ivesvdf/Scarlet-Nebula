package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.SSHCommandConnection;
import be.ac.ua.comp.scarletnebula.core.Server;

import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.JCTermSwing;
import com.jcraft.jsch.JSchException;

public class SSHPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(SSHPanel.class);

	public SSHPanel(final Server server)
	{
		super();

		final JCTermSwing term = new JCTermSwing();
		term.setCompression(7);
		term.setAntiAliasing(true);

		setLayout(new BorderLayout());

		addComponentListener(new ComponentListener()
		{

			@Override
			public void componentShown(final ComponentEvent e)
			{
			}

			@Override
			public void componentResized(final ComponentEvent e)
			{
				final Component c = e.getComponent();
				int cw = c.getWidth();
				int ch = c.getHeight();

				final JPanel source = ((JPanel) c);

				final int cwm = source.getBorder() != null ? source.getBorder()
						.getBorderInsets(c).left
						+ source.getBorder().getBorderInsets(c).right : 0;
				final int chm = source.getBorder() != null ? source.getBorder()
						.getBorderInsets(c).bottom
						+ source.getBorder().getBorderInsets(c).top : 0;
				cw -= cwm;
				ch -= chm;

				term.setBorder(BorderFactory.createMatteBorder(0, 0,
						term.getTermHeight() - c.getHeight(),
						term.getTermWidth() - c.getWidth(), Color.BLACK));
				term.setSize(cw, ch);
				term.setPreferredSize(new Dimension(cw, ch));
				// term.setMinimumSize(new Dimension(cw, ch));
				term.setMaximumSize(new Dimension(cw, ch));
				term.redraw(0, 0, term.getTermWidth(), term.getTermHeight());
			}

			@Override
			public void componentMoved(final ComponentEvent e)
			{ // TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(final ComponentEvent e)
			{ // TODO Auto-generated method stub

			}
		});

		add(term, BorderLayout.CENTER);

		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 20, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		final Thread connectionThread = new Thread()
		{
			@Override
			public void run()
			{

				Connection connection = null;
				try
				{
					final SSHCommandConnection commandConnection = (SSHCommandConnection) server
							.newCommandConnection(new NotPromptingJschUserInfo());

					connection = commandConnection.getJSchTerminalConnection();

					term.requestFocusInWindow();
					term.start(connection);
				}
				catch (final JSchException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (final IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (final Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally
				{
				}

			}
		};

		connectionThread.start();
	}

}
