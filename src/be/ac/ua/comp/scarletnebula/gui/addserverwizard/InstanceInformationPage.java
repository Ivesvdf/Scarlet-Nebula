package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

class InstanceInformationPage extends WizardPage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final JTextField instanceNameField = new JTextField();
	final JComboBox instanceSizeList = new JComboBox();


	InstanceInformationPage(CloudProvider provider)
	{
		instanceNameField.setText(provider.getName() + "-" + provider.listLinkedServers().size());
		instanceNameField.selectAll();
		instanceNameField.setPreferredSize(new Dimension(100, 25));
		add(new JLabel("Instance name:"));
		add(instanceNameField);

		
		add(new JLabel("Instance size:"));
		Collection<String> sizes = provider.getPossibleInstanceSizes();
		for (String size : sizes)
			instanceSizeList.addItem(size);

		instanceSizeList.setSelectedIndex(0);
		add(instanceSizeList);
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
	public WizardPage next(DataRecorder recorder)
	{
		AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder)recorder;
		
		rec.instanceName = instanceNameField.getText();
		rec.instanceSize = (String)instanceSizeList.getSelectedItem();
		return null;
	}
};