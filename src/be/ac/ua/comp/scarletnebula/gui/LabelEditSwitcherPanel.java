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

public class LabelEditSwitcherPanel extends JPanel implements MouseListener,
		KeyListener
{
	private static final long serialVersionUID = 1L;
	private String content;
	private Collection<ContentChangedListener> listeners = new ArrayList<ContentChangedListener>();
	private JTextField textField;

	public LabelEditSwitcherPanel(String initialContent)
	{
		this(initialContent, new JTextField());
	}

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

	final private void fillWithLabel()
	{
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
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

	final protected void fillWithEdit()
	{
		setLayout(new BorderLayout());
		textField.setText(content);

		add(textField, BorderLayout.CENTER);
		textField.requestFocusInWindow();
	}

	public void addContentChangedListener(ContentChangedListener listener)
	{
		listeners.add(listener);
	}

	private final class TryGoingBackToEditActionHandler implements
			ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			goToEdit();
		}
	}

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
			for (ContentChangedListener l : listeners)
			{
				l.changed(content);
			}

			goToLabel();
		}
	}

	public interface ContentChangedListener
	{
		void changed(String newContents);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

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

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	private void goToEdit()
	{
		removeAll();
		fillWithEdit();
		revalidate();
	}

	private void goToLabel()
	{
		removeAll();
		fillWithLabel();
		revalidate();
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

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

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}
}
