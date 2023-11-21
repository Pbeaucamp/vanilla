package bpm.vanilla.workplace.core.model;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.workplace.core.datasource.IDatasource;

public class PlaceImportItem {

	private int directoryId;
	private Integer newId;
	
	private String xml;
	private String path;

	private RepositoryItem item;

	private List<Integer> availableGroupsId = new ArrayList<Integer>();
	private List<Integer> runnableGroupsId = new ArrayList<Integer>();
	private List<Integer> needed = new ArrayList<Integer>();

	private List<IDatasource> datasources = new ArrayList<IDatasource>();
	
	private List<Dependency> dependencies = new ArrayList<Dependency>();
	
	private List<GedDocument> historics;

	private boolean isMainModel;

	public PlaceImportItem() {
	}
	
	public PlaceImportItem(RepositoryItem item) {
		this.item = item;
	}

	public PlaceImportItem(int directoryId, String path, RepositoryItem item) {
		this.setDirectoryId(directoryId);
		this.setPath(path);
		this.setItem(item);
	}

	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}

	public void setDirectoryId(String directoryId) {
		this.directoryId = Integer.parseInt(directoryId);
	}

	public int getDirectoryId() {
		return directoryId;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setAvailableGroupsId(List<Integer> availableGroupsId) {
		this.availableGroupsId = availableGroupsId;
	}

	public List<Integer> getAvailableGroupsId() {
		return availableGroupsId;
	}

	public void setRunnableGroupsId(List<Integer> runnableGroupsId) {
		this.runnableGroupsId = runnableGroupsId;
	}

	public List<Integer> getRunnableGroupsId() {
		return runnableGroupsId;
	}

	public void addAvailableGroupId(Integer groupId) {
		this.availableGroupsId.add(groupId);
	}

	public void addRunnableGroupId(Integer groupId) {
		this.runnableGroupsId.add(groupId);
	}

	public void removeAvailableGroupId(Integer groupId) {
		for (int i = 0; i < availableGroupsId.size(); i++) {
			if (availableGroupsId.get(i).equals(groupId)) {
				availableGroupsId.remove(i);
				break;
			}
		}
	}

	public void removeRunnableGroupId(Integer groupId) {
		for (int i = 0; i < runnableGroupsId.size(); i++) {
			if (runnableGroupsId.get(i).equals(groupId)) {
				runnableGroupsId.remove(i);
				break;
			}
		}
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void addNeeded(String need) {
		this.needed.add(Integer.parseInt(need));
	}

	public void setNeeded(List<Integer> needed) {
		this.needed = needed;
	}

	public List<Integer> getNeeded() {
		return needed;
	}

	public List<IDatasource> getDatasources() {
		return datasources;
	}

	public void setDatasources(List<IDatasource> datasources) {
		this.datasources = datasources;
	}

	public void removeAllAvailableGroupId() {
		this.availableGroupsId.clear();
	}

	public void removeAllRunnableGroupId() {
		this.runnableGroupsId.clear();
	}

	public boolean isMainModel() {
		return isMainModel;
	}

	public void setIsMainModel(boolean isMainModel) {
		this.isMainModel = isMainModel;
	}

	public void setItem(RepositoryItem item) {
		this.item = item;
	}

	public RepositoryItem getItem() {
		return item;
	}

	public void setHistoric(List<GedDocument> historic) {
		this.historics = historic;
	}

	public List<GedDocument> getHistoric() {
		return historics;
	}
	
	public void addHistoric(GedDocument doc) {
		if(historics == null) {
			historics = new ArrayList<GedDocument>();
 		}
		historics.add(doc);
	}

	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}
	
	public void addDependency(Dependency dep) {
		if(dependencies == null) {
			dependencies = new ArrayList<Dependency>();
		}
 		dependencies.add(dep);
	}

	public void setNewId(Integer newId) {
		this.newId = newId;
	}

	public Integer getNewId() {
		return newId;
	}
}
