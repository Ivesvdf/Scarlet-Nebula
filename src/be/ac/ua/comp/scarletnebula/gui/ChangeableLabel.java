package be.ac.ua.comp.scarletnebula.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import be.ac.ua.comp.scarletnebula.misc.Utils;

public class ChangeableLabel extends JPanel
{
	private static final long serialVersionUID = 1L;

	final JLabel contentLabel;

	ChangeableLabel(String originalText, final Executable<JLabel> executable)
	{
		setLayout(new GridBagLayout());
		contentLabel = new JLabel(originalText);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		add(contentLabel, c);
		final JButton editButton = new ToolbarStyleButton(
				Utils.icon("settings16.png"));
		editButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				executable.run(contentLabel);
			}
		});
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.gridx = 1;
		add(editButton, c);

		addMouseListener(new MouseListener()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					executable.run(contentLabel);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
			}
		});
	}

	interface Executable<Paramtype>
	{
		void run(Paramtype param);
	}
}
