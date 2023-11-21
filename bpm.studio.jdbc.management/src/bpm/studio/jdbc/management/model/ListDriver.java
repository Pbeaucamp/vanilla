package bpm.studio.jdbc.management.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


;

/**
 * This class is a static one that contains all the JDBC drivers informations
 * contained by the driverJdbc.xml file
 * @author LCA
 *
 */
public class ListDriver {
	public static final String MS_ACCESS = "MSAccess Database";
	public static final String MS_XLS = "XLS Database";
	private HashMap<String, DriverInfo> drivers = new HashMap<String, DriverInfo>();

	private static ListDriver instance = null;
	private String jdbcXmlFilePath;
	
	public static ListDriver getInstance(String jdbcXmlFilePath) throws Exception{
		if (instance == null){
			instance = new ListDriver(jdbcXmlFilePath);
			
			
//			DriverInfo msAccess = new DriverInfo();
//			msAccess.setName(MS_ACCESS);
//			msAccess.setClassName("sun.jdbc.odbc.JdbcOdbcDriver");
//			msAccess.setUrlPrefix("jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=");
//			instance.addDriver(msAccess, false);
//			
//			DriverInfo msXls = new DriverInfo();
//			msXls.setName(MS_XLS);
//			msXls.setClassName("sun.jdbc.odbc.JdbcOdbcDriver");
//			msXls.setUrlPrefix("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ=");
//			instance.addDriver(msXls, false);
			
		}
		
		return instance;
	}
	
	
	
	private ListDriver(String jdbcXmlFilePath) throws Exception {
		DigesterDBXML dig;
		
		String jarPath = null;
		int p = jdbcXmlFilePath.lastIndexOf("/");
		if (jdbcXmlFilePath.substring(p + 1).contains("\\")){
			jarPath = jdbcXmlFilePath.substring(0, jdbcXmlFilePath.lastIndexOf("\\"));
		}
		else{
			jarPath = jdbcXmlFilePath.substring(0, p);
		}
		
		try {
			dig = new DigesterDBXML(jdbcXmlFilePath , jarPath+ "/jdbc/");
			for(DriverInfo d : dig.getListDriver()){
				addDriver(d, false);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}
		this.jdbcXmlFilePath = jdbcXmlFilePath;
	}
	
	public void addDriver(DriverInfo d, boolean save)throws Exception{
		drivers.put(d.getName(), d);
//		Log.info("Driver " + d.getName() + " found in driverjdbc.xml");
		
		if (save){
			save(this.jdbcXmlFilePath);
		}
		
	}
	
	
	private void save(String path) throws Exception{
		Document doc = DocumentHelper.createDocument();
		Element root  = doc.addElement("drivers");
		
		for(DriverInfo i : drivers.values()){
			Element e = root.addElement("driver");
			e.addAttribute("name", i.getName()).addAttribute("className", i.getClassName()).addAttribute("file", i.getFile()).addAttribute("prefix", i.getUrlPrefix());
		}
		
		try{
			XMLWriter w = new XMLWriter(new FileOutputStream(path), OutputFormat.createPrettyPrint());
			w.write(doc);
			w.close();
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error when saving driverjdbc.xml file : " + ex.getMessage(), ex);
		}
		
	}
	
	/**
	 * return the DriverInfo object matching on Key
	 * @param s
	 * @return
	 */
	public DriverInfo getInfo(String key){
		return drivers.get(key);
	}
	
	/**
	 * return the list of all registerd driver
	 * @return
	 */
	public Collection<String> getDriversName(){
		return drivers.keySet();
	}
	
	
	public Collection<DriverInfo> getDriversInfo(){
		return drivers.values();
	}



	public void removeDriver(DriverInfo info) throws Exception {
		for(String key : drivers.keySet()){
			if (drivers.get(key) == info){
				drivers.remove(key);
				break;
			}
		}
		save(this.jdbcXmlFilePath);
	}
}
