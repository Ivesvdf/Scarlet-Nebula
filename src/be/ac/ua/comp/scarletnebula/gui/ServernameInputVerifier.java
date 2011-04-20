package be.ac.ua.comp.scarletnebula.gui;

import java.util.regex.Pattern;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudManager;

public class ServernameInputVerifier extends LoudInputVerifier
{
	public ServernameInputVerifier(JTextField textfield)
	{
		super(
				textfield,
				"A servername must be at least 1 character long and can only contain letters, numbers, parentheses and dashes.");
	}

	/**
	 * @see InputVerifier
	 */
	@Override
	public boolean verify(JComponent input)
	{
		final String text = ((JTextField) input).getText();

		boolean valid = true;
		if (!Pattern.matches("[a-zA-Z )(-0-9]+", text))
		{
			valid = false;
		}
		else if (CloudManager.get().serverExists(text))
		{
			valid = false;
		}

		return valid;
	}
}
