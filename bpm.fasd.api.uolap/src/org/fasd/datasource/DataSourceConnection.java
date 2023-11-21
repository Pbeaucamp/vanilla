package org.fasd.datasource;


import java.io.FileNotFoundException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import org.fasd.olap.ServerConnection;
import org.fasd.sql.SQLConnection;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;

/**
 * SQLDriver :
 * used to centralize all sql related stuff
 * 
 * @author manu
 *
 */
public class DataSourceConnection implements IConnection{
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	private String id;
	
	private String name = "";
	private SQLConnection con;
	private VanillaJdbcConnection sock = null;
	
	private String url = "";
	private String user = "";
	private String pass = "";
	private String driver = "";
	private String driverFile = "";
	private String type = "";
	private String dataSourceLocation = "";
	private String serverId = "";
	private ServerConnection server;
	private String fileLocation = "";
	private String transUrl = "";
	private String description = "";
	private String schemaName = "";
	private DataSource parent;
	
	private String repositoryUrl = null;
	private String directoryItemId = null;
	private String repositoryDsId = null;

	
	public String getDirectoryItemId(){
		return directoryItemId;
	}
	
	public void setDirectoryItemId(String id){
		try{
			Integer.parseInt(id);
			this.directoryItemId = id;
		}catch(Exception e){
			this.directoryItemId = null;
		}
		
	}
	
	public DataSource getParent() {
		return parent;
	}

	public void setParent(DataSource parent) {
		this.parent = parent;
	}

	public String getDataSourceLocation() {
		return dataSourceLocation;
	}

	public void setDataSourceLocation(String dataSourcerLocation) {
		this.dataSourceLocation = dataSourcerLocation;
	}

	public String getDesc() {
		return description;
	}

