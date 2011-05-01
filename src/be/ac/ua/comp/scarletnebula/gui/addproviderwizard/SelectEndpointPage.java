package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.CloudProviderTemplate;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class SelectEndpointPage extends WizardPage
{
	private static final long serialVersionUID = 1253358185847124985L;
	JList endpoints = null;
	CloudProviderTemplate template = null;

	SelectEndpointPage(final CloudProviderTemplate template)
	{
		this.template = template;

		final Collection<String> endpointNames = new ArrayList<String>();

		for (final CloudProviderTemplate.Endpoint e : template.getEndPoints())
		{
			endpointNames.add(e.getName());
		}

		endpoints = new JList(endpointNames.toArray());
		endpoints.setSelectedIndex(0);
		endpoints.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		final JScrollPane scrollPane = new JScrollPane(endpoints);
		scrollPane.setBorder(new EmptyBorder(20, 20, 10, 20));

		// providerlist.setPreferredSize(new Dimension(260, 20));
		setLayout(new BorderLayout());
		final BetterTextLabel endpointlabel = new BetterTextLabel(
				"Select the endpoint you would like to connect to. ");
		endpointlabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
		add(endpointlabel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

	}

	@Override
	public WizardPage next(final DataRecorder recorder)
	{
		CloudProviderTemplate.Endpoint endpoint = null;

		for (final CloudProviderTemplate.Endpoint e : template.getEndPoints())
		{
			if (e.getName().equals(endpoints.getSelectedValue()))
			{
				endpoint = e;
				break;
			}
		}

		final AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		rec.setEndpoint(endpoint);

		return new ProvideAccessPage();
	}

}
