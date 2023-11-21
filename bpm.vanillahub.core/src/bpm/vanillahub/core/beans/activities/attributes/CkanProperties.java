package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;

public class CkanProperties implements IOpenDataProperties, Serializable {

	private static final long serialVersionUID = 1L;

	private String organisation;
	private String apiKey;
	
	public CkanProperties() {
	}
	
	public CkanProperties(String organisation, String apiKey) {
		this.organisation = organisation;
		this.apiKey = apiKey;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}