	public void setDesc(String desc) {
		this.description = desc;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public ServerConnection getServer() {
		return server;
	}

	public void setServer(ServerConnection server) {
		this.server = server;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getTransUrl() {
		return transUrl;
	}

	public void setTransUrl(String transUrl) {
		this.transUrl = transUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public DataSourceConnection(String name, SQLConnection c) throws ClassNotFoundException {
		this.name = name;
		this.con = c;
		
		counter++;
		id = "a" + String.valueOf(counter);
	}
	
	public DataSourceConnection() {
		counter++;
		id = "a" + String.valueOf(counter);
	}
	
	
	/**
	 * special for loading, do not use
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public void connectAll() throws FileNotFoundException, Exception {
		
		if (driverFile == "" || driverFile == null){
			for(DriverInfo d :ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()){
				if (d.getClassName().equals(driver)){
					driverFile = d.getFile();
					break;
				}
			}
		}
		con = new SQLConnection(url, user, pass, driverFile, driver, schemaName, "");
		
		sock = con.getConnection();
	}
	
	public void connect() throws FileNotFoundException, Exception {
		sock = con.getConnection();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public SQLConnection getConnection() {
		return con;
	}
	
	
	public ArrayList<String> getSchemas() throws Exception{
		ArrayList<String> buf = new ArrayList<String>();
		if (sock == null){
			try{
				connectAll();
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception(" Unable to connect to database : " + ex.getMessage(), ex);
			}
		}
			
		
		if (sock.isClosed()){
			try {
				sock = con.getConnection();
			}catch (Exception e) {
				e.printStackTrace();
				throw new Exception(" Unable to connect to database : " + e.getMessage(), e);
			}
		}
		
		DatabaseMetaData dmd = sock.getMetaData();
		
		ResultSet rs = dmd.getSchemas();
		
		while(rs.next()){
			String sName = rs.getString(1);
			if (getConnection().getUrl().toLowerCase().contains("jdbc:oracle")){
				if (sName.equalsIgnoreCase("SYS") || sName.equalsIgnoreCase("SYSTEM")){
					continue;
				}
			}
			buf.add(sName);
		}
		
		rs.close();
		
		return buf;
	}

	public ArrayList<DataObject> getTables(String schemaName) throws FileNotFoundException, Exception {
		
		ArrayList<DataObject> buf = new ArrayList<DataObject>();
		if (sock == null)
			connectAll();
		
		if (sock.isClosed()){
			try {
				sock = con.getConnection();
				
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		DatabaseMetaData dmd = sock.getMetaData();
		System.out.println("product version : " + dmd.getDatabaseProductVersion());
		System.out.println("driver version : " + dmd.getDriverVersion());
		ResultSet res;
		
//		try{
//			res=dmd.getSchemas();
//			System.out.println("schema");
//			while(res.next()){
//				System.out.println(res.getString(1));
//			}
//			System.out.println("ctalogs");
//			
//		}catch(SQLException e){
//			System.err.println("unable to do dmd.getSchemas()");
//			e.printStackTrace();
//		}
//		
//		
//		try{
//			res=dmd.getCatalogs();
//			while(res.next()){
//				System.out.println(res.getString(1));
//			}
//		}catch(SQLException e){
//			System.err.println("unable to do dmd.getCatalog()");
//			e.printStackTrace();
//		}
		
		
		if (schemaName != null && schemaName.trim().equals(""))
			res = dmd.getTables(null, schemaName, "%", new String[]{"TABLE", "VIEW"});
		else{
			res = dmd.getTables(null, "%", "%", new String[]{"TABLE", "VIEW"});
		}
		
		while (res.next()){
			try{
				String schName = res.getString("TABLE_SCHEM");
				DataObject table = new DataObject(res.getString("TABLE_NAME"));
				System.out.println(table.getName());
			
				if (!table.getName().startsWith("BIN$") && ! table.getName().startsWith("SYS_IOT")){
//					if (schemaName.trim().equals("")){
						if (schName != null && !"".equals(schName) && ! "null".equals(schName)){
							table.setSelectStatement("SELECT * FROM " + schName + "." + table.getName());
						}
						else{
							table.setSelectStatement("SELECT * FROM " + table.getName());
						}
						
//					}else{
//						table.setSelectStatement("SELECT * FROM " + schemaName + "." + table.getName());
//					}
					
					
					VanillaPreparedStatement s = sock.createStatement();

					ResultSet r;
					System.out.println(table.getSelectStatement());
					r = s.executeQuery(table.getSelectStatement() + " WHERE 1=0");
					
					ResultSetMetaData rsmd = r.getMetaData();
					for(int i=1;i<=rsmd.getColumnCount();i++){
						DataObjectItem it = new DataObjectItem(rsmd.getColumnLabel(i));
						it.setOrigin(rsmd.getColumnName(i));
						it.setClasse(rsmd.getColumnClassName(i));
						it.setSqlType(rsmd.getColumnTypeName(i));
						table.addDataObjectItem(it);
					}
					r.close();
					s.close();
					buf.add(table);
					this.getParent().addDataObject(table);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		
			
			
		}
		
		res.close();
		
		ConnectionManager.getInstance().returnJdbcConnection(sock);
		return buf;
	}
	
	
	public DataObject getFactTableFromQuery(String name, String query) throws Exception{
		if (sock == null)
			connectAll();
		
		if (sock.isClosed()){
			try {
				sock = con.getConnection();
				
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
		DataObject table = new DataObject();
		table.setSelectStatement(query);
		table.setName(name);
		table.setView(true);
		table.setDataObjectType("fact"); //$NON-NLS-1$
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		stmt = sock.createStatement();
		rs = stmt.executeQuery(query);// + " WHERE 1=0");
		
		ResultSetMetaData rsmd = rs.getMetaData();
		for(int i=1;i<=rsmd.getColumnCount();i++){
			DataObjectItem it = new DataObjectItem(rsmd.getColumnLabel(i));
			it.setOrigin(rsmd.getColumnName(i));
			it.setClasse(rsmd.getColumnClassName(i));
			it.setSqlType(rsmd.getColumnTypeName(i));
			table.addDataObjectItem(it);
		}
		rs.close();
		stmt.close();
		
		
		return table;
	}
	
	
//	public List<OLAPRelation> getRelations(List<DataObject> tables) throws SQLException{
//		List<OLAPRelation> buf = new ArrayList<OLAPRelation>();
//		if (sock.isClosed()){
//			try {
//				sock = con.getConnection();
//			} catch (FileNotFoundException e) {
//				
//				e.printStackTrace();
//			} catch (Exception e) {
//				
//				e.printStackTrace();
//			}
//		}
//		for(DataObject table : tables){
//			DatabaseMetaData dmd = sock.getMetaData();
//			ResultSet res = dmd.getImportedKeys(null, null, table.getName());
//			while(res.next()){
//				OLAPRelation rel = new OLAPRelation();
//				ResultSet r = dmd.getPrimaryKeys(null, null, table.getName());
//				for(DataObjectItem i : table.getColumns()){
//					if (i.getOrigin().equals(r.getString("PKCOLUMN_NAME"))){
//						rel.setLeftObjectItem(i);
//						break;
//					}	
//				}
//				r.close();
//				for(DataObject t : tables){
//					if(t.getPhysicalName().equals(res.getString("FKTABLE_NAME"))){
//						for(DataObjectItem it : t.getColumns()){
//							if (it.getOrigin().equals(res.getString("FKCOLUMN_NAME"))){
//								rel.setRightObjectItem(it);
//								buf.add(rel);
//								break;
//							}
//						}
//						
//					}
//				}
//				res.close();
//			}
//			
//			
//		}
//		sock.close();
//		
//		return buf;
//	}
	
	public String getFAXML() {
		String tmp = "";
		//buf += "<Schema name=\"" + name + "\">\n\n";
		
		//SQLConnection c = con.getConnection();
		
		tmp = "            <connection>\n" +
			  "                <name>" + name + "</name>\n" +
			  "                <id>" + id +"</id>\n" +
			  "                <type>" + type + "</type>\n" +
			  "                <data-source-location>" + dataSourceLocation + "</data-source-location>\n" ;
		if (server != null)
			tmp += "                <server-id>" + server.getId() + "</server-id>\n";
		else
			tmp += "                <server-id></server-id>\n";
		
		
		if (repositoryDsId != null && repositoryUrl != null){
			tmp += "                <repositoryDataSourceId>" + repositoryDsId + "</repositoryDataSourceId>\n";
			
			tmp += "                <repositoryUrl>" + repositoryUrl + "</repositoryUrl>\n";
		}
		
		
		if (directoryItemId != null){
			tmp += "                <directoryItemId>" + directoryItemId + "</directoryItemId>\n";
		}
		
		tmp +="                <driver>" + getDriver() + "</driver>\n" +
			  "                <schema-name>" + getSchemaName() + "</schema-name>\n" +
			  "                <url>" + getUrl() + "</url>\n" +
			  "                <transformation-url>" + transUrl + "</transformation-url>\n" +
			  "                <file-location>" + fileLocation + "</file-location>\n" +
			  "                <user>" + getUser() + "</user>\n" +
			  "                <password>" + getPass() + "</password>\n" +
			  "                <description>" + description + "</description>\n" +
			
			  "            </connection>\n";
		
		return tmp;
	}

	public String getDriver() {
		if (driver != null)
			return driver;
		else
			return con.getDriverName();
	}

	public void setDriver(String driver) {
		this.driver = driver;
		
	}

	public String getPass() {
		if (pass != null)
			return pass;
		else
			return con.getPass();
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getUrl() {
		if (url != null)
			return url;
		else
			return con.getUrl();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		if (user != null)
			return user;
		else
			return con.getUser();
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}



	public String getDriverFile() {
		return driverFile;
	}

	public void setDriverFile(String driverFile) {
		this.driverFile = driverFile;
	}

	public void setConnection(SQLConnection con2) {
		con = con2;
		
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public void setRepositoryDataSourceId(int id) {
		this.repositoryDsId = "" + id;
		
	}
	
	public void setRepositoryDataSourceId(String id) {
		this.repositoryDsId = id;
		
	}

	public void setRepositoryDataSourceUrl(String host) {
		this.repositoryUrl = host;
		
	}
	
	public String getRepositoryDataSourceId(){
		return repositoryDsId;
	}
	
	public String getRepositoryDataSourceUrl(){
		return repositoryUrl;
	}

}
