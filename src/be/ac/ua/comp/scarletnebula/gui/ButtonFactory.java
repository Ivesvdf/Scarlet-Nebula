package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Insets;

import javax.swing.JButton;

public class ButtonFactory
{
	public static JButton createOkButton()
	{
		JButton button = new JButton("OK");
		button.setMargin(new Insets(0, 15, 0, 15));

		return button;
	}
}
