package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrganigramElement implements Serializable {

	private int id;
	private String name = "Element";
	private boolean root;

	private String description;
	
	private int directoryId;
	private int parentId;

	private List<OrganigramElement> children;
	private List<OrganigramElementSecurity> securities;
	private Tree directory;

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

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<OrganigramElementSecurity> getSecurities() {
		if(securities == null) {
			securities = new ArrayList<OrganigramElementSecurity>();
		}
		return securities;
	}

	public void setSecurities(List<OrganigramElementSecurity> securities) {
		if(securities == null) {
			securities = new ArrayList<OrganigramElementSecurity>();
		}
		this.securities = new ArrayList<OrganigramElementSecurity>(securities);
	}

	public Tree getDirectory() {
		return directory;
	}

	public void setDirectory(Tree directory) {
		this.directory = directory;
	}

	public List<OrganigramElement> getChildren() {
		if(children == null) {
			children = new ArrayList<OrganigramElement>();
		}
		return children;
	}

	public void setChildren(List<OrganigramElement> children) {
		if(children == null) {
			children = new ArrayList<OrganigramElement>();
		}
		this.children = new ArrayList<OrganigramElement>(children);
	}

	public int getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

}
