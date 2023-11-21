package bpm.metadata.layer.physical.sql;

public class FactorySQLConnection {
	private static FactorySQLConnection instance = null;
	
	private FactorySQLConnection(){}
	
	public static FactorySQLConnection getInstance(){
		if (instance == null){
			instance = new FactorySQLConnection();
		}
		return instance;
	}
	
	/**
	 * create an SQLConnection
	 * schemaName can be null
	 * 
	 * @param driverName
	 * @param host
	 * @param portNumber
	 * @param dataBaseName
	 * @param username
	 * @param password
	 * @param schemaName
	 * @return
	 * @deprecated use the same method with useFullUrl and fullUrl param 
	 * @see #createConnection(String, String, String, String, String, String, String, boolean, String)
	 */
	
	public SQLConnection createConnection(String driverName, String host, String portNumber, 
			String dataBaseName, String username, String password, String schemaName)
		throws SQLConnectionException{
		
		
		
		//check the parameters
		if (driverName == null || driverName.equals("")){
			throw new SQLConnectionException("The SQLConnection DriverName is not set");
		}
		else{
			
		}
		if (dataBaseName == null || dataBaseName.equals("")){
			throw new SQLConnectionException("The SQLConnection DataBaseName is not set");
		}
		if (username == null || username.equals("")){
			throw new SQLConnectionException("The SQLConnection UserName is not set");
		}
		if (password == null){
			throw new SQLConnectionException("The SQLConnection Password is not set");
		}

		
		
		SQLConnection con = new SQLConnection();

		con.setDriverName(driverName);
		con.setHost(host);
		con.setPortNumber(portNumber);
		con.setDataBaseName(dataBaseName);
		con.setUsername(username);
		con.setPassword(password);
		
		return con;
		
	}
	/**
	 * 
	 * @param driverName
	 * @param host
	 * @param portNumber
	 * @param dataBaseName
	 * @return
	 * @throws SQLConnectionException
	 * @deprecated
	 * @see #createConnection(String, String, String, String, boolean, String)
	 */
	public SQLConnection createConnection(String driverName, String host, String portNumber, 
			String dataBaseName) throws SQLConnectionException {
		//check the parameters
		if (driverName == null || driverName.equals("")){
			throw new SQLConnectionException("The SQLConnection DriverName is not set");
		}
//		if (portNumber == null || portNumber.equals("")){
//			throw new SQLConnectionException("The SQLConnection PortNumber is not set");
//		}
		
		SQLConnection con = new SQLConnection();

		con.setDriverName(driverName);
		con.setHost(host);
		con.setPortNumber(portNumber);
		con.setDataBaseName(dataBaseName);
		con.setUsername("");
		con.setPassword("");
		
		return con;

	}
	
	/**
	 * 
	 * @param driverName
	 * @param host
	 * @param portNumber
	 * @param dataBaseName
	 * @param useFullUrl : if the url if fuly specified by the fullUrl otherwise, the url will be rebuild using the regsitered DDriver JDBC info 
	 * from the driverjdbc.xml file
	 * @param fullUrl
	 * @return
	 * @throws SQLConnectionException
	 */
	public SQLConnection createConnection(String driverName, String host, String portNumber, 
			String dataBaseName, boolean useFullUrl, String fullUrl) throws SQLConnectionException {
		//check the parameters
		if (driverName == null || driverName.equals("")){
			throw new SQLConnectionException("The SQLConnection DriverName is not set");
		}
//		if (portNumber == null || portNumber.equals("")){
//			throw new SQLConnectionException("The SQLConnection PortNumber is not set");
//		}
		
		SQLConnection con = new SQLConnection();

		con.setDriverName(driverName);
		con.setHost(host);
		con.setPortNumber(portNumber);
		con.setDataBaseName(dataBaseName);
		con.setUsername("");
		con.setPassword("");
		con.setUseFullUrl(useFullUrl);
		con.setFullUrl(fullUrl);
		
		return con;

	}
	
	
	
	
	public SQLConnection createConnection(String driverName, String host, String portNumber, 
			String dataBaseName, String username, String password, String schemaName, boolean useFullUrl, String fullUrl)
		throws SQLConnectionException{
		
		
		
		//check the parameters
		if (driverName == null || driverName.equals("")){
			throw new SQLConnectionException("The SQLConnection DriverName is not set");
		}
//		if (portNumber == null || portNumber.equals("")){
//			throw new SQLConnectionException("The SQLConnection PortNumber is not set");
//		}
		else{
//			try{
//				Integer.parseInt(portNumber);
//			}catch(NumberFormatException e){
//				throw new SQLConnectionException("The SQLConnection PortNumber is not valid");
//			}
			
		}
		if ((dataBaseName == null || dataBaseName.equals("")) && !useFullUrl){
			throw new SQLConnectionException("The SQLConnection DataBaseName is not set");
		}
		if (useFullUrl && fullUrl == null){
			throw new SQLConnectionException("The SQLConnection FullUrl is not set");
		}
//		if (username == null || username.equals("")){
//			throw new SQLConnectionException("The SQLConnection UserName is not set");
//		}
//		if (password == null){
//			throw new SQLConnectionException("The SQLConnection Password is not set");
//		}

		
		
		SQLConnection con = new SQLConnection();

		con.setDriverName(driverName);
		con.setHost(host);
		con.setPortNumber(portNumber);
		con.setDataBaseName(dataBaseName);
		con.setUsername(username);
		con.setPassword(password);
		con.setUseFullUrl(useFullUrl);
		con.setFullUrl(fullUrl);
//		con.setSchemaName(schemaName);
		
		return con;
		
	}
}
