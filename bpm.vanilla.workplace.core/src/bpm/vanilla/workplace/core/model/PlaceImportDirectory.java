package bpm.vanilla.workplace.core.model;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class PlaceImportDirectory {

	private RepositoryDirectory repositoryDirectory;
	
	public PlaceImportDirectory parent;
	
	public List<PlaceImportDirectory> childsDir = new ArrayList<PlaceImportDirectory>();
	public List<PlaceImportItem> childsItems = new ArrayList<PlaceImportItem>();
	
	public String path;
	
	private List<Integer> availableGroupsId = new ArrayList<Integer>();
	
	public PlaceImportDirectory() {}
	
	public PlaceImportDirectory(String name){
		RepositoryDirectory rep = new RepositoryDirectory();
		rep.setName(name);
		this.repositoryDirectory = rep;
	}
	
	public PlaceImportDirectory(RepositoryDirectory repositoryDirectory) {
		this.repositoryDirectory = repositoryDirectory;
	}
	
	public String getName() {
		return repositoryDirectory.getName();
	}
	
	public PlaceImportDirectory getParent() {
		return parent;
	}

	public void setParent(PlaceImportDirectory parent) {
		this.parent = parent;
	}

	
	public List<PlaceImportDirectory> getChildsDir() {
		return childsDir;
	}

	
	public void addChildDir(PlaceImportDirectory childDir) {
		childDir.setParent(this);
		this.childsDir.add(childDir);
	}
	
	public void setChildsDir(List<PlaceImportDirectory> childsDir) {
		this.childsDir = childsDir;
	}

	
	public List<PlaceImportItem> getChildsItems() {
		return childsItems;
	}

	
	public void addChildsItem(PlaceImportItem childsItem) {
		childsItem.setDirectoryId(this.getRepositoryDirectory().getId());
		this.childsItems.add(childsItem);
	}
	
	public void setChildsItems(List<PlaceImportItem> childsItems) {
		this.childsItems = childsItems;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setAvailableGroupsId(List<Integer> availableGroupsId) {
		this.availableGroupsId = availableGroupsId;
	}

	
	public List<Integer> getAvailableGroupsId() {
		return availableGroupsId;
	}

	
	public void addAvailableGroupId(Integer groupId) {
		this.availableGroupsId.add(groupId);
	}

	
	public void removeAvailableGroupId(Integer groupId) {
		for(int i=0; i<availableGroupsId.size(); i++){
			if(availableGroupsId.get(i).equals(groupId)){
				availableGroupsId.remove(i);
				break;
			}
		}
	}

	
	public void removeAllAvailableGroupId() {
		this.availableGroupsId.clear();
	}

	public RepositoryDirectory getRepositoryDirectory() {
		return repositoryDirectory;
	}

	public void setRepositoryDirectory(RepositoryDirectory repositoryDirectory) {
		this.repositoryDirectory = repositoryDirectory;
	}
}
