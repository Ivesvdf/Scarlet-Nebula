package be.ac.ua.comp.scarletnebula.core;

/**
 * An abstract command connection to a server.
 * 
 * @author ives
 * 
 */
public abstract class CommandConnection {
	/**
	 * Executes a command on a remote server.
	 * 
	 * @param command
	 *            The command to be executed
	 * @return Shell output.
	 * @throws Exception
	 */
	public abstract String executeCommand(String command) throws Exception;

	/**
	 * Closes this connection.
	 */
	public abstract void close();
}
