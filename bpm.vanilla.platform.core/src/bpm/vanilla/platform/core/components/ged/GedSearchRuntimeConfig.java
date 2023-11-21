package bpm.vanilla.platform.core.components.ged;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.ged.IComProperties;


public class GedSearchRuntimeConfig {

	/**
	 * matches to field definitions names
	 */
	private IComProperties properties;
	
	/**
	 * 
	 */
	private boolean allOccurences;
	
	/**
	 * list of the assembled keywords
	 */
	private List<String> keywords;
	
	private List<Integer> groupIds = new ArrayList<Integer>();
	private int repositoryId;
	
	public GedSearchRuntimeConfig() {
	}
	
	/**
	 * @return the repositoryId
	 */
	public int getRepositoryId() {
		return repositoryId;
	}

	/**
	 * @param repositoryId the repositoryId to set
	 */
	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public IComProperties getProperties() {
		return properties;
	}

	public void setProperties(IComProperties properties) {
		this.properties = properties;
	}

	public boolean isAllOccurences() {
		return allOccurences;
	}

	public void setAllOccurences(boolean allOccurences) {
		this.allOccurences = allOccurences;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public List<Integer> getGroupId() {
		return groupIds;
	}

	public void addGroupId(int grId) {
		groupIds.add(grId);
	}
//	public void setGroupId(String groupId) {
//		this.groupId = groupId;
//	}
	
	
}
