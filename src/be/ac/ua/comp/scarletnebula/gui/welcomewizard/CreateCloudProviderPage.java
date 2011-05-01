package be.ac.ua.comp.scarletnebula.gui.welcomewizard;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.addproviderwizard.AddProviderWizard;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class CreateCloudProviderPage extends WizardPage
{
	private static final long serialVersionUID = 1L;

	CreateCloudProviderPage()
	{
		final AddProviderWizard w = new AddProviderWizard();
		w.startModal((JDialog) getParent());

		if (CloudManager.get().getLinkedCloudProviders().size() > 0)
		{
			// Get the newly created CloudProvider
			final CloudProvider p = CloudManager.get()
					.getLinkedCloudProviders().iterator().next();

			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			final JLabel txt = new JLabel(
					"<html>A CloudProvider by the name of \""
							+ p.getName()
							+ "\" was created. "
							+ "You can now use this CloudProvider account to start new servers, manage existing servers and more. <br /><br />"
							+ "Some quick tips:<br/>"
							+ "<ul>"
							+ "<li>If you want to start a new server press the big '+' button on the bottom left. "
							+ "<li>If you want to link existing servers to Scarlet Nebula, you can do this in the Providers - Manage Linked/Unlinked Instances menu."
							+ "</ul></html>");
			txt.setFont(new Font("Dialog", Font.PLAIN, txt.getFont().getSize()));
			txt.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

			add(txt);
		}
		else
		{
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			final JLabel txt = new JLabel(
					"<html>You chose not to create a new CloudProvider account. If you change your mind, you can always "
							+ "create a new provider in menu <b>Providers - Manage Providers</b>.</html>");
			txt.setFont(new Font("Dialog", Font.PLAIN, txt.getFont().getSize()));
			txt.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

			add(txt);
		}
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
	public WizardPage next(final DataRecorder recorder)
	{
		return null;
	}

}
