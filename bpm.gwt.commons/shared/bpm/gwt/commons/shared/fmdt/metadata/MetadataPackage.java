package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import bpm.vanilla.platform.core.beans.data.DatabaseTable;

public class MetadataPackage implements IsSerializable {
	
	private String name;
	private String description;

	private List<DatabaseTable> tables;
	private MetadataResources resources;
	private MetadataQueries queries;
	private MetadataModel parent;
	
	public MetadataPackage() { }
	
	public MetadataPackage(String name, String description, List<DatabaseTable> tables, MetadataResources resources, MetadataQueries queries) {
		this.name = name;
		this.description = description;
		this.tables = tables;
		setResources(resources);
		setQueries(queries);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description != null ? description : "";
	}
	
	public List<DatabaseTable> getTables() {
		return tables;
	}
	
	public void setTables(List<DatabaseTable> tables) {
		this.tables = tables;
	}
	
	public MetadataResources getResources() {
		return resources;
	}
	
	public void setResources(MetadataResources resources) {
		resources.setParent(this);
		this.resources = resources;
	}
	
	public MetadataQueries getQueries() {
		return queries;
	}
	
	public void setQueries(MetadataQueries queries) {
		if (queries != null) {
			queries.setParent(this);
		}
		this.queries = queries;
	}

	public void updateResources(List<MetadataResource> resourcesToRemove, List<MetadataResource> resourcesToAdd) {
		if (resources != null) {
			resources.updateResources(resourcesToRemove, resourcesToAdd);
		}
	}
	
	public MetadataModel getParent() {
		return parent;
	}
	
	public void setParent(MetadataModel parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
