package bpm.metadata.layer.physical.sql;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.ITable;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;

public class SQLConnection implements IConnection {
	/**
	 * the names of the different connection methods
	 */
	public static final String[] methods = {"Jdbc"};
	
	
	private String driverName;
	private String methodName = methods[0];
	private String host;
	private String dataBaseName;
	private String portNumber;
	private String username;
	private String password;
	private boolean connected = false;
	private String name = "Default";
	
	private boolean useFullUrl = false;
	private String fullUrl;
	
	private List<ITable> tables = new ArrayList<ITable>();
	
	/**
	 * do not use
	 */
	public SQLConnection(){
		
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		if (name != null && !name.trim().equals("")){
			this.name = name;
		}
		
	}
	
	
	/**
	 * @return the useFullUrl
	 */
	public boolean isUseFullUrl() {
		return useFullUrl;
	}

	/**
	 * @param useFullUrl the useFullUrl to set
	 */
	public void setUseFullUrl(boolean useFullUrl) {
		this.useFullUrl = useFullUrl;
	}
	
	/**
	 * @param useFullUrl the useFullUrl to set
	 */
	public void setUseFullUrl(String useFullUrl) {
		try{
			this.useFullUrl = Boolean.parseBoolean(useFullUrl);
		}catch(Exception ex){
			
		}
	}

	/**
	 * @return the fullUrl
	 */
	public String getFullUrl() {
		return fullUrl;
	}

	/**
	 * @param fullUrl the fullUrl to set
	 */
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

