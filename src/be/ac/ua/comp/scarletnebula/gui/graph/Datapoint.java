package be.ac.ua.comp.scarletnebula.gui.graph;

import com.google.gson.Gson;

public class Datapoint
{
	public enum Type
	{
		RELATIVE, ABSOLUTE
	};

	final private static Gson gson = new Gson();
	final Type datapointType;
	final String datastream;
	final double value;
	Double lowWarnLevel = null;
	Double mediumWarnLevel = null;
	Double highWarnLevel = null;
	Double max = null;

	public Datapoint(Type datapointType, String datastream, Double value,
			Double lowWarnLevel, Double mediumWarnLevel, Double highWarnLevel,
			Double max)
	{
		this.datapointType = datapointType;
		this.datastream = datastream;
		this.value = value;
		this.lowWarnLevel = lowWarnLevel;
		this.mediumWarnLevel = mediumWarnLevel;
		this.highWarnLevel = highWarnLevel;
		this.max = max;

		if (datapointType == Type.RELATIVE)
			this.max = 1.0;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Datapoint))
		{
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

	private boolean equalOrBothNull(Object o1, Object o2)
	{
		return (o1 == null && o2 == null) || o1.equals(o2);
	}

	@Override
	public String toString()
	{
		return "[" + datapointType + " in " + datastream + "] " + value
				+ " (WL=" + lowWarnLevel + ", WM=" + mediumWarnLevel + ", WH="
				+ highWarnLevel + ", Max=" + max + ")";
	}

	public String toJson()
	{
		return gson.toJson(this);
	}

	public static Datapoint fromJson(String input)
	{
		return gson.fromJson(input, Datapoint.class);
	}

	public Type getDatapointType()
	{
		return datapointType;
	}

	public String getDatastreamName()
	{
		return datastream;
	}

	public double getValue()
	{
		return value;
	}

	public Double getLowWarnLevel()
	{
		return lowWarnLevel;
	}

	public Double getMediumWarnLevel()
	{
		return mediumWarnLevel;
	}

	public Double getHighWarnLevel()
	{
		return highWarnLevel;
	}

	public Double getMax()
	{
		return max;
	}
}
