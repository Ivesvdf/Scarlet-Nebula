package be.ac.ua.comp.scarletnebula.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

public class Statusbar extends JLabel
{
	Statusbar()
	{
		super(" "); // Call JLabel ctor with a single space as text so the
					// preferred height for this element will be correct

		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

	}
}
