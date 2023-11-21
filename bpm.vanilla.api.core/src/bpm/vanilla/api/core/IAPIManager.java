package bpm.vanilla.api.core;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IAPIManager {

	public static final String SERVLET_API_XSTREAM = "/apiXStreamServlet";
	public static final String SERVLET_API = "/apiServlet";
	public static final String STREAM_API = "/streamServlet";
	
	public static final String PARAM_TYPE_API = "type";
	public static final String PARAM_METHOD_API = "method";
	public static final String PARAM_PARAMETERS_API = "parameters";
	
	public static enum ApiActionType implements IXmlActionType {
		GENERATE_PROCESS, GET_INTEGRATION_PROCESS_BY_LIMESURVEY, GET_INTEGRATION_PROCESS_BY_CONTRACT, GET_VANILLA_HUBS, RUN_VANILLA_HUB, GET_VANILLA_HUB_PROGRESS, 
		GENERATE_KPI, GET_INTEGRATION_KPI_BY_DATASET_ID, GET_INTEGRATION_INFOS_BY_ORGANISATION, GET_VALIDATION_SCHEMAS, REMOVE_INTEGRATION, UPDATE_SCHEDULE, VALIDATE_DATA,
		GET_ITEM_INFORMATIONS, RUN_ETL, RUN_WORKFLOW, RUN_REPORT;

		@Override
		public Level getLevel() {
			return null;
		}
	}
	
	public enum IAPIType {
		USER, KPI, METADATA, REPOSITORIES, VIEWER, OLAP, FWR
	}
	
	public interface IMethod {
		
	}
	
	public enum MetadataMethod implements IMethod {
		GET_METADATAS,
		LOAD_METADATA,
		GET_BUSINESS_MODELS,
		GET_BUSINESS_PACKAGES,
		GET_BUSINESS_PACKAGE_QUERIES,
		GET_SAVED_QUERY_DATA,
		GET_BUSINESS_TABLES,
		GET_COLUMNS,
		GET_COLUMN,
		GET_TABLES_AND_COLUMNS,
		GET_QUERY_RESULT,
		GET_QUERY_SQL,
		SAVE_QUERY,
	}
		
	public enum RepositoriesMethod implements IMethod {
		GET_ITEMS,
		GET_ALL_ITEMS
	}
		
	public enum ViewerMethod implements IMethod {
		LOAD_VIEWERS,
		GET_VIEWER_INFORMATION,
		OPEN_VIEWER,
		HISTORIZE_VIEWER,
		SAVE_VIEWER_CONFIG
	}
	
	public enum OLAPMethod implements IMethod{
		GET_CUBES,
		EXECUTE_CUBE_QUERY,
		GET_CUBE_VIEW,
		SAVE_CUBE_VIEW,
		GET_DETAILS,
		SEARCH_ON_DIMENSIONS,
		GET_PARAMETERS,
		SET_PARAMETERS
	}
	
	public enum FWRMethod implements IMethod{
		PREVIEW_REPORT,
		GET_FWR_REPORTS,
		SAVE_REPORT,
		LOAD_REPORT
	}
	
	public enum KPIMethod implements IMethod {
		GET_AXIS,
		GET_OBSERVATORIES,
		GET_OBSERVATORIES_BY_GROUP,
		GET_OBSERVATORIES_BY_USER,
		GET_THEMES,
		GET_THEMES_BY_GROUP,
		GET_THEMES_BY_USER,
		GET_KPIS_BY_USER,
		GET_KPIS_BY_GROUP,
		GET_KPIS_BY_OBSERVATORY,
		GET_THEMES_BY_OBSERVATORY,
		GET_KPIS_BY_THEME,
		GET_KPI_VALUE,
		GET_KPI_VALUES,
		GET_KPI_AXIS,
		GET_AXIS_MEMBERS,
		GET_GROUP_AXIS_VALUES,
		GET_GROUP_AXIS_VALUE
	}
}
