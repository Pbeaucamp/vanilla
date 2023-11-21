package bpm.es.pack.manager.vanillaplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.model.PlaceImportItem;

public class ImportItemDatasource {

	private IDatasource datasource;
	
	private String driver;
	private String driverClass;
	
	private String user;
	private String password;
	private String port;
	private String host;
	private String dbName;
	
	private String fmUser;
	private String fmPassword;
	
	private HashMap<PlaceImportItem, List<IDatasource>> items = new HashMap<PlaceImportItem, List<IDatasource>>();
	
	public ImportItemDatasource(IDatasource datasource, PlaceImportItem itemParent) {
		this.datasource = datasource;
		List<IDatasource> datasources = new ArrayList<IDatasource>();
		datasources.add(datasource);
		this.items.put(itemParent, datasources);
		
		//We init the value for port, host and url if the datasource is a JDBC Datasource
		if(datasource instanceof ModelDatasourceJDBC){
			ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)datasource;
			
			driver = dsJdbc.getDriver();
			user = dsJdbc.getUser();
			password = dsJdbc.getPassword();
			fmUser = dsJdbc.getFmUser();
			fmPassword = dsJdbc.getFmPassword();
			
			if(dsJdbc.isUseFullUrl()){
				String fullUrl = dsJdbc.getFullUrl();
				try {
					String urlTmp = ""; //$NON-NLS-1$
					if(fullUrl.indexOf(":@") == -1){ //$NON-NLS-1$
						urlTmp = fullUrl.split("://")[1]; //$NON-NLS-1$
					}
					else {
						urlTmp = fullUrl.split(":@")[1]; //$NON-NLS-1$
					}
					String[] tmp1 = urlTmp.split(":"); //$NON-NLS-1$
					String[] tmp2 = tmp1[1].split("/"); //$NON-NLS-1$
					
					host= tmp1[0];
					port = tmp2[0];
					dbName = tmp2[1];
				} catch (Exception e) {
					e.printStackTrace();
					port = ""; //$NON-NLS-1$
					host = ""; //$NON-NLS-1$
					dbName = ""; //$NON-NLS-1$
				}
			}
			else {
				port = dsJdbc.getPort();
				host = dsJdbc.getHost();
				dbName = dsJdbc.getDbName();
			}
		}
	}

	public void setDatasource(IDatasource datasource) {
		this.datasource = datasource;
	}

	public IDatasource getDatasource() {
		return datasource;
	}

	public void addItemParent(PlaceImportItem itemParent, IDatasource ds) {
		if(this.items.containsKey(itemParent)){
			this.items.get(itemParent).add(ds);
		}
		else {
			List<IDatasource> datasources = new ArrayList<IDatasource>();
			datasources.add(ds);
			this.items.put(itemParent, datasources);
		}
	}

	public HashMap<PlaceImportItem, List<IDatasource>> getItems() {
		return items;
	}
	
	public List<IDatasource> getDatasource(PlaceImportItem item){
		return this.items.get(item);
	}
	
	public String getDriver(){
		return driver;
	}
	
	public void setDriver(String driver){
		this.driver = driver;
	}
	
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	
	public String getDriverClass() {
		return driverClass;
	}
	
	public String getUser(){
		return user;
	}
	
	public void setUser(String user){
		this.user = user;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPort(){
		return port;
	}
	
	public void setPort(String port){
		this.port = port;
	}
	
	public String getHost(){
		return host;
	}
	
	public void setHost(String host){
		this.host = host;
	}
	
	public String getDbName(){
		return dbName;
	}
	
	public void setDbName(String dbName){
		this.dbName = dbName;
	}
	
	public String getFmUser(){
		return fmUser;
	}
	
	public void setFmUser(String fmUser){
		this.fmUser = fmUser;
	}
	
	public String getFmPassword(){
		return fmPassword;
	}
	
	public void setFmPassword(String fmPassword){
		this.fmPassword = fmPassword;
	}
//	
//	public String getFullUrl(){
//		//TODO: Full Url
//		return "";
//	}
	
	public boolean isEqualTo(IDatasource ds){
		if(ds instanceof ModelDatasourceJDBC && datasource instanceof ModelDatasourceJDBC){
			ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)ds;
			
			String portTmp = null;
			String hostTmp = null;
			String dbNameTmp = null;
			if(dsJdbc.isUseFullUrl()){
				String fullUrl = dsJdbc.getFullUrl();
				
				try {
					String urlTmp = ""; //$NON-NLS-1$
					if(fullUrl.indexOf(":@") == -1){ //$NON-NLS-1$
						urlTmp = fullUrl.split("://")[1]; //$NON-NLS-1$
					}
					else {
						urlTmp = fullUrl.split(":@")[1]; //$NON-NLS-1$
					}
					String[] tmp1 = urlTmp.split(":"); //$NON-NLS-1$
					String[] tmp2 = tmp1[1].split("/"); //$NON-NLS-1$
					
					hostTmp = tmp1[0];
					portTmp = tmp2[0];
					dbNameTmp = tmp2[1];
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				
				if(portTmp != null && hostTmp != null && dbNameTmp != null){
					boolean same = isPHDBTheSame(portTmp, hostTmp, dbNameTmp);
					if(same && ((dsJdbc.getFmUser() != null && fmUser != null 
							&& dsJdbc.getFmPassword() != null && fmPassword != null) 
							|| (dsJdbc.getFmUser() == null && fmUser == null 
							&& dsJdbc.getFmPassword() == null && fmPassword == null))){
						return true;
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}
			}
			else {
				portTmp = dsJdbc.getPort();
				hostTmp = dsJdbc.getHost();
				dbNameTmp = dsJdbc.getDbName();

				if(portTmp != null && hostTmp != null && dbNameTmp != null){
					boolean same = isPHDBTheSame(portTmp, hostTmp, dbNameTmp);
					if(same && ((dsJdbc.getFmUser() != null && fmUser != null 
							&& dsJdbc.getFmPassword() != null && fmPassword != null) 
							|| (dsJdbc.getFmUser() == null && fmUser == null 
							&& dsJdbc.getFmPassword() == null && fmPassword == null))){
						if(user != null && password != null && dsJdbc.getUser() != null && dsJdbc.getPassword() != null &&
								(!user.equals(dsJdbc.getUser()) || !password.equals(dsJdbc.getPassword()))) {
							return false;
						}
						return true;
					}
					else {
						return false;
					}
				}
				else if(dsJdbc.getFmUser() != null && fmUser != null 
						&& dsJdbc.getFmPassword() != null && fmPassword != null){
					return true;
				}
				else {
					return false;
				}
			}
		}
		else {
			return false;
		}
	}
	
	private boolean isPHDBTheSame(String portTmp, String hostTmp, String dbNameTmp){
		if(portTmp.equals(port) && hostTmp.equals(host) && dbNameTmp.equals(dbName)){
			return true;
		}
		else {
			return false;
		}
	}
}