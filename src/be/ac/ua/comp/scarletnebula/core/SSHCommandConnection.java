package be.ac.ua.comp.scarletnebula.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.JSchSession;
import com.jcraft.jcterm.Term;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SSHCommandConnection extends CommandConnection
{
	Session session = null;

	SSHCommandConnection(String address, String keypairfilename, UserInfo ui)
			throws Exception
	{
		int port = 22;
		String user = "ubuntu";

		JSchSession jschsession = JSchSession.getSession("p080558", "",
				"radix.cmi.ua.ac.be", port, ui, null);
		java.util.Properties config = new java.util.Properties();

		config.put("compression.s2c", "zlib,none");
		config.put("compression.c2s", "zlib,none");

		session = jschsession.getSession();
		session.setConfig(config);
		session.rekey();

	}

	@Override
	public String executeCommand(String command)
	{
		// ssh.auth(root, authmethods)
		// ssh.authPassword("root", "fakepassword");
		// ssh.authPublickey(System.getProperty("user.name"));

		/*
		 * String output = ""; try {
		 * 
		 * // final Command cmd = session.exec(command); //
		 * System.out.println("\n** exit status: " + cmd.getExitStatus());
		 * 
		 * // output = cmd.getOutputAsString(); } catch (ConnectionException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (TransportException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } finally { close(); }
		 * 
		 * return output;
		 */
		return null;
	}

	@Override
	public void close()
	{
		session.disconnect();
	}

	public Connection getJSchConnection() throws JSchException, IOException
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

}
