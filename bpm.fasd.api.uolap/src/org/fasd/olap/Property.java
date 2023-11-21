package org.fasd.olap;

import org.fasd.datasource.DataObjectItem;

public class Property extends OLAPElement {
	private String columnId = "";
	private String  formatter = "";
	private String type = "";
	private String caption = "";
	private DataObjectItem column;
	private static int counter = 0;
	
	public static String[] TYPES = {"String", "Numeric", "Integer", "Boolean", 
									"Date", "Time", "Timestamp"};

	public static void resetCounter(){
		counter =0;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public Property(){
		counter ++;
		id = Integer.toString(counter);
	}
	
	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("            <Property>\n");
		buf.append("                <id>" + id + "</id>\n");
		buf.append("                <caption>" + caption + "</caption>\n");
		buf.append("                <name>" + getName() + "</name>\n");
		buf.append("                <dataobjectitem-id>" + column.getId() + "</dataobjectitem-id>\n");
		buf.append("                <type>" + type + "</type>\n");
		buf.append("                <formatter>" + formatter + "</formatter>\n");
		buf.append("            </Property>\n");
		return buf.toString();
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("                    <Property name=\"" + getName() + "\"");
		
		if (!caption.trim().equals(""))
			buf.append(" caption=\"" + caption + "\"");
		
		if (column != null)
			buf.append(" column=\"" + column.getName() + "\"");
		else
			buf.append(" column=\"" + columnId + "\"");
		
		buf.append(" type=\"" + type + "\"");
		
		if (!formatter.equals(""))
			buf.append(" formatter=\"" + formatter + "\"/>\n");
		else
			buf.append("/>\n");
		
		return buf.toString();	
	}

	public DataObjectItem getColumn() {
		return column;
	}

	public void setColumn(DataObjectItem column) {
		this.column = column;
	}
	

	public void setName(String name){
		super.setName(name);
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
}
