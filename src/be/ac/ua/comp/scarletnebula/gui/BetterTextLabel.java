package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Font;

import javax.swing.JLabel;

public class BetterTextLabel extends JLabel
{
	private static final long serialVersionUID = 1L;

	BetterTextLabel(String text)
	{
		super("");

		boolean isAlreayHTML = (text.indexOf("<html>") == 0);

		if (!isAlreayHTML)
		{
			text = "<html>" + text.replace("\n", "<br />") + "</html>";
		}

		setText(text);
		setFont(new Font("Dialog", Font.PLAIN, getFont().getSize()));
	}
}
