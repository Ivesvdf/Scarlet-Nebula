package be.ac.ua.comp.scarletnebula.gui.graph;

import java.util.ArrayList;
import java.util.Collection;

import be.ac.ua.comp.scarletnebula.gui.NewDatapointListener;

public class Datastream
{
	private final Collection<NewDatapointListener> newDatapointListeners = new ArrayList<NewDatapointListener>();
	private final String streamname;
	private Double max;
	private Double lowWarnLevel;
	private Double mediumWarnLevel;
	private Double highWarnLevel;

	public String getStreamname()
	{
		return streamname;
	}

	public Double getMax()
	{
		return max;
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

	public Datastream(Datapoint datapoint)
	{
		this.max = datapoint.max;
		this.lowWarnLevel = datapoint.lowWarnLevel;
		this.mediumWarnLevel = datapoint.mediumWarnLevel;
		this.highWarnLevel = datapoint.highWarnLevel;
		this.streamname = datapoint.datastream;
	}

	public void addNewDatapointListener(NewDatapointListener listener)
	{
		newDatapointListeners.add(listener);
	}

	public void updateNewDatapointObservers(Datapoint datapoint)
	{
		for (NewDatapointListener listener : newDatapointListeners)
		{
			listener.newDataPoint(datapoint);
		}
	}
}
