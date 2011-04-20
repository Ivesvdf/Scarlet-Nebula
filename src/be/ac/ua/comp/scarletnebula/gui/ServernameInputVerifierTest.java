package be.ac.ua.comp.scarletnebula.gui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JTextField;

import org.junit.Test;

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
	}

	private boolean test(String text)
	{
		final ServernameInputVerifier verifier = new ServernameInputVerifier(
				null);
		return verifier.verify(new JTextField(text));
	}
}
