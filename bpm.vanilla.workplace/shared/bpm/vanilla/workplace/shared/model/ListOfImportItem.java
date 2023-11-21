package bpm.vanilla.workplace.shared.model;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.workplace.core.model.PlaceImportItem;

import com.google.gwt.user.client.rpc.IsSerializable;


public class ListOfImportItem implements IsSerializable {

	private List<PlaceImportItem> importItems = new ArrayList<PlaceImportItem>();
	
	public ListOfImportItem() { 
		
	}

	public void setImportItems(List<PlaceImportItem> importItems) {
		this.importItems = importItems;
	}

	public List<PlaceImportItem> getImportItems() {
		return importItems;
	}
	
	public void addImportItem(PlaceImportItem item) {
		this.importItems.add(item);
	}
}
