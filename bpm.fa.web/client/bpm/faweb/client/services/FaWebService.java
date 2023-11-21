package bpm.faweb.client.services;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.faweb.client.projection.Projection;
import bpm.faweb.client.projection.data.DataField;
import bpm.faweb.shared.ChartParameters;
import bpm.faweb.shared.DrillDTO;
import bpm.faweb.shared.DrillParameterDTO;
import bpm.faweb.shared.FilterConfigDTO;
import bpm.faweb.shared.GroupChart;
import bpm.faweb.shared.IDirectoryDTO;
import bpm.faweb.shared.ItemCube;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.MapValues;
import bpm.faweb.shared.OpenLayer;
import bpm.faweb.shared.ParameterDTO;
import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.drill.DrillthroughFilter;
import bpm.faweb.shared.infoscube.Calcul;
import bpm.faweb.shared.infoscube.GridComplex;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.faweb.shared.infoscube.ItemDim;
import bpm.faweb.shared.infoscube.ItemOlapMember;
import bpm.faweb.shared.infoscube.ItemView;
import bpm.faweb.shared.infoscube.MapInfo;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.InfoShareCube;
import bpm.gwt.commons.shared.analysis.DrillInformations;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.vanilla.platform.core.beans.data.DatasourceCsv;
import bpm.vanilla.platform.core.repository.Comment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("faWebService")
public interface FaWebService extends RemoteService {
	public static final int FASD = 0;
	public static final int FAV = 1;
	public static final int FD = 2;
	public static final int PROJECTION = 3;
	
	public static class Connect{
		private static FaWebServiceAsync instance;
		public static FaWebServiceAsync getInstance(){
			if(instance == null){
				instance = (FaWebServiceAsync) GWT.create(FaWebService.class);
			}
			return instance;
		}
	}

	public Integer initSession() throws ServiceException;
	
	public TreeParentDTO getRepositories(int keySession, int type) throws ServiceException;
	
	public List<String> browseFASDModel(int keySession, int modelId) throws ServiceException;
	
	public InfosReport loadSavedView(int keySession, String viewKey, HashMap<String, String> parameters);	
	
	public InfosReport getInfosCubeService(int keySession, ItemCube selectedCube, String datasource) throws ServiceException;

	public InfosReport swapAxesService(int keySession, boolean isProjection) throws ServiceException;
	
	public InfosReport showEmptyService(int keySession, boolean isProjection) throws ServiceException;
	
	public InfosReport refreshService(int keySession, boolean isProjection) throws ServiceException;
	
	public InfosReport drillService(int keySession, int row, int cell, Projection projection) throws ServiceException;
	
	public InfosReport drillAllService(int keySession, int row, int cell, Projection projection, boolean isDrillDown) throws ServiceException ;
	
	public HashMap<String, String> drillMultipleService(int keySession, int row, int cell) throws ServiceException;
	
	public List<String> UndoRedoService(int keySession, int type) throws ServiceException; 
	
	public InfosReport addService(int keySession, String source, int row, int cell, boolean before) throws ServiceException;
	
	public InfosReport addService(int keySession, int rows, int cells,int rowt, int cellt, boolean before) throws ServiceException;
	
	public InfosReport filterService(int keySession, int row, int cell) throws ServiceException;
	
	public InfosReport filterService(int keySession, List<String> filters) throws Throwable;
	
	public InfosReport filterService(int keySession, String uname) throws ServiceException;
	
	public InfosReport removefilterService(int keySession, String uname) throws ServiceException;
	
	public InfosReport removeService(int keySession, int row, int cell) throws ServiceException;
	
	public String setDataService(int keySession, String d) throws ServiceException;
	
	public int saveService(int keySession, String name, String group, ChartParameters chartParams, List<Calcul> calculs, int dirId, String comment, String internalVersion, String publicVersion, String gridHtml, InfosReport infosReport, MapOptions mapOptions) throws ServiceException;
	
	public InfosReport moveService(int keySession, int rows, int cells,int rowt, int cellt, boolean before) throws ServiceException;
	
	public InfosReport addService(int keySession, List<String> items, boolean col) throws ServiceException;
	
	public InfosReport removeService(int keySession, List<String> items) throws ServiceException;
	
	public List<ItemView> setViewsService(int keySession) throws ServiceException;
	
	public List<ItemOlapMember> addChildsService(int keySession, ItemDim itemdim) throws ServiceException;
	
	public ExportResult exportCube(InfoShareCube infoShare, String chartSVG, String gridHtml) throws ServiceException;
	
	public InfosReport propertiesService(int keySession) throws ServiceException;
	
	public String mdxService(int keySession) throws ServiceException;
	
	public GridComplex getComplexGridService(int keySession) throws ServiceException;

	public ExportResult drillThroughExportXls(InfoShareCube infoShare) throws ServiceException;
		
	public InfosReport addMultiple(int keySession, List<String> rows, List<String> cols, List<String> filters) throws ServiceException;
	
	public InfosReport addTopx(int keySession, String element, String target, int count) throws ServiceException;
	
	public InfosReport removeTopx(int keySession, String element) throws ServiceException;
	
	public InfosReport addPersonalName(int keySession, String uname, String pname) throws ServiceException;
	
	public InfosReport removePersonalName(int keySession, String uname) throws ServiceException;
	
