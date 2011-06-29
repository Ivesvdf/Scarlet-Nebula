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

package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import be.ac.ua.comp.scarletnebula.core.CloudProviderTemplate;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;

public class AddProviderWizardDataRecorder implements DataRecorder {
	private CloudProviderTemplate template;
	private CloudProviderTemplate.Endpoint endpoint;
	private String apiKey;
	private String apiSecret;
	private String name;

	public CloudProviderTemplate getTemplate() {
		return template;
	}

	public void setTemplate(final CloudProviderTemplate template) {
		this.template = template;
	}

	public CloudProviderTemplate.Endpoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(final CloudProviderTemplate.Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(final String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiSecret() {
		return apiSecret;
	}

	public void setApiSecret(final String apiSecret) {
		this.apiSecret = apiSecret;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
