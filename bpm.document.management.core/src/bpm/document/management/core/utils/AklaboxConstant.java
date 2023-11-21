package bpm.document.management.core.utils;

public interface AklaboxConstant {

	public static final String HTTP_HEADER_VDM_SESSION_ID = "bpm.vdm.session.id";
	public static final String DOCUMENT_MANAGEMENT_SERVLET = "/documentManagementServlet";
	public static final String AKLADEMAT_MANAGEMENT_SERVLET = "/akladematManagementServlet";
	public static final String DOCUMENT_SERVICE_SERVLET = "/documentServiceServlet";
	public static final String DOCUMENT_SERVICE_STREAM_SERVLET = "/documentServiceStreamServlet";
	public static final String SERVLET_DOWNLOAD = "DownloadServlet";

	public static final String UPLOADER_VERSION = "Version";
	public static final String UPLOADER_V2 = "V2";
	
	public static String PORTRAIT = "portrait";
	public static String LANDSCAPE = "lanscape";

	public static final String PARAM_PATH = "path";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_PRINT = "print";
	public static final String PARAM_DOCID = "docid";

	/* REGEX */
	public static final String REGEX_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final String REGEX_NAME = "^[a-z0-9_-]{3,15}$";

	public static final String REGEX_DAY="[$JOUR]";
	public static final String REGEX_MONTH="[$MOIS]";
	
	/* LOCALES */
	public static final String LOCALE = "locale";
	public static final String LOCALE_ENGLISH = "en";
	public static final String LOCALE_FRENCH = "fr";
	public static final String LOCALE_MALAY = "ms";

	/* COOKIE */
	public static final String COOKIE_EMAIL = "cookie";

	public static final String PLACE_HOLDER = "placeHolder";

	/* NAVIGATION */
	public static final int NEXT_PAGE = 0;
	public static final int BACK_PAGE = 1;

	/* DOCUMENT INFO */
	public static final String DOCUMENT_ID = "documentId";
	public static final String DOCUMENT_NAME = "documentName";
	public static final String DOCUMENT_TYPE = "documentType";
	public static final String DOCUMENT_THUMB_IMAGE = "documentThumbImage";
	public static final String DOCUMENT_CREATION_DATE = "documentCreationDate";
	public static final String DOCUMENT_UPLOAD_DATE = "documentUploadDate";
	public static final String DOCUMENT_LAST_MODIFIED = "documentLastModified";
	public static final String DOCUMENT_PATH = "documentPath";
	public static final String DOCUMENT_FILE_NAME = "documentFileName";
	public static final String DOCUMENT_FILE_EXTENSION = "documentFileExtension";
	public static final String DOCUMENT_FILE_SIZE = "documentSize";
	public static final String DOCUMENT_AUTHOR = "documentAuthor";
	public static final String DOCUMENT_PARENT = "documentParent";
	public static final String TYPE = "type";
	public static final String DOCUMENT = "Documents";
	
	public static final String FOLDER = "Folders";
	public static final String FOLDER_WORKFLOW = "Workflow Folders";
	public static final String FOLDER_PESV2 = "PESV2";
	public static final String FOLDER_PROJECT = "Project";
	public static final String FOLDER_MARKET = "Market";
	public static final String FOLDER_MARKET_FINAL = "FinalMarket";

	public static final String UNZIP_FOLDER_NAME = "folderName";
	public static final String UNZIP_FILE_PATH = "unzipFilePath";

	public static final String ALL = "all";

	public static final String CREATION_DATE = "NoCreationDateFilter";
	public static final String LAST_MODIFIED = "Last Modified";

	public static final String PUBLIC = "Public";
	public static final String PERSONAL = "Personal";
	public static final String GROUPS = "Groups";

	public static final String DEFAULT_PROFILE_PIC = "webapps/aklabox_files/images/ic_profile_pic.png";

	public static final int STARRED = 1;
	public static final int DELETED = 2;
	public static final int RECENT = 3;
	public static final int VALIDATED = 4;
	public static final int DEFAULT = 0;
	public static final int IMAGES = -1;
	public static final int MAIL = 5;
	public static final int WORKFLOW = 6;
	public static final int PESV2 = 7;
	public static final int PROJECT = 8;
	public static final int MARKET = 9;
	public static final int MARKET_FINAL = 10;

	public static final String CHAT_ONLINE = "chatOnline";
	public static final String CHAT_AWAY = "chatAway";
	public static final String CHAT_DISTURB = "chatDisturb";
	public static final String CHAT_OFFLINE = "chatOffline";

	public static final String CHAT_ONLINE_ICON = "webapps/aklabox_files/images/ic_chat_online.png";
	public static final String CHAT_AWAY_ICON = "webapps/aklabox_files/images/ic_chat_away.png";
	public static final String CHAT_DISTURB_ICON = "webapps/aklabox_files/images/ic_chat_disturb.png";
	public static final String CHAT_OFFLINE_ICON = "webapps/aklabox_files/images/ic_chat_offline.png";

