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

package be.ac.ua.comp.scarletnebula.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableRowSorter;

import org.dasein.cloud.compute.MachineImage;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.MachineImageTableModel;
import be.ac.ua.comp.scarletnebula.gui.addserverwizard.SmartImageModelContextMenuMouseListener;

public class FavoriteImagesPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	final MachineImageTableModel favoriteImagesModel = new MachineImageTableModel(
			new ArrayList<MachineImage>());
	private final JTable favoriteImagesTable = new JTable(favoriteImagesModel);

	public FavoriteImagesPanel(final CloudProvider provider) {
		super(new BorderLayout());
		final TableRowSorter<MachineImageTableModel> sorter = new TableRowSorter<MachineImageTableModel>(
				favoriteImagesModel);
		favoriteImagesTable.setRowSorter(sorter);
		favoriteImagesTable.setFillsViewportHeight(true);
		favoriteImagesTable
				.addMouseListener(new SmartImageModelContextMenuMouseListener(
						provider, favoriteImagesModel, favoriteImagesTable,
						favoriteImagesModel));

		final JScrollPane tableScrollPane = new JScrollPane(favoriteImagesTable);
		tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 20, 10, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		add(tableScrollPane, BorderLayout.CENTER);

		favoriteImagesModel.addImages(provider.getFavoriteImages());

		if (favoriteImagesModel.getRowCount() > 0) {
			favoriteImagesTable.setRowSelectionInterval(0, 0);
		}
	}

	public MachineImage getSelection() {
		final int selection = favoriteImagesTable.getSelectedRow();

		if (selection < 0) {
			return null;
		}

		return favoriteImagesModel.getRow(favoriteImagesTable
				.convertRowIndexToModel(selection));
	}

	public MachineImageTableModel getModel() {
		return favoriteImagesModel;
	}
}
