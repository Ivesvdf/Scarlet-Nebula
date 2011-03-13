package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ReadOnlyStringTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	private List<String> columnNames = null;
	private List<List<String>> rows = null;

	ReadOnlyStringTableModel(List<String> columnNames, List<List<String>> rows)
	{
		this.columnNames = columnNames;
		this.rows = rows;
	}

	@Override
	public String getColumnName(int col)
	{
		return columnNames.get(col);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
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
	public Class<?> getColumnClass(int c)
	{
		return String.class;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return rows.get(rowIndex).get(columnIndex);
	}

}
