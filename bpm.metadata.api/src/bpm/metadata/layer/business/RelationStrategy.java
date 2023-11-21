package bpm.metadata.layer.business;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class RelationStrategy {

	private String name;
	private List<String> tableNames;
	private List<String> relationKeys;
	
	public RelationStrategy() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getTableNames() {
		return tableNames;
	}

	public void setTableNames(List<String> tableNames) {
		this.tableNames = tableNames;
	}

	public List<String> getRelationKeys() {
		return relationKeys;
	}

	public void setRelationKeys(List<String> relationKeys) {
		this.relationKeys = relationKeys;
	}
	
	public String getXml() {
		Element element = DocumentHelper.createElement("relationStrategy");
		
		element.addElement("name").setText(name);
		
		for(String table : tableNames) {
			element.addElement("table").setText(table);
		}
		for(String rel : relationKeys) {
			element.addElement("relationKey").setText(rel);
		}
		
		return element.asXML();
	}
	
	public void addTableName(String tableName) {
		if(tableNames == null) {
			tableNames = new ArrayList<String>();
		}
		tableNames.add(tableName);
	}
	
	public void addRelationKey(String relationKey) {
		if(relationKeys == null) {
			relationKeys = new ArrayList<String>();
		}
		relationKeys.add(relationKey);
	}

	public String getLabel() {
		StringBuilder buf = new StringBuilder();
		buf.append(name);
		buf.append(" -> ");
		for(String t : tableNames) {
			buf.append(t + " ");	
		}
		return buf.toString();
	}
}
