package be.ac.ua.comp.scarletnebula.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import be.ac.ua.comp.scarletnebula.gui.NewDatapointListener;
import be.ac.ua.comp.scarletnebula.misc.DroppingFifoQueue;

public class Datastream {
	public class TimedDatapoint extends Datapoint {
		final long timeMs;

		public long getTimeMs() {
			return timeMs;
		}

		public TimedDatapoint(final Datapoint dp) {
			super(dp.datapointType, dp.datastream, dp.value, dp.lowWarnLevel,
					dp.mediumWarnLevel, dp.highWarnLevel, dp.max);

			final Calendar c = Calendar.getInstance();
			timeMs = c.getTimeInMillis();
		}
	}

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
		this.max = datapoint.max;
		this.lowWarnLevel = datapoint.lowWarnLevel;
		this.mediumWarnLevel = datapoint.mediumWarnLevel;
		this.highWarnLevel = datapoint.highWarnLevel;
		this.streamname = datapoint.datastream;
		this.type = datapoint.datapointType;
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
