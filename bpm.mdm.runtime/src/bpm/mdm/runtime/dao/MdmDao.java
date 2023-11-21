package bpm.mdm.runtime.dao;

import java.util.ArrayList;
import java.util.List;

import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.model.supplier.MdmDirectory;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class MdmDao extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Supplier> getSuppliers() {
		List<Supplier> suppliers = new ArrayList<Supplier>(getHibernateTemplate().find("From Supplier"));
		if (suppliers != null) {
			for (Supplier sup : suppliers) {
				if (sup.getContracts() != null) {
					List<Contract> contracts = new ArrayList<>();
					for (Contract contract : sup.getContracts()) {
						contracts.add(contract);
					}
					sup.setContracts(contracts);
					buildContracts(contracts);
				}
			}
		}
		return suppliers;
	}

	public Supplier getSupplierById(int id) {
		Supplier supplier = (Supplier) getHibernateTemplate().find("From Supplier where id = " + id).get(0);
		if (supplier != null && supplier.getContracts() != null) {
			List<Contract> contracts = new ArrayList<>();
			for (Contract contract : supplier.getContracts()) {
				contracts.add(contract);
			}
			supplier.setContracts(contracts);
			buildContracts(contracts);
		}
		return supplier;
	}

	@SuppressWarnings("unchecked")
	public List<Supplier> getSuppliersByGroupId(int groupId) {
		List<Supplier> suppliers = getHibernateTemplate().find("From Supplier");
		List<SupplierSecurity> secus = getHibernateTemplate().find("From SupplierSecurity where groupId = " + groupId);
		List<Supplier> results = new ArrayList<Supplier>();

		for (Supplier sup : suppliers) {
			if (sup.getContracts() != null) {
				List<Contract> contracts = new ArrayList<>();
				for (Contract contract : sup.getContracts()) {
					contracts.add(contract);
				}
				sup.setContracts(contracts);
				buildContracts(contracts);
			}
			
			for (SupplierSecurity secu : secus) {
				if (sup.getId() == secu.getSupplierId()) {
					results.add(sup);
					break;
				}
			}
		}

		return results;
	}

	public void addContract(Contract contract) {
		getHibernateTemplate().saveOrUpdate(contract);
	}

	public void addSupplierSecurity(SupplierSecurity secu) {
		getHibernateTemplate().saveOrUpdate(secu);
	}

	public Supplier addSupplier(Supplier supplier) {
		supplier = (Supplier) getHibernateTemplate().saveOrUpdate(supplier);
		return getSupplierById(supplier.getId());
	}

	public void saveOrUpdateDocumentItem(DocumentItem docItem) {
		getHibernateTemplate().saveOrUpdate(docItem);
	}

	public void removeContract(Contract contract) {
		removeDocumentItems(contract.getId());
		removeDocumentSchemas(contract.getId());
		if (contract.getParent() != null) {
			if (contract.getParent().getContracts() != null) {
				int index = -1;
				for (int i=0; i < contract.getParent().getContracts().size(); i++) {
					Contract cont = contract.getParent().getContracts().get(i);
					if (cont.getId().equals(contract.getId())) {
						index = i;
						break;
					}
				}
				
				if (index > -1) {
					contract.getParent().getContracts().remove(index);
				}
			}
			getHibernateTemplate().update(contract.getParent());
		}
		else {
			getHibernateTemplate().delete(contract);
		}
	}

	@SuppressWarnings("unchecked")
	private void removeDocumentItems(int contractId) {
		List<DocumentItem> docItems = getHibernateTemplate().find("From DocumentItem where contractId = " + contractId);
		if (docItems != null) {
			for (DocumentItem docItem : docItems) {
				getHibernateTemplate().delete(docItem);
			}
		}
	}

	public void removeDocumentItem(DocumentItem docItem) {
		getHibernateTemplate().delete(docItem);
	}

	public void removeSupplier(Supplier supplier) {
		removeSupplierSecurity(supplier);
		getHibernateTemplate().delete(supplier);
	}

	@SuppressWarnings("unchecked")
	private void removeSupplierSecurity(Supplier supplier) {
		List<SupplierSecurity> secus = getHibernateTemplate().find("From SupplierSecurity where supplierId = " + supplier.getId());
		if (secus != null) {
			for (SupplierSecurity sec : secus) {
				getHibernateTemplate().delete(sec);
			}
		}
	}

	public List<Supplier> saveSuppliers(List<Supplier> suppliers) {
//		List<Supplier> supWithIds = new ArrayList<Supplier>();
//		for (Supplier sup : suppliers) {
//			sup.setId(((Supplier) getHibernateTemplate().saveOrUpdate(sup)).getId());
//		}

//		List<Supplier> sups = getSuppliers();
//		List<Supplier> result = new ArrayList<Supplier>();
//
//		for (Supplier sup : supWithIds) {
//			for (Supplier bsup : sups) {
//				if (sup.getName().equals(bsup.getName())) {
//					result.add(bsup);
//					break;
//				}
//			}
//		}
		
		getHibernateTemplate().saveOrUpdateAll(suppliers);

		return getSuppliers();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getGroupIdsForSupplier(Integer id) {
		List<SupplierSecurity> secus = getHibernateTemplate().find("From SupplierSecurity where supplierId = " + id);
		List<Integer> result = new ArrayList<Integer>();
		for (SupplierSecurity secu : secus) {
			result.add(secu.getGroupId());
		}
		return result;
	}

	public Supplier addSupplier(Supplier supplier, List<Group> groups) {
		// if the supplier exists, look if there's groups to remove
		if (supplier.getId() != null) {
			List<Integer> toRemove = new ArrayList<Integer>();
			List<Group> alreadyExists = new ArrayList<Group>();
			List<Integer> existingSecurity = getGroupIdsForSupplier(supplier.getId());
			for (Integer id : existingSecurity) {
				boolean finded = false;
				for (Group g : groups) {
					if (g.getId().intValue() == id.intValue()) {
						alreadyExists.add(g);
						finded = true;
						break;
					}
				}
				if (!finded) {
					toRemove.add(id);
				}
			}

			groups.removeAll(alreadyExists);
			removeSecurities(toRemove, supplier.getId());
		}

		Supplier sup = (Supplier) addSupplier(supplier);

		for (Group g : groups) {
			SupplierSecurity secu = new SupplierSecurity();
			secu.setSupplierId(sup.getId());
			secu.setGroupId(g.getId());
			addSupplierSecurity(secu);
		}

		return sup;
	}

	@SuppressWarnings("unchecked")
	private void removeSecurities(List<Integer> groupIds, Integer id) {
		List<SupplierSecurity> secus = new ArrayList<SupplierSecurity>();
		for (Integer torm : groupIds) {
			List<SupplierSecurity> secu = (List<SupplierSecurity>) getHibernateTemplate().find("from SupplierSecurity where groupId = " + torm + " and supplierId = " + id);
			if (secu != null && !secu.isEmpty()) {
				secus.add(secu.get(0));
			}
		}
		getHibernateTemplate().deleteAll(secus);

	}

	@SuppressWarnings("unchecked")
	public List<DocumentItem> getDocumentItems(int contractId) {
		List<DocumentItem> docItems = getHibernateTemplate().find("From DocumentItem where contractId = " + contractId);
		return docItems;
	}

	public Contract getContractById(int contractId) {
		Contract contract = (Contract) getHibernateTemplate().find("From Contract where id = " + contractId).get(0);
		buildContract(contract);
		
		return contract;
	}
	
	private void buildContracts(List<Contract> contracts) {
		if (contracts != null) {
			for (Contract contract : contracts) {
				buildContract(contract);
			}
		}
	}
	
	private void buildContract(Contract contract) {
		if (contract.getParent() == null) {
			Supplier parent = getSupplierById(contract.getParent().getId());
			contract.setParent(parent);
		}
		else if(isProxy(contract.getParent())){
			contract.setParent(getSupplierById(contract.getParent().getId()));
		}
		
		if (contract.getDirectoryId() != null && contract.getDirectory() == null) {
			MdmDirectory directory = getDirectoryById(contract.getDirectoryId());
			contract.setDirectory(directory);
		}
		
		//FIXME input isn't saved in the db so this just crashed
		//contract.setHasInput(hasInput(contract.getId()));
	}

//	@SuppressWarnings("unchecked")
//	private boolean hasInput(int contractId) {
//		List<Long> values = getHibernateTemplate().find("SELECT count(*) FROM DocumentItem WHERE contractId=" + contractId + " AND input=1");
//		if (values != null && !values.isEmpty() && values.get(0) > 0) {
//			return true;
//		}
//
//		return false;
//	}

	public DataWithCount<Contract> getContracts(Integer directoryId, Integer groupId, String query, int firstResult, int length, DataSort sort) {
		List<Object> parameters = new ArrayList<Object>();
		
		boolean hasWhere = false;

		StringBuilder buf = new StringBuilder("FROM Contract");
		if (directoryId != null) {
			buf.append(" WHERE directoryId = ?");
			parameters.add(directoryId);
			hasWhere = true;
		}
		
		if (query != null && !query.isEmpty()) {
			buf.append((hasWhere) ? " AND " : " WHERE ");
			buf.append("name LIKE ?");
			parameters.add("%" + query + "%");
			hasWhere = true;
		}
		
		if (groupId != null) {
			buf.append((hasWhere) ? " AND " : " WHERE ");
			buf.append("(");
			buf.append(" parent IS NULL");
			buf.append(" OR parent.id IN (SELECT sc.supplierId FROM SupplierSecurity sc WHERE sc.groupId = " + groupId + ")");
			buf.append(")");
//				"SELECT true FROM SupplierSecurity WHERE supplierId = " + supplierId + " AND groupId = " + groupId
			hasWhere = true;
		}
		
		if (sort != null) {
			buf.append(" ORDER BY " + sort.getColumn() + " " + (sort.isAscending() ? "ASC" : "DESC"));
		}
		
		List<Contract> result = new ArrayList<Contract>();
		
		long itemsCount = getHibernateTemplate().count("SELECT count(*) " + buf.toString(), parameters);
		
		List<Contract> items = getHibernateTemplate().findWithPag(buf.toString(), firstResult, firstResult + length, parameters);
		if (items != null) {
			for (Contract contract : items) {
				buildContract(contract);
				result.add(contract);
				
				// We need to do an ugly trick to set the contract to the existing supplier
				contract.getParent().updateContract(contract);
			}
		}
		return new DataWithCount<Contract>(items, itemsCount);
	}

//	@SuppressWarnings("unchecked")
//	private boolean isContractAllowed(int supplierId, int groupId) {
//		List<Boolean> values = (List<Boolean>) getHibernateTemplate().find("SELECT true FROM SupplierSecurity WHERE supplierId = " + supplierId + " AND groupId = " + groupId);
//
//		if (values != null && !values.isEmpty()) {
//			return true;
//		}
//
//		return false;
//	}

	@SuppressWarnings("unchecked")
	public List<MdmDirectory> getDirectories(Integer parentId, boolean includeSubdirectories) {
		String query = "FROM MdmDirectory";
		if (parentId != null) {
			query += " WHERE parentId = " + parentId;
		}
		else {
			query += " WHERE parentId IS NULL OR parentId = 0";
		}
		
		List<MdmDirectory> directories = new ArrayList<MdmDirectory>(getHibernateTemplate().find(query));
		if (directories != null) {
			for (MdmDirectory dir : directories) {
				List<MdmDirectory> childs = getDirectories(dir.getId(), includeSubdirectories);
				dir.setChilds(childs);
			}
		}
		return directories;
	}

	public MdmDirectory getDirectoryById(int directoryId) {
		return (MdmDirectory) getHibernateTemplate().find("FROM MdmDirectory WHERE id = " + directoryId).get(0);
	}

	public MdmDirectory saveOrUpdateDirectory(MdmDirectory directory) {
		return (MdmDirectory) getHibernateTemplate().saveOrUpdate(directory);
	}

	public void removeDirectory(MdmDirectory directory) {
		removeDirectoryFromContracts(directory.getId());
		getHibernateTemplate().delete(directory);
	}

	private void removeDirectoryFromContracts(Integer directoryId) {
		DataWithCount<Contract> result = getContracts(directoryId, null, null, 0, 100000, null);
		List<Contract> contracts = result.getItems();
		if (contracts != null) {
			for (Contract contract : contracts) {
				contract.setDirectoryId(null);
			}
		}
		getHibernateTemplate().saveOrUpdateAll(contracts);

	}

	@SuppressWarnings("unchecked")
	public ContractIntegrationInformations getIntegrationInfos(int contractId) {
		List<ContractIntegrationInformations> integrations = (List<ContractIntegrationInformations>) getHibernateTemplate().find("FROM ContractIntegrationInformations WHERE contractId = " + contractId);
		buildIntegrations(integrations);
		return integrations != null && !integrations.isEmpty() ? integrations.get(0) : null;
	}

	public ContractIntegrationInformations getIntegrationInfos(String limesurveyId) {
		List<ContractIntegrationInformations> integrations = getHibernateTemplate().find("FROM ContractIntegrationInformations WHERE item LIKE ?", "%" + limesurveyId + "%");
		buildIntegrations(integrations);
		return integrations != null && !integrations.isEmpty() ? integrations.get(0) : null;
	}

	public List<ContractIntegrationInformations> getKpiInfos(String datasetId) {
		List<ContractIntegrationInformations> integrations = getHibernateTemplate().find("FROM ContractIntegrationInformations WHERE item = ? AND type=" + ContractType.KPI.ordinal(), datasetId);
		buildIntegrations(integrations);
		return integrations;
	}

	public List<ContractIntegrationInformations> getIntegrationInfosByOrganisation(String organisation, ContractType type) {
		List<ContractIntegrationInformations> integrations = getHibernateTemplate().find("FROM ContractIntegrationInformations WHERE sourceOrganisation = ? AND type=" + type.ordinal(), organisation);
		buildIntegrations(integrations);
		return integrations;
	}
	
	private void buildIntegrations(List<ContractIntegrationInformations> integrations) {
		if (integrations != null) {
			for (ContractIntegrationInformations integration : integrations) {
				buildIntegration(integration);
			}
		}
	}

	private void buildIntegration(ContractIntegrationInformations integration) {
		Integer contractId = integration.getContractId();
		if (contractId != null) {
			List<DocumentSchema> schemas = getDocumentSchemas(contractId);
			List<String> validationSchemas = new ArrayList<String>();
			if (schemas != null) {
				for (DocumentSchema schema : schemas) {
					validationSchemas.add(schema.getSchema());
				}
			}
			
			integration.setValidationSchemas(validationSchemas);
		}
	}

	public ContractIntegrationInformations saveOrUpdateIntegrationInfos(ContractIntegrationInformations integrationInfos) {
		//We need to check if a process exist already for the contract
		ContractIntegrationInformations existingProcess = getIntegrationInfos(integrationInfos.getId());
		if (existingProcess != null) {
			integrationInfos.setId(existingProcess.getId());
		}
		return (ContractIntegrationInformations) getHibernateTemplate().saveOrUpdate(integrationInfos);
	}

	public void removeIntegrationInfos(ContractIntegrationInformations integrationInfos) {
		getHibernateTemplate().delete(integrationInfos);
	}

	@SuppressWarnings("unchecked")
	public List<DocumentSchema> getDocumentSchemas(int contractId) {
		return getHibernateTemplate().find("FROM DocumentSchema WHERE contractId = " + contractId);
	}

	@SuppressWarnings("unchecked")
	private void removeDocumentSchemas(int contractId) {
		List<DocumentItem> docItems = getHibernateTemplate().find("FROM DocumentSchema WHERE contractId = " + contractId);
		if (docItems != null) {
			for (DocumentItem docItem : docItems) {
				getHibernateTemplate().delete(docItem);
			}
		}
	}

	public void removeDocumentSchema(DocumentSchema docSchema) {
		getHibernateTemplate().delete(docSchema);
	}

	public void saveOrUpdateDocumentSchema(DocumentSchema docSchema) {
		getHibernateTemplate().saveOrUpdate(docSchema);
	}
}