	public static final String MEMORYFULL = "memoryFull";

	public static final String DONE = "DONE";
	public static final String NOTYET = "NOT FINNISHED";
	public static final String CLOSE = "CLOSED";

	public static final String FAILURE = "failure";

	public static final String LICENSE_TRIAL = "Trial License";
	public static final String LICENSE_CUSTOMER = "Customer License";
	public static final String IMAGE = "image";
	public static final String PDF = "pdf";
	
	public static final String SHARED = "Shared";
	
	public static final String AKLABOX_VERSION = "AklaBox Version 16.9";

	public static final String READER = "Reader";
	public static final String AUTHOR = "Author";
	public static final String ADMIN = "Admin";
	public static final String LOADER = "Loader";
	public static final String PERSONAL_EDITION = "Personal Edition";
	public static final String PLATFORM_MANAGER = "Platform Manager";
	public static final String CAMPAIGN_DISTRIBUTION = "Campaign and Distribution";
	public static final String RECORD_MANAGER = "Record Manager";
	public static final String ADMIN_SPACE = "Admin Space";
	public static final String EXTERNAL = "External";
	
	public static final String RESTRICT_SIZE = "restrictSize";
	public static final String RESTRICT_ALLOW = "restricAllow";
	
//	public static final String STRING = "String";
//	public static final String BOOLEAN = "Boolean";
//	public static final String DATE = "Date";
//	public static final String INTEGER = "Integer";
//	public static final String LOV = "List of Values";
//	public static final String OPTIONS = "Options";
//	public static final String ADDRESS = "Address";
//	public static final String THRESHOLD = "Threshold Value";
//	public static final String UPLOAD_DOCUMENT = "Upload Document";
//	public static final String CALENDAR_TYPE = "Calendar type";
//	public static final String NAME = "Nom";
	
	public static final String TRUE = "True";
	public static final String FALSE = "False";
	
	public static final String EVERYDAY = "Tous les jours";
	public static final String EVERYMONTH = "Tous les mois";
	public static final String EVERYYEAR = "Tous les ans";
	public static final String NEVER = "Jamais";
	
	public static final String MONTHLY= "monthly";
	public static final String WEEKLY= "weekly";
	public static final String DAILY= "daily";
	public static final String ANNUAL="annual";
	public static final String BIANNUAL="biannual";
	public static final String QUATERLY="quaterly";
	
	public static final String MONDAY= "monday";
	public static final String TUESDAY= "tuesday";
	public static final String WEDNESDAY= "wednesday";
	public static final String THURSDAY= "thursday";
	public static final String FRIDAY= "friday";
	public static final String SATURDAY= "saturday";
	public static final String SUNDAY= "sunday";
	
	public static final String PRIVATE = "Private";
	public static final String USER_SHARED ="UserShared";
	public static final String GROUP_SHARED = "GroupShared";
	public static final String EXTERNAL_SHARED = "ExternalShared";

	public static final String LOG_DELETE_DOCUMENT_TEMP = "Delete Document Temporarily";
	public static final String LOG_DELETE_DOCUMENT_PERMANENT = "Delete Document Permanently";
	public static final String LOG_RESTORE_DOCUMENT = "Restore Document";
	public static final String LOG_UPDATE_DOCUMENT = "Update Document";
	public static final String LOG_LOCK_DOCUMENT = "Lock Document";
	public static final String LOG_UNLOCK_DOCUMENT = "Unlock Document";
	public static final String LOG_SUSPEND_DOCUMENT = "Suspend Document";
	public static final String LOG_UNSUSPEND_DOCUMENT = "Unsuspend Document";
	public static final String LOG_SAVE_TAG = "Tag Document";
	public static final String SAVE_WORKFLOW_DOCUMENT = "Save Workflow Document";
	public static final String RATE_DOCUMENT = "Rate Document";
	public static final String COMMENT_DOCUMENT = "Comment Document";
	public static final String LOG_CREATE_DOCUMENT_USER_SHARED = "Create User Shared Document";
	public static final String LOG_CREATE_DOCUMENT_GROUP_SHARED = "Create Group Shared Document";
	public static final String LOG_CREATE_DOCUMENT_EXTERNAL_SHARED = "Create External Shared Document";
	public static final String LOG_CREATE_DOCUMENT_PUBLIC = "Create Public Document";
	public static final String LOG_CREATE_DOCUMENT_PRIVATE = "Create Private Document";
	public static final String DELETE_WORKFLOW_DOCUMENT = "Delete Workflow Document";
	public static final String LOG_ADD_VERSION = "New Document Version";
	public static final String LOG_VIEW_DOCUMENT = "View Document";
	public static final String DOWNLOAD_DOCUMENT = "Download Document";
	public static final String DOWNLOAD_FOLDER = "Download Folder";
	public static final String LOG_PRINT_DOCUMENT = "Print Document";
	public static final String LOG_DELETE_FOLDER_TEMP = "Delete Folder Temporary";
	public static final String LOG_DELETE_FOLDER_PERMANENTLY = "Delete Folder Permanently";
	public static final String LOG_SAVE_FOLDER = "Create Folder";
	public static final String LOG_UPDATE_FOLDER = "Update Folder";
	public static final String LOG_DELETE_VERSION = "Delete Version";
	public static final String LOG_COMMENT_DOCUMENT = "Comment Document";
	public static final String LOG_LOGIN_USER = "Login User";
	public static final String LOG_SHARE_DOCUMENT_USER_SHARED = "Share a User Shared Document";
	public static final String LOG_SHARE_DOCUMENT_GROUP_SHARED = "Share a Group Shared Document";
	public static final String LOG_SHARE_DOCUMENT_EXTERNAL_SHARED = "Share a External Shared Document";
	public static final String LOG_SHARE_DOCUMENT_PUBLIC = "Share a Public Document";
	public static final String LOG_CHECKIN = "Checkin";
	public static final String LOG_CHECKOUT = "Checkout";
	public static final String LOG_VALIDATE = "Validate";
	public static final String LOG_UNVALIDATE = "Unvalidate";
	public static final String ARCHIVING="Archiving";
	public static final String LOG_FREEZE = "Frozen";
	public static final String LOG_UNFREEZE = "Unfrozen";
	
