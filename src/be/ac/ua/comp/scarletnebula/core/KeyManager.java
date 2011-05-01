package be.ac.ua.comp.scarletnebula.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ua.comp.scarletnebula.misc.Utils;

public class KeyManager
{
	private static Log log = LogFactory.getLog(KeyManager.class);

	/**
	 * Takes a keyname, an actual key and a provider name (for which this key
	 * works) and then stores the key on disk.
	 * 
	 * @param providerName
	 * @param keyname
	 * @param keystring
	 */
	static void addKey(final String providerName, final String keyname, final String keystring)
	{
		if (assureDirectory(providerName) == null)
		{
			return;
		}

		// Now store the key to file
		BufferedWriter out;
		try
		{
			out = new BufferedWriter(new FileWriter(getKeyFilename(
					providerName, keyname)));
			out.write(keystring);
			out.close();
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new key from the contents of the key in a string.
	 * 
	 * @param providerName
	 *            Name of the provider
	 * @param keyname
	 *            Name of the key
	 * @param key
	 *            Contents of the key
	 */
	public static void addKey(final String providerName, final String keyname, final File key)
	{

		if (assureDirectory(providerName) == null)
		{
			return;
		}

		try
		{
			Utils.copyFile(key, new File(getKeyFilename(providerName, keyname)));
		}
		catch (final FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	static public Collection<String> getKeyNames(final String providerName)
	{
		final File dirFile = assureDirectory(providerName);

		final Collection<String> keynames = new ArrayList<String>();

		for (final String keyfile : dirFile.list())
		{
			keynames.add(keyfile.replaceAll("\\.key$", ""));
		}
		return keynames;
	}

	static private File assureDirectory(final String providerName)
	{
		final String dir = getKeyPath(providerName);
		final File dirFile = new File(dir);

		// Check if the key dir already exists
		if (!dirFile.exists())
		{
			// If it does not exist, create the directory
			if (!dirFile.mkdirs())
			{
				log.error("Cannot make key directory!");
			}
		}

		return dirFile;
	}

	static String getKeyPath(final String providerName)
	{
		return "keys/" + providerName + "/";
	}

	static String getKeyFilename(final String providerName, final String keyname)
	{
		final String filename = getKeyPath(providerName) + keyname + ".key";

		return filename;
	}

	public static void deleteKey(final String providerName, final String keyname)
	{
		final File keyFile = new File(getKeyFilename(providerName, keyname));

		if (keyFile.exists())
		{
			keyFile.delete();
		}
	}
}
