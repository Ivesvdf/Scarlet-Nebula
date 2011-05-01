package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.misc.Utils;

/**
 * A component that switches between a JLabel and an icon-style button and
 * between a JTextField based on user input.
 * 
 * @author ives
 * 
 */
public class LabelEditSwitcherPanel extends JPanel implements MouseListener,
		KeyListener
{
	private static final long serialVersionUID = 1L;
	private String content;
	private final Collection<ContentChangedListener> listeners = new ArrayList<ContentChangedListener>();
	private final JTextField textField;

	/**
	 * Constructs a LabelEditSwitcherPanel based on the initial JLabel content
	 * that will be shown
	 * 
	 * @param initialContent
	 *            The text that will initially be shown by the JLabel
	 */
	public LabelEditSwitcherPanel(String initialContent)
	{
		this(initialContent, new JTextField());
	}

	/**
	 * Constructs a LabelEditSwitcherPanel based on the initial JLabel content
	 * that will be shown
	 * 
	 * @param initialContent
	 *            The text that will initially be shown by the JLabel
	 * @param theTextField
	 *            The JTextField that will be shown after the user goes from
	 *            display to edit mode
	 */
	public LabelEditSwitcherPanel(String initialContent, JTextField theTextField)
	{
		super(new BorderLayout());
		addMouseListener(this);
		addKeyListener(this);
		this.textField = theTextField;
		textField.addKeyListener(this);
		textField.addActionListener(new TryGoingBackToLabelActionHandler(
				textField));

		content = initialContent;
		fillWithLabel();
	}

	/**
	 * Fills the component with everything required by display mode
	 */
	final private void fillWithLabel()
	{
		setLayout(new GridBagLayout());

		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel(content), c);
		final JButton editButton = new ToolbarStyleButton(
				Utils.icon("settings16.png"));
		editButton.addActionListener(new TryGoingBackToEditActionHandler());
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.gridx = 1;
		add(editButton, c);
	}

	/**
	 * Fills the component with everything required by edit mode
	 */
	final protected void fillWithEdit()
	{
		setLayout(new BorderLayout());
		textField.setText(content);

		add(textField, BorderLayout.CENTER);
		textField.requestFocusInWindow();
	}

	/**
	 * Add a listener that will be updated when going from edit to display mode
	 * 
	 * @param listener
	 *            The listener to be added
	 */
	public void addContentChangedListener(ContentChangedListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * The action handler that will attempt going to edit mode
	 * 
	 * @author ives
	 * 
	 */
	private final class TryGoingBackToEditActionHandler implements
			ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			goToEdit();
		}
	}

	/**
	 * The action handler that will attempt going to display mode
	 * 
	 * @author ives
	 * 
	 */
	private final class TryGoingBackToLabelActionHandler implements
			ActionListener
	{
		private final JTextField edit;

		private TryGoingBackToLabelActionHandler(JTextField edit)
		{
			this.edit = edit;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Check if input is valid before switching
			if (edit.getInputVerifier() != null)
			{
				if (!edit.getInputVerifier().verify(edit))
					return;
			}
			content = edit.getText();

			goToLabel();

			for (final ContentChangedListener l : listeners)
			{
				l.changed(content);
			}
		}
	}

	/**
	 * The interface you need to implement if you wish to be notified when the
	 * component changes from edit to display mode.
	 * 
	 * @author ives
	 * 
	 */
	public interface ContentChangedListener
	{
		void changed(String newContents);
	}

	/**
	 * @see MouseListener
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	/**
	 * @see MouseListener
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getClickCount() == 2)
		{
			// Hack to see if we're in labelmode
			if (getComponentCount() == 2)
				goToEdit();
		}

	}

	/**
	 * @see MouseListener
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	/**
	 * @see MouseListener
	 */
	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	/**
	 * @see MouseListener
	 */
	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	/**
	 * Moves the component from display to edit mode
	 */
	private void goToEdit()
	{
		removeAll();
		fillWithEdit();
		revalidate();
	}

	/**
	 * Moves the component from edit to display mode if and only if it passes
	 * validation.
	 */
	private void goToLabel()
	{
		removeAll();
		fillWithLabel();
		revalidate();
	}

	/**
	 * @see KeyListener
	 */
	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see KeyListener
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			// Hack to see if we're in edit mode
			if (getComponentCount() == 1)
			{
				goToLabel();
			}
		}
	}

	/**
	 * @see KeyListener
	 */
	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}
}
