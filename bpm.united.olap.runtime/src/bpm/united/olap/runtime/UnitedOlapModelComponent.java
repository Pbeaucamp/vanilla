package bpm.united.olap.runtime;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import bpm.united.olap.runtime.service.ModelService;

public class UnitedOlapModelComponent extends ModelService {

	public UnitedOlapModelComponent(){
		
	}
	
	
	public void activated(ComponentContext ctx) throws Exception{
		
		String configFile = System.getProperty("bpm.vanilla.configurationFile");
		
		if (configFile == null){
			throw new RuntimeException("The System Property bpm.vanilla.configurationFile is missing!");
		}
		
		Properties p = new Properties();
		
		try{
			p.load(new FileInputStream(configFile));
		}catch(Exception ex){
			throw new RuntimeException("Unable to read vanilla Properties file " + configFile);
		}
		
		String vanillaUrl = p.getProperty("bpm.vanilla.server.url");
		
		if (vanillaUrl == null){
			Logger.getLogger(getClass()).error("Missing Property bpm.vanilla.server.url within " + configFile);
			throw new RuntimeException("The bpm.vanilla.server.url property is missing within configurationFile " + configFile);
		}
		setVanillaUrl(vanillaUrl);
		
		Logger.getLogger(getClass()).info("UnitedOlapModelComponent inited for vanillaRuntimeUrl=" + vanillaUrl);
		
	}
	
}
