package bpm.gwt.commons.shared.cmis;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CmisInformations implements IsSerializable {
	
	public static final String BINDING_BROWSER_URL = "org.apache.chemistry.opencmis.binding.browser.url";
	public static final String LOGIN = "org.apache.chemistry.opencmis.user";
	public static final String PASSWORD = "org.apache.chemistry.opencmis.password";

	private String login;
	private String password;
	private String url;
	
	private HashMap<String, String> properties;
	
	private boolean customProperties;
	
	private List<String> availableRepositories;
	private String selectedRepositoryId;
	
	public CmisInformations() {}
	
	public CmisInformations(HashMap<String, String> properties, boolean parseProperties) {
		if (parseProperties) {
			this.customProperties = false;
			this.login = properties.get(LOGIN);
			this.password = properties.get(PASSWORD);
			this.url = properties.get(BINDING_BROWSER_URL);
		}
		else {
			this.customProperties = true;
			this.properties= properties;
		}
	}
	
	public CmisInformations(String login, String password, String url) {
		this.customProperties = false;
		this.login = login;
		this.password = password;
		this.url = url;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getUrl() {
		return url;
	}
	
	public HashMap<String, String> getProperties() {
		return properties;
	}
	
	public boolean isCustomProperties() {
		return customProperties;
	}
	
	public List<String> getAvailableRepositories() {
		return availableRepositories;
	}
	
	public void setAvailableRepositories(List<String> availableRepositories) {
		this.availableRepositories = availableRepositories;
	}
	
	public String getSelectedRepositoryId() {
		return selectedRepositoryId;
	}
	
	public void setSelectedRepositoryId(String selectedRepositoryId) {
		this.selectedRepositoryId = selectedRepositoryId;
	}
}
