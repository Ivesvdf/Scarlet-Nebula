package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.misc.Utils;

public class SearchField extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JTextField textfield;

	public SearchField(final JTextField inputfield) {
		super(new BorderLayout());
		textfield = inputfield;
		setOpaque(true);
		setBackground(Color.WHITE);
		textfield.setBackground(Color.WHITE);
		textfield.setBorder(null);
		final JLabel iconLabel = new JLabel(Utils.icon("search16.png"));
		iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		add(iconLabel, BorderLayout.WEST);
		add(textfield, BorderLayout.CENTER);
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
	}

	public JTextField getTextField() {
		return textfield;
	}
}
