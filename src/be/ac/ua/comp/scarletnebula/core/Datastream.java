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

package be.ac.ua.comp.scarletnebula.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.ac.ua.comp.scarletnebula.gui.NewDatapointListener;
import be.ac.ua.comp.scarletnebula.misc.DroppingFifoQueue;

public class Datastream {
	public enum WarnLevel {
		NONE, LOW, MEDIUM, HIGH;
	};

	private final Collection<NewDatapointListener> newDatapointListeners = new ArrayList<NewDatapointListener>();
	private final Datapoint.Type type;
	private final String streamname;
	private final Double max;
	private final Double lowWarnLevel;
	private final Double mediumWarnLevel;
	private final Double highWarnLevel;
	private final DroppingFifoQueue<TimedDatapoint> processedDatapoints = new DroppingFifoQueue<TimedDatapoint>(
			120);
	private WarnLevel currentWarnLevel = WarnLevel.NONE;

	public WarnLevel getCurrentWarnLevel() {
		return currentWarnLevel;
	}

	public String getStreamname() {
		return streamname;
	}

	public Double getMax() {
		return max;
	}

	public Double getLowWarnLevel() {
		return lowWarnLevel;
	}

	public Double getMediumWarnLevel() {
		return mediumWarnLevel;
	}

	public Double getHighWarnLevel() {
		return highWarnLevel;
	}

	public Datastream(final Datapoint datapoint) {
		this.max = datapoint.getMax();
		this.lowWarnLevel = datapoint.getLowWarnLevel();
		this.mediumWarnLevel = datapoint.getMediumWarnLevel();
		this.highWarnLevel = datapoint.getHighWarnLevel();
		this.streamname = datapoint.getDatastream();
		this.type = datapoint.getDatapointType();
	}

	public Datapoint.Type getType() {
		return type;
	}

	public void addNewDatapointListener(final NewDatapointListener listener) {
		newDatapointListeners.add(listener);
	}

	protected void updateNewDatapointObservers(final Datapoint datapoint) {
		for (final NewDatapointListener listener : newDatapointListeners) {
			listener.newDataPoint(datapoint);
		}
	}

	public void newDatapoint(final Datapoint datapoint) {
		if (datapoint.getHighWarnLevel() != null
				&& datapoint.getValue() > datapoint.getHighWarnLevel()) {
			currentWarnLevel = WarnLevel.HIGH;
		} else if (datapoint.getMediumWarnLevel() != null
				&& datapoint.getValue() > datapoint.getMediumWarnLevel()) {
			currentWarnLevel = WarnLevel.MEDIUM;
		} else if (datapoint.getLowWarnLevel() != null
				&& datapoint.getValue() > datapoint.getLowWarnLevel()) {
			currentWarnLevel = WarnLevel.LOW;
		} else {
			currentWarnLevel = WarnLevel.NONE;
		}

		processedDatapoints.add(new TimedDatapoint(datapoint));
		updateNewDatapointObservers(datapoint);
	}

	public List<TimedDatapoint> getRecentlyProcessedDatapoints() {
		return processedDatapoints.asList();
	}
}
