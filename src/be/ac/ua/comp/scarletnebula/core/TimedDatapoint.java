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