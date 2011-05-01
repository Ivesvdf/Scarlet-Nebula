package be.ac.ua.comp.scarletnebula.gui.windows;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.keywizards.ImportKeyWizard;
import be.ac.ua.comp.scarletnebula.gui.keywizards.NewKeyWizard;
import be.ac.ua.comp.scarletnebula.misc.Utils;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;

public class InteractiveKeyPanel extends JPanel
{
	private final class ClearAndPlaceWizardListener implements WizardListener
	{
		private final CloudProvider provider;

		private ClearAndPlaceWizardListener(CloudProvider provider)
		{
			this.provider = provider;
		}

		@Override
		public void onFinish(DataRecorder recorder)
		{
			clearAndPlaceComponents(provider);
		}

		@Override
		public void onCancel(DataRecorder recorder)
		{

		}
	}

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel(new BorderLayout());

	public InteractiveKeyPanel(final CloudProvider provider)
	{
		super(new BorderLayout());

		placeComponents(provider);
	}

	private void placeComponents(final CloudProvider provider)
	{
		final JButton addButton = new JButton(Utils.icon("add22.png"));
		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				NewKeyWizard wiz = new NewKeyWizard((JDialog) Utils
						.findWindow(InteractiveKeyPanel.this), provider);
				wiz.addWizardListener(new ClearAndPlaceWizardListener(provider));

				wiz.start();
			}
		});

		final JButton modifyButton = new JButton("Import Key");
		modifyButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ImportKeyWizard wiz = new ImportKeyWizard((JDialog) Utils
						.findWindow(InteractiveKeyPanel.this), provider);

				wiz.addWizardListener(new ClearAndPlaceWizardListener(provider));

				wiz.start();

			}
		});

		final JButton removeButton = new JButton(Utils.icon("remove22.png"));
		removeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

			}
		});

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

		final GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1.0;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;

		buttonPanel.add(addButton, c);

		c.gridx = 1;
		buttonPanel.add(modifyButton, c);

		c.gridx = 2;
		buttonPanel.add(removeButton, c);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void clearAndPlaceComponents(CloudProvider provider)
	{
		removeAll();
		placeComponents(provider);
	}
}
