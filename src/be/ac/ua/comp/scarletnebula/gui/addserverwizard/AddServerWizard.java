package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.compute.VmState;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizard;
import be.ac.ua.comp.scarletnebula.gui.windows.GUI;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class AddServerWizard implements WizardListener
{
	private final GUI gui;
	private static Log log = LogFactory.getLog(Server.class);
	private static final long serialVersionUID = 1L;

	public AddServerWizard(JFrame parent, final GUI gui)
	{
		this.gui = gui;

		// Only show the choose provider page if more than one provider is
		// available.
		final AddServerWizardDataRecorder rec = new AddServerWizardDataRecorder();

		WizardPage firstPage = null;

		switch (CloudManager.get().getLinkedCloudProviderNames().size())
		{
			case 0:
				JOptionPane
						.showMessageDialog(
								parent,
								"Please add a new CloudProvider before starting new servers.",
								"First add Provider", JOptionPane.ERROR_MESSAGE);
				final AddProviderWizard wiz = new AddProviderWizard();
				wiz.startModal(parent);
				return;
			case 1: // One provider -- user has to pick this one so skip the
					// page
				final CloudProvider prov = (CloudProvider) CloudManager.get()
						.getLinkedCloudProviders().toArray()[0];
				rec.provider = prov;
				firstPage = new InstanceInformationPage(prov);
				break;
			default:
				firstPage = new ChooseProviderPage();
				break;
		}
		final Wizard wiz = new Wizard(firstPage, rec, new SimpleWizardTemplate());
		wiz.addWizardListener(this);
		wiz.startModal("Start new server", 400, 300, gui);
	}

	@Override
	public void onFinish(DataRecorder recorder)
	{
		final AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;
		final String instancename = rec.instanceName;
		final String instancesize = rec.instanceSize;
		final String image = rec.image;
		final CloudProvider provider = rec.provider;
		final Collection<String> tags = rec.tags;
		final String keypairOrPassword = rec.keypairOrPassword;

		if (Server.exists(instancename))
		{
			JOptionPane.showMessageDialog(gui,
					"A server with this name already exists.",
					"Server already exists", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try
		{
			final Server server = provider.startServer(instancename, instancesize,
					image, tags, keypairOrPassword);
			server.refreshUntilServerHasState(VmState.RUNNING);
		}
		catch (final Exception e)
		{
			log.error("Could not start server", e);
		}
	}

	@Override
	public void onCancel(DataRecorder recorder)
	{
		// TODO Auto-generated method stub

	}
}
