package bpm.fmloader.client;

import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.dialog.ErrorDialog;
import bpm.fmloader.client.infos.InfosUser;
import bpm.fmloader.client.panel.FMDataPanel;
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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FMLoaderWeb extends RootWaitPanel implements EntryPoint, ILogin {

	private Keycloak keycloak;
	
	public void onModuleLoad() {
		RootPanel.get("Login").setVisible(true);
//		RootPanel.get("main").getElement().getStyle().setHeight(100, Unit.PCT);
		
		loadingCss();
		initSession();
	}

	private void initSession() {
		Window.addCloseHandler(new CloseHandler<Window>() {
			public void onClose(CloseEvent<Window> event) {
				Constantes.DATAS_SERVICES.cleanJsps(new AsyncCallback<Void>() {
					public void onSuccess(Void result) { }
					
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
			}
		});
		
		Constantes.CONNECTION_SERVICES.initSession(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				String actualUrl = Window.Location.getQueryString();

				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), FMLoaderWeb.this, FMLoaderWeb.this, "fmloaderweb.png", false, false);
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
		this.keycloak = keycloak;
		
		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);
		
		showWaitPart(true);
		
		Constantes.CONNECTION_SERVICES.initFmSession(InfosUser.getInstance(), new AsyncCallback<InfosUser>(){
			
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				
				InformationsDialog dial = new InformationsDialog(Constantes.LBL.error(), "Ok", "Cause", Constantes.LBL.errorConnection() + "\n" + caught.getMessage() , caught);
				dial.center();
			}
			
			@Override
			public void onSuccess(InfosUser result) {
				
				InfosUser.getInstance().setUser(result.getUser());
				InfosUser.getInstance().setGroups(result.getGroups());
				InfosUser.getInstance().setCommentLimit(result.getCommentLimit());
				InfosUser.getInstance().setObservatories(result.getObservatories());
				InfosUser.getInstance().setSelectedGroup(result.getSelectedGroup());
				
				RootPanel.get("main").clear();
				
				Constantes.DATAS_SERVICES.getMetrics(InfosUser.getInstance(), new AsyncCallback<InfosUser>() {
					
					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);
						
						ErrorDialog dial = new ErrorDialog(Constantes.LBL.errorGetMetrics());
						dial.center();
						dial.show();
					}
		
					@Override
					public void onSuccess(InfosUser result) {
						showWaitPart(false);
						
						InfosUser.getInstance().setMetrics(result.getMetrics());
						InfosUser.getInstance().setApplications(result.getApplications());
						RootPanel.get("main").clear();
						RootPanel.get("main").add(new FMDataPanel(FMLoaderWeb.this, infoUser));
					}
				});
				
			}
		});
	}
	
	private void loadingCss() {
		ToolsGWT.setLinkHref("loginCss", "cssHelper?loadLoginCss=true&fileName=login.css");
		ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=kpi_theme.css");
		ToolsGWT.setLinkHref("segmentCss", "cssHelper?loadLoginCss=true&fileName=semantic.min.css");
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
