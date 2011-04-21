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
public class TaggingWindow extends JDialog
{
	private static final long serialVersionUID = 1L;
	private final TaggingPanel taggingPanel;
	private Collection<WindowClosedListener> windowClosedListeners = new ArrayList<WindowClosedListener>();

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            The parent of this dialog (this dialog will be modal)
	 * @param tags
	 *            The initial tags to be displayed in this windows
	 */
	TaggingWindow(final JDialog parent, Collection<String> tags)
	{
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
	private JPanel getButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
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
	interface WindowClosedListener
	{
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
	private void notifyClosedListeners()
	{
		for (WindowClosedListener listener : windowClosedListeners)
			listener.windowClosed(taggingPanel.getTags());
	}

	/**
	 * This method will notify all observers and dispose of the window. This
	 * method will be called by closing the window or clicking the OK button.
	 */
	public void close()
	{
		notifyClosedListeners();
		dispose();
	}

	/**
	 * Subscribe as a WindowClosedListener to this TaggingWindow
	 * 
	 * @param listener
	 *            The class subscribing
	 */
	public void addWindowClosedListener(WindowClosedListener listener)
	{
		windowClosedListeners.add(listener);
	}

	/**
	 * Class that implements the WindowListener that will call the close()
	 * method on the TaggingWindow when the JDialog window closes.
	 * 
	 * @author ives
	 * 
	 */
	public class TagginWindowClosingWindowListener implements WindowListener
	{
		/**
		 * @see WindowListener
		 */
		@Override
		public void windowOpened(WindowEvent e)
		{
		}

		/**
		 * @see WindowListener
		 */
		@Override
		public void windowIconified(WindowEvent e)
		{
		}

		/**
		 * @see WindowListener
		 */
		@Override
		public void windowDeiconified(WindowEvent e)
		{
		}

		/**
		 * @see WindowListener
		 */
		@Override
		public void windowDeactivated(WindowEvent e)
		{
		}

		/**
		 * Method that calls the close() method on TaggingWindow when the window
		 * is supposed to close. This will update observers and dispose of the
		 * Window
		 */
		@Override
		public void windowClosing(WindowEvent e)
		{
			TaggingWindow window = (TaggingWindow) e.getSource();
			window.close();
		}

		/**
		 * @see WindowListener
		 */
		@Override
		public void windowClosed(WindowEvent e)
		{
		}

		/**
		 * @see WindowListener
		 */
		@Override
		public void windowActivated(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}
	}
}
