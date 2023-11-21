package bpm.document.management.core;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bpm.document.management.core.model.*;
import bpm.document.management.core.model.Comments.CommentStatus;
import bpm.document.management.core.model.Enterprise.TypeUser;
import bpm.document.management.core.model.Form.FormType;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.ItemValidation.ItemType;
import bpm.document.management.core.model.Log.LogType;
import bpm.document.management.core.model.MetadataLink.LinkType;
import bpm.document.management.core.model.OrganigramElementSecurity.Fonction;
import bpm.document.management.core.model.Permission.ShareType;
import bpm.document.management.core.model.aklad.AkladSettings;
import bpm.document.management.core.utils.MailConfig;
import bpm.document.management.core.utils.TreatmentImageObject;
import bpm.document.management.core.xstream.IXmlActionType;


public interface IVdmManager {

	public enum ActionType implements IXmlActionType {
		ADD_FEEDBACK,CONNECT, CREATE_USER, ADD_GROUP, GET_GROUP, GET_GROUP_USER, DELETE_GROUP, UPDATE_GROUP, UPDATE_USER, DELETE_USER, RESET_PASSWORD, GET_LOGS, GET_ITEMS, GET_ITEMS_NOT_DELETED_AND_ACTIVATED,
		DELETE_DIRECTORY,DELETE_ITEM, UPDATE_DIRECTORY,UPDATE_DIRECTORY_BY_ID, ADD_LOCALE, DELETE_LOCALE, UPDATE_LOCALE,
		GET_ROLES,ADD_ROLE,UPDATE_ROLE,DELETE_ROLE,GET_COUNTRY,VALIDATE_EMAIL,UPDATE_VALIDATED,GET_USER,GET_CITIES,GET_OTHERS, GET_DIRECTORY, GET_DIRECTORY_INFO, SAVE_DIRECTORY, SAVE_DIRECTORY_WITH_HIER,
		SAVE_DOCUMENTS,DOWNLOADS, SAVE_PAGES, GET_PAGES,GET_SUBDIRECTORIES,GET_SUB_DOCS,GET_DOC_INFO, INDEX_DOCUMENT, SEARCH_ITEMS, SEARCH_INDEXED_DOCUMENTS, DELETE_ITEM_FROM_TRASH, RESTORE_ITEM,
		GET_SUB_IOBJECTS, GET_DIRS_USER, DELETE_FOREVER_FOLDER,RESTORE_FOLDER,SAVE_FAV,REMOVE_FAV,GET_FAV_INFO,GET_FAV, 
		ADD_DOC_CODE, GET_DOC_CODE,GET_CURRENCY,GET_STORAGE,GET_STORAGE_USERS,GET_LIST_DOCS,GET_USER_SHARE,GET_GROUPS,GET_ALL_DOCUMENTS,
		SAVE_DOC_VERSION, DELETE_DOC_VERSION, UPDATE_DOC_VERSION, GET_VERSIONS, GET_LAST_VERSION, GET_SPECIFIC_VERSION,GET_USER_INFO,GET_COUNTRY_ID,GET_CITY_ID,
		GET_DIR_ID,GET_DOC_ID, DELETE_ALL,SAVE_COMMENT, REMOVE_COMMENT, GET_COMMENTS,UPDATE_COMMENT,
		SAVE_DEFAULT_MEMORY, UPDATE_MEMORY, GET_MEMORY_USAGE,SAVE_RATE, UPDATE_RATE, GET_RATE, GET_RATES_BY_USER,
		GET_NOTIFICATIONS, MARK_AS_READ, SAVE_NOTIFICATION,GET_ALL_NOTIFS,MARK_AS_UNREAD,GET_ALL_USERS,UPDATE_DOC_PARENT,
		ALL_USERS,
		SAVE_WORKFLOW, SAVE_FOLDER_WORKFLOW,UPDATE_WORKFLOW, UPDATE_FOLDER_WORKFLOW, UPDATE_DOC_WORKFLOW, SAVE_DOC_WORKFLOW, GET_WORKFLOW, 
		GET_SPECIFIC_WORKFLOW, GET_SPECIFIC_WORKFLOW_BY_DOCID,GET_ALL_WORKFLOW,GET_WORKFLOW_FOLDER_CONDITION, GET_WORKFLOW_DOC_CONDITION,DELETE_WORKFLOW,GET_ENTERPRISES,
		SAVE_ENTERPRISE,DELETE_ENTERPRISE, UPDATE_ENTERPRISE, UPDATE_ENTERPRISE_USERS, SAVE_TASK, GET_TASK, GET_TASK_BY_DOC, GET_TASK_GIVEN, DELETE_TASK,
		GET_USER_GROUP, GET_USER_BY_GROUP, UPDATE_USER_GROUP,SAVE_TASK_COMMENT, GET_TASK_COMMENT, UPDATE_TASK_COMMENT, DELETE_TASK_COMMENT,SAVE_KEYWORD_WORKFLOW, 
		UPDATE_KEYWORD_WORKFLOW, DELETE_KEYWORD_WORKFLOW, GET_ALL_KEYWORD_WORKFLOW,SAVE_KEYWORD_RESULT, DELETE_KEYWORD_RESULT, GET_ALL_KEYWORD_RESULT,SAVE_WORKFLOW_EVENT, 
		GET_ALL_WORKFLOW_EVENT,DELETE_WORKFLOW_EVENT,SAVE_CHAT_MESSAGE, DELETE_CHAT_MESSAGE, GET_ALL_CHAT_MESSAGE_BY_USER, UPDATE_CHAT_MESSAGE,SAVE_PRIVATE_DOCS,
		GET_GROUP_BY_ID,LOAD_ENTERPRISE_USER,GET_FOLDERS_ENTERPRISE,UPDATE_TASK, GET_ENTERPRISE_PARENT, GET_ENTERPRISE_ROOT_FOLDER,
		SAVE_MESSAGE, GET_MESSAGE, DELETE_MESSAGE, 
		UPDATE_MESSAGE, GET_ALL_MESSAGES,GET_MESSAGE_BY_ID,CHECK_LICENSE,SAVE_LICENSE,GET_LICENSE, DELETE_DOCPAGE,GET_TAGS,SAVE_TAG,DELETE_TAG,GET_TAGS_LIBRARY,SAVE_TAG_LIBRARY,
		DELETE_TAG_LIBRARY,UPDATE_TAG_LIBRARY,SEARCH_TAGS,SAVE_CHAT_BUDDY, DELETE_CHAT_BUDDY, GET_ALL_CHAT_BUDDIES,SAVE_USER_WARNING,GET_USER_WARNING,CHANGE_USER_WARNING,
		SAVE_CAMPAIGN, DELETE_CAMPAIGN, UPDATE_CAMPAIGN, GET_ALL_CAMPAIGNS, GET_ALL_CAMPAIGN_LOADER_BY_USER, UPDATE_CAMPAIGN_LOADER, GET_ALL_CAMPAIGN_LOADER_BY_CAMPAIGN,
		SAVE_CAMPAIGN_NOTES, GET_CAMPAIGN_NOTES,FILE_ENCRYPT,FILE_DECRYPT,EDIT_VERSION, 
		GET_LICENCE_KEY, UPLOAD_FROM_ANDROID, GET_USER_LOGS,GET_ALL_TASKS,GET_OVERALL_MESSAGES,GET_OVERALL_COMMENTS,SAVE_ANNOUNCEMENTS,GET_ANNOUNCEMENTS,UPDATE_ANNOUNCEMENTS,
		DELETE_ANNOUNCEMENTS,GET_DOWNLOADS,SAVE_COUNTRY,UPDATE_COUNTRY,DELETE_COUNTRY,GET_CITY,SAVE_CITY,UPDATE_CITY,DELETE_CITY,SAVE_COUNTRY_CITY,
		GET_ALL_CHAT_MESSAGE_BY_RECEIVER,SAVE_XAKL,DELETE_XAKL,SAVE_XAKL_DOCS,SAVE_XAKL_AKLABOX,DELETE_XAKL_AKLABOX,UPDATE_XAKL_DOCS,SEARCH_DOC_OF_XAKL,
		GET_XAKL_TREE,GET_XAKLFILES,GET_XAKLDOCS, GET_ALL_PUBLIC_FILES_BY_USER,SAVE_DOC_VIEWS,GET_DOC_VIEWS,SHARE_DOCUMENT,
		USER_LAST_LOGIN,GET_DRIVER_INFO,SAVE_USER_DRIVER,UPDATE_USER_DRIVER,

