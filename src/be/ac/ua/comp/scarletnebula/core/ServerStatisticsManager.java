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

	private final Collection<NewDatapointListener> newDatapointListeners = new ArrayList<NewDatapointListener>();
	private final Collection<NewDatastreamListener> newDatastreamListeners = new ArrayList<NewDatastreamListener>();

	private final Server server;
	private boolean stop = false;
	private final Map<String, Datapoint> availableStreams = new HashMap<String, Datapoint>();

	private PollingRunnable pollingRunnable = new PollingRunnable();

	ServerStatisticsManager(Server server)
	{
		this.server = server;

		final Thread readerThread = new Thread(pollingRunnable);
		readerThread.start();
		log.info("Starting polling thread");
	}

	public void newDatapoint(String stringRepresentation)
	{
		final Datapoint datapoint = Datapoint.fromJson(stringRepresentation);

		if (!availableStreams.containsKey(datapoint.getDatastreamName()))
		{
			updateNewDatastreamObservers(datapoint);
			availableStreams.put(datapoint.getDatastreamName(), datapoint);
		}

		updateNewDatapointObservers(datapoint);
	}

	public void addNewDatapointListener(NewDatapointListener listener)
	{
		newDatapointListeners.add(listener);
	}

	public void addNewDatastreamListener(NewDatastreamListener listener)
	{
		newDatastreamListeners.add(listener);
	}

	private void updateNewDatapointObservers(Datapoint datapoint)
	{
		for (NewDatapointListener listener : newDatapointListeners)
		{
			listener.newDataPoint(datapoint);
		}
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
}