	/**
	 * helper method to initialize the driver JDBC
	 * @throws Exception
	 * @return the urlPrefix for this driver
	 */
	private String init() throws Exception{
		//look for the entry in the driver list files that macth to driverName
		DriverInfo info = ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverName);
		String urlPrefix = info.getUrlPrefix();
		return urlPrefix;
	}
	

	public String getDriverName() {
		return driverName;
	}


	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}


	public String getMethodName() {
		return methodName;
	}


	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	public String getDataBaseName() {
		return dataBaseName;
	}


	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}


	public String getPortNumber() {
		return portNumber;
	}


	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	

	public List<ITable> connect() throws Exception{
		tables.clear();

		
		VanillaJdbcConnection con = null;
		ResultSet rs = null;
		ResultSet colRs = null;
		VanillaPreparedStatement stmt = null;
		

		con = getJdbcConnection();

		//get the informations for tables
		DatabaseMetaData dmd;
		try {
			dmd = con.getMetaData();
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLConnectionException("Unable to create SQL statement\n" + "SQLCode:" + e.getErrorCode());
		} 
		
			
		ResultSet schemas = dmd.getSchemas();
		boolean hasSchema = false;
		try{
			
			while(schemas.next()){
				
				if(con.getUrl().startsWith("jdbc:relique:csv:")) {
					break;
				}
				
				hasSchema = true;
				String schemaName = schemas.getString("TABLE_SCHEM");
				
				
				try {
					/*
					 * get Tables
					 */
					rs = dmd.getTables(con.getCatalog(), schemaName, "%", new String[]{"TABLE"});
					addTable(dmd, con.getCatalog(), schemaName,tables, rs, stmt, colRs);

					
					/*
					 * get Aliases
					 */
					rs = dmd.getTables(con.getCatalog(), schemaName, "%", new String[]{"ALIAS"});
					addTable(dmd, con.getCatalog(), schemaName,tables, rs, stmt, colRs);

					
					
					/*
					 * get Views
					 */
					rs = dmd.getTables(con.getCatalog(), schemaName, "%", new String[]{"VIEW"});
					addTable(dmd, con.getCatalog(), schemaName,tables, rs, stmt, colRs);

					
				} catch (SQLException e) {
					e.printStackTrace();
					throw new SQLConnectionException("Unable to get SQL Aliases\n" + "SQLCode:" + e.getErrorCode());
				} finally{

				}
				
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			schemas.close();
		}
		
		
		if (!hasSchema){
			try {
				/*
				 * get Tables
				 */
				rs = dmd.getTables(con.getCatalog(), null, "%", new String[]{"TABLE"});
				addTable(dmd, con.getCatalog(), null,tables, rs, stmt, colRs);

				rs = dmd.getTables(con.getCatalog(), null, "%", new String[]{"ALIAS"});
				addTable(dmd, con.getCatalog(), null,tables, rs, stmt, colRs);

				rs = dmd.getTables(con.getCatalog(), null, "%", new String[]{"VIEW"});
				addTable(dmd, con.getCatalog(), null,tables, rs, stmt, colRs);

				
			} catch (SQLException e) {
				e.printStackTrace();
				throw new SQLConnectionException("Unable to get SQL Aliases\n" + "SQLCode:" + e.getErrorCode());
			} finally{

			}
		}
		
		

		closeAll(stmt, colRs,rs);
		returnJdbcConnection(con);
		connected = true;
		return tables;
	}


	
	private void closeAll(VanillaPreparedStatement stmt, ResultSet colRs, ResultSet rs) throws Exception{
		if (stmt != null){
			stmt.close();
		}
		if (colRs != null)
			colRs.close();
		
		if (rs != null)
			rs.close();
	}
	
	
	
	
	public void getColumns(SQLTable table) throws Exception{
		
		
		VanillaJdbcConnection con = null;
		ResultSet rs = null;
		con = getJdbcConnection();		
		
		DatabaseMetaData dmd = con.getMetaData();
		rs = dmd.getColumns(con.getCatalog(), table.getSchemaName(), table.getShortName(), "%");


		while(rs.next()){
			
			String name = rs.getString("COLUMN_NAME");
			String typeJava = "java.lang.Object";
			String sqltype = rs.getString("TYPE_NAME");
			int sqlTypeCode = rs.getInt("DATA_TYPE");
			
			switch(sqlTypeCode){
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				typeJava = "java.lang.String";
				break;
			
			case Types.NUMERIC:
			case Types.DECIMAL:
				typeJava = "java.math.BigDecimal";
				break;
				
			case Types.BIT:
				typeJava = "java.lang.Boolean";
				break;
				
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
				typeJava = "java.lang.Integer";
				break;
				
			case Types.BIGINT:
				typeJava = "java.lang.Long";
				break;
				
			case Types.REAL:
				typeJava = "java.lang.Float";
				break;
			case Types.FLOAT:
			case Types.DOUBLE:
				typeJava = "java.lang.Double";
				break;
				
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				break;
				
			case Types.DATE:
				typeJava = "java.sql.Date";
				break;
				
			case Types.TIME:
				typeJava = "java.sql.Time";
				break;
				
			case Types.TIMESTAMP:
				typeJava = "java.sql.Timestamp";
				break;
				
									
			}
			
			SQLColumn col = new SQLColumn(table);
			col.setName(table.getName() + "." + name);
			
			
			
			col.setClassName(typeJava);
			col.setSqlType(sqltype);
			col.setSqlTypeCode(sqlTypeCode);
			
			table.addColumn(col);
			
			
			
		}
		
		rs.close();
		
		returnJdbcConnection(con);
	}
	
	

	private void addTable(DatabaseMetaData dmd, String catalog, String schema, List<ITable> list, ResultSet rs, VanillaPreparedStatement stmt, ResultSet colRs) throws SQLException{
		while (rs.next()){
			
			SQLTable table = null;
			
			if ("TABLE".equals(rs.getString("TABLE_TYPE"))){
				table = new SQLTable(this, SQLTable.TABLE);
			}
			else if ("VIEW".equals(rs.getString("TABLE_TYPE"))){
				table = new SQLTable(this, SQLTable.VIEW);		
			}
			else if ("ALIAS".equals(rs.getString("TABLE_TYPE"))){
				table = new SQLTable(this, SQLTable.ALIAS);
			}
			else{
				continue;
			}
			
			table.setSchemaName(schema);
			table.setName(rs.getString("TABLE_NAME"));
			if (rs.getString("TABLE_NAME").contains("$")){
				continue;
			}
			list.add(table);


		}
		rs.close();
	}


	public void test() throws Exception{
		try{
			VanillaJdbcConnection c = getJdbcConnection();
			returnJdbcConnection(c);
		}catch(SQLException e){
			throw new SQLConnectionException("SQL Connection failed : " +  e.getMessage() + " "+ e.getErrorCode() + "\nSQL State:" + e.getSQLState());
		}
		
	}
	
	protected List<String> getColumnValues(SQLColumn col){
		List<String> result = new ArrayList<String>();
		try{
			
			VanillaJdbcConnection con = getJdbcConnection();
			
			
			VanillaPreparedStatement stmt = con.createStatement();
			
			String q = "select distinct " + col.getName() ;
			if (((SQLTable)col.getTable()).isQuery()){
//				q += col.getTable().getName().substring(col.getTable().getName().indexOf(" from "));
				q += " from (" + col.getTable().getName() + ") t";
			}
			else{
				q += " from " + col.getTable().getName();
			}
			
			q += " order by " + col.getName();
			
			ResultSet rs =stmt.executeQuery(q);
			
			while(rs.next()){
				if (rs.getString(1)!=null)
					result.add(rs.getString(1));
			}
			
			returnJdbcConnection(con);
		}catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}


	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("        <sqlConnection>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <driverName>" + driverName + "</driverName>\n");
		buf.append("            <methodName>" + methodName + "</methodName>\n");
		buf.append("            <host>" + host + "</host>\n");
		buf.append("            <dataBaseName>" + dataBaseName + "</dataBaseName>\n");
		buf.append("            <portNumber>" + portNumber + "</portNumber>\n");
		buf.append("            <username>" + username + "</username>\n");
		buf.append("            <password>" + password + "</password>\n");
		buf.append("            <useFullUrl>" + useFullUrl + "</useFullUrl>\n");
		if (fullUrl != null){
			buf.append("            <fullUrl>" + fullUrl + "</fullUrl>\n");
		}
		
		buf.append("        </sqlConnection>\n");
		return buf.toString();
	}

	
	public ITable getTable(String name) throws Exception {
		
		
		if (!connected){
			connect();
		}
		
		for(ITable t : tables){
//			System.out.println(t.getName());
			if (t != null &&t.getName() != null && t.getName().equals(name)){
				return t;
			}
			
		}
		
		//postgres.....
		for(ITable t : tables){
//			System.out.println(t.getName());
			if (t != null &&t.getName() != null && t.getName().equalsIgnoreCase("public."+name)){
				return t;
			}
			if (t != null &&t.getName() != null && ("public."+t.getName()).equalsIgnoreCase(name)){
				return t;
			}
			
		}
		return null;
	}
	
	
