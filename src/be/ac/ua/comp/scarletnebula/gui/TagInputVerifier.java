package be.ac.ua.comp.scarletnebula.gui;

import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.gui.inputverifiers.LoudInputVerifier;

public class TagInputVerifier extends LoudInputVerifier {

	public TagInputVerifier(final JTextField textField) {
		super(textField,
				"Only letters, numbers and spaces are allowed in a tag.");
	}

	@Override
	public boolean verify(final JComponent input) {
		return Pattern.matches("^[a-zA-Z0-9 :]*",
				((JTextField) input).getText());
	}

}
