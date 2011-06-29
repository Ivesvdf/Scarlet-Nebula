/*
 * Copyright (C) 2011  Ives van der Flaas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.ExceptionListener;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.SSHCommandConnection;
import be.ac.ua.comp.scarletnebula.core.Server;

import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.JCTermSwing;

public class SSHPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(SSHPanel.class);
	private final Collection<ExceptionListener> exceptionListeners = new LinkedList<ExceptionListener>();

	public SSHPanel(final Server server) {
		super();

		final JCTermSwing term = new JCTermSwing();
		term.setCompression(7);
		term.setAntiAliasing(true);

		setLayout(new BorderLayout());

		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(final ComponentEvent e) {
			}

			@Override
			public void componentResized(final ComponentEvent e) {
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
			public void componentMoved(final ComponentEvent e) { // TODO
																	// Auto-generated
																	// method
																	// stub

			}

			@Override
			public void componentHidden(final ComponentEvent e) { // TODO
																	// Auto-generated
																	// method
																	// stub

			}
		});

		add(term, BorderLayout.CENTER);

		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 20, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		final Thread connectionThread = new Thread() {
			@Override
			public void run() {

				Connection connection = null;
				try {
					final SSHCommandConnection commandConnection = (SSHCommandConnection) server
							.newCommandConnection(new NotPromptingJschUserInfo());

					connection = commandConnection.getJSchTerminalConnection();

					term.requestFocusInWindow();
					term.start(connection);
				} catch (final Exception e) {
					for (final ExceptionListener listener : exceptionListeners) {
						listener.exceptionThrown(e);
					}

					log.warn("Exception thrown by SSHPanel", e);
				} finally {
				}

			}
		};

		connectionThread.start();
	}

	public void addExceptionListener(final ExceptionListener listener) {
		exceptionListeners.add(listener);
	}

}
