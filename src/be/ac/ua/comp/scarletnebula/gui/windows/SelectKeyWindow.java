package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;
import be.ac.ua.comp.scarletnebula.gui.SelectKeyList;

public abstract class SelectKeyWindow extends JDialog
{
	public SelectKeyWindow(JDialog parent, CloudProvider provider)
	{
		super(parent, "Select key", true);

		selectKeylist = new SelectKeyList(provider);
		selectKeylist.fillWithKnownKeys();
		selectKeylist
				.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
		add(selectKeylist, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		final JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onOk(selectKeylist.getSelectedKey());
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		setLayout(new BorderLayout());
		setSize(400, 300);
		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		setVisible(true);
	}

	private static final long serialVersionUID = 1L;
	private final SelectKeyList selectKeylist;

	public abstract void onOk(String keyname);

}
