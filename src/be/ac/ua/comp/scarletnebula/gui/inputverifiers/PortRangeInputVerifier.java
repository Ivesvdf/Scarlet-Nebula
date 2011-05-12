package be.ac.ua.comp.scarletnebula.gui.inputverifiers;

import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTextField;

public final class PortRangeInputVerifier extends LoudInputVerifier {
	public PortRangeInputVerifier(final JTextField textField) {
		super(textField,
				"This field must contain single port like 22 or a range e.g. 100-110.");
	}

	@Override
	public boolean verify(final JComponent input) {
		final String text = ((JTextField) input).getText();
		boolean good = true;
		if (Pattern.matches("[0-9]+|[0-9]+-[0-9]+", text)) {
			final String parts[] = text.split("-");

			for (final String part : parts) {
				final int port = Integer.decode(part);
				if (port > 65536) {
					good = false;
					break;
				}
			}
		} else {
			good = false;
		}

		return good;
	}
}