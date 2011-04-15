package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.BorderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.gui.TaggingPanel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class TaggingPage extends WizardPage
{
	private static Log log = LogFactory.getLog(TaggingPage.class);
	private static final long serialVersionUID = 1L;
	TaggingPanel taggingPanel = new TaggingPanel();

	TaggingPage()
	{
		setLayout(new BorderLayout());

		BetterTextLabel lbl = new BetterTextLabel(
				"Enter some labels that describe the functionality of this server. E.g. dns, webserver, ...");
		lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(lbl, BorderLayout.NORTH);
		taggingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		add(taggingPanel, BorderLayout.CENTER);
		/*
		 * final JTextField inputField = new JTextField();
		 * inputField.setBorder(BorderFactory
		 * .createBevelBorder(BevelBorder.LOWERED)); ActionListener
		 * addTagActionListener = new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { String tagTxt
		 * = inputField.getText(); inputField.setText(""); taglist.addTag(new
		 * TagItem(tagTxt)); } };
		 * inputField.addActionListener(addTagActionListener);
		 * 
		 * final JButton addButton = new JButton(new ImageIcon(getClass()
		 * .getResource("/images/add16.png")));
		 * addButton.addActionListener(addTagActionListener);
		 * bottomPanel.add(inputField, BorderLayout.NORTH);
		 * 
		 * taglist.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED)
		 * );
		 * 
		 * JScrollPane tagScrollPane = new JScrollPane(taglist);
		 * bottomPanel.add(tagScrollPane, BorderLayout.CENTER);
		 * bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20,
		 * 20));
		 * 
		 * bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0,
		 * 100)); add(bottomPanel, BorderLayout.CENTER);
		 * 
		 * inputField.requestFocusInWindow();
		 */
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		// Extract tags
		Collection<String> tags = taggingPanel.getTags();
		AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;
		rec.tags = tags;
		return new FinalServerAddPage(rec);
	}

}
