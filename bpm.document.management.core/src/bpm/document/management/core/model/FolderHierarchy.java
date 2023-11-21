package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FolderHierarchy implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	
	private String model;
	private List<FolderHierarchyItem> items;
	
	public FolderHierarchy() { }
	
	public FolderHierarchy(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<FolderHierarchyItem> getItems() {
		return items;
	}
	
	public void setItems(List<FolderHierarchyItem> items) {
		this.items = items;
	}
	
	public void addItem(FolderHierarchyItem item) {
		if (items == null) {
			this.items = new ArrayList<FolderHierarchyItem>();
		}
		item.setHierarchy(this);
		this.items.add(item);
	}
	
	public void removeItem(FolderHierarchyItem item) {
		this.items.remove(item);
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public String getModel() {
		return model;
	}
	
	@Override
	public String toString() {
		return name != null ? name : "";
	}
}
