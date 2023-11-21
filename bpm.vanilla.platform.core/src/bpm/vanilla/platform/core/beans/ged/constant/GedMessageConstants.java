package bpm.vanilla.platform.core.beans.ged.constant;


public class GedMessageConstants {
	public final static String INDEX_FILE = "bpm.vanilla.server.ged.tasks.IndexFile";
	public static final String DEFINITIONS = "bpm.vanilla.server.ged.tasks.Definitions";
	public static final String TEMPORARY_PATH = "bpm.vanilla.server.ged.tasks.temporarypath";
	
	public final static String AUTHOR = "author";
	public final static String TITLE = "title";
	public final static String VERSION = "version";
	public final static String PREVIOUSVERSION = "previousversion";
	public final static String SUMMARY = "summary";
	public static final String FORMAT = "format";
	public static final String MODIFIABLE = "modifiable";
	public static final String REPOSITORY_ID = "repositoryId";

	public static final String TASK_PRIORITY = "taskPriority";
	public static final String GROUP = "group";
	
	public static final String SEARCH = "search";
	public static final String OCCURENCE = "occurence";
	public static final String KEY_WORDS = "keywords";
	public static final String GET_FILE = "getfile";
	public static final String LOAD_HISTORIZED_REPORT = "loadHistorizedReport";
	public static final String GET_DOC_ID = "getIndexedDocId";
	public static final String TASK_ID = "taskId";
	public static final String GET_ALL_CATEGORY = "getAllCategory";
	public static final String ONLY_HISTORIC = "onlyhistory";
	
	public static final String ITEM_ID = "repository.itemId";
	public static final String HOST = "repository.host";
	public static final String USER_NAME = "repository.username";
	public static final String USER_ID = "repository.userid";//added by ere
	public static final String GROUP_NAME = "repository.groupName";//added by ere
	public static final String USER_PASSWORD = "repository.password";
	public static final String REPOSITORY_VERSION = "repository.version";
	
	public static final String CATEGORY_ID = "category.id";
	public static final String CATEGORY_PARENT_ID = "category.parentid";
	public static final String CATEGORY_NAME = "category.name";
	
	public static final String CREATE_ACCESS = "security.createaccess";
	public static final String AUTORIZED_DOC_ID = "security.autorized.docid";
	public static final String REMOVE_ACCESS = "security.removeaccess";
	
	//add delete
	public static final String CATEGORY_ADD = "category_add";
	public static final String CATEGORY_ADD_NAME = "category_add_name";
	public static final String CATEGORY_ADD_PARENT_ID = "category_add_parent_id";
	
	//Cat
	
	public static final String CATEGORY_UPDATE = "category_update";
	public static final String CATEGORY_UPDATE_ID = "category_update_id";
	public static final String CATEGORY_UPDATE_NAME = "category_update_name";
	public static final String CATEGORY_UPDATE_PARENT_ID = "category_update_parent_id";
	
	public static final String CATEGORY_DELETE = "category_delete";
	public static final String CATEGORY_DEL_ID = "category_del_id";
	
	//cat doc
	
	public static final String CATDOC_ADD = "catdoc_add";
	public static final String CATDOC_ADD_DOC_ID = "catdoc_add_doc_id";
	public static final String CATDOC_ADD_CAT_ID = "catdoc_add_cat_id";
	
	public static final String CATDOC_DEL = "catdoc_del";
	public static final String CATDOC_DEL_ID = "catdoc_del_id";
	public static final String CATDOC_DEL_DOC_ID = "catdoc_del_doc_id";
	public static final String CATDOC_DEL_CAT_ID = "catdoc_del_cat_id";
	
	// fields
	
	public static final String FIELD_ADD = "field_add";
	public static final String FIELD_ADD_NAME = "field_add_name";
	public static final String FIELD_ADD_BOOST = "field_add_boost";

	public static final String FIELD_UPDATE = "field_update";
	public static final String FIELD_UPDATE_ID = "field_update_id";
	public static final String FIELD_UPDATE_NAME = "field_update_name";
	public static final String FIELD_UPDATE_BOOST = "field_update_boost";
	
	public static final String FIELD_DEL = "field_del";
	public static final String FIELD_DEL_ID = "field_del_id";
	
	//documents
/*
	public static final String DOCUMENT_ADD = "document_add";
	public static final String DOCUMENT_ADD_NAME = "document_add_name";
	public static final String DOCUMENT_ADD_PATH = "document_add_path";
	public static final String DOCUMENT_ADD_VERSION = "document_add_version";
*/	
	public static final String DOCUMENT_UPDATE = "document_update";
	public static final String DOCUMENT_UPDATE_ID = "document_update_id";
	public static final String DOCUMENT_UPDATE_NAME = "document_update_name";
	public static final String DOCUMENT_UPDATE_PATH = "document_update_path";
	public static final String DOCUMENT_UPDATE_VERSION = "document_update_version";
	
	public static final String DOCUMENT_DEL = "document_del";
	public static final String DOCUMENT_DEL_ID = "document_del_id";
	public static final String DEFINITIONS_NAME = "definition.name";
	public static final String DEFINITIONS_ADD = "definition.add";
	public static final String DEFINITIONS_REMOVE = "definition.remove";
	public static final String LOOKUP_GEDDOCUMENT = "lookupGedDocById";
	
	

}
