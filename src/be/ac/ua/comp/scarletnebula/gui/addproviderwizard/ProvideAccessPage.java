package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudProviderTemplate.AccessMethod;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ProvideAccessPage extends WizardPage
{
	private static final long serialVersionUID = 1255938294405602870L;
	JTextField apiKey = new JTextField();
	JTextField apiSecret = new JTextField();

	public ProvideAccessPage(final AddProviderWizardDataRecorder rec)
	{
		setLayout(new BorderLayout());

		final String toptext;
		if (rec.getTemplate().getAccessMethod() == AccessMethod.KEY)
		{
			toptext = "Please enter the API Access Key that identifies your account and the API Secret that represents your password.";
		}
		else
		{
			toptext = "Enter the email address and password you used to register with "
					+ rec.getTemplate().getName() + ".";
		}

		// The text on top
		final BetterTextLabel txt = new BetterTextLabel(toptext);

		txt.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

		// And the textfields below
		final FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, max(50dlu;p):grow", "");

		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		final String loginLabel;
		final String passwordLabel;
		if (rec.getTemplate().getAccessMethod() == AccessMethod.KEY)
		{
			loginLabel = "API Key";
			passwordLabel = "API Secret";
		}
		else
		{
			loginLabel = "Email address";
			passwordLabel = "Password";
		}
		builder.append(loginLabel, apiKey);
		builder.nextLine();
		builder.append(passwordLabel, apiSecret);

		add(txt, BorderLayout.NORTH);
		add(builder.getPanel());
	}

	@Override
	public WizardPage next(final DataRecorder recorder)
	{
		final AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		rec.setApiKey(apiKey.getText().trim());
		rec.setApiSecret(apiSecret.getText().trim());

		return new ChooseNamePage(rec);
	}
}
