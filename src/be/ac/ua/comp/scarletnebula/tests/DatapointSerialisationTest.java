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

import org.junit.Test;

import be.ac.ua.comp.scarletnebula.core.Datapoint;
import com.google.gson.Gson;

public class DatapointSerialisationTest {
	@Test
	public void serialiseTest() {
		final Gson gson = new Gson();
		final Datapoint dp = getTestDataPoint();
		final Datapoint[] dps = { dp, dp, dp };
		System.out.println(gson.toJson(dps));
		assertEquals(gson.toJson(dps), getSerialisedDatapoints());
	}

	private Datapoint getTestDataPoint() {
		final Datapoint dp = new Datapoint(Datapoint.Type.RELATIVE, "CPU",
				0.63, // value
				0.5, // low Warning
				0.85, // medium warning
				0.95, // high warning
				null); // max
		return dp;
	}

	@Test
	public void deserialiseTest() {
		final Gson gson = new Gson();
		final Datapoint[] dps = gson.fromJson(getSerialisedDatapoints(),
				Datapoint[].class);

		assertEquals(dps.length, 3);
		System.out.println(getSerialisedDatapoints());
		for (final Datapoint dp : dps) {
			assertEquals(getTestDataPoint(), dp);
		}
	}

	private String getSerialisedDatapoints() {
		return "[{\"datapointType\":\"RELATIVE\",\"datastream\":\"CPU\",\"value\":0.63,\"lowWarnLevel\":0.5,\"mediumWarnLevel\":0.85,\"highWarnLevel\":0.95},{\"datapointType\":\"RELATIVE\",\"datastream\":\"CPU\",\"value\":0.63,\"lowWarnLevel\":0.5,\"mediumWarnLevel\":0.85,\"highWarnLevel\":0.95},{\"datapointType\":\"RELATIVE\",\"datastream\":\"CPU\",\"value\":0.63,\"lowWarnLevel\":0.5,\"mediumWarnLevel\":0.85,\"highWarnLevel\":0.95}]";
	}
}
