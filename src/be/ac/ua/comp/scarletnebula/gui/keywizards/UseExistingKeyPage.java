package be.ac.ua.comp.scarletnebula.gui.keywizards;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.SelectKeyList;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class UseExistingKeyPage extends WizardPage
{
	private static final long serialVersionUID = 1L;
	SelectKeyList keylist;
	private final CloudProvider provider;

	UseExistingKeyPage(CloudProvider provider)
	{
		this.provider = provider;
		final BetterTextLabel lbl = new BetterTextLabel(
				"Select the key you want to use from the following list. "
						+ "Note that you'll be asked to provide a file containing the actual key later on.");
		lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

		setLayout(new BorderLayout());

		add(lbl, BorderLayout.PAGE_START);

		keylist = new SelectKeyList(provider);
		try
		{
			keylist.fillWithUnknownKeys();
		}
		catch (final InternalException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final CloudException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final JScrollPane listScrollPane = new JScrollPane(keylist);
		listScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(15, 20, 20, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
		add(listScrollPane, BorderLayout.CENTER);

	}

	@Override
	public WizardPage next(DataRecorder recorder)
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
