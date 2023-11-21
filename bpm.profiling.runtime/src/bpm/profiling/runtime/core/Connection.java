package bpm.profiling.runtime.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.DriverShim;
import bpm.studio.jdbc.management.model.ListDriver;

public class Connection {
	
	
	public static Logger logger = Logger.getLogger("bpm.profiling.runtime");
	private int id;
	private String name;
	
	private String host;
	private String port;
	private String driverName;
	private String login;
	private String password;
	private String databaseName;
	private String schemaName;
	
	
	private String fmdtConnectionName;
	private boolean isFromRepository = false;
	private Integer directoryItemId;
	private String fmdtDataSourceName;
	private Integer vanillaGroupId;
	
	
	private String fullUrl;
	
	private List<Table> tables = new ArrayList<Table>();
	
	/**
	 * @return the vanillaGroupId
	 */
	public Integer getVanillaGroupId() {
		return vanillaGroupId;
	}
	/**
	 * @param vanillaGroupId the vanillaGroupId to set
	 */
	public void setVanillaGroupId(Integer vanillaGroupId) {
		this.vanillaGroupId = vanillaGroupId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	
	
	/**
	 * return JDBCconnection
	 * @throws Exception
	 */
	public VanillaJdbcConnection getJdbcConnection() throws Exception{
		String urlPrefix = "";
		
		//init the jdbc driver if it isnt
		try{
			urlPrefix = init();
		}catch(Exception e){
			throw e;
		}
		
		VanillaJdbcConnection con = null;

		String url = urlPrefix + host + ":" + port + "/" + databaseName;	
		try {
			con = ConnectionManager.getInstance().getJdbcConnection(url, login, password, ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverName).getClassName());
			
			return con;
		} catch (Exception e) {
			logger.error("Error while connecting to " + url, e);
			throw e;

		}

	}
	
	
	
	
	
	/**
	 * helper method to initialize the driver JDBC
	 * @throws Exception
	 * @return the urlPrefix for this driver
	 */
	private  String init() throws Exception{
		//look for the entry in the driver list files that macth to driverName
		DriverInfo info = ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverName);
		String urlPrefix = info.getUrlPrefix();
			
