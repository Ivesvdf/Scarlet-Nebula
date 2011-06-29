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

import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartPanel;

import be.ac.ua.comp.scarletnebula.core.Server;

public class GraphPanelCache {
	private final Map<Server, ChartPanel> bareServerCache = new HashMap<Server, ChartPanel>();
	private static final GraphPanelCache instance = new GraphPanelCache();

	private GraphPanelCache() {

	}

	public static GraphPanelCache get() {
		return instance;
	}

	public void addToBareServerCache(final Server server, final ChartPanel panel) {
		bareServerCache.put(server, panel);
	}

	public void clearBareServerCache(final Server server) {
		bareServerCache.remove(server);
	}

	public boolean inBareServerCache(final Server server) {
		return bareServerCache.containsKey(server);
	}

	public ChartPanel getBareChartPanel(final Server server) {
		return bareServerCache.get(server);
	}
}
