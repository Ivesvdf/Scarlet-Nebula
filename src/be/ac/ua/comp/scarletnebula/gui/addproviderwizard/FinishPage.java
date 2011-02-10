package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import be.ac.ua.comp.scarletnebula.gui.WrappableLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class FinishPage extends WizardPage
{
	private static final long serialVersionUID = 1L;

	public FinishPage(AddProviderWizardDataRecorder recorder)
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		WrappableLabel toptext = new WrappableLabel(
				"Press the Finish button to create the CloudProvider "
						+ recorder.getName()
						+ ".\n\nThis CloudProvider connects to "
						+ recorder.getTemplate().getName() + " on its "
						+ recorder.getEndpoint().getName() + " endpoint.");

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