		return urlPrefix;
	}
	
	/**
	 * load the class named driverClassName from the jar file path 
	 * @param path : name of the JDBC JAR
	 * @param driverClassName
	 * @throws Exception 
	 */
	private static void registerDriver(String path, String driverClassName) throws Exception {
		URL u;
		try {
			File f = null;
			if (IConstants.getJdbcJarFolder().endsWith("/") || path.startsWith("/")){
				f = new File(IConstants.getJdbcJarFolder()  + path);
			}
			else{
				f = new File(IConstants.getJdbcJarFolder() + "/" + path);
			}
			
			
			if (!f.exists()){
				throw new Exception("Unable to find JDBC Jar File " + f.getAbsolutePath());
			}
			u = f.toURL();
			//p = path.replace("\\", "/");
//			u = new URL("jar:file:/" + path+ "!/");
			logger.info("Try to load class " + driverClassName + " from " + f.toURL());
			URLClassLoader ucl = new URLClassLoader(new URL[] { u });
			
			
			
			
			Driver d = (Driver)Class.forName(driverClassName, true, ucl).newInstance();
			DriverManager.registerDriver(new DriverShim(d));
			logger.info("JDBC driver " + driverClassName + " loaded");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//Log.error(e.getMessage(), e);
			logger.error("Error(url) while loading JDBC driver " + driverClassName + " from path");
		
			throw e;
		} catch (InstantiationException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("Error(instant) while loading JDBC driver " + driverClassName + " from path");
			throw e;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("Error(illegal) while loading JDBC driver " + driverClassName + " from path");
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("Error(sql) while loading JDBC driver " + driverClassName + " from path");
			throw e;
		}

	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Table> getTables() {
		return tables;
	}
	
	
	public List<Table> connect() throws Exception{
		tables.clear();
		
		Exception ex = null;
		String urlPrefix = "";
		
		//init the jdbc driver if it isnt
		try{
			//if (!inited){
			urlPrefix = init();
			//}
		}catch(Exception e){
			throw e;
		}
		
		VanillaJdbcConnection con = null;
		ResultSet rs = null;
		ResultSet colRs = null;
		VanillaPreparedStatement stmt = null;
		

		//get an SQL Connection to execute query
		fullUrl = urlPrefix + host + ":" + port + "/" + databaseName;	
		try {
			logger.info("Connecting to " + fullUrl + " ...");
			con = ConnectionManager.getInstance().getJdbcConnection(fullUrl, login, password, ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverName).getClassName());
			logger.info("Connected to " + fullUrl + " ...");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception("SQL Connection failed for " + fullUrl + "<" + login + "><" + password+">\n" + "SQLCode:" + e.getErrorCode());
		}

			//get the informations for tables
			DatabaseMetaData dmd;
			try {
				dmd = con.getMetaData();
				logger.info("Driver Version : " + dmd.getDriverVersion());
				logger.info("Driver Name : " + dmd.getDriverName());
				logger.info("DataBase Product Version :" + dmd.getDatabaseProductVersion());
				stmt = con.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new Exception("Unable to create SQL statement\n" + "SQLCode:" + e.getErrorCode());
			} finally{
//				closeAll(stmt, colRs,rs, con);
			}
			
			
			try {
				logger.info("Browsing catalog : " + con.getCatalog() + " schema: " + schemaName);
				rs = dmd.getTables(con.getCatalog(), schemaName, "%", new String[]{"ALIAS"});
				addTable(tables, rs, stmt, colRs);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new Exception("Unable to get SQL Aliases\n" + "SQLCode:" + e.getErrorCode());
			} finally{
//				closeAll(stmt, colRs,rs, con);
			}
			
			
		
			try {
				rs = dmd.getTables(con.getCatalog(), schemaName, "%", new String[]{"VIEW"});
				addTable(tables, rs, stmt, colRs);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new Exception("Unable to get SQL Views\n" + "SQLCode:" + e.getErrorCode());
			} finally{
//				closeAll(stmt, colRs,rs, con);
			}
			
			
			try {
				rs = dmd.getTables(con.getCatalog(), schemaName, "%", new String[]{"TABLE"});
				addTable(tables, rs, stmt, colRs);
			} catch (SQLException e) {
				e.printStackTrace();
				closeAll(stmt, colRs,rs, con);
				throw new Exception("Unable to get SQL Tables\n" + "SQLCode:" + e.getErrorCode());
			} finally{
//				closeAll(stmt, colRs,rs, con);
			}

			closeAll(stmt, colRs,rs, con);
		return tables;
	}
	
	
	
	protected void closeAll(VanillaPreparedStatement stmt, ResultSet colRs, ResultSet rs, VanillaJdbcConnection con) throws Exception{
		if (stmt != null){
			stmt.close();
		}
		if (colRs != null)
			colRs.close();
		
		if (rs != null)
			rs.close();
		
		if (con != null)
			ConnectionManager.getInstance().returnJdbcConnection(con);
	}
	
	
	private void addTable(List<Table> list, ResultSet rs, VanillaPreparedStatement stmt, ResultSet colRs) throws Exception{
		while (rs.next()){
			
			Table table = new Table();
			table.setName(rs.getString("TABLE_NAME"));
			table.setLabel(rs.getString("TABLE_NAME"));
			
			logger.debug("table : " + table.getName());
			colRs = stmt.executeQuery("select * from " + table.getName() + " where 1=0");
			ResultSetMetaData metadata = colRs.getMetaData();
			logger.debug("nb cols : "+metadata.getColumnCount());
			for(int i = 0; i< metadata.getColumnCount(); i++){
				logger.debug("    " + i + ":col:" + metadata.getColumnName(i+1));
				int index = i+1;
				String name = metadata.getColumnName(index);
//				String typeJava = metadata.getColumnClassName(index);
				String sqltype = metadata.getColumnTypeName(index);
//				int sqlTypeCode = metadata.getColumnType(index);
				
				int prec = metadata.getPrecision(index);
				
				if (sqltype.equalsIgnoreCase("varchar")){
					if (prec<= 21845){
						sqltype +="(" + prec + ")";
					}
					else{
						sqltype = "TEXT";
					}
					
					
				}
				
				Column col = new Column();
				col.setName(name);
				col.setLabel(name);
				col.setType(sqltype);
//				col.setClassName(typeJava);
//				col.setSqlType(sqltype);
//				col.setSqlTypeCode(sqlTypeCode);
				table.addColumn(col);
				
			}
			table.setConnection(this);
			list.add(table);
		}
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public List<Table> connect(SQLDataSource sqlCon) throws Exception{
//		testConnection();
		
		

		for(IDataStream s : sqlCon.getDataStreams()){
			Table t = new Table();
			t.setName(s.getOriginName());
			t.setLabel(s.getName());
			t.setConnection(this);
			for(IDataStreamElement e : s.getElements()){
				Column c = new Column();
				c.setName(e.getOriginName());
				c.setLabel(e.getName());
				if (e instanceof ICalculatedElement){
					c.setType(((ICalculatedElement)e).getJavaClassName());
				}else{
					c.setType(((SQLColumn)e.getOrigin()).getSqlType());
				}
				
				
				t.addColumn(c);
			}
			tables.add(t);
			
		}
		 
		return tables;
	}
	public boolean getIsFromRepository() {
		return isFromRepository;
	}
	public void setIsFromRepository(boolean isFromRepository) {
		this.isFromRepository = isFromRepository;
	}
	public Integer getDirectoryItemId() {
		return directoryItemId;
	}
	public void setDirectoryItemId(Integer directoryItemId) {
		this.directoryItemId = directoryItemId;
	}
	public String getFmdtDataSourceName() {
		return fmdtDataSourceName;
	}
	public void setFmdtDataSourceName(String fmdtDataSourceName) {
		this.fmdtDataSourceName = fmdtDataSourceName;
	}
	public void setFmdtConnectionName(String name) {
		this.fmdtConnectionName = name;
		
	}
	public String getFmdtConnectionName() {
		return this.fmdtConnectionName;
		
	}
	
	public String getFullUrl(){
		Exception ex = null;
		String urlPrefix = "";
		
		//init the jdbc driver if it isnt
		try{
			
			urlPrefix = init();
			fullUrl = urlPrefix + host + ":" + port + "/" + databaseName;	
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return fullUrl;
	}
}
