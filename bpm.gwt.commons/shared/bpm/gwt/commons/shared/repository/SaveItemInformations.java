package bpm.gwt.commons.shared.repository;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class SaveItemInformations implements IsSerializable {

	private String name;
	private String description;
	private List<Group> groups;
	
	private int repositoryType;
	private int repositorySubtype = -1;
	private RepositoryDirectory selectedDirectory;
	
	private Serializable item;
	
	public SaveItemInformations() {
	}
	
	public SaveItemInformations(String name, String description, List<Group> groups, int repositoryType, int repositorySubtype, RepositoryDirectory selectedDirectory, Serializable item) { 
		this.name = name;
		this.description = description;
		this.groups = groups;
		this.repositoryType = repositoryType;
		this.repositorySubtype = repositorySubtype;
		this.selectedDirectory = selectedDirectory;
		this.item = item;
	}
	
	public SaveItemInformations(String name, String description, List<Group> groups, int repositoryType, RepositoryDirectory selectedDirectory, Serializable item) { 
		this.name = name;
		this.description = description;
		this.groups = groups;
		this.repositoryType = repositoryType;
		this.selectedDirectory = selectedDirectory;
		this.item = item;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<Group> getGroups() {
		return groups;
	}
	
	public int getRepositoryType() {
		return repositoryType;
	}
	
	public void setRepositorySubtype(int repositorySubtype) {
		this.repositorySubtype = repositorySubtype;
	}
	
	public int getRepositorySubtype() {
		return repositorySubtype;
	}
	
	public RepositoryDirectory getSelectedDirectory() {
		return selectedDirectory;
	}
	
	public Object getItem() {
		return item;
	}
	
}
