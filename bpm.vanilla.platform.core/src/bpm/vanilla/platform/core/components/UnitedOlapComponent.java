package bpm.vanilla.platform.core.components;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;


/**
 * simple interface for UnitedOlap component
 * 
 * The load Balancing i smade by the FASD model identifier.
 * The allow this, we will add 2 parameters for the HTTP requests
 * 
 * The VanillaConstants.HTTP_HEADER_SERVLET_DISPATCH_ACTION will be used
 * to adress to the right UnitedOalp Wrapper servlet(model, runtime) 
 * 
 * 
 * @author ludo
 *
 */
public interface UnitedOlapComponent {
	public static final String HTTP_SERVLET_ACTION_MODEL = "bpm.vanilla.platform.core.components.UnitedOlapComponent.httpAction.model";
	public static final String HTTP_SERVLET_ACTION_RUNTIME = "bpm.vanilla.platform.core.components.UnitedOlapComponent.httpAction.runtime";
	
	public static final String SERVLET_PARAMETER_REPOSITORY_ID = "repositoryId";
	public static final String SERVLET_PARAMETER_DIRECTORY_ITEM_ID = "directoryItemId";
	public static final String SERVLET_PARAMETER_SCHEMA_ID = "schemaId";
	
	public static final String SERVLET_MODEL = "/unitedolap/modelService";
	public static final String SERVLET_RUNTIME = "/unitedolap/runtimeService";
	public static final String SERVLET_CACHE = "/unitedolap/cacheManagerService";
	public static final String UOLAP_LOAD_EVALUATOR_SERVLET = "/unitedolap/loadEvaluator";
	public static final String SERVLET_EXCEL = "/unitedolap/excelServlet";
	public static final String DATAPREP_SERVLET_EXCEL = "/unitedolap/dataPrepExcelServlet";
	
	/**
	 * Type of action
	 * @author Marc Lanquetin
	 *
	 */
	public static enum ActionTypes implements IXmlActionType{
		
		//cube actions
		LOAD(Level.DEBUG), UNLOAD(Level.DEBUG), SUBMEMBERS(Level.DEBUG), SEARCHDIMS(Level.DEBUG), LOAD_DIM(Level.DEBUG), REFRESH(Level.DEBUG), CHILDS(Level.DEBUG), EXPLORE(Level.DEBUG), DISTINCTVALUES(Level.DEBUG), REFRESHTIMEDIM(Level.DEBUG),
		
		//engine actions
		EXECUTE_QUERY(Level.DEBUG), EXECUTE_QUERY_FMDT(Level.DEBUG), DRILLTHROUGH(Level.DEBUG), EXECUTE_QUERY_WITH_LIMIT(Level.DEBUG), FIND_SCHEMA(Level.DEBUG), PRELOAD(Level.DEBUG), EXECUTE_FMDT_QUERY(Level.DEBUG), 
		CREATE_EXTRAPOLATION(Level.DEBUG), EXECUTE_EXTRAPOLATION(Level.DEBUG),
		
		//cache actions
		CLEAR_MEMORY_CACHE(Level.DEBUG), CLEAR_DISK_CACHE(Level.DEBUG), MEMORY_STATS(Level.DEBUG), DISK_STATS(Level.DEBUG), DISK_KEYS(Level.DEBUG), DISK_ENTRY(Level.DEBUG), REMOVE_FROM_CACHEDISK(Level.DEBUG), 
		PERSIST_CACHEDISK(Level.DEBUG), APPEND_TO_CACHE(Level.DEBUG), LOAD_CACHE_ENTRY(Level.DEBUG), FIND_SCHEMA_IDENTIFIER(Level.DEBUG), REMOVE_CACHE(Level.DEBUG), RELOAD_CACHE(Level.DEBUG);
		
		private Level level;

		ActionTypes(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
}

