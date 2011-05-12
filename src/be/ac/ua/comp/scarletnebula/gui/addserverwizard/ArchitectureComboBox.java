package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import javax.swing.JComboBox;

import org.dasein.cloud.compute.Architecture;

public class ArchitectureComboBox extends JComboBox {
	private static final long serialVersionUID = 1L;
	private final static String S32BIT = "32 bit";
	private final static String S64BIT = "64 bit";

	ArchitectureComboBox() {
		super();

		addItem(S32BIT);
		addItem(S64BIT);
	}

	public Architecture getSelection() {
		final String itemString = (String) getSelectedItem();
		Architecture rvArchitecture;

		if (itemString.equals(S64BIT)) {
			rvArchitecture = Architecture.I64;
		} else {
			rvArchitecture = Architecture.I32;
		}

		return rvArchitecture;
	}
}
