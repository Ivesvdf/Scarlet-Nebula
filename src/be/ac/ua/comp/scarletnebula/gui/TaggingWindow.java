package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class TaggingWindow extends JDialog
{
	TaggingWindow(final JDialog parent)
	{
		super(parent, "Edit tags", true);
		setLayout(new BorderLayout());
		setSize(500, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationByPlatform(true);
		final TaggingPanel taggingPanel = new TaggingPanel();
		taggingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(taggingPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				TaggingWindow.this.dispose();
			}
		});

		okButton.setMargin(new Insets(0, 10, 0, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(15));

		setVisible(true);
	}
}
