package be.ac.ua.comp.scarletnebula.misc;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Utils
{
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

	public static String implode(List<String> input, String delimiter)
	{
		String out = "";
		for (int i = 0; i < input.size(); i++)
		{
			if (i != 0)
			{
				out += delimiter;
			}
			out += input.get(i);
		}
		return out;
	}

	public static Window findWindow(Component c)
	{
		if (c == null)
		{
			return JOptionPane.getRootFrame();
		}
		else if (c instanceof Window)
		{
			return (Window) c;
		}
		else
		{
			return findWindow(c.getParent());
		}
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
}
