package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.misc.Utils;

public class LabelEditSwitcherPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private String content;
	private InputVerifier inputVerifier = null;
	private Collection<ContentChangedListener> listeners = new ArrayList<ContentChangedListener>();

	LabelEditSwitcherPanel(String initialContent)
	{
		super(new BorderLayout());
		content = initialContent;
		fillWithLabel();
	}

	@Override
	public void setInputVerifier(InputVerifier inputVerifier)
	{
		this.inputVerifier = inputVerifier;
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
		editButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				removeAll();
				fillWithEdit();
				revalidate();
			}
		});
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.gridx = 1;
		add(editButton, c);
	}

	final protected void fillWithEdit()
	{
		setLayout(new BorderLayout());
		final JTextField edit = new JTextField(content, 15);

		if (inputVerifier != null)
			edit.setInputVerifier(inputVerifier);

		edit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Check if input is valid before switching
				if (inputVerifier != null)
				{
					if (!inputVerifier.verify(edit))
						return;
				}
				content = edit.getText();

				for (ContentChangedListener l : listeners)
				{
					l.changed(content);
				}

				removeAll();
				fillWithLabel();
				revalidate();
			}
		});
		add(edit, BorderLayout.CENTER);
	}

	public void addContentChangedListener(ContentChangedListener listener)
	{
		listeners.add(listener);
	}

	interface ContentChangedListener
	{
		void changed(String newContents);
	}
}
