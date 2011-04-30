package be.ac.ua.comp.scarletnebula.gui;

import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTextField;

public class PlainTextVerifier extends LoudInputVerifier
{
	public PlainTextVerifier(JTextField textField, String message)
	{
		super(textField, message);
	}

	@Override
	public boolean verify(JComponent input)
	{
		return Pattern.matches("[a-zA-Z0-9 ]+", ((JTextField) input).getText());
	}

}
