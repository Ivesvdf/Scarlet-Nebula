package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import be.ac.ua.comp.scarletnebula.core.Server;

/**
 * JList that displays JLabels as items in the list. This will allow me to
 * display an image next to the text (for server status).
 * 
 * @author ives
 * 
 */
public class ServerList extends JXList implements ComponentListener
{
	private final class RollOverHighlighter extends AbstractHighlighter
	{
		private final ServerCellRenderer serverCellRenderer;

		private RollOverHighlighter(HighlightPredicate predicate,
				ServerCellRenderer serverCellRenderer)
		{
			super(predicate);
			this.serverCellRenderer = serverCellRenderer;
		}

		@Override
		protected Component doHighlight(Component arg0, ComponentAdapter arg1)
		{
			JXPanel objectToBeRendered = (JXPanel) arg0;
			serverCellRenderer.onRollOver(objectToBeRendered);
			return objectToBeRendered;
		}
	}

	private final class ClearSelectionMouseAdapter extends MouseAdapter
	{
		private void testClearSelection(MouseEvent e)
		{
			JList list = (JList) e.getSource();

			Point currentPos = e.getPoint();

			for (int i = 0; i < list.getModel().getSize(); i++)
			{
				Rectangle r = list.getCellBounds(i, i);
				if (r.contains(currentPos))
					return;
			}
			list.clearSelection();
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			testClearSelection(e);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			testClearSelection(e);
		}
	}

	private static final long serialVersionUID = 1L;

	ServerListModel serverListModel;

	public ServerList(ServerListModel serverListModel)
	{
		super(serverListModel);
		this.serverListModel = serverListModel;
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setVisibleRowCount(-1);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		final ServerCellRenderer serverCellRenderer = new ServerCellRenderer();
		setCellRenderer(serverCellRenderer);
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		addComponentListener(this);
		setRolloverEnabled(true);
		addHighlighter(new RollOverHighlighter(
				HighlightPredicate.ROLLOVER_CELL, serverCellRenderer));

		getActionMap().remove("find"); // JXlist registers its own find, we'll
										// provide our version later.

		addMouseListener(new ClearSelectionMouseAdapter());
	}

	@Override
	public void clearSelection()
	{
		setSelectedIndices(new int[0]);
	}

	public Collection<Server> getSelectedServers()
	{
		int indices[] = getSelectedIndices();
		return serverListModel.getVisibleServersAtIndices(indices);
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		JList list = (JList) e.getSource();
		JViewport viewport = (JViewport) list.getParent();

		Dimension newSize = viewport.getExtentSize();
		final int totalWidth = newSize.width - list.getInsets().left
				- list.getInsets().right - 1;
		final int serverCount = totalWidth / 200;

		if (serverCount == 0)
			return;

		final int serverWidth = totalWidth / serverCount;

		list.setFixedCellHeight(100);
		list.setFixedCellWidth(serverWidth);

		list.revalidate();
		list.repaint();
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void componentHidden(ComponentEvent e)
	{
		// TODO Auto-generated method stub

	}

	public boolean startNewServerServerSelected()
	{
		int indices[] = getSelectedIndices();
		for (int index : indices)
		{
			if (serverListModel.getVisibleServerAtIndex(index) == null)
				return true;
		}
		return false;
	}

}
