package bpm.studio.jdbc.management.config;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;



/**
 * class used to provide resources path for the bpm.metadata.api
 * @author ludo
 *
 */
public final class IConstants {
	private static String driverFile = null; //Platform.getInstallLocation().getURL().toString().substring(6) + "resources/driverjdbc.xml";
	private static String jdbcJarFolder = null; //Platform.getInstallLocation().getURL().toString().substring(6) + "resources/jdbc/";
	private static String resourcesFolder = null;
	private static Properties vanillaConfig;
	
//	
//	public static String driverPath = null; //System.getProperty("user.dir");
//	public static String resourcePath = null;
//	private static Boolean inited = false;
//	
//	public static void init(String resourceFilesPath){
//		synchronized (inited) {
//			if (resourceFilesPath.endsWith(File.separator)) {
//				resourceFilesPath = resourceFilesPath.substring(0, resourceFilesPath.length()-1);
//			}
//			driverPath = resourceFilesPath;
//			resourcePath = resourceFilesPath + File.separator+"resources";
//			driverFile = driverPath + File.separator+"resources"+File.separator+"driverjdbc.xml";
//			jdbcJarFolder = driverPath +File.separator+"resources"+File.separator+"jdbc"+File.separator;
//			
//			inited = true;
//		}
//		
//	}
//	
//	public static boolean isInited(){
//		return inited;
//	}
	
	
	private static void loadVanillaConfig(){
		if (vanillaConfig == null){
			
			
				vanillaConfig = new Properties();
				String p = System.getProperty("bpm.vanilla.configurationFile");
				if (p != null && !p.equals("")){
					Logger.getLogger("bpm.studio.jdbc.management").info("reading Vanilla Config Properties from " + p);
					
					try{
						vanillaConfig.load(new FileInputStream(p));
					}catch(Exception ex){
						Logger.getLogger("bpm.studio.jdbc.management").warn("unable to read Vanilla Config Properties from " + p + " - " + ex.getMessage(), ex);
					}
					
				}
				else{
					Logger.getLogger("bpm.studio.jdbc.management").warn("The system property bpm.vanilla.configurationFile has not been set, the default configuration will be used " );
				}
			}
		
	}
	
	
	/**
	 * if the JdbcDriverXmlFile has not be defined yet,
	 * it loads a Property from properties file specified by the system property bpm.vanilla.configurationFile
	 * then it looks in the loaded properties to the propertyNamed : bpm.vanilla.platform.jdbcDriverXmlFile
	 * 
	 * if the property is not found, the default one will be used : resources/jdbc
	 * 
	 * @return
	 */
	public static String getJdbcDriverXmlFile(){
		if (driverFile == null){
			Logger.getLogger("bpm.studio.jdbc.management").warn("JdbcDriverXmlFile not defined...");
			loadVanillaConfig();

			driverFile = vanillaConfig.getProperty("bpm.vanilla.platform.jdbcDriverXmlFile");
			Logger.getLogger("bpm.studio.jdbc.management").info("JdbcDriverFile set with " + driverFile);
			if (driverFile == null){
				driverFile = "resources/driverjdbc.xml";
				Logger.getLogger("bpm.studio.jdbc.management").warn("JdbcDriverXmlFile not find from vanillaConfiguration properties - inited at " + driverFile);
			}

		}
		
		return driverFile;
	}
	
	/**
	 * if the resourcesFolder has not be defined yet,
	 * it loads a Property from properties file specified by the system property bpm.vanilla.configurationFile
	 * then it looks in the loaded properties to the propertyNamed : bpm.vanilla.platform.resourcesFolder
	 * 
	 * if the property is not found, the default one will be used : resources
	 * 
	 * @return
	 */
	public static String getResourcesFolder(){
		if (resourcesFolder == null){
			Logger.getLogger("bpm.studio.jdbc.management").warn("ResourceFolder not defined...");
			loadVanillaConfig();
	
			resourcesFolder = vanillaConfig.getProperty("bpm.vanilla.platform.resourcesFolder");
			Logger.getLogger("bpm.studio.jdbc.management").info("ResourcesFolder set with " + resourcesFolder);
			if (resourcesFolder == null){
				resourcesFolder = "resources";
				Logger.getLogger("bpm.studio.jdbc.management").warn("ResourcesFolder not find from vanillaConfiguration properties - inited at " + resourcesFolder);
			}
			
		}
		
		return resourcesFolder;
	}
	
	
	
	/**
	 * if the JdbcJarFolder has not be defined yet,
	 * it loads a Property from properties file specified by the system property bpm.vanilla.configurationFile
	 * then it looks in the loaded properties to the propertyNamed : bpm.vanilla.platform.jdbcDriverFolder
	 * 
	 * if the property is not found, the default one will be used : resources/driverjdbc.xml
	 * 
	 * @return
	 */
	public static String getJdbcJarFolder(){
		if (jdbcJarFolder == null){
			Logger.getLogger("bpm.studio.jdbc.management").warn("JdbcJarFolder not defined...");
			loadVanillaConfig();
			

			jdbcJarFolder = vanillaConfig.getProperty("bpm.vanilla.platform.jdbcDriverFolder");
			Logger.getLogger("bpm.studio.jdbc.management").info("JdbcJarFolder set with " + jdbcJarFolder);
			if (jdbcJarFolder == null){
				jdbcJarFolder = "resources/jdbc";
				Logger.getLogger("bpm.studio.jdbc.management").warn("JdbcJarFolder not find from vanillaConfiguration properties - inited at " + jdbcJarFolder);
			}
		}
		
		return jdbcJarFolder;
	}
	
}
