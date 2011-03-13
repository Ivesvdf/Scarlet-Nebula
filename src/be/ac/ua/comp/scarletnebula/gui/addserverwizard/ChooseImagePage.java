package be.ac.ua.comp.scarletnebula.gui.addserverwizard;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.CloudProvider;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;
import be.ac.ua.comp.scarletnebula.wizard.WizardPage;

public class ChooseImagePage extends WizardPage
{

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(ChooseImagePage.class);

	ChooseImagePage(CloudProvider provider)
	{
		setLayout(new BorderLayout());

		JPanel top = new JPanel(new GridBagLayout());
		top.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		add(top, BorderLayout.NORTH);
		PlatformComboBox platformComboBox = new PlatformComboBox();

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 5);

		top.add(platformComboBox, c);
		ArchitectureComboBox architectureComboBox = new ArchitectureComboBox();

		c.gridx = 1;
		top.add(architectureComboBox, c);

		final JTextField searchField = new JTextField();

		searchField.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		c.gridx = 2;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		top.add(searchField, c);

		String[] columns = { "Name", "Platform", "Description" };
		List<List<String>> rows = new ArrayList<List<String>>();

		/*
		 * Iterable<MachineImage> images = provider.getAllMachineImages(); if
		 * (images == null) log.error("No images available");
		 * 
		 * for (MachineImage image : images) {
		 * rows.add(Arrays.asList(image.getName(), image.getPlatform()
		 * .toString(), image.getDescription())); }
		 */
		TableModel model = new ReadOnlyStringTableModel(Arrays.asList(columns),
				rows);
		JTable table = new JTable(model);
		final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				model);
		table.setRowSorter(sorter);
		searchField.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				log.debug("Filtering table");
				String expr = searchField.getText();
				sorter.setRowFilter(RowFilter.regexFilter(expr));
				sorter.setSortKeys(null);
			}
		});
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 20, 20, 20),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		add(tableScrollPane, BorderLayout.CENTER);

	}

	@Override
	public WizardPage next(DataRecorder recorder)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean nextIsEnabled()
	{
		return false;
	}

	@Override
	public boolean finishIsEnabled()
	{
		return true;
	}
}
