package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FolderHierarchyItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	
	private FolderHierarchy hierarchy;
	private FolderHierarchyItem parent;
	private List<FolderHierarchyItem> childs;
	private List<FolderHierarchyDocument> documents;
	
	public FolderHierarchyItem() { }
	
	public FolderHierarchyItem(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public FolderHierarchy getHierarchy() {
		return hierarchy;
	}
	
	public void setHierarchy(FolderHierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	public FolderHierarchyItem getParent() {
		return parent;
	}
	
	public void setParent(FolderHierarchyItem parent) {
		this.parent = parent;
	}
	
	public List<FolderHierarchyItem> getChilds() {
		return childs;
	}
	
	public void addChild(FolderHierarchyItem child) {
		if (childs == null) {
			this.childs = new ArrayList<FolderHierarchyItem>();
		}
		child.setHierarchy(getHierarchy());
		child.setParent(this);
		this.childs.add(child);
	}
	
	public void removeChild(FolderHierarchyItem child) {
		this.childs.remove(child);
	}
	
	
	
	public List<FolderHierarchyDocument> getDocuments() {
		return documents;
	}
	
	public void addDocument(FolderHierarchyDocument document) {
		if (documents == null) {
			this.documents = new ArrayList<FolderHierarchyDocument>();
		}
		document.setHierarchy(getHierarchy());
		document.setParent(this);
		this.documents.add(document);
	}
	
	public void removeDocument(FolderHierarchyDocument document) {
		this.documents.remove(document);
	}
	
	
	@Override
	public String toString() {
		return name != null ? name : "";
	}
}
