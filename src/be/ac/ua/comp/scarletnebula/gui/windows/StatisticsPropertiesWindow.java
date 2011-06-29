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
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;

public class StatisticsPropertiesWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	final private Collection<ActionListener> actionListeners = new ArrayList<ActionListener>();
	final private JTextArea commandArea = new JTextArea();
	private int maxEventId = 0;

	public StatisticsPropertiesWindow(final JDialog parent,
			final Collection<Server> servers) {
		super(parent, "Statistics Properties", true);

		if (servers.size() == 1) {
			setTitle("Statistics Properties for "
					+ servers.iterator().next().getFriendlyName());
		}

		setSize(500, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setLocationRelativeTo(parent);
		setLocationByPlatform(true);

		final BetterTextLabel textLabel = new BetterTextLabel(
				"Enter the command that will be executed on the remote server and will return a continuous stream of newline separated JSON fragments. "
						+ "This command can be an entire script or just the execution of a single server-style program.");
		textLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

		boolean allCommandsAreEqual = true;

		if (servers.size() > 1) {
			final String firstServersText = servers.iterator().next()
					.getStatisticsCommand();

			for (final Server server : servers) {
				if (!server.getStatisticsCommand().equals(firstServersText)) {
					allCommandsAreEqual = false;
					break;
				}
			}
		}

		if (allCommandsAreEqual) {
			commandArea.setText(servers.iterator().next()
					.getStatisticsCommand());
		} else {
			commandArea
					.setText("The selected servers have differing Statistic commands.");
		}

		commandArea.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		final JScrollPane commandAreaScrollPane = new JScrollPane(commandArea);
		commandAreaScrollPane.setBorder(BorderFactory.createEmptyBorder(15, 20,
				15, 20));
		add(textLabel, BorderLayout.NORTH);
		add(commandAreaScrollPane, BorderLayout.CENTER);
		add(getButtonPanel(servers), BorderLayout.SOUTH);

	}

	private JPanel getButtonPanel(final Collection<Server> servers) {
		final JPanel buttonPanel = new JPanel();
		final JButton cancelButton = ButtonFactory.createCancelButton();
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				dispose();
			}
		});
		final JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (servers.size() > 1) {
					final int result = JOptionPane
							.showConfirmDialog(
									StatisticsPropertiesWindow.this,
									"You are changing the Statistics command for multiple servers, are you sure you wish to proceed?",
									"Proceed?", JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.WARNING_MESSAGE);

					if (result == JOptionPane.OK_OPTION) {
						saveAndClose(servers);
					}
				} else {
					saveAndClose(servers);
				}

			}
		});

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		return buttonPanel;
	}

	public void addActionListener(final ActionListener listener) {
		actionListeners.add(listener);
	}

	private void saveAndClose(final Collection<Server> servers) {
		for (final Server server : servers) {
			server.setStatisticsCommand(commandArea.getText());
		}
		dispose();
		final ActionEvent actionEvent = new ActionEvent(this, maxEventId++,
				"Window Closed");
		for (final ActionListener listener : actionListeners) {
			listener.actionPerformed(actionEvent);
		}
	}
}
