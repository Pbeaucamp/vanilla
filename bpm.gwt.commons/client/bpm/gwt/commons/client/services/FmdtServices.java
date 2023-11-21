package bpm.gwt.commons.client.services;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.analysis.ChartData;
import bpm.gwt.commons.shared.fmdt.FmdtModel;
import bpm.gwt.commons.shared.fmdt.FmdtNode;
import bpm.gwt.commons.shared.fmdt.FmdtQueryBuilder;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDatas;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.gwt.commons.shared.fmdt.FmdtRow;
import bpm.gwt.commons.shared.fmdt.FmdtTable;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.vanilla.platform.core.beans.FmdtDriller;
import bpm.vanilla.platform.core.beans.FmdtDrillerFilter;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.chart.SavedChart;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtDimension;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.D4C;
import bpm.vanilla.platform.core.repository.RepositoryItem;

@RemoteServiceRelativePath("fmdtService")
public interface FmdtServices extends RemoteService {

	public static class Connect {
		private static FmdtServicesAsync instance;

		public static FmdtServicesAsync getInstance() {
			if (instance == null) {
				instance = (FmdtServicesAsync) GWT.create(FmdtServices.class);
			}
			return instance;
		}
	}

	public String exportToXLS(FmdtRow columnNames, FmdtTable table, String name, String format, String separator) throws ServiceException;

	public int save(int selectedDirectoryId, FmdtQueryDriller fmdtDriller, List<SavedChart> charts) throws ServiceException;

	public void update(int directoryItemId, int metadataId, FmdtQueryDriller fmdtDriller, List<SavedChart> charts) throws ServiceException;

	public void delete(int directoryItemId) throws ServiceException;

	public List<FmdtModel> getMetadataInfos(int metadataId) throws ServiceException;

	public List<String> getTablesName(FmdtDriller fmdtDriller) throws ServiceException;

	public FmdtTable getTable(FmdtDriller fmdtDriller) throws ServiceException;

	public FmdtTable drillOnTable(FmdtQueryDriller fmdtDriller, String originTable, String destinationTable, HashMap<String, String> values) throws ServiceException;

	public FmdtQueryDriller openView(RepositoryItem item) throws ServiceException;

	public FmdtTable manageFilter(FmdtDriller fmdtDriller, FmdtTable table, FmdtDrillerFilter filter, boolean add) throws ServiceException;

	public FmdtTable addOrderBy(FmdtDriller fmdtDriller, FmdtTable table, String field, boolean group) throws ServiceException;

	public FmdtTable updateValues(FmdtDriller fmdtDriller, FmdtTable table) throws ServiceException;

	public FmdtQueryDatas getTables(FmdtQueryDriller fmdtDriller) throws ServiceException;

	public List<FmdtTable> getRequestValue(FmdtQueryBuilder builder, FmdtQueryDriller fmdtDriller, Boolean formatted) throws ServiceException;

	public List<ChartData> getChartData(FmdtQueryBuilder builder, FmdtQueryDriller fmdtDriller, Boolean formatted) throws ServiceException;

	public String getRequest(FmdtQueryBuilder builder, int metadataId, String modelName, String packageName) throws ServiceException;

	public String exportToXLS(FmdtTable table, String name, String format, String separator) throws ServiceException;

	FmdtTable getGraphicTable(FmdtData colGroup, List<FmdtData> colSeries, List<String> serieNames, String operation, FmdtQueryDriller fmdtDriller, FmdtQueryBuilder builder) throws ServiceException;

	public String createCube(List<FmdtDimension> dimensions, List<FmdtData> measures, FmdtQueryDriller fmdtDriller, FmdtQueryBuilder builder, String title, String description, String local) throws ServiceException;

	public int saveCube(int selectedDirectoryId, String name, String description, Group group, String model) throws ServiceException;

	public String generateCubeUrl(String local, HashMap<String, String> parameters, int dest) throws ServiceException;

	public Double[][] launchRFunctions(List<List<Double>> varX, List<List<Double>> varY,  String function, int nbcluster) throws ServiceException, Exception;

	public List<FmdtNode> launchDecisionTree(List<List<Double>> varX, List<String> varY, Double train, List<String> names) throws Exception;

	public FmdtQueryDatas getMetadataData(FmdtQueryDriller driller, DatasourceFmdt datasourceFmdt) throws ServiceException;
	
	public Dataset getDatasetFromFmdtQuery(FmdtQueryBuilder builder, int metadataId, String modelName, String packageName) throws ServiceException;

	public FmdtQueryBuilder loadMetadataView(FmdtQueryDriller driller, DatasourceFmdt datasourceFmdt, Dataset dataset) throws ServiceException;

	public List<FmdtModel> getMetadataInfos(int metadataId, String vanillaUrl, String login, String password, Group group, Repository repository) throws ServiceException;
	
	public List<DatabaseTable> getDatabaseStructure(Datasource datasource, boolean managePostgres) throws ServiceException;

	public void updateData(FmdtTable currentTable, FmdtRow currentValue, FmdtDriller drill) throws ServiceException;

	public List<Metadata> getMetadatas() throws ServiceException;
	
	public Metadata openMetadata(RepositoryItem item) throws ServiceException;

	public void exportToCkan(String resourceName, CkanPackage pack, FmdtRow columnNames, FmdtTable table, String separator, FmdtQueryBuilder builder) throws ServiceException;

	public void createDataPreparation(String name, FmdtQueryDriller fmdtDriller) throws ServiceException;
	
	public Dataset buildDatasetFromSavedQuery(int metadataId, String modelName, String packageName, String queryName) throws ServiceException;

	public int getRequestCount(FmdtQueryBuilder builder, FmdtQueryDriller driller) throws ServiceException;

	public MetadataData getTableData(Datasource datasource, DatabaseTable selectedTable, DatabaseColumn selectedColumn, int limit, boolean distinct) throws ServiceException;
	
	public List<DatabaseTable> getDatabaseStructure(D4C d4c, String organisation, Datasource datasource, boolean managePostgres) throws ServiceException;
}
