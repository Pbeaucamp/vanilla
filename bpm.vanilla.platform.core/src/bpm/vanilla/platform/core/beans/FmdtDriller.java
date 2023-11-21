package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FmdtDriller implements Serializable {

	private static final long serialVersionUID = 7800771074929698150L;
	
	private int id;
	private String name;
	private String description;
	private String author;
	
	private int metadataId;
	private String connection;

	private String tableName;
	private String modelName;
	private String packageName;
//	private Locale local;
	
	private String queryXml;

	private List<String> groups = new ArrayList<String>();
	private boolean isGroup;
	
	private List<FmdtDrillerFilter> filters = new ArrayList<FmdtDrillerFilter>();

	public FmdtDriller() {
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isGroup() {
		return isGroup;
	}

	public void setIsGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
	
	public void setIsGroup(String isGroup) {
		this.isGroup = Boolean.valueOf(isGroup);
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	public void addGroup(String group) {
		this.groups.add(group);
	}
	
	public int getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(int metadataId) {
		this.metadataId = metadataId;
	}
	
	public void setMetadataId(String metadataId) {
		this.metadataId = Integer.parseInt(metadataId);
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getQueryXml() {
		return queryXml;
	}

	public void setQueryXml(String queryXml) {
		this.queryXml = queryXml;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFilters(List<FmdtDrillerFilter> filters) {
		this.filters = filters;
	}

	public List<FmdtDrillerFilter> getFilters() {
		return filters;
	}
	
	public void addFilter(FmdtDrillerFilter filter) {
		this.filters.add(filter);
	}
/*
	public Locale getLocal() {
		return local;
	}

	public void setLocal(Locale local) {
		this.local = local;
	}
	*/
	
}
