package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.BorderFactory;

import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.TaggingPanel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class TaggingPage extends WizardPage
{
	private static final long serialVersionUID = 1L;
	TaggingPanel taggingPanel = new TaggingPanel();

	TaggingPage()
	{
		setLayout(new BorderLayout());

		final BetterTextLabel lbl = new BetterTextLabel(
				"Enter some labels that describe the functionality of this server. E.g. dns, webserver, ...");
		lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(lbl, BorderLayout.NORTH);
		taggingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		add(taggingPanel, BorderLayout.CENTER);

	}

	@Override
	public WizardPage next(final DataRecorder recorder)
	{
		// Extract tags
		taggingPanel.simulateEnter();
		final Collection<String> tags = taggingPanel.getTags();
		final AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;
		rec.tags = tags;
		return new FinalServerAddPage(rec);
	}

}
