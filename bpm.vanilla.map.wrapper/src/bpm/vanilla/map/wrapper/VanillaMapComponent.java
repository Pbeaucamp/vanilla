package bpm.vanilla.map.wrapper;



import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import bpm.vanilla.map.core.design.IFactoryModelProvider;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.map.core.design.IOpenGisMapService;
import bpm.vanilla.map.core.design.IVanillaMapDaoProvider;
import bpm.vanilla.map.core.design.fusionmap.IFactoryFusionMap;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.map.core.design.kml.IFactoryKml;
import bpm.vanilla.map.core.design.kml.IKmlRegistry;
import bpm.vanilla.map.dao.VanillaMapDaoProviderComponent;
import bpm.vanilla.map.model.VanillaMapFactoryModelProviderComponent;
import bpm.vanilla.map.wrapper.servlets.FusionMapObjectServlet;
import bpm.vanilla.map.wrapper.servlets.KmlObjectServlet;
import bpm.vanilla.map.wrapper.servlets.MapModelServlet;
import bpm.vanilla.map.wrapper.servlets.OpenGisMapServlet;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

public class VanillaMapComponent {

	public static final String PLUGIN_ID = "bpm.vanilla.map.wrapper";
	
	
	
	private HttpService httpService;
	private IFactoryModelProvider factoriesProvider;
	private IVanillaMapDaoProvider daoProvider;
	
	private IVanillaLoggerService loggingService = null;
	
	public VanillaMapComponent(){
		
	}
	
	public void bindLoggingService(IVanillaLoggerService service){
		this.loggingService = service;
		getLogger().info("Vanilla Logging Service binded");
	}
	
	public void unbindLoggingService(IVanillaLoggerService service){
		getLogger().info("unbinding Vanilla Logging Service");
		this.loggingService = null;
		
		
	}
	
	public IVanillaLogger getLogger(){
		return loggingService.getLogger(PLUGIN_ID);
	}

	
	private void registeringServlets() throws Exception{
		

		try{
			httpService.registerResources("/fusionMap/Maps", daoProvider.getFusionMapRegistry().getMapFolderLocation(), new SwfHttpContext(this));
			getLogger().info("/fusionMap/Maps alias for " + daoProvider.getFusionMapRegistry().getMapFolderLocation());
		}catch(Exception ex){
			getLogger().error("Error when registrating /fusionMap/Maps  alias within HttpService\n" + ex.getMessage(), ex);
			throw ex;
		}
		
		
		
		try{
			httpService.registerResources("/Kml", daoProvider.getKmlRegistry().getKmlFolderLocation(), new SwfHttpContext(this));
			getLogger().info("/Kml alias for " + daoProvider.getKmlRegistry().getKmlFolderLocation());
		}catch(Exception ex){
			getLogger().error("Error when registrating /Kml alias within HttpService\n" + ex.getMessage(), ex);
			throw ex;
		}
		
		try{
			httpService.registerServlet("/vanillaMapDefinition", new MapModelServlet(this), null, null);
			getLogger().info("HttpService registered Servlet at /vanillaMapDefinition");
		}catch(Exception ex){
			getLogger().error("Error when registrating /vanillaMapDefinition  Servlet within HttpService\n" + ex.getMessage(), ex);
			throw ex;
		}
		
		try{
			httpService.registerServlet("/fusionMap/fusionMapRegistry", new FusionMapObjectServlet(this), null, null);
			getLogger().info("HttpService registered Servlet at /fusionMap/fusionMapRegistry");
		}catch (Exception e) {
			getLogger().error("Error when registrating /fusionMap/fusionMapRegistry  Servlet within HttpService\n" + e.getMessage(), e);
			throw e;
		}
		
		try{
			httpService.registerServlet("/Kml/kmlFileRegistry", new KmlObjectServlet(this), null, null);
			getLogger().info("HttpService registered Servlet at /Kml/kmlFileRegistry");
		}catch (Exception e) {
			getLogger().error("Error when registrating /Kml/kmlFileRegistry Servlet within HttpService\n" + e.getMessage(), e);
			throw e;
		}

		try{
			httpService.registerServlet("/openGisMapServlet", new OpenGisMapServlet(this), null, null);
			getLogger().info("HttpService registered Servlet at /openGisMapServlet");
		}catch (Exception e) {
			getLogger().error("Error when registrating /openGisMapServlet Servlet within HttpService\n" + e.getMessage(), e);
			throw e;
		}			
	}
	
	
	private void unregisterServlets(){
		httpService.unregister("/fusionMap/Maps");
		getLogger().info("Alias /fusionMap/Maps unregistered from HttpService");
		
		httpService.unregister("/Kml");
		getLogger().info("Alias /Kml unregistered from HttpService");
		
		httpService.unregister("/vanillaMapDefinition");
		getLogger().info("Alias /vanillaMapDefinition unregistered from HttpService");
		
		httpService.unregister("/fusionMap/fusionMapRegistry");
		getLogger().info("Alias /fusionMap/fusionMapRegistry unregistered from HttpService");
		
		httpService.unregister("/Kml/kmlFileRegistry");
		getLogger().info("Alias /Kml/kmlFileRegistry unregistered from HttpService");
		
		httpService.unregister("/fusionMap/Maps");
		getLogger().info("Alias /fusionMap/Maps unregistered from HttpService");	
		
		httpService.unregister("/Kml/kmlFileRegistry");
		getLogger().info("Alias /Kml/kmlFileRegistry unregistered from HttpService");
		
		httpService.unregister("/openGisMapServlet");
		getLogger().info("Alias /openGisMapServlet unregistered from HttpService");
	}
	
	
	public void bindHttpService(HttpService service){
		getLogger().info("Binding Http service ...");
		this.httpService = service;

		
		getLogger().info("Binded Http service");
	}
	public void unbindHttpService(HttpService service){
		unregisterServlets();
		this.httpService = null;
		getLogger().debug("HttpService unbinded");
	}
	

	
	
	

	


	
	
	public IKmlRegistry getKmlRegistry() throws Exception{
		return daoProvider.getKmlRegistry();
	}
	
	public IMapDefinitionService getMapDefinitionDao() throws Exception{
		return daoProvider.getDefinitionService();
		
	}
	
	public IFactoryFusionMap getFactoryFusionMap() throws Exception{
		return factoriesProvider.getFactoryFusionMap();
		
	}
	
	public IFactoryKml getFactoryKml() throws Exception{
		return factoriesProvider.getFactoryKml();
		
	}


	public IOpenGisMapService getOpenGisService() throws Exception {
		return daoProvider.getOpenGisMapService();
	}


	public IFusionMapRegistry getFusionMapRegistry()throws Exception {
		return daoProvider.getFusionMapRegistry();
	}

	
	public void activate(ComponentContext ctx) throws Exception{
		factoriesProvider = new VanillaMapFactoryModelProviderComponent();
		try{
			daoProvider = new VanillaMapDaoProviderComponent();
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Unable to create VanillaMapDao - " + ex.getMessage(), ex);
			throw ex;
		}
		
		try{
			registeringServlets();
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Unable to register Servlets");
			throw ex;
		}
		
		Logger.getLogger(getClass()).info("VanillaMap Component activated");
	}
	
	public void desactivate(ComponentContext ctx){
		if (httpService != null){
			unregisterServlets();
		}
		factoriesProvider = null;
		daoProvider = null;
		Logger.getLogger(getClass()).info("VanillaMap Component desactivated");
	}
	
}
