package be.ac.ua.comp.scarletnebula.gui.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JTextField;

import org.junit.Test;

import be.ac.ua.comp.scarletnebula.gui.inputverifiers.PortRangeInputVerifier;

public class PortRangeInputVerifierTest
{
	@Test
	public void test()
	{
		good("22");
		good("110");
		good("65535");
		good("5-100");

		bad("65540");
		bad("a");
		bad("5-100-5");
		bad("10a");
	}

	private void good(final String input)
	{
		final PortRangeInputVerifier v = new PortRangeInputVerifier(null);
		final JTextField field = new JTextField(input);

		assertTrue(v.verify(field));
	}

	private void bad(final String input)
	{
		final PortRangeInputVerifier v = new PortRangeInputVerifier(null);
		final JTextField field = new JTextField(input);

		assertFalse(v.verify(field));
	}
}
