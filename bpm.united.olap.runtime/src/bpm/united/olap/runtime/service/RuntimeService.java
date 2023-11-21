package bpm.united.olap.runtime.service;

import org.apache.log4j.Logger;

import bpm.united.olap.api.communication.IRuntimeService;
import bpm.united.olap.api.data.IExternalQueryIdentifier;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.ModelFactory;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.impl.DateLevel;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.impl.ValueResultCell;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.engine.IOlapRunner;
import bpm.united.olap.runtime.engine.IUnitedOlapContentManager;
import bpm.united.olap.runtime.engine.OlapRunner;
import bpm.united.olap.runtime.projection.IProjectionCreator;
import bpm.united.olap.runtime.projection.ProjectionCreatorFactory;
import bpm.united.olap.runtime.projection.ProjectionDescriptor;
import bpm.united.olap.runtime.query.FactoryQueryHelper;
import bpm.united.olap.runtime.query.IQueryHelper;
import bpm.united.olap.runtime.tools.DimensionUtils;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.logging.IVanillaLogger;


public abstract class RuntimeService implements IRuntimeService {

//	protected UnitedOlapRuntimeComponent component;
	
	
	private boolean isInStreamMode;
	

	abstract public IVanillaLogger getLogger();
	abstract public ICacheServer getCacheServer();
	
	abstract public IUnitedOlapContentManager getContentManager();
	
	@Override
	public OlapResult executeQuery(String mdx, String schemaId, String cubeName, boolean computeDatas, IRuntimeContext runtimeContext) throws Exception {

//		IUnitedOlapContentManager ctMgr = UnitedOlapContentManager.getInstance();
		ICubeInstance cubeInstance = getContentManager().getCubeInstance(schemaId, cubeName,runtimeContext);
		IOlapRunner runner = null;
		IObjectIdentifier id = null;
		
		try{
			id = getContentManager().getSchemaObjectIdentifier(schemaId);
		}catch(Exception ex){
			Logger.getLogger(getClass()).warn("Could not find the ObjectIdetifier for schema " + schemaId + " - " + ex.getMessage(), ex);
		}
		runner = new OlapRunner(id, cubeInstance, getLogger(), getCacheServer(), runtimeContext);
		runner.setSqlStreamMode(isInStreamMode);

		
		return runner.executeQuery(mdx, computeDatas);
	}

