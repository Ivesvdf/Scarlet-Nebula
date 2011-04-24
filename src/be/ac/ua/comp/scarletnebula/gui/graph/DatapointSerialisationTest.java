package be.ac.ua.comp.scarletnebula.gui.graph;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.Gson;

public class DatapointSerialisationTest
{
	@Test
	public void serialiseTest()
	{
		Gson gson = new Gson();
		Datapoint dp = getTestDataPoint();
		Datapoint[] dps = { dp, dp, dp };
		System.out.println(gson.toJson(dps));
		assertEquals(gson.toJson(dps), getSerialisedDatapoints());
	}

	private Datapoint getTestDataPoint()
	{
		Datapoint dp = new Datapoint(Datapoint.Type.RELATIVE, "CPU", 0.63, // value
				0.5, // low Warning
				0.85, // medium warning
				0.95, // high warning
				null); // max
		return dp;
	}

	@Test
	public void deserialiseTest()
	{
		Gson gson = new Gson();
		Datapoint[] dps = gson.fromJson(getSerialisedDatapoints(),
				Datapoint[].class);

		assertEquals(dps.length, 3);
		System.out.println(getSerialisedDatapoints());
		for (Datapoint dp : dps)
			assertEquals(getTestDataPoint(), dp);
	}

	private String getSerialisedDatapoints()
	{
		return "[{\"datapointType\":\"RELATIVE\",\"datastream\":\"CPU\",\"value\":0.63,\"lowWarnLevel\":0.5,\"mediumWarnLevel\":0.85,\"highWarnLevel\":0.95},{\"datapointType\":\"RELATIVE\",\"datastream\":\"CPU\",\"value\":0.63,\"lowWarnLevel\":0.5,\"mediumWarnLevel\":0.85,\"highWarnLevel\":0.95},{\"datapointType\":\"RELATIVE\",\"datastream\":\"CPU\",\"value\":0.63,\"lowWarnLevel\":0.5,\"mediumWarnLevel\":0.85,\"highWarnLevel\":0.95}]";
	}
}
