package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import be.ac.ua.comp.scarletnebula.core.Datapoint;
import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.core.ServerStatisticsManager;
import be.ac.ua.comp.scarletnebula.core.Datastream.TimedDatapoint;

/**
 * An abstract Graph that displays streaming data
 * 
 * @author ives
 * 
 */
public abstract class Graph implements NewDatapointListener
{
	protected Map<String, TimeSeries> datastreams = new HashMap<String, TimeSeries>();
	protected final TimeSeriesCollection dataset = new TimeSeriesCollection();
	protected final XYItemRenderer renderer = new XYLineAndShapeRenderer(true,
			false);
	final long maximumAge;
	private int maxSeriesID = 0;
	private final Collection<Server> serversToRefresh = new ArrayList<Server>();

	/**
	 * Constructor
	 * 
	 * @param maximumAge
	 *            The age after which data is no longer displayed in the graph
	 */
	public Graph(final long maximumAge)
	{
		this.maximumAge = maximumAge;

	}

	/**
	 * Register a relative datastream coming from server with name streamname
	 * and formal title (a displayable name) streamtitle. This stream will be
	 * displayed with Color color.
	 * 
	 * If this datastream was already running, historical data will be taken
	 * from the stream and be displayed in the graph.
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
	public final void registerRelativeDatastream(final Server server,
			final String streamname, final Color color)
	{
		final ServerStatisticsManager manager = server.getServerStatistics();
		manager.addNewDatapointListener(this, streamname);

		final TimeSeries series = new TimeSeries(streamname);
		series.setMaximumItemAge(maximumAge);
		datastreams.put(streamname, series);
		dataset.addSeries(series);

		renderer.setSeriesPaint(maxSeriesID++, color);

		addListOfDatapoints(manager.getHistoricalDatapoints(streamname));
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
	public void newDataPoint(final Datapoint datapoint)
	{
		newDataPoint(new Millisecond(), datapoint);
	}

	/**
	 * Adds a new server to the list of servers to be refreshed when a new
	 * datapoint is received
	 * 
	 * @param server
	 */
	public void addServerToRefresh(final Server server)
	{
		serversToRefresh.add(server);
	}

	public void addListOfDatapoints(final List<TimedDatapoint> datapoints)
	{
		for (final TimedDatapoint datapoint : datapoints)
		{
			newDataPoint(new Millisecond(new Date(datapoint.getTimeMs())),
					datapoint);
		}
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
					// Do nothing
				}
				else
				{
					datastreams.get(dataStreamName).addOrUpdate(time,
							datapoint.getValue());
				}

				for (final Server server : serversToRefresh)
				{
					server.serverChanged();
				}
			}
		});
	}
}
