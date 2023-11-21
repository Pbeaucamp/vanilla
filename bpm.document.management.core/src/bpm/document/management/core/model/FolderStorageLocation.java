package bpm.document.management.core.model;

import java.io.Serializable;

public class FolderStorageLocation implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int folderId;
	private String name;
	
	public FolderStorageLocation() {
		// TODO Auto-generated constructor stub
	}

	public FolderStorageLocation(int folderId, String name) {
		super();
		this.folderId = folderId;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
