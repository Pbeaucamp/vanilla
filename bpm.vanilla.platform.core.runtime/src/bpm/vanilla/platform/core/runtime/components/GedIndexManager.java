package bpm.vanilla.platform.core.runtime.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import bpm.gateway.core.veolia.ReflectionHelper;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.Category;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.DocCat;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.IComProperties;
import bpm.vanilla.platform.core.beans.ged.Lock;
import bpm.vanilla.platform.core.beans.ged.Security;
import bpm.vanilla.platform.core.beans.ged.constant.RuntimeFields;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassField.TypeField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.beans.resources.OperatorClassic;
import bpm.vanilla.platform.core.beans.resources.RulePatternComparison;
import bpm.vanilla.platform.core.beans.resources.RuleValueComparison;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult.DataValidationResultStatut;
import bpm.vanilla.platform.core.beans.resources.ValidationRuleResult;
import bpm.vanilla.platform.core.beans.resources.ValidationSchemaResult;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedAdvancedSearchRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedSearchRuntimeConfig;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.runtime.dao.GlobalDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.CategoryDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DefinitionDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DocCatDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DocumentDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DocumentVersionDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.SecurityDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.StoredFieldDAO;
import bpm.vanilla.platform.core.runtime.ged.GedIndexHelper;
import bpm.vanilla.platform.core.runtime.ged.hdfs.HdfsHelper;
import bpm.vanilla.platform.core.runtime.ged.solr.SolrServer;
import bpm.vanilla.platform.core.utils.SchemaHelper;

public class GedIndexManager extends AbstractVanillaManager implements IGedComponent {

	private boolean useSolr = false;

	private DocumentDAO documentDao;
	private SecurityDAO securityDao;
	private DefinitionDAO definitionDao;
	private DocCatDAO docCatDao;
	private StoredFieldDAO storedFieldDao;
	private CategoryDAO categoryDao;
	private DocumentVersionDAO documentVersionDAO;
	private GlobalDAO globalDAO;
	
	private IVanillaContext rootVanillaCtx;

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	protected void init() throws Exception {
		documentDao = getDao().getDocumentDao();
		if (documentDao == null) {
			throw new Exception("DocumentDao has not been loaded");
		}
		securityDao = getDao().getSecurityDao();
		if (securityDao == null) {
			throw new Exception("securityDao has not been loaded");
		}
		docCatDao = getDao().getGedDocCatDao();
		if (docCatDao == null) {
			throw new Exception("docCatDao has not been loaded");
		}
		categoryDao = getDao().getGedCategoryDao();
		if (categoryDao == null) {
			throw new Exception("categoryDao has not been loaded");
		}
		definitionDao = getDao().getGedDefinitionDao();
		if (definitionDao == null) {
			throw new Exception("definitionDao has not been loaded");
		}
		storedFieldDao = getDao().getGedStoredFieldDao();
		if (storedFieldDao == null) {
			throw new Exception("storedFieldDao has not been loaded");
		}
		documentVersionDAO = getDao().getDocumentVersionDAO();
		if (documentVersionDAO == null) {
			throw new Exception("documentVersionDAO has not been loaded");
		}
		globalDAO = getDao().getGlobalDAO();
		if (globalDAO == null) {
			throw new Exception("globalDAO has not been loaded");
		}

		try {
			String use = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.solr.usesolr");
			if (use != null && Boolean.parseBoolean(use)) {
				useSolr = true;
			}
		} catch (Exception e) {
			useSolr = false;
		}

		this.rootVanillaCtx = getRootVanillaContext();
	}
	
	public IVanillaContext getRootVanillaContext() {
		if(rootVanillaCtx == null) {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String vanillaUrl = config.getVanillaServerUrl();
			String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
			String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
			rootVanillaCtx = new BaseVanillaContext(vanillaUrl, login, password);
		}
		return rootVanillaCtx;
	}

