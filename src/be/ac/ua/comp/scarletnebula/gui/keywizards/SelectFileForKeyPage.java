/*
 * Copyright (C) 2011  Ives van der Flaas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package be.ac.ua.comp.scarletnebula.gui.keywizards;

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

public class SelectFileForKeyPage extends WizardPage {
	private static final long serialVersionUID = 1L;
	private final JFileChooser fileChooser = new JFileChooser();
	private final JLabel selectedLabel = new JLabel("No file selected");
	private File selectedFile;
	private final String keyname;
	private CloudProvider provider;

	SelectFileForKeyPage(final CloudProvider provider, final String keyname) {
		final BetterTextLabel txt = new BetterTextLabel(
				"Please select the keyfile associated with SSH key " + keyname
						+ ".");
		this.keyname = keyname;
		txt.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new BorderLayout());
		add(txt, BorderLayout.NORTH);
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		final JButton openButton = new JButton("Browse...");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final int rv = fileChooser
						.showOpenDialog(SelectFileForKeyPage.this);
				if (rv == JFileChooser.APPROVE_OPTION) {
					final File selectedFile = fileChooser.getSelectedFile();
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
	public WizardPage next(final DataRecorder recorder) {
		if (selectedFile.length() == 0) {
			JOptionPane.showMessageDialog(this, "Please select a key-file.",
					"Select keyfile", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return new FinalKeyImportPage(provider, keyname, selectedFile);
	}
}
