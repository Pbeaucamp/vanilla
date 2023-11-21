package bpm.united.olap.runtime.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;
import org.fasd.xmla.ISchema.SchemaType;

import bpm.united.olap.api.BadFasdSchemaModelTypeException;
import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.impl.ClosureHierarchy;
import bpm.united.olap.api.model.impl.DateLevel;
import bpm.united.olap.api.preload.IPreloadConfig;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.HierarchyExtractor;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.api.tools.FasdModelConverter;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.model.ClosureHierarchyExtractor;
import bpm.united.olap.runtime.model.CubeInstance;
import bpm.united.olap.runtime.model.DegeneratedDateHierarchyExtractor;
import bpm.united.olap.runtime.model.DegeneratedHierarchyExtractor;
import bpm.united.olap.runtime.model.SnowFlakesHierarchyExtractor;
import bpm.united.olap.runtime.model.SnowFlakesPathFinder;
import bpm.united.olap.runtime.model.StarHierarchyExtractor;
import bpm.united.olap.runtime.preload.Preloader;
import bpm.united.olap.runtime.tools.DimensionUtils;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

public abstract class UnitedOlapContentManager implements IUnitedOlapContentManager {
	private String vanillaUrl;

	/**
	 * Map<Schema , Map<GroupId, List<Cube>>
	 */
	private HashMap<Schema, HashMap<Integer, List<ICubeInstance>>> cubeInstances = new HashMap<Schema, HashMap<Integer, List<ICubeInstance>>>();
	protected HashMap<IObjectIdentifier, Schema> identifierMap = new HashMap<IObjectIdentifier, Schema>(); 
	
	private ICacheServer server;

	protected IVanillaLoggerService logger;

//	private UnitedOlapRuntimeComponent component;
	
	protected UnitedOlapContentManager() {
		
	}
	
	
	public String getVanillaUrl(){
		return vanillaUrl;
	}
	
	protected void setVanillaUrl(String vanillaUrl){
		this.vanillaUrl = vanillaUrl;
	}
	
//	public static IUnitedOlapContentManager getInstance() {
//		if(instance == null) {
//			instance = new UnitedOlapContentManager();
//		}
//		return instance;
//	}
	
	
	protected void storeSchema(Schema schema, IRuntimeContext context){
		synchronized (cubeInstances) {
			Schema schm = null;
			for(Schema s : cubeInstances.keySet()){
				if (s.getId().equals(schema.getId())){
					schm = s;
					break;
				}
			}
			
			// the schema has not yet been loaded
			if (schm == null){
				cubeInstances.put(schema, new HashMap<Integer, List<ICubeInstance>>());
				schm = schema;
			}
		

			
			
			//check if cube has been loaded for the runtimeContext's Group id
			Integer groupId = null; 
			for(Integer i : cubeInstances.get(schm).keySet()){
				if (i == context.getGroupId()){
					groupId = i;
					break;
				}
			}
			
			//the schema have not been loaded for the given Group
			if (groupId == null){
				cubeInstances.get(schm).put(context.getGroupId(), new ArrayList<ICubeInstance>());
				groupId = context.getGroupId();
			}
			
			for(Cube cube : schema.getCubes()){
				
				//check if the cube has been loaded for this group
				boolean found = false;		
				for(ICubeInstance ci : cubeInstances.get(schm).get(groupId)){
					if (ci.getCube().getName().equals(cube.getName())){
						found = true;
						break;
					}
				}
				
				if (!found){
					//add a cube instance for this group in this schema
					ICubeInstance ci = new CubeInstance(
							schema, 
							cube, 
							cube.getFactTable(), 
							getCacheServer(), context);
					
					cubeInstances.get(schm).get(groupId).add(ci);
					
				}
				
			}
		}
		Logger.getLogger(getClass()).info("Store UOLAP Schema with id=" + schema.getId());
	}
	
	
	@Override
	public String loadSchema(IObjectIdentifier identifier, IRuntimeContext context) throws BadFasdSchemaModelTypeException, Exception {
		Schema schema = findSchema(identifier, context);
		
		if (schema == null){
			try{
				schema = importSchema(getVanillaUrl(), identifier, context);
			}catch(BadFasdSchemaModelTypeException ex){
				throw ex;
			}catch(Exception ex){
				Logger.getLogger(getClass()).error("Failed to import UnitedOlapSchema - " + ex.getMessage(), ex);
				throw new Exception("Failed to import UnitedOlapSchema - " + ex.getMessage(), ex);
			}
		}
		
		try{
			storeSchema(schema, context);
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Failed to store UnitedOlapSchema - " + ex.getMessage(), ex);
			throw new Exception("Failed to store UnitedOlapSchema - " + ex.getMessage(), ex);
		}
		
		if (identifier != null && identifierMap.get(identifier) == null){
			identifierMap.put(identifier, schema);
		}
		
		return schema.getId();
	}

