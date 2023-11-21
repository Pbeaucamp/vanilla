package bpm.map.viewer.web.client;

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;
import bpm.map.viewer.web.client.panel.MainPanel;
//import bpm.vanilla.portal.client.services.AdminService;
//import bpm.vanilla.portal.client.services.BiPortalService;
//import bpm.vanilla.portal.client.services.SecurityService;
import bpm.map.viewer.web.client.services.ConnectionServices;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Bpm_map_viewer_web extends RootWaitPanel implements EntryPoint, ILogin {
	
	private static Bpm_map_viewer_web instance;
	private int idMap = 0;

	public static Bpm_map_viewer_web get() {
		return instance;
	}
	
	public void onModuleLoad() {
		instance = this;

		RootPanel.get("loading").setVisible(false);
		RootPanel.get("Login").setVisible(true);

		loadingCss();
		initSession();
	}
	
	private void initSession() {
		ConnectionServices.Connection.getInstance().initSession(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				String actualUrl = Window.Location.getQueryString();

				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), Bpm_map_viewer_web.this, Bpm_map_viewer_web.this, "VanillaMap_Logo_Accueil.png", false, false);
				loginHelper.loginToApplication(actualUrl);
				
				HashMap<String, String> props = parseUrl(actualUrl);
				if (props.get("bpm.kpi.idMap") != null) {
					idMap = Integer.parseInt(props.get("bpm.kpi.idMap"));
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				MessageHelper.openMessageError("The Web Application is not available. Please, check the log for more informations.", caught);
			}
		});
	}

	private void loadingCss() {
		ToolsGWT.setLinkHref("loginCss", "cssHelper?loadLoginCss=true&fileName=login.css");
		ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=map_viewer_theme.css");
		ToolsGWT.setLinkHref("segmentCss", "cssHelper?loadLoginCss=true&fileName=semantic.min.css");
		ToolsGWT.setLinkHref("olCss", "cssHelper?loadLoginCss=true&fileName=ol_css/ol.css");
		
		CommonService.Connect.getInstance().getCommonJavascriptFiles(new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				ToolsGWT.addScriptFiles(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	@Override
	public void login(InfoUser infoUser) {
		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);
		
		showWaitPart(true);
		ConnectionServices.Connection.getInstance().initMapViewerSession(new AsyncCallback<UserSession>() {

			@Override
			public void onSuccess(UserSession result) {
				UserSession.getInstance().setMetrics(result.getMetrics());
				UserSession.getInstance().setGroups(result.getGroups());
				UserSession.getInstance().setObservatories(result.getObservatories());
				UserSession.getInstance().setDefaultDate(result.getDefaultDate());
				UserSession.getInstance().setIconSet(result.getIconSet());
				UserSession.getInstance().setWebappUrl(result.getWebappUrl());
				RootPanel.get().clear();
				showWaitPart(true);
				buildContent();

				showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				showWaitPart(false);
			}
		});
	}
	
	private void buildContent() {
		MainPanel mainPanel = new MainPanel();
		RootPanel.get().add(mainPanel);


	}

	public void onLogout() {
		//this.infoUser = null;
		//this.contentRepository = null;

		String url = Window.Location.getHref();
		bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
	}

	private HashMap<String, String> parseUrl(String location) {
		HashMap<String, String> props = new HashMap<String, String>();
		try {
			location = location.split("\\?")[1];
			String[] couples = location.split("&");

			String[] tmp;

			for (int i = 0; i < couples.length; i++) {
				tmp = couples[i].split("=");
				props.put(tmp[0], tmp[1]);
			}
		} catch (Exception e) {
			System.out.println("error parsing : " + location);
			// do nothing
		}

		return props;
	}
	
	public int getIdMap() {
		return idMap;
	}

	public void setIdMap(int idMap) {
		this.idMap = idMap;
	}

}	
