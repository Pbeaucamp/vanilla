package bpm.gwt.commons.client.services;

import java.util.HashMap;
import java.util.List;

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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FmdtServicesAsync {

	public void exportToXLS(FmdtRow columnNames, FmdtTable table, String name, String format, String separator, AsyncCallback<String> callback);

	void save(int selectedDirectoryId, FmdtQueryDriller fmdtDriller, List<SavedChart> charts, AsyncCallback<Integer> callback);

	public void update(int directoryItemId, int metadataId, FmdtQueryDriller fmdtDriller, List<SavedChart> charts, AsyncCallback<Void> callback);

	public void delete(int directoryItemId, AsyncCallback<Void> callback);

	public void getMetadataInfos(int metadataId, AsyncCallback<List<FmdtModel>> callback);

	public void getTablesName(FmdtDriller fmdtDriller, AsyncCallback<List<String>> callback);

	public void getTable(FmdtDriller fmdtDriller, AsyncCallback<FmdtTable> callback);

	public void drillOnTable(FmdtQueryDriller fmdtDriller, String originTable, String destinationTable, HashMap<String, String> values, AsyncCallback<FmdtTable> callback);

	public void openView(RepositoryItem item, AsyncCallback<FmdtQueryDriller> callback);

	public void manageFilter(FmdtDriller fmdtDriller, FmdtTable table, FmdtDrillerFilter filter, boolean add, AsyncCallback<FmdtTable> callback);

	public void addOrderBy(FmdtDriller fmdtDriller, FmdtTable table, String field, boolean group, AsyncCallback<FmdtTable> callback);

	void updateValues(FmdtDriller fmdtDriller, FmdtTable table, AsyncCallback<FmdtTable> callback);

	public void getTables(FmdtQueryDriller fmdtDriller, AsyncCallback<FmdtQueryDatas> callback);

	void getRequestValue(FmdtQueryBuilder builder, FmdtQueryDriller fmdtDriller, Boolean formatted, AsyncCallback<List<FmdtTable>> callback);

	void getChartData(FmdtQueryBuilder builder, FmdtQueryDriller fmdtDriller, Boolean formatted, AsyncCallback<List<ChartData>> callback);

	public void getRequest(FmdtQueryBuilder builder, int metadataId, String modelName, String packageName, AsyncCallback<String> callback);

	void exportToXLS(FmdtTable table, String name, String format, String separator, AsyncCallback<String> callback);

	void getGraphicTable(FmdtData colGroup, List<FmdtData> colSeries, List<String> serieNames, String operation, FmdtQueryDriller fmdtDriller, FmdtQueryBuilder builder, AsyncCallback<FmdtTable> callback);

	public void createCube(List<FmdtDimension> dimensions, List<FmdtData> measures, FmdtQueryDriller fmdtDriller, FmdtQueryBuilder builder, String title, String description, String local, AsyncCallback<String> callback);

	public void saveCube(int selectedDirectoryId, String name, String description, Group group, String model, AsyncCallback<Integer> callback);

	public void generateCubeUrl(String local, HashMap<String, String> parameters, int dest, AsyncCallback<String> callback);

	public void launchRFunctions(List<List<Double>> varX, List<List<Double>> varY,  String function, int nbcluster, AsyncCallback<Double[][]> callback);

	public void launchDecisionTree(List<List<Double>> varX, List<String> varY, Double train, List<String> names, AsyncCallback<List<FmdtNode>> callback);

	public void getMetadataData(FmdtQueryDriller driller, DatasourceFmdt datasourceFmdt, AsyncCallback<FmdtQueryDatas> asyncCallback);

	public void getDatasetFromFmdtQuery(FmdtQueryBuilder builder, int metadataId, String modelName, String packageName, AsyncCallback<Dataset> callback);

	public void loadMetadataView(FmdtQueryDriller driller, DatasourceFmdt datasourceFmdt, Dataset dataset, AsyncCallback<FmdtQueryBuilder> asyncCallback);

	public void getMetadataInfos(int metadataId, String vanillaUrl, String login, String password, Group group, Repository repository, AsyncCallback<List<FmdtModel>> callback);
	
	public void getDatabaseStructure(Datasource datasource, boolean managePostgres, AsyncCallback<List<DatabaseTable>> callback);

	public void updateData(FmdtTable currentTable, FmdtRow currentValue, FmdtDriller drill, AsyncCallback<Void> callback);

	public void getMetadatas(AsyncCallback<List<Metadata>> callback);
	
	public void openMetadata(RepositoryItem item, AsyncCallback<Metadata> callback);

	public void exportToCkan(String resourceName, CkanPackage pack, FmdtRow columnNames, FmdtTable table, String separator, FmdtQueryBuilder builder, AsyncCallback<Void> callback);

	public void createDataPreparation(String name, FmdtQueryDriller fmdtDriller, AsyncCallback<Void> callback);
	
	public void buildDatasetFromSavedQuery(int metadataId, String modelName, String packageName, String queryName, AsyncCallback<Dataset> callback);

	public void getRequestCount(FmdtQueryBuilder builder, FmdtQueryDriller driller, AsyncCallback<Integer> asyncCallback);

	public void getTableData(Datasource datasource, DatabaseTable selectedTable, DatabaseColumn selectedColumn, int limit, boolean distinct, AsyncCallback<MetadataData> callback);
	
	public void getDatabaseStructure(D4C d4c, String organisation, Datasource datasource, boolean managePostgres, AsyncCallback<List<DatabaseTable>> callback);
}
