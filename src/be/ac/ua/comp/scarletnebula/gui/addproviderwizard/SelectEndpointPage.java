package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import be.ac.ua.comp.scarletnebula.gui.CloudProviderTemplate;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class SelectEndpointPage extends WizardPage
{
	private static final long serialVersionUID = 1253358185847124985L;
	JList endpoints = null;
	CloudProviderTemplate template = null;

	SelectEndpointPage(CloudProviderTemplate template)
	{
		this.template = template;

		Collection<String> endpointNames = new ArrayList<String>();

		for (CloudProviderTemplate.Endpoint e : template.getEndPoints())
			endpointNames.add(e.getName());

		endpoints = new JList(endpointNames.toArray());
		endpoints.setSelectedIndex(0);
		endpoints.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		JScrollPane scrollPane = new JScrollPane(endpoints);
		scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));

		// providerlist.setPreferredSize(new Dimension(260, 20));
		setLayout(new BorderLayout());
		add(scrollPane);

	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		CloudProviderTemplate.Endpoint endpoint = null;

		for (CloudProviderTemplate.Endpoint e : template.getEndPoints())
		{
			if (e.getName().equals(endpoints.getSelectedValue()))
			{
				endpoint = e;
				break;
			}
		}

		AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		rec.setEndpoint(endpoint);

		return new ProvideAccessPage();
	}

}
