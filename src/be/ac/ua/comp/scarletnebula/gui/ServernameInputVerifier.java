package be.ac.ua.comp.scarletnebula.gui;

import java.util.regex.Pattern;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.Server;

public class ServernameInputVerifier extends LoudInputVerifier
{
	private final Server excludeServer;

	/**
	 * Input verifier constructor
	 * 
	 * @param textfield
	 *            The field that should be verified
	 * @param excludeServer
	 *            A server that should be excluded from the unique-ness test --
	 *            ie the server whose name we're checking.
	 */
	public ServernameInputVerifier(final JTextField textfield, final Server excludeServer)
	{
		super(
				textfield,
				"A servername must be at least 1 character long and can only contain letters, numbers, parentheses and dashes.");
		this.excludeServer = excludeServer;
	}

	public ServernameInputVerifier(final JTextField textfield)
	{
		this(textfield, null);
	}

	/**
	 * @see InputVerifier
	 */
	@Override
	public boolean verify(final JComponent input)
	{
		final String text = ((JTextField) input).getText();

		boolean valid = true;
		if (!Pattern.matches("[a-zA-Z0-9 )(-]+", text))
		{
			valid = false;
		}
		else if (((excludeServer != null && !text.equals(excludeServer
				.getFriendlyName())) || excludeServer == null)
				&& CloudManager.get().serverExists(text))
		{
			valid = false;
		}

		return valid;
	}
}
