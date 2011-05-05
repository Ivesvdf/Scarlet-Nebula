package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartPanel;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.core.ServerStatisticsManager;
import be.ac.ua.comp.scarletnebula.core.ServerStatisticsManager.DeleteDatastreamListener;
import be.ac.ua.comp.scarletnebula.core.ServerStatisticsManager.NewDatastreamListener;
import be.ac.ua.comp.scarletnebula.gui.graph.Datapoint;

public class AllGraphsPanel extends JPanel implements NewDatastreamListener,
		DeleteDatastreamListener
{
	private static final long serialVersionUID = 1L;
	final private static Log log = LogFactory.getLog(AllGraphsPanel.class);

	final private Server server;
	private final ServerStatisticsManager statisticsManager;

	public AllGraphsPanel(final Server server)
	{
		super(new GridBagLayout());
		this.server = server;
		statisticsManager = server.getServerStatistics();
		statisticsManager.addNewDatastreamListener(this);
		statisticsManager.addDeleteDatastreamListener(this);

		setOpaque(true);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEtchedBorder());

		placeComponents();

	}

	private void placeComponents()
	{
		final GridBagConstraints constraints = new GridBagConstraints();

		int numberOfComponentsPlaced = 0;
		int currXPos = 0;
		final int graphHeight = 150;
		final int graphWidth = 100;

		final Collection<String> datastreams = statisticsManager
				.getAvailableDatastreams();
		for (final String streamname : datastreams)
		{
			log.info("drawing stream");
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 0.5;
			constraints.gridx = currXPos;
			constraints.gridy = numberOfComponentsPlaced / 2;
			constraints.insets = new Insets(10, 0, 0, 0);

			final DecoratedGraph graph = new DecoratedGraph(
					(long) 30 * 60 * 1000,
					statisticsManager.getDatastream(streamname));
			graph.registerRelativeDatastream(server, streamname, Color.GREEN);
			graph.addServerToRefresh(server);
			final ChartPanel chartPanel = graph.getChartPanel();
			chartPanel.setPreferredSize(new Dimension(graphWidth, graphHeight));
			add(chartPanel, constraints);

			if (currXPos == 0)
			{
				currXPos = 1;
			}
			else
			{
				currXPos = 0;
			}
			numberOfComponentsPlaced++;
		}
	}

	@Override
	public void newDataStream(final Datapoint datapoint)
	{
		resetPanel();
	}

	private void resetPanel()
	{
		removeAll();
		placeComponents();
		revalidate();
	}

	@Override
	public void deleteDataStream(final String streamname)
	{
		resetPanel();
	}

}
