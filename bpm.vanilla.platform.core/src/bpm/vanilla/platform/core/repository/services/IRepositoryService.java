package bpm.vanilla.platform.core.repository.services;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.validation.UserValidation.UserValidationType;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.beans.validation.ValidationCircuit;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.Template;
import bpm.vanilla.platform.core.repository.Template.TypeTemplate;
import bpm.vanilla.platform.core.utils.ImpactLevel;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IRepositoryService {
	
	public static final int ONLY_ITEM = 0;
	public static final int ONLY_DIRECTORY = 1;
	public static final int BOTH = 2;

	public static enum ActionType implements IXmlActionType {
		CREATE_DIRECTORY(Level.INFO), CREATE_ITEM(Level.INFO), DELETE_DIRECTORY(Level.INFO), DELETE_ITEM(Level.INFO), LIST_CUBE_NAMES(Level.DEBUG), 
		LIST_CUBE_VIEWS(Level.DEBUG), DEPENDANT_ITEMS(Level.DEBUG), REQUESTED_ITEMS(Level.DEBUG), FIND_DIRECTORY(Level.DEBUG),  LIST_LINKED_DOCUMENT(Level.DEBUG), 
		FIND_DIRECTORY_ITEM(Level.DEBUG), ITEM_PARAMETERS(Level.DEBUG), DIRECTORY_CONTENT(Level.DEBUG), IMPORT_MODEL(Level.DEBUG), UPDATE_DIRECTORY(Level.INFO), 
		UPDATE_MODEL_DEFINITION(Level.INFO), CREATE_EXT_DOC_ITEM(Level.INFO), CHECK_ITEM_UPATE(Level.INFO), LIST_CUBE_VIEWS_WITH_IMAGES(Level.DEBUG), 
		LIST_FMDT_DRILLER(Level.DEBUG), CREATE_DISCONNECTED_PACKAGE(Level.INFO), GET_IMPACT_GRAPH(Level.DEBUG), SEARCH_ITEMS(Level.DEBUG), 
		ADD_OR_UPDATE_VALIDATION(Level.INFO), ADD_OR_UPDATE_COMMENT_DEFINITION(Level.INFO), ADD_OR_UPDATE_COMMENT_VALUE(Level.INFO), 
		DELETE_COMMENT_DEFINITION(Level.INFO), DELETE_COMMENT_VALUE(Level.INFO), GET_VALIDATIONS(Level.DEBUG), GET_VALIDATION(Level.DEBUG), GET_VALIDATION_BY_START_ETL(Level.DEBUG),
		GET_COMMENT_DEFINITION(Level.DEBUG), GET_COMMENTS_DEFINITION(Level.DEBUG), GET_COMMENT_NOT_VALIDATE(Level.DEBUG), GET_COMMENTS(Level.DEBUG), 
		GET_COMMENTS_FOR_USER(Level.DEBUG), ADD_SHARED_FILE(Level.INFO), GET_ITEMS(Level.DEBUG), 
		GET_TEMPLATES(Level.DEBUG), GET_TEMPLATE(Level.DEBUG), ADD_TEMPLATE(Level.DEBUG), DELETE_TEMPLATE(Level.DEBUG),
		ADD_ITEM_METADATA_LINK(Level.DEBUG), DELETE_ITEM_METADATA_LINK(Level.DEBUG), GET_METADATA_LINKS(Level.DEBUG),
		GET_PENDING_ITEMS_TO_COMMENT(Level.DEBUG), GET_VALIDATION_CIRCUITS(Level.DEBUG), MANAGE_VALIDATION_CIRCUIT(Level.DEBUG), UPDATE_NEXT_USER_VALIDATION(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	public RepositoryDirectory addDirectory(String name, String comment, RepositoryDirectory parent) throws Exception;

	public RepositoryItem addDirectoryItemWithDisplay(int type, int subType, RepositoryDirectory target, String name, String comment, String internalVersion, String publicVersion, String xml, boolean display) throws Exception;

	public RepositoryItem addExternalDocumentWithDisplay(RepositoryDirectory target, String name, String comment, String internalVersion, String publicVersion, InputStream extDocStream, boolean display, String format) throws Exception;

	public void delete(RepositoryDirectory directory) throws Exception;

	public void delete(RepositoryItem directoryItem) throws Exception;

	public List<String> getCubeNames(RepositoryItem fasdItem) throws Exception;

	public List<RepositoryItem> getCubeViews(String cubeName, RepositoryItem fasdItem) throws Exception;
	
	public List<RepositoryItem> getFmdtDrillers(RepositoryItem fmdtItem) throws Exception;

	public HashMap<RepositoryItem, byte[]> getCubeViewsWithImageBytes(String cube, RepositoryItem fasdItem) throws Exception;

	public List<RepositoryItem> getDependantItems(RepositoryItem directoryItem) throws Exception;

	public RepositoryDirectory getDirectory(int id) throws Exception;

	public RepositoryItem getDirectoryItem(int directoryItemId) throws Exception;

	public List<LinkedDocument> getLinkedDocumentsForGroup(int directoryItemId, int groupId) throws Exception;

	public List<RepositoryItem> getNeededItems(int directoryItemId) throws Exception;

	public List<Parameter> getParameters(RepositoryItem item) throws Exception;

	/**
	 * return the content of parent, parent may be null, in this case the root
	 * diretories are returned
	 * 
	 * TYPE can be:
	 *   IRepositoryService.ONLY_ITEM
	 *   IRepositoryService.ONLY_DIRECTORY
	 *   IRepositoryService.BOTH
	 */
	public List<IRepositoryObject> getDirectoryContent(RepositoryDirectory parent, int type) throws Exception;
	
	public String loadModel(RepositoryItem repItem) throws Exception;

	public void update(RepositoryDirectory directory) throws Exception;

	public void updateModel(RepositoryItem item, String xml) throws Exception;

	/**
	 * check if an item has been updated from a date
	 * 
	 * @param item
	 *            : item to check
	 * @param date
	 *            : the date that will be used for comparison(compared with the
	 *            last modification date of the item on the repository database)
	 * @return : true if the Item has been updated false otherwise
	 * @throws Exception
	 */
	public boolean checkItemUpdate(RepositoryItem item, Date date) throws Exception;

	public RepositoryItem addExternalUrl(RepositoryDirectory target, String name, String comment, String internalVersion, String publicVersion, String url) throws Exception;

	public String loadUrl(RepositoryItem item) throws Exception;
	
	public byte[] createDisconnectedPackage(String projectName, int limitRows, List<RepositoryItem> items) throws Exception; 
	
	public List<ImpactLevel> getImpactGraph(int itemId) throws Exception;

	public List<RepositoryItem> getItems(String search) throws Exception;
	
	public Validation addOrUpdateValidation(Validation validation) throws Exception;
	
	public CommentDefinition addOrUpdateCommentDefinition(CommentDefinition commentDefinition) throws Exception;
	
	public CommentValue addOrUpdateCommentValue(CommentValue comment) throws Exception;
	
	public void deleteCommentDefinition(CommentDefinition comment) throws Exception;
	
	public void deleteCommentValue(CommentValue comment) throws Exception;

	public List<Validation> getValidations(boolean includeInactive) throws Exception;

	public Validation getValidation(int itemId) throws Exception;

	public List<Validation> getValidationByStartEtl(int itemId) throws Exception;

	public CommentDefinition getCommentDefinition(int itemId, String commentName) throws Exception;

	public List<CommentDefinition> getCommentDefinitions(int itemId) throws Exception;

	public CommentValue getCommentNotValidate(int commentDefinitionId) throws Exception;

	public List<CommentValue> getComments(int itemId, String commentName, List<CommentParameter> parameters) throws Exception;

	public List<CommentValue> getComments(int commentDefinitionId, int repId, int userId) throws Exception;

	public void addSharedFile(String fileName, InputStream sharedFileStream) throws Exception;

	public List<RepositoryItem> getItems(List<Integer> ids) throws Exception;
	
	public <T> List<Template<T>> getTemplates(boolean lightWeight, TypeTemplate type) throws Exception;
	
	public <T> Template<T> getTemplate(int templateId) throws Exception;
	
	public void addTemplate(Template<?> template) throws Exception;
	
	public void deleteTemplate(Template<?> template) throws Exception;
	
	public void addItemMetadataTableLink(ItemMetadataTableLink link) throws Exception;

	public void deleteItemMetadataTableLink(ItemMetadataTableLink link) throws Exception;

	public List<ItemMetadataTableLink> getMetadataLinks(int itemId) throws Exception;

	public List<RepositoryItem> getPendingItemsToComment(int userId) throws Exception;
	
	public List<ValidationCircuit> getValidationCircuits() throws Exception;
	
	public ValidationCircuit manageValidationCircuit(ValidationCircuit circuit, ManageAction action) throws Exception;

	void updateUserValidation(int validationId, int oldUserId, int newUserId, UserValidationType type) throws Exception;
}
