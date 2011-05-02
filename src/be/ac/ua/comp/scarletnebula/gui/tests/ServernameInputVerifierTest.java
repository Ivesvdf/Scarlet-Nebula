package be.ac.ua.comp.scarletnebula.gui.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JTextField;

import org.junit.Test;

import be.ac.ua.comp.scarletnebula.gui.inputverifiers.ServernameInputVerifier;

public class ServernameInputVerifierTest
{
	@Test
	public void testEmptyNotAccepted()
	{
		assertTrue(test(" bla"));
		assertFalse(test(""));
	}

	@Test
	public void testValid()
	{
		assertTrue(test("foo bar"));
		assertFalse(test("foo#bar"));
		assertTrue(test("foo (bar-bar)"));
		assertTrue(test("foo (bar-22bar)"));

	}

	private boolean test(final String text)
	{
		final ServernameInputVerifier verifier = new ServernameInputVerifier(
				null, null);
		return verifier.verify(new JTextField(text));
	}
}
