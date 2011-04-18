package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class BetterTextField extends JTextField
{
	private static final long serialVersionUID = 1L;
	private boolean originalPlaceholderText = true;

	public BetterTextField(int columns)
	{
		super(columns);
	}

	public BetterTextField()
	{
		super();
	}

	public void setPlaceHolder(String placeholder)
	{
		setText(placeholder);
		final Color originalTextColor = getForeground();
		setForeground(Colors.Gray.alpha(1.0f));
		addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent e)
			{

			}

			@Override
			public void focusGained(FocusEvent e)
			{
				if (originalPlaceholderText)
				{
					originalPlaceholderText = false;
					setForeground(originalTextColor);
					setText("");
				}
			}
		});
	}
}
