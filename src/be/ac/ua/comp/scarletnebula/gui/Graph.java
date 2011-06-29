/*
 * Copyright (C) 2011  Ives van der Flaas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import be.ac.ua.comp.scarletnebula.core.TimedDatapoint;

/**
 * An abstract Graph that displays streaming data.
 * 
 * @author ives
 * 
 */
public abstract class Graph implements NewDatapointListener {
	protected Map<String, TimeSeries> datastreams = new HashMap<String, TimeSeries>();
	protected final TimeSeriesCollection dataset = new TimeSeriesCollection();
	protected final XYItemRenderer renderer = new XYLineAndShapeRenderer(true,
			false);
	final long maximumAge;
	private int maxSeriesID = 0;
	private final Collection<Server> serversToRefresh = new ArrayList<Server>();

	/**
	 * Constructor.
	 * 
	 * @param maximumAge
	 *            The age after which data is no longer displayed in the graph
	 */
	public Graph(final long maximumAge) {
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
	 * @param color
	 *            The color this stream will be displayed in
	 */
	public final void registerRelativeDatastream(final Server server,
			final String streamname, final Color color) {
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
	 * updated.
	 * 
	 * @return The ChartPanel containing the Graph
	 */
	abstract ChartPanel getChartPanel();

	/**
	 * Registers a new datapoint for the stream named streamname, which was
	 * measured at value, at the current time.
	 * 
	 * @param datapoint
	 *            The datapoint to add (now).
	 */
	@Override
	public void newDataPoint(final Datapoint datapoint) {
		newDataPoint(new Millisecond(), datapoint);
	}

	/**
	 * Adds a new server to the list of servers to be refreshed when a new
	 * datapoint is received.
	 * 
	 * @param server
	 *            Server that's refreshed.
	 */
	public void addServerToRefresh(final Server server) {
		serversToRefresh.add(server);
	}

	/**
	 * Adds a list of datapoints.
	 * 
	 * @param datapoints
	 *            The timed datapoints to add.
	 */
	public void addListOfDatapoints(final List<TimedDatapoint> datapoints) {
		for (final TimedDatapoint datapoint : datapoints) {
			newDataPoint(new Millisecond(new Date(datapoint.getTimeMs())),
					datapoint);
		}
	}

	/**
	 * Registers a new datapoint for the stream named streamname, which was
	 * measured at value, at time time.
	 * 
	 * @param datapoint
	 *            The datapoint to add.
	 * @param time
	 *            The time at which this measurement was made
	 */
	@Override
	public void newDataPoint(final RegularTimePeriod time,
			final Datapoint datapoint) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final String dataStreamName = datapoint.getDatastream();
				if (datastreams.containsKey(dataStreamName)) {
					datastreams.get(dataStreamName).addOrUpdate(time,
							datapoint.getValue());
				}

				for (final Server server : serversToRefresh) {
					server.serverChanged();
				}
			}
		});
	}
}
