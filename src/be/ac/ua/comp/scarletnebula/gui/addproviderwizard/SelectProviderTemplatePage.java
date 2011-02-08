package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.gui.CloudProviderTemplate;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class SelectProviderTemplatePage extends WizardPage
{
	private static final long serialVersionUID = 9100766686174798829L;

	JList providerlist = null;

	SelectProviderTemplatePage()
	{
		Collection<String> names = new ArrayList<String>();
		for (CloudProviderTemplate t : CloudManager.get().getTemplates())
		{
			names.add(t.getName());
		}
		providerlist = new JList(names.toArray());
		providerlist.setSelectedIndex(0);

		JScrollPane scrollPane = new JScrollPane(providerlist);
		scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// providerlist.setPreferredSize(new Dimension(260, 20));
		setLayout(new BorderLayout());
		add(scrollPane);
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		String name = (String) providerlist.getSelectedValue();
		CloudProviderTemplate template = null;

		// Retreive the classname from name
		for (CloudProviderTemplate t : CloudManager.get().getTemplates())
		{
			if (t.getName().equals(name))
				template = t;
		}

		AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		rec.template = template;
		return new SelectEndpointPage(template);
	}

}
