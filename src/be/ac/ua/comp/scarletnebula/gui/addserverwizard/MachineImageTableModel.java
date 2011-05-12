package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.dasein.cloud.compute.MachineImage;

public class MachineImageTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	private List<String> columnNames = null;
	private List<MachineImage> rows = null;

	public MachineImageTableModel(final List<MachineImage> rows)
	{
		final String[] columns = { "Name", "Description", "Type" };
		this.columnNames = Arrays.asList(columns);
		this.rows = rows;
	}

	@Override
	public String getColumnName(final int col)
	{
		return columnNames.get(col);
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex)
	{
		return false;
	}

	@Override
	public int getRowCount()
	{
		return rows.size();
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.size();
	}

	@Override
	public Class<?> getColumnClass(final int c)
	{
		return String.class;
	}

	public MachineImage getRow(final int rowIndex)
	{
		return rows.get(rowIndex);
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex)
	{
		final MachineImage img = rows.get(rowIndex);

		switch (columnIndex)
		{
			case 0:
				return img.getName();
			case 1:
				return img.getDescription();
			case 2:
				return img.getType().toString();
		}

		return null;
	}

	public void clear()
	{
		if (getRowCount() == 0)
		{
			return;
		}

		final int rowcount = getRowCount();
		rows.clear();
		fireTableRowsDeleted(0, rowcount - 1);
	}

	public void addImages(final Collection<MachineImage> images)
	{
		final int prevRowCount = getRowCount();
		rows.addAll(images);
		fireTableRowsInserted(prevRowCount - 1,
				prevRowCount - 1 + images.size());
	}

	public MachineImage getImage(final int identifier)
	{
		return getRow(identifier);
	}

}
