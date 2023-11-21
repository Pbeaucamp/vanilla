package bpm.gwt.commons.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;

public class VanillaServerInformations implements IsSerializable {
	
	private String url;
	private String login;
	private String password;
	private int groupId;
	private int repoId;
	
	public VanillaServerInformations() {
	}
	
	public VanillaServerInformations(String url, String login, String password, int groupId, int repoId) {
		this.url = url;
		this.login = login;
		this.password = password;
		this.groupId = groupId;
		this.repoId = repoId;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public int getRepoId() {
		return repoId;
	}

	public Group getEmptyGroup() {
		Group group = new Group();
		group.setId(groupId);
		return group;
	}

	public Repository getEmptyRepo() {
		Repository repo = new Repository();
		repo.setId(repoId);
		return repo;
	}
}
