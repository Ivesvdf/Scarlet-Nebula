package be.ac.ua.comp.scarletnebula.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jfree.chart.ChartPanel;

import be.ac.ua.comp.scarletnebula.core.Server;
import be.ac.ua.comp.scarletnebula.core.ServerStatisticsManager;
import be.ac.ua.comp.scarletnebula.gui.graph.Datastream;
import be.ac.ua.comp.scarletnebula.misc.Colors;
import be.ac.ua.comp.scarletnebula.misc.Utils;

class ServerCellRenderer implements ListCellRenderer
{
	private static final long serialVersionUID = 1L;
	public static HashMap<Server, JPanel> panelMapping = new HashMap<Server, JPanel>();
	private static Log log = LogFactory.getLog(ServerCellRenderer.class);

	ServerCellRenderer()
	{
	}

	@Override
	public Component getListCellRendererComponent(final JList list,
			Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		// Dirty hack: the last item in the serverlist is always a fake server
		// that when double clicked produces an "add new server" wizard.
		if (value == null)
			return getNewServerServer(list, index, isSelected);
		final Server server = (Server) value;

		final JPanel p = createServerPanel(server, list, index, isSelected);
		final Color foreground = getForegroundColor(list, index, isSelected);

		final JLabel label = getServernameComponent(server, foreground);
		final JLabel tags = getTagComponent(server, foreground);
		// final ChartPanel chartPanel = getChartPanelComponent();

		final GraphPanelCache gcp = GraphPanelCache.get();

		final Component chartOrNothing;

		if (server.sshWillFail())
		{
			chartOrNothing = new JLabel();
		}
		else
		{
			chartOrNothing = gcp.inBareServerCache(server) ? gcp
					.getBareChartPanel(server) : createAndStoreBareChartPanel(
					list, server);
		}

		p.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 1, 5);
		c.anchor = GridBagConstraints.FIRST_LINE_START;

		p.add(label, c);
		c.insets = new Insets(0, 5, 5, 5);
		c.gridy = 1;
		p.add(tags, c);

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.gridy = 2;
		p.add(chartOrNothing, c);

