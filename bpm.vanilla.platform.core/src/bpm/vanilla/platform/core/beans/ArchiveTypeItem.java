package bpm.vanilla.platform.core.beans;

import java.io.Serializable;

import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ArchiveTypeItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	
	private int archiveTypeId;
	private int itemId;
	private int repositoryId;
	private boolean directory;
	
	private RepositoryItem item;
	private ArchiveType archiveType;

	public int getArchiveTypeId() {
		return archiveTypeId;
	}

	public void setArchiveTypeId(int archiveTypeId) {
		this.archiveTypeId = archiveTypeId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean isDirectory) {
		this.directory = isDirectory;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RepositoryItem getItem() {
		return item;
	}

	public void setItem(RepositoryItem item) {
		this.item = item;
	}

	public ArchiveType getArchiveType() {
		return archiveType;
	}

	public void setArchiveType(ArchiveType archiveType) {
		this.archiveType = archiveType;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

}
