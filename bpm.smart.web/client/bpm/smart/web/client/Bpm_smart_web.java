package bpm.smart.web.client;

import java.util.List;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.workflow.commons.client.ILogout;
import bpm.gwt.workflow.commons.client.utils.Tools;
import bpm.smart.web.client.services.ConnectionServices;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class Bpm_smart_web extends RootWaitPanel implements EntryPoint, ILogin, ILogout {

	private InfoUser infoUser;
	private Keycloak keycloak;
	private static boolean copyEnabled;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
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

				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), Bpm_smart_web.this, Bpm_smart_web.this, "VanillaAir_Logo_Accueil.png", false, false);
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
//		ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=smart_theme.css");
		ToolsGWT.setLinkHref("segmentCss", "cssHelper?loadLoginCss=true&fileName=semantic.min.css");
		ToolsGWT.setLinkHref("olCss", "cssHelper?loadLoginCss=true&fileName=ol_css/ol.css");
		Tools.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=vanilla_theme_air.css");

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
		showWaitPart(true);
		this.infoUser = infoUser;
		this.keycloak = keycloak;
		
		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);
		
		ConnectionServices.Connection.getInstance().initSmartWebSession(infoUser, new AsyncCallback<UserSession>() {

			@Override
			public void onSuccess(UserSession result) {
				exec();
				result.setCopyPasteEnabled(copyEnabled);
				
				buildContent(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				showWaitPart(false);
			}
		});
	}

	private void buildContent(UserSession result) {
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

	private final native void exec() /*-{		
		try {
		   var successful = $doc.execCommand("copy");
		   var msg = successful ? 'successful' : 'unsuccessful';
		   
		   if(successful){
		   		console.log('The text command was ' + msg);
		   		$wnd.handleTestCopy = @bpm.smart.web.client.Bpm_smart_web::handleTestCopy(Ljava/lang/Boolean;)(@java.lang.Boolean::TRUE);
		   } else {
		   		$wnd.handleTestCopy = @bpm.smart.web.client.Bpm_smart_web::handleTestCopy(Ljava/lang/Boolean;)(@java.lang.Boolean::FALSE);
		   }
		   
		 } catch (err) {
		   	$wnd.handleTestCopy = @bpm.smart.web.client.Bpm_smart_web::handleTestCopy(Ljava/lang/Boolean;)(@java.lang.Boolean::FALSE);
		 }
	}-*/;
	
	static void handleTestCopy(Boolean enabled){
		copyEnabled = enabled;	
	}
}
