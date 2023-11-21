package bpm.fm.designer.web.client;

import bpm.fm.designer.web.client.panel.MainPanel;
import bpm.fm.designer.web.client.services.ConnectionServices;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class FMDesignerWeb extends RootWaitPanel implements EntryPoint, ILogin {
	
	private static FMDesignerWeb instance;
	
	private Keycloak keycloak;
	
	public static FMDesignerWeb getInstance() {
		return instance;
	}
	
	public void onModuleLoad() {
		instance = this;
		
		RootPanel.get("Login").setVisible(true);

		loadingCss();
		initSession();
	}

	private void initSession() {
		ConnectionServices.Connection.getInstance().initSession(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				String actualUrl = Window.Location.getQueryString();

				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), FMDesignerWeb.this, FMDesignerWeb.this, "fmdesignerweb.png", false, false);
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
	}

	@Override
	public void login(InfoUser infoUser, Keycloak keycloak) {
		this.keycloak = keycloak;
		
		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);
		
		showWaitPart(true);
		
		ConnectionServices.Connection.getInstance().initFmSession(new AsyncCallback<ClientSession>() {
			@Override
			public void onSuccess(ClientSession result) {
				ClientSession.getInstance().setAxis(result.getAxis());
				ClientSession.getInstance().setMetrics(result.getMetrics());
				ClientSession.getInstance().setObservatories(result.getObservatories());
				ClientSession.getInstance().setDatasets(result.getDatasets()); //kevin
				ClientSession.getInstance().setMaps(result.getMaps()); //kevin
				ClientSession.getInstance().setGroup(result.getGroup()); //kevin
				ClientSession.getInstance().setLogin(result.getLogin());
				ClientSession.getInstance().setPassword(result.getPassword());
				ClientSession.getInstance().setVanillaUrl(result.getVanillaUrl());
				
				showWaitPart(false);

				RootPanel.get().clear();
				RootPanel.get().add(new MainPanel());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				
				caught.printStackTrace();
				
				InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), "Ok", Messages.lbl.errorOnConnection(), Messages.lbl.errorOnConnection(), caught);
				dial.center();
			}
		});
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
}
