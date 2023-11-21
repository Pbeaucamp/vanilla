package bpm.es.gedmanager.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.norparena.ui.menu.Activator;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ged.Category;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.IComProperties;
import bpm.vanilla.platform.core.beans.ged.Security;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedSearchRuntimeConfig;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;

public class GedModel {

	private IGedComponent gedComponent;
	private HashMap<Category, List<GedDocument>> categoriesDocuments = new HashMap<Category, List<GedDocument>>();
	private IRepository repository;

	public GedModel() {
		gedComponent = new RemoteGedComponent(Activator.getDefault().getRepositoryContext().getVanillaContext());
	}

	public void search(Definition fieldDef, String value) throws Exception {
		IComProperties props = new ComProperties();
		props.setProperty(fieldDef, value);

		List<String> keywords = new ArrayList<String>();
		keywords.add(value);

		GedSearchRuntimeConfig config = new GedSearchRuntimeConfig();
		config.setAllOccurences(true);
		config.setKeywords(keywords);
		config.setProperties(props);
		config.setRepositoryId(Activator.getDefault().getSocket().getContext().getRepository().getId());

		documents = gedComponent.search(config);
	}

	public IRepository getRepository() throws Exception {
		if (repository == null) {
			this.repository = new Repository(Activator.getDefault().getSocket());
		}

		return repository;
	}

	public List<Category> getCategories() throws Exception {

		List<Category> cats = new ArrayList<Category>(gedComponent.getGedCategories());
		return cats;
	}

	public List<Definition> getFieldDefinitions() throws Exception {
		return gedComponent.getFieldDefinitions(true);
	}

	private List<GedDocument> documents;

	public List<GedDocument> getDocuments() throws Exception {
		if (documents == null) {
			documents = gedComponent.getAllDocuments();
		}

		return documents;
	}

	public List<GedDocument> getPendingDocuments() throws Exception {

		return gedComponent.getDocumentsToIndex();
	}

	public List<GedDocument> getDocumentsForCategory(Category category) throws Exception {

		if (categoriesDocuments.get(category) != null) {
			return categoriesDocuments.get(category);
		}
		List<GedDocument> defs = gedComponent.getDocumentsByCategory(category.getId());
		categoriesDocuments.put(category, defs);

		return defs;
	}

	public void indexPendingDocument(GedDocument doc, IComProperties props) throws Exception {
		// gedComponent.indexExistingFile(doc.getId());
	}

	public void deleteDocument(GedDocument doc) throws Exception {
		gedComponent.deleteGedDocument(doc.getId());
	}

	public void deletePendingDocument(GedDocument doc) throws Exception {
		gedComponent.deleteGedDocument(doc.getId());
	}

	public void addCategory(Category category) throws Exception {
		gedComponent.addCategory(category);
	}

	public void updateCategory(Category category) throws Exception {
		gedComponent.updateCategory(category);
	}

	public void deleteCategory(Category category) throws Exception {
		gedComponent.deleteCategory(category);
	}

	public void addDocCat(Category category, GedDocument document) throws Exception {
		gedComponent.addDocumentCategory(document.getId(), category.getId());
	}

	public void delDocCat(int docId, int catId) throws Exception {
		gedComponent.deleteDocumentCategory(docId, catId);
	}

	public void addDefinition(String name) throws Exception {
		Definition def = new Definition();
		def.setAnalyzed("0");
		def.setBoostLvl("1");
		def.setCustom("1");
		def.setMultiple("0");
		def.setName(name);
		def.setRequired("0");
		def.setStored("0");
		def.setSystem("0");
		gedComponent.addFieldDefinition(def);

	}

	public void deleteDefinition(Definition def) throws Exception {
		gedComponent.deleteFieldDefinition(def);

	}

	public boolean resetIndex(boolean rebuild) throws Exception {
		if (rebuild) {
			return gedComponent.rebuildGedIndex();
		}
		else {
			gedComponent.resetGedIndex();
			return true;
		}
	}

	public void refresh() throws Exception {
		documents = gedComponent.getAllDocuments();
	}

	public GedDocument createNewDocument(String name, String file/* , int dirId */) throws Exception {

		GedDocument doc = new GedDocument();

		doc.setDirectoryId(0);
		doc.setName(name);
		doc.setCreationDate(new Date());
		int userId = Activator.getDefault().getRemote().getVanillaSecurityManager().getUserByLogin(Activator.getDefault().getRepositoryContext().getVanillaContext().getLogin()).getId();
		doc.setCreatedBy(userId);

		FileInputStream in = new FileInputStream(file);

		String format = file.substring(file.lastIndexOf(".") + 1, file.length());

		int id = gedComponent.addGedDocument(doc, in, format);
		doc.setId(id);

		return doc;
	}

	public List<Integer> getSecurityForDocument(GedDocument document) throws Exception {
		List<Integer> groupIds = new ArrayList<Integer>();

		List<Security> secus = gedComponent.getSecuForDocument(document);

		for (Security secu : secus) {
			groupIds.add(secu.getGroupId());
		}

		return groupIds;
	}

	public void updateSecurity(GedDocument document, Group group, boolean authorized) throws Exception {
		gedComponent.updateSecurity(document, group, authorized, Activator.getDefault().getSocket().getContext().getRepository().getId());
	}

	public void indexVersion(DocumentVersion version) throws Exception {
		gedComponent.indexExistingFile(null, version.getDocumentId(), version.getVersion(), false);
	}

	public void addVersion(GedDocument doc, String format, String file) {
		try {
			gedComponent.addVersionToDocument(doc, format, new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean canCheckin(int documentId, int userId) throws Exception {
		return gedComponent.canCheckin(documentId, userId);
	}

	public void checkin(int documentId, int userId, String format, InputStream is, boolean index) throws Exception {
		gedComponent.checkin(documentId, userId, format, is, index);
	}

	public boolean canCheckout(int documentId) throws Exception {
		return gedComponent.canCheckout(documentId);
	}

	public void checkout(int documentId, int userId) throws Exception {
		gedComponent.checkout(documentId, userId);
	}

	public InputStream loadGedDocument(GedLoadRuntimeConfig config) throws Exception {
		return gedComponent.loadGedDocument(config);
	}

	public void updateVersion(DocumentVersion version) throws Exception {
		gedComponent.updateVersion(version);

	}
}
