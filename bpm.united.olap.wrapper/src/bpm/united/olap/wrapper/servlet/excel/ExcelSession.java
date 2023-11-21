package bpm.united.olap.wrapper.servlet.excel;

import java.io.Serializable;

import bpm.fa.api.olap.OLAPCube;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ExcelSession implements Serializable {

	private String user;
	private String password;
	private Group group;
	private Repository repository;
	private int fasdId;
	private String cubeName;
	
	private OLAPCube cube;
	
	private IRepositoryApi sock;
	private RepositoryItem fasdItem;

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

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public int getFasdId() {
		return fasdId;
	}

	public void setFasdId(int fasdId) {
		this.fasdId = fasdId;
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	public OLAPCube getCube() {
		return cube;
	}

	public void setCube(OLAPCube cube) {
		this.cube = cube;
	}

	public void setSock(IRepositoryApi sock) {
		this.sock = sock;
	}

	public IRepositoryApi getSock() {
		return sock;
	}

	public void setFasdItem(RepositoryItem item) {
		this.fasdItem = item;
	}
	
	public RepositoryItem getFasdItem() {
		return fasdItem;
	}
}
