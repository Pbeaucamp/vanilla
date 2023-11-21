package bpm.workflow.runtime.resources.servers;

import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * DataBase server
 * @author CHARBONNIER, MARTIN
 *
 */
public class DataBaseServer extends Server {

	private String dataBaseName;
	private String jdbcDriver;
	private String schemaName;
	private String port;
	
	/**
	 * do not use, only for XML parsing
	 */
	public DataBaseServer(){}
	
	/**
	 * Create a database
	 * @param prop : url,username,password,jdbcDriver,name,dataBaseName,schemaName,port
	 */
	protected DataBaseServer(Properties prop){
		setUrl(prop.getProperty("url"));
		setLogin(prop.getProperty("username"));
		setPassword(prop.getProperty("password"));
		setJdbcDriver(prop.getProperty("jdbcDriver"));
		setName(prop.getProperty("name"));
		setDataBaseName(prop.getProperty("dataBaseName"));
		setSchemaName(prop.getProperty("schemaName"));
		setPort(prop.getProperty("port"));
		
	}
	
	/**
	 * 
	 * @return the database name
	 */
	public String getDataBaseName() {
		return dataBaseName;
	}
	/**
	 * Set the database name
	 * @param dataBaseName
	 */
	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}
	/**
	 * 
	 * @return the jdbc Driver
	 */
	public String getJdbcDriver() {
		return jdbcDriver;
	}
	/**
	 * Set the jdbc Driver
	 * @param jdbcDriver
	 */
	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}
	/**
	 * 
	 * @return the schema name
	 */
	public String getSchemaName() {
		return schemaName;
	}
	/**
	 * Set the schema name
	 * @param schemaName
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	/**
	 * 
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	/**
	 * Set the port
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	public List<Element> toXPDL() {
		List<Element> list = super.toXPDL();
		
		Element jdbcDriver =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_jdbcDriver").addAttribute("Name",  getId() + "_jdbcDriver");
		jdbcDriver.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
		jdbcDriver.addElement("InitialValue").setText(getJdbcDriver());

		
		Element dataBaseName =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_dataBaseName").addAttribute("Name",  getId() + "_dataBaseName");
		dataBaseName.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
		dataBaseName.addElement("InitialValue").setText(getDataBaseName());
		
		Element port =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_port").addAttribute("Name",  getId() + "_port");
		port.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
		port.addElement("InitialValue").setText(getPort());

		list.add(dataBaseName);
		list.add(jdbcDriver);
		list.add(port);
		
		if (this.schemaName != null && !this.schemaName.trim().equals("")){
			Element schemaName =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_schemaName").addAttribute("Name",  getId() + "_schemaName");
			schemaName.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
			schemaName.addElement("InitialValue").setText(getSchemaName());

			list.add(schemaName);
		}
		

		return list;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("dataBaseServer");
		if(!dataBaseName.equalsIgnoreCase("")){
			e.addElement("dataBaseName").setText(dataBaseName);
		}
		if(!jdbcDriver.equalsIgnoreCase("")){
		e.addElement("jdbcDriver").setText(jdbcDriver);
		}
		if (schemaName != null){
			e.addElement("schemaName").setText(schemaName);
		}
		if (port != null) {
			e.addElement("port").setText(port);
		}
		
		return e;
	}
	
	
//	public List<ProfilingQueryObject> getProfilingQueries() throws WorkflowException{
//		List<ProfilingQueryObject> list = new ArrayList<ProfilingQueryObject>();
//		
//		Properties conParameters = new Properties();
//		conParameters.put("", getUrl());
//		conParameters.put("driverClassName", getJdbcDriver());
//		conParameters.put("username", getLogin());
//		conParameters.put("password", getPassword());
//		
//		try {
//			Helper helper = Helper.getInstance(/*conParameters*/);
//
//			return list;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new WorkflowException("Error while wonnecting to the Profiling DataBase", e);
//		}
//		
//
//	}
//	
//	
//	public ProfilingQueryObject getProfilingObject(int queryId) throws WorkflowException{
//		Properties conParameters = new Properties();
//		conParameters.put("", getUrl());
//		conParameters.put("driverClassName", getJdbcDriver());
//		conParameters.put("username", getLogin());
//		conParameters.put("password", getPassword());
//		
//		
//		try {
//			Helper helper = Helper.getInstance(/*conParameters*/);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new WorkflowException("Error while wonnecting to the Profiling DataBase", e);
//		}
//		throw new WorkflowException("Unable to find the ProfilingQUery with the given id");
//		
//	}


}
