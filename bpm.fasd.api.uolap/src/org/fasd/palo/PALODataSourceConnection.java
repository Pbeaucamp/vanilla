package org.fasd.palo;

import org.fasd.datasource.IConnection;

public class PALODataSourceConnection implements IConnection {

	public String getPass() {
		
		return null;
	}

	public String getUrl() {
		
		return null;
	}

	public String getUser() {
		
		return null;
	}

	public void setPass(String password) {
		

	}

	public void setUrl(String url) {
		

	}

	public void setUser(String user) {
		

	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}

}
