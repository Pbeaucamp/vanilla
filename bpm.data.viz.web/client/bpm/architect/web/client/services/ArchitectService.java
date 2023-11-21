package bpm.architect.web.client.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import bpm.architect.web.shared.HistoricLog;
import bpm.architect.web.shared.HistoricLog.HistoricType;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.data.viz.core.preparation.ExportPreparationInfo;
import bpm.gwt.commons.client.services.exception.ServiceException;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("architect")
public interface ArchitectService extends RemoteService {

	public static class Connect {
		private static ArchitectServiceAsync instance;

		public static ArchitectServiceAsync getInstance() {
			if (instance == null) {
				instance = (ArchitectServiceAsync) GWT.create(ArchitectService.class);
			}
			return instance;
		}
	}

	public void initSession() throws ServiceException;
	
	public List<Contract> getContracts() throws ServiceException;

	public void confirmUpload(Contract contract, String documentName, String filePath) throws ServiceException;

	public void addLinkedItem(DocumentItem docItem) throws ServiceException;

	public void removeLinkedItem(DocumentItem docItem) throws ServiceException;

	public List<DocumentItem> getLinkedItems(int contractId) throws ServiceException;

	public List<ItemInstance> getItemHistoric(int itemId, Date startDate, Date endDate) throws ServiceException;
	
	public void saveOrUpdateContract(Contract contract, HistoricType type) throws ServiceException;
	
	public void removeContract(Contract contract) throws ServiceException;

	public String loadDocument(Contract contract, Integer versionId) throws ServiceException;
	
	public void saveOrUpdateSupplier(Supplier supplier, List<Group> groups) throws ServiceException;

	public List<Supplier> getSuppliers() throws ServiceException;

	public List<Integer> getSupplierSecurity(int supplierId) throws ServiceException;
	
	public void removeSupplier(Supplier supplier) throws ServiceException;

	public List<HistoricLog> buildHistoricLogs(Contract contract) throws ServiceException;
	
	public List<ClassDefinition> getAvailableClasses() throws ServiceException;

	public ClassDefinition buildClassDefinition(ClassDefinition myClass) throws ServiceException;

	public ClassRule saveOrUpdateClassRule(ClassRule classRule) throws ServiceException;

	public void deleteClassRule(ClassRule classRule) throws ServiceException;

	public List<DataPreparation> getDataPreparations() throws ServiceException;

	public DataPreparation saveDataPreparation(DataPreparation dataPrep) throws ServiceException;

	public void deleteDataPreparation(DataPreparation dataPrep) throws ServiceException;
	
	public DataPreparationResult executeDataPreparation(DataPreparation dataPrep) throws ServiceException;

	public Integer countDataPreparation(DataPreparation dataPrep) throws ServiceException;

	public String exportPreparation(ExportPreparationInfo info) throws ServiceException;
	
	public DataPreparation undo(DataPreparation dataPrep) throws ServiceException;

	public DataPreparation redo(DataPreparation dataPrep) throws ServiceException;
	
	public void exportToCkan(String resourceName, CkanPackage pack, String exportFileKey) throws ServiceException;

	public void updateDataset(Dataset dataset) throws Exception;

	public String map(DataPreparation dp) throws Exception;

	public String getFormat(Contract contract) throws ServiceException;

	public RScriptModel getRScript(Dataset dataset) throws ServiceException;

	public String publicationETL(DataPreparation dp) throws Exception;

	public void createDatabase(String tableName, DataPreparation dataPrep, boolean insert) throws Exception;

	public RepositoryItem getRepositoryItemById(int repositoryId);

	public String getPublicUrl(int itemId) throws ServiceException;
	
	public List<ChartData> getChartData(DataPreparationResult result, DataColumn axeX, List<Serie<DataColumn>> series) throws ServiceException;
	
	public List<MapVanilla> getMaps() throws ServiceException;

	public Map<String, String> getCities() throws ServiceException;
}
