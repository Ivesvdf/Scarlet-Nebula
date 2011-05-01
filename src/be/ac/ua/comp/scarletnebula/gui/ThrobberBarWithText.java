package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import be.ac.ua.comp.scarletnebula.misc.Utils;

public class ThrobberBarWithText extends JLabel
{
	private static final long serialVersionUID = 1L;

	public ThrobberBarWithText(final String text)
	{
		super(text, Utils.icon("throbber.gif"), SwingConstants.CENTER);
		setOpaque(true);
		setBackground(new Color(255, 255, 255));
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(0, 0, 0, 20)));
	}
}
