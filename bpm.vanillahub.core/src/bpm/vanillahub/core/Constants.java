package bpm.vanillahub.core;

public class Constants {
	
	public static final String SERVLET_ADMIN_MANAGER = "/hubAdminServlet";
	
	public static final String FILES = "Files/";
	
	public static final String TWITTER_KEY = "kmp4l9OqjyqXd5lFuikPyMVi9";
	public static final String TWITTER_SECRET = "Aq07bYFNidwf3q53OVKTsq2vhRNDrWzhCmaS7j96yd4AOlXmJV";
	
	public static final String GOOGLE_API_FREEBASE_URL = "https://www.googleapis.com/freebase/v1/search";
	//If it does not work, create a new project and a new api key
	//Not used directly
//	public static final String YOUTUBE_KEY = "";
	public static final long YOUTUBE_DEFAULT_NB_VIDEOS = 50;

	public static final String SID = "SID";
	public static final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	
	public static final String KEY_GO_BACK_PREVIOUS = "GoBackPrevious";
	public static final String KEY_UPDATE_FILE_NAME = "UpdateFileName";
	
	public static final String MAIL_TARGET_USERNAME = "{TARGET_USERNAME}";
	public static final String MAIL_WORKFLOW_NAME = "{WORKFLOW_NAME}";
	public static final String MAIL_CURRENT_USERNAME = "{CURRENT_USERNAME}";
	public static final String MAIL_START_DATE = "{START_DATE}";
	public static final String MAIL_END_DATE = "{END_DATE}";
	public static final String MAIL_HAS_ERRORS = "{HAS_ERRORS}";
	
	public static final String XML = "xml";
	public static final String CSV = "csv";
	public static final String JSON = "json";
	
	public static final String NUTCH_SERVER_STATUS = "/admin";
	public static final String NUTCH_JOB_STATUS = "/job";
	public static final String NUTCH_SEED = "/seed/create";
	public static final String NUTCH_JOB = "/job/create";
	
	public static final String JOB_STATUS_MSG = "msg";
	public static final String JOB_STATUS_STATE = "state";
	
	public static final String CRAWL_STORAGE_FOLDER = "temp/storage";
	
	public static final int TWITTER_METHOD_SEARCH = 0;
	public static final int TWITTER_METHOD_TIMELINE = 1;
	
	public static final int YOUTUBE_METHOD_SEARCH_KEYWORD = 0;
	public static final int YOUTUBE_METHOD_SEARCH_TOPIC = 1;
}
