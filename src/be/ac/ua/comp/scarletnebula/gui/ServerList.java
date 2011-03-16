package be.ac.ua.comp.scarletnebula.gui;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 * JList that displays JLabels as items in the list. This will allow me to
 * display an image next to the text (for server status).
 * 
 * @author ives
 * 
 */
public class ServerList extends javax.swing.JList
{
	private static final long serialVersionUID = 1L;

	ServerListModel serverListModel;

	public ServerList(ServerListModel serverListModel)
	{
		super(serverListModel);
		this.serverListModel = serverListModel;
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setVisibleRowCount(-1);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setSelectedIndex(0);
		setCellRenderer(new ServerCellRenderer());
	}

	@Override
	public void clearSelection()
	{
		setSelectedIndices(new int[0]);
	}

}
