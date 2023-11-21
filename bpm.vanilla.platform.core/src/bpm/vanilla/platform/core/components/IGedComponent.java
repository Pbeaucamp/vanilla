package bpm.vanilla.platform.core.components;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.ged.Category;
import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.Security;
import bpm.vanilla.platform.core.components.ged.GedAdvancedSearchRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedSearchRuntimeConfig;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

/**
 * Ged component
 * Used for GED and Index, for the historic see "ReportHistoricComponent"
 * 
 * @author manu
 *
 */
public interface IGedComponent {
	
	public static final String PARAM_ACTION = "bpm.ged.param.action";
	public static final String PARAM_DOC_ID = "bpm.ged.param.docId";
	public static final String PARAM_FORMAT = "bpm.ged.param.format";
	public static final String PARAM_DOCUMENT_NAME = "bpm.ged.param.documentName";
	public static final String PARAM_USER_ID = "bpm.ged.param.userId";
	public static final String PARAM_GROUP_IDS = "bpm.ged.param.groupIds";
	public static final String PARAM_REPOSITORY_ID = "bpm.ged.param.repositoryId";
	
	public static enum ActionType implements IXmlActionType{
		SEARCH(Level.DEBUG), GET_DOCUMENT_VERSION_BY_ID(Level.DEBUG),
		GET_FIELD_DEFINITIONS(Level.DEBUG), ADD_FIELD_DEFINITION(Level.DEBUG), DELETE_FIELD_DEFINITION(Level.DEBUG), UPDATE_FIELD_DEFINITION(Level.DEBUG),
		GET_CATEGORIES(Level.DEBUG), ADD_CATEGORY(Level.INFO), UPDATE_CATEGORY(Level.INFO), DELETE_CATEGORY(Level.INFO),
		GET_DOCS_FOR_CAT(Level.DEBUG), ADD_DOC_TO_CAT(Level.INFO), DELETE_DOC_TO_CAT(Level.INFO),
		ADD_ACCESS(Level.INFO), DELETE_ACCESS(Level.INFO), LOAD_DOCUMENT(Level.DEBUG), INDEX(Level.INFO),
		GET_DOC(Level.DEBUG), GET_ALL_DOCS(Level.DEBUG), GET_DOC_BY_GROUP(Level.DEBUG), GET_WAITING_DOCS(Level.DEBUG), ADD_GED_DOC(Level.INFO), 
		DEL_GED_DOC(Level.INFO), INDEX_EXISTING(Level.INFO), RESET_GED_INDEX(Level.INFO), REBUILD_INDEX(Level.INFO), 
		ADVANCED_SEARCH(Level.DEBUG), GET_SECU_BY_DOC(Level.DEBUG), UPDATE_SECU(Level.INFO), ADD_VERSION(Level.INFO), CHECKOUT(Level.INFO), 
		CHECKIN(Level.INFO), CAN_CHECKOUT(Level.DEBUG), CAN_CHECKIN(Level.DEBUG), UPDATE_VERSION(Level.INFO), COME_BACK_TO_VERSION(Level.INFO), 
		DELETE_VERSION(Level.INFO), CREATE_GED_DOCUMENT(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	/**
	 * Add access
	 * 
	 * @param docId
	 * @param groupId
	 * @param repId
	 * @throws Exception 
	 */
	public void addAccess(int docId, int groupId, int repId) throws Exception;
	
	/**
	 * Remove access
	 * 
	 * @param docId
	 * @param groupId
	 * @param repId
	 */
	public void removeAccess(int docId, int groupId, int repId) throws Exception;
	
	/**
	 * Main purpose of ged, being able to search....
	 * 
	 * @param config
	 * @return
	 */
	public List<GedDocument> search(GedSearchRuntimeConfig config) throws Exception;
	
	/**
	 * Returns a list of categories available in ged.
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Category> getGedCategories() throws Exception;
	
	/**
	 * Add a ged Category
	 * 
	 * @param category
	 * @throws Exception
	 */
	public void addCategory(Category category) throws Exception;
	
	/**
	 * Update a ged Category
	 * 
	 * @param category
	 * @throws Exception
	 */
	public void updateCategory(Category category) throws Exception;
	
	/**
	 * Delete a ged Category
	 * 
	 * @param category
	 * @throws Exception
	 */
	public void deleteCategory(Category category) throws Exception;
	
	/**
	 * Add a category on a document
	 * 
	 * @param docId
	 * @param catId
	 * @throws Exception
	 */
	public void addDocumentCategory(int docId, int catId) throws Exception;
	
	/**
	 * Remove a category for a document
	 * 
	 * @param docId
	 * @param catId
	 * @throws Exception
	 */
	public void deleteDocumentCategory(int docId, int catId) throws Exception;
	
	/**
	 * Add a fieldDefinition
	 * 
	 * @param definition
	 * @throws Exception
	 */
	public void addFieldDefinition(Definition definition) throws Exception;
	
	/**
	 * Update a fieldDefinition
	 * It didn't exist before so be careful with that. I don't the reaction of the index after that.
	 * 
	 * @param definition
	 * @throws Exception
	 */
	public void updateFieldDefinition(Definition definition) throws Exception;
	
	/**
	 * Delete a fieldDefinition
	 * 
	 * @param definition
	 * @throws Exception
	 */
	public void deleteFieldDefinition(Definition definition) throws Exception;
	
	/**
	 * Returns the list of field definitions used in the ged
	 *  
	 * @param includeCustom include custom fields or only return "system" ones
	 * @return
	 * @throws Exception 
	 */
	public List<Definition> getFieldDefinitions(boolean includeCustom) throws Exception;
	
	/**
	 * Create and index a file and only that ! It doesn't create an historic. You need to do that before 
	 * 
	 * @param config
	 * @param inputstream
	 * @return String the result of the operation, 'ok' or error something 
	 * @throws Exception
	 */
	public int index(GedIndexRuntimeConfig config, InputStream is) throws Exception;
	
	/**
	 * Return the document as InputStream
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public InputStream loadGedDocument(GedLoadRuntimeConfig config) throws Exception;

	public GedDocument getDocumentDefinitionById(int itemId) throws Exception;

	public DocumentVersion getDocumentVersionById(int versionId) throws Exception;

	public List<GedDocument> getAllDocuments() throws Exception;
	
	/**
	 * This method brings all the documents availables for groups with id include in groupIds
	 * 
	 * @param groupIds
	 * @param repositoryId
	 * @return list of documents
	 * @throws Exception
	 */
	public List<GedDocument> getDocuments(List<Integer> groupIds, int repositoryId) throws Exception;
	
	/**
	 * Used to get all documents waiting for indexation.
	 * These replace the incoherent "PendingDocument" class.
	 * @return
	 * @throws Exception
	 */
	public List<GedDocument> getDocumentsToIndex() throws Exception;
	
	public void indexExistingFile(HistoricRuntimeConfiguration config, int docId, int version, boolean createEntry) throws Exception;
	
	public int addGedDocument(GedDocument doc, InputStream docInputStream, String format) throws Exception;
	
	public void deleteGedDocument(int docId) throws Exception;
	
	public List<GedDocument> getDocumentsByCategory(int catId) throws Exception;
	
	/**
	 * This method reset the ged index
	 * Be careful if you use this, there is no turning back.
	 * 
	 * @throws Exception
	 */
	public void resetGedIndex() throws Exception;
	
	/**
	 * This is rebuilding the index entirely.
	 * If the rebuild fail, the old index is kept
	 * @return true if succeed
	 * @throws Exception
	 */
	public boolean rebuildGedIndex() throws Exception;

	public List<GedDocument> advancedSearch(GedAdvancedSearchRuntimeConfig config) throws Exception;
	
	public List<Security> getSecuForDocument(GedDocument document) throws Exception;

	public void updateSecurity(GedDocument document, Group group, boolean authorized, int repositoryId) throws Exception;

	public GedDocument createDocument(String documentName, String format, int userId, List<Integer> groupIds, int repositoryId, InputStream docInputStream) throws Exception;
	public GedDocument createDocumentThroughServlet(String documentName, String format, int userId, List<Integer> groupIds, int repositoryId, InputStream fileInputStream) throws Exception;

	public DocumentVersion addVersionToDocument(GedDocument doc, String format, InputStream fileInputStream) throws Exception;
	public DocumentVersion addVersionToDocumentThroughServlet(int docId, String format, InputStream fileInputStream) throws Exception;

	/**
	 * 
	 * @param id the version id
	 * @throws Exception 
	 */
	public void indexExistingFile(HistoricRuntimeConfiguration config, Integer id, boolean createEntry) throws Exception;

	/**
	 * This method is used to determine if a specified document can be checkout by the specified user
	 * 
	 * @param documentId
	 * @param userId
	 * @return if the user can checkout
	 * @throws Exception
	 */
	public boolean canCheckout(int documentId) throws Exception;
	
	/**
	 * This method is used to checkout a Ged Document
	 * When a ged document is checkout, it can be modified until it is checkin
	 * 
	 * @param documentId
	 * @param userId
	 * @throws Exception
	 */
	public void checkout(int documentId, int userId) throws Exception;

	/**
	 * This method is used to determine if a specified document can be checkin by the specified user
	 * 
	 * @param documentId
	 * @param userId
	 * @return if the user can checkin
	 * @throws Exception
	 */
	public boolean canCheckin(int documentId, int userId) throws Exception;
	
	/**
	 * This methed is used to checkin a Ged Document
	 * When a ged document is checkin, it can be checkout again
	 * 
	 * @param documentId
	 * @param userId
	 * @param format
	 * @param is (the input stream of the new version)
	 * @param true if we need to index the document version
	 * @throws Exception
	 */
	public void checkin(int documentId, int userId, String format, InputStream is, boolean index) throws Exception;

	/**
	 * Update a document
	 * Used for updating the peremption date of the document
	 * @param version
	 * @throws Exception 
	 */
	public void updateVersion(DocumentVersion version) throws Exception;

	/**
	 * 
	 * This method is used to create a new version of the document with the selected one
	 * It allows the user to come back to a previous version of the document but it keeps the last one also
	 * @param user 
	 * @param repository 
	 * @param group 
	 * 
	 * 
	 * @param the document to update
	 * @param the old version that we want to be the new one
	 * @return the new version of the document
	 * @throws Exception 
	 */
	public DocumentVersion comeBackToVersion(GedDocument doc, DocumentVersion oldVersion, Group group, Repository repository, User user) throws Exception;

	public void deleteVersion(DocumentVersion version) throws Exception;
}
