package be.ac.ua.comp.scarletnebula.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.SSHCommandConnection.ChannelInputStreamTuple;
import be.ac.ua.comp.scarletnebula.gui.NewDatapointListener;
import be.ac.ua.comp.scarletnebula.gui.NotPromptingJschUserInfo;
import be.ac.ua.comp.scarletnebula.gui.graph.Datapoint;
import be.ac.ua.comp.scarletnebula.gui.graph.Datastream;
import be.ac.ua.comp.scarletnebula.gui.graph.Datastream.TimedDatapoint;

import com.jcraft.jsch.Channel;

public class ServerStatisticsManager
{
	private final class PollingRunnable implements Runnable
	{
		private boolean stop = false;

		@Override
		public void run()
		{
			try
			{
				final SSHCommandConnection connection = (SSHCommandConnection) server
						.newCommandConnection(new NotPromptingJschUserInfo());
				final ChannelInputStreamTuple tup = connection
						.executeContinuousCommand(server.getStatisticsCommand());

				final InputStream pollingInputStream = tup.inputStream;
				final Channel sshChannel = tup.channel;

				StringBuilder result = new StringBuilder();
				final int buffersize = 1024;
				final byte[] tmp = new byte[buffersize];

				while (!stop)
				{
					while (pollingInputStream.available() > 0)
					{
						final int i = pollingInputStream.read(tmp, 0,
								buffersize);
						if (i < 0)
						{
							break;
						}
						result.append(new String(tmp, 0, i));
						// Split on newlines
						while (result.indexOf("\n") >= 0)
						{
							final int nlPos = result.indexOf("\n");
							final String before = result.substring(0, nlPos);
							newDatapoint(before);
							final String after = result.substring(nlPos + 1);

							result = new StringBuilder(after);
						}
					}
					if (sshChannel.isClosed())
					{
						log.warn("SSH Channel was closed.");
						break;
					}
					try
					{
						Thread.sleep(1000);
					}
					catch (final Exception ee)
					{
					}
				}
				sshChannel.disconnect();

			}
			catch (final Exception e)
			{
				log.error("Problem executing continuous command.", e);
			}
		}

		public void pleaseStop()
		{
			stop = true;
		}
	}

	final private static Log log = LogFactory
			.getLog(ServerStatisticsManager.class);

	private final Collection<NewDatastreamListener> newDatastreamListeners = new ArrayList<NewDatastreamListener>();
	private final Collection<DeleteDatastreamListener> deleteDatastreamListeners = new ArrayList<DeleteDatastreamListener>();

	private final Server server;
	private final Map<String, Datastream> availableStreams = new HashMap<String, Datastream>();
	private final Map<String, Collection<NewDatapointListener>> futureHookups = new HashMap<String, Collection<NewDatapointListener>>();

	private PollingRunnable pollingRunnable;

	ServerStatisticsManager(final Server server)
	{
		this.server = server;

		startPolling();
	}

	private void startPolling()
	{
		pollingRunnable = new PollingRunnable();
		final Thread readerThread = new Thread(pollingRunnable);
		readerThread.start();
		log.info("Starting polling thread");
	}

	private void newDatapoint(final String stringRepresentation)
	{
		final Datapoint datapoint = Datapoint.fromJson(stringRepresentation);

		final String datastreamName = datapoint.getDatastreamName();
		if (!availableStreams.containsKey(datastreamName))
		{
			log.info("Registering new stream " + datastreamName);
			updateNewDatastreamObservers(datapoint);
			final Datastream newDatastream = new Datastream(datapoint);
			availableStreams.put(datastreamName, newDatastream);

			// If the streamname appears in the futureHookups, add the listeners
			// for that streamname
			if (futureHookups.containsKey(datastreamName))
			{
				for (final NewDatapointListener listener : futureHookups
						.get(datastreamName))
				{
					log.info("Adding future hookup listener to newly created stream.");
					newDatastream.addNewDatapointListener(listener);
				}
				futureHookups.remove(datastreamName);
			}
		}

		availableStreams.get(datastreamName).newDatapoint(datapoint);
	}

