/*
 * Copyright (C) 2011  Ives van der Flaas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
