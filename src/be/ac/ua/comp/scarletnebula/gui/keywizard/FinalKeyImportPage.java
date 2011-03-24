package be.ac.ua.comp.scarletnebula.gui.keywizard;

import java.awt.BorderLayout;
import java.io.File;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.KeyManager;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class FinalKeyImportPage extends WizardPage
{
	private static final long serialVersionUID = 1L;
	private CloudProvider provider;
	private String keyname;
	private File keyFile;

	FinalKeyImportPage(CloudProvider provider, String keyname, File keyFile)
	{
		this.provider = provider;
		this.keyname = keyname;
		this.keyFile = keyFile;
		setLayout(new BorderLayout());
		add(new BetterTextLabel("Press finish to create a new SSH key named "
				+ keyname + " from file " + keyFile.getName() + "."));
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		KeyManager.addKey(provider.getName(), keyname, keyFile);
		return null;
	}

	@Override
	public boolean nextIsEnabled()
	{
		return false;
	}

	@Override
	public boolean finishIsEnabled()
	{
		return true;
	}

}