		SAVE_DISTRIBUTION, GET_ALL_DISTRIBUTION, PRE_SAVE_DISTRIBUTION, UPDATE_DISTRIBUTION, UPDATE_DISTRIBUTIONLOADER, DELETE_DISTRIBUTION_LOADER, GET_ALL_DISTRIBUTION_LOADER,
		SAVE_DISTRIBUTION_LOG, GET_DISTRIBUTION_RUN_LOGS, GET_DISTRIBUTION_RUN_LOGS_LOADERS, SAVE_DISTIRIBUTION_LOADER,SAVE_USERGROUP,
		LOAD_FILE, UPLOAD_FILE, UPLOAD_VERSION_FILE, CREATE_DIRECTORY, CREATE_SUB_DIRECTORY, GET_CHECKOUT_DOCS,
		SAVE_PAGE, GET_XAKLFILES_BY_ID,GET_FOLDER_CODE, SAVE_STATE,UPDATE_STATE,GET_STATE,DELETE_STATE,CONVERT_TO_PDF,
		SAVE_DETAILED_ADDRESS,UPDATE_DETAILED_ADDRESS,DELETE_DETAILED_ADDRESS,GET_DETAILED_ADDRESS,ADD_DETAILED_LEVEL,GET_DETAILED_LEVEL,SAVE_FILE_LOCATION,GET_PHYSICAL_LOCATIONS, AGGREGATE_DOCUMENT, DELETE_PHYSICAL_LOCATION,
		SAVE_OFFICE_NO,GET_OFFICE_NO,DELETE_LEVEL,UPDATE_LEVEL,DELETE_OFFICE,UPDATE_OFFICE,UPDATE_FILE_LOCATION,
		SEND_MAIL, SEND_MAILS, COPY_PASTE,  DELETE_DISTRIBUTION,
		INIT_VANILLA_CONNECTION, GET_USER_FROM_VANILLA, GET_CAMPAIGN_LOADER_BY_ID, SAVE_SEARCH_RESULT, DELETE_SEARCH_RESULT, GET_ALL_SEARCH_RESULT, GET_TAG_BY_USER,
		SAVE_PLATFORM, UPDATE_PLATFORM, GET_PLATFORM, SAVE_LOGIN_DETAIL, GET_ALL_LOGIN_DETAIL, UPDATE_LOGIN_DETAIL,
		SAVE_USER_ROLE, DELETE_USER_ROLE, GET_ALL_USER_ROLES,
		SAVE_DOC_NAME, UPDATE_DOC_NAME, GET_DOC_NAME_BY_FOLDER,
		SAVE_DOC_LOG, UPDATE_DOC_LOG, GET_ALL_DOC_LOGS,  DELETE_DOC_LOG,
		SAVE_USER_CONNECTION_PROPERTY, UPDATE_USER_CONNECTION_PROPERTY, GET_USER_CONNECTION_PROPERTY,GET_RM_FOLDER, GET_ALL_RM_FOLDERS_BY_USER, GET_ALL_RM_FIELDS_BY_FOLDER, SAVE_RM_DOC, GET_ALL_RM_DOC_BY_FOLDER, GET_RM_FIELD,
		CHECK_RM_SCHEDULE_DEADLINE, SAVE_LOGIN_ATTEMPT, GET_ALL_LOGIN_ATTEMPTS, GET_USER_LOGS_BY_USER, SAVE_USER_LOG, GET_DOC_DOWNLOADS, GET_FOLDER_DOWNLOADS, GET_TOP_MOST_DOWNLOADS, SAVE_LINKED_DOC, GET_LINKED_DOCUMENTS, SAVE_REFERENCE_DOC, GET_REFERENCE_DOC,
		GET_ALL_TYPES, GET_FIRST_TYPE,
		DELETE_COPY_DOC, GET_COPY_DOCUMENT, GET_CUSTOM_NAME_BY_FOLDER, GET_AUTOMATIC_CUSTOM_NAME, SAVE_LOV, UPDATE_LOV, GET_ALL_LOV, GET_DISTINCT_LOV, SAVE_FOLDER_STORAGE_LOCATION, GET_FOLDER_STORAGE_LOCATION, SAVE_ADDRESS_FIELD, GET_ALL_ADDRESS_FIELD, DELETE_ADDRESS_FIELD,
		SAVE_FORM, DELETE_FORM, UPDATE_FORM, GET_ALL_FORMS, SAVE_FORM_FIELD, UPDATE_FORM_FIELD, DELETE_FORM_FIELD, GET_ALL_FORM_FIELD, GET_ALL_LOV_BY_TABLE_CODE, SAVE_FOLDER_FORM, GET_FOLDER_FORM, GET_FOLDER_FORM_BY_USER, SAVE_FORM_FIELD_VALUE, GET_ALL_FORM_FIELD_VALUES, GET_FORM_FIELD, UPDATE_FORM_FIELD_VALUE,
		GET_FORM_FIELD_VALUE, GET_FORM_FIELD_VALUE_BY_DOC, SEARCH_ON_FORM_FIELD, SAVE_FILES_TYPES,
		ADD_REPLY, MODIFY_REPLY, DELETE_REPLY, GET_ALL_REPLIES,
		ADD_TASK, GET_ARCHIVING_DOC, GET_ALL_ARCHIVING, GET_ARCHIVING_BYID, SAVE_ARCHIVING, GET_ALL_MASSIMPORT, GET_MASSIMPORT_BYID, SAVE_MASSIMPORT, DELETE_ARCHIVING, DELETE_MASSIMPORT, GET_LOG_MASSIMPORT, SAVE_LOG_MASSIMPORT, UPDATE_LOG_MASSIMPORT, DELETE_LOG_MASSIMPORT, GET_ALL_CONNECTION, GET_CONNECTION_BYID, SAVE_CONNECTION, DELETE_CONNECTION,
		TEST_CONNECTION, GET_TABLESNAME, GET_COLUMNSNAME,
		GET_MAIL_ROOT_DIRECTORIES, DELETE_LOV,	
		SAVE_MAIL_DOCUMENT,
		GET_ORGANIGRAM, SAVE_ORGANIGRAM, GET_ORGANIGRAM_FOLDER_CONTENT,
		GET_MAIL_SEARCH_RESULT, SAVE_MAIL_SEARCH_RESULT, DELETE_MAIL_SEARCH_RESULT,

		SAVE_COMMENT_UPDATE_TASK, APPROVE_COMMENT, VALIDATE_TASK, CLOSE_TASK, REVIVE_TASK, SAVE_CONFIG, GET_CONFIG, RELANCE_TASK, GET_PREVIOUS_TASK, 
		ADD_LIST_VALUE, ADD_RESPONSE_DOC, DELETE_FILE_TYPE,GET_TASK_BY_ID, GET_ALL_DEPARTEMENTS_BY_COUNTRY, GET_CITIES_BY_DEP, SAVE_DEPARTEMENT, GET_ALL_DEPARTEMENT, GET_FORM, GET_ALL_DOC_ORGANIGRAM, GET_ALL_ALFRESCO_EXPORT, GET_ALFR_EXPORT_BY_ID, SAVE_ALFRESCO_EXPORT, DELETE_ALFRESCO_EXPORT, GET_RULE_ALIM, SAVE_RULE_ALIM, DELETE_RULE_ALIM, GET_TASKS_DOC,
		MUTATE_USER, MUTATE_SUSPEND, ATTRIB_SUSPEND, GET_SUSPENDED_TASKS, GET_HISTO_MUTATION,  GET_GIVEN_TASKS_DOC, GET_ALL_MAILS, GET_ALL_TASKSDOC, GET_TASK_FOR_DOC,
		ADD_THESAURUS, GET_THESAURUSES, DELETE_THESAURUS,
		GET_HIERARCHIES, UPDATE_HIERARCHY, DELETE_HIERARCHY,
		GET_LOGS_BY_DOCUMENT, UPDATE_DOCUMENT, ADD_DOCUMENT_ARCHIVE,
		GET_ALL_DOCUMENT_ARCHIVES, GET_DOCUMENT_ARCHIVES_BY_ARCHIVING, GET_DOCUMENT_ARCHIVES_BY_DOCUMENT, GET_XAKL_CONTAINING_DOC, GET_ENTERPRISE_BY_ID, 
		GET_ALL_MODEL_ENTERPRISE, SAVE_MODEL_ENTERPRISE, GET_ALL_MODEL_ENTERPRISE_BY_FOLDER, DELETE_MODEL_ENTERPRISE, GET_CONTENT_FILEPATH,

		SAVE_TYPE_TASK, GET_TYPE_TASK, ADRESS_MAIL_GROUP,
		SAVE_VALIDATION_STATUS, DELETE_VALIDATION_STATUS, GET_ALL_VALIDATION_STATUS, GET_VALIDATION_STATUS_BY_ID, GET_ALL_FORM_BY_TYPE, GET_ORIGINAL_OF_DOCUMENT, SAVE_DELEGATION, GET_ALL_DELEGATION_BY_DELEGATOR, REMOVE_DELEGATION, GET_ALL_DELEGATION_BY_DELEGATE, 
		GET_ALL_ACTUAL_NEWS_INFOS, REMOVE_INFORMATION_NEWS, SAVE_INFORMATION_NEWS, CREATE_SUBJECT_IDEA_BOX, CREATE_MESSAGE_IDEA_BOX, GET_MESSAGE_IDEA_BOX_BY_ID, 

