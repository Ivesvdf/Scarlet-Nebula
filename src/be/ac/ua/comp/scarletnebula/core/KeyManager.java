package be.ac.ua.comp.scarletnebula.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KeyManager
{
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
				System.out.println("Cannot make key directory!");
				return;
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

	static String getKeyPath(String providerClassName)
	{
		return "keys/" + providerClassName + "/";
	}

	static String getKeyFilename(String providerClassName, String keyname)
	{
		return getKeyPath(providerClassName) + keyname + ".key";
	}
}
