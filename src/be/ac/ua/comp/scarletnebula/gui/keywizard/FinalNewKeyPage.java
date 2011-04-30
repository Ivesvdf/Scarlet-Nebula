package be.ac.ua.comp.scarletnebula.gui.keywizard;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.ChooseImagePage;

public class FinalNewKeyPage extends AbstractFinalKeyWizardPage
{
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ChooseImagePage.class);

	private CloudProvider provider;
	private String keyname;

	public FinalNewKeyPage(CloudProvider provider, String keyname)
	{
		super(provider, "Click Finish to create a new SSH key named " + keyname
				+ " for provider " + provider.getName(), keyname);
		this.provider = provider;
		this.keyname = keyname;
	}

	@Override
	protected boolean performAction()
	{
		boolean result = true;
		try
		{
			provider.createKey(keyname);
		}
		catch (Exception e)
		{
			log.error("Could not create key", e);
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Error creating key", JOptionPane.ERROR_MESSAGE);
			result = false;
		}
		return result;
	}

}
