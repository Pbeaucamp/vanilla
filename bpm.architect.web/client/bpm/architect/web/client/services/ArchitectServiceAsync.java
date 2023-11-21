package bpm.architect.web.client.services;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import bpm.architect.web.shared.HistoricLog;
import bpm.architect.web.shared.HistoricLog.HistoricType;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;

public interface ArchitectServiceAsync {

	public void initSession(AsyncCallback<Void> callback);
	
	public void getContracts(Integer directoryId, String query, int firstResult, int length, DataSort sort, AsyncCallback<DataWithCount<Contract>> callback);

	public void confirmUpload(Contract contract, String documentName, String filePath, AsyncCallback<Void> callback);

	public void saveOrUpdateDocumentItem(DocumentItem docItem, AsyncCallback<Void> callback);

	public void removeLinkedItem(DocumentItem docItem, AsyncCallback<Void> callback);

	public void getItemHistoric(int itemId, Date startDate, Date endDate, AsyncCallback<List<ItemInstance>> callback);

	public void saveOrUpdateContract(Contract contract, HistoricType type, AsyncCallback<Void> callback);

	public void removeContract(Contract contract, AsyncCallback<Void> callback);

	public void loadDocument(Contract contract, Integer versionId, AsyncCallback<String> callback);

	public void saveOrUpdateSupplier(Supplier supplier, List<Group> groups, AsyncCallback<Supplier> callback);

	public void getSuppliers(AsyncCallback<List<Supplier>> callback);
	
	public void getSupplierSecurity(int supplierId, AsyncCallback<List<Integer>> callback);

	public void removeSupplier(Supplier supplier, AsyncCallback<Void> callback);
	
	public void buildHistoricLogs(Contract contract, AsyncCallback<List<HistoricLog>> callback);
	
	public void getAvailableClasses(AsyncCallback<List<ClassDefinition>> callback);
	
	public void buildClassDefinition(ClassDefinition myClass, AsyncCallback<ClassDefinition> callback);

	public void saveOrUpdateClassRule(ClassRule classRule, AsyncCallback<ClassRule> callback);

	public void deleteClassRule(ClassRule classRule, AsyncCallback<Void> callback);

	public void generateIntegrationProcess(ContractIntegrationInformations integrationInfos, AsyncCallback<Void> callback);
	
	public void getIntegrationInfos(int contractId, AsyncCallback<ContractIntegrationInformations> callback);
	
	public void getSourceUrl(ContractIntegrationInformations integrationInfos, AsyncCallback<String> callback);

	public void getDatasetUrl(ContractIntegrationInformations integrationInfos, AsyncCallback<String> callback);

	public void saveOrUpdateSchema(DocumentSchema docSchema, AsyncCallback<Void> callback);

	public void removeLinkedSchema(DocumentSchema docSchema, AsyncCallback<Void> callback);
	
	public void deleteIntegration(ContractIntegrationInformations integrationInfos, AsyncCallback<Void> callback);
	
	public void deleteSchema(ClassDefinition classDef, AsyncCallback<Void> callback);
	
	public void loadSchema(ClassDefinition schema, AsyncCallback<String> callback);
	
	public void addAPISchema(String name, String url, AsyncCallback<Void> callback);
}
