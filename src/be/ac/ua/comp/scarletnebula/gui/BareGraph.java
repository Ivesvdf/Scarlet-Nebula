package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;

/**
 * A Graph without a legend, title or axes
 * 
 * @author ives
 * 
 */
public class BareGraph extends Graph {
	private final DateAxis domain = new DateAxis();
	private final NumberAxis range = new NumberAxis();

	/**
	 * Constructor.
	 * 
	 * @param maximumAge
	 *            The age after which data is no longer displayed in the graph
	 */
	public BareGraph(final long maximumAge) {
		super(maximumAge);
		domain.setVisible(false);
		domain.setAutoRange(true);
		domain.setLowerMargin(0.0);
		domain.setUpperMargin(0.0);
		domain.setTickLabelsVisible(true);
		final int secondsBetweenTicks = 30;
		domain.setTickUnit(new DateTickUnit(DateTickUnitType.SECOND,
				secondsBetweenTicks));

		range.setTickUnit(new NumberTickUnit(0.2, new DecimalFormat(), 5));
		range.setRange(0, 1);
		range.setVisible(false);
	}

	/**
	 * @see Graph
	 */
	@Override
	public ChartPanel getChartPanel() {
		final XYPlot plot = new XYPlot(dataset, domain, range, renderer);
		plot.setBackgroundPaint(Color.darkGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setInsets(new RectangleInsets(0, 0, 0, 0));
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinesVisible(true);

		final JFreeChart chart = new JFreeChart(null, new Font("SansSerif",
				Font.BOLD, 24), plot, true);
		chart.setBackgroundPaint(Color.white);
		chart.removeLegend();
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));

		return chartPanel;
	}

}
