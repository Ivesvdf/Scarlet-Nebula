package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;

public class StatisticsPropertiesWindow extends JDialog
{
	private static final long serialVersionUID = 1L;
	final private Collection<ActionListener> actionListeners = new ArrayList<ActionListener>();
	final private JTextArea commandArea = new JTextArea();
	private int maxEventId = 0;

	public StatisticsPropertiesWindow(JDialog parent, Collection<Server> servers)
	{
		super(parent, "Statistics Properties", false);

		if (servers.size() == 1)
		{
			setTitle("Statistics Properties for "
					+ servers.iterator().next().getFriendlyName());
		}

		setSize(500, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setLocationRelativeTo(parent);
		setLocationByPlatform(true);

		final BetterTextLabel textLabel = new BetterTextLabel(
				"Enter the command that will be executed on the remote server and will return a continuous stream of newline separated JSON fragments. "
						+ "This command can be an entire script or just the execution of a single server-style program.");
		textLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

		commandArea.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		JScrollPane commandAreaScrollPane = new JScrollPane(commandArea);
		commandAreaScrollPane.setBorder(BorderFactory.createEmptyBorder(15, 20,
				15, 20));
		add(textLabel, BorderLayout.NORTH);
		add(commandAreaScrollPane, BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);

	}

	private JPanel getButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		final JButton cancelButton = ButtonFactory.createCancelButton();
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		final JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveAndClose();
			}
		});

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		return buttonPanel;
	}

	public void addActionListener(ActionListener listener)
	{
		actionListeners.add(listener);
	}

	private void saveAndClose()
	{
		dispose();
		final ActionEvent actionEvent = new ActionEvent(this, maxEventId++,
				"Window Closed");
		for (ActionListener listener : actionListeners)
		{
			listener.actionPerformed(actionEvent);
		}
	}
}
