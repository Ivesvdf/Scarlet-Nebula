package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Insets;

import javax.swing.JButton;

public class ButtonFactory
{
	public static JButton createOkButton()
	{
		JButton button = new JButton("OK");
		button.setMargin(new Insets(0, 25, 0, 25));

		return button;
	}

	public static JButton createCancelButton()
	{
		JButton button = new JButton("Cancel");
		button.setMargin(new Insets(0, 10, 0, 10));

		return button;
	}
}
