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
import bpm.gwt.commons.shared.InfoShareCube;
import bpm.gwt.commons.shared.analysis.DrillInformations;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.vanilla.platform.core.beans.data.DatasourceCsv;
import bpm.vanilla.platform.core.repository.Comment;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FaWebServiceAsync {

	public void initSession(AsyncCallback<Integer> callback);
	
	public void getRepositories(int keySession, int type, AsyncCallback<TreeParentDTO> callback);
	
	public void browseFASDModel(int keySession, int fasdId, AsyncCallback<List<String>> callback);
	
	public void loadSavedView(int keySession, String viewKey, HashMap<String, String> parameters, AsyncCallback<InfosReport> callback);
	
	public void getInfosCubeService(int keySession, ItemCube selectedCube, String datasource, AsyncCallback<InfosReport> callback);
	
	public void swapAxesService(int keySession, boolean isProjection,AsyncCallback<InfosReport> callback);
	
	public void drillService(int keySession, int row, int cell, Projection projection, AsyncCallback<InfosReport> callback);
	
	public void drillAllService(int keySession, int row, int cell, Projection projection, boolean isDrillDown, AsyncCallback<InfosReport> callback);
	
	public void drillMultipleService(int keySession, int row, int cell ,AsyncCallback<HashMap<String, String>> callback);
	
	public void refreshService(int keySession, boolean isProjection,AsyncCallback<InfosReport> callback);
	
	public void UndoRedoService(int keySession, int type ,AsyncCallback<List<String>> callback);
	
	public void addService(int keySession, String e,int row, int cell, boolean before , AsyncCallback<InfosReport> callback);
	
	public void addService(int keySession, int rows, int cells ,int rowt, int cellt, boolean before,  AsyncCallback<InfosReport> callback);
	
	public void filterService(int keySession, int row, int cell , AsyncCallback<InfosReport> callback);
	
	public void filterService(int keySession, List<String> filters , AsyncCallback<InfosReport> callback);
	
	public void filterService(int keySession, String uname , AsyncCallback<InfosReport> callback);
	
	public void removefilterService(int keySession, String uname ,AsyncCallback<InfosReport> callback);
	
	public void removeService(int keySession, int row, int cell, AsyncCallback<InfosReport> callback);

	public void setDataService(int keySession, String d ,AsyncCallback<String> callback);

	public void showEmptyService(int keySession, boolean isProjection,AsyncCallback<InfosReport> callback);

	public void saveService(int keySession, String name, String group, ChartParameters chartParams,  List<Calcul> calculs, int dirId, String comment, String internalVersion, String publicVersion, String gridHtml, InfosReport infosReport, MapOptions mapOptions, AsyncCallback<Integer> callback);

	public void moveService(int keySession, int rows, int cells, int rowt, int cellt, boolean before ,AsyncCallback<InfosReport> callback);

	public void addService(int keySession, List<String> items, boolean col ,AsyncCallback<InfosReport> callback);

	public void removeService(int keySession, List<String> items, AsyncCallback<InfosReport> callback);

	public void setViewsService(int keySession,  AsyncCallback<List<ItemView>> callback);

	public void addChildsService(int keySession, ItemDim itemdim,  AsyncCallback<List<ItemOlapMember>> callback);

	public void exportCube(InfoShareCube infoShare, String chartSVG, String gridHtml, AsyncCallback<ExportResult> callback);

	public void propertiesService(int keySession,  AsyncCallback<InfosReport> callback);

	public void mdxService(int keySession,  AsyncCallback<String> callback);
	
	public void getComplexGridService(int keySession,  AsyncCallback<GridComplex> callback);

	public void drillThroughExportXls(InfoShareCube infoShare, AsyncCallback<ExportResult> callback);
	
	public void addMultiple(int keySession, List<String> rows, List<String> cols, List<String> filters,  AsyncCallback<InfosReport> callback);

	public void addTopx(int keySession, String element, String target, int count,  AsyncCallback<InfosReport> callback);

	public void removeTopx(int keySession, String element,  AsyncCallback<InfosReport> callback);

	public void addPersonalName(int keySession, String uname, String pname,  AsyncCallback<InfosReport> callback);

	public void removePersonalName(int keySession, String uname,  AsyncCallback<InfosReport> callback);

	public void addPercentMeasures(int keySession, HashMap<String, Boolean> percentMeasures,  AsyncCallback<InfosReport> callback);

	public void setShowTotals(int keySession,  AsyncCallback<InfosReport> callback);

	public void searchDimensions(int keySession, String word, String level,  AsyncCallback<List<String>> callback);

	public void getImagesForBackground(AsyncCallback<List<IDirectoryDTO>> callback);

	public void getReporterSubItems(int keySession, String uname, String hiera,  AsyncCallback<List<ItemDim>> callback);

	public void setReportTitle(int keySession, String reportTitle,  AsyncCallback<Void> callback);

	public void addViewParameters(int keySession, List<ParameterDTO> parameters,  AsyncCallback<Void> callback);

	public void getLevels(int keySession,  AsyncCallback<LinkedHashMap<String, LinkedHashMap<String, String>>> callback);

	public void getParametersForView(int keySession, String viewId, String cubeName, int fasdId,  boolean fromPortal, AsyncCallback<HashMap<ParameterDTO, List<String>>> callback);

	public void createSnapshot(int keySession, String name, ChartParameters chartParams, List<Calcul> calculs, String gridHtml, InfosReport infosReport, MapOptions mapOptions,  AsyncCallback<Void> callback);

	public void getSnapshots(int keySession,  AsyncCallback<List<ItemView>> callback);

	public void saveSnapshots(int keySession, List<ItemView> views,  AsyncCallback<String> callback);

	public void createDashboard(int keySession, List<ItemView> views,  AsyncCallback<String> callback);

	public void saveDashboard(int keySession, List<ItemView> views,  String name, String group, String comment, String publicVersion, String internalVersion, int dirId, AsyncCallback<String> callback);

//	public void drillMap(int keySession, String selectedMeasure, String selectedDimension,  String internalId, AsyncCallback<List<List<String>>> callback);
	
	public void closeCube(int keySession, AsyncCallback<Void> callback);
	
	public void addAnnotations(int keySession, String annote, AsyncCallback<Comment> callback);
	
	public void getCurrentCubeItemId(int keySession, AsyncCallback<Integer> callback);
	
	public void canComment(int keySession, int fasdId, AsyncCallback<Boolean> callback);

	public void openCubeFromPortal(int keySession, int fasdId, String cubeName, String dimName, String memberName, AsyncCallback<InfosReport> callback);
	
	public void openViewFromPortal(int keySession, int fasdId, String cubeName, String viewName, HashMap<String, String> parameters, AsyncCallback<InfosReport> callback);

	public void drillThroughProjection(int keySession, int row, int col, Projection projection, AsyncCallback<List<List<DataField>>> callback);

	public void createNewProjection(int keySession, Projection projection, AsyncCallback<InfosReport> callback);

	public void saveProjection(int keySession, Projection projection, String name, String comment, String internalVersion, String publicVersion, String group, int directoryId, AsyncCallback<Void> callback);

	public void loadProjection(int keySession, int projectionId, AsyncCallback<InfosReport> callback);

	public void getGridCubeForActualQuery(int keySession, boolean isProjection, boolean refresh, AsyncCallback<GridCube> callback);

	public void saveFilterConfig(int keySession, FilterConfigDTO config, boolean execute, AsyncCallback<InfosReport> callback);

	public void getFilterConfigs(int keySession, AsyncCallback<List<FilterConfigDTO>> callback);

	public void loadFilterConfig(int keySession, FilterConfigDTO config, AsyncCallback<InfosReport> callback);

	public void deleteFilterConfig(FilterConfigDTO config, AsyncCallback<Void> callback);

	public void getPossibleValuesForParameter(int keySession, List<DrillParameterDTO> paramaters, AsyncCallback<List<DrillParameterDTO>> callback);

	public void runDrill(int keySession, DrillDTO drill, AsyncCallback<String> callback);
	
	public void executeQuery(int keySession, List<String> groups, List<String> datas, List<String> filters, String measure, AsyncCallback<List<GroupChart>> callback);

	public void exportFromDrilThroughPDF(boolean isLandscape, int pageSize, String title, String origin, String description, String filters, List<String> headerList, List<List<String>> cellList,AsyncCallback<String> callback);
	
	public void exportFromDrillThroughXLS(String title, String description, String origin, String filters, List<String> headerList, List<List<String>> cellList,AsyncCallback<String> callback);
	
	public void getMapDefinitions(int keySession, String label, AsyncCallback<List<List<String>>> callback);
	
	public void buildOpenLayerMap(int keySession,  Integer id, String label, String uname, AsyncCallback<OpenLayer> callback);

	public void drillThroughService(int keySession, int row, int cell , Projection projection, AsyncCallback<DrillInformations> callback);
	
	public void applyDrillFilter(DrillInformations drillInfo, List<DrillthroughFilter> filters, AsyncCallback<DrillInformations> asyncCallback);
	
	public void getPartOfDrills(int key, int start, int length, Integer indexSort, boolean ascending, boolean changed, AsyncCallback<List<List<String>>> asyncCallback);

	public void setSortingElements(int keySession, HashMap<String, String> sortElements, AsyncCallback<InfosReport> callback);

	public void updateView(int keySession, ChartParameters chartParams, String gridHtml, List<Calcul> calculs, InfosReport infosReport, MapOptions mapOptions, AsyncCallback<Void> asyncCallback);

	public void getOsmValues(int keySession, String uname, String selectedMeasure, int datasetId, String selectedDimension, List<List<String>> colors, AsyncCallback<List<MapValues>> callback);

	public void openCubeFromFMDTWeb( int keySession, String sessionId, AsyncCallback<InfosReport> callback);

	public void uploadCsv(int keySession, String name, boolean hasHeader, String separator, DrillInformations drillInfos, AsyncCallback<DatasourceCsv> asyncCallback);

	public void getMapInfo(String uname, String element, AsyncCallback<MapInfo> callback);

}