		GET_ALL_SUBJECT_IDEA_BOX, GET_SUBJECT_IDEA_BOX_BY_ID, ADD_VOTE_TO_SUBJECT, GET_COMMENTS_TO_APPROVE, 
		GET_FORM_FIELD_VALUE_BY_DOC_ID, CREATE_BAR_CODE, PARSE_PDF_FORM, CREATE_SURVEY, GET_SURVEY, UPDATE_SUBJECT, 
		CREATE_VOTE, GET_RESULT_SURVEY, GET_ALL_NEWS_INFO, UPDATE_BACKGROUND_FOLDER, REMOVE_USER_SUCCESSOR, GET_USER_SUCCESSOR, SAVE_USER_SUCCESSOR, 
		GENERATE_SCAN_OBJECT_FROM_ZIP, GENERATE_SCAN_OBJECT_FROM_ZIP2, GENERATE_SCAN_OBJECT_FROM_FILES, TRANSFERT_GROUP, TRANSFERT_ENTERPRISE,
		ADD_ABSENCE,REMOVE_ABSENCE,GET_ABSENCES, TREAT_IMAGE, SAVE_SCAN_DOCUMENT_TYPE, UPDATE_SCAN_DOCUMENT_TYPE, GET_ALL_SCAN_DOCUMENT_TYPES,
		DELETE_SCAN_DOCUMENT_TYPE, LOAD_DOCUMENT_STREAM, SAVE_SCAN_KEYWORD, UPDATE_SCAN_KEYWORD, GET_ALL_SCAN_KEYWORDS_BY_DOC_TYPE,
		DELETE_SCAN_KEYWORD, GET_LOV, GET_METADATAS_BY_ITEM, GET_METADATAS, SAVE_METADATA_LINK, SAVE_METADATA_VALUES, GET_METADATA_VALUES, UPDATE_METADATA_VALUES,
		CREATE_WORD_FROM_PDF, MANAGE_ITEM_PERMISSION, GET_PERMISSIONS_BY_TYPE, GET_PERMISSIONITEM_BY_HASH,

		IS_FORM_COMPLETE, UPDATE_PAGE, SAVE_AKLAD_SETTINGS, GET_AKLAD_SETTING, GET_REQUEST_USER, PDF_TO_DOCS, ADD_ACTION_LOG, GET_ACTION_LOGS, GET_ENTERPRISE_FOLDER_CHILDS,
		SEARCH_USERS, GET_USER_ENTERPRISE_BY_TYPE, MANAGE_ITEM_VALIDATION, GET_ITEM_VALIDATIONS, HAS_FORMS, CHECK_IF_FORMS_ARE_COMPLETED, MANAGE_ITEM, GET_ITEM
		,SAVE_MAIL_SERVERS, GET_MAIL_SERVERS, GET_ALL_FORMS_BY_TYPE2, GET_ORBEON_SECTIONS, CREATE_ORBEON_DOC, GET_ORBEON_INSTANCE,
		GET_ORGA_ELEM, UPDATE_ORBEON_INSTANCE, GET_FILE_TYPE, LOAD_ORBEON_SERVLET, SEND_ORBEON_NOTIF,
		GET_ORBEON_DOCUMENT, DELETE_ORGA_ELEM, DELETE_ORGA_ELEM_SECU, SAVE_VERSION, UPDATE_FORM_STAMP_SECTION,
		GET_DIRECT_SUPERIORS, GET_DIR_NAME_PARENT, GET_FORM_INSTANCE, GET_TAGS_BY_POPULARITY, GET_DOCUMENTS_BY_USER, GET_DIRECTORY_BY_HIERARCHY_ITEM_AND_ROOT,
		GET_HIERARCHY, GET_COLUMNS_BY_REQUEST, GET_RESULT_BY_REQUEST, CREATE_METADATA_PDF, GET_FORM_VALUES_FROM_SOURCE, GET_USER_BY_SESSION
	}
	
	public Integer manageItem(Serializable item, ManagerAction action) throws Exception;
	
	public List<Message> getAllMessages(String owner) throws Exception;
	
	public Message getMessageById(int messageId) throws Exception;
	
	public void saveMessage(String sender, List<String> receiver, String message, Date date, String status, String subject, List<Documents> attachments, String whoFirst,boolean isCopyRequest) throws Exception; 
	
	public List<Message> getMessage(String status) throws Exception;
	
	public void deleteMessage(Message message) throws Exception;
	
	public void updateMessage(Message message) throws Exception;
	
	public void saveTaskComment(User user, String message, int docId, Date commentDate) throws Exception;
	
	public void deleteTaskComment(TasksComment tasksComment) throws Exception;
	
	public List<TasksComment> getTaskComment(int taskId) throws Exception;
	
	public void updateTaskComment(TasksComment tasksComment) throws Exception;

	public User connect(User user) throws Exception;

	public Boolean createUser(User user, String verifyUrl, boolean fromAndroid, String licenceFile, UserRole userRole, UserConnectionProperty userConnectionProperty) throws Exception;

	public void addGroup(Group group, List<User> users) throws Exception;

	public List<Group> getGroups() throws Exception;

	public List<Group> getGroupsByUser(String email) throws Exception;

	public void deleteGroup(Group group) throws Exception;

	public void updateGroup(Group group, List<User> users) throws Exception;

	public User updateUser(User user,String email) throws Exception;

	public Boolean deleteUser(User user) throws Exception;

	public Boolean resetPassword(User user, String url) throws Exception;

	public List<UserLogs> getLogs(User user) throws Exception;

	public Tree deleteDirectory(Tree tree,String email) throws Exception;

	public Tree updateDirectory(Tree folder) throws Exception;
	
	public Locale addLocale(Locale locale) throws Exception;

	public Locale deleteLocale(Locale locale) throws Exception;

	public Locale updateLocale(Locale locale) throws Exception;

//	public Tree getItem(Tree folder) throws Exception;
	
//	public List<Documents> getDocs(String email) throws Exception;

	public List<Roles> getRoles() throws Exception;

	public Roles addRole(Roles role) throws Exception;

	public Roles updateRole(Roles role) throws Exception;

	public Boolean deleteRole(Roles role) throws Exception;

	public List<Country> getCountry() throws Exception;

	public User validateUser(String verifierCode) throws Exception;

	public void updateValidated(User user) throws Exception;

	public User getUserInfo(String email) throws Exception;

	public List<City> getCities(Country country) throws Exception;
	
	public List<User> getOtherUsers(String email) throws Exception;
	
	public Tree saveDirectory(Tree tree) throws Exception;

//	public List<IObject> getContents(int id, String email) throws Exception;

	public Downloads downloadsFolders(Downloads downloads) throws Exception;
 
	public void savePages(List<DocPages> pages) throws Exception;

	public List<DocPages> getPages(Versions docVersion) throws Exception;

	public Documents getDocInfo(int docId) throws Exception;

	public void indexDocument(Documents doc, InputStream fileInputStream) throws Exception;

	public List<Documents> searchIndexedDocuments(String searchString) throws Exception;
	
	public void deleteDocument(Documents doc,String email) throws Exception;
	
	public void restoreDocument(Documents doc) throws Exception;
	
	public void deleteDocumentFromTrash(Documents doc) throws Exception;
	
	public void deleteForeverFolder(Tree folder) throws Exception;
	
	public void restoreFolder(Tree folder) throws Exception;	
	
	public void saveFav(Favorites fav) throws Exception; 
	
	public void removeFav(Favorites fav) throws Exception;
	
	public Favorites getFavInfo(IObject docId) throws Exception;
	
	public List<IObject> getFav(String email) throws Exception;
	
	public Documents addCode(Documents documents) throws Exception;
	
	public Documents getCode(String uniqueCode) throws Exception;
	
	public List<Currency> getCurrencies() throws Exception;

	public List<MemoryStorage> getMemoryStorage() throws Exception;
	
	public List<UserStorage> getUserStorage() throws Exception;
	
	public List<IObject> getUserShare(String email) throws Exception;
	
	public List<Group> getGroupsForUser(String email) throws Exception;
	
//	public List<IObject> getGroupShare(int groupId) throws Exception;
	
//	public List<IObject> getGroupSharePerUser(String email) throws Exception;
	
	public Versions saveDocVersion(Versions docVersion) throws Exception;

	public Versions getSpecificVersion(Versions version) throws Exception;
	
//	public Documents updateDocStatus(Documents doc) throws Exception;
	
	public List<Versions> getVersions(Documents doc) throws Exception;
	
	public Versions getLastVersion(int docId) throws Exception;
	
	public User getUserInfoThroughId(String userId) throws Exception;
	
	public Country getCountryById(int countryId) throws Exception;
	
	public City getCityById(int cityId) throws Exception;
	
//	public List<IObject> getPublicTree() throws Exception;

	public void deleteAllFromTrash(String email) throws Exception;
	
	public void saveComments(Comments comment) throws Exception;
	
	public void removeComments(Comments comment) throws Exception;
	
	public List<Comments> getComments(int docId, CommentStatus status) throws Exception;
	
	public Comments getSpecificCommment(int commentId) throws Exception;
	
	public void updateComment(Comments comment) throws Exception;
	
	public void saveDefaultMemory(DocumentMemoryUsage memory) throws Exception;
	
	public void updateMemory(DocumentMemoryUsage memory) throws Exception;
	
	public DocumentMemoryUsage getMemoryUsage(String user) throws Exception;
	
	public void saveRate(Rate rate)throws Exception;

	public void updateRate(Rate rate) throws Exception;
	
	public Rate getRate(String user , int docId) throws Exception;
	
