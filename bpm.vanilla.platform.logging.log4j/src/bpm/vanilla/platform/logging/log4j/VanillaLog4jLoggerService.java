package bpm.vanilla.platform.logging.log4j;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;



public class VanillaLog4jLoggerService implements IVanillaLoggerService{
	private List<VanillaLog4jLogger> loggers = Collections.synchronizedList(new ArrayList<VanillaLog4jLogger>());
	
	
	
	@Override
	public IVanillaLogger getLogger(String loggerName) {
		
		VanillaLog4jLogger logger = null;
		
		synchronized (loggers) {
			for(VanillaLog4jLogger l : loggers){
				if (l.getName().equals(loggerName)){
					logger = l;
					break;
				}
			}
		}
		
		
		if (logger == null){
			logger = new VanillaLog4jLogger(loggerName);
			loggers.add(logger);
		}
		
		return logger;
	}



	@Override
	public void configure(Object configuration) {
				
	}

	public void activate(ComponentContext context){
		boolean vanillaConf = false;
		boolean vanillaLog = false;
		boolean loadFailed = false;
		Properties p = new Properties();
		
		if (System.getProperty("bpm.vanilla.configurationFile") != null){
			vanillaConf = true;
			try{
				FileInputStream fis = new FileInputStream(System.getProperty("bpm.vanilla.configurationFile"));
				
				
				p.load(fis);
				
				if (p.getProperty("bpm.log4j.configurationFile") != null){
					vanillaLog = true;
					try{
						DOMConfigurator.configure(p.getProperty("bpm.log4j.configurationFile"));
						
						Logger.getLogger(context.getBundleContext().getBundle().getSymbolicName()).info("Log4j inited with file " + p.getProperty("bpm.log4j.configurationFile"));
					}catch(Exception ex){
						loadFailed = true;
						throw ex;
					}
				}
				return;
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
		}
		if (System.getProperty("log4j.configuration") == null){
			
			StringBuffer errorMessage = new StringBuffer();
			
			errorMessage.append("Log4j has not been rightly inited\n");
			if (!vanillaConf){
				errorMessage.append("The property bpm.vanilla.configurationFile has not been set.\n");
			}
			if (!vanillaLog){
				errorMessage.append("The property bpm.vanilla.configurationFile has not been set whithin the vanillaConfigurationFile.\n");
			}
			if (loadFailed){
				errorMessage.append("An error occured when trying to load the log4j configurationFile :" + p.getProperty("bpm.log4j.configurationFile") + "\n");
			}
			Logger.getLogger(context.getBundleContext().getBundle().getSymbolicName()).warn(errorMessage.toString());
		}
		else{
			Logger.getLogger(context.getBundleContext().getBundle().getSymbolicName()).info("using the property log4j.configuration=" + System.getProperty("log4j.configuration")+" to init log4j." );
		}
	}
	
	public void desactivate(ComponentContext context){
		
	}
}
