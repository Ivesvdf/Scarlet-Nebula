package be.ac.ua.comp.scarletnebula.gui.inputverifiers;

import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTextField;

public class NumberInputVerifier extends LoudInputVerifier
{
	public NumberInputVerifier(JTextField textField, String message)
	{
		super(textField, message);
	}

	@Override
	public boolean verify(JComponent input)
	{
		return Pattern.matches("[0-9]+", ((JTextField) input).getText());
	}
}