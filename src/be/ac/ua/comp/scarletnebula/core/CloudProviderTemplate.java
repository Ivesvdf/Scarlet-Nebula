/*
 * Copyright (C) 2011  Ives van der Flaas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package be.ac.ua.comp.scarletnebula.core;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class that descirbes a template for a cloudprovider.
 * 
 * @author ives
 * 
 */
public class CloudProviderTemplate {
	/**
	 * Class that describes and endpoint.
	 * 
	 * @author ives
	 * 
	 */
	public class Endpoint {
		private final String url;
		private final String name;
		private final String shortname;

		/**
		 * Ctor.
		 * 
		 * @param name
		 *            Name for the endpoint
		 * @param shortname
		 *            Short name for the endpoint
		 * @param url
		 *            URL to connect to that describes this endpoint.
		 */
		public Endpoint(final String name, final String shortname,
				final String url) {
			this.name = name;
			this.shortname = shortname;
			this.url = url;
		}

		/**
		 * @return The name of this endpoint
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return The short name of this endpoint
		 */
		public String getShortName() {
			return shortname;
		}

		/**
		 * @return The url for this CloudProviderTemplate.
		 */
		public String getURL() {
			return url;
		}
	}

	/**
	 * Enumeration with possibilities for login methods.
	 * 
	 * @author ives
	 * 
	 */
	public static enum AccessMethod {
		KEY, EMAILPASSWD
	};

	private final String name;
	private final String classname;
	private final String shortname;
	private final AccessMethod accessMethod;

	private final Collection<Endpoint> endpoints = new ArrayList<Endpoint>();

	private boolean allowCustomEndpoint = false;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            Template name
	 * @param shortname
	 *            Short template name
	 * @param classname
	 *            Dasein class name
	 * @param accessMethod
	 *            The acces method for this cloudprovider.
	 */
	public CloudProviderTemplate(final String name, final String shortname,
			final String classname, final AccessMethod accessMethod) {
		this.name = name;
		this.shortname = shortname;
		this.classname = classname;
		this.accessMethod = accessMethod;
	}

	/**
	 * Adds an endpoint for this CloudProviderTemplate.
	 * 
	 * @param name
	 *            The name of the endpoint to add.
	 * @param shortname
	 *            The short name of the endpoint to add.
	 * @param url
	 *            The url of the endpoint to add.
	 */
	public void addEndPoint(final String name, final String shortname,
			final String url) {
		endpoints.add(new Endpoint(name, shortname, url));
	}

	/**
	 * @return True if a custom endpoint is allowed.
	 */
	public boolean getAllowCustomEndpoint() {
		return allowCustomEndpoint;
	}

	/**
	 * @return The dasein classname.
	 */
	public String getClassname() {
		return classname;
	}

	/**
	 * @return The collection of endpoints for this CloudProviderTemplate.
	 */
	public Collection<Endpoint> getEndPoints() {
		return endpoints;
	}

	/**
	 * @return The name of this CloudProviderTemplate.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param newEndpoint
	 *            True if a custom endpoint is allowed, false otherwise.
	 */
	public void setAllowCustomEndpoint(final boolean newEndpoint) {
		allowCustomEndpoint = newEndpoint;
	}

	/**
	 * @return The short name for this CloudProviderTemplate.
	 */
	public String getShortName() {
		return shortname;
	}

	/**
	 * @return The access method for this CloudProviderTemplate.
	 */
	public AccessMethod getAccessMethod() {
		return accessMethod;
	}
}
