package be.ac.ua.comp.scarletnebula.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.core.SSHCommandConnection.ChannelInputStreamTuple;
import be.ac.ua.comp.scarletnebula.gui.DataStreamListener;
import be.ac.ua.comp.scarletnebula.gui.NotPromptingJschUserInfo;
import be.ac.ua.comp.scarletnebula.gui.graph.Datapoint;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;

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
				ChannelInputStreamTuple tup = connection
						.executeContinuousCommand("while [[ 1 ]]; "
								+ "do echo '{\"datapointType\":\"RELATIVE\",\"datastream\":\"CPU\",\"value\":0.63,\"lowWarnLevel\":0.5,\"mediumWarnLevel\":0.85,\"highWarnLevel\":0.95}\'; "
								+ "sleep 1; done");

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

	private final Collection<DataStreamListener> listeners = new ArrayList<DataStreamListener>();
	private final Server server;

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
		updateObservers(Datapoint.fromJson(stringRepresentation));
	}

	public void addStreamListener(DataStreamListener listener)
	{
		listeners.add(listener);
	}

	private void updateObservers(Datapoint datapoint)
	{
		for (DataStreamListener listener : listeners)
		{
			listener.newDataPoint(datapoint);
		}
	}

	public void stop()
	{
		log.info("Stopping polling thread");
		pollingRunnable.pleaseStop();
	}
}
