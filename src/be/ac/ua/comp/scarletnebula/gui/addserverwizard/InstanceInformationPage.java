package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

class InstanceInformationPage extends WizardPage
{

	private static final long serialVersionUID = 1L;
	final JTextField instanceNameField = new JTextField();
	final JComboBox instanceSizeList = new JComboBox();

	InstanceInformationPage(CloudProvider provider)
	{
		instanceNameField.setText(provider.getName() + "-"
				+ provider.listLinkedServers().size());
		instanceNameField.selectAll();

		Collection<String> sizes = provider.getPossibleInstanceSizes();
		for (String size : sizes)
			instanceSizeList.addItem(size);

		instanceSizeList.setSelectedIndex(0);

		FormLayout layout = new FormLayout(
				"right:max(40dlu;p), 4dlu, max(50dlu;p):grow, 7dlu", "");

		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.append("Instance name", instanceNameField);
		builder.nextLine();
		builder.append("Instance size", instanceSizeList);

		add(builder.getPanel());
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;

		rec.instanceName = instanceNameField.getText();
		rec.instanceSize = (String) instanceSizeList.getSelectedItem();
		return new ImageChoicePage(rec.provider);
	}
};