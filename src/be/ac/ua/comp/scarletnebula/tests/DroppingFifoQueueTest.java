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

package be.ac.ua.comp.scarletnebula.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import be.ac.ua.comp.scarletnebula.misc.DroppingFifoQueue;

public class DroppingFifoQueueTest {
	@Test
	public void basicTest() {
		final DroppingFifoQueue<Integer> queue = new DroppingFifoQueue<Integer>(
				6);

		queue.add(99);

		assertEquals(Arrays.asList(99), queue.asList());

		queue.add(98);
		assertEquals(Arrays.asList(99, 98), queue.asList());

		queue.add(97);
		queue.add(96);
		assertEquals(Arrays.asList(99, 98, 97, 96), queue.asList());

		queue.add(95);
		assertEquals(Arrays.asList(99, 98, 97, 96, 95), queue.asList());

		queue.add(94);
		assertEquals(Arrays.asList(99, 98, 97, 96, 95, 94), queue.asList());

		queue.add(93);
		assertEquals(Arrays.asList(98, 97, 96, 95, 94, 93), queue.asList());

		for (int i = 0; i < 100; i++) {
			queue.add(35);
		}

		assertEquals(6, queue.asList().size());
	}
}
