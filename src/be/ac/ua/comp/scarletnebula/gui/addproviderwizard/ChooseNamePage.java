package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.gui.WrappableLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ChooseNamePage extends WizardPage
{
	private static final long serialVersionUID = 1L;
	JTextField name = new JTextField();

	ChooseNamePage(AddProviderWizardDataRecorder recorder)
	{
		setLayout(new BorderLayout());
		WrappableLabel text = new WrappableLabel(
				"What name would you like to use to describe this account with "
						+ recorder.getTemplate().getName() + "?");
		text.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
		add(text, BorderLayout.NORTH);

		// And the textfields below
		FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, max(50dlu;p):grow, 7dlu", "");

		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.append("Account Name", name);
		add(builder.getPanel());

		// Prefill the textfield with something useful

		name.setText(recorder.getTemplate().getShortName() + " ("
				+ recorder.getEndpoint().getShortName() + ")");
		name.setPreferredSize(name.getMinimumSize());

	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		String providerName = name.getText();

		// Check to see if this name hasn't been taken
		for (String aProviderName : CloudManager.get()
				.getLinkedCloudProviderNames())
		{
			if (aProviderName.equals(providerName))
			{
				JOptionPane
						.showMessageDialog(
								this,
								"The name you provided for this CloudProvider is already in use. Please choose another one.",
								"CloudProvider name in use",
								JOptionPane.ERROR_MESSAGE);
				return null;
			}

		}

		rec.setName(providerName);

		return new FinishPage(rec);
	}

}
