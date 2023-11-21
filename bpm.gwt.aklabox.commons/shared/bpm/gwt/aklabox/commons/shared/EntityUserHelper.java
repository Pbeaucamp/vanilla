package bpm.gwt.aklabox.commons.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import bpm.document.management.core.model.User;

public class EntityUserHelper implements IsSerializable {

	private List<User> managers;
	private List<User> enterpriseValidators;
	private List<User> financeUsers;
	private List<User> readers;
	private List<User> mailing;
	private List<User> administrators;
	private List<User> signatories;
	private List<User> users;
	private List<User> defaultValidators;
	private List<User> defaultReaders;
	private List<User> archive;

	public EntityUserHelper() {
	}

	public List<User> getManagers() {
		return managers;
	}

	public void setManagers(List<User> managers) {
		this.managers = managers;
	}

	public List<User> getEnterpriseValidators() {
		return enterpriseValidators;
	}

	public void setEnterpriseValidators(List<User> enterpriseValidators) {
		this.enterpriseValidators = enterpriseValidators;
	}

	public List<User> getFinanceUsers() {
		return financeUsers;
	}

	public void setFinanceUsers(List<User> financeUsers) {
		this.financeUsers = financeUsers;
	}

	public List<User> getReaders() {
		return readers;
	}

	public void setReaders(List<User> readers) {
		this.readers = readers;
	}

	public List<User> getMailing() {
		return mailing;
	}

	public void setMailing(List<User> mailing) {
		this.mailing = mailing;
	}

	public List<User> getAdministrators() {
		return administrators;
	}

	public void setAdministrators(List<User> administrators) {
		this.administrators = administrators;
	}

	public List<User> getSignatories() {
		return signatories;
	}

	public void setSignatories(List<User> signatories) {
		this.signatories = signatories;
	}
	
	public List<User> getUsers() {
		return users;
	}
	
	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<User> getDefaultValidators() {
		return defaultValidators;
	}

	public void setDefaultValidators(List<User> defaultValidators) {
		this.defaultValidators = defaultValidators;
	}

	public List<User> getDefaultReaders() {
		return defaultReaders;
	}

	public void setDefaultReaders(List<User> defaultReaders) {
		this.defaultReaders = defaultReaders;
	}

	public List<User> getArchive() {
		return archive;
	}

	public void setArchive(List<User> archive) {
		this.archive = archive;
	}

}