	@Override
	public void addAccess(int docId, int groupId, int repId) throws Exception {
		Security s = securityDao.findByIds(docId, groupId, repId);
		if (s == null) {
			s = new Security();
			s.setDocumentId(docId);
			s.setGroupId(groupId);
			s.setRepositoryId(repId);
			s.setIsAvailable(1);
			securityDao.save(s);
		}
		else {
			s.setIsAvailable(1);
			securityDao.update(s);
		}
	}

	@Override
	public void addCategory(Category category) throws Exception {
		categoryDao.save(category);
	}

	@Override
	public void addDocumentCategory(int docId, int catId) throws Exception {
		DocCat docCat = new DocCat();
		docCat.setCategoryId(catId);
		docCat.setDocumentId(docId);
		docCatDao.save(docCat);
	}

	@Override
	public void addFieldDefinition(Definition definition) throws Exception {
		definitionDao.save(definition);
	}

	@Override
	public void deleteCategory(Category category) throws Exception {
		categoryDao.delete(category);
	}

	@Override
	public void deleteDocumentCategory(int docId, int catId) throws Exception {
		List<DocCat> docCat = docCatDao.findByIds(catId, docId);
		if (docCat != null && docCat.size() > 0) {
			docCatDao.delete(docCat.get(0));
		}
	}

	@Override
	public void deleteFieldDefinition(Definition definition) throws Exception {
		definitionDao.delete(definition);
	}

	@Override
	public List<Definition> getFieldDefinitions(boolean includeCustom) throws Exception {
		return definitionDao.findAll();
	}

	@Override
	public List<Category> getGedCategories() throws Exception {
		return categoryDao.findAll();
	}

	@Override
	public int index(GedIndexRuntimeConfig config, InputStream is) throws Exception {
		if (useSolr) {
			getLogger().info("Use solr server");
			return SolrServer.getInstance().index(config, is, getLogger(), documentDao, securityDao, definitionDao, storedFieldDao, categoryDao, docCatDao);
		}
		else {
			getLogger().info("Use Lucene");
			return GedIndexHelper.index(config, is, getLogger(), documentDao, securityDao, definitionDao, storedFieldDao, categoryDao, docCatDao);
		}
	}

	@Override
	public InputStream loadGedDocument(GedLoadRuntimeConfig config) throws Exception {

		int id = config.getDefinition().getId();
		GedDocument gedDocument = documentDao.findByPrimaryKey(id);

		if (gedDocument == null) {
			throw new Exception("The document with id " + id + " cannot be found.");
		}

		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);

		if (rootDir != null && !rootDir.endsWith("/")) {
			rootDir = rootDir + "/";
		}

		DocumentVersion version = findDocumentVersion(gedDocument, config.getVersionToLoad());

		String path = rootDir + version.getDocumentPath();
		path = path.replace("/\\", "/");

		getLogger().info("Trying to retrive file : " + path);

