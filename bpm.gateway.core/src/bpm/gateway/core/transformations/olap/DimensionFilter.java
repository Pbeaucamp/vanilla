package bpm.gateway.core.transformations.olap;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class DimensionFilter {
	private String name = "olap Dimension filter";
	private List<FilterClause> filters = new ArrayList<FilterClause>();
	private String outputTransformation;
	
	
	public void setOutputTransformation(String outputName){
		this.outputTransformation = outputName;
	}
	
	public String getOutputName(){
		return outputTransformation;
	}
	
	public List<FilterClause> getFilters(){
		return new ArrayList<FilterClause>(filters);
	}
	
	public void addLevel(FilterClause filter){
		filters.add(filter);
		filter.setParent(this);
	}
			
	public void addLevel(String levelName){
		for(FilterClause c : filters){
			if (c.getLevelName().equals(levelName)){
				return;
			}
		}
		
		filters.add(new FilterClause(levelName, this));
	}
	
	public FilterClause getFilter(String levelName){
		for(FilterClause c : filters){
			if (c.getLevelName().equals(levelName)){
				return c;
			}
		}
		return null;
	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("olapDimensionFilter");
		e.addElement("name").setText(getName());
		if (getOutputName() != null){
			e.addElement("outputName").setText(getOutputName());
		}
		for(FilterClause c : getFilters()){
			e.add(c.getElement());
		}
		
		return e;
	}
	
	
}
