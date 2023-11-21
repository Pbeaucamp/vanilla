package bpm.architect.web.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
import org.json.JSONObject;

import bpm.architect.web.client.services.ArchitectService;
import bpm.architect.web.client.utils.DocumentHelper;
import bpm.architect.web.server.security.ArchitectSession;
import bpm.architect.web.server.utils.AdresseGeoLocHelper;
import bpm.architect.web.server.utils.Coordinate;
import bpm.architect.web.shared.HistoricLog;
import bpm.architect.web.shared.HistoricLog.HistoricType;
import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.data.viz.core.preparation.ExportPreparationInfo;
import bpm.data.viz.core.preparation.LinkItem;
import bpm.gateway.core.veolia.ReflectionHelper;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.gwt.commons.shared.analysis.ChartData;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.model.supplier.Supplier;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.vanilla.map.core.design.MapHelper;
import bpm.vanilla.map.core.design.MapLayer;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.MapZone;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.chart.Serie;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DataColumn.FunctionalType;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.Customer;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.CkanHelper;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
	public List<Contract> getContracts() throws ServiceException {
		ArchitectSession session = getSession();
		IGedComponent gedComponent = session.getGedComponent();
		IVanillaSecurityManager securityManager = session.getVanillaApi().getVanillaSecurityManager();

		int groupId = session.getCurrentGroup().getId();

		try {
			List<Contract> contracts = new ArrayList<>();

			List<Supplier> suppliers = session.getMdmRemote().getSuppliersByGroupId(groupId);
			if (suppliers != null) {
				for (Supplier supplier : suppliers) {
					if (supplier.getContracts() != null) {
						List<Contract> supplierContracts = new ArrayList<>();
						for (Contract contract : supplier.getContracts()) {
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

							supplierContracts.add(contract);
						}
						supplier.setContracts(supplierContracts);

						contracts.addAll(supplierContracts);
					}
				}
			}

			return contracts;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get documents : " + e.getMessage());
		}
	}

	@Override
	public void confirmUpload(Contract contract, String documentName, String filePath) throws ServiceException {
		ArchitectSession session = getSession();
		int userId = session.getUser().getId();
		int repositoryId = session.getCurrentRepository().getId();

		try {
			GedDocument doc = null;

			if (contract.getDocId() != null) {
				doc = session.getGedComponent().getDocumentDefinitionById(contract.getDocId());
			}

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

	@Override
	public RScriptModel getRScript(Dataset dataset) throws ServiceException {
		RScriptModel script = null;
		try {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String smartRuntimUrl = config.getProperty(VanillaConfiguration.P_SMART_RUNTIME_URL);

			RemoteAdminManager adm = new RemoteAdminManager(smartRuntimUrl, null, Locale.getDefault());
			String sessionId = adm.connect(getSession().getUser());

			RemoteSmartManager smartManager = new RemoteSmartManager(smartRuntimUrl, sessionId, getThreadLocalRequest().getLocale());
			smartManager.addDatasettoR(dataset);
			script = smartManager.generateSummaryRPlot(dataset);
			return script;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return script;
	}

	@Override
	public String getFormat(Contract contract) throws ServiceException {
		ArchitectSession session = getSession();

		GedDocument doc = null;
		if (contract.getDocId() != null) {
			try {
				doc = session.getGedComponent().getDocumentDefinitionById(contract.getDocId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (doc != null && doc.getCurrentVersion(contract.getVersionId()) != null && doc.getCurrentVersion(contract.getVersionId()).getFormat() != null) {
			return doc.getLastVersion().getFormat();
		}
		return "csv";
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
	public void addLinkedItem(DocumentItem docItem) throws ServiceException {
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

	@Override
	public List<DocumentItem> getLinkedItems(int contractId) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			List<DocumentItem> docItems = session.getMdmRemote().getDocumentItems(contractId);
			if (docItems != null) {
				for (DocumentItem docItem : docItems) {
					RepositoryItem item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(docItem.getItemId());
					docItem.setItem(item);
				}
			}
			return docItems;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get all linked items: " + e.getMessage());
		}
	}

	@Override
	public List<ItemInstance> getItemHistoric(int itemId, Date startDate, Date endDate) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			Group group = session.getCurrentGroup();
			return session.getRepositoryConnection().getAdminService().getItemInstances(itemId, startDate, endDate, group != null ? group.getId() : null);
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

			String fullName = new Object().hashCode() + "";
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

	@Override
	public void saveOrUpdateSupplier(Supplier supplier, List<Group> groups) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			if (groups == null) {
				session.getMdmRemote().addSupplier(supplier);
			}
			else {
				session.getMdmRemote().addSupplier(supplier, groups);
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
		return classes;
	}

	@Override
	public ClassDefinition buildClassDefinition(ClassDefinition myClass) throws ServiceException {
		try {
			ArchitectSession session = getSession();

			ClassDefinition classDef = ReflectionHelper.loadClass(myClass.getIdentifiant());
			List<ClassRule> classRules = session.getVanillaApi().getResourceManager().getClassRules(myClass.getIdentifiant());

			ReflectionHelper.buildClassDefinitionWithRules(null, classDef, classRules, false);

			return classDef;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load class: " + e.getMessage());
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
	public List<DataPreparation> getDataPreparations() throws ServiceException {
		ArchitectSession session = getSession();
		try {
			return session.getDatavizRemote().getDataPreparations();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get datapreparations : " + e.getMessage());
		}
	}

	@Override
	public DataPreparation saveDataPreparation(DataPreparation dataPrep) throws ServiceException {
		ArchitectSession session = getSession();
		dataPrep.setUserId(session.getUser().getId());
		try {
			DataPreparation d = session.getDatavizRemote().saveDataPreparation(dataPrep);
			return d;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save datapreparation : " + e.getMessage());
		}
	}

	@Override
	public void deleteDataPreparation(DataPreparation dataPrep) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			session.getDatavizRemote().deleteDataPreparation(dataPrep);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete datapreparation : " + e.getMessage());
		}
	}

	@Override
	public DataPreparationResult executeDataPreparation(DataPreparation dataPrep) throws ServiceException {
		ArchitectSession session = getSession();
		try {

			session.getUndoRedo(dataPrep).historize(dataPrep.getRules());

			return session.getDatavizRemote().executeDataPreparation(dataPrep);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to execute datapreparation : " + e.getMessage());
		}
	}
	
	@Override
	public Integer countDataPreparation(DataPreparation dataPrep) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			return session.getDatavizRemote().countDataPreparation(dataPrep);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to count datapreparation : " + e.getMessage());
		}
	}

	@Override
	public String exportPreparation(ExportPreparationInfo info) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			InputStream is = session.getDatavizRemote().exportDataPreparation(info);
			session.addStream(info.getName(), "csv", (ByteArrayInputStream) is);
			session.setPendingNewVersion(is);
			session.getDatapreps().put(info.getName(), info.getDataPreparation());
			return info.getName();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to export datapreparation : " + e.getMessage());
		}
	}

	@Override
	public DataPreparation undo(DataPreparation dataPrep) throws ServiceException {
		ArchitectSession session = getSession();
		try {

			try {
				dataPrep.setRules(session.getUndoRedo(dataPrep).undo());
			} catch (Exception e) {
			}

			return dataPrep;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to undo datapreparation : " + e.getMessage());
		}
	}

	@Override
	public DataPreparation redo(DataPreparation dataPrep) throws ServiceException {
		ArchitectSession session = getSession();
		try {

			try {
				dataPrep.setRules(session.getUndoRedo(dataPrep).redo());
			} catch (Exception e) {
			}

			return dataPrep;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to redo datapreparation : " + e.getMessage());
		}
	}

	@Override
	public void exportToCkan(String resourceName, CkanPackage pack, String exportFileKey) throws ServiceException {
		ArchitectSession session = getSession();
		ByteArrayInputStream is = session.getStream(exportFileKey, "csv");
		DataPreparation dataPrep = session.getDatapreps().get(exportFileKey);
		CkanHelper ckanHelper = new CkanHelper();
		try {
			JSONObject repos = new JSONObject();
			JSONArray array = new JSONArray();
			if(dataPrep.getLinkedItems() != null) {
				for (LinkItem item : dataPrep.getLinkedItems()) {
					try {
						PublicUrl url = getSession().getVanillaApi().getVanillaExternalAccessibilityManager().getPublicUrlsByItemIdRepositoryId(item.getItemId(), getSession().getRepositoryConnection().getContext().getRepository().getId()).get(0);
						List<PublicParameter> params = getSession().getVanillaApi().getVanillaExternalAccessibilityManager().getParametersForPublicUrl(url.getId());
						JSONObject repo = new JSONObject();
						repo.put("url", session.getVanillaRuntimeExternalUrl() + VanillaConstants.VANILLA_EXTERNAL_CALL + "?publickey=" + url.getPublicKey());
						JSONArray jparams = new JSONArray();
						for(PublicParameter p : params){
							/*JSONObject jp = new JSONObject();
							jp.put("name", p.getParameterName());
							jp.put("column", );*/
							jparams.put(p.getParameterName());
						}
						repo.put("parameters", jparams);
						
						array.put(repo);
					} catch(Exception e) {
	//					e.printStackTrace();
					}
				}
				
				repos.put("report", array);
				pack.putExtra("reports", repos.toString());
			}
			if (dataPrep.getMapInformation() != null && dataPrep.getMapInformation().getLayers() != null && !dataPrep.getMapInformation().getLayers().isEmpty()) {
				String overlays = "";
				for (MapLayer layer : dataPrep.getMapInformation().getLayers()) {
					JSONObject layerObjet = new JSONObject();

					layerObjet.put("name", layer.getName());
					layerObjet.put("label", layer.getTitle());
					layerObjet.put("provider", "custom_wms");
					layerObjet.put("url", layer.getParent().getUrl());
					layerObjet.put("minZoom", 5);
					layerObjet.put("maxZoom", 20);
					layerObjet.put("type", "layer");
					// layerObjet.put("key", "");
					layerObjet.put("attribution", layer.getTitle());
					// layerObjet.put("mapId", "");
					layerObjet.put("layers", layer.getName());

					// String params = layerObjet.toString().replace("{",
					// "?").replace("}", "").replace(",", "&").replace("\":",
					// "=").replace("\"", "");
					// System.out.println(params);
					try {
						callD4C("api/maps/addlayers", layerObjet.toString());
						// URLConnection conn = new
						// URL(ConfigurationManager.getProperty(VanillaConfiguration.P_D4C_URL)
						// + "api/maps/addlayers" + params).openConnection();
						// conn.getInputStream();
					} catch (Exception e) {
					}
					overlays += layer.getName() + ",";
				}
				if (!overlays.isEmpty()) {
					overlays = overlays.substring(0, overlays.length() - 1);
					pack.putExtra("overlays", overlays);
				}

			}
			pack.putExtra("type_map", "osm");
			pack = ckanHelper.uploadCkanFile(resourceName, pack, is);

			// pack.getExtras().get(key)

			// DataPreparation dp = session.getDatapreps().get(exportFileKey);
			// String geojson = map(dp);

			// XXX call precluster
			// wait for the datastore
			Thread.sleep(10000);

			// XXX call datastore to update types
			CkanPackage packag = ckanHelper.getCkanPackage(pack.getName());

			System.out.println();
			CkanResource res = null;
			for (CkanResource r : packag.getResources()) {
				if (r.getFormat().equalsIgnoreCase("csv")) {
					res = r;
					break;
				}
			}

			try {
				String stringfields = ckanHelper.getResourceFields(res.getId());
				if (stringfields.startsWith("200")) {
					stringfields = stringfields.substring(4, stringfields.length());
				}
				JSONObject json = new JSONObject(stringfields);
				JSONArray fields = json.getJSONObject("result").getJSONArray("fields");
				JSONArray filtered = new JSONArray();
				for (int i = 0; i < fields.length(); i++) {
					JSONObject f = fields.getJSONObject(i);
					String colName = f.getString("id");
					if (colName.equals("_id")) {
						continue;
					}

					// XXX set the type
					for (DataColumn col : dataPrep.getDataset().getMetacolumns()) {
						if (col.getColumnName().equals(colName)) {
							List<String> types = new ArrayList<>();
							if (col.getFt() != null) {
								switch (col.getFt()) {
								case ADRESSE:
								case CODE_POSTAL:
								case COMMUNE:
								case CONTINENT:
								case PAYS:
								case REGION:
									types.add("<!--facet-->");
									types.add("<!--table-->");
									types.add("<!--tooltip-->");
									types.add("<!--disjunctive-->");
									break;
								case ANNEE:
								case MOIS:
								case SEMAINE:
								case TRIMESTRE:
									types.add("<!--facet-->");
									types.add("<!--table-->");
									types.add("<!--tooltip-->");
									types.add("<!--disjunctive-->");
									break;
								case AVG:
								case COUNT:
								case MAX:
								case MIN:
								case SUM:
									types.add("<!--table-->");
									types.add("<!--tooltip-->");
									break;
								case NON_EXCLUSIF:
								case DIMENSION:
								case EXCLUSIF:
									types.add("<!--facet-->");
									types.add("<!--table-->");
									types.add("<!--tooltip-->");
									types.add("<!--disjunctive-->");
									break;
								case ZONEID:
									types.add("<!--table-->");
									break;

								}

							}
							else {
								try {
									if (col.getTypes() != null) {
										if (col.getTypes().isDateDebut()) {
											types.add("<!--startDate-->");
										}
										if (col.getTypes().isDateFin()) {
											types.add("<!--endDate-->");
										}
										if (col.getTypes().isDateHeure()) {
											types.add("<!--timeserie_precision-->");
										}
										if (col.getTypes().isDatePonctuelle()) {
											types.add("<!--date-->");
										}
										if (col.getTypes().isFacette()) {
											types.add("<!--facet-->");
										}
										if (col.getTypes().isFacetteMultiple()) {
											types.add("<!--disjunctive-->");
										}
										if (col.getTypes().isFriseDate()) {
											types.add("<!--date_timeline-->");
										}
										if (col.getTypes().isFriseDescription()) {
											types.add("<!--descr_for_timeLine-->");
										}
										if (col.getTypes().isFriseLibelle()) {
											types.add("<!--title_for_timeLine-->");
										}
										if (col.getTypes().isImages()) {
											types.add("<!--images-->");
										}
										if (col.getTypes().isInfobulle()) {
											types.add("<!--tooltip-->");
										}
										if (col.getTypes().isNuageDeMot()) {
											types.add("<!--wordcount-->");
										}
										if (col.getTypes().isNuageDeMotNombre()) {
											types.add("<!--wordcountNumber-->");
										}
										if (col.getTypes().isTableau()) {
											types.add("<!--table-->");
										}
										if (col.getTypes().isTri()) {
											types.add("<!--sortable-->");
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							if (f.isNull("info")) {
								f.put("info", new JSONObject());
								f.getJSONObject("info").put("notes", "");
							}
							String notes = f.getJSONObject("info").getString("notes");
							for (String type : types) {
								if (!notes.contains(type)) {
									if (!notes.isEmpty()) {
										notes += ",";
									}
									notes += type;
								}
							}
							f.getJSONObject("info").put("notes", notes);
							break;
						}
					}

					filtered.put(f);
				}

				json = new JSONObject();
				json.put("resource_id", res.getId());
				json.put("force", "true");
				json.put("fields", filtered);

				ckanHelper.updateResourceDatastore(json);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String exportMap = ConfigurationManager.getProperty("P_D4C_EXPORT_MAP");
				if (exportMap != null && Boolean.parseBoolean(exportMap)) {

					String ress = callNode("https://localhost:1337/", pack.getName());
					// try {
					// URLConnection conn = new
					// URL(ConfigurationManager.getProperty(VanillaConfiguration.P_D4C_URL)
					// + "precluster?id=" + pack.getName()).openConnection();
					// conn.getInputStream();
					// } catch(Exception e) {
					// }

					Thread.sleep(10000);
					pack.setSelectedResource(null);
					ckanHelper.uploadCkanFile(resourceName + ".geojson", pack, new ByteArrayInputStream(Files.readAllBytes(Paths.get("/home/user-client/data/" + pack.getName() + ".geojson"))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

	}

	private String callNode(String nodeUrl, String packageName) throws Exception {
		final SSLConnectionSocketFactory sslsf;
		try {
			sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), NoopHostnameVerifier.INSTANCE);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", new PlainConnectionSocketFactory()).register("https", sslsf).build();

		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		cm.setMaxTotal(100);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).build();

		HttpGet getRequest;
		try {
			getRequest = new HttpGet(nodeUrl + "precluster?id=" + packageName);

			HttpResponse response = httpclient.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			boolean hasError = false;

			StringBuilder sb = new StringBuilder();
			sb.append(statusCode + "\n");
			if (statusCode != 200) {
				hasError = true;
			}

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}

			httpclient.close();
			if (hasError) {
				throw new Exception(sb.toString());
			}

			return sb.toString();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw ioe;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	private String callD4C(String action, String params) throws Exception {
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
		SSLContext.setDefault(ctx);
		final SSLConnectionSocketFactory sslsf;
		try {
			sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), NoopHostnameVerifier.INSTANCE);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", new PlainConnectionSocketFactory()).register("https", sslsf).build();

		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		cm.setMaxTotal(100);

		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).build();

		VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		String nodeUrl = vanillaConfig.getProperty(VanillaConfiguration.P_D4C_URL);
		HttpPost getRequest;
		try {
			getRequest = new HttpPost(nodeUrl + action);
			// getRequest.addHeader("content-type",
			// "application/x-www-form-urlencoded");
			// getRequest.setEntity(new StringEntity("json=" + params,
			// "UTF-8"));
			MultipartEntityBuilder multipart = MultipartEntityBuilder.create().addPart("json", new StringBody(params, Charset.forName("UTF-8")));
			HttpEntity reqEntity = multipart.build();
			getRequest.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			boolean hasError = false;

			StringBuilder sb = new StringBuilder();
			sb.append(statusCode + "\n");
			if (statusCode != 200) {
				hasError = true;
			}

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}

			httpclient.close();
			if (hasError) {
				throw new Exception(sb.toString());
			}

			return sb.toString();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw ioe;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	@Override
	public String getPublicUrl(int itemId) throws ServiceException {
		ArchitectSession session = getSession();
		try {
			int repositoryId = session.getRepositoryConnection().getContext().getRepository().getId();
			List<PublicUrl> urls = session.getVanillaApi().getVanillaExternalAccessibilityManager().getPublicUrlsByItemIdRepositoryId(itemId, repositoryId);
			if (urls != null && !urls.isEmpty()) {
				return session.getVanillaRuntimeExternalUrl() + VanillaConstants.VANILLA_EXTERNAL_CALL + "?publickey=" + urls.get(0).getPublicKey();
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get Public URL: " + e.getMessage());
		}
	}

	private class Coordinates {
		private String latitude;
		private String longitude;

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

	}

	@Override
	public String map(DataPreparation dp) {

		Date start = new Date();
		HashMap<String, List<Coordinates>> mapVanillaCoords = new HashMap<>();

		if (dp.getMap() != null) {
			try {
				RemoteMapDefinitionService mapRemote = new RemoteMapDefinitionService();
				mapRemote.configure(getSession().getVanillaApi().getVanillaUrl());
				Map<String, MapZone> zones = null;
				if(dp.getDataset().getDatasource().getObject() instanceof DatasourceFmdt) {
					zones = MapHelper.getMapZone(dp.getMapDataset(), getSession().getVanillaApi(), mapRemote, ((DatasourceFmdt)dp.getDataset().getDatasource().getObject()).getItemId());
				}
				else {
					zones = MapHelper.getMapZone(dp.getMapDataset(), getSession().getVanillaApi(), mapRemote);
				}
				
				for(String zoneId : zones.keySet()) {
					if (mapVanillaCoords.get(zones.get(zoneId).getGeoId()) == null) {
						List<Coordinates> v = new ArrayList<>();
						mapVanillaCoords.put(zones.get(zoneId).getGeoId(), v);
					}
					for(int i = 0 ; i < zones.get(zoneId).getLatitudes().size() ; i++) {
						Coordinates c = new Coordinates();
						c.setLatitude(zones.get(zoneId).getLatitudes().get(i));
						c.setLongitude(zones.get(zoneId).getLongitudes().get(i));
						mapVanillaCoords.get(zones.get(zoneId).getGeoId()).add(c);
						if(i > 0) {
							dp.getMapDataset().setType("polygon");
						}
					}
				}
				
				if(dp.getMapDataset().getType() == null) {
					dp.getMapDataset().setType("point");
				}
//				
//				MapDataSet selectedDataSet = dp.getMapDataset();
//
//				VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(selectedDataSet.getDataSource().getUrl(), selectedDataSet.getDataSource().getLogin(), selectedDataSet.getDataSource().getMdp(), selectedDataSet.getDataSource().getDriver());
//
//				VanillaPreparedStatement stmt = connection.prepareQuery(selectedDataSet.getQuery());
//
//				// map the zones
//				ResultSet rs = stmt.executeQuery();
//
//				while (rs.next()) {
//					String zoneId = rs.getString(selectedDataSet.getIdZone());
//					String lat = rs.getString(selectedDataSet.getLatitude());
//					String longi = rs.getString(selectedDataSet.getLongitude());
//
//					if (mapVanillaCoords.get(zoneId) == null) {
//						List<Coordinates> v = new ArrayList<>();
//						mapVanillaCoords.put(zoneId, v);
//					}
//					Coordinates c = new Coordinates();
//					c.setLatitude(lat);
//					c.setLongitude(longi);
//					mapVanillaCoords.get(zoneId).add(c);
//
//				}
//
//				rs.close();
//				stmt.close();
//				ConnectionManager.getInstance().returnJdbcConnection(connection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String json = null;

		ArchitectSession session;
		VanillaJdbcConnection conn = null;
		DataColumn codePostal = null;
		try {
			session = getSession();
			conn = getGeoConnection();
			DataPreparationResult result = session.getDatavizRemote().executeDataPreparation(dp);

			StringBuilder buf = new StringBuilder();
			boolean first = true;

			List<String> communeIds = new ArrayList<>();
			Map<String, Map<String, JSONArray>> coordinatesById = new HashMap<String, Map<String, JSONArray>>();

			for (Map<DataColumn, Serializable> line : result.getValues()) {
				for (DataColumn dc : line.keySet()) {
					if (line.get(dc) != null) {
						if (dc.getFt() == FunctionalType.COMMUNE) {
							if (!communeIds.contains(line.get(dc))) {
								communeIds.add(line.get(dc).toString());

								if (first) {
									first = false;
								}
								else {
									buf.append(",");
								}
								buf.append("'" + line.get(dc) + "'");
							}
						}
						if (dc.getFt() == FunctionalType.CODE_POSTAL) {
							codePostal = dc;
						}
					}
				}
			}
			//FIXME remettre ce caca
			if (!buf.toString().equals("")) {
				ResultSet rs = conn.prepareQuery("SELECT ville_nom_reel, ville_code_commune, ville_longitude_deg, ville_latitude_deg FROM villes_france_free WHERE ville_code_commune in (" + buf.toString() + ")").executeQuery();
				while (rs.next()) {
					JSONArray value = new JSONArray();
					value.put(rs.getDouble("ville_longitude_deg"));
					value.put(rs.getDouble("ville_latitude_deg"));
					Map<String, JSONArray> coord = new HashMap<String, JSONArray>();
					coord.put(rs.getString("ville_nom_reel"), value);
					coordinatesById.put(rs.getString("ville_code_commune"), coord);
				}
			}

			// main jsonobject
			JSONObject main = new JSONObject();
			main.put("type", "FeatureCollection");

			// map properties
			JSONObject mapProps = new JSONObject();
			if (dp.getMap() != null) {
				//FIXME test
				mapProps.put("name", dp.getMap().getProjection());
//				mapProps.put("name", "EPSG:900913");
				
			}
			else {
				mapProps.put("name", "EPSG:4326");
			}
			JSONObject crs = new JSONObject();
			crs.put("type", "name");
			crs.put("properties", mapProps);
			main.put("crs", crs);

			JSONArray features = new JSONArray();

			List<String> lstCountries = new ArrayList<String>();
			for (Map<DataColumn, Serializable> line : result.getValues()) {

				JSONObject feature = new JSONObject();
				feature.put("type", "Feature");

				JSONObject props = new JSONObject();

				for (DataColumn dc : line.keySet()) {
					if (line.get(dc) != null) {
						
						if (dc.getTypes() == null || dc.getTypes().isDisplayPopup()) {
							props.put(dc.getColumnLabel(), line.get(dc).toString());
						}

						if (dc.getFt() == FunctionalType.COMMUNE && !coordinatesById.isEmpty()) {
							try {
								JSONObject commune = new JSONObject();
								commune.put("type", "Point");

								String comName = coordinatesById.get(line.get(dc).toString()).keySet().iterator().next();
								props.put("nom_commune", comName);
								JSONArray coordinates = coordinatesById.get(line.get(dc).toString()).values().iterator().next();
								if (coordinates == null) {
									coordinates = new JSONArray();
									coordinates.put(0);
									coordinates.put(0);
								}
								commune.put("coordinates", coordinates);

								feature.put("geometry", commune);
							} catch (Exception e) {
								System.out.println(line.get(dc).toString() + " doesn't exist in city table.");
							}
						}
						if (dc.getFt() == FunctionalType.ADRESSE) {
							try {
								JSONObject commune = new JSONObject();
								commune.put("type", "Point");

								JSONArray coordinates = new JSONArray();

								if (codePostal != null) {
									Coordinate coordinate = AdresseGeoLocHelper.getGeoloc(false, line.get(dc).toString(), line.get(codePostal).toString(), 0.5);
									coordinates.put(coordinate.getLongitude());
									coordinates.put(coordinate.getLatitude());
								}
								else {
									Coordinate coordinate = AdresseGeoLocHelper.getGeoloc(true, line.get(dc).toString(), null, 0.5);
									coordinates.put(coordinate.getLongitude());
									coordinates.put(coordinate.getLatitude());
								}

//								if (coordinates == null) {
//									coordinates = new JSONArray();
//									coordinates.put(0);
//									coordinates.put(0);
//								}
								commune.put("coordinates", coordinates);

								feature.put("geometry", commune);
							} catch (Exception e) {
								System.out.println(line.get(dc).toString() + " doesn't exist in city table.");
							}
						}
						if (dc.getFt() == FunctionalType.PAYS) {
							if (!lstCountries.contains(line.get(dc).toString())) {
								lstCountries.add(line.get(dc).toString());

								JSONObject pays = new JSONObject();
								pays.put("type", "Polygon");

								String countryName = line.get(dc).toString().replaceAll("'", "''");
								ResultSet set = conn.prepareQuery("select * from country where name_country = '" + countryName + "'").executeQuery();

								JSONArray coordinates = new JSONArray();
								JSONArray coord = new JSONArray();
								coord.put(0);
								coord.put(0);
								while (set.next()) {
									coord = new JSONArray();
									coord.put(set.getDouble("longitude"));
									coord.put(set.getDouble("latitude"));

									coordinates.put(coord);
								}
								JSONArray extraCoord = new JSONArray();
								extraCoord.put(coordinates);

								pays.put("coordinates", extraCoord);
								feature.put("geometry", pays);
							}
						}
						if (dc.getFt() == FunctionalType.ZONEID) {

							try {
								JSONObject coordArray = new JSONObject();
								if (dp.getMapDataset().getType().equals("polygon")) {
									coordArray.put("type", "Polygon");

									JSONArray coordinates = new JSONArray();
									List<Coordinates> coords = mapVanillaCoords.get(line.get(dc).toString());
									for (Coordinates c : coords) {
										JSONArray coordinatesSub = new JSONArray();
										coordinatesSub.put(Double.parseDouble(c.getLongitude()));
										coordinatesSub.put(Double.parseDouble(c.getLatitude()));
										coordinates.put(coordinatesSub);
									}
									JSONArray extraCoord = new JSONArray();
									extraCoord.put(coordinates);

									coordArray.put("coordinates", extraCoord);
									feature.put("geometry", coordArray);
								}
								else {
									coordArray.put("type", "Point");

									JSONArray coordinates = new JSONArray();
									List<Coordinates> coords = mapVanillaCoords.get(line.get(dc).toString());
									coordinates.put(Double.parseDouble(coords.get(0).getLongitude()));
									coordinates.put(Double.parseDouble(coords.get(0).getLatitude()));

									coordArray.put("coordinates", coordinates);
									feature.put("geometry", coordArray);
								}
							} catch (Exception e) {
							}

						}
						if (dc.getFt() == FunctionalType.GEOLOCAL) {
							try {
								JSONObject commune = new JSONObject();
								commune.put("type", "Point");

								JSONArray coordinates = new JSONArray();
								String val = line.get(dc).toString();

								try {
									if (val != null && !val.isEmpty()) {
										String[] coords = val.split(",");
										coordinates.put(Double.parseDouble(coords[1]));
										coordinates.put(Double.parseDouble(coords[0]));
									}
									else {
										coordinates = null;
									}
								} catch (Exception e) {
									coordinates = null;
								}

								if (coordinates == null) {
									coordinates = new JSONArray();
									coordinates.put(0);
									coordinates.put(0);
								}
								else {
									commune.put("coordinates", coordinates);

									feature.put("geometry", commune);
								}
							} catch (Exception e) {
								System.out.println(line.get(dc).toString() + " doesn't exist in city table.");
							}
						}
						if (dc.getFt() == FunctionalType.LATITUDE || dc.getFt() == FunctionalType.LONGITUDE) {
							try {
								JSONObject commune = null;
								JSONArray coordinates = null;
								if(feature.has("geometry")) {
									commune = (JSONObject) feature.get("geometry");
									coordinates = (JSONArray) commune.get("coordinates");
								}
								else {
									commune = new JSONObject();
									coordinates = new JSONArray();
								}
								
								commune.put("type", "Point");
								
								String val = line.get(dc).toString();

								try {
									if (val != null && !val.isEmpty()) {
										if(coordinates.length() > 0) {
											if(dc.getFt() == FunctionalType.LATITUDE) {
												
												coordinates.put(Double.parseDouble(val));
											}
											else {
												double oldVal = (double) coordinates.get(0);
												coordinates.put(0, Double.parseDouble(val));
												coordinates.put(oldVal);
												
											}
										}
										else {
											coordinates.put(Double.parseDouble(val));
										}
										
									}
									else {
										coordinates = null;
									}
								} catch (Exception e) {
									coordinates = null;
								}

								if (coordinates == null) {
									coordinates = new JSONArray();
									coordinates.put(0);
									coordinates.put(0);
								}
								else {
									commune.put("coordinates", coordinates);

									feature.put("geometry", commune);
								}
							} catch (Exception e) {
								System.out.println(line.get(dc).toString() + " doesn't exist in city table.");
							}
						}
					}
				}
				feature.put("properties", props);
				features.put(feature);
			}

			main.put("features", features);
			json = main.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ConnectionManager.getInstance().returnJdbcConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println("time to generate geojson : " + (new
		// Date().getTime() - start.getTime()));
		 System.out.println(json);
		return json;
	}

	private VanillaJdbcConnection getGeoConnection() throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String driver = config.getProperty(VanillaConfiguration.P_AGILA_DB_DRIVERCLASSNAME);
		String dbUrl = config.getProperty(VanillaConfiguration.P_AGILA_DB_JDBCURL);
		String login = config.getProperty(VanillaConfiguration.P_AGILA_DB_USERNAME);
		String password = config.getProperty(VanillaConfiguration.P_AGILA_DB_PASSWORD);

		return ConnectionManager.getInstance().getJdbcConnection(dbUrl, login, password, driver);
	}

	@Override
	public void updateDataset(Dataset dataset) throws Exception {
		getSession().getVanillaApi().getVanillaPreferencesManager().updateDataset(dataset);
	}

	@Override
	public void createDatabase(String tableName, DataPreparation dataPrep, boolean insert) throws Exception {
		getSession().getDatavizRemote().createDatabase(tableName, dataPrep, insert);
	}

	@Override
	public String publicationETL(DataPreparation dp) throws Exception {
		return getSession().getDatavizRemote().publicationETL(dp);
	}

	@Override
	public RepositoryItem getRepositoryItemById(int repositoryId) {
		try {
			return getSession().getRepositoryConnection().getRepositoryService().getDirectoryItem(repositoryId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ChartData> getChartData(DataPreparationResult result, DataColumn axeX, List<Serie<DataColumn>> series) throws ServiceException {
		List<Map<DataColumn, Serializable>> values = result.getValues();

		HashMap<String, List<List<Serializable>>> chartDataMap = new LinkedHashMap<>();
		for (Map<DataColumn, Serializable> row : values) {
			String groupValue = row.get(axeX) != null ? row.get(axeX).toString() : "";

			List<List<Serializable>> serieValues = chartDataMap.get(groupValue);
			if (serieValues == null) {
				serieValues = new ArrayList<>();
				for (int i = 0; i < series.size(); i++) {
					serieValues.add(new ArrayList<Serializable>());
				}
			}

			for (int i = 0; i < series.size(); i++) {
				Serie<DataColumn> serie = series.get(i);

				Serializable value = row.get(serie.getData());
				serieValues.get(i).add(value);
			}

			chartDataMap.put(groupValue, serieValues);
		}

		List<ChartData> chartData = new ArrayList<>();
		for (String groupValue : chartDataMap.keySet()) {
			List<List<Serializable>> chartDataMapValue = chartDataMap.get(groupValue);

			List<String> serieValues = new ArrayList<>();
			for (int i = 0; i < chartDataMapValue.size(); i++) {
				Serie<DataColumn> serie = series.get(i);
				List<Serializable> chartDataValues = chartDataMapValue.get(i);

				Double currentValue = null;
				List<String> distinctValues = new ArrayList<>();
				for (Serializable value : chartDataValues) {
					try {
						if (value != null) {
							switch (serie.getAggregation()) {
							case "SUM":
							case "AVG":
								Double doubleValue = Double.parseDouble(value.toString());
								currentValue = currentValue != null ? currentValue + doubleValue : doubleValue;
								break;
							case "COUNT":
								currentValue = currentValue != null ? currentValue + 1 : 1;
								break;
							case "DISTINCT COUNT":
								if (!distinctValues.contains(value.toString())) {
									currentValue = currentValue != null ? currentValue + 1 : 1;
									distinctValues.add(value.toString());
								}
								break;
							case "MAX":
								doubleValue = Double.parseDouble(value.toString());
								currentValue = currentValue != null ? (doubleValue > currentValue ? doubleValue : currentValue) : doubleValue;
								break;
							case "MIN":
								doubleValue = Double.parseDouble(value.toString());
								currentValue = currentValue != null ? (doubleValue < currentValue ? doubleValue : currentValue) : doubleValue;
								break;
							default:
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (serie.getAggregation().equalsIgnoreCase("AVG")) {
					currentValue = currentValue / chartDataValues.size();
				}

				serieValues.add(currentValue.toString());
			}
			chartData.add(new ChartData(groupValue, serieValues));
		}

		return chartData;
	}

	@Override
	public List<MapVanilla> getMaps() throws ServiceException {
		ArchitectSession session = getSession();
		List<MapVanilla> lesMaps;

		try {
			lesMaps = session.getRemoteMap().getAllMapsVanilla();
			return lesMaps;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during the recuperation of maps : " + e.getMessage());
		}
	}

	@Override
	public Map<String, String> getCities() throws ServiceException {
		Map<String, String> res = new HashMap<String, String>();
		VanillaJdbcConnection conn = null;
		try {
			// session = getSession();
			conn = getGeoConnection();
			ResultSet rs = conn.prepareQuery("SELECT ville_nom_reel, ville_departement FROM villes_france_free").executeQuery();
			while (rs.next()) {
				String dep = rs.getString("ville_departement");
				String ville = rs.getString("ville_nom_reel");
				res.put(ville, ville + ", " + dep);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during the recuperation of cities : " + e.getMessage());
		}
		return res;
	}

	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
