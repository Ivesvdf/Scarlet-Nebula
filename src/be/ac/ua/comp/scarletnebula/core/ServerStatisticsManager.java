package be.ac.ua.comp.scarletnebula.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.SSHCommandConnection.ChannelInputStreamTuple;
import be.ac.ua.comp.scarletnebula.gui.NewDatapointListener;
import be.ac.ua.comp.scarletnebula.gui.NotPromptingJschUserInfo;
import be.ac.ua.comp.scarletnebula.gui.graph.Datapoint;
import be.ac.ua.comp.scarletnebula.gui.graph.Datastream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;

public class ServerStatisticsManager
{
	private final class PollingRunnable implements Runnable
	{

		@Override
		public void run()
		{
			try
			{
				final SSHCommandConnection connection = (SSHCommandConnection) server
						.newCommandConnection(new NotPromptingJschUserInfo());
				ChannelInputStreamTuple tup = connection
						.executeContinuousCommand(server.getStatisticsCommand());

				InputStream pollingInputStream = tup.inputStream;
				Channel sshChannel = tup.channel;

				StringBuilder result = new StringBuilder();
				final int buffersize = 1024;
				byte[] tmp = new byte[buffersize];

				while (!stop)
				{
					while (pollingInputStream.available() > 0)
					{
						int i = pollingInputStream.read(tmp, 0, buffersize);
						if (i < 0)
							break;
						result.append(new String(tmp, 0, i));

						// Split on newlines
						while (result.indexOf("\n") >= 0)
						{
							int nlPos = result.indexOf("\n");
							String before = result.substring(0, nlPos);
							newDatapoint(before);
							String after = result.substring(nlPos + 1);

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
					catch (Exception ee)
					{
					}
				}
				sshChannel.disconnect();

			}
			catch (JSchException e)
			{
				log.error("Problem executing continuous command.", e);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
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

	private final Server server;
	private boolean stop = false;
	private final Map<String, Datastream> availableStreams = new HashMap<String, Datastream>();
	private final Map<String, Collection<NewDatapointListener>> futureHookups = new HashMap<String, Collection<NewDatapointListener>>();

	private PollingRunnable pollingRunnable = new PollingRunnable();

	ServerStatisticsManager(Server server)
	{
		this.server = server;

		final Thread readerThread = new Thread(pollingRunnable);
		readerThread.start();
		log.info("Starting polling thread");
	}

	private void newDatapoint(String stringRepresentation)
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
				for (NewDatapointListener listener : futureHookups
						.get(datastreamName))
				{
					log.info("Adding future hookup listener to newly created stream.");
					newDatastream.addNewDatapointListener(listener);
				}
				futureHookups.remove(datastreamName);
			}
		}

		availableStreams.get(datastreamName).updateNewDatapointObservers(
				datapoint);
	}

	public void addNewDatastreamListener(NewDatastreamListener listener)
	{
		newDatastreamListeners.add(listener);
	}

	private void updateNewDatastreamObservers(Datapoint datapoint)
	{
		for (NewDatastreamListener listener : newDatastreamListeners)
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

	/**
	 * Adds a new listener to the Datastream datastream. If no such stream is
	 * found, this request is stored and applied as soon as this stream is
	 * created. So when the first datapoint for this stream arrives, the
	 * listener will automatically be added.
	 * 
	 * @param listener
	 * @param datastream
	 */
	public void addNewDatapointListener(NewDatapointListener listener,
			String datastream)
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
}
