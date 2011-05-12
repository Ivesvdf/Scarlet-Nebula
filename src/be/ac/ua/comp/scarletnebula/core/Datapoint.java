package be.ac.ua.comp.scarletnebula.core;

import com.google.gson.Gson;

/**
 * A datapoint in a Datastream.
 * 
 * @author ives
 * 
 */
public class Datapoint {

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
