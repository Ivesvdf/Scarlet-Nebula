package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.Platform;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.gui.ThrobberBarWithText;
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
	private final JPanel throbberPanel = new JPanel(new BorderLayout());

	ChooseImagePage(final CloudProvider provider)
	{
		super();
		this.provider = provider;
		setLayout(new BorderLayout());

		model = new MachineImageTableModel(new ArrayList<MachineImage>());
		table = new JTable(model);
		final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				model);
		table.setRowSorter(sorter);

		JPanel aboveTable = new JPanel(new BorderLayout());
		JPanel searchPanel = getSearchPanel(sorter);

		aboveTable.add(searchPanel, BorderLayout.NORTH);
		aboveTable.add(throbberPanel, BorderLayout.SOUTH);

		add(aboveTable, BorderLayout.NORTH);

		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(10, 20, 20, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		add(tableScrollPane, BorderLayout.CENTER);

	}

	private JPanel getSearchPanel(final TableRowSorter<TableModel> sorter)
	{
		JPanel searchPanel = new JPanel(new GridBagLayout());
		searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

		add(searchPanel, BorderLayout.NORTH);
		final PlatformComboBox platformComboBox = new PlatformComboBox();

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 5);

		searchPanel.add(platformComboBox, c);
		final ArchitectureComboBox architectureComboBox = new ArchitectureComboBox();

		c.gridx = 1;
		searchPanel.add(architectureComboBox, c);

		final JTextField searchField = new JTextField();

		searchField.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		c.gridx = 2;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		searchPanel.add(searchField, c);

		searchField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				log.debug("Filtering table");
				final Platform currentPlatform = platformComboBox
						.getSelection();
				final Architecture currentArchitecture = architectureComboBox
						.getSelection();

				if (!currentPlatform.equals(previousSelectedPlatform)
						|| !currentArchitecture
								.equals(previousSelectedArchitecture))
				{
					model.clear();
					log.warn("executing");

					throbberPanel.removeAll();

					ThrobberBarWithText throbber = new ThrobberBarWithText(
							"Loading machine images");
					throbberPanel.add(throbber, BorderLayout.CENTER);

					throbberPanel.revalidate();

					ImageModelFillerTask task = new ImageModelFillerTask(Utils
							.findWindow(ChooseImagePage.this), table, model,
							provider, currentPlatform, currentArchitecture);
					log.warn("going to execute");
					task.execute();

					previousSelectedArchitecture = currentArchitecture;
					previousSelectedPlatform = currentPlatform;
				}
				String expr = searchField.getText();
				sorter.setRowFilter(RowFilter.regexFilter(expr));
				sorter.setSortKeys(null);
			}
		});

		return searchPanel;
	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		AddServerWizardDataRecorder rec = (AddServerWizardDataRecorder) recorder;

		int selection = table.getSelectedRow();

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
		MachineImage image = model.getRow(selection);
		rec.image = image.getProviderMachineImageId();
		return new TaggingPage();
	}

	class ImageModelFillerTask extends
			SwingWorker<MachineImageTableModel, MachineImage>
	{
		MachineImageTableModel model;
		CloudProvider provider;
		Platform platform;
		Architecture architecture;
		JTable table;

		ImageModelFillerTask(final Window win, JTable jtable,
				MachineImageTableModel model, CloudProvider provider,
				Platform platform, Architecture architecture)
		{
			this.model = model;
			this.provider = provider;
			this.platform = platform;
			this.architecture = architecture;
			this.table = jtable;

			addPropertyChangeListener(new PropertyChangeListener()
			{
				@Override
				public void propertyChange(PropertyChangeEvent evt)
				{
					if ("state".equals(evt.getPropertyName())
							&& SwingWorker.StateValue.DONE == evt.getNewValue())
					{
						throbberPanel.removeAll();
						throbberPanel.revalidate();
					}
				}

			});

		}

		@Override
		public MachineImageTableModel doInBackground()
		{
			Iterable<MachineImage> images = provider.getAvailableMachineImages(
					platform, architecture);
			if (images == null)
				log.error("No images available");

			for (MachineImage image : images)
			{
				if (isCancelled())
					return model;
				publish(image);
			}

			return model;
		}

		@Override
		protected void process(List<MachineImage> toAdd)
		{
			model.addImages(toAdd);
		}
	};

}
