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

import java.util.Calendar;

/**
 * A Datapoint that occurred at a certain time.
 * 
 * @author ives
 * 
 */
public class TimedDatapoint extends Datapoint {
	private final long timeMs;

	/**
	 * @return The time this datapoint was measured in milliseconds.
	 */
	public long getTimeMs() {
		return timeMs;
	}

	/**
	 * Constructor that creates a TimedDatapoint at the current time.
	 * 
	 * @param dp
	 *            The datapoint that is contained in this.
	 */
	public TimedDatapoint(final Datapoint dp) {
		super(dp.getDatapointType(), dp.getDatastream(), dp.getValue(), dp
				.getLowWarnLevel(), dp.getMediumWarnLevel(), dp
				.getHighWarnLevel(), dp.getMax());

		final Calendar c = Calendar.getInstance();
		timeMs = c.getTimeInMillis();
	}

}