	public List<Rate> getRateByUser(String user)throws Exception;
	
//	public List<IObject> getSharedByMe(String email) throws Exception;

	public List<Notifications> getNotifications(int userId) throws Exception;
	
//	public List<IObject>getSubShare(int id,String email) throws Exception;

	public List<Notifications> markAsRead(Notifications notification) throws Exception;
	
	public void saveNotification(Notifications notification) throws Exception;	

	public void changeDocumentParent(int itemId, IObject.ItemType type, int parentId) throws Exception;
	
//	public void editDocumentInfo(Documents doc, String email) throws Exception;
	
	public List<Notifications> getAllNotifs(String email) throws Exception;
	
	public List<Notifications> markAsUnread(Notifications notification) throws Exception;
	
	public List<User> getAllUsers() throws Exception;
	
//	public List<Tree> getFromLast(String email) throws Exception;
	
	public List<User> allUsers(String email) throws Exception;

	public WorkFlow saveWorkFlow(WorkFlow workFlow) throws Exception;
	
	public void deleteWorkFlow(WorkFlow workFlow) throws Exception;
	
	public void updateWorkFlow(WorkFlow workFlow) throws Exception;
	
	public void saveFolderWorkFlow(WorkFlowFolderCondition folderCondition) throws Exception;
	
	public void saveDocWorkFlow(WorkFlowDocumentCondition docCondition) throws Exception;
	
	public void updateFolderWorkFlow(WorkFlowFolderCondition folderCondition) throws Exception;
	
	public void updateDocWorkFlow(WorkFlowDocumentCondition docCondition) throws Exception;
	
	public List<WorkFlow> getWorkFlow(int userId) throws Exception;
	
	public WorkFlow getSpecificWorkFlowEachUser(int userId, int docId, boolean isDocument) throws Exception;
	
	public List<WorkFlow> getAllWorkFlow(int docId, boolean isDocument) throws Exception;
	
//	public WorkFlow getSpecificWorkFlow(int docId) throws Exception;
	
	public WorkFlowFolderCondition getFolderCondition(int workflowId) throws Exception;
	
	public WorkFlowDocumentCondition getDocCondition(int workflowId) throws Exception;
	
	public List<Enterprise> getEnterprises() throws Exception;
	
//	public void saveEnterpriseFolders(Tree folder, List<Enterprise> enterprises) throws Exception;
	
	public int saveEnterprise(Enterprise enterprise, List<User> users,String email, boolean isFromAdmin, List<FolderHierarchy> hierarchy) throws Exception;
	
	public boolean deleteEnterprise(Enterprise enterprise) throws Exception;
	
	public void updateEnterprise(Enterprise enterprise, List<User> users) throws Exception;
	
	public void updateEnterpriseUsers(int enterpriseId, List<User> users, TypeUser typeUser) throws Exception;
	
	public void saveTask(Set<User> setUsers, Documents doc, String taskTitle, String taskTitleDesc, Date taskDueDate, String email, String taskStatus, String groupTree, int taskStatusType) throws Exception;
	
	public List<Tasks> getTask (String email) throws Exception;
	
	public List<Tasks> getTaskByDoc(Documents doc) throws Exception;
	
	public List<Tasks> getTaskGiven (String email) throws Exception;

	public void deleteTask(Tasks task) throws Exception;
	
//	public List<UserEnterprise> getUserEnterprise(Enterprise enterprise) throws Exception;
	
	public List<UserGroup> getUserGroups(Group group) throws Exception;
	
	public List<User> getUsersByGroup(List<Group> groups, Integer enterpriseId) throws Exception;
	
	public void updateUserGroups(String email, List<Group> groups) throws Exception;
	
//	public List<Tree> getEnterpriseFolders() throws Exception;
	
	public Tree saveDirectory(Tree folder, List<Integer> hierarchies) throws Exception;
	
	public Documents savePrivateDocs(Documents doc) throws Exception;
	
	public Group getGrouById(int groupId) throws Exception;

	public void saveKeywordWorkFlow(Keywords keyword) throws Exception;
	
	public void updateKeywordWorkFlow(Keywords keyword) throws Exception;
	
	public void deleteKeywordWowkFlow(Keywords keyword) throws Exception;
	
	public List<Keywords> getAllKeywordsPerWorkFlow(WorkFlow workflow) throws Exception;
	
	public List<Enterprise> getEnterprisePerUser(String email) throws Exception;
	
	public List<Tree> getFilesPerEnterprise(int enterpriseId, boolean lightWeight) throws Exception;
	
	public void saveWorkFlowEvent(WorkFlowEvents event) throws Exception;
	
	public List<WorkFlowEvents> getAllWorkFlowEvents(int receiverId) throws Exception;
	
	public void deleteWorkFlowEvent(WorkFlowEvents event) throws Exception;
	
	public void saveKeywordResults(KeywordResults result) throws Exception;
	
	public void deleteKeywordResults(KeywordResults result) throws Exception;                            

	public List<KeywordResults> getAllKeywordResults(int keywordId) throws Exception;
	
	public void saveChatMessage(ChatMessage message) throws Exception;
	
	public void deleteChatMessage(ChatMessage message) throws Exception;
	
	public List<ChatMessage> getAllChatMessageByUser(String email) throws Exception;
	
	public List<ChatMessage> getAllChatMessageByReceiver(String receiver) throws Exception;
	
	public void updateTask(Tasks tasks) throws Exception;
	
	public Enterprise getEnterpriseParent(int itemId, bpm.document.management.core.model.IObject.ItemType type) throws Exception;
	
	public void updateChatMessage(ChatMessage m)throws Exception;

//	public List<User> getAllUsersSharedPerDocument(int docId,String email) throws Exception;
	
//	public List<Documents> getAllPublicDocuments(String email) throws Exception;
	
	public int checkLicence(String location) throws Exception;
	
	public void deleteDocPage(int docId, int versionNumber) throws Exception;
	
	public List<Tags> getTags(int id, String type) throws Exception;

	public void saveTag(Tags tags, int userId) throws Exception;
	
	public void deleteTag(Tags tags) throws Exception;
	
	public List<FacetValue> getTagsByPopularity() throws Exception;
	
	public List<ProposedTag> getTagLibrary() throws Exception;

	public void saveTagLibrary(ProposedTag proposedTag) throws Exception;
	
	public void deleteTagLibrary(ProposedTag proposedTag) throws Exception;

	public void updateTagLibrary(ProposedTag proposedTag) throws Exception;
	
	public List<IObject> searchTags(String word) throws Exception;
	
	public void saveChatBuddy(ChatBuddy buddy) throws Exception;
	
	public void deleteChatBuddy(ChatBuddy buddy) throws Exception;
	
	public List<ChatBuddy> getAllChatBuddies(String user) throws Exception;
	
	public void saveLicense(LicenceKey license) throws Exception;
	
	public LicenceKey getLicense() throws Exception;
	
	public void saveUserWarning(Warnings warning) throws Exception;
	
	public Warnings getWarning(int userId) throws Exception;
	
	public void changeWarning(int userId) throws Exception;

	
	public void saveCampaign(Campaign campaign, List<CampaignLoader> loaders) throws Exception;
	
	public void updateCampaignLoader(CampaignLoader loader) throws Exception;
	
	public void deleteCampaign(Campaign campaign)throws Exception;
	
	public void updateCampaign(Campaign campaign)throws Exception;
	
	public List<Campaign> getAllCampaigns()throws Exception;
	
	public List<CampaignLoader> getAllCampaignLoadersByUser(String email) throws Exception;
	
	public List<CampaignLoader> getAllCampaignLoadersByCampaign(Campaign campaign) throws Exception;
	
	public void saveCampaignNotes(CampaignNotes note) throws Exception;
	
	public List<CampaignNotes> getCampaignNotes(CampaignLoader loader) throws Exception;
	
	public CampaignLoader getCampaignLoaderById(int id) throws Exception;
	
	public void encryptFile(InputStream doc,String password,String url) throws Exception;
	
	public void decryptFile(InputStream doc,String password,String url) throws Exception;
	
//	public List<Documents> getAllDocs() throws Exception;
	
//	public void editThisDoc(Documents doc) throws Exception;
	
	public void editThisVersion(Versions version) throws Exception;
	
//	public List<Versions> getAllVersions() throws Exception;
	
//	public IObject getRootParent(int id,String type) throws Exception;

	public LicenceKey getLicenceFromFile(String url) throws Exception;

	public Documents uploadFiles(HashMap<String, InputStream> files, Documents doc) throws Exception;

//	public List<Tree> getAllFiles() throws Exception; 
	
	public List<IObject> getAllFilesbyUser(User user) throws Exception;
	
	public List<UserLogs> getAllUserLogs() throws Exception;
	
	public List<Tasks> getAllTasks() throws Exception;
	
	public List<Message> getOverallMessage() throws Exception;
	
	public List<Comments> getOverallComments() throws Exception;
	
	public void saveAnnouncement(Announcements a) throws Exception;
	
	public void updateAnnouncement(Announcements a) throws Exception;
	
	public void deleteAnnouncement(Announcements a) throws Exception;
	
