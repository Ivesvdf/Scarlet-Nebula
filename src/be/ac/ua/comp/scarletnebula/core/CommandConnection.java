package be.ac.ua.comp.scarletnebula.core;

import com.jcraft.jsch.JSchException;

abstract public class CommandConnection {
	/**
	 * Executes a command on a remote server.
	 * 
	 * @param Command
	 *            The command to be executed
	 * @return Shell output.
	 * @throws JSchException
	 */
	abstract public String executeCommand(String command) throws Exception;

	/**
	 * Closes this connection.
	 */
	abstract public void close();
}
