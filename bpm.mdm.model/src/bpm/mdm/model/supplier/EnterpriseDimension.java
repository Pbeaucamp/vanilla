package bpm.mdm.model.supplier;

import java.util.ArrayList;
import java.util.List;

public class EnterpriseDimension {

	private int id;
	private String name;
	
	private String externalId;
	private String externalSource;
	
	private EnterpriseDimension parent;
	private List<EnterpriseDimension> children;
	
	private List<EnterpriseDimensionElement> elements;
	
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
	
	public String getExternalId() {
		return externalId;
	}
	
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	public String getExternalSource() {
		return externalSource;
	}
	
	public void setExternalSource(String externalSource) {
		this.externalSource = externalSource;
	}

	public EnterpriseDimension getParent() {
		return parent;
	}

	public void setParent(EnterpriseDimension parent) {
		this.parent = parent;
	}

	public List<EnterpriseDimension> getChildren() {
		return children;
	}

	public void setChildren(List<EnterpriseDimension> children) {
		this.children = children;
	}
	
	public void addChild(EnterpriseDimension child) {
		if(children == null) {
			children = new ArrayList<EnterpriseDimension>();
		}
		children.add(child);
	}

	public void setElements(List<EnterpriseDimensionElement> elements) {
		this.elements = elements;
	}

	public List<EnterpriseDimensionElement> getElements() {
		return elements;
	}
	
	public void addElement(EnterpriseDimensionElement element) {
		if(elements == null) {
			elements = new ArrayList<EnterpriseDimensionElement>();
		}
		elements.add(element);
	}
}
