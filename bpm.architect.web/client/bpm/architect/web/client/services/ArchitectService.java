package bpm.architect.web.client.services;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import bpm.architect.web.shared.HistoricLog;
import bpm.architect.web.shared.HistoricLog.HistoricType;
import bpm.gwt.commons.client.services.exception.ServiceException;
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
	
	public DataWithCount<Contract> getContracts(Integer directoryId, String query, int firstResult, int length, DataSort sort) throws ServiceException;

	public void confirmUpload(Contract contract, String documentName, String filePath) throws ServiceException;

	public void saveOrUpdateDocumentItem(DocumentItem docItem) throws ServiceException;

	public void removeLinkedItem(DocumentItem docItem) throws ServiceException;

	public List<ItemInstance> getItemHistoric(int itemId, Date startDate, Date endDate) throws ServiceException;
	
	public void saveOrUpdateContract(Contract contract, HistoricType type) throws ServiceException;
	
	public void removeContract(Contract contract) throws ServiceException;

	public String loadDocument(Contract contract, Integer versionId) throws ServiceException;
	
	public Supplier saveOrUpdateSupplier(Supplier supplier, List<Group> groups) throws ServiceException;

	public List<Supplier> getSuppliers() throws ServiceException;

	public List<Integer> getSupplierSecurity(int supplierId) throws ServiceException;
	
	public void removeSupplier(Supplier supplier) throws ServiceException;

	public List<HistoricLog> buildHistoricLogs(Contract contract) throws ServiceException;
	
	public List<ClassDefinition> getAvailableClasses() throws ServiceException;

	public ClassDefinition buildClassDefinition(ClassDefinition myClass) throws ServiceException;

	public ClassRule saveOrUpdateClassRule(ClassRule classRule) throws ServiceException;

	public void deleteClassRule(ClassRule classRule) throws ServiceException;

	public void generateIntegrationProcess(ContractIntegrationInformations integrationInfos) throws ServiceException;

	public ContractIntegrationInformations getIntegrationInfos(int contractId) throws ServiceException;

	public String getSourceUrl(ContractIntegrationInformations integrationInfos) throws ServiceException;

	public String getDatasetUrl(ContractIntegrationInformations integrationInfos) throws ServiceException;

	public void saveOrUpdateSchema(DocumentSchema docSchema) throws ServiceException;

	public void removeLinkedSchema(DocumentSchema docSchema) throws ServiceException;
	
	public void deleteIntegration(ContractIntegrationInformations integrationInfos) throws ServiceException;
	
	public void deleteSchema(ClassDefinition classDef) throws ServiceException;
	
	public String loadSchema(ClassDefinition schema) throws ServiceException;
	
	public void addAPISchema(String name, String url) throws ServiceException;
}