	private Schema importSchema(String vanillaUrl, IObjectIdentifier identifier, IRuntimeContext context) throws Exception{
		/*
		 * create RepositoryCOnnection
		 */
		IVanillaContext vanillaCtx = new BaseVanillaContext(vanillaUrl, context.getLogin(), context.getPassword());
		IVanillaAPI remoteVanilla = new RemoteVanillaPlatform(vanillaCtx);
		
		
		
		Repository rep = null;
		
		try{
			rep = remoteVanilla.getVanillaRepositoryManager().getRepositoryById(identifier.getRepositoryId());
			if (rep == null){
				throw new Exception("The Repository with id=" + identifier.getRepositoryId() + " does no exist");
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Unable to find Repository reference within the VanillaPlatform - " + ex.getMessage(), ex);
		}
		
		Group group = remoteVanilla.getVanillaSecurityManager().getGroupById(context.getGroupId());
		
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, group, rep);
		IRepositoryApi sock = new RemoteRepositoryApi(ctx);
		
		RepositoryItem it = null;
		
		try{
			it = sock.getRepositoryService().getDirectoryItem(identifier.getDirectoryItemId());
			if (it == null){
				throw new Exception("This item does not exist");
			}
		}catch(Exception ex){
			throw new Exception("Unable to get DirectoryItem " + identifier.getDirectoryItemId() + "- " + ex.getMessage(), ex);
		}
		
		String xml = null;
		String schemaId = null;
		
		try{
			xml = sock.getRepositoryService().loadModel(it);
			schemaId = FasdModelConverter.generateUuidFromXmlDefinition(xml);
			if (xml == null){
				throw new Exception("No XML definition found on the repository for FASD " + it.getId() );
			}
		}catch(Exception ex){
			throw new Exception("Unable to load model XML from repository - " + ex.getMessage(), ex);
		}
		
		/*
		 * we override the Oda properties relative to the repository
		 * -> repositoryUrl/repositoryId + vanillaUrl to
		 * make sure that the model is made on only one repository
		 */
		try{
			
			xml = overrideRepositoryAccess(rep, xml);
			Logger.getLogger(getClass()).info("Oda repository properties overriden for object " + identifier.toString());
		}catch(Exception ex){
			Logger.getLogger(getClass()).warn("Failed to override odainput's repository properties for " + identifier.toString());
			Logger.getLogger(getClass()).warn("because : " + ex.getMessage(), ex);
			throw ex;
		}
		
		
		
		
		
		DigesterFasd fasdDigester = null;
		
		try{
			fasdDigester = new DigesterFasd(IOUtils.toInputStream(xml, "UTF-8"));
		}catch(Exception ex){
			throw new Exception("Unable to rebuild FASD model - " + ex.getMessage(), ex);
		}
		
		FAModel faModel = fasdDigester.getFAModel();
		
			
		if (faModel.getOLAPSchema().getSchemaType() != SchemaType.UNITED_OLAP && faModel.getOLAPSchema().getSchemaType() != SchemaType.MONDRIAN){
			throw new BadFasdSchemaModelTypeException(faModel, identifier);
		}
		
		FasdModelConverter converter = new FasdModelConverter();
		Schema schema = converter.convert(faModel);
		schema.setId(schemaId);
		schema.setLastModificationDate(it.getDateModification());
		IPreloadConfig config = converter.getPreloadConfig();
		
		
		//XXX
//		if (faModel.getUuid() != null){
//			Logger.getLogger(getClass()).info("The FASDModel already has an UUid " +  faModel.getUuid() + ", it will be used for the UnitedOlap Schema");
//			schema.setId(faModel.getUuid());
//		}
//		else{
//			Logger.getLogger(getClass()).info("The FASDModel has no UUid ");
//			
//			faModel.setUuid(schema.getId());
//			Logger.getLogger(getClass()).info("The FASDModel UUid set to " + schema.getId());
//			Logger.getLogger(getClass()).info("Updating the FASDModel on the repository to persists its UUid....");
//			
//			try{
//				sock.updateModel(it.getDirectoryItem(), faModel.getFAXML());
//				Logger.getLogger(getClass()).info("FASModel has been updated on the Repository");
//			}catch(Exception ex){
//				Logger.getLogger(getClass()).error("Unable to update the FASDModel " + ex.getMessage(), ex);
//			}
//			
//			
//		}
		
		try{
			if (config != null){
				Preloader p = new Preloader(this, schema, context);
				p.preload((IModelService)this, config);
			}
		}catch(Exception ex){
			Logger.getLogger(getClass()).warn("Unable to preload dimensions - " + ex.getMessage(), ex);
		}
		

		return schema;
	}