		boolean useHdfs = Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HDFS_USE));
		if (useHdfs) {

			return HdfsHelper.loadFileFromHDFS(path);
		}

		File f = new File(path);
		if (f.exists()) {
			return new FileInputStream(f);
		}
		else {
			throw new FileNotFoundException("The file " + path + " does not exist");
		}
	}

	private DocumentVersion findDocumentVersion(GedDocument gedDocument, int versionToLoad) {
		DocumentVersion version = null;
		if (versionToLoad <= 0) {
			for (DocumentVersion v : gedDocument.getDocumentVersions()) {
				if (version == null) {
					version = v;
				}
				else if (v.getVersion() > version.getVersion()) {
					version = v;
				}
			}
		}
		else {
			for (DocumentVersion v : gedDocument.getDocumentVersions()) {
				if (v.getVersion() == versionToLoad) {
					version = v;
					break;
				}
			}
		}

		return version;
	}

	@Override
	public void removeAccess(int docId, int groupId, int repId) throws Exception {
		Security s = securityDao.findByIds(docId, groupId, repId);
		if (s != null) {
			// s.setIsAvailable(0);
			// securityDao.update(s);

			securityDao.delete(s);
		}
	}

	@Override
	public List<GedDocument> search(GedSearchRuntimeConfig config) throws Exception {
		if (useSolr) {
			getLogger().info("Use solr server");
			return SolrServer.getInstance().search(config, getLogger(), definitionDao, documentDao, securityDao);
		}
		else {
			getLogger().info("Use Lucene");
			return GedIndexHelper.search(config, getLogger(), definitionDao, documentDao, securityDao);
		}
	}

	@Override
	public void updateCategory(Category category) throws Exception {
		categoryDao.update(category);
	}

	@Override
	public void updateFieldDefinition(Definition definition) throws Exception {
		definitionDao.update(definition);
	}

	@Override
	public GedDocument getDocumentDefinitionById(int itemId) throws Exception {
		GedDocument doc = documentDao.findByPrimaryKey(itemId);
		try {
			List<DocCat> cat = docCatDao.findForDocument(doc.getId());

			if (cat != null && cat.size() > 0) {
				doc.setCategoryId(cat.get(0).getCategoryId());
			}
		} catch(Exception e) {
		}
		return doc;
	}

	@Override
	public DocumentVersion getDocumentVersionById(int versionId) throws Exception {
		DocumentVersion docVersion = documentVersionDAO.getDocumentVersionById(versionId);

		GedDocument doc = documentDao.findByPrimaryKey(docVersion.getDocumentId());
		docVersion.setParent(doc);

		return docVersion;
	}

	@Override
	public List<GedDocument> getAllDocuments() throws Exception {
		List<GedDocument> docs = documentDao.findAll();

		if (docs != null) {
			for (GedDocument doc : docs) {
				List<DocumentVersion> versions = documentVersionDAO.getDocumentVersionForDocument(doc);
				doc.setDocumentVersions(versions);
			}
		}

		return addCategoryToDocs(docs);
	}

	private List<GedDocument> addCategoryToDocs(List<GedDocument> docs) {
		for (GedDocument doc : docs) {
			List<DocCat> cat = docCatDao.findForDocument(doc.getId());

			if (cat != null && cat.size() > 0) {
				doc.setCategoryId(cat.get(0).getCategoryId());
			}
		}
		return docs;
	}

	@Override
	public List<GedDocument> getDocumentsToIndex() throws Exception {
		return addCategoryToDocs(documentDao.findWaitingDocs());
	}

	@Override
	public int addGedDocument(GedDocument doc, InputStream docInputStream, String format) throws Exception {
		int docId = documentDao.save(doc);

		// create firstVersion
		DocumentVersion version = new DocumentVersion();
		version.setModificationDate(new Date());
		version.setModifiedBy(doc.getCreatedBy());

		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);

		String path = "ged_documents" + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/" + Calendar.getInstance().get(Calendar.MONTH) + "/";

		path = path.replace("/\\", "/");
		File f = new File(rootDir + "/" + path);

		if (!f.exists()) {
			f.mkdirs();
		}

		path += doc.getId() + "_" + doc.getName() + "_V" + version.getVersion() + "." + format;
		FileOutputStream fos = new FileOutputStream(new File(rootDir + "/" + path));
		byte buffer[] = new byte[512 * 1024];
		int nbLecture;

		while ((nbLecture = docInputStream.read(buffer)) != -1) {
			fos.write(buffer, 0, nbLecture);
		}
		docInputStream.close();
		fos.close();

		version.setDocumentPath(path);
		version.setDocumentId(docId);

		documentVersionDAO.addVersion(version);

		return docId;
	}

	@Override
	public GedDocument createDocumentThroughServlet(String documentName, String format, int userId, List<Integer> groupIds, int repositoryId, InputStream fileInputStream) throws Exception {
		return createDocument(documentName, format, userId, groupIds, repositoryId, fileInputStream);
	}

	@Override
	public void deleteGedDocument(int docId) throws Exception {
		documentDao.delete(documentDao.findByPrimaryKey(docId));
	}

	@Override
	public void indexExistingFile(HistoricRuntimeConfiguration config, int docId, int version, boolean createEntry) throws Exception {
		GedDocument doc = documentDao.findByPrimaryKey(docId);
		String format = "any";
		try {
			format = doc.getVersion(version).getFormat();
		} catch (Exception e) {
			e.printStackTrace();
		}

		IComProperties comProps = createProperties(doc);

		GedIndexRuntimeConfig runtimeConfig = new GedIndexRuntimeConfig(comProps, doc.getCreatedBy(), -1, new ArrayList<Integer>(), -1, format, docId, version);

		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);

		DocumentVersion v = findDocumentVersion(doc, version);

		String path = rootDir + File.separator + v.getDocumentPath();
		InputStream is = new FileInputStream(new File(path));
		if (useSolr) {
			SolrServer.getInstance().index(runtimeConfig, is, getLogger(), documentDao, securityDao, definitionDao, storedFieldDao, categoryDao, docCatDao);
		}
		else {
			GedIndexHelper.index(runtimeConfig, is, getLogger(), documentDao, securityDao, definitionDao, storedFieldDao, categoryDao, docCatDao);
		}

		if (createEntry) {

			String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
			String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
			String password = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

			Group dummyGroup = new Group();
			dummyGroup.setId(config.getVanillaGroupId());

			Repository dummyRep = new Repository();
			dummyRep.setId(config.getObjectIdentifier().getRepositoryId());

			IRepositoryApi vanillaApi = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(vanillaUrl, login, password), dummyGroup, dummyRep));

			RepositoryDirectory dir = null;
			if (config.getDirectoryTargetId() != null) {
				dir = vanillaApi.getRepositoryService().getDirectory(config.getDirectoryTargetId());
			}

			if (dir == null) {
				getLogger().info("Report's not added because directory not found.");
			}
			else {
				getLogger().info("Report's result will be added to the repository in folder " + dir.getName());
				vanillaApi.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.GED_ENTRY, -1, dir, comProps.getValueForField("title"), "", "", "", "<indexid>" + v.getId() + "</indexid>", true);
			}
		}
		// end section comment
	}

	private IComProperties createProperties(GedDocument doc) {
		// create comProps
		List<Definition> defs = definitionDao.findAll();

		ComProperties comProps = new ComProperties();

		Definition author = findFieldByName(RuntimeFields.AUTHOR.getName(), defs);
		if (doc.getCreatedBy() > 0) {
			try {
				String vanilla = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
				String user = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
				String pass = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
				IVanillaAPI api = new RemoteVanillaPlatform(vanilla, user, pass);
				String userName = api.getVanillaSecurityManager().getUserById(doc.getCreatedBy()).getLogin();
				comProps.setProperty(author, userName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Definition t = findFieldByName(RuntimeFields.TITLE.getName(), defs);
		comProps.setProperty(t, doc.getName());

		return comProps;
	}

	private Definition findFieldByName(String fieldName, List<Definition> defs) {
		for (Definition def : defs) {
			if (def.getName().equals(fieldName)) {
				return def;
			}
		}

		return null;
	}

	@Override
	public List<GedDocument> getDocumentsByCategory(int catId) throws Exception {

		List<DocCat> docCats = docCatDao.findForCategory(catId);

		int[] docIds = new int[docCats.size()];

		int i = 0;
		for (DocCat dc : docCats) {
			docIds[i] = dc.getDocumentId();
			i++;
		}

		List<GedDocument> docs = documentDao.findByPrimaryKeys(docIds);

		return addCategoryToDocs(docs);
	}

	@Override
	public void resetGedIndex() throws Exception {
		// We just reset the index here.
		// The documents stays in the database but we change the "isIndexed"
		// flag to false.

		// clear the index
		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);
		File indexDirectory = new File(rootDir + File.separator + "ged_index");

		if (indexDirectory != null && indexDirectory.exists()) {
			for (File indexFile : indexDirectory.listFiles()) {
				indexFile.delete();
			}
		}

		List<GedDocument> docs = documentDao.findAll();
		for (GedDocument doc : docs) {
			for (DocumentVersion v : doc.getDocumentVersions()) {
				if (v.isIndexed()) {
					v.setIsIndexed(0);
					documentVersionDAO.updateVersion(v);
				}
			}
		}

	}

	@Override
	public boolean rebuildGedIndex() throws Exception {
		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);
		File indexDirectory = new File(rootDir + File.separator + "ged_index");

		try {
			// copy the index
			copyIndexDirectory(indexDirectory, new File(rootDir + File.separator + "temp_index"));

			// remove the index
			if (indexDirectory != null && indexDirectory.exists()) {
				for (File indexFile : indexDirectory.listFiles()) {
					indexFile.delete();
				}
			}

			// reindex files
			List<GedDocument> docs = documentDao.findAll();
			for (GedDocument doc : docs) {
				for (DocumentVersion v : doc.getDocumentVersions()) {
					if (v.isIndexed()) {
						try {
							indexExistingFile(null, doc.getId(), v.getVersion(), false);
						} catch (Exception e) {
							Logger.getLogger(GedIndexHelper.class.getName()).warning("The doc with id " + doc.getId() + " cannot be indexed");
							e.printStackTrace();
						}
					}
				}
			}

			// remove the old index
			for (File indexFile : new File(rootDir + File.separator + "temp_index").listFiles()) {
				indexFile.delete();
			}

			return true;
		} catch (Exception e) {
			Logger.getLogger(GedIndexHelper.class.getName()).warning("Failed to rebuild index");
			e.printStackTrace();
			// restore the old index
			copyIndexDirectory(new File(rootDir + File.separator + "temp_index"), indexDirectory);
			return false;
		}
	}

	/**
	 * Copy a directory
	 * 
	 * @param source
	 * @param destination
	 */
	private void copyIndexDirectory(File source, File destination) throws Exception {

		if (!destination.exists()) {
			destination.mkdir();
		}
		else {
			for (File f : destination.listFiles()) {
				f.delete();
			}
		}

		int i = 0;
		for (File f : source.listFiles()) {

			InputStream in = new FileInputStream(f);
			OutputStream out = new FileOutputStream(new File(destination, f.getName()));

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();

			i++;
		}

	}

	@Override
	public List<GedDocument> advancedSearch(GedAdvancedSearchRuntimeConfig config) throws Exception {
		getLogger().info("Use Lucene");
		return GedIndexHelper.advancedSearch(config, getLogger(), categoryDao, docCatDao, documentDao, securityDao, definitionDao);
	}

	@Override
	public List<Security> getSecuForDocument(GedDocument document) throws Exception {

		return securityDao.findForDocument(document.getId());
	}

	@Override
	public void updateSecurity(GedDocument document, Group group, boolean authorized, int repositoryId) throws Exception {
		for (Security sec : securityDao.findForDocument(document.getId())) {
			if (sec.getGroupId() == group.getId().intValue()) {
				sec.setIsAvailable(authorized ? 1 : 0);
				securityDao.update(sec);
				return;
			}
		}
		Security sec = new Security();
		sec.setDocumentId(document.getId());
		sec.setGroupId(group.getId());
		sec.setIsAvailable(authorized ? 1 : 0);
		sec.setRepositoryId(repositoryId);
		securityDao.save(sec);
	}

	@Override
	public GedDocument createDocument(String documentName, String format, int userId, List<Integer> groupIds, int repositoryId, InputStream docInputStream) throws Exception {
		ComProperties com = new ComProperties();
		com.setSimpleProperty(RuntimeFields.TITLE.getName(), documentName);

		GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(com, userId, -1, groupIds, -1, format, null, -1);
		config.setMdmAttached(true);

		int id = index(config, docInputStream);

		for (Integer groupId : groupIds) {
			addAccess(id, groupId, repositoryId);
		}

		return getDocumentDefinitionById(id);
	}

	@Override
	public DocumentVersion addVersionToDocument(GedDocument doc, String format, InputStream docInputStream) throws Exception {
		DocumentVersion version = new DocumentVersion();
		version.setModificationDate(new Date());
		version.setModifiedBy(doc.getCreatedBy());
		version.setDocumentId(doc.getId());

		int maxVersion = 0;
		for (DocumentVersion v : doc.getDocumentVersions()) {
			if (v.getVersion() > maxVersion) {
				maxVersion = v.getVersion();
			}
		}
		version.setVersion(maxVersion + 1);

		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);

		String path = "ged_documents" + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/" + Calendar.getInstance().get(Calendar.MONTH) + "/";
		path = path.replace("/\\", "/");
		File f = new File(rootDir + "/" + path);
		if (!f.exists()) {
			f.mkdir();
		}

		path += doc.getId() + "_" + doc.getName() + "_V" + version.getVersion() + "." + format;
//
//		FileOutputStream fos = new FileOutputStream(new File(rootDir + "/" + path));
//		byte buffer[] = new byte[512 * 1024];
//		int nbLecture;
//
//		while ((nbLecture = docInputStream.read(buffer)) != -1) {
//			fos.write(buffer, 0, nbLecture);
//
//		}
//		docInputStream.close();
//		fos.close();

		version.setDocumentPath(path);

		try {
			IComProperties comProps = new ComProperties();

			GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(comProps, doc.getCreatedBy(), -1, new ArrayList<Integer>(), -1, format, doc.getId(), version.getId());
			index(config, docInputStream);//new FileInputStream(new File(rootDir + "/" + path)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		GedDocument document = getDocumentDefinitionById(doc.getId());
		if (document.getLastVersion() != null) {
			version.setId(document.getLastVersion().getId());
		}
		
		return version;
	}

	@Override
	public DocumentVersion addVersionToDocumentThroughServlet(int docId, String format, InputStream fileInputStream) throws Exception {
		GedDocument doc = getDocumentDefinitionById(docId);
		return addVersionToDocument(doc, format, fileInputStream);
	}

	@Override
	public void indexExistingFile(HistoricRuntimeConfiguration config, Integer id, boolean createEntry) throws Exception {
		DocumentVersion version = documentVersionDAO.getDocumentVersionById(id);
		indexExistingFile(config, version.getDocumentId(), version.getVersion(), createEntry);
	}

	@Override
	public void checkin(int documentId, int userId, String format, InputStream stream, boolean index) throws Exception {
		if (documentId < 0) {
			throw new Exception("The Document Manager needs the Document ID to make a checkin.");
		}

		GedDocument doc = documentDao.findByPrimaryKey(documentId);
		if (doc == null) {
			throw new Exception("Document with id " + documentId + " has not been found in the database. Please check before checkin.");
		}

		Lock lock = documentDao.getLockForDocument(documentId);
		if (lock == null) {
			throw new Exception("This document cannot be checkin because it has not been checkout already.");
		}
		else if (lock.getUserId() != userId) {
			throw new Exception("This document cannot be checkin because it has not been checkout by this user.");
		}

		DocumentVersion docVersion = addVersionToDocument(doc, format, stream);
		documentDao.deleteLock(lock);

		if (index) {
			indexExistingFile(null, documentId, docVersion.getVersion(), false);
		}
	}

	@Override
	public void checkout(int documentId, int userId) throws Exception {
		if (documentId < 0) {
			throw new Exception("The Document Manager needs the Document ID to make a checkout.");
		}
		if (documentDao.getLockForDocument(documentId) != null) {
			throw new Exception("This document cannot be checkout because it is already checkout by somebody else.");
		}

		// We create the lock object
		Lock lock = new Lock();
		lock.setDocId(documentId);
		lock.setUserId(userId);
		lock.setLockDate(new Date());

		documentDao.saveLock(lock);
	}

	@Override
	public boolean canCheckin(int documentId, int userId) throws Exception {
		Lock lock = documentDao.getLockForDocument(documentId);
		if (lock != null) {
			if (lock.getUserId() == userId) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canCheckout(int documentId) throws Exception {
		return documentDao.getLockForDocument(documentId) == null ? true : false;
	}

	@Override
	public List<GedDocument> getDocuments(List<Integer> groupIds, int repositoryId) throws Exception {
		List<GedDocument> docs = documentDao.findAll();
		if (docs != null) {
			boolean showAllDocument = false;
			try {
				showAllDocument = Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_SHOW_ALL_DOCUMENT));
				getLogger().info("The property " + VanillaConfiguration.P_GED_SHOW_ALL_DOCUMENT + " is set to " + showAllDocument);
			} catch (Exception e) {
				getLogger().info("No property " + VanillaConfiguration.P_GED_SHOW_ALL_DOCUMENT + " in the vanilla.properties file. We put the value to false.");
				showAllDocument = false;
			}

			List<Integer> autorized = securityDao.findAuthorizedDocumentIds(groupIds, repositoryId);

			List<GedDocument> res = new ArrayList<GedDocument>();

			for (GedDocument d : docs) {
				boolean granted = false;
				for (Integer i : autorized) {
					if (i != null && i.intValue() == d.getId()) {
						granted = true;
						break;
					}
				}

				if (showAllDocument) {
					if (granted) {
						getLogger().debug("Ged DocDef with id = " + d.getId() + " has been included, allowed by security");
					}
					else {
						getLogger().debug("Ged DocDef with id = " + d.getId() + " has been included, however the document is not granted due to security restrictions");
					}

					List<DocumentVersion> versions = documentVersionDAO.getDocumentVersionForDocument(d);
					d.setDocumentVersions(versions);
					d.setGranted(granted);
					res.add(d);
				}
				else {
					if (granted) {
						getLogger().debug("Ged DocDef with id = " + d.getId() + " has been included, allowed by security");

						List<DocumentVersion> versions = documentVersionDAO.getDocumentVersionForDocument(d);
						d.setDocumentVersions(versions);
						d.setGranted(granted);
						res.add(d);
					}
					else {
						getLogger().debug("Ged DocDef with id = " + d.getId() + " has been filtered out due to security restrictions");
					}
				}
			}
		}

		return addCategoryToDocs(docs);
	}

	@Override
	public void updateVersion(DocumentVersion version) throws Exception {
		documentVersionDAO.updateVersion(version);
	}

	@Override
	public DocumentVersion comeBackToVersion(GedDocument doc, DocumentVersion oldVersion, Group group, Repository repository, User user) throws Exception {

		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);

		if (rootDir != null && !rootDir.endsWith("/")) {
			rootDir = rootDir + "/";
		}

		String oldPath = rootDir + oldVersion.getDocumentPath();
		oldPath = oldPath.replace("/\\", "/");

		getLogger().info("Trying to retrive file : " + oldPath);

		File oldFile = new File(oldPath);
		if (oldFile.exists()) {
			FileInputStream fis = new FileInputStream(oldFile);

			DocumentVersion version = new DocumentVersion();
			version.setModificationDate(new Date());
			version.setModifiedBy(doc.getCreatedBy());
			version.setDocumentId(doc.getId());

			int maxVersion = 0;
			for (DocumentVersion v : doc.getDocumentVersions()) {
				if (v.getVersion() > maxVersion) {
					maxVersion = v.getVersion();
				}
			}
			version.setVersion(maxVersion + 1);

			String newPath = "ged_documents" + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/" + Calendar.getInstance().get(Calendar.MONTH) + "/";
			newPath = newPath.replace("/\\", "/");
			File newFile = new File(rootDir + "/" + newPath);
			if (!newFile.exists()) {
				newFile.mkdir();
			}

			newPath += doc.getId() + "_" + doc.getName() + "_V" + version.getVersion() + "." + oldVersion.getFormat();

			FileOutputStream fos = new FileOutputStream(new File(rootDir + "/" + newPath));
			byte buffer[] = new byte[512 * 1024];
			int nbLecture;

			while ((nbLecture = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, nbLecture);

			}
			fis.close();
			fos.close();

			version.setDocumentPath(newPath);
			// documentVersionDAO.addVersion(version);

			// index the file

			IComProperties comProps = new ComProperties();

			GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(comProps, user.getId(), group.getId(), new ArrayList<Integer>(), repository.getId(), oldVersion.getFormat(), oldVersion.getDocumentId(), version.getId());
			index(config, new FileInputStream(new File(rootDir + "/" + newPath)));

			return version;
		}
		else {
			throw new FileNotFoundException("The file " + oldPath + " does not exist");
		}

	}

	@Override
	public void deleteVersion(DocumentVersion version) throws Exception {
		documentVersionDAO.delete(version);
	}
}
