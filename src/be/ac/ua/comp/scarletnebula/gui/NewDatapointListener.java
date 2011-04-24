package be.ac.ua.comp.scarletnebula.gui;

import org.jfree.data.time.RegularTimePeriod;

import be.ac.ua.comp.scarletnebula.gui.graph.Datapoint;

public interface NewDatapointListener
{
	/**
	 * Notifies when a new datapoint is added to stream streamname at the
	 * current time with value currentValue
	 * 
	 * @param currentMeasurement
	 *            The new value in the datastream
	 */
	void newDataPoint(Datapoint currentMeasurement);

	/**
	 * Notifies when a new datapoint is added to the stream @param streamname at
	 * the time @param time with with value currentValue
	 * 
	 * @param time
	 *            The time at which this datapoint was detected
	 * @param currentMeasurement
	 *            The new value in the datastream
	 */
	void newDataPoint(RegularTimePeriod time, Datapoint currentMeasurement);

}
