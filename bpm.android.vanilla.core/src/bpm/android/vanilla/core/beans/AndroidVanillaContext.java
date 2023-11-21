package bpm.android.vanilla.core.beans;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class AndroidVanillaContext implements Serializable {

	private String login;
	private String password;
	private String vanillaRuntimeUrl;
	private AndroidGroup group;
	private AndroidRepository repository;

	private List<AndroidGroup> availableGroups;
	private List<AndroidRepository> availableRepositories;

	public AndroidVanillaContext() {
	}

	public void setGroup(AndroidGroup group) {
		this.group = group;
	}

	public AndroidGroup getGroup() {
		return group;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setRepository(AndroidRepository repository) {
		this.repository = repository;
	}

	public AndroidRepository getRepository() {
		return repository;
	}

	public void setVanillaRuntimeUrl(String vanillaRuntimeUrl) {
		this.vanillaRuntimeUrl = vanillaRuntimeUrl;
	}

	public String getVanillaRuntimeUrl() {
		return vanillaRuntimeUrl;
	}

	public void setAvailableGroups(List<AndroidGroup> availableGroups) {
		this.availableGroups = availableGroups;
	}

	public List<AndroidGroup> getAvailableGroups() {
		return availableGroups;
	}

	public void setAvailableRepositories(List<AndroidRepository> availableRepositories) {
		this.availableRepositories = availableRepositories;
	}

	public List<AndroidRepository> getAvailableRepositories() {
		return availableRepositories;
	}

}
