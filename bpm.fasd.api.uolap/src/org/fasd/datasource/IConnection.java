package org.fasd.datasource;

public interface IConnection {
	public void setUrl(String url);
	public void setPass(String password);
	public void setUser(String user);
	
	public String getUrl();
	public String getPass();
	public String getUser();
	
}