	@Override
	public void unloadSchema(String schemaId, IObjectIdentifier id) {
		Schema s = getSchema(schemaId);
		if(s != null) {
			synchronized (cubeInstances) {
				try {
					getCacheServer().unloadObject(s);
					
					getCacheServer().getCacheDisk().removeForSchema(schemaId);
					
					synchronized (identifierMap) {
						for(IObjectIdentifier sc : identifierMap.keySet()) {
							if(identifierMap.get(sc).getId().equals(schemaId)) {
								identifierMap.remove(sc);
							}
						}
					}
					
				} catch (Exception e) {
					Logger.getLogger(getClass()).error("Unable to unload schema " + schemaId + " from the cache - " + e.getMessage(), e);
				}
				cubeInstances.remove(s);
				
//				for(IObjectIdentifier key : identifierMap.keySet()){
//					if (identifierMap.get(key) != null && identifierMap.get(key).getId().equals(schemaId)){
//						identifierMap.remove(key);
//					}
//				}
				
				Logger.getLogger(getClass()).info("Unloaded schema " + schemaId);
			}
			
		}
	}
	
	
	public List<Schema> getLoadedSchemas(){
		List<Schema> l = new ArrayList<Schema>(cubeInstances.keySet());
		return l;
	}


	@Override
	public void clearCache() {
		server.clearCache();
	}



	@Override
	public Schema getSchema(String schemaId) {
		for(Schema s : cubeInstances.keySet()){
			if (s.getId().equals(schemaId)){
				return s;
			}
		}
		return null;
	}

