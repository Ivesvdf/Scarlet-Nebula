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
