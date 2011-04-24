package be.ac.ua.comp.scarletnebula.gui.graph;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import be.ac.ua.comp.scarletnebula.gui.NewDatapointListener;
import be.ac.ua.comp.scarletnebula.misc.DroppingFifoQueue;

public class Datastream
{
	public class TimedDatapoint extends Datapoint
	{
		final long timeMs;

		public long getTimeMs()
		{
			return timeMs;
		}

		public TimedDatapoint(Datapoint dp)
		{
			super(dp.datapointType, dp.datastream, dp.value, dp.lowWarnLevel,
					dp.mediumWarnLevel, dp.highWarnLevel, dp.max);

			Calendar c = Calendar.getInstance();
			timeMs = c.getTimeInMillis();
		}
	}

	private final Collection<NewDatapointListener> newDatapointListeners = new ArrayList<NewDatapointListener>();
	private final Datapoint.Type type;
	private final String streamname;
	private Double max;
	private Double lowWarnLevel;
	private Double mediumWarnLevel;
	private Double highWarnLevel;
	private DroppingFifoQueue<TimedDatapoint> processedDatapoints = new DroppingFifoQueue<TimedDatapoint>(
			50);

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
		this.type = datapoint.datapointType;
	}

	public Datapoint.Type getType()
	{
		return type;
	}

	public void addNewDatapointListener(NewDatapointListener listener)
	{
		newDatapointListeners.add(listener);
	}

	protected void updateNewDatapointObservers(Datapoint datapoint)
	{
		for (NewDatapointListener listener : newDatapointListeners)
		{
			listener.newDataPoint(datapoint);
		}
	}

	public void newDatapoint(Datapoint datapoint)
	{
		processedDatapoints.add(new TimedDatapoint(datapoint));
		updateNewDatapointObservers(datapoint);
	}

	public List<TimedDatapoint> getRecentlyProcessedDatapoints()
	{
		return processedDatapoints.asList();
	}
}
