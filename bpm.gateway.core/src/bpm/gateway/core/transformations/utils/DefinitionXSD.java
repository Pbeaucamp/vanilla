package bpm.gateway.core.transformations.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.transformations.calcul.Script;

public class DefinitionXSD {

	public static final String NONE = "NONE";
	public static final String ITERABLE = "ITERABLE";
	public static final String EXCLUDE = "EXCLUDE";
	public static final String[] TYPES = {NONE, ITERABLE, EXCLUDE};

	private String name;
	private String elementPath;
	private String type;
	private Script columnId;
	private String outputName;
	
	protected DefinitionXSD parent;
	protected List<DefinitionXSD> childs;
	
	private boolean isAttribute;
	
//	protected List<AttributeXSD> attributes;

	public DefinitionXSD() { }

	public DefinitionXSD(DefinitionXSD parent, String name, String elementPath, boolean isAttribute) {
		this.parent = parent;
		this.name = name;
		this.elementPath = elementPath;
		this.type = NONE;
		this.isAttribute = isAttribute;
	}
	
	public String getName() {
		return name;
	}

	public String getElementPath() {
		return elementPath;
	}

	public void setElementPath(String elementPath) {
		this.elementPath = elementPath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Script getColumnId() {
		return columnId;
	}

	public void setColumnId(Script columnId) {
		this.columnId = columnId;
	}

	public String getOutputName() {
		return outputName != null ? outputName : "";
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public void setParent(DefinitionXSD parent) {
		this.parent = parent;
	}

	public DefinitionXSD getParent() {
		return parent;
	}

	public void setChilds(List<DefinitionXSD> childs) {
		this.childs = childs;
	}

	public void addChilds(DefinitionXSD child) {
		if(childs == null) {
			this.childs = new ArrayList<DefinitionXSD>();
		}
		this.childs.add(child);
	}
	
	public List<DefinitionXSD> getChilds() {
		return childs;
	}
	
	public boolean isAttribute() {
		return isAttribute;
	}

//	public void setAttributes(List<AttributeXSD> attributes) {
//		this.attributes = attributes;
//	}
//
//	public void addAttribute(AttributeXSD attribute) {
//		if(attributes == null) {
//			this.attributes = new ArrayList<AttributeXSD>();
//		}
//		this.attributes.add(attribute);
//	}
//	
//	public List<AttributeXSD> getAttributes() {
//		return attributes;
//	}
}
