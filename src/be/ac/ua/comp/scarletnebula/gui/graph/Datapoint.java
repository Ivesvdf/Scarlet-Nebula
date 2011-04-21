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
	final double lowWarnLevel;
	final double mediumWarnLevel;
	final double highWarnLevel;

	public Datapoint(Type datapointType, String datastream, double value,
			double lowWarnLevel, double mediumWarnLevel, double highWarnLevel)
	{
		this.datapointType = datapointType;
		this.datastream = datastream;
		this.value = value;
		this.lowWarnLevel = lowWarnLevel;
		this.mediumWarnLevel = mediumWarnLevel;
		this.highWarnLevel = highWarnLevel;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Datapoint))
		{
			return false;
		}
		Datapoint o = (Datapoint) obj;
		return this.datapointType == o.datapointType
				&& this.datastream.equals(o.datastream)
				&& this.value == o.value && this.lowWarnLevel == o.lowWarnLevel
				&& this.mediumWarnLevel == o.mediumWarnLevel
				&& this.highWarnLevel == o.highWarnLevel;
	}

	@Override
	public String toString()
	{
		return "[" + datapointType + " in " + datastream + "] " + value
				+ " (WL=" + lowWarnLevel + ", WM=" + mediumWarnLevel + ", WH="
				+ highWarnLevel + ")";
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

	public double getLowWarnLevel()
	{
		return lowWarnLevel;
	}

	public double getMediumWarnLevel()
	{
		return mediumWarnLevel;
	}

	public double getHighWarnLevel()
	{
		return highWarnLevel;
	}
}
