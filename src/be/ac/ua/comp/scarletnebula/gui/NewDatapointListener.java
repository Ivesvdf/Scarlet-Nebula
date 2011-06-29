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

import org.jfree.data.time.RegularTimePeriod;

import be.ac.ua.comp.scarletnebula.core.Datapoint;

public interface NewDatapointListener {
	/**
	 * Notifies when a new datapoint is added to stream streamname at the
	 * current time with value currentValue
	 * 
	 * @param currentMeasurement
	 *            The new value in the datastream
	 */
	void newDataPoint(Datapoint currentMeasurement);

	/**
	 * Notifies when a new datapoint is added to the stream @param streamname at
	 * the time @param time with with value currentValue
	 * 
	 * @param time
	 *            The time at which this datapoint was detected
	 * @param currentMeasurement
	 *            The new value in the datastream
	 */
	void newDataPoint(RegularTimePeriod time, Datapoint currentMeasurement);

}
