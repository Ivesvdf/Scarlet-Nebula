package be.ac.ua.comp.scarletnebula.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import be.ac.ua.comp.scarletnebula.misc.Executable;
import be.ac.ua.comp.scarletnebula.misc.Utils;

/**
 * A class that displays a label that can be "executed". After execution the
 * content of the label might change.
 * 
 * @author ives
 * 
 */
public class ChangeableLabel extends JPanel
{
	private static final long serialVersionUID = 1L;

	final private JLabel contentLabel;

	/**
	 * Constructor.
	 * 
	 * @param originalText
	 *            Text that will be displayed in the JLabel when it is first
	 *            shown
	 * @param executable
	 *            The Executable class that will be executed when the
	 *            ChangeableLabel is executed.
	 */
	public ChangeableLabel(final String originalText,
			final Executable<JLabel> executable)
	{
		setLayout(new GridBagLayout());
		contentLabel = new JLabel(originalText);

		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		add(contentLabel, c);
		final ToolbarStyleButton editButton = new ToolbarStyleButton(
				Utils.icon("settings16.png"),
				Utils.icon("settings_hover16.png"));
		editButton.addActionListener(new ExecuteActionListener<JLabel>(
				executable, contentLabel));
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.gridx = 1;
		add(editButton, c);

		addMouseListener(new ExecuteMouseListener<JLabel>(executable,
				contentLabel));
	}

	/**
	 * Class that converts an ActionListener trigger to an Executable trigger
	 * 
	 * @author ives
	 * 
	 */
	private final class ExecuteActionListener<T> implements ActionListener
	{
		private final Executable<T> executable;
		private final T argument;

		/**
		 * Constructor.
		 * 
		 * @param executable
		 *            The interface that will be executed when the
		 *            ActionListener fires.
		 * @param argument
		 *            This parameter will be given to the run() method of the
		 *            Executable when the ActionListener fires
		 */
		private ExecuteActionListener(final Executable<T> executable,
				final T argument)
		{
			this.executable = executable;
			this.argument = argument;
		}

		/**
		 * @see ActionListener
		 */
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			executable.run(argument);
		}
	}

	/**
	 * Class that converts a double click to the running of an Executable.
	 * 
	 * @author ives
	 * 
	 */
	private final class ExecuteMouseListener<T> implements MouseListener
	{
		private final Executable<T> executable;
		private final T argument;

		/**
		 * Constructor
		 * 
		 * @param executable
		 *            The Executable to be executed on double click
		 * @param argument
		 *            The parameter given to the Executable's run method
		 */
		private ExecuteMouseListener(final Executable<T> executable,
				final T argument)
		{
			this.executable = executable;
			this.argument = argument;
		}

		/**
		 * @see MouseListener
		 */
		@Override
		public void mouseClicked(final MouseEvent e)
		{
		}

		/**
		 * @see MouseListener
		 */
		@Override
		public void mousePressed(final MouseEvent e)
		{
			if (e.getClickCount() == 2)
			{
				executable.run(argument);
			}
		}

		/**
		 * @see MouseListener
		 */
		@Override
		public void mouseReleased(final MouseEvent e)
		{
		}

		/**
		 * @see MouseListener
		 */
		@Override
		public void mouseEntered(final MouseEvent e)
		{
		}

		/**
		 * @see MouseListener
		 */
		@Override
		public void mouseExited(final MouseEvent e)
		{
		}
	}

}
