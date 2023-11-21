package bpm.fm.runtime.helper;

import java.util.List;

import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class Session {
	private String user;
	private String password;
	private Group group;
	private Observatory observatory;
	private Theme theme;
	private List<Observatory> currentListObs;
//	private Repository repository;
	
	private IRepositoryApi sock;	
	

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public void setSock(IRepositoryApi sock) {
		this.sock = sock;
	}

	public IRepositoryApi getSock() {
		return sock;
	}
/*	
	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
*/
	public Observatory getObservatory() {
		return observatory;
	}

	public void setObservatory(Observatory observatory) {
		this.observatory = observatory;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public List<Observatory> getCurrentListObs() {
		return currentListObs;
	}

	public void setCurrentListObs(List<Observatory> currentListObs) {
		this.currentListObs = currentListObs;
	}
	
	

}
