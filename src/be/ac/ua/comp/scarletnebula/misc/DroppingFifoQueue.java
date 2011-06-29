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

package be.ac.ua.comp.scarletnebula.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * A queue that has a limited size and will drop its oldest element when it's
 * full and a new element presents itself.
 * 
 * @author ives
 * 
 */
public class DroppingFifoQueue<T> {
	final private ArrayList<T> array = new ArrayList<T>();
	private int nextToTake = 0;
	private int lastValid;
	private final int maxCapacity;

	public DroppingFifoQueue(final int maxCapacity) {
		this.maxCapacity = maxCapacity;
		this.lastValid = -1;
		array.ensureCapacity(maxCapacity);
		for (int i = 0; i < maxCapacity; i++) {
			array.add(null);
		}
	}

	public void add(final T newElement) {

		if (nextToTake == lastValid) {
			lastValid = advance(lastValid);
		}

		array.set(nextToTake, newElement);

		nextToTake = advance(nextToTake);

		if (lastValid == -1) {
			lastValid = 0;
		}
	}

	/**
	 * Returns a list representation of the Queue, with the oldest elements
	 * first
	 * 
	 * @return List of the dropping queue, oldest first
	 */
	public List<T> asList() {
		final List<T> rv = new ArrayList<T>();

		if (lastValid == -1) {
			// Return an empty list
		} else {
			boolean goForward = true;
			for (int i = lastValid; goForward; i = advance(i)) {
				rv.add(array.get(i));

				goForward = (advance(i) != nextToTake);
			}
		}

		return rv;
	}

	/**
	 * Moves an index one step forward in the cyclical structure
	 * 
	 * @param oldPos
	 *            Position from where to advance
	 * @return The new position after advancing
	 */
	private int advance(final int oldPos) {
		return (oldPos + 1) % maxCapacity;
	}

}
