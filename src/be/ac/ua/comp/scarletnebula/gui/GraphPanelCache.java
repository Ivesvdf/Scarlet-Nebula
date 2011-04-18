package be.ac.ua.comp.scarletnebula.gui;

import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartPanel;

import be.ac.ua.comp.scarletnebula.core.Server;

public class GraphPanelCache
{
	private final Map<Server, ChartPanel> bareServerCache = new HashMap<Server, ChartPanel>();
	private static final GraphPanelCache instance = new GraphPanelCache();

	private GraphPanelCache()
	{

	}

	public static GraphPanelCache get()
	{
		return instance;
	}

	public void addToBareServerCache(Server server, ChartPanel panel)
	{
		bareServerCache.put(server, panel);
	}

	public void clearBareServerCache(Server server)
	{
		bareServerCache.remove(server);
	}

	public boolean inBareServerCache(Server server)
	{
		return bareServerCache.containsKey(server);
	}

	public ChartPanel getBareChartPanel(Server server)
	{
		return bareServerCache.get(server);
	}
}
