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

import com.google.gson.Gson;

/**
 * A datapoint in a Datastream.
 * 
 * @author ives
 * 
 */
public class Datapoint implements Comparable {

	/**
	 * Type of datapoint.
	 * 
	 * @author ives
	 * 
	 */
	public enum Type {
		RELATIVE, ABSOLUTE
	};

	private static final Gson GSON = new Gson();
	private Type datapointType;
	private String datastream;
	private double value;
	private Double lowWarnLevel = null;
	private Double mediumWarnLevel = null;
	private Double highWarnLevel = null;
	private Double max = null;

	public Datapoint() {

	}

	public Datapoint(final Type datapointType, final String datastream,
			final double value, final Double lowWarnLevel,
			final Double mediumWarnLevel, final Double highWarnLevel,
			final Double max) {
		this.datapointType = datapointType;
		this.datastream = datastream;
		this.value = value;
		this.lowWarnLevel = lowWarnLevel;
		this.mediumWarnLevel = mediumWarnLevel;
		this.highWarnLevel = highWarnLevel;
		this.max = max;

		if (datapointType == Type.RELATIVE) {
			this.max = 1.0;
		}
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof Datapoint)) {
			return 0;
		} else {
			Double myValue = value;
			Double hisValue = ((Datapoint) o).value;
			return myValue.compareTo(hisValue);
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Datapoint)) {
			return false;
		}
		final Datapoint o = (Datapoint) obj;
		return this.datapointType == o.datapointType
				&& this.datastream.equals(o.datastream)
				&& this.value == o.value
				&& equalOrBothNull(this.lowWarnLevel, o.lowWarnLevel)
				&& equalOrBothNull(this.mediumWarnLevel, o.mediumWarnLevel)
				&& equalOrBothNull(this.highWarnLevel, o.highWarnLevel)
				&& equalOrBothNull(this.max, o.max);
	}

	private boolean equalOrBothNull(final Object o1, final Object o2) {
		return (o1 == null && o2 == null) || o1.equals(o2);
	}

	@Override
	public String toString() {
		return "[" + datapointType + " in " + datastream + "] " + value
				+ " (WL=" + lowWarnLevel + ", WM=" + mediumWarnLevel + ", WH="
				+ highWarnLevel + ", Max=" + max + ")";
	}

	public String toJson() {
		return GSON.toJson(this);
	}

	public static Datapoint fromJson(final String input) {
		return GSON.fromJson(input, Datapoint.class);
	}

	/**
	 * @return the datapointType
	 */
	public Type getDatapointType() {
		return datapointType;
	}

	/**
	 * @return the datastream
	 */
	public String getDatastream() {
		return datastream;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @return the lowWarnLevel
	 */
	public Double getLowWarnLevel() {
		return lowWarnLevel;
	}

	/**
	 * @return the mediumWarnLevel
	 */
	public Double getMediumWarnLevel() {
		return mediumWarnLevel;
	}

	/**
	 * @return the highWarnLevel
	 */
	public Double getHighWarnLevel() {
		return highWarnLevel;
	}

	/**
	 * @return the max
	 */
	public Double getMax() {
		return max;
	}
}