	public List<Announcements> getAnnouncements() throws Exception;
	
//	public List<Documents> getDocsFromParent(int id) throws Exception;
	
	public List<Downloads> getDownloads() throws Exception;
	
	public void saveCountry(Country c) throws Exception;

	public void updateCountry(Country c) throws Exception;
	
	public void deleteCountry(Country c) throws Exception;
	
	public List<City> getCity() throws Exception;
	
	public void saveCity(City c) throws Exception;

	public void updateCity(City c) throws Exception;
	
	public void deleteCity(City c) throws Exception;
	
	public void saveCountryCities(List<City> cc, int countryId) throws Exception;
	
	public XaklFiles saveXakl(XaklFiles xaklFiles) throws Exception;
		
	public void deleteXakl(int xaklId) throws Exception;
	
	public Documents saveXaklDocs(Documents doc) throws Exception;
	
	public void saveXaklAklaBox(List<XaklAklaBox> aklaboxDocs) throws Exception;
	
	public void deleteXaklAklaBox(List<XaklAklaBox> aklaboxDocs) throws Exception;
	
//	public List<Documents> getScanDocs(User user) throws Exception;
	
	public XaklFiles updateXakl(XaklFiles xaklFiles) throws Exception;
	
	public Documents searchDocsOfXakl(Documents doc) throws Exception;
	
	public List<XaklFiles> getXaklTree(Versions v) throws Exception;
	
	public List<XaklFiles> getXaklFiles(int docId) throws Exception;
	
	public List<XaklAklaBox> getXaklDocs(XaklFiles xaklFiles) throws Exception;
	
//	public void createPersonalUser(User user,String url,Boolean isAndroid) throws Exception;
	
	public void saveDocViews(DocViews docViews) throws Exception;
	
	public List<DocViews> getDocViews(String email) throws Exception;
	
	public void shareDocument(String documentName, String typeShare, List<User> users, List<Group> groups, InputStream stream, User user, int directoryId) throws Exception;

	public UserLogs getUserLastLogin(int userId) throws Exception;


//	public List<IObject> getFilesEnterprise(int enterpriseId) throws Exception;
	
//	public List<IObject> getFilesEnterprisePerUser(String email) throws Exception;

	
	public UserDrivers getDriverInfo(int userId, String driverType) throws Exception;
	
	public UserDrivers saveUserDriver(UserDrivers userDrivers) throws Exception;
	
	public UserDrivers updateUserDriver(UserDrivers userDrivers) throws Exception;
	
	public void saveDistribution(Distribution distribution, HashMap<DistributionLoader, List<Documents>> dispatchedMap, DistributionRunLog log) throws Exception;
	
	public void preSaveDistribution(Distribution distribution, List<DistributionLoader> listLoaders) throws Exception;
	
	public void deleteDistribution(Distribution distribution) throws Exception;
	
	public HashMap<Distribution, List<DistributionLoader>> getAllDistribution() throws Exception;

	public void updateDistributionLoader(DistributionLoader loader) throws Exception;
	
	public void updateDistribution(Distribution distribution) throws Exception;
	
	public void deleteDistributionLoader(DistributionLoader loader) throws Exception;
	
	public List<DistributionLoader> getAllDistributionLoaders(Distribution distribution) throws Exception;
	
	public void saveDistributionLog(DistributionRunLog log) throws Exception;
	
	public List<DistributionRunLog> getDistributionRunLogs(int distributionId) throws Exception;
	
	public List<DistributionRunLogLoaders> getDistributionLogLoaders(DistributionRunLog log) throws Exception;
	
	public void saveDistributionLoader(DistributionLoader loader) throws Exception;
	
	public void saveUserGroup(List<User> users, int groupId) throws Exception;

	
	public InputStream loadfile(String id) throws Exception;
	
	public String uploadfile(InputStream file, String parentid,  String name, String path, User user, Date lastModified) throws Exception;
	
	public String uploadNewVersionFile( Documents doc, InputStream inputStream, String filename, User user, Date lastModifiedDate) throws Exception;
	
	public String createDirectory(User user, String name) throws Exception;
	
	public String createSubDirectory(String name, String parentId, User user) throws Exception;
	
//	public List<IObject> getDirGroupShare(int groupId) throws Exception;
	
//	public List<IObject> getPublicDir() throws Exception;

//	public List<IObject> getDirUserShare(String email) throws Exception;

//	public List<IObject> getDirsEnterprise(int enterpriseId) throws Exception;
	
	public void addFeedback(FeedBack feedback) throws Exception;
	
	public List<Documents> getCheckoutDocs() throws Exception;

	public void savePage(DocPages page) throws Exception;
	
	public List<XaklFiles> getXaklFilesById(int id) throws Exception;
	
	public Tree getFolderCode(String code) throws Exception;
	
	public void saveState(StandardAddress sAddress) throws Exception;
	
	public void updateState(StandardAddress sAddress) throws Exception;
	
	public List<StandardAddress> getStates() throws Exception;
	
	public void deleteState(StandardAddress sAddress) throws Exception;
	
	public void convertToPDF(IObject o) throws Exception;
	
	public void saveDetailedAddress(DetailedAddress dAddress) throws Exception;

	public void updateDetailedAddress(DetailedAddress dAddress, String originalReference,int saId) throws Exception;
	
	public void deleteDetailedAddress(DetailedAddress dAddress) throws Exception;
	
	public List<DetailedAddress> getDetailedAddress() throws Exception;
	
	public void addLevel(DetailedLevel level) throws Exception;
	
	public List<DetailedLevel> getLevels(int daId) throws Exception;
	
	public void saveFileLocation(FileLocation fileLocation) throws Exception;
	
//	public List<IObject> getAllFilesAndDocs() throws Exception;
	
	public List<FileLocation> getPhysicalLocations() throws Exception;
	
	public List<String> aggregateDocuments(List<IObject> files, int aklaBoxDirectory, String path, String fileName, int userId, String type, String orientation) throws Exception;

	public void deletePhysicalLocation(FileLocation fileLocation) throws Exception;
	
	public void saveOfficeNo(OfficeAddress officeAddress) throws Exception;

	public List<OfficeAddress> getOfficeAddress(int levelNo, int daId) throws Exception;
	
	public void deleteLevel(DetailedLevel dLevel) throws Exception;
	
	public void updateLevel(DetailedLevel dLevel) throws Exception;
	
	public void deleteOffice(OfficeAddress oAddress) throws Exception;
	
	public void updateOffice(OfficeAddress oAddress) throws Exception;
	
	public void updateFileLocation(FileLocation location) throws Exception;
	
	public String sendEmail(MailConfig mailConfig, HashMap<String, InputStream> attachements) throws Exception;
	
	public void sendEmails(List<EmailInfo> mails) throws Exception;

	public void copyPaste(IObject doc, Tree t, User us) throws Exception;
	
	public void deleteVersion(Versions version) throws Exception;
	
	public void updateVersion(Versions version) throws Exception;

	public void initSessionFromVanilla(String hash, String email) throws Exception;
	
	public User getUserFromVanilla(String hash) throws Exception;
	
	public void saveSearchResult(SearchResult result) throws Exception;
	
	public void deleteSearchResult(SearchResult result) throws Exception;
	
	public List<SearchResult> getAllSearchResult(int userId) throws Exception;
	
	public List<Tags> getTagByUser(int userId) throws Exception;
	
	public void savePlatform(Platform platform) throws Exception;
	
	public void updatePlatform(Platform platform) throws Exception;
	
	public Platform getPlatform() throws Exception;
	
	public LoginDetail saveLoginDetail(LoginDetail loginDetail) throws Exception;
	
	public List<LoginDetail> getAllLoginDetails() throws Exception;
	
	public void updateLoginDetail(LoginDetail loginDetail) throws Exception;
	
	public void saveUserRole(UserRole userRole) throws Exception;
	
	public void deleteUserRole(UserRole userRole) throws Exception;
	
	public UserRole getUserRole(int userId) throws Exception;
	
	public void saveDocName(DocName docName) throws Exception;
	
	public void updateDocName(DocName docName) throws Exception;
	
	public DocName getDocNameByFolder(int folderId) throws Exception;
	
	public void saveDocLog(DocLogs log) throws Exception;
	
	public void updateDocLog(DocLogs log) throws Exception;
	
	public void deleteDocLog(DocLogs log) throws Exception;
	
	public List<DocLogs> getAllDocLogs(int docId, String type) throws Exception;
	
	public void saveUserConnectionProperty(UserConnectionProperty connProperty) throws Exception;
	
	public void updateUserConnectionProperty(UserConnectionProperty connProperty) throws Exception;
	
	public UserConnectionProperty getConnectionPropertyByUser(int userId) throws Exception;
	
//	public void saveRMFolder(Tree tree, RMFolder folder, List<Enterprise> enterprises, List<RMField> rmFields) throws Exception;

	public List<RMFolder> getAllRMFoldersByUser(int userId) throws Exception;
	
	public RMFolder getRMFolder(int folderId) throws Exception;
	
	public List<RMField> getAllRMFieldsByFolder(int rmFolderId) throws Exception;
	
	public void saveRMDoc(RMDocument rmDoc) throws Exception;
	
