package be.ac.ua.comp.scarletnebula.core;

import java.util.ArrayList;
import java.util.Collection;

import be.ac.ua.comp.scarletnebula.gui.DataStreamListener;

public class ServerStatisticsManager
{
	final Collection<DataStreamListener> listeners = new ArrayList<DataStreamListener>();

	public void addStreamListener(DataStreamListener listener)
	{
		listeners.add(listener);
	}

	public void poll()
	{
		// Send data to the server and parse its response

		updateObservers("CPU", 0.88);
	}

	private void updateObservers(String datastream, double value)
	{
		for (DataStreamListener listener : listeners)
		{
			listener.newDataPoint(datastream, value);
		}
	}

}
