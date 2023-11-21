package bpm.mdm.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;

import com.thoughtworks.xstream.XStream;

import bpm.mdm.model.Entity;
import bpm.mdm.model.Model;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.serialisation.EMFSerializer;
import bpm.mdm.model.serialisation.MdmConfiguration;
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
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

/**
 * Used to get access to Mdm Model remotely and to its datas
 * 
 * Each MdmRemote instance has only one IEntityStorage for each entity so do not share the MdmRemote across multiples Threads
 * 
 * 
 * @author ludo
 * 
 */
public class MdmRemote implements IMdmProvider {
	private static XStream xstream;
	private HttpRemote http = new HttpRemote();
	private Model model;

	private EContentAdapter collectionsListeners;
	private Adapter propertyAdapter;

	private HashMap<Entity, IEntityStorage> stores = new HashMap<Entity, IEntityStorage>();

	static {
		init();
	}

	public MdmRemote(String login, String password, String vanillaUrl) {
		http.init(vanillaUrl, login, password);
		init();
	}

	public MdmRemote(String login, String password, String vanillaUrl, Adapter propertyAdapter, EContentAdapter collectionsListeners) {
		this.collectionsListeners = collectionsListeners;
		this.propertyAdapter = propertyAdapter;
		http.init(vanillaUrl, login, password);
		init();
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	private static void init() {
		xstream = new XStream();
	}

	@Override
	public Model getModel() {

		return model;

	}

	@Override
	public void persistModel(Model model) throws Exception {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			EMFSerializer s = new EMFSerializer(new MdmConfiguration());
			s.save(bos, model);

		} catch (Exception ex) {
			throw new Exception("Could not serialize Object Model - " + ex.getMessage(), ex);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}

		byte[] bytes = new Base64().encode(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(bytes), IMdmProvider.ActionType.UPDATE_MODEL);
		String xml = http.executeModelAction(xstream.toXML(op));

		try {
			Object res = xstream.fromXML(xml);
			if (res != null) {
				if (res == Boolean.TRUE) {
					return;
				}
				else if (res instanceof Exception) {
					throw (Exception) res;
				}
			}
		} catch (Exception ex) {
			throw new Exception("Unable to parse server response - " + ex.getMessage(), ex);
		}

	}

