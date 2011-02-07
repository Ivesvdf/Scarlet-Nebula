package be.ac.ua.comp.scarletnebula.gui;

import java.util.ArrayList;
import java.util.Collection;

public class CloudProviderTemplate
{
	class Endpoint
	{
		String url;
		String name;

		public Endpoint(String name, String url)
		{
			this.name = name;
			this.url = url;
		}
	}

	String name;
	String classname;

	Collection<Endpoint> endpoints = new ArrayList<Endpoint>();

	boolean allowCustomEndpoint = false;
	boolean usesAccessKey = true;

	public CloudProviderTemplate(String name, String classname)
	{
		this.name = name;
		this.classname = classname;
	}

	public void addEndPoint(String name, String url)
	{
		endpoints.add(new Endpoint(name, url));
	}

	public Collection<Endpoint> getEndPoints()
	{
		return endpoints;
	}

	public void setAllowCustomEndpoint(boolean val)
	{
		allowCustomEndpoint = val;
	}

	public boolean getAllowCustomEndpoint()
	{
		return allowCustomEndpoint;
	}

	public void setUsesAccessKey(boolean b)
	{
		usesAccessKey = b;
	}

	public boolean getUsesAccessKey()
	{
		return usesAccessKey;
	}

	public String getName()
	{
		return name;
	}
}
