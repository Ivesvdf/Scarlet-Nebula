package be.ac.ua.comp.scarletnebula.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KeyManager
{
	private static Log log = LogFactory.getLog(KeyManager.class);

	/**
	 * Takes a keyname, an actual key and a provider name (for which this key
	 * works) and then stores the key on disk.
	 * 
	 * @param providerClassName
	 * @param keyname
	 * @param keystring
	 */
	static void addKey(String providerClassName, String keyname,
			String keystring)
	{
		// Write key to file
		String dir = getKeyPath(providerClassName);
		File dirFile = new File(dir);

		// Check if the key dir already exists
		if (!dirFile.exists())
		{
			// If it does not exist, create the directory
			if (!dirFile.mkdirs())
			{
				log.error("Cannot make key directory!");
				System.out.println("Cannot make key directory!");
			}
		}

		// Now store the key to file
		BufferedWriter out;
		try
		{
			out = new BufferedWriter(new FileWriter(getKeyFilename(
					providerClassName, keyname)));
			out.write(keystring);
			out.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
