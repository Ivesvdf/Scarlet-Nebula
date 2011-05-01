package be.ac.ua.comp.scarletnebula.gui.keywizards;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.PlainTextVerifier;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class SelectNewKeynamePage extends WizardPage
{
	private static final long serialVersionUID = 1L;
	private final CloudProvider provider;
	private final JTextField namefield = new JTextField();

	public SelectNewKeynamePage(CloudProvider provider)
	{
		super(new BorderLayout());
		final BetterTextLabel toptext = new BetterTextLabel(
				"Choose a name for the SSH key you'll use to establish SSH connections to servers.");
		toptext.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		namefield
				.setInputVerifier(new PlainTextVerifier(
						namefield,
						"An SSH key name must be at least 1 character long and may only contain letters and digits."));
		final String username = System.getProperty("user.name");
		namefield.setText((username != null ? username : "default") + "key");

		final FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, min(50dlu;p):grow", "");
		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.append("Name", namefield);

		final JPanel namepanel = builder.getPanel();
		namepanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		add(namepanel, BorderLayout.CENTER);
		add(toptext, BorderLayout.NORTH);
		this.provider = provider;
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		WizardPage finalPage;
		if (!namefield.getInputVerifier().verify(namefield))
		{
			// do not proceed
			finalPage = null;
		}
		else
		{
			finalPage = new FinalNewKeyPage(provider, namefield.getText());
		}

		return finalPage;
	}

}
