package be.ac.ua.comp.scarletnebula.gui;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

public class WrappableLabel extends JTextArea
{
	private static final long serialVersionUID = 1L;

	public WrappableLabel(final String text)
	{
		super(text);
		setLineWrap(true);
		setWrapStyleWord(true);
		setEditable(false);
		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(false);
	}
}
