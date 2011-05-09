package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.compute.VmState;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizard;
import be.ac.ua.comp.scarletnebula.misc.Utils;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.SimpleWizardTemplate;
import be.ac.ua.comp.scarletnebula.wizard.Wizard;
import be.ac.ua.comp.scarletnebula.wizard.WizardListener;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class AddServerWizard implements WizardListener
{
	private static Log log = LogFactory.getLog(Server.class);
	private static final long serialVersionUID = 1L;
	private JFrame parent;

	public AddServerWizard(final JFrame parent)
	{
		this.parent = parent;

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
				firstPage = new ChooseImagePage(prov);
				break;
			default:
				firstPage = new ChooseProviderPage();
				break;
		}
		final Wizard wiz = new Wizard(firstPage, rec,
				new SimpleWizardTemplate());
		wiz.addWizardListener(this);
		wiz.startModal("Start new server", 500, 400, parent);
	}

	@Override
	public void onFinish(final DataRecorder recorder)
	{
		final AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;
		final String instancename = rec.instanceName;
		final VirtualMachineProduct instancesize = rec.instanceSize;
		final MachineImage image = rec.image;
		final CloudProvider provider = rec.provider;
		final Collection<String> tags = rec.tags;
		final String keypairOrPassword;

		if (provider.supportsSSHKeys())
		{
			keypairOrPassword = rec.keypairOrPassword != null ? rec.keypairOrPassword
					: provider.getDefaultKeypair();
		}
		else
		{
			keypairOrPassword = Utils.getRandomString(8);
		}
		final Collection<String> firewallIds = rec.firewallIds;
		final int instanceCount = rec.instanceCount;

		if (Server.exists(instancename))
		{
			JOptionPane.showMessageDialog(parent,
					"A server with this name already exists.",
					"Server already exists", JOptionPane.ERROR_MESSAGE);
			return;
		}

		(new SwingWorker<Exception, Server>()
		{

			@Override
			protected Exception doInBackground() throws Exception
			{
				for (int serverStarted = 0; serverStarted < instanceCount; serverStarted++)
				{
					try
					{
						final String localServername;

						if (instanceCount == 1)
						{
							localServername = instancename;
						}
						else
						{
							localServername = instancename + " "
									+ serverStarted;
						}

						final Server server = provider.startServer(
								localServername, instancesize, image, tags,
								keypairOrPassword, firewallIds);
						server.refreshUntilServerHasState(VmState.RUNNING);
					}
					catch (final Exception e)
					{
						return e;
					}
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
						log.error("Could not start server", result);
						JOptionPane.showMessageDialog(null,
								result.getLocalizedMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (final Exception ignore)
				{
				}

			}
		}).execute();

	}

	@Override
	public void onCancel(final DataRecorder recorder)
	{
	}
}
