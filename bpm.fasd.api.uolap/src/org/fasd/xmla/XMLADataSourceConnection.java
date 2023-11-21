package org.fasd.xmla;

import org.fasd.datasource.IConnection;

public class XMLADataSourceConnection implements IConnection {

	private String pass = "";
	private String url = "";
	private String user = "";
	private String schema = "";
	private String catalog = "";
	private String type = "";
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPass() {
		return pass;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public void setPass(String password) {
		this.pass = password;

	}

	public void setUrl(String url) {
		this.url = url;

	}

	public void setUser(String user) {
		this.user = user;

	}
	
	public void setSchema(String schemaName){
		this.schema = schemaName;
	}
	
	public String getCatalog(){
		return catalog;
	}
	
	
	/**
	 * return datasource string
	 * @return
	 */
	@Deprecated 
	//XXX :rename getDataSource
	public String getSchema(){
		return schema;
	}
	
	public void setCatalog(String catalog){
		this.catalog = catalog;
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <XMLAConnection>\n");
		buf.append("            <url>" + url + "</url>\n");
		buf.append("            <user>" + user + "</user>\n");
		buf.append("            <password>" + pass + "</password>\n");
		buf.append("            <schema>" + schema + "</schema>\n");
		buf.append("            <catalog>" + catalog + "</catalog>\n");
		buf.append("            <type>" + type + "</type>\n");
		buf.append("        </XMLAConnection>\n");
		
		return buf.toString();
	}

}
