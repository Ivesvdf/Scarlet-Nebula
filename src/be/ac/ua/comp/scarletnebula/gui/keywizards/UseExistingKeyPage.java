package be.ac.ua.comp.scarletnebula.gui.keywizards;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.CollapsablePanel;
import be.ac.ua.comp.scarletnebula.gui.SelectKeyList;
import be.ac.ua.comp.scarletnebula.gui.ThrobberBarWithText;
import be.ac.ua.comp.scarletnebula.misc.SwingWorkerWithThrobber;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class UseExistingKeyPage extends WizardPage
{
	private static final long serialVersionUID = 1L;
	SelectKeyList keylist;
	private final CloudProvider provider;

	UseExistingKeyPage(final CloudProvider provider)
	{
		this.provider = provider;
		final BetterTextLabel lbl = new BetterTextLabel(
				"Select the key you want to use from the following list. "
						+ "Note that you'll be asked to provide a file containing the actual key later on.");
		lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

		setLayout(new BorderLayout());

		keylist = new SelectKeyList(provider);

		final ThrobberBarWithText throbber = new ThrobberBarWithText(
				"Loading keys");
		final JPanel throbberContainer = new JPanel(new BorderLayout());
		throbberContainer.add(throbber, BorderLayout.CENTER);
		throbberContainer.setBorder(BorderFactory.createEmptyBorder(15, 0, 15,
				0));

		final CollapsablePanel collapsableThrobber = new CollapsablePanel(
				throbberContainer, false);

		final JPanel aboveKeylist = new JPanel(new BorderLayout());
		aboveKeylist.add(lbl, BorderLayout.PAGE_START);
		aboveKeylist.add(collapsableThrobber, BorderLayout.PAGE_END);
		add(aboveKeylist, BorderLayout.NORTH);

		(new SwingWorkerWithThrobber<Object, String>(collapsableThrobber)
		{
			@Override
			protected Object doInBackground() throws Exception
			{
				for (final String keyname : provider.getUnknownKeys())
				{
					publish(keyname);
				}

				return null;
			}

			@Override
			protected void process(final List<String> keynames)
			{
				for (final String keyname : keynames)
				{
					keylist.add(keyname);
				}
			}
		}).execute();

		final JScrollPane listScrollPane = new JScrollPane(keylist);
		listScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(15, 20, 20, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
		add(listScrollPane, BorderLayout.CENTER);

	}

	@Override
	public WizardPage next(final DataRecorder recorder)
	{
		final String selectedKey = keylist.getSelectedKey();

		if (selectedKey == null)
		{
			JOptionPane.showMessageDialog(this, "Please select a key.");
			return null;
		}
		return new SelectFileForKeyPage(provider, keylist.getSelectedKey());
	}

}