	@Override
	public Model loadModel() throws Exception {
		if (model != null) {
			attachListeners(model, false);
		}
		try {
			XmlAction op = new XmlAction(createArguments(), IMdmProvider.ActionType.GET_MODEL);
			String xml = http.executeModelAction(xstream.toXML(op));

			EMFSerializer s = new EMFSerializer(new MdmConfiguration());
			model = s.loadModel(new ByteArrayInputStream(xml.getBytes("UTF-8")));

			attachListeners(model, true);
			return model;
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage(), ex);
		}
	}

	/**
	 * attach or remove listener on the EObject recursively
	 * 
	 * @param object
	 * @param add
	 */
	private void attachListeners(EObject object, boolean add) {
		if (collectionsListeners != null && propertyAdapter != null) {
			if (object instanceof EList<?>) {
				if (add) {
					object.eAdapters().add(collectionsListeners);
				}
				else {
					object.eAdapters().remove(collectionsListeners);
				}

			}
			else {
				if (add) {
					object.eAdapters().add(propertyAdapter);
				}
				else {
					object.eAdapters().remove(propertyAdapter);
				}

			}
		}

		for (EObject child : object.eContents()) {
			attachListeners(child, add);
		}
	}

	@Override
	public IEntityStorage getStore(Entity entity) {
		if (stores.get(entity) == null) {

			RemoteStore rm = new RemoteStore(entity, http, xstream);
			stores.put(entity, rm);

		}
		return stores.get(entity);
	}

	public StoreReader createStoreReader(Entity entity) {
		StoreReader reader = new StoreReader(entity, http, xstream);
		return reader;
	}

	@Override
	public void addContract(Contract contract) throws Exception {
		XmlAction op = new XmlAction(createArguments(contract), IMdmProvider.ActionType.ADD_CONTRACT);
		http.executeModelAction(xstream.toXML(op));
	}

	@Override
	public void addSecuredSupplier(int supplierId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(supplierId, groupId), IMdmProvider.ActionType.ADD_SECURITY);
		http.executeModelAction(xstream.toXML(op));
	}

	@Override
	public Supplier addSupplier(Supplier supplier) throws Exception {
		XmlAction op = new XmlAction(createArguments(supplier), IMdmProvider.ActionType.ADD_SUPPLIER);
		String xml = http.executeModelAction(xstream.toXML(op));
		return (Supplier) xstream.fromXML(xml);
	}

	@Override
	public List<Supplier> getSuppliers() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IMdmProvider.ActionType.GET_SUPPLIERS);
		String xml = http.executeModelAction(xstream.toXML(op));

		List<Supplier> model = (List<Supplier>) xstream.fromXML(xml);

		return model;
	}

	@Override
	public List<Supplier> getSuppliersByGroupId(int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId), IMdmProvider.ActionType.GET_SUPPLIERS);
		String xml = http.executeModelAction(xstream.toXML(op));

		List<Supplier> model = (List<Supplier>) xstream.fromXML(xml);

		return model;
	}

	@Override
	public void removeContract(Contract contract) throws Exception {
		XmlAction op = new XmlAction(createArguments(contract), IMdmProvider.ActionType.REMOVE_CONTRACT);
		http.executeModelAction(xstream.toXML(op));
	}

	@Override
	public void removeSupplier(Supplier supplier) throws Exception {
		XmlAction op = new XmlAction(createArguments(supplier), IMdmProvider.ActionType.REMOVE_SUPPLIER);
		http.executeModelAction(xstream.toXML(op));
	}

	@Override
	public List<Supplier> saveSuppliers(List<Supplier> suppliers) throws Exception {
		XmlAction op = new XmlAction(createArguments(suppliers), IMdmProvider.ActionType.SAVE_SUPPLIERS);
		String xml = http.executeModelAction(xstream.toXML(op));
		List<Supplier> model = (List<Supplier>) xstream.fromXML(xml);
		return model;
	}

	@Override
	public Supplier addSupplier(Supplier supplier, List<Group> groups) throws Exception {
		XmlAction op = new XmlAction(createArguments(supplier, groups), IMdmProvider.ActionType.ADD_SUPPLIER);
		String xml = http.executeModelAction(xstream.toXML(op));
		return (Supplier) xstream.fromXML(xml);
	}

	@Override
	public List<Integer> getSupplierSecurity(Integer id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IMdmProvider.ActionType.GET_SUPPLIER_SECURITY);
		String xml = http.executeModelAction(xstream.toXML(op));
		List<Integer> model = (List<Integer>) xstream.fromXML(xml);
		return model;
	}

	@Override
	public void saveOrUpdateDocumentItem(DocumentItem docItem) throws Exception {
		XmlAction op = new XmlAction(createArguments(docItem), IMdmProvider.ActionType.ADD_DOCUMENT_ITEM);
		http.executeModelAction(xstream.toXML(op));
	}

	@Override
	public void removeDocumentItem(DocumentItem docItem) throws Exception {
		XmlAction op = new XmlAction(createArguments(docItem), IMdmProvider.ActionType.REMOVE_DOCUMENT_ITEM);
		http.executeModelAction(xstream.toXML(op));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<DocumentItem> getDocumentItems(int contractId) throws Exception {
		XmlAction op = new XmlAction(createArguments(contractId), IMdmProvider.ActionType.GED_DOCUMENT_ITEMS);
		String xml = http.executeModelAction(xstream.toXML(op));
		return (List<DocumentItem>) xstream.fromXML(xml);
	}

	@Override
	public Contract getContract(int contractId) throws Exception {
		XmlAction op = new XmlAction(createArguments(contractId), IMdmProvider.ActionType.GET_CONTRACT);
		String xml = http.executeModelAction(xstream.toXML(op));
		return (Contract) xstream.fromXML(xml);
	}

	@Override
	public void launchAssociatedItems(int contractId, IRepositoryContext repositoryContext, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(contractId, repositoryContext, user), IMdmProvider.ActionType.LAUNCH_ASSOCIATED_ITEMS);
		http.executeModelAction(xstream.toXML(op));
	}

	@Override
	@SuppressWarnings("unchecked")
	public DataWithCount<Contract> getContracts(Integer directoryId, Integer groupId, String query, int firstResult, int length, DataSort sort) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryId, groupId, query, firstResult, length, sort), IMdmProvider.ActionType.GET_CONTRACTS_BY_DIRECTORY);
		String xml = http.executeModelAction(xstream.toXML(op));
		return (DataWithCount<Contract>) xstream.fromXML(xml);
	}

	@Override
	public MdmDirectory saveOrUpdateDirectory(MdmDirectory directory) throws Exception {
		XmlAction op = new XmlAction(createArguments(directory), IMdmProvider.ActionType.SAVE_DIRECTORY);
		String xml = http.executeModelAction(xstream.toXML(op));
		return (MdmDirectory) xstream.fromXML(xml);
	}

	@Override
	public void removeDirectory(MdmDirectory directory) throws Exception {
		XmlAction op = new XmlAction(createArguments(directory), IMdmProvider.ActionType.REMOVE_DIRECTORY);
		http.executeModelAction(xstream.toXML(op));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<MdmDirectory> getDirectories(Integer parentId, boolean includeSubdirectories) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId, includeSubdirectories), IMdmProvider.ActionType.GET_DIRECTORIES);
		String xml = http.executeModelAction(xstream.toXML(op));
		return (List<MdmDirectory>) xstream.fromXML(xml);
	}

	@Override
	public ContractIntegrationInformations getIntegrationInfosByContract(int contractId) throws Exception {
		XmlAction op = new XmlAction(createArguments(contractId), IMdmProvider.ActionType.GET_INTEGRATION_INFOS_BY_CONTRACT);
		String xml = http.executeModelAction(xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (ContractIntegrationInformations) xstream.fromXML(xml) : null;
	}

	@Override
	public ContractIntegrationInformations getIntegrationInfosByLimesurvey(String limesurveyId) throws Exception {
		XmlAction op = new XmlAction(createArguments(limesurveyId), IMdmProvider.ActionType.GET_INTEGRATION_INFOS_BY_LIMESURVEY);
		String xml = http.executeModelAction(xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (ContractIntegrationInformations) xstream.fromXML(xml) : null;
	}

	@Override
	public ContractIntegrationInformations saveOrUpdateIntegrationInfos(ContractIntegrationInformations integrationInfos) throws Exception {
		XmlAction op = new XmlAction(createArguments(integrationInfos), IMdmProvider.ActionType.MANAGE_INTEGRATION_INFOS);
		String xml = http.executeModelAction(xstream.toXML(op));
		return (ContractIntegrationInformations) xstream.fromXML(xml);
	}

	@Override
	public void removeIntegrationInfos(ContractIntegrationInformations integrationInfos) throws Exception {
		XmlAction op = new XmlAction(createArguments(integrationInfos), IMdmProvider.ActionType.REMOVE_INTEGRATION_INFOS);
		http.executeModelAction(xstream.toXML(op));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<DocumentSchema> getDocumentSchemas(int contractId) throws Exception {
		XmlAction op = new XmlAction(createArguments(contractId), IMdmProvider.ActionType.GED_DOCUMENT_SCHEMAS);
		String xml = http.executeModelAction(xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (List<DocumentSchema>) xstream.fromXML(xml) : new ArrayList<DocumentSchema>();
	}
	
	@Override
	public void removeDocumentSchema(DocumentSchema docSchema) throws Exception {
		XmlAction op = new XmlAction(createArguments(docSchema), IMdmProvider.ActionType.REMOVE_DOCUMENT_SCHEMA);
		http.executeModelAction(xstream.toXML(op));
	}
	
	@Override
	public void saveOrUpdateDocumentSchema(DocumentSchema docSchema) throws Exception {
		XmlAction op = new XmlAction(createArguments(docSchema), IMdmProvider.ActionType.ADD_DOCUMENT_SCHEMA);
		http.executeModelAction(xstream.toXML(op));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ContractIntegrationInformations> getKpiInfosByDatasetId(String datasetId) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasetId), IMdmProvider.ActionType.GET_KPI_INFOS_BY_DATASET_ID);
		String xml = http.executeModelAction(xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (List<ContractIntegrationInformations>) xstream.fromXML(xml) : null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ContractIntegrationInformations> getIntegrationInfosByOrganisation(String organisation, ContractType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(organisation, type), IMdmProvider.ActionType.GET_INTEGRATION_INFOS_BY_ORGANISATION);
		String xml = http.executeModelAction(xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (List<ContractIntegrationInformations>) xstream.fromXML(xml) : null;
	}
}
