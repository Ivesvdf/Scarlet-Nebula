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
	static void addKey(String providerName, String keyname, String keystring)
	{
		if (assureDirectory(providerName) == null)
			return;

		// Now store the key to file
		BufferedWriter out;
		try
		{
			out = new BufferedWriter(new FileWriter(getKeyFilename(
					providerName, keyname)));
			out.write(keystring);
			out.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static public Collection<String> getKeyNames(String providerName)
	{
		File dirFile = assureDirectory(providerName);

		Collection<String> keynames = new ArrayList<String>();

		for (String keyfile : dirFile.list())
		{
			keynames.add(keyfile.replaceAll("\\.key$", ""));
		}
		return keynames;
	}

	static private File assureDirectory(String providerName)
	{
		String dir = getKeyPath(providerName);
		File dirFile = new File(dir);

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

	static String getKeyPath(String providerName)
	{
		return "keys/" + providerName + "/";
	}

	static String getKeyFilename(String providerName, String keyname)
			throws FileNotFoundException
	{
		String filename = getKeyPath(providerName) + keyname + ".key";

		File f = new File(filename);
		if (!f.exists())
			throw new FileNotFoundException("No key by the name of " + keyname
					+ " found for " + providerName);

		return filename;
	}
}
