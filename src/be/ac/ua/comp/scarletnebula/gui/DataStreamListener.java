package be.ac.ua.comp.scarletnebula.gui;

import org.jfree.data.time.RegularTimePeriod;

public interface DataStreamListener
{
	/**
	 * Notifies when a new datapoint is added to stream streamname at the
	 * current time with value currentValue
	 * 
	 * @param streamname
	 *            Name of the stream this point was detected in
	 * @param currentValue
	 *            The new value in the datastream
	 */
	void newDataPoint(String streamname, double currentValue);

	/**
	 * Notifies when a new datapoint is added to the stream @param streamname at
	 * the time @param time with with value currentValue
	 * 
	 * @param streamname
	 *            Name of the stream
	 * @param time
	 *            The time at which this datapoint was detected
	 * @param currentValue
	 *            The new value in the datastream
	 */
	void newDataPoint(String streamname, RegularTimePeriod time,
			double currentValue);

}
