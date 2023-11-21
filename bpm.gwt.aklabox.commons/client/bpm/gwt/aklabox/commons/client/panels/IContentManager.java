package bpm.gwt.aklabox.commons.client.panels;

import java.util.List;

import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.MailSearch;
import bpm.document.management.core.model.SearchMetadata;
import bpm.document.management.core.model.SearchResult;
import bpm.document.management.core.model.Tree;

public interface IContentManager {

	public void loadItems(List<IObject> items, Tree folder, Enterprise enterprise, Group group, Integer selectedItemId);
	
	public void loadItemsSearch(SearchResult result, SearchMetadata searchMetadata, MailSearch mailSearch);

//	public void showGroupWorkspace(GroupSharedName groupShare);

	public void loadGroups(List<Group> groups);

//	public void loadGroupShares(List<GroupSharedName> groupShares);

	public void loadSearchResults(List<SearchResult> searchResults);

	public void loadEnterprises(List<Enterprise> enterprises);

	public void showSearchResult(SearchResult searchResult);

	public void clearInterface();
	
	public List<IObject> getSelectedItems();
}