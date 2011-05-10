package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableRowSorter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.Platform;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.BetterTextField;
import be.ac.ua.comp.scarletnebula.gui.Collapsable;
import be.ac.ua.comp.scarletnebula.gui.CollapsablePanel;
import be.ac.ua.comp.scarletnebula.gui.ThrobberBarWithText;
import be.ac.ua.comp.scarletnebula.misc.SwingWorkerWithThrobber;
import be.ac.ua.comp.scarletnebula.misc.Utils;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class ChooseImagePage extends WizardPage
{
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ChooseImagePage.class);
	private Platform previousSelectedPlatform = null;
	private Architecture previousSelectedArchitecture = null;
	private final MachineImageTableModel allImagesModel = new MachineImageTableModel(
			new ArrayList<MachineImage>());
	private final JTable allImagesTable = new JTable(allImagesModel);
	private final MachineImageTableModel favoriteImagesModel = new MachineImageTableModel(
			new ArrayList<MachineImage>());
	private final JTable favoriteImagesTable = new JTable(favoriteImagesModel);

	private final CloudProvider provider;
	private final CollapsablePanel throbberPanel = new CollapsablePanel(
			new ThrobberBarWithText("Loading machine images"), false);// Not
																		// initially
																		// visible

	private final JTabbedPane tabs = new JTabbedPane();
	private final JPanel favoriteImagesPanel;
	private final JPanel allImagesPanel;

	public ChooseImagePage(final CloudProvider provider)
	{
		super(new BorderLayout());
		this.provider = provider;

		favoriteImagesPanel = getFavoriteImagesPanel(provider);
		tabs.addTab("Favorite images", favoriteImagesPanel);
		allImagesPanel = getAllImagesPanel(provider);
		tabs.addTab("All images", allImagesPanel);

		if (provider.getFavoriteImages().isEmpty())
		{
			tabs.setSelectedComponent(allImagesPanel);
		}

		add(tabs, BorderLayout.CENTER);
	}

	private JPanel getFavoriteImagesPanel(CloudProvider provider)
	{
		JPanel panel = new JPanel(new BorderLayout());
		final TableRowSorter<MachineImageTableModel> sorter = new TableRowSorter<MachineImageTableModel>(
				favoriteImagesModel);
		favoriteImagesTable.setRowSorter(sorter);
		favoriteImagesTable.setFillsViewportHeight(true);
		favoriteImagesTable
				.addMouseListener(new SmartFavoritesContextMenuMouseListener(
						provider, favoriteImagesModel, favoriteImagesTable));

		final JScrollPane tableScrollPane = new JScrollPane(favoriteImagesTable);
		tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 20, 10, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		panel.add(tableScrollPane, BorderLayout.CENTER);

		favoriteImagesModel.addImages(provider.getFavoriteImages());

		return panel;
	}

	private final JPanel getAllImagesPanel(final CloudProvider provider)
	{
		JPanel panel = new JPanel(new BorderLayout());
		final TableRowSorter<MachineImageTableModel> sorter = new TableRowSorter<MachineImageTableModel>(
				allImagesModel);
		allImagesTable.setRowSorter(sorter);
		allImagesTable.setFillsViewportHeight(true);
		allImagesTable
				.addMouseListener(new SmartFavoritesContextMenuMouseListener(
						provider, allImagesModel, allImagesTable));

		final JPanel aboveTable = new JPanel(new BorderLayout());
		final JPanel searchPanel = getSearchPanel(sorter);

		aboveTable.add(searchPanel, BorderLayout.NORTH);
		aboveTable.add(throbberPanel, BorderLayout.SOUTH);

		panel.add(aboveTable, BorderLayout.NORTH);

		final JScrollPane tableScrollPane = new JScrollPane(allImagesTable);
		tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 20, 10, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		panel.add(tableScrollPane, BorderLayout.CENTER);

		return panel;
	}

	private JPanel getSearchPanel(
			final TableRowSorter<MachineImageTableModel> sorter)
	{
		final JPanel searchPanel = new JPanel(new GridBagLayout());
		searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));

		add(searchPanel, BorderLayout.NORTH);
		final PlatformComboBox platformComboBox = new PlatformComboBox();

		final GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 5);

		searchPanel.add(platformComboBox, c);
		final ArchitectureComboBox architectureComboBox = new ArchitectureComboBox();

		c.gridx = 1;
		searchPanel.add(architectureComboBox, c);

		final BetterTextField searchField = new BetterTextField();
		searchField.setPlaceHolder("Search terms");

		searchField.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		c.gridx = 2;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		searchPanel.add(searchField, c);

		searchField.addActionListener(new SearchFieldListener(
				architectureComboBox, sorter, platformComboBox, searchField));

		return searchPanel;
	}

	@Override
	public WizardPage next(final DataRecorder recorder)
	{
		final AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;

		if (tabs.getSelectedComponent() == allImagesPanel)
		{
			final int selection = allImagesTable.getSelectedRow();

			if (selection < 0)
			{
				JOptionPane
						.showMessageDialog(
								this,
								"Select an image by choosing a platform, architecture, "
										+ "entering search terms in the search box and subsequently pressing the ENTER button.",
								"Select image", JOptionPane.ERROR_MESSAGE);
				return null;
			}

			rec.image = allImagesModel.getRow(allImagesTable
					.convertRowIndexToModel(selection));
		}
		else
		{
			final int selection = favoriteImagesTable.getSelectedRow();

			if (selection < 0)
			{
				JOptionPane
						.showMessageDialog(
								this,
								"Either select one of your favorite images or go to the All Images tab and search for an image.",
								"Select image", JOptionPane.ERROR_MESSAGE);
				return null;
			}

			rec.image = favoriteImagesModel.getRow(favoriteImagesTable
					.convertRowIndexToModel(selection));
		}

		return new ChooseSizePage(provider, rec.image);
	}

	private final class SmartFavoritesContextMenuMouseListener implements
			MouseListener
	{
		private final CloudProvider provider;
		private MachineImageTableModel model;
		private JTable table;

		private SmartFavoritesContextMenuMouseListener(CloudProvider provider,
				MachineImageTableModel model, JTable table)
		{
			this.provider = provider;
			this.model = model;
			this.table = table;
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{

		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				final JPopupMenu popup = new JPopupMenu();
				final int indexOfSelectedServer = table
						.rowAtPoint(e.getPoint());

				final int modelIndex = table
						.convertRowIndexToModel(indexOfSelectedServer);

				final MachineImage image = model.getImage(modelIndex);

				if (provider.getFavoriteImages().contains(image))
				{
					JMenuItem removeFromFavorites = new JMenuItem(
							"Remove from favorites");
					removeFromFavorites.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							provider.removeFromFavorites(image);
							favoriteImagesModel.clear();
							favoriteImagesModel.addImages(provider
									.getFavoriteImages());
							provider.store();
						}
					});
					popup.add(removeFromFavorites);
				}
				else
				{
					JMenuItem addToFavorites = new JMenuItem("Add to favorites");
					addToFavorites.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							provider.addToFavorites(image);
							provider.store();
						}
					});
				}
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void mouseExited(MouseEvent e)
		{

		}

		@Override
		public void mouseEntered(MouseEvent e)
		{

		}

		@Override
		public void mouseClicked(MouseEvent e)
		{

		}
	}

	private final class SearchFieldListener implements ActionListener
	{

		private final ArchitectureComboBox architectureComboBox;
		private final TableRowSorter<MachineImageTableModel> sorter;
		private final PlatformComboBox platformComboBox;
		private final BetterTextField searchField;

		private SearchFieldListener(
				final ArchitectureComboBox architectureComboBox,
				final TableRowSorter<MachineImageTableModel> sorter,
				final PlatformComboBox platformComboBox,
				final BetterTextField searchField)
		{
			this.architectureComboBox = architectureComboBox;
			this.sorter = sorter;
			this.platformComboBox = platformComboBox;
			this.searchField = searchField;
		}

		@Override
		public void actionPerformed(final ActionEvent e)
		{
			log.debug("Filtering table");
			final Platform currentPlatform = platformComboBox.getSelection();
			final Architecture currentArchitecture = architectureComboBox
					.getSelection();

			if (!currentPlatform.equals(previousSelectedPlatform)
					|| !currentArchitecture
							.equals(previousSelectedArchitecture))
			{
				allImagesModel.clear();

				final ImageModelFillerTask task = new ImageModelFillerTask(
						Utils.findWindow(ChooseImagePage.this), throbberPanel,
						allImagesTable, allImagesModel, provider,
						currentPlatform, currentArchitecture);
				task.execute();

				previousSelectedArchitecture = currentArchitecture;
				previousSelectedPlatform = currentPlatform;

			}
			final String expr = searchField.getText();
			sorter.setRowFilter(new AllTermsRowFilter(expr));
			sorter.setSortKeys(null);
		}
	}

	class ImageModelFillerTask extends
			SwingWorkerWithThrobber<MachineImageTableModel, MachineImage>
	{
		MachineImageTableModel model;
		CloudProvider provider;
		Platform platform;
		Architecture architecture;
		JTable table;

		ImageModelFillerTask(final Window win, final Collapsable throbber,
				final JTable jtable, final MachineImageTableModel model,
				final CloudProvider provider, final Platform platform,
				final Architecture architecture)
		{
			super(throbber);
			this.model = model;
			this.provider = provider;
			this.platform = platform;
			this.architecture = architecture;
			this.table = jtable;
		}

		@Override
		public MachineImageTableModel doInBackground()
		{
			final Iterable<MachineImage> images = provider
					.getAvailableMachineImages(platform, architecture);
			if (images == null)
			{
				log.error("No images available");
			}

			for (final MachineImage image : images)
			{
				if (isCancelled())
				{
					return model;
				}
				publish(image);
			}
			return model;
		}

		@Override
		protected void process(final List<MachineImage> toAdd)
		{
			model.addImages(toAdd);
		}
	};

	private final class AllTermsRowFilter extends
			RowFilter<MachineImageTableModel, Integer>
	{
		private final String expr;

		private AllTermsRowFilter(String expr)
		{
			this.expr = expr;
		}

		@Override
		public boolean include(
				Entry<? extends MachineImageTableModel, ? extends Integer> entry)
		{
			MachineImageTableModel tableModel = entry.getModel();
			MachineImage image = tableModel.getImage(entry.getIdentifier());

			String terms[] = expr.split("\\s");

			boolean allTermsFound = true;

			for (String term : terms)
			{
				if (!image.getDescription().toLowerCase()
						.contains(term.toLowerCase())
						&& !image.getName().toLowerCase()
								.contains(term.toLowerCase())
						&& !image.getType().toString().toLowerCase()
								.contains(term))
				{
					allTermsFound = false;
					break;
				}
			}

			return allTermsFound;
		}
	}
}