	public List<RMDocument> getAllRMDocByFolder(int rmFolderId) throws Exception;
	
	public RMField getRMField(int rmFieldId) throws Exception;
	
	public void checkRMScheduleDeadline(Documents doc, int currentUser) throws Exception;
	
	public void saveLoginAttempt(LoginAttempt attempt) throws Exception;
	
	public List<LoginAttempt> getAllLoginAttempts() throws Exception;
	
	public List<UserLogs> getUserLogsByUser(int userId) throws Exception;
	
	public void saveUserLog(UserLogs userLog) throws Exception;
	
	public int getDocDownloads(int docId) throws Exception;
	
	public int getFolderDownloads(int folderId) throws Exception;
	
	public List<TopDownloads> getTopMostDownloads(int userId) throws Exception;
	
	public void saveLinkedDoc(CopyDocuments linkedDoc) throws Exception;
	
	public List<CopyDocuments> getLinkedDocuments(int toFolder) throws Exception;
	
	public void saveAttachDoc(ReferenceDocuments refDoc) throws Exception;
	
	public List<ReferenceDocuments> getAttachDocuments(int docId) throws Exception;
	
	public void deleteCopyDoc(CopyDocuments doc) throws Exception;
	
	public List<CopyDocuments> getCopyDocument(int docId, int folderId) throws Exception;

	public DocName getCustomNameByFolder(int folderId) throws Exception;

	public List<DocName> getAllAutomaticCustomName() throws Exception;
	
	public LOV saveLov(LOV lov) throws Exception;
	
	public List<LOV> getAllLov() throws Exception;
	
	public List<LOV> getAllDistinctLov() throws Exception;

	public void saveFolderStorageLocation(FolderStorageLocation fsl) throws Exception;
	
	public FolderStorageLocation getFolderStorageLocation(int folderId) throws Exception;

	public void saveAddressField(AddressField address) throws Exception;

	public List<AddressField> getAllAddressField() throws Exception;
	
	public void deleteAddressField(AddressField address) throws Exception;
	
	public Form saveForm(Form form) throws Exception;
	
	public void updateForm(Form form) throws Exception;
	
	public void deleteForm(Form form) throws Exception;
	
	public List<Form> getAllForm() throws Exception;
	
	public FormField saveFormField(FormField formField) throws Exception;
	
	public void updateFormField(FormField formField) throws Exception;
	
	public void deleteFormField(FormField formField) throws Exception;
	
	public List<FormField> getAllFormFields(int formId) throws Exception;

	public HashMap<String, String> getAllLovByTableCode(LOV lov) throws Exception;
	
	public void saveFolderForm(FormFolder formFolder) throws Exception;
	
	public List<FormFolder> getFolderForms(int folderId) throws Exception;
	
	public List<Form> getFolderFormsByUser(int folderId, int userId) throws Exception;
	
	public void saveFormFieldValue(int docId, List<FormFieldValue> value) throws Exception;
	
	public List<FormFieldValue> getAllFormFieldValues(int formId) throws Exception;
	
	public FormField getFormField(int id) throws Exception;
	
	public void updateFormFieldValue(FormFieldValue value) throws Exception;

	public FormFieldValue getFormFieldValue(int ffValueId) throws Exception;
	
	public FormFieldValue getFormFieldValueByDoc(int ffId, int userId,int docId) throws Exception;
	
	public List<FormFieldValue> searchOnFormField(int formFieldId, String value, String operator) throws Exception;

	public List<FileType> getAllFilesType() throws Exception;

	public FileType getFirstType() throws Exception;

	public void saveFileTypes(List<FileType> fileTypes) throws Exception;
	
	public void addReply(Reply reply) throws Exception;
	public void modifyReply(Reply reply) throws Exception;
	public void deleteReply(Reply reply) throws Exception;
	public List<Reply> getAllReplies() throws Exception;

	public void saveTask(Tasks task) throws Exception;

	public List<Documents> getDocumentForArchiving(Archiving a, Date dateLimit)
			throws Exception;


	public List<Archiving> getAllArchiving() throws Exception;

	public Archiving getArchivingbyId(int id) throws Exception;

	public Archiving saveArchiving(Archiving archiving) throws Exception;

	public List<MassImport> getAllMassImport() throws Exception;

	public MassImport getMassImportbyId(int id) throws Exception;

	public MassImport saveMassImport(MassImport massImport) throws Exception;

	public void deleteArchiving(Archiving archiving) throws Exception;

	public void deleteMassImport(MassImport massImport) throws Exception;
	
	public Documents saveMailDocument(Documents doc, List<Tasks> task, List<FormFieldValue> fieldValues) throws Exception;


	public List<SourceConnection> getAllConnection() throws Exception;

	public List<OrganigramElement> getOrganigram() throws Exception;
	
	public void saveOrganigram(List<OrganigramElement> elements) throws Exception;
	
	public List<IObject> getOrganigramFolderContent(int parentId) throws Exception;
	
	public List<MailSearchResult> getMailSearchResult(int userId) throws Exception;
	
	public void saveMailSearchResult(MailSearchResult mailSearchResult) throws Exception;
	
	public void deleteMailSearchResult(MailSearchResult mailSearchResult) throws Exception;

	public SourceConnection getSourceConnectionbyId(int id) throws Exception;

	public SourceConnection saveSourceConnection(SourceConnection connection)
			throws Exception;

	public void deleteSourceConnection(SourceConnection connection) throws Exception;

	public Boolean testConnection(SourceConnection connection) throws Exception;

	public List<String> getTable(SourceConnection connection) throws Exception;

	public List<String> getColumns(SourceConnection connection, String tableName) throws Exception;

	public void deleteLov(LOV lov) throws Exception;

	public void saveCommentAndUpdateTasks(Comments comment, Tasks task) throws Exception;

	public void approveComment(Comments comment, Tasks task) throws Exception;

	public void validateTask(Tasks task) throws Exception;

	public void closeTask(Tasks t) throws Exception;

	public void reviveTask(Tasks task) throws Exception;

	public AklaboxConfig getConfig() throws Exception;
	
	public void saveConfig(AklaboxConfig config) throws Exception;

	public void relance(Tasks task) throws Exception;

	public Tasks getPreviousTask(Tasks task) throws Exception;

	public void addListValue(LOV listOfValues, String value) throws Exception;

	public void addResponse(Tasks task, Documents doc) throws Exception;
	
	public void deleteFileType(FileType object) throws Exception;
	
	public Tasks getTaskById(int id) throws Exception;
	
	public List<Departement> getDepartements(int countryId) throws Exception;
	
	public List<City> getCities(int depId) throws Exception;
	
	public void saveDepartement(Departement dep) throws Exception;
	
	public List<Departement> getDepartements() throws Exception;

	public List<LogMassImport> getLogMassImports(int id) throws Exception;

	public LogMassImport saveLogMassImport(LogMassImport logMassImport)
			throws Exception;

	public void updateLogMassImport(LogMassImport logMassImport) throws Exception;

	public void deleteLogMassImport(LogMassImport logMassImport) throws Exception;

	public void updateLov(LOV lov) throws Exception;

	public Form getForm(int id) throws Exception;

	public List<Documents> getAllDocOrganigram(int parentId) throws Exception;

	public List<AlfrescoExport> getAllAlfrescoExport() throws Exception;

	public AlfrescoExport getAlfrescoExportbyId(int id) throws Exception;

	public AlfrescoExport saveAlfrescoExport(AlfrescoExport export) throws Exception;

	public void deleteAlfrescoExport(AlfrescoExport export) throws Exception;

	public void mutateUser(OrganigramElementSecurity object, User user, boolean allTasks) throws Exception;
	
	public void suspendTasks(OrganigramElementSecurity object, boolean allTasks, Date dateExpiration) throws Exception;
	
	public void attribTasks(String userMail, User newUser) throws Exception;

	public List<RuleAlim> getRuleAlim(int id) throws Exception;

	public RuleAlim saveRuleAlim(RuleAlim rule) throws Exception;

	public List<RuleAlim> getAllRuleAlim() throws Exception;

	public void deleteRuleAlim(RuleAlim rule) throws Exception;

	public List<Tasks> getTaskWithDoc(String email) throws Exception;

	public List<SuspendedTasks> getSuspendedTasks() throws Exception;
	
	public List<HistoMutation> getHistoMutations(String userEmail) throws Exception;

	public List<Tasks> getTaskGivenWithDoc(String email) throws Exception;

	public List<Tasks> getAllTaskWithDoc() throws Exception;

	public List<Tasks> getTaskForDoc(String email) throws Exception;
	
	public void addThesaurus(Thesaurus thesaurus) throws Exception;
	
	public List<Thesaurus> getThesauruses() throws Exception;
	
	public void deleteThesaurus(Thesaurus object) throws Exception;

	public List<FolderHierarchy> getHierarchies() throws Exception;

	public void updateHierarchy(FolderHierarchy hierarchy) throws Exception;

	public void deleteHierarchy(FolderHierarchy hierarchy) throws Exception;
	
	public List<UserLogs> getLogsByDocument(int documentId) throws Exception;

	public void updateDocument(Documents document) throws Exception;

