package org.fasd.datasource;

import java.util.HashMap;

public class DataView extends DataObject{
	private HashMap<String, String> mondrianSql = new HashMap<String, String>();

	public void addDialect(String dialect, String sql){
		mondrianSql.put(dialect, sql);
	}
	
	public String getSql(String dialect){
		return mondrianSql.get(dialect);
	}
	
	public String getXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("            <View>\n");
		for(String s : mondrianSql.keySet()){
			buf.append("                <SQL dialect=\"" + s + "\">\n");
			buf.append("                    " + mondrianSql.get(s) + "\n");
			buf.append("                </SQL>\n");
		}
		buf.append("            </View>\n");
		
		return buf.toString();
	}
	
	public String getFAXML(){
		String tmp = "";
		
		tmp += "            <dataview-item>\n";
		tmp += "            	<id>" + getId() + "</id>\n";
		tmp += "                <name>" + getName() + "</name>\n";
		tmp += "                <data-source-id>" + getDataSource().getId() + "</data-source-id>\n";
		tmp += "                <server-id>" + getServerId() + "</server-id>\n";
		tmp += "                <data-object-type>" + getDataObjectType() + "</data-object-type>\n";
		
		String statementFormated =getSelectStatement().replace('<', '#');
		statementFormated = statementFormated.replace('>', '#');
		
		tmp += "                <select-statment-definition>" + statementFormated + "</select-statment-definition>\n";
		tmp += "                <transformation-name>" + getTransName() + "</transformation-name>\n";
		tmp += "                <file-name>" + getFileName() + "</file-name>\n";
		tmp += "                <description>" + getDesc() + "</description>\n";
		tmp += "                <isInline>" + isInline() + "</isInline>\n";		
		for (int i=0; i < getColumns().size(); i++) {
			tmp += getColumns().get(i).getFAXML();
		}
		
		for(String s : mondrianSql.keySet()){
			tmp += "                <dialect>\n";
			tmp += "                    <name>" + s + "</name>\n";
			tmp += "                    <query>" + mondrianSql.get(s) + "</query>\n";
			tmp += "                </dialect>\n";
		}
		
		tmp += "            </dataview-item>\n";
		
		return tmp;
	}
	
}

