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
