package bpm.faweb.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.faweb.client.I18N.LabelsConstants;
import bpm.faweb.client.services.FaWebService;
import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class FreeAnalysisWeb extends RootWaitPanel implements EntryPoint, ILogin {
	
	public enum TypeDisplay {
		DISCO,
		VIEWER,
		DASHBOARD,
		NORMAL
	}
	
	private static final String FASD_KEY = "bpm.vanilla.fasd.id";
	private static final String CUBENAME_KEY = "bpm.vanilla.cubename";
	private static final String VIEW_KEY = "bpm.vanilla.viewname";
	private static final String DIM_NAME = "bpm.vanilla.dimension";
	private static final String FILTER_NAME = "bpm.vanilla.filters";
	private static final String DISCO = "disco";
	private static final String DIMENSIONS_PANEL = "dimensionpanel";
	private static final String FUNCTIONS_TOOLS = "functionstools";
	private static final String DIMENSIONS_LISTE= "dimensionsliste";
	private static final String FMDTWEB_CUBE ="fmdtwebcube";
	private static final String SESSION_KEY = "bpm.vanilla.sessionId";

	public static final String VIEWER = "viewer";
	

	public static final LabelsConstants LBL = (LabelsConstants) GWT.create(LabelsConstants.class);

	private Integer fasdId;
	private String cubeName, viewName, dimName, memberName, sessionId;
	private static boolean hostedMode = !GWT.isScript() && GWT.isClient();
	private int keySession;
	private boolean viewer = false;
	private boolean disco = false;
	
	private boolean callbydasboard =false;
	private boolean dimPanel = false;
	private boolean tools = false;
	private String[] unames=null;
	private boolean fmdtWebCube=false;
	

	public void onModuleLoad() {
		RootPanel.get("loading").setVisible(false);
		RootPanel.get("Login").setVisible(true);
		
		initApplication();
		loadingTheme();
		initSession();
	}
	
	public static String getRightPath(String path) {
		path = path.replace("\\", "/");
		if(hostedMode) {
			return path;
		}
		else {
			return path.replace("webapps", "..");
		}
		
	}
	
	private void initApplication() {

		Window.addCloseHandler(new CloseHandler<Window>() {
			
			@Override
			public void onClose(CloseEvent<Window> event) {
				
				FaWebService.Connect.getInstance().closeCube(keySession, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(Void result) { }
				});
			}
		});

		Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
			
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONCONTEXTMENU) {
					event.cancel();
					event.getNativeEvent().preventDefault();
				}
			}
		});
	}

	private void initSession() {
		FaWebService.Connect.getInstance().initSession(new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				keySession = result;
				
				String actualUrl = Window.Location.getQueryString();
				parseUrlToOpenCube(actualUrl);
				
				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), FreeAnalysisWeb.this, FreeAnalysisWeb.this, "fa_web.png", false, true);
				loginHelper.loginToApplication(actualUrl);
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				
				MessageHelper.openMessageError("The Web Application is not available. Please, check the log for more informations.", caught);
			}
		});
	}

	@Override
	public void login(InfoUser infoUser, Keycloak keycloak) {
		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);
		
		TypeDisplay display = TypeDisplay.NORMAL;
		if(disco) {
			display = TypeDisplay.DISCO;
		}
		if(callbydasboard) {
			display = TypeDisplay.DASHBOARD;
		}
		if(viewer) {
			display = TypeDisplay.VIEWER;
		}
		
		RootPanel.get().add(new MainPanel(keySession, display, cubeName, infoUser, fasdId, viewName, dimName, memberName, dimPanel, unames, tools, fmdtWebCube, sessionId, keycloak));
	}
	
	private void parseUrlToOpenCube(String actualUrl) {

		HashMap<String, String> props = parseUrl(actualUrl);

		fasdId = -1;
		cubeName = "";
		viewName = "";
		dimName = "";
		memberName = "";
		if (props.get(FASD_KEY) != null && props.get(CUBENAME_KEY) != null && props.get(VIEW_KEY) != null) {
			try {
				fasdId = Integer.parseInt(props.get(FASD_KEY));
			} catch (Exception e) {
				e.printStackTrace();
			}
			cubeName = props.get(CUBENAME_KEY);
			viewName = props.get(VIEW_KEY);// .replace(" ", "");
		}
		else if (props.get(FASD_KEY) != null && props.get(CUBENAME_KEY) != null) {
			try {
				fasdId = Integer.parseInt(props.get(FASD_KEY));
			} catch (Exception e) {
				e.printStackTrace();
			}
			cubeName = props.get(CUBENAME_KEY);
		}
		else if (props.get(FASD_KEY) != null) {
			try {
				fasdId = Integer.parseInt(props.get(FASD_KEY));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(props.get(VIEWER) != null) {
			try {
				viewer = Boolean.parseBoolean(props.get(VIEWER));
			} catch (Exception e) { }
		}
		
		if(props.get(DISCO) != null) {
			try {
				disco = Boolean.parseBoolean(props.get(DISCO));
			} catch (Exception e) { }
		}
		
		if (props.get(DIM_NAME) != null && props.get(FILTER_NAME) != null) {
			dimName = props.get(DIM_NAME);
			memberName = props.get(FILTER_NAME);
		}
		if (props.get(DIMENSIONS_PANEL) != null && props.get(FUNCTIONS_TOOLS) != null) {
			callbydasboard=true;
			dimPanel =  Boolean.parseBoolean(props.get(DIMENSIONS_PANEL));
			tools =  Boolean.parseBoolean(props.get(FUNCTIONS_TOOLS));
		}
		if (dimPanel){
			if(props.get(DIMENSIONS_LISTE)!=null)
			unames = props.get(DIMENSIONS_LISTE).split(";"); 
		}
		if(props.get(FMDTWEB_CUBE)!=null){
			fmdtWebCube= Boolean.parseBoolean(props.get(FMDTWEB_CUBE));
			if( props.get(SESSION_KEY)!=null)
			sessionId = props.get(SESSION_KEY);
		}
	}

	/**
	 * Get URL and chops it into: Key/Values bpm.vanilla.sessionId/session
	 * bpm.vanilla.groupId/groupId bpm.vanilla.repositoryId/repositoryId
	 * bpm.vanilla.fasd.id/fasdId bpm.vanilla.cubename/cubename
	 * bpm.vanilla.viewname/viewname
	 * 
	 * @param location
	 * @return properties
	 * 
	 */
	private HashMap<String, String> parseUrl(String location) {
		HashMap<String, String> props = new HashMap<String, String>();
		try {
			location = location.split("\\?")[1];
			String[] couples = location.split("&");

			String[] tmp;

			for (int i = 0; i < couples.length; i++) {
				tmp = couples[i].split("=");
				String val = URL.decodeQueryString(tmp[1]);
				props.put(tmp[0], val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return props;
	}
	
	private void loadingTheme() {
		ToolsGWT.setLinkHref("loginCss", "cssHelper?loadLoginCss=true&fileName=login.css");
		ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=vanilla_theme_default.css");
		ToolsGWT.setLinkHref("segmentCss", "cssHelper?loadLoginCss=true&fileName=semantic.min.css");
		
		CommonService.Connect.getInstance().getCommonJavascriptFiles(new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				List<String> toRm = new ArrayList<String>();
				for(String res : result) {
					if(res.contains("OpenLayers")) {
						toRm.add(res);
					}
				}
				result.removeAll(toRm);
				try {
					result.add(result.get(0).substring(0, result.get(0).indexOf("freedashboardRuntime") + 21) + "Ol465/ol.js");
				} catch(Exception e) {
				}
				ToolsGWT.addScriptFiles(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}
}
