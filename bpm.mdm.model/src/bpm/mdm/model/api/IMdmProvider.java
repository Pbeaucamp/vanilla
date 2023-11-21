package bpm.mdm.model.api;

import java.util.List;

import bpm.mdm.model.Entity;
import bpm.mdm.model.Model;
import bpm.mdm.model.runtime.exception.UpdateEntityPrimaryKeyException;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.model.supplier.MdmDirectory;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IMdmProvider {
	public static final String SERVLET_MODEL = "/mdm/model";
	public static final String SERVLET_DATAS = "/mdm2/datas";
	
	public enum ActionType implements IXmlActionType{
		GET_MODEL(Level.DEBUG), UPDATE_MODEL(Level.INFO), GET_ENTITY_DATAS(Level.DEBUG), SAVE_DATAS(Level.INFO), UPDATE_DATAS(Level.INFO), 
		DELETE_DATAS(Level.INFO), LOOKUP(Level.DEBUG), GET_STORE_STATISTICS(Level.DEBUG), GET_INVALID_ROWS(Level.DEBUG), SAVE_SUPPLIERS_MODEL(Level.INFO), 
		LOAD_SUPPLIERS_MODEL(Level.DEBUG), GET_SUPPLIERS(Level.DEBUG), ADD_SUPPLIER(Level.INFO), GET_CONTRACT(Level.INFO), ADD_CONTRACT(Level.INFO), REMOVE_CONTRACT(Level.INFO), 
		REMOVE_SUPPLIER(Level.INFO), ADD_SECURITY(Level.INFO), SAVE_SUPPLIERS(Level.INFO), GET_SUPPLIER_SECURITY(Level.DEBUG), 
		ADD_DOCUMENT_ITEM(Level.INFO), REMOVE_DOCUMENT_ITEM(Level.INFO), GED_DOCUMENT_ITEMS(Level.INFO), LAUNCH_ASSOCIATED_ITEMS(Level.DEBUG),
		GET_CONTRACTS_BY_DIRECTORY(Level.INFO), SAVE_DIRECTORY(Level.DEBUG), REMOVE_DIRECTORY(Level.DEBUG), GET_DIRECTORIES(Level.DEBUG), 
		GET_INTEGRATION_INFOS_BY_CONTRACT(Level.DEBUG), GET_INTEGRATION_INFOS_BY_LIMESURVEY(Level.DEBUG), GET_KPI_INFOS_BY_DATASET_ID(Level.DEBUG), GET_INTEGRATION_INFOS_BY_ORGANISATION(Level.DEBUG), 
		MANAGE_INTEGRATION_INFOS(Level.DEBUG), REMOVE_INTEGRATION_INFOS(Level.DEBUG), 
		GED_DOCUMENT_SCHEMAS(Level.DEBUG), REMOVE_DOCUMENT_SCHEMA(Level.DEBUG), ADD_DOCUMENT_SCHEMA(Level.DEBUG);

		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	public Model getModel() ;
	
	/**
	 * reload the model and return it
	 * @return
	 * @throws Exception
	 */
	public Model loadModel() throws Exception;
	
	public IEntityStorage getStore(Entity entity);
	
	public void persistModel(Model model) throws UpdateEntityPrimaryKeyException, Exception;
	
	public List<Supplier> getSuppliers() throws Exception;
	
	public List<Supplier> getSuppliersByGroupId(int groupId) throws Exception;
	
	public Supplier addSupplier(Supplier supplier) throws Exception;
	
	public void addContract(Contract contract) throws Exception;
	
	public void removeContract(Contract contract) throws Exception;
	
	public void removeSupplier(Supplier supplier) throws Exception;
	
	public void addSecuredSupplier(int supplierId, int groupId) throws Exception;

	public List<Supplier> saveSuppliers(List<Supplier> suppliers) throws Exception;

	public Supplier addSupplier(Supplier supplier, List<Group> groups) throws Exception;

	public List<Integer> getSupplierSecurity(Integer id) throws Exception;
	
	public void saveOrUpdateDocumentItem(DocumentItem docItem) throws Exception;
	
	public void removeDocumentItem(DocumentItem docItem) throws Exception;
	
	public List<DocumentItem> getDocumentItems(int contractId) throws Exception;
	
	public Contract getContract(int contractId) throws Exception;

	public void launchAssociatedItems(int contractId, IRepositoryContext repositoryContext, User user) throws Exception;
	
	//public void rebuildIndex(Entity entity, HashMap<Row, Row> replacement) throws IndexRebuildException, RowsExceptionHolder, Exception;
	
	public DataWithCount<Contract> getContracts(Integer directoryId, Integer groupId, String query, int firstResult, int length, DataSort sort) throws Exception;
	
	public List<MdmDirectory> getDirectories(Integer parentId, boolean includeSubdirectories) throws Exception;

	public MdmDirectory saveOrUpdateDirectory(MdmDirectory directory) throws Exception;
	
	public void removeDirectory(MdmDirectory directory) throws Exception;

	public ContractIntegrationInformations getIntegrationInfosByContract(int contractId) throws Exception;

	public ContractIntegrationInformations getIntegrationInfosByLimesurvey(String limesurveyId) throws Exception;

	public List<ContractIntegrationInformations> getKpiInfosByDatasetId(String datasetId) throws Exception;
	
	public List<ContractIntegrationInformations> getIntegrationInfosByOrganisation(String organisation, ContractType type) throws Exception;

	public ContractIntegrationInformations saveOrUpdateIntegrationInfos(ContractIntegrationInformations integrationInfos) throws Exception;

	public void removeIntegrationInfos(ContractIntegrationInformations integrationInfos) throws Exception;
	
	public void saveOrUpdateDocumentSchema(DocumentSchema docSchema) throws Exception;
	
	public void removeDocumentSchema(DocumentSchema docSchema) throws Exception;
	
	public List<DocumentSchema> getDocumentSchemas(int contractId) throws Exception;
}
