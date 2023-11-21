package bpm.studio.jdbc.management.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

//import bpm.studio.jdbc.management.Activator;


/**
 * This class read an XML stream that contains informations about jdbc files
 * 
 * @author LCA
 *
 */
public class DigesterDBXML {
	private List<DriverInfo> list;
	private Document doc;
	private Logger logger = Logger.getLogger(DigesterDBXML.class.getName());
	
	private String jdbcFolderPath; 
	public DigesterDBXML(String path, String jdbcFolderPath) throws FileNotFoundException, Exception {
				
		this.jdbcFolderPath = jdbcFolderPath;
		doc = DocumentHelper.parseText(IOUtils.toString(new FileInputStream(path), "UTF-8"));
		try
		{
			list = parse();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			//System.out.println("NB Dims = " + schema.getDimensions().size());
		}
	}
	

	
	private List<DriverInfo> parse(){
		List<DriverInfo> list = new ArrayList<DriverInfo>();
		Element root = doc.getRootElement();
		
		
		for(Element e : (List<Element>)root.elements("driver")){
			String prefix = null;
			if (e.attribute("prefix") != null){
				prefix = e.attributeValue("prefix");
			}
			if (prefix == null){
				logger.warning("Missing prefix in jdbcdriver xml entry -> entry skipped");
				continue;
			}
			String name = null;
			if (e.attribute("name") != null){
				name = e.attributeValue("name");
			}
			if (name == null){
				logger.warning("Missing name in jdbcdriver xml entry -> entry skipped");
				continue;
			}
			
			String className = null;
			if (e.attribute("className") != null){
				className = e.attributeValue("className");
			}
			if (className == null){
				logger.warning("Missing className in jdbcdriver xml entry -> entry skipped");
				continue;
			}
			
			String file = null;
			if (e.attribute("file") != null){
				file = e.attributeValue("file");
			}
			if (className == null){
				logger.warning("Missing file in jdbcdriver xml entry -> entry skipped");
				continue;
			}
			
			if (!new File(jdbcFolderPath + "/" + file).exists()){
				logger.warning("The specified file " + file + " for driver " + name + " do not exists -> entry skipped");
				continue;
			}
			
			DriverInfo info = new DriverInfo();
			info.setClassName(className);
			info.setFile(file);
			info.setName(name);
			info.setUrlPrefix(prefix);
			list.add(info);
		}
		return list;
		
	}
	
	public List<DriverInfo> getListDriver() {
		return list;
	}
}
