package bpm.vanilla.platform.core.runtime.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.Security;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig.HistorizationTarget;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.listeners.event.impl.ItemVersionEvent;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.dao.ged.DocumentDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.SecurityDAO;
import bpm.vanilla.platform.core.runtime.tools.FireEventHelper;
import bpm.vanilla.platform.core.runtime.tools.OSGIHelper;
import bpm.vanilla.platform.core.utils.IOWriter;

public class HistoricReportManager extends AbstractVanillaManager implements ReportHistoricComponent {

	private DocumentDAO documentDao;
	private SecurityDAO securityDao;
	private IRepositoryManager repositoryManager;
	private IVanillaSecurityManager securityManager;

	@Override
	public void activate(ComponentContext ctx) {
		super.activate(ctx);
	}

	@Override
	protected void init() throws Exception {
		documentDao = getDao().getDocumentDao();
		if (documentDao == null) {
			throw new Exception("Historic documentDao has not been loaded");
		}
		securityDao = getDao().getSecurityDao();
		if (securityDao == null) {
			throw new Exception("Historic securityDao has not been loaded");
		}
	}

	/**
	 * Return a repositoryConnection created with the root user
	 * 
	 * @param repId
	 * @return
	 * @throws Exception
	 */
	private IRepositoryApi getRepositoryConnection(int repId, Integer groupId) throws Exception {
		Repository rep = repositoryManager.getRepositoryById(repId);
		String user = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String pass = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		Group group = null;
		if (groupId != null) {
			group = securityManager.getGroupById(groupId);
		}
		if (group == null) {
			group = new Group();
			group.setId(-1);
		}
		IRepositoryApi sock = null;
		try {
			sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), user, pass), group, rep));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sock;
	}

	public void bind(IRepositoryManager service) {
		repositoryManager = service;
		getLogger().info("RepositoryManager binded");
	}

	public void unbind(IRepositoryManager service) {
		repositoryManager = null;
		getLogger().info("RepositoryManager unbinded");
	}

	public void bind(IVanillaSecurityManager service) {
		securityManager = service;
		getLogger().info("VanillaSecurityManager binded");
	}

	public void unbind(IVanillaSecurityManager service) {
		securityManager = null;
		getLogger().info("VanillaSecurityManager unbinded");
	}

	@Override
	public void deleteHistoricEntry(List<GedDocument> entries, int repId) throws Exception {

		for (GedDocument doc : entries) {
			getRepositoryConnection(repId, null).getReportHistoricService().deleteHistoricEntry(doc.getId());
		}
	}

	@Override
	public List<GedDocument> getReportHistoric(IObjectIdentifier identifier, int groupId) throws Exception {
		List<Integer> ids = getRepositoryConnection(identifier.getRepositoryId(), null).getReportHistoricService().getHistorizedDocumentIdFor(identifier.getDirectoryItemId());

		List<GedDocument> docs = new ArrayList<GedDocument>();
		if (ids != null && !ids.isEmpty()) {
			for (Integer id : ids) {
				GedDocument doc = documentDao.findByPrimaryKey(id);
				if (doc != null) {

					Security sec = securityDao.findByIds(doc.getId(), groupId, identifier.getRepositoryId());
					if (groupId < 0 || (sec != null && sec.getIsAvailable() > 0)) {
						docs.add(doc);
					}
				}
			}
		}

		return docs;
	}

	@Override
	public GedDocument historize(HistorizationConfig conf, InputStream datas) throws Exception {
		String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);

		User user = securityManager.getUserByLogin(login);

		return createHistoricEntry(conf.getIdentifier(), user, datas, conf.getVanillaGroupId(), conf.getTargetType(), conf.getTargetIds(), conf.getEntryName(), conf.getEntryFormat(), null, null);
	}

	@Override
	public InputStream loadHistorizedDocument(Integer gedHistoricDocumentId) throws Exception {
		DocumentVersion doc = documentDao.getDocumentVersionDAO().getDocumentVersionById(gedHistoricDocumentId);
		return loadHistorizedDocument(doc);
	}

	@Override
	public InputStream loadHistorizedDocument(DocumentVersion gedHistoricDocument) throws Exception {

		if (gedHistoricDocument == null) {
			throw new Exception("No Historic found");
		}

		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);

		if (rootDir != null && !rootDir.endsWith("/")) {
			rootDir = rootDir + "/";
		}

		String path = rootDir + gedHistoricDocument.getDocumentPath();
		path = path.replace("/\\", "/");

		getLogger().info("Trying to retrive file : " + path);
		path = path.replace("//", "/");

		File f = new File(path);
		if (f.exists()) {
			return new FileInputStream(f);
		}
		else {
			throw new FileNotFoundException("The file " + path + " does not exist");
		}
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	public List<Integer> getGroupsAuthorizedByItemId(IObjectIdentifier identifier) throws Exception {

		List<Integer> ids = getRepositoryConnection(identifier.getRepositoryId(), null).getReportHistoricService().getAuthorizedGroupId(identifier.getDirectoryItemId());

		if (ids != null && !ids.isEmpty()) {
			List<Integer> groups = new ArrayList<Integer>();
			for (int i : ids) {
				groups.add(i);
			}
			return groups;
		}

		return new ArrayList<Integer>();
	}

	@Override
	public void grantHistoricAccess(int groupId, int directoryItem, int repositoryId) throws Exception {

		// Grant access to the report historics
		IRepositoryApi api = getRepositoryConnection(repositoryId, groupId);
		api.getReportHistoricService().createHistoricAccess(directoryItem, groupId);

		// find the existing historics
		List<Integer> geddocuments = new ArrayList<Integer>();
		try {
			for (int i : api.getReportHistoricService().getHistorizedDocumentIdFor(directoryItem)) {
				geddocuments.add(i);
			}
		} catch (Exception ex) {
			throw new Exception("Error when getting HistorizedDocument - " + ex.getMessage(), ex);
		}

		// for each historic, add the new rights for the concerned group
		for (int docId : geddocuments) {
			Security s = securityDao.findByIds(docId, groupId, repositoryId);
			if (s == null) {
				Security sec = new Security();
				sec.setDocumentId(docId);
				sec.setGroupId(groupId);
				sec.setRepositoryId(repositoryId);
				sec.setIsAvailable(1);
				this.securityDao.save(sec);
			}
			else if (s.getIsAvailable() != 1) {
				s.setIsAvailable(1);
				this.securityDao.update(s);
			}

		}
	}

	@Override
	public void removeHistoricAccess(int groupId, int directoryItem, int repositoryId) throws Exception {
		// remove access to the report historics
		IRepositoryApi api = getRepositoryConnection(repositoryId, groupId);
		api.getReportHistoricService().removeHistoricAccess(directoryItem, groupId);
		// find the existing historics
		List<Integer> geddocuments = new ArrayList<Integer>();
		try {
			for (int i : api.getReportHistoricService().getHistorizedDocumentIdFor(directoryItem)) {
				geddocuments.add(i);
			}
		} catch (Exception ex) {
			throw new Exception("Error when getting HistorizedDocument - " + ex.getMessage(), ex);
		}

		// for each historic, remove the rights for the concerned group
		for (int docId : geddocuments) {
			Security s = securityDao.findByIds(docId, groupId, repositoryId);

			if (s != null) {
				if (s.getIsAvailable() == 1) {
					s.setIsAvailable(0);
					this.securityDao.update(s);
				}
			}

		}
	}

	private GedDocument createNewDocument(int userId, String name, String format, InputStream datas, IObjectIdentifier identifier, int groupId, Integer historyId, Date peremptionDate) throws Exception {
		// XXX need to change
		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);

		if (rootDir != null && !rootDir.endsWith("/")) {
			rootDir = rootDir + "/";
		}

		File folder = new File(rootDir + "/historic");
		if (!folder.exists()) {
			folder.mkdirs();
		}

		// Integer[] ids = getRepositoryConnection(identifier.getRepositoryId(),
		// groupId).getReportHistoricService().getHistorizedDocumentIdFor(identifier.getDirectoryItemId(),
		// groupId);

		GedDocument doc = null;
		if (historyId != null && historyId > 0) {
			doc = documentDao.findByPrimaryKey(historyId);
		}

		if (doc == null) {
			doc = new GedDocument();
			doc.setName(name);
		}

		int index = 0;
		File file = null;
		do {
			index++;
			file = new File(folder, doc.getName() + "_" + index + new Object().hashCode() + "." + format);

		} while (file.exists());

		try {
			FileOutputStream fos = new FileOutputStream(file);
			IOWriter.write(datas, fos, true, true);
		} catch (Exception ex) {
			getLogger().error(ex.getMessage(), ex);
			throw new Exception("Unabe to save the historized file " + ex.getMessage());
		} finally {
			datas.close();
		}

		try {
			doc.setAccessCounter(0L);
			doc.setCreatedBy(userId);
			doc.setName(name);

			if (doc.getId() == 0) {
				doc.setId(documentDao.save(doc));
			}

			DocumentVersion version = new DocumentVersion();
			version.setDocumentId(doc.getId());
			version.setDocumentPath("/historic/" + file.getName());
			version.setIsIndexed(0);
			version.setModificationDate(new Date());
			version.setModifiedBy(userId);

			if (peremptionDate != null) {
				version.setPeremptionDate(peremptionDate);
			}

			int max = 1;
			boolean found = false;
			for (DocumentVersion v : doc.getDocumentVersions()) {
				if (max <= v.getVersion()) {
					max = v.getVersion();
					found = true;
				}
			}

			if (found) {
				version.setVersion(max + 1);
			}
			else {
				version.setVersion(max);
			}
			
			documentDao.getDocumentVersionDAO().addVersion(version);
			
			try {
				String sessionId = null;
				for (VanillaSession s : OSGIHelper.getSystemManager().getActiveSessions()) {
					if (s.getUser().getId().equals(userId)) {
						sessionId = s.getUuid();
						break;
					}
				}
				
				ItemVersionEvent event = new ItemVersionEvent(null, ItemVersionEvent.EVENT_ITEM_VERSION_UPDATED, identifier.getRepositoryId(), groupId, identifier.getDirectoryItemId(), version.getId(), sessionId);
				FireEventHelper.fireEvent(event);
			} catch(Exception ex) {
				ex.printStackTrace();
				getLogger().warn("Unable to trigger an event for this historisation.", ex);
			}
			
			doc.addDocumentVersion(version);
			getLogger().info("GedDocument " + doc.getId() + " saved as historic.");
			return doc;
		} catch (Exception ex) {
			getLogger().error(ex.getMessage(), ex);
			file.delete();
			throw new Exception("Could not store the GedDocument in database " + ex.getMessage());
		}

	}

	@Override
	public Integer historizeReport(HistoricRuntimeConfiguration conf, InputStream is) throws Exception {

		User user = securityManager.getUserById(conf.getUserId());
		GedDocument doc = createHistoricEntry(conf.getObjectIdentifier(), user, is, conf.getVanillaGroupId(), conf.getTargetType(), conf.getTargetIds(), conf.getEntryName(), conf.getFormat(), conf.getHistoryId(), conf.getPeremptionDate());
		DocumentVersion docVersion = doc.getLastVersion();
		if (docVersion != null) {
			return docVersion.getId();
		}
		else {
			return doc.getId();
		}
	}

	private GedDocument createHistoricEntry(IObjectIdentifier identifier, User user, InputStream datas, int groupId, HistorizationTarget targetType, List<Integer> targetIds, String entryName, String format, Integer historyId, Date peremptionDate) throws Exception {

		IRepositoryApi api = getRepositoryConnection(identifier.getRepositoryId(), groupId);

		RepositoryItem item = api.getRepositoryService().getDirectoryItem(identifier.getDirectoryItemId());

		GedDocument document = createNewDocument(user.getId(), entryName, format, datas, identifier, groupId, historyId, peremptionDate);

		if (historyId != null) {
			getLogger().info("We added a new version, so we don't change the history's grant.");
		}
		else {
			// Historizing for groups
			if (targetType == HistorizationTarget.Group) {
				// create the repositorySock for the user

				for (Integer i : targetIds) {
					// XXX
					api.getDocumentationService().mapReportItemToReportDocument(item, document.getId(), false);
					Security d = new Security();
					d.setDocumentId(document.getId());
					d.setRepositoryId(identifier.getRepositoryId());
					d.setUserId(user.getId());
					d.setGroupId(i);
					securityDao.save(d);
				}

			}
			// historizing for users
			else {

				Group dummyGr = new Group();
				dummyGr.setId(-1);
				// for(Integer i : targetIds){
				// User u = securityManager.getUserById(i);
				api = new RemoteRepositoryApi(

				new BaseRepositoryContext(new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), user.getLogin(), user.getPassword()), dummyGr, repositoryManager.getRepositoryById(identifier.getRepositoryId())));
				// XXX
				api.getDocumentationService().mapReportItemToReportDocument(item, document.getId(), true);
				// }
			}
		}
		return document;
	}

	@Override
	public void removeDocumentVersion(DocumentVersion version) throws Exception {
		documentDao.delete(version);
	}

}
