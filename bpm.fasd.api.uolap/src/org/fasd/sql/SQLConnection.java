package org.fasd.sql;

import java.io.FileNotFoundException;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;




/**
 * SQLConnection:
 * Internal class representing a sql connection.
 * @author manu
 *
 */

public class SQLConnection {
	private String url;
	private String user;
	private String pass;
	private String driverFile;
	private String driverName;
	private String type;
	private String schemaName;
	
	public SQLConnection(){}
	
	public void setDriverFile(String driverFile) {
		this.driverFile = driverFile;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public SQLConnection(String url, String user, String pass, String driverFile, String driverName, String dbName, String type) {
		this.url = url;
		this.user = user;
		this.pass = pass;
		this.driverFile = driverFile;
		this.driverName = driverName;
		this.schemaName = dbName;
		this.type = type;
	}
	
	public VanillaJdbcConnection getConnection() throws FileNotFoundException, Exception {
		String jdbcXml = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_JDBC_XML_FILE);
		String driverClass = ListDriver.getInstance(jdbcXml).getInfo(driverName).getClassName();
		
		if (user.equals("")) {
			return ConnectionManager.getInstance().getJdbcConnection(url, null, null, driverClass);
		}
		else{
			try{
				return ConnectionManager.getInstance().getJdbcConnection(url, user, pass, driverClass);
			}catch(Exception ex){
				ex.printStackTrace();
				
				if (url.startsWith("jdbc:oracle") && url.contains("/")){
					return ConnectionManager.getInstance().getJdbcConnection(url.replace("/", ":"), user, pass, driverClass);
				}
				throw ex;
			}
		}
			
	}


	public String getUrl(){
		return url;
	}
	public String getUser(){
		return user;
	}
	public String getPass(){
		return pass;
	}
	public String getDriverFile(){
		return driverFile;
	}
	public String getDriverName(){
		return driverName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
		
	}
	
}
