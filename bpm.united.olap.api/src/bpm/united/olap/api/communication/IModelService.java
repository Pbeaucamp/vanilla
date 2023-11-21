package bpm.united.olap.api.communication;

import java.util.List;

import bpm.united.olap.api.BadFasdSchemaModelTypeException;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.preload.IPreloadConfig;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.vanilla.platform.core.IObjectIdentifier;

public interface IModelService {

	IObjectIdentifier getSchemaObjectIdentifier(String schemaId) throws Exception;
	Schema getSchema(String schemaId) throws Exception;
	Schema getSchema(IObjectIdentifier identifier, IRuntimeContext ctx) throws Exception;
	
	/**
	 * allow to preload Hierarchies in the cache
	 * @param schema
	 * @param config
	 * @throws Exception
	 */
	String loadSchema(IObjectIdentifier objectIdentifier, IRuntimeContext runtimeContext) throws BadFasdSchemaModelTypeException, Exception;
	
	String loadSchema(Schema schema, IPreloadConfig config, IRuntimeContext runtimeContext) throws Exception;
	
	List<Schema> getLoadedSchema() throws Exception;
	
	void unloadSchema(String schemaId, IObjectIdentifier id) throws Exception;
	
	void refreshSchema(IObjectIdentifier objectIdentifier, IRuntimeContext runtimeContext) throws Exception;
	
	List<Member> getSubMembers(String uname, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception;
	
	List<String> searchOnDimensions(String word, String levelUname, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception;
	
		
	List<Member> getChilds(String uname, String schemaId, IRuntimeContext ctx) throws Exception;
	
	
	void refreshSchema(Schema model, IObjectIdentifier id, IPreloadConfig conf, IRuntimeContext runtimeContext) throws Exception;

	List<List<String>> exploreDimension(String dimensionName, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception;
	
	List<String> getLevelValues(String levelUname, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception;
	Member refreshTimeDimension(String utdSchemaId, String cubeName, IRuntimeContext ctx, Projection createUnitedOlapProjection) throws Exception; 
	
	

	/**
	 * Allow to remove cache for cacheDisk or memcached or both
	 * 
	 * @param schemaId
	 * @param cubeName
	 * @param ctx
	 * @param removeCacheDisk
	 * @param removeMemCached
	 * @throws Exception
	 */
	public void removeCache(String schemaId, String cubeName, IRuntimeContext ctx, boolean removeCacheDisk, boolean removeMemCached) throws Exception;
	
	/**
	 * Used to preload dimensions and queries for a given cube
	 * 
	 * @param schemaId
	 * @param cubename
	 * @param preloadConfig
	 * @param ctx
	 * @param mdxQueries
	 * @throws Exception 
	 */
	public void restoreReloadCache(String schemaId, String cubename, IPreloadConfig preloadConfig, IRuntimeContext ctx, List<String> mdxQueries) throws Exception;
}
