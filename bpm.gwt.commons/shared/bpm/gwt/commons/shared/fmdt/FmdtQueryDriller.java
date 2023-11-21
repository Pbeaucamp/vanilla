package bpm.gwt.commons.shared.fmdt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.chart.SavedChart;

public class FmdtQueryDriller implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id = 0;
	private String name;
	private String description;
	private String author;

	private int metadataId;
	private String connection;

	private String modelName;
	private String packageName;

	private int currentIndex;

	private List<String> groups = new ArrayList<String>();
	private boolean isGroup;

	private List<FmdtQueryBuilder> builders = new ArrayList<FmdtQueryBuilder>();
	private List<SavedChart> charts;

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

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
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

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public List<FmdtQueryBuilder> getBuilders() {
		return builders;
	}

	public void setBuilders(List<FmdtQueryBuilder> builders) {
		this.builders = builders;
	}

	public void addBuilders(FmdtQueryBuilder builder) {
		this.builders.add(builder);
	}

	public void removeBuilders(FmdtQueryBuilder builder) {
		this.builders.remove(builder);
	}

	public void setBuilder(FmdtQueryBuilder builder) {
		this.builders.clear();
		this.builders.add(builder);
	}

	public FmdtQueryBuilder getBuilder() {
		return this.builders.get(0);
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
	public List<SavedChart> getCharts() {
		return charts;
	}
	
	public void setCharts(List<SavedChart> charts) {
		this.charts = charts;
	}

	public boolean hasChart() {
		return charts != null && !charts.isEmpty();
	}
}