public ITable getTableByName(String name) throws Exception {
		
		
		if (!connected){
			connect();
		}
		
		for(ITable t : tables){
			if (t != null &&t.getNameWithoutShema() != null && t.getNameWithoutShema().equals(name)){
				return t;
			}
			
		}
		

		return null;
	}

	public ITable getTableWithoutConnect(String name) throws Exception {
		
		for(ITable t : tables){
			if (t.getName().equals(name)){
				return t;
			}
		}
		
		
		ITable t = null;
		if (name.contains(".")){
			String[] p = name.split("\\.");
			t = loadTable(p[0], p[1]);
		}
		else{
			t = loadTable("%", name);
		}
		
		
		return t;
	}

	
	private ITable loadTable(String schemaName, String tableName) throws Exception{
		
		
		for(ITable t : tables){
			if (((SQLTable)t).getShortName().equals(tableName)){
				if (schemaName == null){
					if (((SQLTable)t).getSchemaName() == null || ((SQLTable)t).getSchemaName().equals("")){
						return t;
					}
				}
				else{
					if (((SQLTable)t).getSchemaName().equals("") && (schemaName == null || schemaName.equals(""))){
						return t;
					}
				}
				
			}
		}
		
		
		
		VanillaJdbcConnection con = getJdbcConnection();
		DatabaseMetaData dmd = con.getMetaData();
		
				
		ResultSet rs = dmd.getTables(con.getCatalog(), schemaName, tableName, new String[]{"TABLE", "VIEW", "ALIAS"});
		
		SQLTable t = null;
		while(rs.next()){
			
			String type = rs.getString("TABLE_TYPE");
			int typeCte = SQLTable.TABLE;
			if (type.equalsIgnoreCase("TABLE")){
				typeCte = SQLTable.TABLE;
			}
			else if (type.equalsIgnoreCase("VIEW")){
				typeCte = SQLTable.VIEW;
			}
			else if (type.equalsIgnoreCase("ALIAS")){
				typeCte = SQLTable.ALIAS;
			}
			else{
				continue;
			}
			t = new SQLTable(this, typeCte );
			t.setSchemaName(rs.getString("TABLE_SCHEM"));
			t.setName(rs.getString("TABLE_NAME"));
		}
		
		rs.close();
		returnJdbcConnection(con);
		
		if (t != null){
			tables.add(t);
		}
		if (t == null){
		}
		return t;
		
		
	}
	
	/**
	 * 
	 * @return a table named by name, without taking care of the schema name
	 */
	public ITable getTableRegardlessSchema(String name)throws Exception {
		if (!connected){
			connect();
		}
		
		for(ITable t : tables){
			
			if (((SQLTable)t).getShortName().equals(name)){
				return t;
			}
		}
		
		return null;
	}
	
	public void returnJdbcConnection(VanillaJdbcConnection c) throws Exception {

		ConnectionManager.getInstance().returnJdbcConnection(c);

	}
	
	
	
	/**
	 * the best to use it is to create a statement as a stream first
	 * then once the query have been used, to close statement and connection
	 * 
	 * @return a Jdbc Connection object to perform queries
	 * @throws Exception
	 */
	public VanillaJdbcConnection getJdbcConnection()throws Exception {
		

		String url = "";
		
		if (useFullUrl && fullUrl != null){
			init();
			url = fullUrl;
		}
		else{
			DriverInfo driverInfo = ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverName);
			
			if (driverName.equals(ListDriver.MS_ACCESS) || driverName.equals(ListDriver.MS_XLS)){
				url = driverInfo.getUrlPrefix() + dataBaseName ;
				if (!url.trim().endsWith(";")){
					url = url.trim() + ";";
				}
			}
			else {
				String urlPrefix = init();
				if (portNumber == null || "".equals(portNumber)){
					url = urlPrefix + host  + "/" + dataBaseName;
				}
				else {
					url = urlPrefix + host + ":" + portNumber + "/" + dataBaseName;
				}
				
				if (ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverName)
						.getClassName().contains("oracle")){
					url = url.replace("/", ":");
				}
				
			}
		}
		return ConnectionManager.getInstance().getJdbcConnection(url, username, password, ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverName).getClassName());
	}
	
	
	/**
	 * execute the given selection query and return its result
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List<List<String>> executeQuery(String query, Integer maxRows, boolean[] flags) throws Exception{
		
//		maxRows = null;
		
		//connect
		VanillaJdbcConnection con = null;
		ResultSet rs = null;;
		VanillaPreparedStatement stmt = null;
		
		List<List<String>> result = new ArrayList<List<String>>();
		
		try{
			con = getJdbcConnection();
		
			
			stmt = con.createStatement();
			if (maxRows != null){
				stmt.setMaxRows(maxRows);
			}
			if (maxRows != null && maxRows > 0){
				if(con.getDriverClass().contains("Oracle")) {
					if(query.toLowerCase().contains(" limit")) {
						if(!StringUtils.containsIgnoreCase(query, "where ")) {
							query = query.replaceAll("(?i)limit " + maxRows, "");
							int orderIndex = query.toLowerCase().indexOf("order by");
							int groupIndex = query.toLowerCase().indexOf("group by");
							
							if(groupIndex == -1 && orderIndex == -1) {
								query = query + " where ROWNUM < " + maxRows;
							}
							else if(groupIndex == -1) {
								query = query.replaceAll("(?i)order by ", "where ROWNUM <= " + maxRows + " order by ");
							}
							else {
								query = query.replaceAll("(?i)group by ", "where ROWNUM <= " + maxRows + " group by ");
							}
						}
						else {
							query = query.replaceAll("(?i)limit " + maxRows, "");
							query = query.replaceAll("(?i)where ", "where ROWNUM <= " + maxRows + " and ");
						}
						
					}
					else if(StringUtils.containsIgnoreCase(query, "where ")) {
						query = query.replaceAll("(?i)where ", "where ROWNUM <= " + maxRows + " and ");
					}
					else {
						query = query + " where ROWNUM <= " + maxRows;
					}
				}
				else {
					if (!query.toLowerCase().contains(" limit")){
						query = query + " limit " + maxRows;
						
					}
				}
			}
			
			String toExecute = query.replace("`", "\"");
			
			if(con.getDriverClass().contains("apache.drill")) {
				toExecute = toExecute.replace(" limit " + maxRows, "");
				toExecute = toExecute.replace("\"", "`");
			}
			else if (con.getDriverClass().contains("mariadb")) {
				toExecute = toExecute.replace("\"", "`");
			}
			
			rs = stmt.executeQuery(toExecute);
			
			result = new ArrayList< List<String> >();
			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()){
				ArrayList<String> row = new ArrayList<String>();
				for(int i = 1 ; i <= meta.getColumnCount(); i++){
					if (flags != null){
						if (flags[i - 1] == true){
							if(	meta.getColumnType(i) == Types.DECIMAL ||
									meta.getColumnType(i) == Types.DOUBLE ||
									meta.getColumnType(i) == Types.FLOAT ||
									meta.getColumnType(i) == Types.NUMERIC ||
									meta.getColumnType(i) == Types.REAL) {
								try {
									Double d = rs.getDouble(i);
									//Before we used %f which transform the decimal separator with a comma instead of dot
//									row.add(String.format("%f",d));
									row.add(String.format("%s",d));
								} catch (Exception e) {
									row.add(rs.getString(i));
								}
							}
							else {
								row.add(rs.getString(i));
							}
						}
						else{
							row.add("XXXXXXXX");
						}
					}
					else{
						if(	meta.getColumnType(i) == Types.DOUBLE ||
								meta.getColumnType(i) == Types.FLOAT ||
								meta.getColumnType(i) == Types.REAL) {
							try {
								Double d = rs.getDouble(i);
								//Before we used %f which transform the decimal separator with a comma instead of dot
//								row.add(String.format("%f",d));
								row.add(String.format("%s",d));
							} catch (Exception e) {
								row.add(rs.getString(i));
							}
						}
						else {
							row.add(rs.getString(i));
						}
					}
					
					
					
				}
				result.add(row);
			}
	
		}catch(SQLException e){
			e.printStackTrace();
			String errMsg = "SQLException : ";
			while (e != null) {
                
                errMsg += "\n\tErrorCode = "+ e.getErrorCode();
                errMsg += "\n\tSQLState = "+ e.getSQLState();
                errMsg += "\n\tMessage = "+ e.getMessage();
            
                e = e.getNextException();
//                throw e;
            }
			Exception res = new SQLConnectionException("Query " + query + "\n failed .\n" + errMsg );
			throw res;
		
		
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally{
			if( rs != null){
				rs.close();
			}
			if( stmt != null){
				stmt.close();
			}
			if( con != null){
				returnJdbcConnection(con);
			}
		}
		
		
		return result;
	}
	
	
	/**
	 * execute the given selection query and return its number of row
	 * @param query
	 * @return
	 * @throws Exception
	 */
	@Override
	public Integer countQuery(String query, Integer maxRows) throws Exception{
		//connect
		VanillaJdbcConnection con = null;
		ResultSet rs = null;;
		VanillaPreparedStatement stmt = null;
		
		int count = 0;
		
		try{
			con = getJdbcConnection();
		
			
			stmt = con.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			if (maxRows != null){
				stmt.setMaxRows(maxRows);
			}
			if (maxRows != null && maxRows > 0){
				if(con.getDriverClass().contains("Oracle")) {
					if(query.toLowerCase().contains(" limit")) {
						if(!StringUtils.containsIgnoreCase(query, "where ")) {
							query = query.replaceAll("(?i)limit " + maxRows, "");
							int orderIndex = query.toLowerCase().indexOf("order by");
							int groupIndex = query.toLowerCase().indexOf("group by");
							
							if(groupIndex == -1 && orderIndex == -1) {
								query = query + " where ROWNUM < " + maxRows;
							}
							else if(groupIndex == -1) {
								query = query.replaceAll("(?i)order by ", "where ROWNUM <= " + maxRows + " order by ");
							}
							else {
								query = query.replaceAll("(?i)group by ", "where ROWNUM <= " + maxRows + " group by ");
							}
						}
						else {
							query = query.replaceAll("(?i)limit " + maxRows, "");
							query = query.replaceAll("(?i)where ", "where ROWNUM <= " + maxRows + " and ");
						}
						
					}
					else if(StringUtils.containsIgnoreCase(query, "where ")) {
						query = query.replaceAll("(?i)where ", "where ROWNUM <= " + maxRows + " and ");
					}
					else {
						query = query + " where ROWNUM <= " + maxRows;
					}
				}
				else {
					if (!query.toLowerCase().contains(" limit")){
						query = query + " limit " + maxRows;
						
					}
				}
			}
			
			String toExecute = query.replace("`", "\"");
			
			if(con.getDriverClass().contains("apache.drill")) {
				toExecute = toExecute.replace(" limit " + maxRows, "");
				toExecute = toExecute.replace("\"", "`");
			}
			else if (con.getDriverClass().contains("mariadb")) {
				toExecute = toExecute.replace("\"", "`");
			}
			
			rs = stmt.executeQuery(toExecute);
			rs.last();
			count = rs.getRow();
		}catch(SQLException e){
			e.printStackTrace();
			String errMsg = "SQLException : ";
			while (e != null) {
                
                errMsg += "\n\tErrorCode = "+ e.getErrorCode();
                errMsg += "\n\tSQLState = "+ e.getSQLState();
                errMsg += "\n\tMessage = "+ e.getMessage();
            
                e = e.getNextException();
//                throw e;
            }
			Exception res = new SQLConnectionException("Query " + query + "\n failed .\n" + errMsg );
			throw res;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally{
			if( rs != null){
				rs.close();
			}
			if( stmt != null){
				stmt.close();
			}
			if( con != null){
				returnJdbcConnection(con);
			}
		}
		return count;
	}

	/**
	 * create a table from a query
	 * @param query
	 * @return
	 */
	public ITable createTableFromQuery(/*String name,*/  String query) throws Exception{
		if (query == null){
			throw new Exception("Cannot create a Table from a null query");
		}
		while(query.trim().endsWith(";")){
			query = query.trim().substring(0, query.trim().length() -1);
		}
		
		SQLTable table = new SQLTable(this, SQLTable.QUERY);
		table.setName(query);

		//connect
		String url = "";
		VanillaJdbcConnection con = null;
		ResultSet rs = null;;
		VanillaPreparedStatement stmt = null;
		
		try{
			con = getJdbcConnection();

			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			ResultSetMetaData metadata = rs.getMetaData();
			
			for(int i = 0; i< metadata.getColumnCount(); i++){
				int index = i+1;
				String s = metadata.getSchemaName(index);
				String typeJava = metadata.getColumnClassName(index);
				String tableName = metadata.getTableName(index);
				String shemaName = metadata.getSchemaName(index);
				String label = metadata.getColumnLabel(index);
				String colName = metadata.getColumnName(index);
				
				if (label.equals(colName)){
					if (!label.matches("[a-zA-Z0-9_]*\\.[a-zA-Z0-9_]*") && !label.matches("[a-zA-Z0-9_]*")){
						throw new Exception("The query {" + query + "} contains some columns that are not aliased");
					}
				}
				
//				if(label != null && !label.equals("")) {
//					colName = label;
//				}
//				else {
//					colName = metadata.getColumnName(index);
//				}
				
				
				if (shemaName!= null && !shemaName.trim().equals("")){
					tableName = shemaName  + "." + tableName;
				}
				SQLColumn col = new SQLColumn(table);
				
				if (tableName.equals("") || colName.contains("(")){
					col.setName(colName);
				}else{
					col.setName(tableName + "." + colName );
				}
				
				col.setLabel(label);
				
				col.setClassName(typeJava);
				table.addColumn(col);
				
			}	
	
		}catch(SQLException e){
			e.printStackTrace();
			if( rs != null){
				rs.close();
			}
			if( stmt != null){
				stmt.close();
			}
			if( con != null){
				returnJdbcConnection(con);
			}
			throw new SQLConnectionException(e.getMessage());
		}catch(Exception e){
			throw e;
		}
		finally{
			if( rs != null){
				rs.close();
			}
			if( stmt != null){
				stmt.close();
			}
			if( con != null){
				returnJdbcConnection(con);
			}
		}
		
		
		tables.add(table);
		return table;
	}

	public VanillaPreparedStatement getStreamStatement() throws Exception {
		 // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		try{
			VanillaPreparedStatement jdbcStatement = getJdbcConnection().createStatement(
					java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			try{
				jdbcStatement.setFetchSize(Integer.MIN_VALUE);
			}catch(SQLException ex){
				jdbcStatement.setFetchSize(1);
			}
			
			return jdbcStatement;
		}catch(Exception e){
			e.printStackTrace();
			try{
				System.err.println("-Error when fetching at Integer.MIN_VALUE on JDBC Statement");
				System.err.println("-Try to set Fetch at 1");
				VanillaPreparedStatement jdbcStatement = getJdbcConnection().createStatement(
						java.sql.ResultSet.TYPE_FORWARD_ONLY,
						java.sql.ResultSet.CONCUR_READ_ONLY);
				try{
					jdbcStatement.setFetchSize(Integer.MIN_VALUE);
				}catch(SQLException ex1){
					jdbcStatement.setFetchSize(1);
				}
				
				return jdbcStatement;
			}catch(Exception ex){
				throw new Exception("Unable to create a JDBC STream Statement", ex);
			}
		}
	}


	@Override
	public void configure(Object conf) {
		
	}

	public List<String> getColumnValues(SQLColumn col, HashMap<String, String> parentValues) {
		List<String> result = new ArrayList<String>();
		try{
			
			VanillaJdbcConnection con = getJdbcConnection();
			
			
			VanillaPreparedStatement stmt = con.createStatement();
			
			String q = "select distinct " + col.getName() ;
			if (((SQLTable)col.getTable()).isQuery()){
				q += col.getTable().getName().substring(col.getTable().getName().indexOf(" from "));
			}
			else{
				q += " from " + col.getTable().getName();
			}
			
			if(parentValues != null && ! parentValues.isEmpty()) {
				boolean first = true;
				for(String column : parentValues.keySet()) {
					
					String val = parentValues.get(column);
					
					Class c = String.class;
					try {
						c = col.getJavaClass();				
					} catch(Exception e) {}
					
					if(!Number.class.isAssignableFrom(c)) {
						val = "'" + val + "'";
					}
					
					if(first) {
						q += " where " + column + " = " + val;
						first = false;
					}
					else {
						q += " and " + column + " = " + val;
					}
				}
			}
			
			ResultSet rs =stmt.executeQuery(q);
			
			while(rs.next()){
				if (rs.getString(1)!=null)
					result.add(rs.getString(1));
			}
			
			returnJdbcConnection(con);
		}catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}

	public String getJdbcPrefix() {
		try {
			if(useFullUrl) {
				return getFullUrl().substring(0, getFullUrl().indexOf("//") + 2);
			}
			else {
				return init();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
