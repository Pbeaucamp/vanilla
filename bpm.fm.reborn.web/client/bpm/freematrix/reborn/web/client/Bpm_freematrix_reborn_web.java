package bpm.freematrix.reborn.web.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import bpm.freematrix.reborn.web.client.dialog.WaitDialog;
import bpm.freematrix.reborn.web.client.main.FreeMatrixMain;
import bpm.freematrix.reborn.web.client.services.ConnectionServices;
import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;

public class Bpm_freematrix_reborn_web extends RootWaitPanel implements EntryPoint, ILogin {

	private DateTimeFormat df = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
	
	private static final String THEME_KEY = "bpm.vanilla.theme.id";
	private static final String GROUP_KEY = "bpm.vanilla.groupId";
	private static final String DATE = "date";
	private static final String VIEWER = "viewer";

	private Integer themeId;
	private Integer groupId;
	private Date selectedDate;
	private boolean viewer = false;
	
	private Keycloak keycloak;

	public void onModuleLoad() {
		ClientSession.clear();

		RootPanel.get("Login").setVisible(true);

		loadingCss();
		initSession();
	}

	private void initSession() {
		ConnectionServices.Connection.getInstance().initSession(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				String actualUrl = Window.Location.getQueryString();
				parseUrlToOpenTheme(actualUrl);

				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), Bpm_freematrix_reborn_web.this, Bpm_freematrix_reborn_web.this, "fmuserweb.png", false, false);
				loginHelper.loginToApplication(actualUrl);
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
		ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=kpi_theme.css");
		ToolsGWT.setLinkHref("segmentCss", "cssHelper?loadLoginCss=true&fileName=semantic.min.css");

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
	public void login(InfoUser infoUser, Keycloak keycloak) {
		this.keycloak = keycloak;
		
		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);

		WaitDialog.showWaitPart(true);
		ConnectionServices.Connection.getInstance().initFmSession(new AsyncCallback<ClientSession>() {

			@Override
			public void onSuccess(ClientSession result) {
				ClientSession.getInstance().setMetrics(result.getMetrics());
				ClientSession.getInstance().setDrivers(result.getDrivers());
				ClientSession.getInstance().setGroups(result.getGroups());
				ClientSession.getInstance().setObservatories(result.getObservatories());
				ClientSession.getInstance().setDefaultDate(result.getDefaultDate());
				ClientSession.getInstance().setConnectedToCkan(result.isConnectedToCkan());

				RootPanel.get().clear();
				WaitDialog.showWaitPart(true);
				RootPanel.get().add(new FreeMatrixMain(Bpm_freematrix_reborn_web.this, themeId, groupId, selectedDate, viewer));
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				WaitDialog.showWaitPart(false);
			}
		});
	}

	private void parseUrlToOpenTheme(String actualUrl) {
		HashMap<String, String> props = parseUrl(actualUrl);

		this.themeId = null;
		this.groupId = null;
		this.selectedDate = null;
		this.viewer = false;
		
		if (props.get(THEME_KEY) != null) {
			try {
				this.themeId = Integer.parseInt(props.get(THEME_KEY));
			} catch (Exception e) { }
		}
		
		if (props.get(GROUP_KEY) != null) {
			try {
				this.groupId = Integer.parseInt(props.get(GROUP_KEY));
			} catch (Exception e) { }
		}

		if (props.get(VIEWER) != null) {
			try {
				this.viewer = Boolean.parseBoolean(props.get(VIEWER));
			} catch (Exception e) { }
		}

		if (props.get(DATE) != null) {
			try {
				this.selectedDate = df.parse(props.get(DATE));
			} catch (Exception e) { }
		}
	}

	public void onLogout() {
		try {
			String sessionID = Cookies.getCookie(CommonConstants.SID);
			if (sessionID != null) {
				Cookies.removeCookie(CommonConstants.SID, "/");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (keycloak != null) {
			keycloak.logout();
		}
		
		String url = GWT.getHostPageBaseURL();
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
				String val = URL.decodeQueryString(tmp[1]);
				props.put(tmp[0], val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return props;
	}
}
