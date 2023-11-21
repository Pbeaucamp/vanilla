package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private String mail;

	private String searchQuery;
	private List<String> facets;

	private Integer folderId;
	private boolean myDocOnly;
	private SearchCriteria crit;

	private boolean saveSearchResult;

	// Advance Search Part
	private boolean isAdvanceSearch;
	private List<String> filterExtensions;
	private String filterCreationDate;
	private int sortByResult;
	private int sortByTime;
	
	private Date dateBeginDua;
	private Date dateEndDua;
	
	public SearchMetadata() { }
	
	public SearchMetadata(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	public SearchMetadata(String mail, String searchQuery, Integer folderId, boolean myDocOnly, SearchCriteria crit, boolean saveSearchResult, boolean isAdvanceSearch,	
			List<String> filterExtensions, String filterCreationDate, int sortByResult, int sortByTime, Date dateBeginDua, Date dateEndDua) {
		this.mail = mail;
		this.searchQuery = searchQuery;
		this.folderId = folderId;
		this.myDocOnly = myDocOnly;
		this.crit = crit;
		this.saveSearchResult = saveSearchResult;
		this.isAdvanceSearch = isAdvanceSearch;
		this.filterExtensions = filterExtensions;
		this.filterCreationDate = filterCreationDate;
		this.sortByResult = sortByResult;
		this.sortByTime = sortByTime;
		this.dateBeginDua = dateBeginDua;
		this.dateEndDua = dateEndDua;
	}

	public String getMail() {
		return mail;
	}

	public String getSearchQuery() {
		return searchQuery;
	}
	
	public List<String> getFacets() {
		return facets;
	}
	
	public void clearFacets() {
		if (facets != null) {
			facets.clear();
		}
	}
	
	public void addFacet(String facet) {
		if (facets == null) {
			this.facets = new ArrayList<String>();
		}
		this.facets.add(facet);
	}

	public Integer getFolderId() {
		return folderId;
	}
	
	public boolean isMyDocOnly() {
		return myDocOnly;
	}

	public SearchCriteria getCrit() {
		return crit;
	}

	public boolean isSaveSearchResult() {
		return saveSearchResult;
	}
	
	public boolean isAdvanceSearch() {
		return isAdvanceSearch;
	}

	public List<String> getFilterExtensions() {
		return filterExtensions;
	}
	
	public String getFilterCreationDate() {
		return filterCreationDate;
	}
	
	public int getSortByResult() {
		return sortByResult;
	}
	
	public int getSortByTime() {
		return sortByTime;
	}
	
	public Date getDateBeginDua() {
		return dateBeginDua;
	}
	
	public Date getDateEndDua() {
		return dateEndDua;
	}
}
