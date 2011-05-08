package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;

import be.ac.ua.comp.scarletnebula.core.Datastream;
import be.ac.ua.comp.scarletnebula.core.Datapoint.Type;
import be.ac.ua.comp.scarletnebula.core.Datastream.TimedDatapoint;

public class DecoratedGraph extends Graph
{
	final private static Log log = LogFactory.getLog(DecoratedGraph.class);
	private final DateAxis domain = new DateAxis();
	private final NumberAxis range = new NumberAxis();
	private final Datastream stream;

	/**
	 * Constructor
	 * 
	 * @param maximumAge
	 *            The age after which data is no longer displayed in the graph
	 */
	public DecoratedGraph(final long maximumAge, final Datastream stream)
	{
		super(maximumAge);
		this.stream = stream;
		domain.setVisible(false);
		domain.setAutoRange(true);
		domain.setLowerMargin(0.0);
		domain.setUpperMargin(0.0);
		domain.setTickLabelsVisible(true);
		domain.setTickUnit(new DateTickUnit(DateTickUnitType.SECOND, 30));

		range.setTickUnit(new NumberTickUnit(0.2, new DecimalFormat(), 5));
		range.setAutoRange(true);
		range.setVisible(true);

	}

	/**
	 * @see Graph
	 */
	@Override
	public ChartPanel getChartPanel()
	{
		final XYPlot plot = new XYPlot(dataset, domain, range, renderer);
		plot.setBackgroundPaint(Color.darkGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		// plot.setInsets(new RectangleInsets(0, 0, 0, 0));
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinesVisible(true);

		if (stream.getMax() != null)
		{
			log.info("Getting chart panel for stream with maximum.");
			range.setAutoRange(false);

			range.setTickUnit(new NumberTickUnit(stream.getMax() / 10,
					new DecimalFormat(), 1));
			range.setRange(0, stream.getMax());
		}
		else if (stream.getType() == Type.RELATIVE)
		{
			range.setAutoRange(false);

			range.setTickUnit(new NumberTickUnit(0.1, new DecimalFormat(), 1));
			range.setRange(0, 1);
		}
		else
		{
			double sum = 0;
			final List<TimedDatapoint> datapoints = stream
					.getRecentlyProcessedDatapoints();
			for (final TimedDatapoint dp : datapoints)
			{
				sum += dp.getValue();
			}
			range.setTickUnit(new NumberTickUnit((int) (sum / (datapoints
					.size() * 5)), new DecimalFormat(), 1));
		}

		final JFreeChart chart = new JFreeChart(stream.getStreamname(),
				new Font("SansSerif", Font.PLAIN, 20), plot, true);
		chart.setBackgroundPaint(Color.white);
		chart.removeLegend();
		final ChartPanel chartPanel = new ChartPanel(chart);
		// chartPanel.setBorder(BorderFactory
		// .createBevelBorder(BevelBorder.LOWERED));

		return chartPanel;
	}
}
