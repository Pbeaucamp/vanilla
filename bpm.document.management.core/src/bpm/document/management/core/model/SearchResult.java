package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SearchResult implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;
	private String keyword;
	private int totalResult;
	private Date searchDate = new Date();
	private int userId;
	private Integer folder;
	private int noSearches = 1;
	private Boolean personal = false;
	private Boolean myDocOnly = false;
	
	/**
	 * Use for the search saved in personal space
	 */
	private String name;
	
	/**
	 * This is filled on server side when we get back the searchResult.
	 * Only the folderId is available in the DB.
	 */
	//This folder is used to keep rights for a search and display toolbar icones accordingly
	private Tree folderSearch;
	private List<IObject> items;
	private List<SearchFacet> facets;
	private String folderName;
	
	public SearchResult() {	}

	public SearchResult(String keyword, Date searchDate, Tree folderSearch, List<IObject> items, List<SearchFacet> facets, int userId, Integer folderId) {
		super();
		this.keyword = keyword;
		this.searchDate = searchDate;
		this.folderSearch = folderSearch;
		this.userId = userId;
		this.items = items;
		this.facets = facets;
		this.totalResult = items != null ? items.size() : 0;
		this.folder = folderId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public Tree getFolderSearch() {
		return folderSearch;
	}
	
	public List<IObject> getItems() {
		return items;
	}
	
	public List<SearchFacet> getFacets() {
		return facets;
	}

	public int getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(int totalResult) {
		this.totalResult = totalResult;
	}

	public Date getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getNoSearches() {
		return noSearches;
	}

	public void setNoSearches(int noSearches) {
		this.noSearches = noSearches;
	}

	public Integer getFolder() {
		return folder;
	}

	public void setFolder(Integer folder) {
		this.folder = folder;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public Boolean isPersonal() {
		if (personal == null) {
			personal = false;
		}
		return personal;
	}

	public void setPersonal(Boolean personal) {
		this.personal = personal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getMyDocOnly() {
		return myDocOnly;
	}

	public void setMyDocOnly(Boolean myDocOnly) {
		this.myDocOnly = myDocOnly;
	}
	
	
}
