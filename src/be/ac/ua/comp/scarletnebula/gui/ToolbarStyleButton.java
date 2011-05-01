package be.ac.ua.comp.scarletnebula.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JLabel;

public class ToolbarStyleButton extends JLabel
{
	private static final long serialVersionUID = 1L;
	private Collection<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private int eventID = 0;

	public ToolbarStyleButton(final Icon icon, final Icon onHover)
	{
		super(icon);
		// setBounds(10, 10, icon.getIconWidth(), icon.getIconHeight() - 2);

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				setIcon(onHover);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setIcon(icon);
			}

			@Override
			public void mousePressed(MouseEvent e)
			{

				for (ActionListener listener : actionListeners)
				{
					listener.actionPerformed(new ActionEvent(
							ToolbarStyleButton.this, eventID++, "Clicked"));
				}
			}
		});

	}

	public void addActionListener(ActionListener listener)
	{
		actionListeners.add(listener);
	}
}
