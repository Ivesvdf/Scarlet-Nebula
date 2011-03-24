package be.ac.ua.comp.scarletnebula.gui.keywizard;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class CreateUseChoicePage extends WizardPage
{
	private static final long serialVersionUID = 1L;

	final JRadioButton createButton = new JRadioButton("Create a new key");
	final JRadioButton useButton = new JRadioButton("Use an existing key");
	ButtonGroup group = new ButtonGroup();
	CloudProvider provider;

	CreateUseChoicePage(CloudProvider provider)
	{
		setLayout(new BorderLayout());
		this.provider = provider;
		// The text on top
		BetterTextLabel txt = new BetterTextLabel(
				"After starting new servers, you'll connect to these servers through an SSH connection. "
						+ "Before you can securely connect to a server, you need an SSH key. \n\n"
						+ "Would you like to create a new key or use an existing key?");

		txt.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
		add(txt, BorderLayout.PAGE_START);

		group.add(createButton);
		group.add(useButton);
		group.setSelected(createButton.getModel(), true);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.add(createButton);
		buttonPanel.add(Box.createVerticalStrut(10));
		buttonPanel.add(useButton);
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		add(buttonPanel, BorderLayout.CENTER);
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		if (group.getSelection() == createButton.getModel())
		{
			return new AcceptKeyHandlerPage(AcceptKeyHandlerPage.Action.CREATE,
					provider);
		}
		else
		{
			return new UseExistingKeyPage(provider);
		}

	}

}
