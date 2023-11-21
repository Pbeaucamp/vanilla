package bpm.united.olap.runtime;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.engine.IOlapRunner;
import bpm.united.olap.runtime.engine.IUnitedOlapContentManager;
import bpm.united.olap.runtime.engine.OlapRunner;
import bpm.united.olap.runtime.service.RuntimeService;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

public class UnitedOlapRuntimeComponent extends RuntimeService {

	public static final String ID = "bpm.united.olap.runtime.RuntimeService";	
	
	private IVanillaLoggerService loggerService;
	private ICacheServer cacheServer;
	private IUnitedOlapContentManager contentManager ;
	
	
	public void bind(IModelService service){
		this.contentManager = (IUnitedOlapContentManager)service;
		getLogger().info("Binded IUnitedOlapContentManager");
	}
	
	public void unbind(IModelService service){
		this.contentManager = null;
		getLogger().info("Unbinded IUnitedOlapContentManager");
	}
	
	public IUnitedOlapContentManager getContentManager(){
		return contentManager;
	}
	
	public UnitedOlapRuntimeComponent(){
		
	}
	
	public  IVanillaLogger getLogger(){
		return loggerService.getLogger(ID);
	}
	
	public void bind(ICacheServer service){
		cacheServer = service;
		getLogger().info("Binded ICacheServer");
	}
	
	public void unbind(ICacheServer service){
		cacheServer = null;
		getLogger().info("UnBinded ICacheServer");
	}
	
	public void bind(IVanillaLoggerService service){
		loggerService = service;
		getLogger().info("Binded IVanillaLoggerService");
	}
	
	public void unbind(IVanillaLoggerService service){
		loggerService = null;
		getLogger().info("UnBinded IVanillaLoggerService");
	}
	
	public void activated(ComponentContext ctx){
//		
//		
//		component = this;
//		UnitedOlapContentManager.getInstance().setComponent(this, cacheServer);
	}
	
	

	public  ICacheServer getCacheServer() {
		return cacheServer;
	}

	@Override
	public void preload(String mdx, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception{
		Logger.getLogger(getClass()).info("Preloading " + schemaId + " for group " + runtimeContext.getGroupName() + " " + mdx );
		ICubeInstance cubeInstance = getContentManager().getCubeInstance(schemaId, cubeName,runtimeContext);
		IOlapRunner runner = null;
		IObjectIdentifier id = null;
		try{
			id = getContentManager().getSchemaObjectIdentifier(schemaId);
		}catch(Exception ex){
			
		}
		runner = new OlapRunner(id, cubeInstance, getLogger(), getCacheServer(), runtimeContext);
				
		runner.executeQuery(mdx, true);
		Logger.getLogger(getClass()).debug("Preload done");
	}

	@Override
	/*
	 * nothing change on the runtime side, only the remote layer will
	 * have a different behavior
	 */
	public OlapResult executeQueryForFmdt(String mdx, String schemaId,
			String cubeName, boolean computeDatas,
			IRuntimeContext runtimeContext) throws Exception {
		
		return executeQuery(mdx, schemaId, cubeName, computeDatas, runtimeContext);
	}
}
