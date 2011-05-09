package be.ac.ua.comp.scarletnebula.gui;

import java.util.ArrayList;
import java.util.Collection;

public class CloudProviderTemplate
{
	public class Endpoint
	{
		private final String url;
		private String name;
		private String shortname;

		public Endpoint(final String name, final String shortname,
				final String url)
		{
			this.name = name;
			this.shortname = shortname;
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

		public void setName(final String name)
		{
			this.name = name;
		}

		public void setShortName(final String shortname)
		{
			this.shortname = shortname;
		}
	}

	public static enum AccessMethod
	{
		KEY, EMAILPASSWD
	};

	private final String name;
	private final String classname;
	private final String shortname;
	private final AccessMethod accessMethod;

	Collection<Endpoint> endpoints = new ArrayList<Endpoint>();

	boolean allowCustomEndpoint = false;
	boolean usesAccessKey = true;

	public CloudProviderTemplate(final String name, final String shortname,
			final String classname, final AccessMethod accessMethod)
	{
		this.name = name;
		this.shortname = shortname;
		this.classname = classname;
		this.accessMethod = accessMethod;
	}

	public void addEndPoint(final String name, final String shortname,
			final String url)
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

	public void setAllowCustomEndpoint(final boolean val)
	{
		allowCustomEndpoint = val;
	}

	public void setUsesAccessKey(final boolean b)
	{
		usesAccessKey = b;
	}

	public String getShortName()
	{
		return shortname;
	}

	public AccessMethod getAccessMethod()
	{
		return accessMethod;
	}
}
