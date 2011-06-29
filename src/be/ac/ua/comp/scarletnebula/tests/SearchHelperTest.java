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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.dasein.cloud.compute.VmState;
import org.junit.Test;

import be.ac.ua.comp.scarletnebula.misc.SearchHelper;

public class SearchHelperTest {
	@Test
	public void testTokenize() {
		testTokenizerString("foo", "foo");
		testTokenizerString("foo bar", "foo", "bar");
		testTokenizerString("tag:dns bar", "tag:dns", "bar");
		testTokenizerString("some more text", "some", "more", "text");
		testTokenizerString(" ");
		testTokenizerString("name:\"my server\" tag:web", "name:my server",
				"tag:web");
		testTokenizerString("tag:'txt' bar", "tag:txt", "bar");
		testTokenizerString("tag:web\\ server", "tag:web server");
		testTokenizerString(" \" ", " ");
		testTokenizerString("tag:\\\" bar", "tag:\"", "bar");
		testTokenizerString("tag:\"the \\\" way it is\"",
				"tag:the \" way it is");
	}

	public void testTokenizerString(final String input, final String... result) {
		final Collection<String> resultCollection = Arrays.asList(result);
		final Collection<String> tokenizedCollection = SearchHelper
				.tokenize(input);

		assertEquals(resultCollection, tokenizedCollection);
	}

	@Test
	public void testMatchPrefix() {
		testMatcherString("tag", "foo:something", null);
		testMatcherString("tag", "tag:something", "something");
		testMatcherString("name", "name:a long name", "a long name");
	}

	private void testMatcherString(final String prefix, final String token,
			final String supposedResult) {
		assertEquals(supposedResult, SearchHelper.matchPrefix(prefix, token));
	}

	@Test
	public void testMatchName() {
		assertTrue(SearchHelper.matchName("bla", "bla", false));
		assertTrue(SearchHelper.matchName("bla", "sblab", false));
		assertFalse(SearchHelper.matchName("bla", "sblab", true));
		assertTrue(SearchHelper.matchName("bla", "foo", true));
	}

	@Test
	public void testMatchStatus() {
		assertTrue(SearchHelper.matchStatus("running", VmState.RUNNING, false));
		assertTrue(SearchHelper.matchStatus("PAUSED", VmState.PAUSED, false));
		assertTrue(SearchHelper.matchStatus("PAUSED", VmState.RUNNING, true));
	}

	@Test
	public void testMatchTags() {
		final Collection<String> testTags = new ArrayList<String>();
		testTags.add("dns");
		testTags.add("ftp");
		assertTrue(SearchHelper.matchTags("dns", testTags, false));
		assertTrue(SearchHelper.matchTags("FTP", testTags, false));
		assertFalse(SearchHelper.matchTags("FTP", testTags, true));
		assertFalse(SearchHelper.matchTags("foo", testTags, false));
	}

	@Test
	public void testMathCloudProvider() {
		assertTrue(SearchHelper.matchCloudProvider("amazon", "Amazon EC2 (EU)",
				false));
		assertFalse(SearchHelper.matchCloudProvider("rackspace",
				"Amazon EC2 (EU)", false));
		assertTrue(SearchHelper.matchCloudProvider("EC2", "Amazon EC2 (EU)",
				false));
		assertFalse(SearchHelper.matchCloudProvider("EC2", "Amazon EC2 (EU)",
				true));
	}
}
