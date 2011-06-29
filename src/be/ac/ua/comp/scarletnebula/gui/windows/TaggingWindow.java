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

package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;
import be.ac.ua.comp.scarletnebula.gui.TaggingPanel;

/**
 * A window that serves to add new tags to and remove tags from a server.
 * 
 * @author ives
 */
public class TaggingWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	private final TaggingPanel taggingPanel;
	private final Collection<WindowClosedListener> windowClosedListeners = new ArrayList<WindowClosedListener>();

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            The parent of this dialog (this dialog will be modal)
	 * @param tags
	 *            The initial tags to be displayed in this windows
	 */
	TaggingWindow(final JDialog parent, final Collection<String> tags) {
		super(parent, "Edit tags", true);
		setLayout(new BorderLayout());
		setSize(300, 300);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationByPlatform(true);

		taggingPanel = new TaggingPanel(tags);
		taggingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));
		add(taggingPanel, BorderLayout.CENTER);

		final JPanel buttonPanel = getButtonPanel();
		add(buttonPanel, BorderLayout.SOUTH);

		addWindowListener(new TagginWindowClosingWindowListener());
	}

	/**
	 * Gets the button panel containing the OK button.
	 * 
	 * @return The button panel containing the OK button.
	 */
	private JPanel getButtonPanel() {
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		final JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				close();
			}
		});

		okButton.requestFocusInWindow();
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(15));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
		return buttonPanel;
	}

	/**
	 * A simple interface containing only one method, which will be called when
	 * the TaggingWindow one would subscribe to is closed. The implementors of
	 * this interface will then receive the set of tags contained in the Window
	 * upon closing.
	 * 
	 * @author ives
	 * 
	 */
	interface WindowClosedListener {
		/**
		 * Called when the TaggingWindow is closed
		 * 
		 * @param newTags
		 *            The collection of tags after the window closes
		 */
		void windowClosed(Collection<String> newTags);
	}

	/**
	 * Method to notify all subscribed implementors of WindowClosedListener.
	 * These will be called with the new set of tags.
	 */
	private void notifyClosedListeners() {
		for (final WindowClosedListener listener : windowClosedListeners) {
			listener.windowClosed(taggingPanel.getTags());
		}
	}

	/**
	 * This method will notify all observers and dispose of the window. This
	 * method will be called by closing the window or clicking the OK button.
	 */
	public void close() {
		notifyClosedListeners();
		dispose();
	}

	/**
	 * Subscribe as a WindowClosedListener to this TaggingWindow
	 * 
	 * @param listener
	 *            The class subscribing
	 */
	public void addWindowClosedListener(final WindowClosedListener listener) {
		windowClosedListeners.add(listener);
	}

	/**
	 * Class that implements the WindowListener that will call the close()
	 * method on the TaggingWindow when the JDialog window closes.
	 * 
	 * @author ives
	 * 
	 */
	public class TagginWindowClosingWindowListener implements WindowListener {
		/**
		 * @see WindowListener
		 */
		@Override
		public void windowOpened(final WindowEvent e) {
		}

		/**
		 * @see WindowListener
		 */
		@Override
		public void windowIconified(final WindowEvent e) {
		}

		/**
		 * @see WindowListener
		 */
		@Override
		public void windowDeiconified(final WindowEvent e) {
		}

		/**
		 * @see WindowListener
		 */
		@Override
		public void windowDeactivated(final WindowEvent e) {
		}

		/**
		 * Method that calls the close() method on TaggingWindow when the window
		 * is supposed to close. This will update observers and dispose of the
		 * Window
		 */
		@Override
		public void windowClosing(final WindowEvent e) {
			final TaggingWindow window = (TaggingWindow) e.getSource();
			window.close();
		}

		/**
		 * @see WindowListener
		 */
		@Override
		public void windowClosed(final WindowEvent e) {
		}

		/**
		 * @see WindowListener
		 */
		@Override
		public void windowActivated(final WindowEvent e) {
			// TODO Auto-generated method stub

		}
	}
}
