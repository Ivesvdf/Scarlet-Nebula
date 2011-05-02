package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import javax.swing.InputVerifier;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.inputverifiers.ServernameInputVerifier;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

class InstanceInformationPage extends WizardPage
{

	private static final long serialVersionUID = 1L;
	final JTextField instanceNameField = new JTextField();

	InstanceInformationPage(final CloudProvider provider)
	{
		instanceNameField.setInputVerifier(new ServernameInputVerifier(
				instanceNameField));
		instanceNameField.setText(provider.getName() + "-"
				+ provider.listLinkedServers().size());
		instanceNameField.selectAll();

		final FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, max(50dlu;p):grow, 7dlu", "");

		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.append("Instance name", instanceNameField);
		builder.nextLine();

		add(builder.getPanel());
	}

	@Override
	public WizardPage next(final DataRecorder recorder)
	{
		WizardPage returnPage;
		final InputVerifier inputVerifier = instanceNameField
				.getInputVerifier();
		if (inputVerifier != null && !inputVerifier.verify(instanceNameField))
		{
			returnPage = null;
		}
		else
		{
			final AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;

			rec.instanceName = instanceNameField.getText();
			returnPage = new ChooseImagePage(rec.provider);
		}

		return returnPage;
	}
};