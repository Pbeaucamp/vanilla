package bpm.mdm.runtime;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.MdmFactory;
import bpm.mdm.model.Model;
import bpm.mdm.model.Rule;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.rules.impl.EntityLinkRuleImpl;
import bpm.mdm.model.serialisation.EMFSerializer;
import bpm.mdm.model.serialisation.MdmConfiguration;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.model.supplier.MdmDirectory;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.runtime.dao.MdmDao;
import bpm.mdm.runtime.dao.SupplierSecurity;
import bpm.mdm.runtime.serializers.Reader;
import bpm.mdm.runtime.serializers.RuntimeSerializer;
import bpm.mdm.runtime.serializers.index.IndexBuilder;
import bpm.mdm.runtime.serializers.index.IndexNode;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MdmRuntime implements IMdmProvider {

	private Model model;
	private HashMap<Entity, IEntityStorage> stores = new HashMap<Entity, IEntityStorage>();

	private EContentAdapter collectionsListeners;
	private Adapter propertyAdapter;
	private MdmConfiguration configuration;
	private Semaphore sem = new Semaphore(1, true);
	private Date updateDate = null;

	private RuntimeSerializer runtimeSerializer;
	private MdmDao mdmDao;

	public MdmRuntime(MdmConfiguration configuration, Adapter propertyAdapter, EContentAdapter collectionsListeners) {
		this.collectionsListeners = collectionsListeners;
		this.propertyAdapter = propertyAdapter;

		File folder = new File(configuration.getMdmPersistanceFolderName());
		if (!folder.exists()) {
			Logger.getLogger(getClass()).warn("The configured storage folder " + folder.getAbsolutePath() + " does not exist");
			if (folder.mkdirs()) {
				Logger.getLogger(getClass()).info("Storage Folder " + folder.getAbsolutePath() + " created");
			}
			else {
				throw new RuntimeException("Could not create folder structure " + folder.getAbsolutePath());
			}
		}

		this.configuration = configuration;
		runtimeSerializer = new RuntimeSerializer(configuration);
		try {
			model = loadModel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		initDao();

	}

	private void initDao() {
		ClassPathResource configFile = new ClassPathResource("/bpm/mdm/runtime/dao/mdm.context.xml", MdmRuntime.class.getClassLoader());
		XmlBeanFactory factory = new XmlBeanFactory(configFile);

		try {
			PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
			FileSystemResource resource = new FileSystemResource(System.getProperty("bpm.vanilla.configurationFile"));
			cfg.setLocation(resource);

			cfg.postProcessBeanFactory(factory);

		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.getLogger(this.getClass()).warn("Error when using PropertyPlaceHolder on bpm.vanilla.configurationFile=" + System.getProperty("bpm.vanilla.configurationFile"), ex);
		}

		try {
			MdmRuntime.class.getClassLoader().loadClass("org.hibernate.proxy.HibernateProxy");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		mdmDao = (MdmDao) factory.getBean("mdmDAO");
	}

	public MdmConfiguration getConfig() {
		return configuration;
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

	protected void initStores() {
		stores.clear();

		for (Entity e : getModel().getEntities()) {
			IEntityStorage store = new RuntimeStoreTreeIndexed(runtimeSerializer, e);
			stores.put(e, store);
		}

	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public IEntityStorage getStore(Entity entity) {
		IEntityStorage s = stores.get(entity);
		if (s == null) {
			synchronized (stores) {
				s = new RuntimeStoreTreeIndexed(runtimeSerializer, entity);
				stores.put(entity, s);
			}
		}
		return s;
	}

	@Override
	public void persistModel(Model model) throws Exception {
		try {
			sem.acquire();
			// check if the mode has been changed
			Date now = new Date();
			if (now.before(updateDate)) {
				throw new Exception("The Mdm has already been changed. Please update your model before trying to modify it");
			}

			// check entity primarykeys changes
			ModelUpdateChecker checker = new ModelUpdateChecker(this.model);
			HashMap<Entity, Entity> toRebuild = checker.listIndexEntityRebuild(model);
			HashMap<Entity, IndexNode> replacement = new HashMap<Entity, IndexNode>();

			for (Entity e : toRebuild.keySet()) {

				Logger.getLogger(getClass()).info("Try to build Entity " + e.getName() + " index for its new definition...");

				IndexNode originalIndex = runtimeSerializer.getIndexTree(e);
				Reader reader = runtimeSerializer.getReader(e);
				IndexBuilder builder = new IndexBuilder(originalIndex, reader, toRebuild.get(e));

				replacement.put(e, builder.rebuildIndex());

			}
			for (Entity e : replacement.keySet()) {
				runtimeSerializer.resetIndexTree(e, replacement.get(e));
			}

			EMFSerializer serializer = new EMFSerializer(configuration);
			serializer.save(model);
			this.model = model;

			// set the linkedRules objetcs
			for (Entity e : model.getEntities()) {
				for (Attribute a : e.getAttributes()) {
					for (Rule r : a.getRules()) {
						if (r instanceof EntityLinkRuleImpl) {
							EntityLinkRuleImpl lkr = (EntityLinkRuleImpl) r;
							if (lkr.getEntityReader() == null) {
								lkr.setEntityReader(new RuntimeEntityReader(getStore(e), e));
							}
						}
					}
				}
			}

			sem.release();
		} finally {
			if (sem.availablePermits() <= 0) {
				sem.release();
			}
		}

	}

	@Override
	public Model loadModel() throws Exception {
		if (model != null) {
			attachListeners(model, false);
		}
		try {
			EMFSerializer serializer = new EMFSerializer(configuration);
			Logger.getLogger(getClass()).info("Reloading MdmModel from " + configuration.getMdmPersistanceFolderName() + "/mdm.xml");
			model = serializer.loadModel();

			// set the linkedRules objetcs
			for (Entity e : model.getEntities()) {
				for (Attribute a : e.getAttributes()) {
					for (Rule r : a.getRules()) {
						if (r instanceof EntityLinkRuleImpl) {
							EntityLinkRuleImpl lkr = (EntityLinkRuleImpl) r;
							if (lkr.getEntityReader() == null) {
								Entity ent = (Entity) lkr.getLinkedAttribute().eContainer();
								lkr.setEntityReader(new RuntimeEntityReader(getStore(ent), e));
							}
						}
					}
				}
			}
			attachListeners(model, true);
		} catch (Exception ex) {
			// Logger.getLogger(getClass()).info("Failed to reload MdmModel : "
			// + ex.getMessage(), ex);
			model = MdmFactory.eINSTANCE.createModel();
			Logger.getLogger(getClass()).info("New Mdm Model created");
		}
		updateDate = new Date();
		initStores();
		return model;
	}

	public RuntimeSerializer getRuntimeSerializer() {
		return runtimeSerializer;

	}

	@Override
	public void addContract(Contract contract) throws Exception {
		mdmDao.addContract(contract);
		
		//Maybe we will add the launch later
		//For now we don't know how to deal with parameters or multiple ETLs
//		if (launchLinkedEtl) {
//			List<DocumentItem> linkedItems = getDocumentItems(contract.getId());
//			if (linkedItems != null) {
//				int nbEtl = 0;
//				DocumentItem onlyEtl = null;
//				for (DocumentItem item : linkedItems) {
//					if (item.getItem().getType() == IRepositoryApi.GTW_TYPE) {
//						onlyEtl = item;
//						nbEtl++;
//					}
//				}
//				
//				if (nbEtl == 1) {
//					final DocumentItem selectedEtl = onlyEtl;
//					
//					final InformationsDialog dial = new InformationsDialog(Labels.lblCnst.LaunchEtl(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.WouldYouLikeToLaunchEtl(), true);
//					dial.center();
//					dial.addCloseHandler(new CloseHandler<PopupPanel>() {
//						
//						@Override
//						public void onClose(CloseEvent<PopupPanel> event) {
//							if (dial.isConfirm()) {
//								PortailRepositoryItem itemReport = new PortailRepositoryItem(selectedEtl.getItem(), IRepositoryApi.BIG);
//
//								ViewerDialog dial = new ViewerDialog(parent.getInfoUser(), itemReport);
//								dial.center();
//							}
//						}
//					});
//				}
//		}
	}

	@Override
	public void addSecuredSupplier(int supplierId, int groupId) throws Exception {
		SupplierSecurity secu = new SupplierSecurity();
		secu.setGroupId(groupId);
		secu.setSupplierId(supplierId);
		mdmDao.addSupplierSecurity(secu);
	}

	@Override
	public List<Supplier> getSuppliers() throws Exception {
		return mdmDao.getSuppliers();
	}

	@Override
	public List<Supplier> getSuppliersByGroupId(int groupId) throws Exception {
		return mdmDao.getSuppliersByGroupId(groupId);
	}

	@Override
	public void removeContract(Contract contract) throws Exception {
		mdmDao.removeContract(contract);
	}

	@Override
	public void removeSupplier(Supplier supplier) throws Exception {
		mdmDao.removeSupplier(supplier);
	}

	@Override
	public List<Supplier> saveSuppliers(List<Supplier> suppliers) throws Exception {
		return mdmDao.saveSuppliers(suppliers);
	}

	@Override
	public Supplier addSupplier(Supplier supplier) throws Exception {
		return (Supplier) mdmDao.addSupplier(supplier);
	}

	@Override
	public Supplier addSupplier(Supplier supplier, List<Group> groups) throws Exception {
		return mdmDao.addSupplier(supplier, groups);
	}

	@Override
	public List<Integer> getSupplierSecurity(Integer id) throws Exception {
		return mdmDao.getGroupIdsForSupplier(id);
	}

	@Override
	public void saveOrUpdateDocumentItem(DocumentItem docItem) throws Exception {
		mdmDao.saveOrUpdateDocumentItem(docItem);
	}

	@Override
	public void removeDocumentItem(DocumentItem docItem) throws Exception {
		mdmDao.removeDocumentItem(docItem);
	}

	@Override
	public List<DocumentItem> getDocumentItems(int contractId) throws Exception {
		return mdmDao.getDocumentItems(contractId);
	}

	@Override
	public Contract getContract(int contractId) throws Exception {
		return mdmDao.getContractById(contractId);
	}

	
	@Override
	public void launchAssociatedItems(int contractId, IRepositoryContext repositoryContext, User user) throws Exception {
		IRepositoryApi repositoryApi = new RemoteRepositoryApi(repositoryContext);
		
		List<DocumentItem> docItems = getDocumentItems(contractId);
		if (docItems != null) {
			for (DocumentItem docItem : docItems) {
				RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(docItem.getItemId());
				docItem.setItem(item);
			}
		}
		
		if (docItems != null) {
			int nbEtl = 0;
			DocumentItem onlyItem = null;
			boolean isEtl = false;
			for (DocumentItem item : docItems) {
				if ((item.getItem().getType() == IRepositoryApi.GTW_TYPE || item.getItem().getType() == IRepositoryApi.BIW_TYPE) && !item.isInput()) {
					onlyItem = item;
					isEtl = item.getItem().getType() == IRepositoryApi.GTW_TYPE;
							
					nbEtl++;
				}
			}
			
			if (nbEtl == 1) {
				final DocumentItem selectedEtl = onlyItem;
				if (isEtl) {
					launchGateway(onlyItem.getItemId(), repositoryContext, user);
				}
				else {
					launchWorkflow(onlyItem.getItemId(), repositoryContext);
				}
			}
		}
	}
	
	private void launchGateway(int itemId, IRepositoryContext repositoryCtx, User user) throws Exception {
		try {
			ObjectIdentifier id = new ObjectIdentifier(repositoryCtx.getRepository().getId(), itemId);

			GatewayRuntimeConfiguration conf = new GatewayRuntimeConfiguration(id, null, repositoryCtx.getGroup().getId());
			conf.setRuntimeUrl(repositoryCtx.getVanillaContext().getVanillaUrl());

			RemoteGatewayComponent remote = new RemoteGatewayComponent(repositoryCtx.getVanillaContext());

			Logger.getLogger(getClass()).debug("Launching Gateway...");
			GatewayRuntimeState state = remote.runGateway(conf, user);
			Logger.getLogger(getClass()).debug("Gateway terminated.");

			if (state != null && (state.getState() != ActivityState.ENDED || state.getState() == ActivityState.FAILED)) {
				throw new Exception(state.getFailureCause());
			}

			if (state.getState() != ActivityState.ENDED) {
			}
		} catch (Exception ex) {
			throw new Exception("An error occured during the launch of the gateway.\n " + ex.getMessage());
		}
	}

	private void launchWorkflow(int itemId, IRepositoryContext repositoryCtx) throws Exception {
		try {
			IRuntimeConfig conf = new RuntimeConfiguration(repositoryCtx.getGroup().getId(), new ObjectIdentifier(repositoryCtx.getRepository().getId(), itemId), null);

			RemoteWorkflowComponent remote = new RemoteWorkflowComponent(repositoryCtx.getVanillaContext());

			Logger.getLogger(getClass()).debug("Launching Workflow...");
			IRunIdentifier wkfId = remote.startWorkflow(conf);
			if (wkfId != null) {
				TaskInfo taskInfos = remote.getTasksInfo(wkfId);
				if (taskInfos != null && taskInfos.getResult() == ActivityResult.FAILED) {
					throw new Exception(taskInfos.getFailureCause());
				}
			}
			Logger.getLogger(getClass()).debug("Workflow terminated.");
		} catch (Exception ex) {
			throw new Exception("An error occured during the launch of the workflow.\n " + ex.getMessage());
		}
	}

	@Override
	public DataWithCount<Contract> getContracts(Integer directoryId, Integer groupId, String query, int firstResult, int length, DataSort sort) throws Exception {
		return mdmDao.getContracts(directoryId, groupId, query, firstResult, length, sort);
	}

	@Override
	public MdmDirectory saveOrUpdateDirectory(MdmDirectory directory) throws Exception {
		return mdmDao.saveOrUpdateDirectory(directory);
	}

	@Override
	public void removeDirectory(MdmDirectory directory) throws Exception {
		mdmDao.removeDirectory(directory);
	}

	@Override
	public List<MdmDirectory> getDirectories(Integer parentId, boolean includeSubdirectories) throws Exception {
		return mdmDao.getDirectories(parentId, includeSubdirectories);
	}

	@Override
	public ContractIntegrationInformations getIntegrationInfosByContract(int contractId) throws Exception {
		return mdmDao.getIntegrationInfos(contractId);
	}

	@Override
	public ContractIntegrationInformations getIntegrationInfosByLimesurvey(String limesurveyId) throws Exception {
		return mdmDao.getIntegrationInfos(limesurveyId);
	}

	@Override
	public ContractIntegrationInformations saveOrUpdateIntegrationInfos(ContractIntegrationInformations integrationInfos) throws Exception {
		return mdmDao.saveOrUpdateIntegrationInfos(integrationInfos);
	}
	
	@Override
	public List<ContractIntegrationInformations> getKpiInfosByDatasetId(String datasetId) throws Exception {
		return mdmDao.getKpiInfos(datasetId);
	}
	
	@Override
	public List<ContractIntegrationInformations> getIntegrationInfosByOrganisation(String organisation, ContractType type) throws Exception {
		return mdmDao.getIntegrationInfosByOrganisation(organisation, type);
	}

	@Override
	public void removeIntegrationInfos(ContractIntegrationInformations integrationInfos) throws Exception {
		mdmDao.removeIntegrationInfos(integrationInfos);
	}
	
	@Override
	public List<DocumentSchema> getDocumentSchemas(int contractId) throws Exception {
		return mdmDao.getDocumentSchemas(contractId);
	}
	
	@Override
	public void removeDocumentSchema(DocumentSchema docSchema) throws Exception {
		mdmDao.removeDocumentSchema(docSchema);
	}
	
	@Override
	public void saveOrUpdateDocumentSchema(DocumentSchema docSchema) throws Exception {
		mdmDao.saveOrUpdateDocumentSchema(docSchema);
	}
}
