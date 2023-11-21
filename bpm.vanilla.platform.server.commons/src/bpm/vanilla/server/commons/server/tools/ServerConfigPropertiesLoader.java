package bpm.vanilla.server.commons.server.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author ludo
 *
 */
public class ServerConfigPropertiesLoader {

	/**
	 * load all file *.properties from path in the returned object
	 * @param path
	 * @return
	 */
	public static Properties loadFileProperties(String path){
		File f = new File(path);
		
		Properties props = new Properties();
		if (f.isDirectory()){
			
			for(String fName : f.list()){
				
				if (fName.toLowerCase().endsWith(".properties")){
					
					
					try { 
						Properties p = new Properties();
						p.load(new FileInputStream(new File(f, fName))); 
						
						
						for(Object o : p.keySet()){
							props.setProperty((String)o, p.getProperty((String)o));
						}
						
					} 
					catch (IOException e) { 
						e.printStackTrace();
						
					} 
					
				}
				
			}
			
		
			
			
		}
		return props;
	}
}
