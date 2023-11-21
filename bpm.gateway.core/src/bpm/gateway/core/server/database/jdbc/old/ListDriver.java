package bpm.gateway.core.server.database.jdbc.old;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;

;

/**
 * This class is a static one that contains all the JDBC drivers informations
 * contained by the driverJdbc.xml file
 * @author LCA
 *
 */
public class ListDriver {
//	public static final String MS_ACCESS = "MSAccess Database";
//	public static final String MS_XLS = "XLS Database";
//	private HashMap<String, DriverInfo> drivers = new HashMap<String, DriverInfo>();
//
//	private static ListDriver instance = null;
//	
//	public static ListDriver getInstance(String jdbcXmlFilePath) throws Exception{
//		if (instance == null){
//			instance = new ListDriver(jdbcXmlFilePath);
//			
//			
//			DriverInfo msAccess = new DriverInfo();
//			msAccess.setName(MS_ACCESS);
//			msAccess.setClassName("sun.jdbc.odbc.JdbcOdbcDriver");
//			msAccess.setUrlPrefix("jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=");
//			instance.addDriver(msAccess);
//			
//			DriverInfo msXls = new DriverInfo();
//			msXls.setName(MS_XLS);
//			msXls.setClassName("sun.jdbc.odbc.JdbcOdbcDriver");
//			msXls.setUrlPrefix("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ=");
//			instance.addDriver(msXls);
//			
//		}
//		
//		return instance;
//	}
//	
//	
//	
//	private ListDriver(String jdbcXmlFilePath) throws Exception {
//		DigesterDBXML dig;
//		try {
//			dig = new DigesterDBXML(jdbcXmlFilePath);
//			for(DriverInfo d : dig.getListDriver()){
//				addDriver(d);
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			throw e;
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			throw e;
//		}
//		
//	}
//	
//	public void addDriver(DriverInfo d){
//		drivers.put(d.getName(), d);
////		Log.info("Driver " + d.getName() + " found in driverjdbc.xml");
//	}
//	
//	/**
//	 * return the DriverInfo object matching on Key
//	 * @param s
//	 * @return
//	 */
//	public DriverInfo getInfo(String key){
//		return drivers.get(key);
//	}
//	
//	/**
//	 * return the list of all registerd driver
//	 * @return
//	 */
//	public Collection<String> getDriversName(){
//		return drivers.keySet();
//	}
}
