package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Font;

import javax.swing.JLabel;

public class BetterTextLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public BetterTextLabel(String label) {
		super("");

		final boolean isAlreayHTML = (label.indexOf("<html>") == 0);

		if (!isAlreayHTML) {
			label = "<html>" + label.replace("\n", "<br />") + "</html>";
		}

		setText(label);
		setFont(new Font("Dialog", Font.PLAIN, getFont().getSize()));
	}
}
