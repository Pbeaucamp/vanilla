package bpm.vanilla.platform.core.components.ged;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.ged.Category;

public class GedAdvancedSearchRuntimeConfig {

	private List<Integer> groupIds;
	private int userId;
	private int repositoryId;
	
	private Integer directoryId;
	private List<Integer> gedCategories;
	private String query;
	
	public GedAdvancedSearchRuntimeConfig() {
		super();
	}

	public GedAdvancedSearchRuntimeConfig(List<Integer> groupIds, int userId, Integer directoryId, List<Integer> gedCategories, String query, int repositoryId) {
		super();
		this.groupIds = groupIds;
		this.userId = userId;
		this.directoryId = directoryId;
		this.gedCategories = gedCategories;
		this.query = query;
		this.repositoryId = repositoryId;
	}

	public List<Integer> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<Integer> groupIds) {
		this.groupIds = groupIds;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Integer getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(Integer directoryId) {
		this.directoryId = directoryId;
	}

	public List<Integer> getGedCategories() {
		return gedCategories;
	}

	public void setGedCategories(List<Integer> gedCategories) {
		this.gedCategories = gedCategories;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public List<String> getGroupIdsAsStrings() {
		List<String> ids = new ArrayList<String>();
		for(Integer id : groupIds) {
			ids.add(String.valueOf(id));
		}
		return ids;
	}
	
	
}
