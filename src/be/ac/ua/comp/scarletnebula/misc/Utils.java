package be.ac.ua.comp.scarletnebula.misc;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class Utils
{
	/**
	 * Copies the contents of a file to a different file
	 * 
	 * @param sourceFile
	 *            The source file
	 * @param destFile
	 *            The place the source file will be copied to
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File destFile)
			throws IOException
	{
		if (!destFile.exists())
		{
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try
		{
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally
		{
			if (source != null)
			{
				source.close();
			}
			if (destination != null)
			{
				destination.close();
			}
		}
	}

	/**
	 * Joins a list of strings by using delimiter as glue
	 * 
	 * @param pieces
	 *            The different pieces to be joined by glue
	 * @param glue
	 *            The glue used to join the different pieces
	 * @return A string containing a a string representation of all elements in
	 *         pieces in their respective orders, with a glue string between
	 *         each of them (but not before the first or after the last one).
	 */
	public static String implode(List<String> pieces, String glue)
	{
		String out = "";
		for (int i = 0; i < pieces.size(); i++)
		{
			if (i != 0)
			{
				out += glue;
			}
			out += pieces.get(i);
		}
		return out;
	}

	/**
	 * Returns the parent window of the component in parameter
	 * 
	 * @param c
	 *            The component for which the parent window (if any) will be
	 *            returned
	 * @return The parent window if one is found, null otherwise
	 */
	public static Window findWindow(Component c)
	{
		return (Window) SwingUtilities.getAncestorOfClass(Window.class, c);
	}

	/**
	 * Returns an icon in the images/ directory for the name you provide
	 * (including an extension). E.g. "stop.png" will return an Icon (actually
	 * an ImageIcon) containing a stop symbol.
	 * 
	 * @param name
	 *            Filename including extension excluding path
	 * @return The icon described by name
	 */
	public static Icon icon(String name)
	{
		return new ImageIcon(Utils.class.getResource("/images/" + name));
	}

	public static File internalFile(String name)
	{
		final URL url = Utils.class.getResource("/" + name);

		File f;
		try
		{
			f = new File(url.toURI());
		}
		catch (URISyntaxException e)
		{
			f = new File(url.getPath());
		}
		return f;
	}
}
