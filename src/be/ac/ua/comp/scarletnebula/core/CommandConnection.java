package be.ac.ua.comp.scarletnebula.core;

abstract public class CommandConnection
{
	/**
	 * Executes a command on a remote server.
	 * @param Command The command to be executed
	 * @return Shell output.
	 */
	abstract public String executeCommand(String command);

	abstract public void close();
}
