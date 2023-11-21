package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;

public class CubeView implements ICubeView{
	private List<String> cols = new ArrayList<String>();
	private List<String> rows = new ArrayList<String>();
	private String name = "cubeView";
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addCol(String s){
		cols.add(s);
	}
	
	public void removeCol(String s){
		cols.remove(s);
	}
	
	public void addRow(String s){
		rows.add(s);
	}
	
	public void removeRow(String s){
		rows.remove(s);
	}
	
	public List<String> getCols(){
		return cols;
	}
	
	public List<String> getRows(){
		return rows;
	}
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		
		buf.append("    <view>\n");
		for(String s : cols){
			buf.append("        <col>" + s + "</col>\n");
		}
		
		for(String s : rows){
			buf.append("        <row>" + s + "</row>\n");
		}
		buf.append("    </view>\n");
		return buf.toString();
	
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		
		
		buf.append("    <drill-throught>\n");
		buf.append("        <name>" + name + "</name>\n");
		for(String s : cols){
			buf.append("        <col>" + s + "</col>\n");
		}
		
		for(String s : rows){
			buf.append("        <row>" + s + "</row>\n");
		}
		buf.append("    </drill-throught>\n");
		return buf.toString();
	}
}
