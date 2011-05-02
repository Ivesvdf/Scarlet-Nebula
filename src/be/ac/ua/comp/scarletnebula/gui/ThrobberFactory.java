package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class ThrobberFactory
{
	public static CollapsablePanel getCollapsableThrobber(final String text,
			final int topspace, final int bottomspace)
	{
		final JPanel decoratedBar = new JPanel(new BorderLayout());
		decoratedBar.add(new ThrobberBarWithText(text));
		decoratedBar.setBorder(BorderFactory.createEmptyBorder(topspace, 0,
				bottomspace, 0));
		return new CollapsablePanel(decoratedBar, false);
	}
}
