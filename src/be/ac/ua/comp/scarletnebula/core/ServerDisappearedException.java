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
 * The exception that will be thrown when a Server is refreshed and the Server
 * is gone (either because it was deleted outside of Scarlet Nebula or because
 * it was terminated and disappeared in the mean while.
 * 
 * @author ives
 * 
 */
public class ServerDisappearedException extends Exception {
	private static final long serialVersionUID = 1L;

	ServerDisappearedException(final Server server) {
		super("Server " + server.getFriendlyName()
				+ " disappeared when refreshing.");
	}
}
