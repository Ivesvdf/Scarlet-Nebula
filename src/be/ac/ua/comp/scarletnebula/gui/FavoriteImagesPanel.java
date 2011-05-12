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

public class FavoriteImagesPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	final MachineImageTableModel favoriteImagesModel = new MachineImageTableModel(
			new ArrayList<MachineImage>());
	private final JTable favoriteImagesTable = new JTable(favoriteImagesModel);

	public FavoriteImagesPanel(CloudProvider provider)
	{
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
	}

	public MachineImage getSelection()
	{
		final int selection = favoriteImagesTable.getSelectedRow();

		if (selection < 0)
		{
			return null;
		}

		return favoriteImagesModel.getRow(favoriteImagesTable
				.convertRowIndexToModel(selection));
	}

	public MachineImageTableModel getModel()
	{
		return favoriteImagesModel;
	}
}
