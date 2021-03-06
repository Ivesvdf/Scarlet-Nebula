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

package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;

import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.VirtualMachineProduct;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class ChooseSizePage extends WizardPage {
	class SizeList extends JList {
		private final class SizeListCellRenderer implements ListCellRenderer {
			@Override
			public Component getListCellRendererComponent(final JList list,
					final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				final VirtualMachineProduct product = (VirtualMachineProduct) value;

				final String specColor = isSelected ? "#FFFFFF" : "#646060";

				final JLabel productLabel = new JLabel(
						"<html><font size=\"5\">" + product.getName()
								+ "</font><br/><font color=\"" + specColor
								+ "\">" + "Number of CPUs: "
								+ product.getCpuCount() + " <br/>RAM: "
								+ product.getRamInMb() + " MB"
								+ "<br/>Hard drive: "
								+ product.getDiskSizeInGb() + " GB<br/>"
								+ "</font></html>");

				productLabel.setOpaque(true);
				if (isSelected) {
					productLabel.setBackground(getSelectionBackground());
					productLabel.setForeground(getSelectionForeground());
				} else {
					productLabel.setBackground(Color.white);
					productLabel.setForeground(getForeground());
				}
				productLabel.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Color.white, 2),
						BorderFactory.createEtchedBorder()));
				return productLabel;
			}
		}

		private static final long serialVersionUID = 1L;

		public SizeList(final CloudProvider provider,
				final Architecture architecture) {
			setCellRenderer(new SizeListCellRenderer());

			final DefaultListModel listModel = new DefaultListModel();
			setModel(listModel);
			fillWithSizes(provider, architecture, listModel);
		}

		private void fillWithSizes(final CloudProvider provider,
				final Architecture architecture,
				final DefaultListModel listModel) {
			for (final VirtualMachineProduct product : provider
					.getPossibleInstanceSizes(architecture)) {
				listModel.addElement(product);
			}
		}
	};

	private static final long serialVersionUID = 1L;
	private final SizeList sizelist;
	private final CloudProvider provider;

	public ChooseSizePage(final CloudProvider provider, final MachineImage image) {
		this.provider = provider;
		final Architecture architecture = image.getArchitecture();

		sizelist = new SizeList(provider, architecture);
		sizelist.setSelectedIndex(0);

		final JScrollPane sizeScrollPane = new JScrollPane(sizelist);
		sizeScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 20, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
		setLayout(new BorderLayout());
		add(sizeScrollPane, BorderLayout.CENTER);
	}

	@Override
	public WizardPage next(final DataRecorder recorder) {
		((AddServerWizardDataRecorder) recorder).instanceSize = (VirtualMachineProduct) sizelist
				.getSelectedValue();

		if (provider.supportsFirewalls()) {
			return new FirewallPage(provider);
		} else {
			return new TaggingPage();
		}
	}

}