	public void addNewDatastreamListener(final NewDatastreamListener listener)
	{
		newDatastreamListeners.add(listener);
	}

	public void addDeleteDatastreamListener(final DeleteDatastreamListener listener)
	{
		deleteDatastreamListeners.add(listener);
	}

	private void updateNewDatastreamObservers(final Datapoint datapoint)
	{
		for (final NewDatastreamListener listener : newDatastreamListeners)
		{
			listener.newDataStream(datapoint);
		}
	}

	/**
	 * Stops the polling thead and closes the SSH connection.
	 */
	public void stop()
	{
		log.info("Stopping polling thread");
		pollingRunnable.pleaseStop();
	}

	public Collection<String> getAvailableDatastreams()
	{
		return availableStreams.keySet();
	}

	/**
	 * Interface to implement if you'd like to be notified of new data streams
	 * that present themselves relating to this server.
	 * 
	 * @author ives
	 * 
	 */
	public interface NewDatastreamListener
	{
		/**
		 * Called when a new datastream is registered, together with the first
		 * point in that datastream. Do *not* use the value in this point as an
		 * actual datapoint, instead use a NewDataPointListener for this. The
		 * Datapoint should only be used for its fields such as lowWarnLevel,
		 * max, etc. This method is guaranteed to be called before the
		 * NewDatapointObservers.
		 * 
		 * @param datapoint
		 */
		public void newDataStream(Datapoint datapoint);
	}

	public interface DeleteDatastreamListener
	{
		/**
		 * Called when a datastream is removed (when the corresponding graphs
		 * should no longer be displayed)
		 * 
		 * @author ives
		 * 
		 */
		public void deleteDataStream(String streamname);
	}

	/**
	 * Adds a new listener to the Datastream datastream. If no such stream is
	 * found, this request is stored and applied as soon as this stream is
	 * created. So when the first datapoint for this stream arrives, the
	 * listener will automatically be added.
	 * 
	 * @param listener
	 * @param datastream
	 */
	public void addNewDatapointListener(final NewDatapointListener listener,
			final String datastream)
	{
		if (availableStreams.containsKey(datastream))
		{
			availableStreams.get(datastream).addNewDatapointListener(listener);
		}
		else
		{
			log.info("Adding stream " + datastream
					+ " to be hooked when it arrives.");

			if (!futureHookups.containsKey(datastream))
			{
				futureHookups.put(datastream,
						new ArrayList<NewDatapointListener>());
			}
			futureHookups.get(datastream).add(listener);
		}
	}

	public Datastream getDatastream(final String streamname)
	{
		return availableStreams.get(streamname);
	}

	public List<TimedDatapoint> getHistoricalDatapoints(final String streamname)
	{
		List<TimedDatapoint> datapoints;

		if (availableStreams.containsKey(streamname))
		{
			datapoints = availableStreams.get(streamname)
					.getRecentlyProcessedDatapoints();
		}
		else
		{
			datapoints = new ArrayList<TimedDatapoint>();
		}

		return datapoints;
	}

	public void reset()
	{
		for (final String streamname : availableStreams.keySet())
		{
			for (final DeleteDatastreamListener listener : deleteDatastreamListeners)
			{
				listener.deleteDataStream(streamname);
			}
		}

		stop();
		startPolling();
	}

	public Datastream.WarnLevel getHighestWarnLevel()
	{
		Datastream.WarnLevel highestWarnLevel = Datastream.WarnLevel.NONE;

		for (final Datastream datastream : availableStreams.values())
		{
			if (datastream.getCurrentWarnLevel().compareTo(highestWarnLevel) > 0)
			{
				highestWarnLevel = datastream.getCurrentWarnLevel();
			}
		}

		return highestWarnLevel;
	}
}
