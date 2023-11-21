package bpm.gateway.core.transformations.olap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class FilterClause {
	private DimensionFilter parent;
	private String levelName;
	private String value;
	
	public FilterClause(){}
	public FilterClause(String levelName, DimensionFilter parent){
		setLevelName(levelName);
		setParent(parent);
	}
	
	
	public void setParent(DimensionFilter parent){
		this.parent = parent;
	}
	public DimensionFilter getParent() {
		return parent;
	}
	
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("olapFilterClause");
		e.addElement("levelName").setText(getLevelName());
		
		if (getValue() != null){
			e.addElement("value").setText(getValue());
		}
		
		
		return e;
	}
	
}
