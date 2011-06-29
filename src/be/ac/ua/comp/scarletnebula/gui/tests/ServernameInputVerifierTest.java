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

package be.ac.ua.comp.scarletnebula.gui.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JTextField;

import org.junit.Test;

import be.ac.ua.comp.scarletnebula.gui.inputverifiers.ServernameInputVerifier;

public class ServernameInputVerifierTest {
	@Test
	public void testEmptyNotAccepted() {
		assertTrue(test(" bla"));
		assertFalse(test(""));
	}

	@Test
	public void testValid() {
		assertTrue(test("foo bar"));
		assertFalse(test("foo#bar"));
		assertTrue(test("foo (bar-bar)"));
		assertTrue(test("foo (bar-22bar)"));

	}

	private boolean test(final String text) {
		final ServernameInputVerifier verifier = new ServernameInputVerifier(
				null, null);
		return verifier.verify(new JTextField(text));
	}
}
