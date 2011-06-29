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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;

public class EditStatisticsCommandWindow extends JDialog {
	private final class CancelActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			dispose();
		}
	}

	private final class SaveAndCloseActionListener implements ActionListener {
		private final Server server;
		private final JTextArea textArea;

		private SaveAndCloseActionListener(final Server server,
				final JTextArea textArea) {
			this.server = server;
			this.textArea = textArea;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			server.setStatisticsCommand(textArea.getText());
			server.store();
			dispose();
		}
	}

	private static final long serialVersionUID = 1L;

	public EditStatisticsCommandWindow(final JDialog parent, final Server server) {
		super(parent,
				"Edit statistics command for " + server.getFriendlyName(), true);

		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		setSize(400, 350);
		setLayout(new BorderLayout());

		final JTextArea textArea = new JTextArea(server.getStatisticsCommand());
		final JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 10, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		add(scrollPane, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		final JButton cancelButton = ButtonFactory.createCancelButton();
		cancelButton.addActionListener(new CancelActionListener());
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		final JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new SaveAndCloseActionListener(server,
				textArea));
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		add(buttonPanel, BorderLayout.SOUTH);
		setVisible(true);
	}
}
