package bpm.united.olap.runtime.engine;

import java.util.List;

import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.vanilla.platform.core.IObjectIdentifier;

/**
 * Used to manage content like schemas, dimensions, cubes...
 * @author Marc Lanquetin
 *
 */
public interface IUnitedOlapContentManager {

	/**
	 * 
	 * @return all the loaded schema
	 */
	public List<Schema> getLoadedSchemas();
	
	/**
	 * 
	 * if no cubeInstance has been create for the groupId within the runtimeContext, a new one is created
	 * 
	 * @param schemaId
	 * @param cubeName
	 * @param runtimeContext
	 * @return the CubeInstance for the given datas
	 * @throws Exception
	 */
	ICubeInstance getCubeInstance(String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception;
	/**
	 * Load a schema and create CubeInstances for each cube within the schema for the groupId within the IRuntimeContext
	 * @param schema
	 * @throws Exception 
	 */
	String loadSchema(/*Schema schema, */IObjectIdentifier identifier, IRuntimeContext context) throws Exception;
	
	
	
	
	/**
	 * Unload a schema for the manager and clear the cache
	 * @param schema
	 */
	void unloadSchema(String schema, IObjectIdentifier id);
	
	 
	 /**
	  * Clear all items in cache
	  */
	 void clearCache();
	
	 /**
	  * Get the schema by its name
	  * @param schemaId
	  * @return
	  */
	 Schema getSchema(String schemaId);

//	 /**
//	  * Init the contentManager
//	  * @param unitedOlapRuntimeComponent
//	  * @param cacheServer
//	  */
//     void setComponent(UnitedOlapRuntimeComponent unitedOlapRuntimeComponent, ICacheServer cacheServer);
	
    ICacheServer getCacheServer();

    /**
     * unload the schema and reload it
     * @param schema
     * @param ctx
     * @throws Exception 
     */
	public void refreshSchema(/*Schema schema, */IObjectIdentifier identifier, IRuntimeContext ctx) throws Exception;

	
	public List<Member> getChilds(String uname, String schemaId, IRuntimeContext ctx) throws Exception;

	List<String> searchOnDimensions(String word, String levelUname, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception;

	IObjectIdentifier getSchemaObjectIdentifier(String schemaId) throws Exception;
}
