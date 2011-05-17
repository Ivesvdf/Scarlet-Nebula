package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.dasein.cloud.compute.MachineImage;

/**
 * Model for a table containing MachineImages.
 * 
 * @author ives
 * 
 */
public class MachineImageTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private List<String> columnNames = null;
	private List<MachineImage> rows = null;

	/**
	 * Constructor.
	 * 
	 * @param rows
	 *            The initial rows to contain.
	 */
	public MachineImageTableModel(final List<MachineImage> rows) {
		final String[] columns = { "Name", "Description", "Type" };
		this.columnNames = Arrays.asList(columns);
		this.rows = rows;
	}

	@Override
	public String getColumnName(final int col) {
		return columnNames.get(col);
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	@Override
	public Class<?> getColumnClass(final int c) {
		return String.class;
	}

	/**
	 * @param rowIndex
	 *            Model index
	 * @return The MachineImage at that index.
	 */
	public MachineImage getRow(final int rowIndex) {
		return rows.get(rowIndex);
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final MachineImage img = rows.get(rowIndex);

		switch (columnIndex)
		{
			case 0:
				return img.getName();
			case 1:
				return img.getDescription();
			case 2:
				return img.getType().toString();
			default:
				return "";
		}
	}

	/**
	 * Removes all rows.
	 */
	public void clear() {
		if (getRowCount() == 0) {
			return;
		}

		final int rowcount = getRowCount();
		rows.clear();
		fireTableRowsDeleted(0, rowcount - 1);
	}

	/**
	 * Adds a collection of images to the model.
	 * 
	 * @param images
	 *            The images to add.
	 */
	public void addImages(final Collection<MachineImage> images) {
		final int prevRowCount = getRowCount();
		rows.addAll(images);
		fireTableRowsInserted(prevRowCount - 1,
				prevRowCount - 1 + images.size());
	}

	/**
	 * @param rowIndex
	 *            The index of the row to return
	 * @return The machineimage at that index.
	 */
	public MachineImage getImage(final int rowIndex) {
		return getRow(rowIndex);
	}

}
