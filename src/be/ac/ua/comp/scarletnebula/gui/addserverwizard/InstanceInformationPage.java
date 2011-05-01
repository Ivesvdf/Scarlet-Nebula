package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.util.Collection;

import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.ServernameInputVerifier;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

class InstanceInformationPage extends WizardPage
{

	private static final long serialVersionUID = 1L;
	final JTextField instanceNameField = new JTextField();
	final JComboBox instanceSizeList = new JComboBox();

	InstanceInformationPage(final CloudProvider provider)
	{
		instanceNameField.setInputVerifier(new ServernameInputVerifier(
				instanceNameField));
		instanceNameField.setText(provider.getName() + "-"
				+ provider.listLinkedServers().size());
		instanceNameField.selectAll();

		final Collection<String> sizes = provider.getPossibleInstanceSizes();
		for (final String size : sizes)
		{
			instanceSizeList.addItem(size);
		}

		instanceSizeList.setSelectedIndex(0);

		final FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, max(50dlu;p):grow, 7dlu", "");

		final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.append("Instance name", instanceNameField);
		builder.nextLine();
		builder.append("Instance size", instanceSizeList);

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
			rec.instanceSize = (String) instanceSizeList.getSelectedItem();
			returnPage = new ChooseImagePage(rec.provider);
		}

		return returnPage;
	}
};