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

	public SSHCommandConnection(String address, String keypairfilename,
			UserInfo ui) throws Exception
	{
		String user = "ubuntu";

		JSch jsch = new JSch();

		jsch.addIdentity(keypairfilename);

		session = jsch.getSession(user, address, 22);

		java.util.Properties config = new java.util.Properties();

		config.put("compression.s2c", "zlib,none");
		config.put("compression.c2s", "zlib,none");

		session.setUserInfo(ui);
		session.setConfig(config);
		session.connect();
		// session.rekey();

	}

	public SSHCommandConnection(String address, String username,
			String password, UserInfo ui) throws Exception
	{
		JSch jsch = new JSch();

		session = jsch.getSession(username, address, 22);

		java.util.Properties config = new java.util.Properties();

		config.put("compression.s2c", "zlib,none");
		config.put("compression.c2s", "zlib,none");

		session.setUserInfo(ui);
		session.setConfig(config);
		session.setPassword(password);
		session.connect();
		// session.rekey();

	}

	@Override
	public String executeCommand(String command) throws JSchException,
			IOException
	{
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand(command);

		channel.setInputStream(null);
		channel.setErrStream(System.err);

		InputStream in = channel.getInputStream();

		channel.connect();

		StringBuilder result = new StringBuilder();
		byte[] tmp = new byte[1024];
		while (true)
		{
			while (in.available() > 0)
			{
				int i = in.read(tmp, 0, 1024);
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
			catch (Exception ee)
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

		InputStream in = channel.getInputStream();

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
		Channel channel = session.openChannel("shell");

		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();

		channel.connect();
		final OutputStream fout = out;
		final InputStream fin = in;
		final Channel fchannel = channel;

		Connection connection = new Connection()
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
					int c = term.getColumnCount();
					int r = term.getRowCount();
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
			SSHCommandConnection connection = new SSHCommandConnection(
					"radix.cmi.ua.ac.be", "p080558", "somepassword",
					new NotPromptingJschUserInfo());

			ChannelInputStreamTuple tuple = connection
					.executeContinuousCommand("date"); // while [[ 1 ]]; do echo
														// \"incoming data\"; date; sleep 1; done");

			InputStream in = tuple.inputStream;
			Channel channel = tuple.channel;

			System.out.println();

			StringBuilder result = new StringBuilder();
			final int buffersize = 1024;
			byte[] tmp = new byte[buffersize];
			while (true)
			{

				while (in.available() > 0)
				{
					int i = in.read(tmp, 0, buffersize);
					if (i < 0)
						break;
					result.append(new String(tmp, 0, i));

					// Start of weird shit
					while (result.indexOf("\n") >= 0)
					{
						int nlPos = result.indexOf("\n");
						String before = result.substring(0, nlPos);
						System.out.println("Newline detected: came before it:"
								+ before);
						String after = result.substring(nlPos + 1);

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
				catch (Exception ee)
				{
				}
			}
			channel.disconnect();

		}
		catch (Exception e)
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
