package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import be.ac.ua.comp.scarletnebula.core.CloudManager;
import be.ac.ua.comp.scarletnebula.core.CloudProviderTemplate;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class SelectProviderTemplatePage extends WizardPage {
	private static final long serialVersionUID = 9100766686174798829L;

	JList providerlist = null;

	SelectProviderTemplatePage() {
		final Collection<String> names = new ArrayList<String>();
		for (final CloudProviderTemplate t : CloudManager.get().getTemplates()) {
			names.add(t.getName());
		}
		providerlist = new JList(names.toArray());
		providerlist.setSelectedIndex(0);
		providerlist.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		final JScrollPane scrollPane = new JScrollPane(providerlist);
		scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));

		setLayout(new BorderLayout());
		add(scrollPane);
	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		final String name = (String) providerlist.getSelectedValue();
		CloudProviderTemplate template = null;

		// Retreive the classname from name
		for (final CloudProviderTemplate t : CloudManager.get().getTemplates()) {
			if (t.getName().equals(name)) {
				template = t;
			}
		}

		final AddProviderWizardDataRecorder rec = (AddProviderWizardDataRecorder) recorder;
		rec.setTemplate(template);

		if (template.getEndPoints().isEmpty()) {
			return new ProvideAccessPage(rec);
		} else {
			return new SelectEndpointPage(template);
		}
	}
}