	public void addDocumentArchive(DocumentArchives docarc) throws Exception;

	public List<DocumentArchives> getAllDocumentArchives() throws Exception;

	public List<DocumentArchives> getDocumentArchivesbyArchiving(int idArchiving) throws Exception;

	public List<DocumentArchives> getDocumentArchivesbyDocument(int documentId) throws Exception;

	public Enterprise getEnterprise(int enterpriseId) throws Exception;

	public List<XaklFiles> getXaklContainingDoc(int id) throws Exception;

	public List<ModelEnterprise> getAllModelEnterprise(int enterpriseId) throws Exception;

	public ModelEnterprise saveModelEnterprise(ModelEnterprise me) throws Exception;

	public List<ModelEnterprise> getAllModelEnterprisebyFolder(int folderId) throws Exception;

	public void saveTypeTask(TypeTask typeTask) throws Exception;
	
	public List<TypeTask> getTypeTask() throws Exception;

	public void deleteModeleEnterprise(ModelEnterprise modelEnterprise) throws Exception;
	
	public String getFileContent(String filePath) throws Exception;

	public Group getGroupByAdress(String receiver) throws Exception;

	public void saveOrUpdateValidationStatus(ValidationStatus vs) throws Exception;

	public void deleteValidationStatus(ValidationStatus vs) throws Exception;

	public List<ValidationStatus> getAllValidationStatus() throws Exception;

	public ValidationStatus getValidationStatusbyId(int idVS) throws Exception;

	public List<Form> getAllFormByType(String formType) throws Exception;

//	public List<Documents> getAllDocsbyFileType(int typeId) throws Exception;

//	public Documents TakeScreenshotUrl(Documents doc) throws Exception;

	public Documents getOriginalOfDocument(Documents document) throws Exception;

	public int saveDelegation(Delegation delegation) throws Exception;

	public List<Delegation> getAllDelegation(String emailDelegator) throws Exception;

	public void removeDelegation(List<Delegation> removeDelegationList) throws Exception;

	public List<Delegation> getAllDelegationByDelegate(String emailDelegate) throws Exception;

	public List<InformationsNews> getAllActualNewsInfos() throws Exception;

	public void removeInformationsNews(List<InformationsNews> informationsNews) throws Exception;

	public void saveInformationsNews(List<InformationsNews> informationsNews) throws Exception;

	public SubjectIdeaBox createSubjectIdeaBox(SubjectIdeaBox s) throws Exception;

	public MessageIdeaBox createMessageIdeaBox(MessageIdeaBox m) throws Exception;

	public List<MessageIdeaBox> getMessageIdeaBoxBySubjectId(int idSubject) throws Exception;

	public List<SubjectIdeaBox> getAllSubjectIdeaBox() throws Exception;

	public SubjectIdeaBox getSubjectIdeaBoxById(int id) throws Exception;

	public void addVoteToSubject(int id) throws Exception;

	public List<Comments> getCommentToApprove() throws Exception;

	public List<FormFieldValue> getFormFieldValueByDoc(int docId,int formId) throws Exception;

	public String createBarCodeImage(Documents d,String format, String data) throws Exception;

	public List<String> parsePDFForm(String filePDFForm) throws Exception;

	public SurveyIdeaBox createSurvey(SurveyIdeaBox survey) throws Exception;

	public SurveyIdeaBox getSurveyBySubjectId(int idSubject)  throws Exception;

	public SubjectIdeaBox updateSubject(SubjectIdeaBox subject) throws Exception;

	public String createVoteSurveyIdeaBox(SurveyIdeaBox survey,ChoiceIdeaBox choice,User u) throws Exception;

	public Map<String, Integer> getResultBySurvey(SurveyIdeaBox survey) throws Exception;

	public List<InformationsNews> getAllNewsInfos() throws Exception;

	public void updateFolderBackground(Tree folder, String background) throws Exception;

	public ScanMetaObject generateScanMetaObjectFromZip(InputStream stream) throws Exception;
	public ScanMetaObject generateScanMetaObjectFromZip(String path) throws Exception;

	public ScanMetaObject generateScanMetaObjectFromFiles(List<InputStream> files, List<String> names, List<Integer> sizes) throws Exception;

	public void transfertToGroup(Group fromGroup, Group toGroup) throws Exception;

	public void transfertToEnterprise(Enterprise fromEnterprise,Enterprise toEnterprise) throws Exception;

	public void saveUserSuccessor(List<UserSuccessor> activeUserSuccessorList) throws Exception;

	public List<UserSuccessor> getUserSuccessor(int userId)	throws Exception;
	public void removeUserSuccessor(List<UserSuccessor> removeUserSuccessorList) throws Exception ;

	public void addAbsence(Absence absence) throws Exception;
	
	public void removeAbsence(Absence absence)throws Exception;
	
	public List<Absence> getAbsences(Integer userId)throws Exception;

	public void treatImage(TreatmentImageObject treatment, Documents doc) throws Exception;

	public void saveScanDocumentType(ScanDocumentType docType) throws Exception;

	public void updateScanDocumentType(ScanDocumentType docType) throws Exception;

	public List<ScanDocumentType> getAllScanDocumentType() throws Exception;

	public void deleteScanDocumentType(ScanDocumentType docType) throws Exception;

	public InputStream getDocumentStream(int docId) throws Exception;

	public void saveScanKeyWord(ScanKeyWord keyword) throws Exception;

	public void updateScanKeyWord(ScanKeyWord keyword) throws Exception;

	public List<ScanKeyWord> getAllScanKeyWordsbyDocType(ScanDocumentType docType) throws Exception;

	public void deleteScanKeyWord(ScanKeyWord keyword) throws Exception;

	public LOV getLov(int id) throws Exception;
	
	public List<MetadataLink> getMetadatas(IObject item, LinkType type, boolean fetchParent) throws Exception;

	public List<Form> getMetadatas() throws Exception;

	public Integer saveMetadataLink(MetadataLink link, boolean deleteOldLink) throws Exception;
	
	public void saveMetadataValues(int itemId, HashMap<Form, List<FormFieldValue>> metadataValues, LinkType type, boolean applyToChild) throws Exception;
	
	public List<FormFieldValue> getMetadataValues(MetadataLink link, Integer docId) throws Exception;

	public void updateMetadataValues(HashMap<Form, List<FormFieldValue>> metadataValues) throws Exception;

	public Deed getDeedInformationsFromMetadata(Documents doc, int userId)	throws Exception;

	public void updatePage(DocPages page) throws Exception;

	public void saveAkladSettings(AkladSettings settings) throws Exception;

	public AkladSettings getAkladSettingbyUser(int userId) throws Exception;

	public User getRequestUser() throws Exception ;

	public List<Documents> pdfToDocs(String name, String path, int size)  throws Exception;
	
	public Log<? extends ILog> addOrUpdateActionLog(Log<? extends ILog> log) throws Exception;
	
	public List<Log<? extends ILog>> getActionLogs(LogType type) throws Exception;
	
	public List<IObject> findChildrenEnterprise(Enterprise enterprise, Tree parentFolder, boolean lightWeight) throws Exception;
	
	public List<User> searchUsers(String filter) throws Exception;
	
	public List<User> getUserEnterprise(int enterpriseId, TypeUser typeUser) throws Exception;
	
	public ItemValidation manageItemValidation(ItemValidation validation, ManagerAction action) throws Exception;

	public List<ItemValidation> getItemValidations(int itemId, ItemType type) throws Exception;

	public String createWordFromPdf(Documents document) throws Exception;
	
	public boolean hasForms(IObject item, LinkType type) throws Exception;
	
	public boolean checkIfFormsAreCompleted(IObject item, LinkType type) throws Exception;
	
	public PermissionItem managePermissionItem(IObject item, PermissionItem permissionItem, ManagerAction action, ShareType shareType, List<Permission> permissions, boolean savePermission) throws Exception;
	
	/**
	 * Get folder by ID and check permission for the current user.
	 * 
	 * @param folderId
	 * @param itemTreeType
	 * @return
	 * @throws Exception
	 */
	public Tree getDirectory(int folderId, ItemTreeType itemTreeType, Boolean isDeleted) throws Exception;

	/**
	 * Get folder by ID
	 * No check is made for permission. Please use {@link IVdmManager#getDirectory(int, ItemTreeType, Boolean) getDirectory} if you want permission applied.
	 * 
	 * @param tree
	 * @return
	 * @throws Exception
	 */
	public Tree getDirById(int folderId) throws Exception;
	
	/**
	 * Get items (documents and/or folder) according to certains parameters.
	 * 
	 * CAREFULL -> If parentId is null or <= 0 and ItemTreeType.ENTERPRISE, we return all the root's enterprise folders for the user
	 * 
	 * @param userId (User from session used if set to null)
	 * @param parentId (FolderId)
	 * @param permissionItem  (PermissionItem of the parent)
	 * @param treeItemType (MY_DOCUMENTS, ENTERPRISE, SHARED_WITH_ME)
	 * @param type (DOCUMENT, FOLDER, ALL)
	 * @param isDeleted (Set to null if you want all documents (deleted and not deleted))
	 * @param isActivated (Set to null if you want all documents (activated and not activated))
	 * @param checkPermission (Check permission for the user)
	 * @param loadChilds (Load childs if set to true)
	 * @return A list of items (documents and/or folder)
	 * @throws Exception
	 */
	public List<IObject> getItems(Integer userId, Integer parentId, PermissionItem permissionItem, ItemTreeType treeItemType, IObject.ItemType type, Boolean isDeleted, Boolean isActivated, boolean checkPermission, boolean loadChilds) throws Exception;

