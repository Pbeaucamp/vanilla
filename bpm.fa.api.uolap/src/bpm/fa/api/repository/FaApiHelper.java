package bpm.fa.api.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;

import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPStructure;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.olap.xmla.XMLAStructure;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.tools.FasdModelConverter;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FaApiHelper {
	private UnitedOlapLoader loader = new UnitedOlapLoader();
	private static class  StructureId{
		private IObjectIdentifier identifier;
		private IRuntimeContext context;
		public StructureId(IObjectIdentifier identifier, IRuntimeContext context){
			this.identifier = identifier;
			this.context = context;
		}
		/**
		 * @return the identifier
		 */
		public IObjectIdentifier getIdentifier() {
			return identifier;
		}
		
		/**
		 * @return the context
		 */
		public IRuntimeContext getContext() {
			return context;
		}
		
		
		
		
	}
	
//	private HashMap<StructureId, List<OLAPStructure>> structures = new HashMap<StructureId, List<OLAPStructure>>();
	private HashMap<StructureId, String> schemas = new HashMap<StructureId, String>();
	private String vanillaUrl;
	
	
	public FaApiHelper(String vanillaUrl, UnitedOlapLoader loader){
		this.vanillaUrl = vanillaUrl;
		this.loader = loader;
	}
	
	/**
	 * Method to create an OlapCube.
	 * Will load or refresh the schema if needed.
	 * 
	 * @param identifier
	 * @param ctx
	 * @param cubeName
	 * @return
	 * @throws Exception
	 */
	public OLAPCube getCube(IObjectIdentifier identifier, IRuntimeContext ctx, String cubeName) throws Exception{
		
		if(isXMLA(identifier, ctx)) {
			return getCubeXMLA(identifier, ctx, cubeName);
		}
		
		StructureId id = new StructureId(identifier, ctx);
		
		String schemaId = loadSchema(id);
		
		return loader.createCube(schemaId, cubeName, ctx);
	}

	public UnitedOlapLoader getLoader(){
		return loader;
	}
	
	/**
	 * This method is used to find cube names in a schema.
	 * If the schema is not loaded it will be by calling this method (it will be modified if there was modifications).
	 * 
	 * If you already have the cube name, you can use getCube(IObjectIdentifier identifier, IRuntimeContext ctx, String cubeName);
	 * The method will load or refresh the schema and return a new OlapCube.
	 * 
	 * @param identifier
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public Collection<String> getCubeNames(IObjectIdentifier identifier, IRuntimeContext ctx) throws Exception {
		
		if(isXMLA(identifier, ctx)) {
			return getXMLACubeNames(identifier, ctx);
		}
		
		List<String> names = new ArrayList<String>();
		
		StructureId id = new StructureId(identifier, ctx);
		
		String schemaId = loadSchema(id);
		
		for(OLAPStructure struct : loader.getStructures(schemaId)) {
			names.add(struct.getCubeName());
		}
		
		return names;
	}

	private Collection<String> getXMLACubeNames(IObjectIdentifier identifier, IRuntimeContext ctx) throws Exception {
		
		for(StructureId st : xmlaModels.keySet()) {
			if(st.getIdentifier().getDirectoryItemId() == identifier.getDirectoryItemId() && st.getIdentifier().getRepositoryId() == identifier.getRepositoryId()) {
				String xml = xmlaModels.get(st);
				
				DigesterFasd fasdDigester = new DigesterFasd(IOUtils.toInputStream(xml, "UTF-8"));
				FAModel xmla = fasdDigester.getFAModel();
				
				List<String> names = new ArrayList<String>();
				for(ICube c : xmla.getCubes()) {
					names.add(c.getName());
				}
				
				xmlaFasds.put(st, xmla);
				
				return names;
			}
		}
		
		
		
		return null;
	}
	
	private OLAPCube getCubeXMLA(IObjectIdentifier identifier, IRuntimeContext ctx, String cubeName) throws Exception {
		
		for(StructureId st : xmlaFasds.keySet()) {
			if(st.getIdentifier().getDirectoryItemId() == identifier.getDirectoryItemId() && st.getIdentifier().getRepositoryId() == identifier.getRepositoryId()) {
		
				XMLAStructure structure = new XMLAStructure(xmlaFasds.get(st), cubeName);
				
				OLAPCube cube = structure.createCube(ctx);
				
				return cube;
			}
		}
		return null;
	}

	private HashMap<StructureId, String> xmlaModels = new HashMap<StructureId, String>();
	private HashMap<StructureId, FAModel> xmlaFasds = new HashMap<StructureId, FAModel>();
	
	//XXX : can be done better, but for now, just make it work
	private boolean isXMLA(IObjectIdentifier identifier, IRuntimeContext ctx) throws Exception {

		StructureId structId = new StructureId(identifier, ctx);
		
		IVanillaContext vanillaContext = new BaseVanillaContext(vanillaUrl, structId.getContext().getLogin(), structId.getContext().getPassword()); 
		
		IVanillaAPI remoteVanilla = new RemoteVanillaPlatform(vanillaContext);
		Repository rep = remoteVanilla.getVanillaRepositoryManager().getRepositoryById(structId.getIdentifier().getRepositoryId());
		Group group = remoteVanilla.getVanillaSecurityManager().getGroupById(ctx.getGroupId());
		
		IRepositoryContext repctx = new BaseRepositoryContext(vanillaContext, group, rep);
		
		IRepositoryApi sock = new RemoteRepositoryApi(repctx);
		
		//get back item from repository
		RepositoryItem it = null;
		
		try{
			it = sock.getRepositoryService().getDirectoryItem(structId.getIdentifier().getDirectoryItemId());
			if (it == null){
				throw new Exception("This item does not exist");
			}
		}catch(Exception ex){
			throw new Exception("Unable to get DirectoryItem " + structId.getIdentifier().getDirectoryItemId() + "- " + ex.getMessage(), ex);
		}
		
		String xml = sock.getRepositoryService().loadModel(it);
		
		if(xml.contains("<XMLAConnection>")) {
			xmlaModels.put(structId, xml);
			return true;
		}
		
		return false;
	}

	private String loadSchema(StructureId structId) throws Exception {
		
		/*
		 * create RepositoryCOnnection
		 */
		IVanillaContext vanillaCtx = new BaseVanillaContext(vanillaUrl, structId.getContext().getLogin(), structId.getContext().getPassword());
		IVanillaAPI remoteVanilla = new RemoteVanillaPlatform(vanillaCtx);
		
		Repository rep = null;
		
		try{
			rep = remoteVanilla.getVanillaRepositoryManager().getRepositoryById(structId.getIdentifier().getRepositoryId());
			if (rep == null){
				throw new Exception("The Repository with id=" + structId.getIdentifier().getRepositoryId() + " does no exist");
			}
		}catch(Exception ex){
			throw new Exception("Unable to find Repository reference within the VanillaPlatform - " + ex.getMessage(), ex);
		}
		
		Group dummyGroup = new Group();
		dummyGroup.setId(structId.getContext().getGroupId());
		dummyGroup.setName(structId.getContext().getGroupName());
		
		IRepositoryApi sock = new RemoteRepositoryApi(
				new BaseRepositoryContext(
						vanillaCtx, 
						dummyGroup, 
						rep));

		//get back item from repository
		RepositoryItem it = null;
		
		try{
			it = sock.getRepositoryService().getDirectoryItem(structId.getIdentifier().getDirectoryItemId());
			if (it == null){
				throw new Exception("This item does not exist");
			}
		}catch(Exception ex){
			throw new Exception("Unable to get DirectoryItem " + structId.getIdentifier().getDirectoryItemId() + "- " + ex.getMessage(), ex);
		}
		
		//look if it's already loaded
		synchronized (schemas) {
			
			for(StructureId id : schemas.keySet()) {
				
				//if it's already loaded look if it's been updated
				if(id.identifier.getDirectoryItemId() == structId.getIdentifier().getDirectoryItemId() && id.getIdentifier().getRepositoryId() == structId.getIdentifier().getRepositoryId()) {
					String schemaId = schemas.get(id);
					
					for(Schema sch : loader.loadedSchemas()) {
						if(sch.getId().equals(schemaId)) {
							
							if(sock.getRepositoryService().checkItemUpdate(it, sch.getLastModificationDate())) {
								
								//reload it !
								loader.unloadSchema(schemaId, id.identifier);
								StructureQueryLogger queryLogger = new StructureQueryLogger(remoteVanilla, structId.getIdentifier(), structId.getContext());
								schemaId = loader.loadModel(structId.getIdentifier(), structId.getContext(), queryLogger);
								schemas.put(id, schemaId);
							}
							else{
								//XXX : LCA not sure about this
								//as the sock.hasBeenUpdated may fail,
								//we add another check by generating the UUID from the currentModelXml
								//if it has changed, we must reload the model into uOLAP
								//incovenient : lastUsed from the VanillaPorta, will be fucked because the definition is loaded
								
								try{
									String xml = sock.getRepositoryService().loadModel(it);
									if (!FasdModelConverter.generateUuidFromXmlDefinition(xml).equals(schemaId)){
										//reload it !
										loader.unloadSchema(schemaId, id.identifier);
										StructureQueryLogger queryLogger = new StructureQueryLogger(remoteVanilla, structId.getIdentifier(), structId.getContext());
										schemaId = loader.loadModel(structId.getIdentifier(), structId.getContext(), queryLogger);
										schemas.put(id, schemaId);
									}
								}catch(Exception ex){
									Logger.getLogger(getClass()).warn("Could not reload the FASD model within unitedOlap Engine - " + ex.getMessage(), ex);
								}
								
								
							}
							
							return schemaId;
						}
					}
				}
			}
		}
	
		
		//it's not loaded yet, load it !
		StructureQueryLogger queryLogger = new StructureQueryLogger(remoteVanilla, structId.getIdentifier(), structId.getContext());
		String id = loader.loadModel(structId.getIdentifier(), structId.getContext(), queryLogger);
		schemas.put(structId, id);
		return id;
	}
	
	
	
	
	
	
}
