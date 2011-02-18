package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JDialog;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;

public class AddProviderWizard extends Wizard implements WizardListener
{
	Collection<ProviderAddedListener> providerAddedListeners = new ArrayList<ProviderAddedListener>();

	public AddProviderWizard()
	{
		super(new SelectProviderTemplatePage(),
				new AddProviderWizardDataRecorder(), new SimpleWizardTemplate());

		addWizardListener(this);
	}

	public void startModal(JDialog parent)
	{
		startModal("Add a new Cloud Provider", 400, 300, parent);
	}

	public void addProviderAddedListener(ProviderAddedListener pal)
	{
		providerAddedListeners.add(pal);
	}

	@Override
	public void onFinish(DataRecorder recorder)
	{
		AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;

		CloudManager.get().registerNewCloudProvider(rec.getName(),
				rec.getTemplate().getClassname(), rec.getEndpoint().getURL(),
				rec.getApiKey(), rec.getApiSecret());

		for (ProviderAddedListener p : providerAddedListeners)
			p.providerWasAdded(rec.getName());
	}

	@Override
	public void onCancel(DataRecorder recorder)
	{
		// Do nothing...
	}
}
