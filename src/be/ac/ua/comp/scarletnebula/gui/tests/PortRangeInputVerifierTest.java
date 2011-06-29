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

import be.ac.ua.comp.scarletnebula.gui.inputverifiers.PortRangeInputVerifier;

public class PortRangeInputVerifierTest {
	@Test
	public void test() {
		good("22");
		good("110");
		good("65535");
		good("5-100");

		bad("65540");
		bad("a");
		bad("5-100-5");
		bad("10a");
	}

	private void good(final String input) {
		final PortRangeInputVerifier v = new PortRangeInputVerifier(null);
		final JTextField field = new JTextField(input);

		assertTrue(v.verify(field));
	}

	private void bad(final String input) {
		final PortRangeInputVerifier v = new PortRangeInputVerifier(null);
		final JTextField field = new JTextField(input);

		assertFalse(v.verify(field));
	}
}
