package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartPanel;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.core.ServerStatisticsManager;
import be.ac.ua.comp.scarletnebula.core.ServerStatisticsManager.NewDatastreamListener;
import be.ac.ua.comp.scarletnebula.gui.graph.Datapoint;

public class AllGraphsPanel extends JPanel implements NewDatastreamListener
{
	private static final long serialVersionUID = 1L;
	final private static Log log = LogFactory.getLog(AllGraphsPanel.class);

	final private Server server;
	private ServerStatisticsManager statisticsManager;

	public AllGraphsPanel(Server server)
	{
		super(new GridBagLayout());
		this.server = server;
		statisticsManager = server.getServerStatistics();
		statisticsManager.addNewDatastreamListener(this);

		setOpaque(true);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEtchedBorder());

		placeComponents();

	}

	private void placeComponents()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		int numberOfComponentsPlaced = 0;
		int currXPos = 0;

		for (String streamname : statisticsManager.getAvailableDatastreams())
		{
			log.info("drawing stream");
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 0.5;
			constraints.gridx = currXPos;
			constraints.gridy = numberOfComponentsPlaced / 2;

			final DecoratedGraph graph = new DecoratedGraph(
					(long) 30 * 60 * 1000,
					statisticsManager.getDatastream(streamname));
			graph.registerRelativeDatastream(server, streamname, Color.GREEN);
			graph.addServerToRefresh(server);
			final ChartPanel chartPanel = graph.getChartPanel();
			chartPanel.setPreferredSize(new Dimension(100, 150));
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
	public void newDataStream(Datapoint datapoint)
	{
		removeAll();
		placeComponents();
		revalidate();
	}

}
