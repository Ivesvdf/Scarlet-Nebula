package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

public class ManageProvidersWindow extends JDialog
{
	private static final long serialVersionUID = 1L;

	ManageProvidersWindow(JFrame parent)
	{
		super(parent, "Manage Providers", true);

		setSize(400, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JList providerList = new JList();
		providerList.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		JScrollPane providerListScrollPane = new JScrollPane(providerList);
		providerListScrollPane.setBorder(BorderFactory.createEmptyBorder(20,
				20, 20, 20));

		setLayout(new BorderLayout());

		JButton addButton = new JButton("+");
		JButton modifyButton = new JButton("?");
		JButton removeButton = new JButton("-");

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		buttonPanel.add(addButton);
		buttonPanel.add(modifyButton);
		buttonPanel.add(removeButton);

		add(providerListScrollPane);
		add(buttonPanel, BorderLayout.SOUTH);
		setVisible(true);
	}

}
