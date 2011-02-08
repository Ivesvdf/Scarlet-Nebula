package be.ac.ua.comp.scarletnebula.gui.addproviderwizard;

import be.ac.ua.comp.scarletnebula.gui.CloudProviderTemplate;
import be.ac.ua.comp.scarletnebula.wizard.DataRecorder;

public class AddProviderWizardDataRecorder implements DataRecorder
{
	private CloudProviderTemplate template;
	private CloudProviderTemplate.Endpoint endpoint;
	private String apiKey;
	private String apiSecret;
	private String name;

	public CloudProviderTemplate getTemplate()
	{
		return template;
	}

	public void setTemplate(CloudProviderTemplate template)
	{
		this.template = template;
	}

	public CloudProviderTemplate.Endpoint getEndpoint()
	{
		return endpoint;
	}

	public void setEndpoint(CloudProviderTemplate.Endpoint endpoint)
	{
		this.endpoint = endpoint;
	}

	public String getApiKey()
	{
		return apiKey;
	}

	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}

	public String getApiSecret()
	{
		return apiSecret;
	}

	public void setApiSecret(String apiSecret)
	{
		this.apiSecret = apiSecret;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

}
