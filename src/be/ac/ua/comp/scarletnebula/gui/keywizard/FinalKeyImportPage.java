package be.ac.ua.comp.scarletnebula.gui.keywizard;

import java.awt.BorderLayout;
import java.io.File;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.KeyManager;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;

public class FinalKeyImportPage extends AbstractFinalKeyWizardPage
{
	private static final long serialVersionUID = 1L;
	private CloudProvider provider;
	private String keyname;
	private File keyFile;

	public FinalKeyImportPage(CloudProvider provider, String keyname,
			File keyFile)
	{
		super(provider, "Click Finish to import the key" + keyname, "");
		this.provider = provider;
		this.keyname = keyname;
		this.keyFile = keyFile;
		setLayout(new BorderLayout());
		add(new BetterTextLabel("Press finish to create a new SSH key named "
				+ keyname + " from file " + keyFile.getName() + "."),
				BorderLayout.NORTH);

	}

	@Override
	protected boolean performAction()
	{
		KeyManager.addKey(provider.getName(), keyname, keyFile);
		return true;
	}

}
