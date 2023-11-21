package bpm.architect.web.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import bpm.architect.web.client.services.ArchitectService;
import bpm.architect.web.client.utils.DocumentHelper;
import bpm.architect.web.server.security.ArchitectSession;
import bpm.architect.web.shared.HistoricLog;
import bpm.architect.web.shared.HistoricLog.HistoricType;
import bpm.gateway.core.veolia.ReflectionHelper;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.Customer;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.utils.SchemaHelper;

public class ArchitectServiceImpl extends RemoteServiceServlet implements ArchitectService {
	
	private static final double BIG_FILE = 50000000;

	private static final long serialVersionUID = 1L;

	private ArchitectSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), ArchitectSession.class);
	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(ArchitectSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}

	@Override
	public DataWithCount<Contract> getContracts(Integer directoryId, String query, int firstResult, int length, DataSort sort) throws ServiceException {
		ArchitectSession session = getSession();
		IGedComponent gedComponent = session.getGedComponent();
		IVanillaSecurityManager securityManager = session.getVanillaApi().getVanillaSecurityManager();

		int groupId = session.getCurrentGroup().getId();

		try {
			DataWithCount<Contract> contracts = session.getMdmRemote().getContracts(directoryId, groupId, query, firstResult, length, sort);
			for (Contract contract : contracts.getItems()) {
				buildContract(gedComponent, securityManager, contract);
			}

			return contracts;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get documents : " + e.getMessage());
		}
	}

	private void buildContract(IGedComponent gedComponent, IVanillaSecurityManager securityManager, Contract contract) throws Exception {
		if (contract.getDocId() != null) {
			GedDocument doc = gedComponent.getDocumentDefinitionById(contract.getDocId());
			if (doc.getDocumentVersions() != null) {
				for (DocumentVersion version : doc.getDocumentVersions()) {
					if (version.getModifiedBy() > 0) {
						User user = securityManager.getUserById(version.getModifiedBy());
						version.setModificator(user);
					}
				}
			}

			contract.setFileVersions(doc);
		}
	}

	@Override
	public void confirmUpload(Contract contract, String documentName, String filePath) throws ServiceException {
		ArchitectSession session = getSession();
		int userId = session.getUser().getId();
		int repositoryId = session.getCurrentRepository().getId();

		try {
			GedDocument doc = contract.getFileVersions();
			if (doc != null) {
				String format = extractFormat(contract, doc, filePath);
				InputStream newVersion = session.getPendingNewVersion();

				session.getGedComponent().addVersionToDocumentThroughServlet(doc.getId(), format, newVersion);
			}
			else {
				String format = DocumentHelper.getFormat(filePath);
				InputStream newVersion = session.getPendingNewVersion();
				
				List<Integer> groupIds = session.getMdmRemote().getSupplierSecurity(contract.getParent().getId());

				doc = session.getGedComponent().createDocumentThroughServlet(documentName, format, userId, groupIds, repositoryId, newVersion);
				contract.setFileVersions(doc);
			}

			contract.setVersionId(null);
			saveOrUpdateContract(contract, HistoricType.ADD_VERSION);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add a new version: " + e.getMessage());
		}
	}

	private String extractFormat(Contract contract, GedDocument doc, String filePath) {
		if (doc != null && doc.getCurrentVersion(contract.getVersionId()) != null && doc.getCurrentVersion(contract.getVersionId()).getFormat() != null) {
			return doc.getLastVersion().getFormat();
		}
		else {
			return DocumentHelper.getFormat(filePath);
		}
	}

	@Override
	public void saveOrUpdateDocumentItem(DocumentItem docItem) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			session.getMdmRemote().saveOrUpdateDocumentItem(docItem);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add a linked item: " + e.getMessage());
		}
	}

	@Override
	public void removeLinkedItem(DocumentItem docItem) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			session.getMdmRemote().removeDocumentItem(docItem);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to remove this linked item: " + e.getMessage());
		}
	}

