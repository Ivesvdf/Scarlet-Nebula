package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.core.ServerStatisticsManager;
import be.ac.ua.comp.scarletnebula.gui.graph.Datapoint;

/**
 * An abstract Graph that displays streaming data
 * 
 * @author ives
 * 
 */
public abstract class Graph implements DataStreamListener
{
	private static Log log = LogFactory.getLog(Graph.class);
	protected Map<String, TimeSeries> datastreams = new HashMap<String, TimeSeries>();
	protected final TimeSeriesCollection dataset = new TimeSeriesCollection();
	protected final XYItemRenderer renderer = new XYLineAndShapeRenderer(true,
			false);
	final long maximumAge;
	private int maxSeriesID = 0;
	private Collection<Server> serversToRefresh = new ArrayList<Server>();

	/**
	 * Constructor
	 * 
	 * @param maximumAge
	 *            The age after which data is no longer displayed in the graph
	 */
	public Graph(long maximumAge)
	{
		this.maximumAge = maximumAge;

	}

	/**
	 * Register a relative datastream coming from server with name streamname
	 * and formal title (a displayable name) streamtitle. This stream will be
	 * displayed with Color color.
	 * 
	 * @param server
	 *            Server that generates this relative datastream
	 * @param streamname
	 *            The stream's short ID name (e.g. MEM)
	 * @param streamtitle
	 *            The stream's potentially longer nice name (e.g. Memory)
	 * @param color
	 *            The color this stream will be displayed in
	 */
	public final void registerRelativeDatastream(Server server,
			String streamname, String streamtitle, Color color)
	{
		ServerStatisticsManager manager = server.getServerStatistics();
		if (manager != null)
			manager.addStreamListener(this);

		TimeSeries series = new TimeSeries(streamtitle);
		series.setMaximumItemAge(maximumAge);
		datastreams.put(streamname, series);
		dataset.addSeries(series);

		renderer.setSeriesPaint(maxSeriesID++, color);
	}

	/**
	 * Returns the JFreeChart ChartPanel that will be updated as this object is
	 * updated
	 * 
	 * @return The ChartPanel containing the Graph
	 */
	abstract ChartPanel getChartPanel();

	/**
	 * Registers a new datapoint for the stream named streamname, which was
	 * measured at value, at the current time
	 * 
	 * @param streamname
	 *            Stream identifier name
	 * @param value
	 *            Current measurement for streamname
	 */
	@Override
	public void newDataPoint(Datapoint datapoint)
	{
		newDataPoint(new Millisecond(), datapoint);
	}

	public void addServerToRefresh(Server server)
	{
		serversToRefresh.add(server);
	}

	/**
	 * Registers a new datapoint for the stream named streamname, which was
	 * measured at value, at time time.
	 * 
	 * @param streamname
	 *            Stream identifier name
	 * 
	 * @param value
	 *            Current measurement for streamname
	 * @param time
	 *            The time at which this measurement was made
	 */
	@Override
	public void newDataPoint(final RegularTimePeriod time,
			final Datapoint datapoint)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				final String dataStreamName = datapoint.getDatastreamName();
				if (!datastreams.containsKey(dataStreamName))
				{
					log.error("Adding datapoint to unregistered stream.");
				}
				else
				{
					datastreams.get(dataStreamName).addOrUpdate(time,
							datapoint.getValue());
				}

				for (Server server : serversToRefresh)
					server.serverChanged();
			}
		});
	}
}
