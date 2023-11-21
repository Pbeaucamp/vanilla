package bpm.vanilla.platform.core.wrapper.servlets40.helper;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class Session implements Serializable {

	private String user;
	private String password;
	private Group group;
	private Repository repository;
	
	private IRepositoryApi sock;
	
//	private List<IBusinessModel> models;
	private BusinessModel model;
	private BusinessPackage businessPackage;
	private RepositoryItem item;
	private Locale locale;
	
	
	public BusinessModel getModel() {
		return model;
	}

	public void setModel(BusinessModel model) {
		this.model = model;
	}

	public BusinessPackage getBusinessPackage() {
		return businessPackage;
	}

	public void setBusinessPackage(BusinessPackage businessPackage) {
		this.businessPackage = businessPackage;
	}

	public RepositoryItem getItem() {
		return item;
	}

	public void setItem(RepositoryItem item) {
		this.item = item;
	}

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


	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setSock(IRepositoryApi sock) {
		this.sock = sock;
	}

	public IRepositoryApi getSock() {
		return sock;
	}


}
