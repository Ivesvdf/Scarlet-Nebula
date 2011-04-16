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

public class TaggingWindow extends JDialog
{
	private static final long serialVersionUID = 1L;
	private final TaggingPanel taggingPanel;
	private Collection<WindowClosedListener> windowClosedListeners = new ArrayList<WindowClosedListener>();

	TaggingWindow(final JDialog parent, Collection<String> tags)
	{
		super(parent, "Edit tags", true);
		taggingPanel = new TaggingPanel(tags);
		setLayout(new BorderLayout());
		setSize(300, 300);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationByPlatform(true);
		taggingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));
		add(taggingPanel, BorderLayout.CENTER);

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
		add(buttonPanel, BorderLayout.SOUTH);

		addWindowListener(new ClosingWindowListener());
	}

	private final class ClosingWindowListener implements WindowListener
	{
		@Override
		public void windowOpened(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void windowIconified(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeactivated(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent e)
		{
			TaggingWindow window = (TaggingWindow) e.getSource();
			window.close();
		}

		@Override
		public void windowClosed(WindowEvent e)
		{

		}

		@Override
		public void windowActivated(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}
	}

	interface WindowClosedListener
	{
		void windowClosed(Collection<String> newTags);
	}

	private void notifyClosedListeners()
	{
		for (WindowClosedListener listener : windowClosedListeners)
			listener.windowClosed(taggingPanel.getTags());
	}

	public void close()
	{
		notifyClosedListeners();
		dispose();
	}

	public void addWindowClosedListener(WindowClosedListener listener)
	{
		windowClosedListeners.add(listener);
	}
}
