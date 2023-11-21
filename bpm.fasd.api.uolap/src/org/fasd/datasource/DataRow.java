package org.fasd.datasource;

import java.util.HashMap;

public class DataRow {
	private HashMap<String, String> row = new HashMap<String, String>();
	
	public void addValue(String colName, String value){
		row.put(colName,value);
	}
	
	public int size(){
		return row.size();
	}
	
	public void clear(){
		row.clear();
	}
	
	public HashMap<String,String> getValues(){
		return row;
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("                   <row>\n");
		for(String s : row.keySet()){
			buf.append("                       <cell>\n");
			buf.append("                           <value>" + row.get(s) + "</value>\n");
			buf.append("                           <column-id>" + s + "</column-id>\n");
			buf.append("                       </cell>\n");
		}
		buf.append("                   </row>\n");
		return buf.toString();
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("                        <Row>\n");
		for(String s : row.keySet()){
			buf.append("                            <Value column=\"" + s + "\">" + row.get(s) + "</Value>\n");
		}
		buf.append("                        </Row>\n");
		return buf.toString();
	}

	public String getValue(String name) {
		return row.get(name);
	}

}
