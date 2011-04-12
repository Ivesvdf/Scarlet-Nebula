package be.ac.ua.comp.scarletnebula.gui;

import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dasein.cloud.compute.VmState;

public class SearchHelper
{
	public static Collection<String> tokenize(String searchString)
	{
		Collection<String> tokens = new LinkedList<String>();

		String tmp = "";
		char openLiteral = 0;
		boolean prevWasBackslash = false;

		for (int pos = 0; pos < searchString.length(); pos++)
		{
			final char currentChar = searchString.charAt(pos);
			if (currentChar == '\\')
			{
				prevWasBackslash = true;
			}
			else if (currentChar == ' ' && openLiteral == 0
					&& !prevWasBackslash)
			{
				if (tmp.length() > 0)
					tokens.add(tmp);
				tmp = "";
				prevWasBackslash = false;
			}
			else if ((currentChar == '"' || currentChar == '\'')
					&& !prevWasBackslash)
			{
				// If we are not in a literal string, open one
				if (openLiteral == 0)
				{
					openLiteral = currentChar;
				}
				// If we are in a literal string, close it
				else
				{
					openLiteral = 0;
				}
				prevWasBackslash = false;
			}
			else
			{
				tmp += currentChar;
				prevWasBackslash = false;
			}
		}

		if (tmp.length() > 0)
			tokens.add(tmp);

		return tokens;
	}

	/**
	 * Tests a string "token" if it contains a prefix "prefix". For example:
	 * matchPrefix("tag", "tag:something") will return "something"
	 * matchPrefix("tag", "anything") will return null (no match)
	 * 
	 * @param prefix
	 *            Prefix that will be tested for
	 * @param token
	 *            Complete input string, without the possibility of a - in front
	 *            of it
	 * @return Everyting behind the : if a match is found, null otherwise
	 */
	public static String matchPrefix(String prefix, String token)
	{
		Pattern p = Pattern.compile("^" + prefix + ":(.*)$");
		Matcher matcher = p.matcher(token);
		final boolean matches = matcher.matches();
		return matches ? matcher.group(1) : null;
	}

	public static boolean matchTags(String term, Collection<String> tags,
			boolean negated)
	{
		boolean found = false;
		for (String tag : tags)
		{
			if (tag.equalsIgnoreCase(term))
			{
				found = true;
				break;
			}
		}

		return foundToMatch(found, negated);
	}

	public static boolean matchName(String term, String friendlyName,
			boolean negated)
	{
		return foundToMatch(friendlyName.contains(term), negated);
	}

	public static boolean matchSize(String term, String size, boolean negated)
	{
		return foundToMatch(size.equalsIgnoreCase(term), negated);
	}

	public static boolean matchStatus(String term, VmState status,
			boolean negated)
	{
		return foundToMatch(VmState.valueOf(term.toUpperCase()) == status,
				negated);
	}

	private static boolean foundToMatch(boolean found, boolean negated)
	{
		return !((negated && found) || (!negated && !found));
	}

	public static boolean matchCloudProvider(String term, String providerName,
			boolean negated)
	{
		return foundToMatch(
				providerName.toLowerCase().contains(term.toLowerCase()),
				negated);
	}

}
