package bpm.document.management.core.model;

import java.io.Serializable;

public class FolderHierarchyDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	private int docId;
	private String docName;
	
	private FolderHierarchy hierarchy;
	private FolderHierarchyItem parent;
	
	public FolderHierarchyDocument() { }
	
	public FolderHierarchyDocument(int docId, String docName) {
		this.docId = docId;
		this.docName = docName;
	}
	
	public int getDocId() {
		return docId;
	}
	
	public String getDocName() {
		return docName;
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
	
	@Override
	public String toString() {
		return docName != null ? docName : "";
	}
}
