package bpm.united.olap.runtime.query;

import bpm.united.olap.api.data.IExternalQueryIdentifier;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.result.DrillThroughIdentifier;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * Help with query execution
 * @author Marc Lanquetin
 *
 */
public interface IQueryHelper {
	
	/**
	 * create the Query String for the factTable and evantually improve it
	 * 
	 * @param cubeInstance
	 * @throws Exception 
	 */
	public String prepareQuery(ICubeInstance cubeInstance) throws Exception;
	
	
	/**
	 * if prepareQuery has not been called, it is called firstly
	 * 
	 * execute the query to get the values
	 * @param storage
	 * @return a filled dataStorage
	 */
	DataStorage executeQuery(ICubeInstance cubeInstance, DataStorage storage, int limit) throws Exception;
	
	/**
	 * Find/Load the members for a given level
	 * @param level
	 * @param memberName create this member only. Load all the level if its a level name or null
	 * @param parentMembers
	 * @param datasource
	 * @return
	 * @throws Exception
	 */
//	List<Member> findMembers(Level level, String memberName, List<String> parentMembers) throws Exception;
	
	void setCacheServer(ICacheServer server);
	
	/**
	 * Execute the query for the dimension dataObject and create the dimensionTable
	 * @param dataObject
	 * @return
	 * @throws Exception
	 */
//	public IDimensionTable getDimensionTableFromOda(DataObject dataObject, Dimension dim, Hierarchy hiera) throws Exception;
	
	IVanillaLogger getLogger();
	
	/**
	 * Get the dataObject table
	 * Search the dataObject in cache or get it from the oda query
	 * @param dataObject
	 * @return 
	 * @throws Exception
	 */
//	IDimensionTable getDimensionTable(DataObject dataObject, Dimension dim, Hierarchy hiera) throws Exception;

	/**
	 * Drillthrough
	 * @param schema
	 * @param nodeIds identifier of the selected cell
	 * @return an OlapResult with the first as column names and next lines as values
	 */
	public OlapResult drillthrough(ICubeInstance cubeInstance, DrillThroughIdentifier identifier) throws Exception ;
	
	public OlapResult advancedDrillthrough(ICubeInstance cubeInstance, DrillThroughIdentifier identifier) throws Exception ;
	
	 /**
	  * If Sql queries need to be in streamMode
	  * @param isInStreamMode
	  */
	 void setSqlStreamMode(boolean isInStreamMode);
	 
	 /**
	  * Clear tables in memory
	  */
	 void clearTablesInMemory();


	public String getEffectiveQuery();

	/**
	 * Return an OlapResult representing the resultSet of a FMDT query
	 * @param identifier
	 * @param cubeInstance 
	 * @return
	 * @throws Exception 
	 */
	public OlapResult executeFmdtQuery(IExternalQueryIdentifier identifier, ICubeInstance cubeInstance) throws Exception;


	public OlapResult advancedDrillthrough(ICubeInstance cubeInstance, DrillThroughIdentifier dataCellId, Projection projection) throws Exception;
}
