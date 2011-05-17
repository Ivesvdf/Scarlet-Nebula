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
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	private final class SaveAndCloseActionListener implements ActionListener {
		private final Server server;
		private final JTextArea textArea;

		private SaveAndCloseActionListener(Server server, JTextArea textArea) {
			this.server = server;
			this.textArea = textArea;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			server.setStatisticsCommand(textArea.getText());
			server.store();
			dispose();
		}
	}

	private static final long serialVersionUID = 1L;

	public EditStatisticsCommandWindow(JDialog parent, final Server server) {
		super(parent,
				"Edit statistics command for " + server.getFriendlyName(), true);

		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		setSize(400, 350);
		setLayout(new BorderLayout());

		final JTextArea textArea = new JTextArea(server.getStatisticsCommand());
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 10, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
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