	@Override
	public List<String> searchOnDimensions(String word, String levelUname, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception {
		ICubeInstance cubeInstance = getCubeInstance(schemaId, cubeName, ctx);
		
		if (cubeInstance == null){
			throw new Exception("The cube " + cubeName + " for the schema " + schemaId + " does not exists anymore.");
		}
		
		List<String> results = new DimensionUtils(cubeInstance).searchDimensions(word, levelUname, server, ctx);
		
		return results;
	}


	public void bind(ICacheServer server) {
		this.server = server;

	}

	@Override
	public ICacheServer getCacheServer() {
		return server;
	}

	@Override
	public ICubeInstance getCubeInstance(String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception{
		Schema sc = null;
		
		for(Schema s : cubeInstances.keySet()){
			if (s.getId().equals(schemaId)){
				sc = s;
				break;
			}
		}
		
		if (sc == null){
			throw new Exception("No schema with id = " + schemaId + " loaded");
		}
		
		
		Integer groupId = null;
		for(Integer k : cubeInstances.get(sc).keySet()){
			if (k == runtimeContext.getGroupId()){
				groupId = k;
				for(ICubeInstance c : cubeInstances.get(sc).get(k)){
					if (c.getCube().getName().equals(cubeName)){
						Logger.getLogger(getClass()).debug("CubeInstance for cube named "  + cubeName + " found within schema " + schemaId);
						return c;	
					}
				}
			}
			
		}
		
		synchronized (cubeInstances) {
			//cube not loaded for the cube Instance
			if (groupId == null){
				groupId = runtimeContext.getGroupId();
				 cubeInstances.get(sc).put(groupId, new ArrayList<ICubeInstance>());
			}
			
			//create the cube Instance
			Cube cube = null;
			for(Cube cu : sc.getCubes()){
				if (cu.getName().equals(cubeName)){
					cube = cu;
					break;
				}
			}
			
			if (cube == null){
				throw new Exception("No Cube named " + cubeName + " within schema " + schemaId);
			}
			Logger.getLogger(getClass()).info("created CubeInstance for cube="  + cubeName + " schema=" + schemaId + " groupId=" + runtimeContext.getGroupId() + " groupName=" + runtimeContext.getGroupName());
			ICubeInstance ci = new CubeInstance(
					sc, 
					cube, 
					cube.getFactTable(), 
					getCacheServer(), runtimeContext);
			
			cubeInstances.get(sc).get(groupId).add(ci);
			return ci;
		}
	}
	
	
	private Schema findSchema(IObjectIdentifier identifier, IRuntimeContext ctx){
		synchronized (identifierMap) {
			for (IObjectIdentifier id : identifierMap.keySet()){
				if (id.getDirectoryItemId() == identifier.getDirectoryItemId() && id.getRepositoryId() == identifier.getRepositoryId()){
					return identifierMap.get(id);
				}
			}
		}
		return null;
	}
	

	@Override
	public void refreshSchema(IObjectIdentifier identifier, IRuntimeContext ctx) throws BadFasdSchemaModelTypeException, Exception{
		Schema schema = findSchema(identifier, ctx);
		if (schema != null){
			unloadSchema(schema.getId(), identifier);
		}
		
		
		loadSchema(identifier, ctx);
		
	}

	@Override
	public List<Member> getChilds(String uname, String schemaId, IRuntimeContext ctx) throws Exception {
		Schema schema = getSchema(schemaId);
		
		Hierarchy hierarchy = null;
		LOOK:for(Dimension dim : schema.getDimensions()) {
			for(Hierarchy hiera : dim.getHierarchies()) {
				if(uname.startsWith(hiera.getUname())) {
					hierarchy = hiera;
					break LOOK;
				}
			}
		}
		
		HierarchyExtractor extractor = createHierarchyExtractor(hierarchy,schema);
		Member mem = extractor.getMember(uname, ctx);
		return extractor.getChilds(mem, ctx);
	}

	private HierarchyExtractor createHierarchyExtractor(Hierarchy hierarchy, Schema schema) throws Exception{
		
		DataObject factTable = null;
		for(Datasource datasource : schema.getDatasources()) {
			for(DataObject dobj : datasource.getDataObjects()) {
				if(dobj.isIsFact()) {
					factTable = dobj;
				}
			}
		}
		
		if(hierarchy.isIsClosureHierarchy()) {
			return new ClosureHierarchyExtractor((ClosureHierarchy)hierarchy, server);
		}
		else {
			DataObject previous = null;
			for(Level lvl : hierarchy.getLevels()) {
				if(lvl.getItem().getParent() == factTable) {
					if(lvl instanceof DateLevel) {
						return new DegeneratedDateHierarchyExtractor(hierarchy, server);
					}
					else {
						return new DegeneratedHierarchyExtractor(hierarchy, server);
					}
				}
				else {
					if(previous == null) {
						previous = lvl.getItem().getParent();
					}
					else {
						if(previous == lvl.getItem().getParent()) {
							return new StarHierarchyExtractor(hierarchy, server);
						}
						else {
							List<Relation> relations = new ArrayList<Relation>();
							for(Datasource ds : schema.getDatasources()) {
								for(Relation rel : ds.getRelations()) {
									if(!relations.contains(rel)) {
										relations.add(rel);
									}
								}
							}
							
							return new SnowFlakesHierarchyExtractor(hierarchy, server, new SnowFlakesPathFinder(relations), factTable);
						}
					}
				}
			}
		}
		
		if (hierarchy.getLevels().size() == 1){
			return new StarHierarchyExtractor(hierarchy, server);
		}
		else if (hierarchy.getLevels().size() == 0){
			throw new Exception("The hierarchy " + hierarchy.getParentDimension().getName() +  " " + hierarchy.getName() + " has not Level");
		}
		return null;
	}
	
	
	private String overrideRepositoryAccess(Repository rep, String xml) throws Exception{
//		String xmlTrimmed = xml.trim();
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		
		
		for(Element dsE : (List<Element>)root.element("datasources").elements("datasource-oda")){
			
			boolean repIdPresent = false;
			if (dsE.element("public-properties") == null){
				continue;
			}
			for(Element e : (List<Element>)dsE.element("public-properties").elements("property")){
				if ("VANILLA_URL".equals(e.element("name").getText())){
					Logger.getLogger(getClass()).info("Replaced odainput property VANILLA_URL " + e.element("name").getText()+ "->" +ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
					e.element("value").setText(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
				}
				if ("REPOSITORY_ID".equals(e.element("name").getText())){
					Logger.getLogger(getClass()).info("Replaced odainput property REPOSITORY_ID " + e.element("name").getText()+ "->" +rep.getUrl());
					e.element("value").setText(rep.getId() + "");
					repIdPresent = true;
				}
			}
			if (!repIdPresent){
				 Element p = dsE.element("public-properties").addElement("property");
				 p.addElement("name").setText("VANILLA_URL");
				 p.addElement("value").setText(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
				 
				 p = dsE.element("public-properties").addElement("property");
				 p.addElement("name").setText("REPOSITORY_ID");
				 p.addElement("value").setText(rep.getId()+"");
				 
				 Logger.getLogger(getClass()).info("Odainput properties VANILLA_URL/REPOSITORY_ID missed, we add them ->" + ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl() + " " + rep.getId());

			}
		}
		return doc.asXML();
	}
	
	public void bind(IVanillaLoggerService service) {
		this.logger = service;
	}
	
	public void unbind(IVanillaLoggerService service) {
		this.logger = null;
	}
}
