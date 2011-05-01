package be.ac.ua.comp.scarletnebula.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jfree.util.Log;

import be.ac.ua.comp.scarletnebula.gui.NotPromptingJschUserInfo;

import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.Term;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SSHCommandConnection extends CommandConnection
{
	Session session = null;

	private enum LoginMethod
	{
		PASSWORD, KEY
	};

	private SSHCommandConnection(String address, String username,
			String keypairfilenameOrPassword, UserInfo ui,
			LoginMethod loginMethod) throws Exception
	{
		final JSch jsch = new JSch();

		if (loginMethod == LoginMethod.KEY)
		{
			jsch.addIdentity(keypairfilenameOrPassword);
		}
		session = jsch.getSession(username, address, 22);

		final java.util.Properties config = new java.util.Properties();

		config.put("compression.s2c", "zlib,none");
		config.put("compression.c2s", "zlib,none");

		session.setUserInfo(ui);
		session.setConfig(config);

		if (loginMethod == LoginMethod.PASSWORD)
		{
			session.setPassword(keypairfilenameOrPassword);
		}
		session.connect();
		// session.rekey();
	}

	static public SSHCommandConnection newConnectionWithPassword(
			String address, String username, String password, UserInfo ui)
			throws Exception
	{
		return new SSHCommandConnection(address, username, password, ui,
				LoginMethod.PASSWORD);
	}

	static public SSHCommandConnection newConnectionWithKey(String address,
			String username, String key, UserInfo ui) throws Exception
	{
		return new SSHCommandConnection(address, username, key, ui,
				LoginMethod.KEY);
	}

	@Override
	public String executeCommand(String command) throws JSchException,
			IOException
	{
		final ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand(command);

		channel.setInputStream(null);
		channel.setErrStream(System.err);

		final InputStream in = channel.getInputStream();

		channel.connect();

		final StringBuilder result = new StringBuilder();
		final byte[] tmp = new byte[1024];
		while (true)
		{
			while (in.available() > 0)
			{
				final int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				result.append(new String(tmp, 0, i));
			}
			if (channel.isClosed())
			{
				Log.info("exit-status: " + channel.getExitStatus());
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

		channel.disconnect();

		return result.toString();
	}

	public ChannelInputStreamTuple executeContinuousCommand(String command)
			throws JSchException, IOException
	{
		final ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand(command);

		channel.setInputStream(null);
		channel.setErrStream(System.err);

		final InputStream in = channel.getInputStream();

		channel.connect();

		return new ChannelInputStreamTuple(channel, in);
	}

	@Override
	public void close()
	{
		session.disconnect();
	}

	public Connection getJSchTerminalConnection() throws JSchException,
			IOException
	{
		final Channel channel = session.openChannel("shell");

		final OutputStream out = channel.getOutputStream();
		final InputStream in = channel.getInputStream();

		channel.connect();
		final OutputStream fout = out;
		final InputStream fin = in;
		final Channel fchannel = channel;

		final Connection connection = new Connection()
		{
			@Override
			public InputStream getInputStream()
			{
				return fin;
			}

			@Override
			public OutputStream getOutputStream()
			{
				return fout;
			}

			@Override
			public void requestResize(Term term)
			{
				if (fchannel instanceof ChannelShell)
				{
					final int c = term.getColumnCount();
					final int r = term.getRowCount();
					((ChannelShell) fchannel).setPtySize(c, r,
							c * term.getCharWidth(), r * term.getCharHeight());
				}
			}

			@Override
			public void close()
			{
				fchannel.disconnect();
			}
		};
		return connection;
	}

	public static void main(String[] args)
	{
		try
		{
			final SSHCommandConnection connection = SSHCommandConnection
					.newConnectionWithPassword("radix.cmi.ua.ac.be", "p080558",
							"somepassword", new NotPromptingJschUserInfo());

			final ChannelInputStreamTuple tuple = connection
					.executeContinuousCommand("date"); // while [[ 1 ]]; do echo
														// \"incoming data\"; date; sleep 1; done");

			final InputStream in = tuple.inputStream;
			final Channel channel = tuple.channel;

			System.out.println();

			StringBuilder result = new StringBuilder();
			final int buffersize = 1024;
			final byte[] tmp = new byte[buffersize];
			while (true)
			{

				while (in.available() > 0)
				{
					final int i = in.read(tmp, 0, buffersize);
					if (i < 0)
						break;
					result.append(new String(tmp, 0, i));

					// Start of weird shit
					while (result.indexOf("\n") >= 0)
					{
						final int nlPos = result.indexOf("\n");
						final String before = result.substring(0, nlPos);
						System.out.println("Newline detected: came before it:"
								+ before);
						final String after = result.substring(nlPos + 1);

						result = new StringBuilder(after);
					}
				}
				if (channel.isClosed())
				{
					System.out.println("ow ...");
					break;

				}
				try
				{
					System.out.println("sleeping");
					Thread.sleep(1000);
				}
				catch (final Exception ee)
				{
				}
			}
			channel.disconnect();

		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class ChannelInputStreamTuple
	{
		public Channel channel;
		public InputStream inputStream;

		ChannelInputStreamTuple(Channel channel, InputStream inputStream)
		{
			this.channel = channel;
			this.inputStream = inputStream;
		}
	}
}