	/**
	 * Get not deleted and activated items for a user (documents and/or folder) according to certains parameters.
	 * (CheckPermission is set to true, isDeleted to false, isActivated to true)
	 * 
	 * @param userId (User from session used if set to null)
	 * @param parentId (FolderId)
	 * @param permissionItem (PermissionItem of the parent)
	 * @param treeItemType (MY_DOCUMENTS, ENTERPRISE, SHARED_WITH_ME)
	 * @param type (DOCUMENT, FOLDER, ALL)
	 * @param loadChilds (Load childs if set to true)
	 * @return A list of items (documents and/or folder)
	 * @throws Exception
	 */
	public List<IObject> getItems(Integer userId, Integer parentId, PermissionItem permissionItem, ItemTreeType itemTreeType, IObject.ItemType type, boolean loadChilds) throws Exception;
	
	/**
	 * Get root folder for an enterprise (with or without his childs) according to certains parameters.
	 * Call {@link IVdmManager#getItems(Integer, Integer, ItemTreeType, bpm.document.management.core.model.IObject.ItemType, Boolean, Boolean, boolean, boolean)} if you already have a folder from an enterprise
	 * 
	 * @param userId (User from session used if set to null)
	 * @param enterpriseId
	 * @param type (DOCUMENT, FOLDER, ALL)
	 * @param isDeleted
	 * @param isActivated
	 * @param checkPermission
	 * @param loadChilds (Load childs if set to true)
	 * @return The root folder
	 * @throws Exception
	 */
	public Tree getEnterpriseRootFolder(Integer userId, Integer enterpriseId, IObject.ItemType type, Boolean isDeleted, Boolean isActivated, boolean checkPermission, boolean loadChilds) throws Exception;
	
	public List<Permission> getPermissions(int permissionItemId, ShareType type, boolean lightWeight) throws Exception;
	
	public PermissionItem getPermissionItem(String hash) throws Exception;
	
	public SearchResult searchItems(final SearchMetadata searchMetadata, IObject.ItemType type) throws Exception;
	
	/**
	 * Get the item
	 *
	 * @param itemId
	 * @param type
	 * @param itemTreeType
	 * @param isDeleted
	 * @param checkPermission
	 * @param loadParent
	 * @param lightWeight
	 * @return
	 * @throws Exception
	 */
	public IObject getItem(int itemId, IObject.ItemType type, ItemTreeType itemTreeType, Boolean isDeleted, boolean checkPermission, boolean loadParent, boolean lightWeight) throws Exception;
	
	//Deprecated

	//Try to use DocumentManager
	@Deprecated
	public List<Documents> saveDocuments(List<Documents> documents) throws Exception;

	/**
	 * Get folder by ID
	 * No check is made for permission. Please use {@link IVdmManager#getDirectory(int, ItemTreeType, Boolean) getDirectory} instead.
	 * 
	 * @param tree
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public Tree getDirectoryInfo(Tree tree) throws Exception;

	/**
	 * Please use {@link IVdmManager#getDocInfo(int)}
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public Documents getDocById(int id) throws Exception;
	
	@Deprecated
	public List<IObject> getAllSubIObject(int id, PermissionItem permissionParent)throws Exception;
	
	/**
	 * To remove someday
	 * 
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public List<Documents> getDocList() throws Exception;

	@Deprecated
	public List<Tree> getSubdirectories(Tree tree) throws Exception;

	@Deprecated
	public List<Documents> getSubDocs(Tree folder) throws Exception;

	@Deprecated
	public List<Documents> getAllDocuments(int userId) throws Exception;

	@Deprecated
	public List<Tree> getDirectoriesPerUser(String email) throws Exception;

	@Deprecated
	public List<IObject> getMailRootDirectories() throws Exception;

	@Deprecated
	public List<Documents> getAllMails() throws Exception;

	public void saveMailServers(List<MailServer> mailServers) throws Exception;

	public List<MailServer> getMailServers() throws Exception;

	public List<Form> getAllFormByType(FormType formType) throws Exception;

	public List<OrbeonFormSection> getOrbeonFormSections(Form selectedObject) throws Exception;

	public Documents createOrbeonDocument(int parentId, String name) throws Exception;

	public OrbeonFormInstance getOrbeonFormInstance(Form form, Documents doc) throws Exception;

	public OrganigramElement getOrganigramElement(int id, boolean fill) throws Exception;

	public void updateOrbeonInstance(OrbeonFormInstance instance) throws Exception;

	public FileType getFileType(int idFileType) throws Exception;

	public List<String> loadOrbeonCheckBoxes(int linkedFormId) throws Exception;

	public void sendOrbeonNotification(Documents doc, User user, int typeMail) throws Exception;
	
	public Documents getOrbeonDocument(int parentId) throws Exception;

	public void deleteElement(OrganigramElement elem) throws Exception;

	public void deleteElementSecurity(OrganigramElementSecurity elem) throws Exception;

	public Versions saveVersion(Versions docVersion) throws Exception;

	public void updateOrbeonStampSection(Form form, List<String>  sections, String stampPath, String instanceId, String dataXml) throws Exception;

	public List<OrganigramElementSecurity> getDirectSuperiors(User user, int levelSup, Fonction fonctionSup) throws Exception;

	public Tree getDirectoryByNameParent(String name, Integer parentId) throws Exception;

	public OrbeonFormInstance getFormInstance(String instanceId) throws Exception;

	public List<Documents> getDocumentsByUser(TypeMethodDocument type, User user) throws Exception;
	
	public Tree getDirectoryByHierarchyItemAndRoot(FolderHierarchyItem item, Tree root) throws Exception;

	public FolderHierarchy getHierarchy(int id) throws Exception;

	public List<String> getColumnsbyRequest(SourceConnection connection, String request) throws Exception;

	public List<Map<String, String>> getResultbyRequest(SourceConnection connection, String request) throws Exception;

	public InputStream createMetadataPDFFile(IObject item, Map<Form, List<FormFieldValue>> values) throws Exception;

	public List<FormFieldValue> getFormValuesFromDataSource(Form form, List<FormFieldValue> filterKeys, Documents doc) throws Exception;

	public User getUserBySession(String sessionId) throws Exception;
	
	//TODO: REFACTOR RIGHT - LATER - TO REMOVE

//	public List<IObject> getTrashedItems(String email) throws Exception;
//	
//	public void pushToAklaBox(List<Documents> documents, String sourceURL) throws Exception;
//	
//	@Deprecated
//	public List<IObject> getSubItems(int folderId) throws Exception;
//	
//	public List<FolderShare> getExternalShare(int docId) throws Exception;
//	
//	public FolderShare checkIfShared(String email, int id, String type) throws Exception;
//	
//	public List<FolderShare> getDocShares(int docId) throws Exception;
//	
//	public List<FolderShare> getFolderShares(int folderId) throws Exception;
//
//	public List<FolderShare> getSharedDocs(int docId) throws Exception;
//	
//	public List<FileShared> getFileShared(int fileId, String fileType) throws Exception;
//	
//	public void editPreEmption(List<FolderShare> fs) throws Exception;
//	
//	public List<FolderShare> getAllExternalShare() throws Exception;
//	
//	public void updateFolderShare(FolderShare fs) throws Exception;
//	
//	public List<FolderShare> getFolderExternalShare(int folderId) throws Exception;
//	 
//	public FolderShare getDocShareByUserDocId(int userId, int docId) throws Exception;
//	
//	public FolderShare getFolderShareByUserDocId(int userId, int folderId) throws Exception;
//	
//	public void deleteFolderShare(FolderShare fs) throws Exception;
//	
//	public void saveFolderShare(FolderShare fs) throws Exception;
//	
//	public List<FileShared> getExternalShareByUserId(int userId) throws Exception;
//
//	public List<Documents> getGroupDocs(int gsnId) throws Exception;
//	
//	public void saveGroupWorkspace(String email,String groupName, String groupDescription, String pinCode, List<User> emails, List<Documents> docs,boolean canRead, boolean isAnonymous, boolean allowAds) throws Exception;
//	
//	public void updateGroupWorkspace(int gsnId,String email,String groupName, String groupDescription, String pinCode, List<User> emails, List<Documents> docs,boolean canRead, boolean isAnonymous, boolean allowAds) throws Exception;
//	
//	public void saveSuspendDocument(SuspendDocument suspendDoc) throws Exception;
//	
//	public void updateSuspendDocument(SuspendDocument suspendDoc) throws Exception;
//	
//	public void deleteSuspendDocument(SuspendDocument suspendDoc) throws Exception;
//	
//	public List<SuspendDocument> getAllSuspendDocuments(int userId) throws Exception;
//	
//	public SuspendDocument getSuspendDocumentByDocId(int docId, String type) throws Exception;
}