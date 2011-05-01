package be.ac.ua.comp.scarletnebula.gui.keywizard;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.ChooseImagePage;

public class FinalNewKeyPage extends AbstractFinalKeyWizardPage
{
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ChooseImagePage.class);

	private final CloudProvider provider;
	private final String keyname;

	public FinalNewKeyPage(CloudProvider provider, String keyname)
	{
		super(provider, "Click Finish to create a new SSH key named " + keyname
				+ " for provider " + provider.getName(), keyname);
		this.provider = provider;
		this.keyname = keyname;
	}

	@Override
	protected void performAction()
	{
		(new SwingWorker<Exception, Object>()
		{
			@Override
			protected Exception doInBackground() throws Exception
			{
				try
				{
					provider.createKey(keyname, makeKeyDefault());
				}
				catch (final Exception e)
				{
					return e;
				}
				return null;
			}

			@Override
			public void done()
			{
				try
				{
					final Exception result = get();

					if (result != null)
					{
						log.error("Could not create key", result);
						JOptionPane
								.showMessageDialog(FinalNewKeyPage.this,
										result.getLocalizedMessage(),
										"Error creating key",
										JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (final Exception ignore)
				{
				}
			}
		}).execute();
	}
}