	@Override
	public OlapResult drillthrough(ValueResultCell cell, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception {

		Schema schema = getContentManager().getSchema(schemaId);
		ICubeInstance cubeInstance = getContentManager().getCubeInstance(schemaId, cubeName, runtimeContext);
		DimensionUtils utils = new DimensionUtils(cubeInstance);
		return utils.drillthrough(schema, cell, getLogger(), getCacheServer(), runtimeContext);
	}
	
	@Override
	public OlapResult drillthrough(ValueResultCell cell, String schemaId, String cubeName, IRuntimeContext runtimeContext, Projection projection) throws Exception {
		Schema schema = getContentManager().getSchema(schemaId);
		ICubeInstance cubeInstance = getContentManager().getCubeInstance(schemaId, cubeName, runtimeContext);
		DimensionUtils utils = new DimensionUtils(cubeInstance);
		return utils.drillthrough(schema, cell, getLogger(), getCacheServer(), runtimeContext, projection);
	}

	@Override
	public OlapResult executeQuery(String mdx, String schemaId, String cubeName, Integer limit, boolean computeDatas, IRuntimeContext runtimeContext) throws Exception {
		ICubeInstance cubeInstance = getContentManager().getCubeInstance(schemaId, cubeName, runtimeContext);
		IObjectIdentifier id = null;
		
		try{
			id = getContentManager().getSchemaObjectIdentifier(schemaId);
		}catch(Exception ex){
			Logger.getLogger(getClass()).warn("Could not find the ObjectIdetifier for schema " + schemaId + " - " + ex.getMessage(), ex);
		}
		
		IOlapRunner runner = new OlapRunner(id, cubeInstance, getLogger(), getCacheServer(), limit, runtimeContext);
		runner.setSqlStreamMode(isInStreamMode);
		return runner.executeQuery(mdx, computeDatas);
	}
	
	public void setIsInStreamMode(boolean isInStreamMode) {
		this.isInStreamMode = isInStreamMode;
	}

	@Override
	public OlapResult executeFMDTQuery(IExternalQueryIdentifier identifier, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception {
		ICubeInstance cubeInstance = getContentManager().getCubeInstance(schemaId, cubeName, runtimeContext);
		IQueryHelper helper = FactoryQueryHelper.getQueryHelper(getLogger(), runtimeContext, cubeInstance.getFactTable().getParent().getDatasourceExtensionId());
		return helper.executeFmdtQuery(identifier, cubeInstance);
	}
	
	@Override
	public String createExtrapolation(String schemaId, String cubeName, Projection projection, IRuntimeContext ctx) throws Exception {
		ICubeInstance cubeInstance = getContentManager().getCubeInstance(schemaId, cubeName, ctx);
		ProjectionDescriptor desc = null;
		for(ProjectionMeasure pm : projection.getProjectionMeasures()) {
		
			IProjectionCreator creator = ProjectionCreatorFactory.getProjectionCreator(projection, getLogger(), pm);
			if(creator != null) {
				desc = creator.createProjection(cubeInstance, ctx);
			}
		}
		
		return desc.getFileName();
	}
	
	@Override
	public OlapResult executeQueryForExtrapolationProjection(String mdx, String schemaId, String cubeName, Integer limit, boolean computeDatas, IRuntimeContext runtimeContext, Projection projection) throws Exception {
		ICubeInstance cubeInstance = getContentManager().getCubeInstance(schemaId, cubeName, runtimeContext);
		IObjectIdentifier id = null;
		
		try{
			id = getContentManager().getSchemaObjectIdentifier(schemaId);
		}catch(Exception ex){
			Logger.getLogger(getClass()).warn("Could not find the ObjectIdetifier for schema " + schemaId + " - " + ex.getMessage(), ex);
		}
		
		Dimension originalDateDim = null;
//		
//		Dimension projDim = null;
		for(Dimension dim : cubeInstance.getCube().getDimensions()) {
//			if(dim.getName().startsWith("PROJECTION_")) {
//				projDim = dim;
//			}
			if(dim.isDate()) {
				originalDateDim = dim; 
			}
		}
		
		//FIXME : empty the cache
		for(Level lvl : originalDateDim.getHierarchies().get(0).getLevels()) {
			getCacheServer().unloadObject(lvl);
			lvl.getMembers().clear();
		}
		cubeInstance.getHierarchyExtractor(originalDateDim.getHierarchies().get(0)).getRootMember().getSubMembers().clear();
		cubeInstance.getHierarchyExtractor(originalDateDim.getHierarchies().get(0)).getRootMember().setChildsLoaded(false);
		cubeInstance.getHierarchyExtractor(originalDateDim.getHierarchies().get(0)).getRootMember().setHasProjectionMembers(false);
		
		
//		if(projDim == null) {
//			//FIXME : Create the dimension
//			projDim = ModelFactory.eINSTANCE.createDimension();
//			projDim.setName("PROJECTION_" + originalDateDim.getName());
//			projDim.setCaption("PROJECTION_" + originalDateDim.getCaption());
//			projDim.setParentSchema(cubeInstance.getSchema());
//			projDim.setUname("[" + projDim.getName() + "]");
//			cubeInstance.getCube().getDimensions().add(projDim);
//		}
//		
//		Hierarchy hiera = null;
//		for(Hierarchy h : projDim.getHierarchies()) {
//			if(h.getName().endsWith("_" + projection.getName())) {
//				hiera = h;
//				break;
//			}
//		}
//		if(hiera == null) {
//			//FIXME : Create the hierarchy
//			Hierarchy original = originalDateDim.getHierarchies().get(0);
//			
//			hiera = ModelFactory.eINSTANCE.createHierarchy();
//			hiera.setCaption(original.getCaption() + "_" + projection.getName());
//			hiera.setName(original.getName() + "_" + projection.getName());
//			hiera.setUname("[" + projDim.getName() + "." + hiera.getName() + "]");
//			hiera.setParentDimension(projDim);
//			projDim.getHierarchies().add(hiera);
//			
//			//create the levels
//			Level previous = null;
//			for(Level lvl : original.getLevels()) {
//				Level newLevel = createLevelFromOriginal(lvl, projection, hiera);
//				hiera.getLevels().add(newLevel);
//				if(previous != null) {
//					newLevel.setParentLevel(previous);
//					previous.setSubLevel(newLevel);
//				}
//				previous = newLevel;
//				if(lvl.getUname().equals(projection.getProjectionLevel())) {
//					break;
//				}
//			}
//			
//			//create a hierarchyExtractor too
//			ProjectionHierarchyExtractor ext = new ProjectionHierarchyExtractor(hiera, projection, getCacheServer());
//			cubeInstance.addHierarchyExtractor(ext);
//		}
		
		
		
		IOlapRunner runner = new OlapRunner(id, cubeInstance, getLogger(), getCacheServer(), limit, runtimeContext, projection);
		runner.setSqlStreamMode(isInStreamMode);
		return runner.executeQuery(mdx, computeDatas);
	}
	private Level createLevelFromOriginal(Level lvl, Projection projection, Hierarchy hiera) {
		
		Level newLevel = null;
		
		if(lvl instanceof DateLevel) {
			newLevel = new DateLevel();
			((DateLevel)newLevel).setDateOrder(((DateLevel) lvl).getDateOrder());
			((DateLevel)newLevel).setDatePart(((DateLevel) lvl).getDatePart());
			((DateLevel)newLevel).setDatePattern(((DateLevel) lvl).getDatePattern());
			((DateLevel)newLevel).setDateType(((DateLevel) lvl).getDateType());
		}
		else {
			newLevel = ModelFactory.eINSTANCE.createLevel();
		}
		
		newLevel.setName(lvl.getName() + "_" + hiera.getName());
		newLevel.setCaption(lvl.getCaption() + "_" + hiera.getCaption());
		newLevel.setUname(hiera.getUname() + ".[" + newLevel.getName() + "]");
		newLevel.setParentHierarchy(hiera);
		
		return newLevel;
	}
}
