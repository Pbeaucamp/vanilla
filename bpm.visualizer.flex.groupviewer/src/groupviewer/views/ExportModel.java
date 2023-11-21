package groupviewer.views;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;

public class ExportModel {
	// Constants
	//private final static String PLUGIN_NAME = "GroupViewer";
	//private final static String CONF_FILE_PATH = "/property/FlexPropertys.xml";
	private final static String CONF_FILE_PATH = "resources/FlexPropertys.xml";
	private final static String APP_ROOT_PATH ="resources/bin-release/";
	// TAGS
	private final static String TAG_APPNAME="swf_file";
	//private final static String TAG_APPROOTPATH = "root_path";
	private final static String TAG_DATAPATH="data_path";
	private final static String TAG_DATANAME="data_name";
	private final static String TAG_HTMLFILE="html_file";
	// Data
	private String appName;
	private String appRootPath;
	private String appDataPath;
	private String appDataName;
	private String appHtmlPath;
	private boolean runApp = false;
	
	private Properties propertys;
	private static ExportModel instance = null;
	
	public static synchronized ExportModel getInstance(){
		if (instance == null){
			instance = new ExportModel();
		}
		return instance;
	}
	
	private ExportModel(){
		init();
	}
	public void init(){
		loadPropertys();
	}
	
	private void loadPropertys(){	
			try {
				propertys = new Properties();
				//URL url = Platform.getBundle(PLUGIN_NAME).getEntry(CONF_FILE_PATH);
				String url = Platform.getInstallLocation().getURL().getPath() + CONF_FILE_PATH;
				File prop = new File(url);
				if (prop.canRead()){
					propertys.loadFromXML(prop.toURI().toURL().openStream());
					setpropertys();
				}
			} catch (Exception e) {	
				e.printStackTrace();
			}	
	}

	private void setpropertys() {
		setAppDataPath((String)propertys.get(TAG_DATAPATH));
		//setAppRootPath((String)propertys.get(TAG_APPROOTPATH));
		setAppRootPath(Platform.getInstallLocation().getURL().getPath()+ APP_ROOT_PATH);
		setAppName((String)propertys.get(TAG_APPNAME));
		setAppHtmlPath((String) propertys.get(TAG_HTMLFILE));
		setAppDataName((String) propertys.get(TAG_DATANAME));
	}
	
	protected void setAppName(String appName) {
		this.appName = appName;
	}
	
	protected String getAppName() {
		return appName;
	}
	
	protected void setAppRootPath(String appRootPath) {
		this.appRootPath = appRootPath;
	}
	
	protected String getAppRootPath() {
		return appRootPath;
	}
	
	protected void setAppDataPath(String appDataPath) {
		this.appDataPath = appDataPath;
	}
	
	protected String getAppDataPath() {
		return appDataPath;
	}

	protected void setAppHtmlPath(String appHtmlPath) {
		this.appHtmlPath = appHtmlPath;
	}

	protected String getAppHtmlPath() {
		return appHtmlPath;
	}

	protected void setRunApp(boolean runApp) {
		this.runApp = runApp;
	}

	protected boolean isRunApp() {
		return runApp;
	}

	protected void setAppDataName(String appDataName) {
		this.appDataName = appDataName;
	}

	protected String getAppDataName() {
		return appDataName;
	}
	public URL getLaunchURL(){
		String loc = this.getAppRootPath()+ this.getAppHtmlPath();
		File file = new File(loc);
		if (file.exists()){
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return null;	
	}
	protected void save(){
		//propertys.
	}
}