		return p;

	}

	private ChartPanel createAndStoreBareChartPanel(final JList list,
			final Server server)
	{
		final BareGraph graph = new BareGraph((long) 30 * 60 * 1000);
		graph.registerRelativeDatastream(server,
				server.getPreferredDatastream(), Color.GREEN);
		graph.addServerToRefresh(server);
		final ChartPanel chartPanel = graph.getChartPanel();
		log.info("Making new baregraph for server " + server);

		GraphPanelCache.get().addToBareServerCache(server, chartPanel);
		return chartPanel;
	}

	private JLabel getServernameComponent(Server server, final Color foreground)
	{
		final JLabel label = new JLabel(server.getFriendlyName(),
				getServerIcon(server), SwingConstants.LEFT);
		label.setOpaque(false);

		label.setForeground(foreground);
		return label;
	}

	private JLabel getTagComponent(Server server, final Color foreground)
	{
		final JLabel tags = new JLabel();

		final Font tagFont = new Font(tags.getFont().getName(), Font.PLAIN, 11);
		tags.setFont(tagFont);
		tags.setText(Utils.implode(new ArrayList<String>(server.getTags()),
				", "));
		tags.setForeground(foreground);
		return tags;
	}

	Color getBackgroundColor(JList list, int index, boolean isSelected)
	{
		Color background;

		// check if this cell represents the current DnD drop location
		final JList.DropLocation dropLocation = list.getDropLocation();
		if (dropLocation != null && !dropLocation.isInsert()
				&& dropLocation.getIndex() == index)
		{
			background = Color.RED;
		}
		else if (isSelected)
		{
			background = UIManager.getColor("Tree.selectionBackground");
		}
		else
		{
			background = Color.WHITE;
		}

		return background;
	}

	Color getForegroundColor(JList list, int index, boolean isSelected)
	{
		Color foreground;

		// check if this cell represents the current DnD drop location
		final JList.DropLocation dropLocation = list.getDropLocation();
		if (dropLocation != null && !dropLocation.isInsert()
				&& dropLocation.getIndex() == index)
		{
			foreground = Color.WHITE;
		}
		else if (isSelected)
		{
			foreground = UIManager.getColor("Tree.selectionForeground");
		}
		else
		{
			foreground = Color.BLACK;
		}
		return foreground;
	}

	private JPanel createServerPanel(Server server, JList list, int index,
			boolean isSelected)
	{
		final JXPanel p = new JXPanel();
		p.setLayout(new GridBagLayout());
		final Color background = Colors.alpha(
				getBackgroundColor(list, index, isSelected), 0.4f);

		p.setBackground(background);

		Color color1 = Colors.White.color(0.5f);
		Color color2 = Colors.Gray.color(0.95f);

		final Point2D start = new Point2D.Float(0, 0);
		Point2D stop = new Point2D.Float(150, 500);

		if (server != null && !server.sshWillFail()
				&& server.getServerStatistics() != null)
		{
			final ServerStatisticsManager manager = server.getServerStatistics();
			final Datastream.WarnLevel warnlevel = manager.getHighestWarnLevel();

			if (warnlevel == Datastream.WarnLevel.HIGH)
			{
				color1 = Colors.Red.alpha(0.2f);
				color2 = Colors.Red.alpha(0.8f);
				stop = new Point2D.Float(500, 2);
			}
			else if (warnlevel == Datastream.WarnLevel.MEDIUM)
			{
				color1 = Colors.Orange.alpha(0.3f);
				color2 = Colors.Orange.alpha(0.8f);
				stop = new Point2D.Float(500, 2);
			}
			else if (warnlevel == Datastream.WarnLevel.LOW)
			{
				color1 = Colors.Orange.alpha(0.2f);
				color2 = Colors.Orange.alpha(0.4f);
				stop = new Point2D.Float(500, 2);
			}
		}

		final LinearGradientPaint gradientPaint = new LinearGradientPaint(start,
				stop, new float[] { 0.0f, 1.0f },
				new Color[] { color1, color2 });
		final MattePainter mattePainter = new MattePainter(gradientPaint, true);
		p.setBackgroundPainter(mattePainter);

		if (isSelected)
		{
			p.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(
							UIManager.getColor("List.background"), 2),
					BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
		}
		else
		{
			p.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(
							UIManager.getColor("List.background"), 2),
					BorderFactory.createEtchedBorder()));

		}

		return p;
	}

	private Component getNewServerServer(JList list, int index,
			boolean isSelected)
	{
		final JPanel p = createServerPanel(null, list, index, isSelected);
		final JLabel label = new JLabel("Start a new server", new ImageIcon(
				getClass().getResource("/images/add.png")), SwingConstants.LEFT);
		label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 16));
		// Border for better horizontal alignment
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
		p.add(label);

		return p;
	}

	/**
	 * Returns the icon that represents this server's status.
	 * 
	 * @param server
	 * @return The 16x16px Icon representing the server's state
	 */
	public ImageIcon getServerIcon(Server server)
	{
		String filename = null;

		switch (server.getStatus())
		{
			case PAUSED:
				filename = "/images/paused.png";
				break;
			case PENDING:
				filename = "/images/pending.png";
				break;
			case RUNNING: // This needs to be made load-dependent...
				filename = "/images/running_ok.png";
				break;
			case REBOOTING:
				filename = "/images/restarting.png";
				break;
			case STOPPING:
				filename = "/images/stopping.png";
				break;
			case TERMINATED:
				filename = "/images/terminated.png";
				break;

		}

		final ImageIcon icon = new ImageIcon(getClass().getResource(filename));
		return icon;
	}

	public JXPanel onRollOver(JXPanel input)
	{
		final Color color1 = Colors.White.color(0.5f);
		final Color color2 = Colors.Black.color(0.8f);
		// Color color2 = Colors.Red.color(0.2f);

		final LinearGradientPaint gradientPaint = new LinearGradientPaint(0.0f, 0.0f,
				250, 500, new float[] { 0.0f, 1.0f }, new Color[] { color1,
						color2 });
		final MattePainter mattePainter = new MattePainter(gradientPaint, true);

		input.setBackgroundPainter(new CompoundPainter<Object>(mattePainter,
				input.getBackgroundPainter()));
		return input;
	}
}