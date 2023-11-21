package bpm.gwt.aklabox.commons.client.tree;

import java.util.List;

import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.Tree;
import bpm.gwt.aklabox.commons.shared.InfoUser;

public class ToolbarOptions {
	
	public enum TypeSearch {
		NONE,
		CLASSIC,
		MAIL
	}

	private ItemTreeType type;

	private Tree selectedFolder;
	private List<IObject> selectedItems;

	private Enterprise enterprise;
	private TypeSearch typeSearch;

	private boolean showDiaporama;

	public ToolbarOptions(ItemTreeType type, Tree selectedFolder, List<IObject> selectedItems, boolean showDiaporama, TypeSearch typeSearch, InfoUser infoUser) {
		buildOptions(type, selectedFolder, selectedItems, showDiaporama, typeSearch, infoUser, null);
	}

	public ToolbarOptions(ItemTreeType type, Tree selectedFolder, List<IObject> selectedItems, Enterprise enterprise, boolean showDiaporama, TypeSearch typeSearch, InfoUser infoUser) {
		buildOptions(type, selectedFolder, selectedItems, showDiaporama, typeSearch, infoUser, enterprise);
	}
	
	private void buildOptions(ItemTreeType type, Tree selectedFolder, List<IObject> selectedItems, boolean showDiaporama, TypeSearch typeSearch, InfoUser infoUser, Enterprise enterprise) {
		this.type = type;
		this.selectedFolder = selectedFolder;
		this.selectedItems = selectedItems;
		this.showDiaporama = showDiaporama;
		this.typeSearch = typeSearch;
		this.enterprise = enterprise;
	}

	public ItemTreeType getType() {
		return type;
	}

	public Tree getSelectedFolder() {
		return selectedFolder;
	}
	
	public List<IObject> getSelectedItems() {
		return selectedItems;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public boolean showDiaporama() {
		return showDiaporama;
	}

	public TypeSearch getTypeSearch() {
		return typeSearch;
	}

	public boolean hasMultipleItems() {
		return selectedItems != null && !selectedItems.isEmpty() && selectedItems.size() > 1;
	}

	public IObject getItem() {
		return selectedItems != null && !selectedItems.isEmpty() ? selectedItems.get(0) : null;
	}
}