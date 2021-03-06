/*
 * Copyright (C) 2011  Ives van der Flaas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		KeyListener {
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
	public LabelEditSwitcherPanel(final String initialContent) {
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
	public LabelEditSwitcherPanel(final String initialContent,
			final JTextField theTextField) {
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
	final private void fillWithLabel() {
		setLayout(new GridBagLayout());

		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel(content), c);
		final ToolbarStyleButton editButton = new ToolbarStyleButton(
				Utils.icon("settings16.png"),
				Utils.icon("settings_hover16.png"));
		editButton.addActionListener(new TryGoingBackToEditActionHandler());
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.gridx = 1;
		add(editButton, c);
	}

	/**
	 * Fills the component with everything required by edit mode
	 */
	final protected void fillWithEdit() {
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
	public void addContentChangedListener(final ContentChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * The action handler that will attempt going to edit mode
	 * 
	 * @author ives
	 * 
	 */
	private final class TryGoingBackToEditActionHandler implements
			ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
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
			ActionListener {
		private final JTextField edit;

		private TryGoingBackToLabelActionHandler(final JTextField edit) {
			this.edit = edit;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			// Check if input is valid before switching
			if (edit.getInputVerifier() != null) {
				if (!edit.getInputVerifier().verify(edit)) {
					return;
				}
			}
			content = edit.getText();

			goToLabel();

			for (final ContentChangedListener l : listeners) {
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
	public interface ContentChangedListener {
		void changed(String newContents);
	}

	/**
	 * @see MouseListener
	 */
	@Override
	public void mouseClicked(final MouseEvent e) {
	}

	/**
	 * @see MouseListener
	 */
	@Override
	public void mousePressed(final MouseEvent e) {
		if (e.getClickCount() == 2) {
			// Hack to see if we're in labelmode
			if (getComponentCount() == 2) {
				goToEdit();
			}
		}

	}

	/**
	 * @see MouseListener
	 */
	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	/**
	 * @see MouseListener
	 */
	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	/**
	 * @see MouseListener
	 */
	@Override
	public void mouseExited(final MouseEvent e) {
	}

	/**
	 * Moves the component from display to edit mode
	 */
	private void goToEdit() {
		removeAll();
		fillWithEdit();
		revalidate();
	}

	/**
	 * Moves the component from edit to display mode if and only if it passes
	 * validation.
	 */
	private void goToLabel() {
		removeAll();
		fillWithLabel();
		revalidate();
	}

	/**
	 * @see KeyListener
	 */
	@Override
	public void keyTyped(final KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see KeyListener
	 */
	@Override
	public void keyPressed(final KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			// Hack to see if we're in edit mode
			if (getComponentCount() == 1) {
				goToLabel();
			}
		}
	}

	/**
	 * @see KeyListener
	 */
	@Override
	public void keyReleased(final KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
