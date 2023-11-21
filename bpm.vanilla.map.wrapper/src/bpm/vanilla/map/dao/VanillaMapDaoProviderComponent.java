package bpm.vanilla.map.dao;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.map.core.design.IOpenGisMapService;
import bpm.vanilla.map.core.design.IVanillaMapDaoProvider;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.map.core.design.kml.IKmlRegistry;
import bpm.vanilla.map.dao.fusionmap.FusionMapRegistry;
import bpm.vanilla.map.dao.kml.KmlFileRegistry;
import bpm.vanilla.map.dao.managers.DatabaseMapDefinitionService;
import bpm.vanilla.map.dao.opengis.OpenGisMapDAO;

public class VanillaMapDaoProviderComponent implements IVanillaMapDaoProvider{
	
	public static String PLUGIN_ID = "bpm.vanilla.map.dao";
	
	
	public static final String BEAN_DEFINITION_MANAGER_ID = "mapDefinitionManager";
	public static final String BEAN_FUSIONMAP_REGISTRY_ID = "fusionMapRegistry";
	public static final String BEAN_KML_REGISTRY_ID = "kmlFileRegistry";
	public static final String BEAN_OPENGIS = "openGisDAO";
	
	
	private DatabaseMapDefinitionService mapDefinitionManager = null;
	private FusionMapRegistry fusionMapRegistry = null;
	private KmlFileRegistry kmlRegistry = null;
	private OpenGisMapDAO openGisDAO;
	
	
	public VanillaMapDaoProviderComponent() throws Exception{
		init();
	}
	
	public void desactivate(ComponentContext ctx) {
		mapDefinitionManager.stop();
		mapDefinitionManager = null;
		
		fusionMapRegistry.stop();
		fusionMapRegistry = null;
		
		kmlRegistry.stop();
		kmlRegistry = null;
		
		openGisDAO.stop();
		openGisDAO = null;
	}
	
	
	public IMapDefinitionService getDefinitionService()throws Exception{
		return mapDefinitionManager;
	}
	
	public IFusionMapRegistry getFusionMapRegistry()throws Exception{
		return fusionMapRegistry;
	}
	
	public IKmlRegistry getKmlRegistry()throws Exception{
		return kmlRegistry;
	}
	
	private synchronized void init() throws Exception{
		
		ClassPathResource configFile = new ClassPathResource("/bpm/vanilla/map/dao/bpm.vanilla.map.context.xml", VanillaMapDaoProviderComponent.class.getClassLoader());
		XmlBeanFactory factory = new XmlBeanFactory(configFile);
		
		try{
			PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
			cfg.setLocation(new FileSystemResource(System.getProperty("bpm.vanilla.configurationFile")));

			cfg.postProcessBeanFactory(factory);
			
		}catch(Exception ex){
			ex.printStackTrace();
			Logger.getLogger(getClass()).warn("Error when using PropertyPlaceHolder on bpm.vanilla.configurationFile=" + System.getProperty("bpm.vanilla.configurationFile"), ex);
		}
		
		try{
			mapDefinitionManager = (DatabaseMapDefinitionService)factory.getBean(BEAN_DEFINITION_MANAGER_ID);
			fusionMapRegistry = (FusionMapRegistry)factory.getBean(BEAN_FUSIONMAP_REGISTRY_ID);
			kmlRegistry = (KmlFileRegistry)factory.getBean(BEAN_KML_REGISTRY_ID);
			openGisDAO = (OpenGisMapDAO)factory.getBean(BEAN_OPENGIS);
			Logger.getLogger(getClass()).info("Spring Beans loaded");
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Error when getting Dao bspring beans", ex);
			throw ex;
		}
	}




	@Override
	public IOpenGisMapService getOpenGisMapService() throws Exception {
		return openGisDAO;
	}
	
}
