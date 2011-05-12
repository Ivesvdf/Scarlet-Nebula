package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import javax.swing.JComboBox;

import org.dasein.cloud.compute.Platform;

public class PlatformComboBox extends JComboBox {
	private static final long serialVersionUID = 1L;

	PlatformComboBox() {
		super();

		for (final Platform platform : org.dasein.cloud.compute.Platform
				.values()) {
			addItem(toProperCase(platform.name().replace('_', ' ')).replace(
					"Bsd", "BSD").replace("Os", "OS"));
		}
	}

	Platform getSelection() {
		String itemname = (String) getSelectedItem();
		itemname = itemname.replace(' ', '_').toUpperCase();

		return org.dasein.cloud.compute.Platform.valueOf(itemname);
	}

	public static String toProperCase(String input) {
		input = input.toLowerCase();
		String result = "";
		for (int i = 0; i < input.length(); i++) {
			final String next = input.substring(i, i + 1);
			if (i == 0 || (i > 0 && input.substring(i - 1, i).equals(" "))) {
				result += next.toUpperCase();
			} else {
				result += next;
			}
		}
		return result;
	}
}
