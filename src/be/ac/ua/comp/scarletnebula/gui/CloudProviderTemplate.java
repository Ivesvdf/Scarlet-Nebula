package be.ac.ua.comp.scarletnebula.gui;

import java.util.ArrayList;
import java.util.Collection;

public class CloudProviderTemplate
{
	public class Endpoint
	{
		private String url;
		private String name;
		private String shortname;

		public Endpoint(String name, String shortname, String url)
		{
			this.setName(name);
			this.setShortName(shortname);
			this.url = url;
		}

		public String getName()
		{
			return name;
		}

		public String getShortName()
		{
			return shortname;
		}

		public String getURL()
		{
			return url;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		private void setShortName(String shortname)
		{
			this.shortname = shortname;
		}
	}

	private String name;
	private String classname;
	private String shortname;

	Collection<Endpoint> endpoints = new ArrayList<Endpoint>();

	boolean allowCustomEndpoint = false;
	boolean usesAccessKey = true;

	public CloudProviderTemplate(String name, String shortname, String classname)
	{
		this.name = name;
		this.shortname = shortname;
		this.classname = classname;
	}

	public void addEndPoint(String name, String shortname, String url)
	{
		endpoints.add(new Endpoint(name, shortname, url));
	}

	public boolean getAllowCustomEndpoint()
	{
		return allowCustomEndpoint;
	}

	public String getClassname()
	{
		return classname;
	}

	public Collection<Endpoint> getEndPoints()
	{
		return endpoints;
	}

	public String getName()
	{
		return name;
	}

	public boolean getUsesAccessKey()
	{
		return usesAccessKey;
	}

	public void setAllowCustomEndpoint(boolean val)
	{
		allowCustomEndpoint = val;
	}

	public void setUsesAccessKey(boolean b)
	{
		usesAccessKey = b;
	}

	public String getShortName()
	{
		return shortname;
	}
}
