package be.ac.ua.comp.scarletnebula.gui;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class ServernameInputVerifier extends InputVerifier
{

	@Override
	public boolean verify(JComponent input)
	{
		JTextField inputField = (JTextField) input;
		return inputField.getText().length() > 0;
	}

}
