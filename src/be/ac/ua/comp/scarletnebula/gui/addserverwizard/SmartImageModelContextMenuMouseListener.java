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
		MouseListener
{
	/**
	 * 
	 */
	private final CloudProvider provider;
	private final MachineImageTableModel model;
	private final JTable table;
	private MachineImageTableModel favoritesModel;

	public SmartImageModelContextMenuMouseListener(
			final CloudProvider provider, final MachineImageTableModel model,
			final JTable table, final MachineImageTableModel favoritesModel)
	{
		this.provider = provider;
		this.model = model;
		this.table = table;
		this.favoritesModel = favoritesModel;
	}

	@Override
	public void mouseReleased(final MouseEvent e)
	{

	}

	@Override
	public void mousePressed(final MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			final JPopupMenu popup = new JPopupMenu();
			final int indexOfSelectedServer = table.rowAtPoint(e.getPoint());

			final int modelIndex = table
					.convertRowIndexToModel(indexOfSelectedServer);

			final MachineImage image = model.getImage(modelIndex);

			if (provider.imageInFavorites(image))
			{
				final JMenuItem removeFromFavorites = new JMenuItem(
						"Remove from favorites");
				removeFromFavorites.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent e)
					{
						provider.removeFromFavorites(image);

						if (favoritesModel != null)
						{
							favoritesModel.clear();
							favoritesModel.addImages(provider
									.getFavoriteImages());
						}

						provider.store();
					}
				});
				popup.add(removeFromFavorites);
			}
			else
			{
				final JMenuItem addToFavorites = new JMenuItem(
						"Add to favorites");
				addToFavorites.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent e)
					{
						provider.addToFavorites(image);
						provider.store();
					}
				});
				popup.add(addToFavorites);
			}
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseExited(final MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(final MouseEvent e)
	{

	}

	@Override
	public void mouseClicked(final MouseEvent e)
	{

	}
}