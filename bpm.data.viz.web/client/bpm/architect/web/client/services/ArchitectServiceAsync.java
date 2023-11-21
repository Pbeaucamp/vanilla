package bpm.architect.web.client.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import bpm.architect.web.shared.HistoricLog;
import bpm.architect.web.shared.HistoricLog.HistoricType;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.data.viz.core.preparation.ExportPreparationInfo;
import bpm.gwt.commons.shared.analysis.ChartData;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.model.supplier.Supplier;
import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.chart.Serie;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ArchitectServiceAsync {

	public void initSession(AsyncCallback<Void> callback);
	
	public void getContracts(AsyncCallback<List<Contract>> callback);

	public void confirmUpload(Contract contract, String documentName, String filePath, AsyncCallback<Void> callback);

	public void addLinkedItem(DocumentItem docItem, AsyncCallback<Void> callback);

	public void removeLinkedItem(DocumentItem docItem, AsyncCallback<Void> callback);

	public void getLinkedItems(int contractId, AsyncCallback<List<DocumentItem>> callback);

	public void getItemHistoric(int itemId, Date startDate, Date endDate, AsyncCallback<List<ItemInstance>> callback);

	public void saveOrUpdateContract(Contract contract, HistoricType type, AsyncCallback<Void> callback);

	public void removeContract(Contract contract, AsyncCallback<Void> callback);

	public void loadDocument(Contract contract, Integer versionId, AsyncCallback<String> callback);

	public void saveOrUpdateSupplier(Supplier supplier, List<Group> groups, AsyncCallback<Void> callback);

	public void getSuppliers(AsyncCallback<List<Supplier>> callback);
	
	public void getSupplierSecurity(int supplierId, AsyncCallback<List<Integer>> callback);
	
	public void removeSupplier(Supplier supplier, AsyncCallback<Void> callback);
	
	public void buildHistoricLogs(Contract contract, AsyncCallback<List<HistoricLog>> callback);
	
	public void getAvailableClasses(AsyncCallback<List<ClassDefinition>> callback);
	
	public void buildClassDefinition(ClassDefinition myClass, AsyncCallback<ClassDefinition> callback);

	public void saveOrUpdateClassRule(ClassRule classRule, AsyncCallback<ClassRule> callback);

	public void deleteClassRule(ClassRule classRule, AsyncCallback<Void> callback);

	public void getDataPreparations(AsyncCallback<List<DataPreparation>> asyncCallback);
	
	public void saveDataPreparation(DataPreparation dataPrep, AsyncCallback<DataPreparation> callback);
	
	public void deleteDataPreparation(DataPreparation dataPrep, AsyncCallback<Void> callback);

	public void executeDataPreparation(DataPreparation dataPrep, AsyncCallback<DataPreparationResult> callback);

	public void countDataPreparation(DataPreparation dataPrep, AsyncCallback<Integer> callback);
	
	public void updateDataset(Dataset dataset, AsyncCallback<Void> callback);

	public void exportPreparation(ExportPreparationInfo info, AsyncCallback<String> asyncCallback);

	public void undo(DataPreparation dataPrep, AsyncCallback<DataPreparation> callback);

	public void redo(DataPreparation dataPrep, AsyncCallback<DataPreparation> asyncCallback);
	
	public void exportToCkan(String resourceName, CkanPackage pack, String exportFileKey, AsyncCallback<Void> asyncCallback);

	public void map(DataPreparation dp, AsyncCallback<String> callback);

	public void getFormat(Contract contract, AsyncCallback<String> callback);

	public void getRScript(Dataset dataset, AsyncCallback<RScriptModel> callback);

	public void publicationETL(DataPreparation dp, AsyncCallback<String> callback);

	public void getRepositoryItemById(int repositoryId, AsyncCallback<RepositoryItem> callback);

	public void getPublicUrl(int itemId, AsyncCallback<String> callback);

	public void createDatabase(String tableName, DataPreparation dataPrep, boolean insert, AsyncCallback<Void> callback);
	
	public void getChartData(DataPreparationResult result, DataColumn axeX, List<Serie<DataColumn>> series, AsyncCallback<List<ChartData>> callback);
	
	public void getMaps(AsyncCallback<List<MapVanilla>> callback);

	public void getCities(AsyncCallback<Map<String, String>> callback);
}
