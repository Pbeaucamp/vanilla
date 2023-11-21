package bpm.vanillahub.web.client;

import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.workflow.commons.client.ILogout;
import bpm.gwt.workflow.commons.client.utils.Tools;
import bpm.vanillahub.web.client.services.AdminService;
import bpm.vanillahub.web.client.services.AdminServiceAsync;
import bpm.vanillahub.web.shared.Dummy;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class Bpm extends RootWaitPanel implements EntryPoint, ILogin, ILogout {

	private InfoUser infoUser;
	private Keycloak keycloak;

	public void onModuleLoad() {
		RootPanel.get("loading").setVisible(false);
		RootPanel.get("Login").setVisible(true);

		loadingCss();
		initSession();
		
		AdminService.Connect.getInstance().dummy(new Dummy(), new GwtCallbackWrapper<Dummy>(null, false, false) {

			@Override
			public void onSuccess(Dummy result) {
				// TODO Auto-generated method stub
				
			}
		}.getAsyncCallback());
	}
	
	private void initSession() {
		AdminService.Connect.getInstance().initSession(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				String actualUrl = Window.Location.getQueryString();

				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), Bpm.this, Bpm.this, "vanilla_webapps_welcome_vanillahub.png", false, false);
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
	public void login(final InfoUser infoUser, Keycloak keycloak) {
		showWaitPart(true);
		this.infoUser = infoUser;
		this.keycloak = keycloak;
		
		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);
		
		AdminService.Connect.getInstance().initVanillaHubSession(infoUser, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				buildContent(infoUser);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				showWaitPart(false);
			}
		});
	}

	private void buildContent(InfoUser result) {
		showWaitPart(true);
		
		RootPanel.get().clear();
		MainPanel mainPanel = new MainPanel(this, result);
		RootPanel.get().add(mainPanel);

		showWaitPart(false);
	}
	
	public InfoUser getInfoUser() {
		return infoUser;
	}

	public void setInfoUser(InfoUser infoUser) {
		this.infoUser = infoUser;
	}

	@Override
	public void onLogout() {
		LoginService.Connect.getInstance().disconnectUser(new AsyncCallback<Void>() {

			public void onFailure(Throwable arg0) {
				deconnect();
			}

			public void onSuccess(Void arg0) {
				deconnect();
			}
		});
	}
	
	private void deconnect() {
		try {
			String sessionID = Cookies.getCookie(CommonConstants.SID);
			if (sessionID != null) {
				Cookies.removeCookie(CommonConstants.SID, "/");
			}
		} catch(Exception e) { }
		
		if (keycloak != null) {
			keycloak.logout();
		}
			
		String urlDeconnect = infoUser.getUrlDeconnect();
		if(urlDeconnect == null || urlDeconnect.isEmpty()) {
			final String url = GWT.getHostPageBaseURL();
			bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
		}
		else {
			bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(urlDeconnect);
		}
	}

	private void loadingCss() {
		Tools.setLinkHref("loginCss", "cssHelper?loadLoginCss=true&fileName=login.css");
		Tools.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=vanilla_theme_default.css");
		Tools.setLinkHref("segmentCss", "cssHelper?loadLoginCss=true&fileName=semantic.min.css");
	}

	public native void doRedirect(String url)/*-{
		$wnd.open(url);
	}-*/;

	/**This method aim to dynamically change the current URL 
	 * @param url the new URL string
	 */
	public static native void changeCurrURL(String url)/*-{
    	$wnd.location.replace(url);
	}-*/;
}
