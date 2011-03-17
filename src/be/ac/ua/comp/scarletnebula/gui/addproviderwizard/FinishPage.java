package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class FinishPage extends WizardPage
{
	private static final long serialVersionUID = 1L;

	public FinishPage(AddProviderWizardDataRecorder rec)
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		String txt = "<html>Press the Finish button to create the CloudProvider "
				+ rec.getName()
				+ ".\n\nThis CloudProvider connects to "
				+ rec.getTemplate().getName()
				+ " on its "
				+ rec.getEndpoint().getName() + " endpoint.";

		CloudProvider tmpProvider = new CloudProvider(rec.getName(), rec
				.getTemplate().getClassname(), rec.getEndpoint().getURL(),
				rec.getApiKey(), rec.getApiSecret());

		boolean credentialsOk = tmpProvider.test();

		if (credentialsOk)
		{
			txt += "<br/><br/><font color=\"green\"><b>Succesfully connected to this CloudProvider. </font></html>";
		}
		else
		{
			txt += "<br/><br/><font color=\"red\"><b>Warning!</b> Could not connect to this CloudProvider! "
					+ "Continue at your own risk or press the Previous button to try again. </font></html>";
		}
		BetterTextLabel toptext = new BetterTextLabel(txt);

		toptext.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(toptext);
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

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		return null;
	}
}
