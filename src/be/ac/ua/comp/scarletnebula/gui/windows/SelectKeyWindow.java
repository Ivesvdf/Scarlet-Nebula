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
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.ButtonFactory;
import be.ac.ua.comp.scarletnebula.gui.SelectKeyList;

public abstract class SelectKeyWindow extends JDialog {

	private static final long serialVersionUID = 1L;
	private final SelectKeyList selectKeylist;

	public abstract void onOk(String keyname);

	public SelectKeyWindow(final JDialog parent, final CloudProvider provider) {
		super(parent, "Select key", true);
		setLayout(new BorderLayout());
		setSize(400, 300);

		selectKeylist = new SelectKeyList(provider);
		selectKeylist.fillWithKnownKeys();

		final JScrollPane keyScrollPane = new JScrollPane(selectKeylist);
		keyScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 20, 10),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		add(keyScrollPane, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		final JButton okButton = ButtonFactory.createOkButton();
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				onOk(selectKeylist.getSelectedKey());
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(20));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		add(buttonPanel, BorderLayout.SOUTH);

		setLocationRelativeTo(parent);
		setLocationByPlatform(true);
		setVisible(true);
	}

}
