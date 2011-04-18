package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

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

public abstract class Graph implements DataStreamListener
{
	private static Log log = LogFactory.getLog(Graph.class);
	protected Map<String, TimeSeries> datastreams = new HashMap<String, TimeSeries>();
	protected final TimeSeriesCollection dataset = new TimeSeriesCollection();
	protected final XYItemRenderer renderer = new XYLineAndShapeRenderer(true,
			false);
	final long maximumAge;
	private int maxSeriesID = 0;

	public Graph(long maximumAge)
	{
		this.maximumAge = maximumAge;

	}

	public final void registerRelativeDatastream(Server server,
			String streamname, String streamtitle, Color color)
	{
		// ServerStatisticsManager manager = server.getServerStatistics();
		// manager.addStreamListener(this);

		TimeSeries series = new TimeSeries(streamtitle);
		series.setMaximumItemAge(maximumAge);
		datastreams.put(streamname, series);
		dataset.addSeries(series);

		renderer.setSeriesPaint(maxSeriesID++, color);
	}

	abstract ChartPanel getChartPanel();

	@Override
	public void newDataPoint(String streamname, double value)
	{
		newDataPoint(streamname, new Millisecond(), value);
	}

	@Override
	public void newDataPoint(String streamname, RegularTimePeriod time,
			double value)
	{
		if (!datastreams.containsKey(streamname))
		{
			log.error("Adding datapoint to unregistered stream.");
		}
		else
		{
			datastreams.get(streamname).addOrUpdate(time, value);
		}
	}

}
