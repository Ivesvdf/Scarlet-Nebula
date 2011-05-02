package be.ac.ua.comp.scarletnebula.gui.inputverifiers;

import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTextField;

public class IpAddressVerifier extends LoudInputVerifier
{
	public IpAddressVerifier(final JTextField textField, final String message)
	{
		super(textField, message);
	}

	@Override
	public boolean verify(final JComponent input)
	{
		return Pattern.matches("(?:\\d{1,3}\\.){3}\\d{1,3}(?:/\\d\\d?)?",
				((JTextField) input).getText());
	}
}