package bpm.gwt.commons.client.login;

import java.util.HashMap;

import javax.annotation.Nonnull;

import org.realityforge.gwt.keycloak.Keycloak;
import org.realityforge.gwt.keycloak.KeycloakListenerAdapter;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoConnection;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;

public class LoginHelper extends Composite {

	public static final String IS_AUTO_LOGIN = "bpm.vanilla.autoLogin";
	public static final String HASH_KEY = "bpm.vanilla.hash";
	private static final String SESSION_KEY = "bpm.vanilla.sessionId";
	private static final String REPOSITORY_KEY = "bpm.vanilla.repositoryId";
	private static final String GROUP_KEY = "bpm.vanilla.groupId";

	private RootPanel rootPanelLogin;
	private ILogin loginPanel;
	private IWait waitPanel;
	private String imgWebAppName;
	private boolean fromPortal;
	private boolean isComplete;

	public LoginHelper(RootPanel rootPanelLogin, ILogin loginPanel, IWait waitPanel, String imgWebAppName, boolean fromPortal, boolean isComplete) {
		this.rootPanelLogin = rootPanelLogin;
		this.loginPanel = loginPanel;
		this.waitPanel = waitPanel;
		this.imgWebAppName = imgWebAppName;
		this.fromPortal = fromPortal;
		this.isComplete = isComplete;
		
		loadCss();
	}

	public void loginToApplication(String actualUrl) {
		LoginService.Connect.getInstance().getConnectionProperties(new AsyncCallback<InfoConnection>() {

			@Override
			public void onFailure(Throwable caught) {
				parseUrlAndLogin(actualUrl, null);
			}

			@Override
			public void onSuccess(InfoConnection result) {
				parseUrlAndLogin(actualUrl, result);
			}
		});
	}
	
	private void checkKeycloak(InfoConnection result) {
		if (result!= null && result.useKeycloak()) {
			initFromKeycloak(result.getKeycloakKey(), result.getKeycloakConfigUrl());
		}
		else {
			rootPanelLogin.add(new ConnexionPanel(waitPanel, loginPanel, imgWebAppName, fromPortal, isComplete));
		}
	}
	
	private void parseUrlAndLogin(String actualUrl, InfoConnection infoConnection) {
		HashMap<String, String> props = parseUrl(actualUrl);

		if (props.get(HASH_KEY) != null) {
			String hash = props.get(HASH_KEY);
			
			if (props.get(IS_AUTO_LOGIN) != null) {
				rootPanelLogin.add(new ConnexionPanel(waitPanel, loginPanel, imgWebAppName, fromPortal, isComplete, hash));
			}
			else {
				rootPanelLogin.add(new PasswordRecoveryPanel(waitPanel, hash));
			}
		}
		else if (props.get(SESSION_KEY) == null) {
			String sessionID = null;
			try {
				sessionID = Cookies.getCookie(CommonConstants.SID);
			} catch(Exception e) { }
			
			if (sessionID != null) {
				checkWithServerIfSessionIdIsStillLegal(sessionID, infoConnection);
			}
			else {
				checkKeycloak(infoConnection);
			}
		}
		else {
			String sessionId = props.get(SESSION_KEY);
			int repoId = -1;
			int groupId = -1;
			try {
				repoId = Integer.parseInt(props.get(REPOSITORY_KEY));
				groupId = Integer.parseInt(props.get(GROUP_KEY));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (repoId != -1 && groupId != -1) {
				initFromPortal(sessionId, groupId, repoId, infoConnection);
			}
			else {
				InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.ErrorDetails(), "Group ID or Repository ID are not correctly formed.", false);
				dial.center();
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						checkKeycloak(infoConnection);
					}
				});
			}
		}
	}

	private void checkWithServerIfSessionIdIsStillLegal(String sessionId, InfoConnection infoConnection) {
		waitPanel.showWaitPart(true);
		LoginService.Connect.getInstance().checkCookieSession(sessionId, new AsyncCallback<InfoUser>() {

			@Override
			public void onFailure(Throwable arg0) {
				waitPanel.showWaitPart(false);
				checkKeycloak(infoConnection);
			}

			@Override
			public void onSuccess(InfoUser result) {
				waitPanel.showWaitPart(false);
				if (result != null) {
					loginPanel.login(result, null);
				}
				else {
					checkKeycloak(infoConnection);
				}
			}

		});
	}

	private void initFromPortal(String sessionId, int groupId, int repositoryId, InfoConnection infoConnection) {
		LoginService.Connect.getInstance().initFromPortal(sessionId, groupId, repositoryId, new AsyncCallback<InfoUser>() {

			@Override
			public void onFailure(Throwable arg0) {
				checkKeycloak(infoConnection);
			}

			@Override
			public void onSuccess(InfoUser result) {
				if (result != null) {
					loginPanel.login(result, null);
				}
				else {
					checkKeycloak(infoConnection);
				}
			}

		});
	}

	private void initFromKeycloak(String key, String configUrl) {
		waitPanel.showWaitPart(true);
		
		try {
			final Keycloak keycloak = new Keycloak(key, configUrl);
			keycloak.addKeycloakListener(new KeycloakListenerAdapter() {
				@Override
				public void onReady(@Nonnull final Keycloak keycloak, final boolean authenticated) {
					waitPanel.showWaitPart(false);
					if (authenticated) {
						rootPanelLogin.add(new ConnexionPanel(waitPanel, loginPanel, imgWebAppName, fromPortal, isComplete, null, keycloak));
					}
					else {
						keycloak.login();
					}
				}
				
				@Override
				public void onAuthError(Keycloak keycloak) {
					super.onAuthError(keycloak);
					waitPanel.showWaitPart(false);
					rootPanelLogin.add(new ConnexionPanel(waitPanel, loginPanel, imgWebAppName, fromPortal, isComplete));
				}
			});
			keycloak.init();
		} catch(Exception e) {
			e.printStackTrace();
			waitPanel.showWaitPart(false);
			rootPanelLogin.add(new ConnexionPanel(waitPanel, loginPanel, imgWebAppName, fromPortal, isComplete));
		}
	}

	/**
	 * Get URL and chops it into: Key/Values bpm.vanilla.sessionId/session
	 * bpm.vanilla.groupId/groupId bpm.vanilla.repositoryId/repositoryId
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
				props.put(tmp[0], tmp[1]);
			}
		} catch (Exception e) {
			System.out.println("error parsing : " + location);
			// do nothing
		}

		return props;
	}
	
	private void loadCss() {
		ToolsGWT.setLinkHref("circleCss", "cssHelper?loadLoginCss=true&fileName=circle.css");
	}
}