//	@Override
//	public List<DocumentItem> getLinkedItems(int contractId) throws ServiceException {
//		ArchitectSession session = getSession();
//		try {
//			List<DocumentItem> docItems = session.getMdmRemote().getDocumentItems(contractId);
//			if (docItems != null) {
//				for (DocumentItem docItem : docItems) {
//					RepositoryItem item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(docItem.getItemId());
//					docItem.setItem(item);
//				}
//			}
//			return docItems;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new ServiceException("Unable to get all linked items: " + e.getMessage());
//		}
//	}

	@Override
	public List<ItemInstance> getItemHistoric(int itemId, Date startDate, Date endDate) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			Group group = session.getCurrentGroup();
			List<ItemInstance> instances = session.getRepositoryConnection().getAdminService().getItemInstances(itemId, startDate, endDate, group != null ? group.getId() : null);
			return instances;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get all linked items: " + e.getMessage());
		}
	}

	@Override
	public void saveOrUpdateContract(Contract contract, HistoricType type) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			session.getMdmRemote().addContract(contract);
			
			saveLog(session, type, contract.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get all linked items: " + e.getMessage());
		}
	}

	@Override
	public void removeContract(Contract contract) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			session.getMdmRemote().removeContract(contract);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to remove the contract : " + e.getMessage());
		}
	}

	@Override
	public String loadDocument(Contract contract, Integer versionId) throws ServiceException {
		try {
			ArchitectSession session = getSession();

			GedDocument document = contract.getFileVersions();

			DocumentVersion version = null;
			if (versionId != null) {
				version = session.getGedComponent().getDocumentVersionById(versionId);
			}
			else {
				version = document.getLastVersion();
			}

			String format = version.getFormat();

			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(document, session.getUser().getId(), version.getVersion());

			InputStream is = session.getGedComponent().loadGedDocument(config);
			byte currentXMLBytes[] = IOUtils.toByteArray(is);
			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
			
			boolean bigFile = currentXMLBytes.length > BIG_FILE;
			
			ObjectInputStream repIS = new ObjectInputStream();
			repIS.addStream(format, byteArrayIs);

			is.close();

			String fullName = clearName(contract.getFileVersions().getName(), format);
			session.addReport(fullName, repIS);
			
			saveLog(session, HistoricType.DOWNLOAD_VIEW, contract.getId());

			return CommonConstants.PREVIEW_REPORT_SERVLET + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + fullName + "&" + CommonConstants.REPORT_OUTPUT + "=" + format + "&" + CommonConstants.REPORT_BIG_FILE + "=" + bigFile;
		} catch (ServiceException e) {
			e.printStackTrace();
			return "<error>" + e.getMessage() + "</error>";
		} catch (Exception e) {
			e.printStackTrace();
			return "<error>" + e.getMessage() + "</error>";
		}
	}	
	
	private String clearName(String name, String format) {
		name = name.replace(format, "");
		name = name.replace("'", "");
		String nfdNormalizedString = Normalizer.normalize(name, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9_-]");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	@Override
	public Supplier saveOrUpdateSupplier(Supplier supplier, List<Group> groups) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			if (groups == null) {
				return session.getMdmRemote().addSupplier(supplier);
			}
			else {
				return session.getMdmRemote().addSupplier(supplier, groups);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get all linked items: " + e.getMessage());
		}
	}

	@Override
	public List<Supplier> getSuppliers() throws ServiceException {
		ArchitectSession session = getSession();

		int groupId = session.getCurrentGroup().getId();
		try {
			List<Supplier> suppliers = session.getMdmRemote().getSuppliersByGroupId(groupId);
			if (suppliers != null) {
				for (Supplier supplier : suppliers) {
					if (supplier.getContracts() != null) {
						List<Contract> supplierContracts = new ArrayList<>();
						for (Contract contract : supplier.getContracts()) {
							contract.setParent(supplier);
							supplierContracts.add(contract);
						}
						supplier.setContracts(supplierContracts);
					}
				}
			}
			return suppliers;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get suppliers : " + e.getMessage());
		}
	}
	
	@Override
	public List<Integer> getSupplierSecurity(int supplierId) throws ServiceException {
		ArchitectSession session = getSession();

		try {
			return session.getMdmRemote().getSupplierSecurity(supplierId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get supplier security : " + e.getMessage());
		}
	}

	@Override
	public void removeSupplier(Supplier supplier) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			session.getMdmRemote().removeSupplier(supplier);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to remove this supplier : " + e.getMessage());
		}
	}
	
	@Override
	public List<HistoricLog> buildHistoricLogs(Contract contract) throws ServiceException {
		ArchitectSession session = getSession();
		IVanillaAPI vanillaApi = session.getVanillaApi();
		try {
			List<VanillaLogs> logs = vanillaApi.getVanillaLoggingManager().getListVanillaLogs(contract.getId(), IRepositoryApi.ARCHITECT_WEB);
			User creator = null;
			if (contract.getUserId() != null && contract.getUserId() > 0) {
				creator = vanillaApi.getVanillaSecurityManager().getUserById(contract.getUserId());
			}
			
			List<HistoricLog> architectLogs = new ArrayList<>();
			architectLogs.add(new HistoricLog(HistoricType.CREATION, contract.getCreationDate(), creator));
			
			if (logs != null) {
				for (VanillaLogs log : logs) {
					HistoricType type = HistoricType.valueOf(log.getOperation());
					User user = null;
					if (log.getUserId() > 0) {
						user = vanillaApi.getVanillaSecurityManager().getUserById(log.getUserId());
					}
					
					HistoricLog architectLog = new HistoricLog(type, log.getDate(), user);
					architectLog.setMessage(log.getMessage());
					architectLogs.add(architectLog);
				}
			}
			
			return architectLogs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to remove this supplier : " + e.getMessage());
		}
	}
	
	private void saveLog(ArchitectSession session, HistoricType typeAction, int contractId) throws Exception {
		int userId = session.getUser().getId();
		int groupId = session.getCurrentGroup().getId();
		int repId = session.getCurrentRepository().getId();
		
		VanillaLogs log = new VanillaLogs(Level.INFO, "bpm.architect", typeAction.name(), new Date(), userId, groupId, repId, contractId, "", "", 0);	
		log.setObjectType(IRepositoryApi.ARCHITECT_WEB);
		
		session.getVanillaApi().getVanillaLoggingManager().addVanillaLog(log);
	}

	@Override
	public List<ClassDefinition> getAvailableClasses() throws ServiceException {
		ArchitectSession session = getSession();
		Customer customer = session.getCustomer();
		
		List<ClassDefinition> classes = new ArrayList<>();
		if (customer == Customer.VE) {
			ClassDefinition classAbonnes = new ClassDefinition("Abonnes", "bpm.gateway.core.veolia.abonnes.GRC");
			ClassDefinition classPatrimoine = new ClassDefinition("Patrimoine", "bpm.gateway.core.veolia.patrimoine.Patrimoine");
			ClassDefinition classPatrimoineFile = new ClassDefinition("PatrimoineXLS", "bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls");
			
			classes.add(classAbonnes);
			classes.add(classPatrimoine);
			classes.add(classPatrimoineFile);
		}
		ClassDefinition classSchemas = new ClassDefinition("Schemas de validation", "schema_validation");
		classes.add(classSchemas);
		return classes;
	}

	@Override
	public ClassDefinition buildClassDefinition(ClassDefinition myClass) throws ServiceException {
		try {
			ArchitectSession session = getSession();
			
			ClassDefinition classDef = null;
			if (myClass.getIdentifiant().equals("schema_validation")) {
				classDef = new ClassDefinition();
				classDef.setIdentifiant(myClass.getIdentifiant());
				
				String schemaPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "schemas/";
				List<Path> jsonSchemas = Files.walk(Paths.get(schemaPath))
                        .filter(p -> p.toString().endsWith(".json"))
                        .collect(Collectors.toList());
				
				List<ClassRule> classRules = session.getVanillaApi().getResourceManager().getClassRules(myClass.getIdentifiant());

				for (Path schema : jsonSchemas) {
					//Get filename without extension
					String name = schema.getFileName().toString().replaceFirst("[.][^.]+$", "");
					
					ClassDefinition classSchema = SchemaHelper.loadSchema(schema.toString(), name, false);
					if (classSchema != null) {
						classDef.addClass(classSchema);
					}
				}

				List<ClassRule> rulesToApply = new ArrayList<ClassRule>();
				for (ClassDefinition schema : classDef.getClasses()) {
					if (classRules != null) {
						for (ClassRule rule : classRules) {
							if (rule.getParentPath().contains(schema.getPath())) {
								rulesToApply.add(rule);
							}
						}
					}
				}
				
				ReflectionHelper.buildClassDefinitionWithRules(null, classDef, rulesToApply, false);
			}
			else {
				classDef = ReflectionHelper.loadClass(myClass.getIdentifiant());
				List<ClassRule> classRules = session.getVanillaApi().getResourceManager().getClassRules(myClass.getIdentifiant());
				
				ReflectionHelper.buildClassDefinitionWithRules(null, classDef, classRules, false);
			}
			
			return classDef;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load class: " + e.getMessage());
		}
	}

	@Override
	public String loadSchema(ClassDefinition schema) throws ServiceException {
		try {
			ArchitectSession session = getSession();

			String schemaName = schema.getIdentifiant();
			String format = "json";
			String schemaFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "schemas/";
			
			boolean isAPI = false;
			String apiUrl = null;
			if (schemaName.contains("api.")) {
				isAPI = true;
				try(FileInputStream fis = new FileInputStream(schemaFolder + schemaName + "." + format)) {
					apiUrl = IOUtils.toString(fis);
				} catch (Exception e) {
					throw e;
				}
			}
			
			try (InputStream is = isAPI ? new URL(apiUrl).openStream() : new FileInputStream(schemaFolder + schemaName + "." + format);
					BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
				byte currentXMLBytes[] = IOUtils.toByteArray(br);
				ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
				
				ObjectInputStream repIS = new ObjectInputStream();
				repIS.addStream(format, byteArrayIs);

				session.addReport(schemaName, repIS);

				return CommonConstants.PREVIEW_REPORT_SERVLET + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + schemaName + "&" + CommonConstants.REPORT_OUTPUT + "=" + format + "&" + CommonConstants.REPORT_BIG_FILE + "=true";
			} catch (Exception e) {
				throw e;
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}	

	@Override
	public ClassRule saveOrUpdateClassRule(ClassRule classRule) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			return session.getVanillaApi().getResourceManager().addOrUpdateClassRule(classRule);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save or update this rule : " + e.getMessage());
		}
	}

	@Override
	public void deleteClassRule(ClassRule classRule) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			session.getVanillaApi().getResourceManager().removeClassRule(classRule);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to remove this rule : " + e.getMessage());
		}
	}
	
	@Override
	public void generateIntegrationProcess(ContractIntegrationInformations integrationInfos) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			
			IRepositoryContext ctx = getRepositoryContext(session, session.getVanillaContext());
			session.getVanillaApi().getResourceManager().generateIntegration(ctx, integrationInfos, false, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to remove this rule : " + e.getMessage());
		}
	}
	
	@Override
	public void deleteIntegration(ContractIntegrationInformations integrationInfos) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			
			IRepositoryContext ctx = getRepositoryContext(session, session.getVanillaContext());
			session.getVanillaApi().getResourceManager().deleteIntegration(ctx, integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to remove this rule : " + e.getMessage());
		}
	}

	private IRepositoryContext getRepositoryContext(ArchitectSession session, IVanillaContext vanillaContext) {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		int groupId = -1;
		if (session.getCurrentGroup() != null) {
			groupId = session.getCurrentGroup().getId();
		}
		else {
			groupId = Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID));
		}
		Group group = new Group();
		group.setId(groupId);

		int repositoryId = -1;
		if (session.getCurrentRepository() != null) {
			repositoryId = session.getCurrentRepository().getId();
		}
		else {
			repositoryId = Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID));
		}
		Repository repository = new Repository();
		repository.setId(repositoryId);
		
		return new BaseRepositoryContext(vanillaContext, group, repository);
	}

	@Override
	public ContractIntegrationInformations getIntegrationInfos(int contractId) throws ServiceException  {
		ArchitectSession session = getSession();
		try {
			return session.getMdmRemote().getIntegrationInfosByContract(contractId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to remove this supplier : " + e.getMessage());
		}
	}
	
	@Override
	public String getSourceUrl(ContractIntegrationInformations integrationInfos) throws ServiceException {
		switch (integrationInfos.getType()) {
		case LIMESURVEY:
		case LIMESURVEY_SHAPES:
			String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_LIMESURVEY_URL);
			url += "/index.php?r=survey/index&sid=" + integrationInfos.getItem();
			return url;
		case API:
			return integrationInfos.getItem();
		default:
			break;
		}
		return "";
	}
	
	@Override
	public String getDatasetUrl(ContractIntegrationInformations integrationInfos) throws ServiceException {
		String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_D4C_URL);
		url += "/visualisation/?id=" + integrationInfos.getTargetDatasetName();
		return url;
	}

	@Override
	public void saveOrUpdateSchema(DocumentSchema docSchema) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			session.getMdmRemote().saveOrUpdateDocumentSchema(docSchema);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add a linked item: " + e.getMessage());
		}
	}

	@Override
	public void removeLinkedSchema(DocumentSchema docSchema) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			session.getMdmRemote().removeDocumentSchema(docSchema);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to remove this linked item: " + e.getMessage());
		}
	}
	
	@Override
	public void deleteSchema(ClassDefinition classDef) throws ServiceException {
		try {
			String schemaPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "schemas/";
			List<Path> jsonSchemas = Files.walk(Paths.get(schemaPath))
                    .filter(p -> p.toString().endsWith(".json"))
                    .collect(Collectors.toList());
			
			for (Path schema : jsonSchemas) {
				//Get filename without extension
				String name = schema.getFileName().toString().replaceFirst("[.][^.]+$", "");
				
				if (classDef.getName().equals(name)) {
					// We delete the schema
					boolean isDelete = new File(schema.toString()).delete();
					System.out.println("The schema has been deleted : " + isDelete);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to remove this linked item: " + e.getMessage());
		}
	}

	@Override
	public void addAPISchema(String name, String url) throws ServiceException {
		// We store the URL in a file and put it in the schema folder
		try {
			String schemaPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "schemas/";
			
			// Clean name and create file name
			String filename = cleanName(name);
			filename = "api." + filename + ".json";
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(schemaPath + "/" + filename))) {
			    writer.write(url);
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add this schema: " + e.getMessage());
		}
	}
	
	private String cleanName(String name) {
        // Remove any special characters
        String cleanedName = name.replaceAll("[^a-zA-Z0-9]", "_");
        // Remove any leading or trailing underscores
        cleanedName = cleanedName.replaceAll("^_+|_+$", "");
        // Replace consecutive underscores with a single underscore
        cleanedName = cleanedName.replaceAll("__+", "_");
        // Convert the name to lowercase
        cleanedName = cleanedName.toLowerCase();
        return cleanedName;
    }
}
