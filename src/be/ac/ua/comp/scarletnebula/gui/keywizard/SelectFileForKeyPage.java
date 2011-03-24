package be.ac.ua.comp.scarletnebula.gui.keywizard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextLabel;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class SelectFileForKeyPage extends WizardPage
{
	private static final long serialVersionUID = 1L;
	private JFileChooser fileChooser = new JFileChooser();
	private JLabel selectedLabel = new JLabel("No file selected");
	private File selectedFile;
	private String keyname;
	private CloudProvider provider;

	SelectFileForKeyPage(CloudProvider provider, String keyname)
	{
		BetterTextLabel txt = new BetterTextLabel(
				"Please select the keyfile associated with SSH key " + keyname
						+ ".");
		this.keyname = keyname;
		txt.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new BorderLayout());
		add(txt, BorderLayout.NORTH);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		JButton openButton = new JButton("Browse...");
		openButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int rv = fileChooser.showOpenDialog(SelectFileForKeyPage.this);
				if (rv == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					SelectFileForKeyPage.this.selectedFile = selectedFile;
					selectedLabel.setText(selectedFile.getName());
					selectedLabel.revalidate();
				}
			}
		});
		mainPanel.add(openButton);
		mainPanel.add(selectedLabel);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

		add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		if (selectedFile.length() == 0)
		{
			JOptionPane.showMessageDialog(this, "Please select a key-file.",
					"Select keyfile", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return new FinalKeyImportPage(provider, keyname, selectedFile);
	}
}
