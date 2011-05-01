package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableModel;
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
	private final JTable table;
	private final MachineImageTableModel model;
	private final CloudProvider provider;
	private final CollapsablePanel throbberPanel = new CollapsablePanel(
			new ThrobberBarWithText("Loading machine images"), false);// Not
																		// initially
																		// visible

	ChooseImagePage(final CloudProvider provider)
	{
		super(new BorderLayout());
		this.provider = provider;

		model = new MachineImageTableModel(new ArrayList<MachineImage>());
		table = new JTable(model);
		final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				model);
		table.setRowSorter(sorter);

		final JPanel aboveTable = new JPanel(new BorderLayout());
		final JPanel searchPanel = getSearchPanel(sorter);

		aboveTable.add(searchPanel, BorderLayout.NORTH);
		aboveTable.add(throbberPanel, BorderLayout.SOUTH);

		add(aboveTable, BorderLayout.NORTH);

		final JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 20, 10, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		add(tableScrollPane, BorderLayout.CENTER);

	}

	private JPanel getSearchPanel(final TableRowSorter<TableModel> sorter)
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

		final int selection = table.getSelectedRow();

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
		final MachineImage image = model.getRow(selection);
		rec.image = image.getProviderMachineImageId();
		return new TaggingPage();
	}

	private final class SearchFieldListener implements ActionListener
	{
		private final ArchitectureComboBox architectureComboBox;
		private final TableRowSorter<TableModel> sorter;
		private final PlatformComboBox platformComboBox;
		private final BetterTextField searchField;

		private SearchFieldListener(final ArchitectureComboBox architectureComboBox,
				final TableRowSorter<TableModel> sorter,
				final PlatformComboBox platformComboBox, final BetterTextField searchField)
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
				model.clear();

				final ImageModelFillerTask task = new ImageModelFillerTask(
						Utils.findWindow(ChooseImagePage.this), throbberPanel,
						table, model, provider, currentPlatform,
						currentArchitecture);
				task.execute();

				previousSelectedArchitecture = currentArchitecture;
				previousSelectedPlatform = currentPlatform;

			}
			final String expr = searchField.getText();
			sorter.setRowFilter(RowFilter.regexFilter(expr));
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

}