	public static final String ENTERPRISE = "Enterprise";
	public static final String RM = "RM";
	public static final String RM_CLASSIFICATION = "RmClassification";
	public static final String RM_TYPE = "RmType";
	public static final String RM_SUBTYPE = "RmSubType";
	public static final String EXCHANGE = "Exchange";
	public static final String ATTEMPT_USER_DESACTIVATED = "User Desactivated";
	public static final String ATTEMPT_WRONG_PASSWORD = "Wrong Password";
	public static final String ATTEMPT_USER_NOT_REGISTERED = "User Not Registered";
	public static final String ATTEMPT_SUCCESSFULL = "Successfull";
	public static final String ATTEMPT_UNSUCCESSFULL = "Unsuccessfull";
	public static final String NONE = "None";
	
	public static final String LOCATION_OFFICE = "webapps/aklabox_files/Documents/Office";
	public static final String LOCATION_OFFICE_THUMBNAIL = "webapps/aklabox_files/Documents/Office/Thumbnails";
	public static final String LOCATION_VIDEO = "webapps/aklabox_files/Documents/Videos";
	public static final String LOCATION_VIDEO_THUMBNAIL = "webapps/aklabox_files/Documents/Videos/Thumbnails";
	public static final String LINKED = "Linked";
	
	public static final String PRINT_EXCEL = "Print Excel";
	public static final String PRINT_PDF = "Print PDF";
	
	public static final String FORM_IMAGE = "Image Form";
	public static final String FORM_AUDIO = "Audio Form";
	public static final String FORM_VIDEO = "Video Form";
	public static final String FORM_OFFICE = "Office Form";
	public static final String FORM_TEXT = "Text Form";
	public static final String FORM_DEFAULT = "Default Form";
	public static final String FORM_EXTENSION = "Extension Form";

	public static final String LEAVING= "leaving";
	public static final String ARRIVED= "arrived";
	
	public static final int WORKFLOW_FOLDER_STUDY = 0;
	public static final int WORKFLOW_FOLDER_INSTRUCTION = 1;
	public static final int WORKFLOW_FOLDER_WAITING = 2;
	public static final int WORKFLOW_FOLDER_FINALIZATION = 3;
	
	public static final int WORKFLOW_PROCESS_OPEN = 0;
	public static final int WORKFLOW_PROCESS_SUSPEND = 1;
	public static final int WORKFLOW_PROCESS_FINISHED = 2;
	public static final int WORKFLOW_PROCESS_ARCHIVED = 3;
	
	public static final int DOC_MODEL_ENTERPRISE = 0;
	public static final int DOC_MODEL_FOLDER = 1;
	
	public static final String XAKLAD2 = "xaklad2";
	public static final String AKLAD = "aklad";
	
	public static final String COOKIE_AKLADEMAT_CHORUS_TARGET = "akladematChorusTarget";
	
	public static final String PATTERN_DATE = "yyyy-MM-dd";
	public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	
	public static final int ORBEON_MAIL_WAITING_ACTION = 0;
	public static final int ORBEON_MAIL_INVALIDATION_FOR_OWNER = 1;
	public static final int ORBEON_MAIL_DG_VALIDATION_FOR_OWNER = 2;
	
}
