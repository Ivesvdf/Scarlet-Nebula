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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.dasein.cloud.compute.MachineImage;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;

final public class SmartImageModelContextMenuMouseListener implements
		MouseListener {
	/**
	 * 
	 */
	private final CloudProvider provider;
	private final MachineImageTableModel model;
	private final JTable table;
	private final MachineImageTableModel favoritesModel;

	public SmartImageModelContextMenuMouseListener(
			final CloudProvider provider, final MachineImageTableModel model,
			final JTable table, final MachineImageTableModel favoritesModel) {
		this.provider = provider;
		this.model = model;
		this.table = table;
		this.favoritesModel = favoritesModel;
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			showPopup(e);
		}
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			showPopup(e);
		}
	}

	private void showPopup(final MouseEvent e) {
		final JPopupMenu popup = new JPopupMenu();
		final int indexOfSelectedServer = table.rowAtPoint(e.getPoint());

		final int modelIndex = table
				.convertRowIndexToModel(indexOfSelectedServer);

		final MachineImage image = model.getImage(modelIndex);

		if (provider.imageInFavorites(image)) {
			final JMenuItem removeFromFavorites = new JMenuItem(
					"Remove from favorites");
			removeFromFavorites.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					provider.removeFromFavorites(image);

					if (favoritesModel != null) {
						favoritesModel.clear();
						favoritesModel.addImages(provider.getFavoriteImages());
					}

					provider.store();
				}
			});
			popup.add(removeFromFavorites);
		} else {
			final JMenuItem addToFavorites = new JMenuItem("Add to favorites");
			addToFavorites.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					provider.addToFavorites(image);
					provider.store();
				}
			});
			popup.add(addToFavorites);
		}
		popup.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void mouseExited(final MouseEvent e) {

	}

	@Override
	public void mouseEntered(final MouseEvent e) {

	}

	@Override
	public void mouseClicked(final MouseEvent e) {

	}
}