	public InfosReport addPercentMeasures(int keySession, HashMap<String, Boolean> percentMeasures) throws ServiceException;
	
	public InfosReport setShowTotals(int keySession) throws ServiceException;
	
	public List<String> searchDimensions(int keySession, String word, String level) throws ServiceException;
	
	public List<IDirectoryDTO> getImagesForBackground() throws ServiceException;
	
	public List<ItemDim> getReporterSubItems(int keySession, String uname, String hiera) throws ServiceException;
	
	public void setReportTitle(int keySession, String reportTitle) throws ServiceException;
	
	public void addViewParameters(int keySession, List<ParameterDTO> parameters) throws ServiceException;
	
	public LinkedHashMap<String, LinkedHashMap<String, String>> getLevels(int keySession) throws ServiceException;
	
	public HashMap<ParameterDTO, List<String>> getParametersForView(int keySession, String viewId, String cubeName, int fasdId, boolean fromPortal) throws ServiceException;
	
	public void createSnapshot(int keySession, String name, ChartParameters chartParams, List<Calcul> calculs, String gridHtml, InfosReport infosReport, MapOptions mapOptions) throws ServiceException;
	
	public List<ItemView> getSnapshots(int keySession) throws ServiceException;
	
	public String saveSnapshots(int keySession, List<ItemView> views);
	
	public String createDashboard(int keySession, List<ItemView> views) throws ServiceException;
	
	public String saveDashboard(int keySession, List<ItemView> views, String name, String group, String comment, String publicVersion, String internalVersion, int dirId) throws Exception;

	public MapInfo getMapInfo(String uname, String element) throws ServiceException;

//	public List<List<String>> drillMap(int keySession, String selectedMeasure, String selectedDimension, String internalId) throws ServiceException;
	
	public void closeCube(int keySession);
	
	public Comment addAnnotations(int keySession, String annote) throws ServiceException;
	
	public int getCurrentCubeItemId(int keySession) throws ServiceException;
	
	public boolean canComment(int keySession, int fasdId) throws ServiceException;
	
	public InfosReport openCubeFromPortal(int keySession, int fasdId, String cubeName, String dimName, String memberName) throws ServiceException;
	
	public InfosReport openViewFromPortal(int keySession, int fasdId, String cubeName, String viewName, HashMap<String, String> parameters) throws ServiceException;

	public List<List<DataField>> drillThroughProjection(int keySession, int row, int col, Projection projection) throws ServiceException;
	
	public InfosReport createNewProjection(int keySession, Projection projection) throws ServiceException;
	
	public void saveProjection(int keySession, Projection projection, String name, String comment, String internalVersion, String publicVersion, String group, int directoryId) throws ServiceException;
	
	public InfosReport loadProjection(int keySession, int projectionId) throws ServiceException;
	
	public GridCube getGridCubeForActualQuery(int keySession, boolean isProjection, boolean refresh) throws ServiceException;
	
	public InfosReport saveFilterConfig(int keySession, FilterConfigDTO config, boolean execute) throws Throwable;
	
	public List<FilterConfigDTO> getFilterConfigs(int keySession) throws Throwable;
	
	public InfosReport loadFilterConfig(int keySession, FilterConfigDTO config) throws Throwable;
	
	public void deleteFilterConfig(FilterConfigDTO config) throws Throwable;
	
	public List<DrillParameterDTO> getPossibleValuesForParameter(int keySession, List<DrillParameterDTO> paramaters) throws Throwable;
	
	public String runDrill(int keySession, DrillDTO drill) throws Throwable;
	
	public List<GroupChart> executeQuery(int keySession, List<String> groups, List<String> datas, List<String> filters, String measure) throws ServiceException;
	
	public String exportFromDrilThroughPDF(boolean isLandscape, int pageSize,String title, String origin, String description, String filters,List<String> headerList, List<List<String>> cellList)throws Exception;
	
	public String exportFromDrillThroughXLS(String title, String description, String origin, String filters, List<String> headerList, List<List<String>> cellList)throws Exception;

	public List<List<String>> getMapDefinitions(int keySession, String label) throws Exception;
	
	public OpenLayer buildOpenLayerMap(int keySession, Integer id, String label, String uname) throws Exception;
	
	public DrillInformations drillThroughService(int keySession, int row, int cell, Projection projection) throws ServiceException;
	
	public DrillInformations applyDrillFilter(DrillInformations drillInfo, List<DrillthroughFilter> filters) throws ServiceException;
	
	public List<List<String>> getPartOfDrills(int key, int start, int length, Integer indexSort, boolean ascending, boolean changed) throws Throwable;
	
	public InfosReport setSortingElements(int keySession, HashMap<String, String> sortElements) throws Exception;

	public void updateView(int keySession, ChartParameters chartParams, String gridHtml, List<Calcul> calculs, InfosReport infosReport, MapOptions mapOptions) throws Exception;
	
	public List<MapValues> getOsmValues(int keySession, String uname, String selectedMeasure, int datasetId, String selectedDimension, List<List<String>> colors) throws Exception;

	public InfosReport openCubeFromFMDTWeb(int keySession, String sessionId) throws Exception;

	public DatasourceCsv uploadCsv(int keySession, String name, boolean hasHeader, String separator, DrillInformations drillInfos) throws Exception;
}
