package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class PortailRepositoryDirectory implements IRepositoryObject {

	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private PortailRepositoryDirectory parent;
	private RepositoryDirectory directory;
	
	private List<IRepositoryObject> items;
	
	private boolean isChildLoad = false;

	public PortailRepositoryDirectory() {
		super();
	}

	public PortailRepositoryDirectory(String name) {
		this.name = name;
	}

	public PortailRepositoryDirectory(RepositoryDirectory directory) {
		this.directory = directory;
	}

	@Override
	public String getName() {
		return directory != null ? directory.getName() : name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setParent(PortailRepositoryDirectory parent) {
		this.parent = parent;
	}
	
	public PortailRepositoryDirectory getParent() {
		return parent;
	}

	public RepositoryDirectory getDirectory() {
		return directory;
	}

	public List<IRepositoryObject> getItems() {
		return items;
	}

	public void setItems(List<IRepositoryObject> items) {
		if(items != null) {
			for(IRepositoryObject item : items) {
				setParent(item);
			}
		}
		
		this.items = items;
	}

	public void addItem(IRepositoryObject item) {
		if(items == null) {
			this.items = new ArrayList<IRepositoryObject>();
		}
		
		setParent(item);
		
		this.items.add(item);
	}

	private void setParent(IRepositoryObject item) {
		if(item instanceof PortailRepositoryDirectory) {
			((PortailRepositoryDirectory) item).setParent(this);
		}
		else if(item instanceof PortailRepositoryItem) {
			((PortailRepositoryItem) item).setParent(this);
		}
	}

	public boolean isChildLoad() {
		return isChildLoad;
	}

	public void setChildLoad(boolean isChildLoad) {
		this.isChildLoad = isChildLoad;
	}

	public int getId() {
		return directory.getId();
	}

	public boolean hasChild() {
		return items != null && !items.isEmpty();
	}
}
