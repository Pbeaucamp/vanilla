package bpm.vanilla.server.reporting.server.tasks.birt;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.model.api.DefaultResourceLocator;
import org.eclipse.birt.report.model.api.IResourceLocator;
import org.eclipse.birt.report.model.api.ModuleHandle;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class BirtResourceLocator implements IResourceLocator {
	
	private DefaultResourceLocator defaultLocator = new DefaultResourceLocator();
	private Logger logger;
	private Map map;
	
	private String resourceFolder;
	
	public BirtResourceLocator(Logger logger, Map map) {
		String resourceFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_BIRT_RESOURCE_PATH);
		
		this.logger = logger;
		this.map = map;
		this.resourceFolder = resourceFolder != null ? (resourceFolder.endsWith("/") ? resourceFolder : resourceFolder + "/") : "";
	}
	
	@Override
	public URL findResource(ModuleHandle moduleHandle, String fileName, int type) {
		URL url = null;
		
		try {
			logger.info("Looking for fileName='" + fileName + "' moduleHandle='" + moduleHandle + "'" +
					" with type='" + type + "");
			
			if(map.get(fileName) != null){
				String filestr = map.get(fileName) + File.separator + fileName;
				
				logger.info("Opening : " + filestr);
				File f = new File(filestr);
				url = f.toURL();
				

				if (url != null) {
					return url;
				}  
				else {
					logger.warn("Could not find resource named : " + fileName + ", trying birt defaults...");
					return defaultLocator.findResource(moduleHandle, fileName, type);	
				}
			}
			else {
				File f = new File(resourceFolder + fileName);
				url = f.toURL();
				
				if (url != null) {
					return url;
				}  
				else {
					logger.warn("Could not find resource named : " + fileName + ", trying birt defaults...");
					return defaultLocator.findResource(moduleHandle, fileName, type);	
				}
			}
		} catch (Exception e) {
			logger.error("Error: " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public URL findResource(ModuleHandle moduleHandle, String fileName,
			int type, Map appContext) {
		logger.info("FindResource with appContext, redirecting");
		return findResource(moduleHandle, fileName, type);
	}

	
}
