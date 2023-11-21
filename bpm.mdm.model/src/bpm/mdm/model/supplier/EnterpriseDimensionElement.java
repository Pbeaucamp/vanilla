package bpm.mdm.model.supplier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EnterpriseDimensionElement {
	
	private int id;
	private String name;
	private int parentId;
	private int parentElementId;
	private EnterpriseDimensionElement parentElement;
	private EnterpriseDimension parent;
	private Date creationDate;
	private Date endDate;
	private int version;
	
	private List<EnterpriseDimensionElement> children;
	
	public EnterpriseDimensionElement() {
		
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

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public EnterpriseDimension getParent() {
		return parent;
	}

	public void setParent(EnterpriseDimension parent) {
		this.parent = parent;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getParentElementId() {
		return parentElementId;
	}

	public void setParentElementId(int parentElementId) {
		this.parentElementId = parentElementId;
	}

	public EnterpriseDimensionElement getParentElement() {
		return parentElement;
	}

	public void setParentElement(EnterpriseDimensionElement parentElement) {
		this.parentElement = parentElement;
	}

	public void setChildren(List<EnterpriseDimensionElement> children) {
		this.children = children;
	}

	public List<EnterpriseDimensionElement> getChildren() {
		return children;
	}
	
	public void addChild(EnterpriseDimensionElement child) {
		if(children == null) {
			children = new ArrayList<EnterpriseDimensionElement>();
		}
		children.add(child);
	}
}
