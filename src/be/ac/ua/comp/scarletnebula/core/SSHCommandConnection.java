package be.ac.ua.comp.scarletnebula.core;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.text.Format;
import java.util.ArrayList;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.keyprovider.FileKeyProvider;
import net.schmizz.sshj.userauth.method.AuthMethod;

public class SSHCommandConnection extends CommandConnection
{
	final SSHClient ssh;
	
	SSHCommandConnection(String address, String keypairfilename) throws IOException
	{
		System.out.println("Keypair is " + keypairfilename);

		ssh = new SSHClient();
		ssh.loadKnownHosts();
		
		HostKeyVerifier verifier = new HostKeyVerifier()
		{
			@Override
			public boolean verify(String arg0, int arg1, PublicKey arg2)
			{
				// I can hear the people that invented SSH drop dead just so they can start
				// spinning in their graves. Problem is that there really is no way to know
				// this is really the good server -- it's not like you can actually request the 
				// fingerprint through some sort of a secure API (everything goes through plain
				// old http anyways so nobody could prevent someone from intercepting). 
				
				// In short, yes, you could be man-in-the-middled but you probably won't.
				return true;
			}
		};
		
		ssh.addHostKeyVerifier(verifier);

		ssh.connect(address);

		FileKeyProvider.Format fmt = net.schmizz.sshj.userauth.keyprovider.KeyProviderUtil
				.detectKeyFileFormat(new File(keypairfilename));

		if(fmt != FileKeyProvider.Format.PKCS8)
		{
			System.out.println("Unsupported key file format, ask Ives to put another if here...");
		}
		net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile keyfile = new net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile();
		keyfile.init(new File(keypairfilename));

		ssh.authPublickey("ubuntu", keyfile);

	}
	
	@Override
	public String executeCommand(String command)
	{
		//ssh.auth(root, authmethods)
		//ssh.authPassword("root", "fakepassword");
	//	ssh.authPublickey(System.getProperty("user.name"));
		
		Session session = null;
		String output = "";
		try
		{
			 session = ssh.startSession();

			final Command cmd = session.exec(command);
			//System.out.println("\n** exit status: " + cmd.getExitStatus());

			output = cmd.getOutputAsString();
		}
		catch (ConnectionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransportException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				session.close();
			}
			catch (TransportException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ConnectionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		return output;
	}
	
	public void close()
	{
		try
		{
			ssh.disconnect();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
