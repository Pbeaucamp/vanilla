package bpm.gwt.aklabox.commons.client.login;

import java.util.HashMap;

import bpm.gwt.aklabox.commons.client.loading.IWait;
import bpm.gwt.aklabox.commons.client.services.LoginService;
import bpm.gwt.aklabox.commons.shared.CommonConstants;
import bpm.gwt.aklabox.commons.shared.InfoUser;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

public class LoginHelper extends Composite {

	private static final String SESSION_KEY = "bpm.aklabox.sessionId";
//	private static final String ROLE_KEY = "bpm.aklabox.roleKey";

	private RootPanel rootPanelLogin;
	private ILogin loginPanel;
	private IWait waitPanel;
	private ImageResource imgWebApp;
	private boolean fromPortal;

	public LoginHelper(RootPanel rootPanelLogin, ILogin loginPanel, IWait waitPanel, ImageResource imgWebApp, boolean fromPortal) {
		this.rootPanelLogin = rootPanelLogin;
		this.loginPanel = loginPanel;
		this.waitPanel = waitPanel;
		this.imgWebApp = imgWebApp;
		this.fromPortal = fromPortal;
	}

	public void loginToApplication(String actualUrl) {
		HashMap<String, String> props = parseUrl(actualUrl);

		if (props.get(SESSION_KEY) == null) {
			String sessionID = null;
			try {
				sessionID = Cookies.getCookie(CommonConstants.SID);
			} catch(Exception e) { }
			
			if (sessionID != null) {
				checkWithServerIfSessionIdIsStillLegal(sessionID);
			}
			else {
				rootPanelLogin.add(new ConnexionPanel(waitPanel, loginPanel, imgWebApp, fromPortal));
			}
		}
		else {
			String sessionId = props.get(SESSION_KEY);
//			initFromPortal(sessionId, groupId, repoId);
		}
	}

	private void checkWithServerIfSessionIdIsStillLegal(String sessionId) {
		waitPanel.showWaitPart(true);
		LoginService.Connect.getService().checkCookieSession(sessionId, new AsyncCallback<InfoUser>() {

			@Override
			public void onFailure(Throwable arg0) {
				waitPanel.showWaitPart(false);
				rootPanelLogin.add(new ConnexionPanel(waitPanel, loginPanel, imgWebApp, fromPortal));
			}

			@Override
			public void onSuccess(InfoUser result) {
				waitPanel.showWaitPart(false);
				if (result != null) {
					loginPanel.login(result);
				}
				else {
					rootPanelLogin.add(new ConnexionPanel(waitPanel, loginPanel, imgWebApp, fromPortal));
				}
			}

		});
	}

//	private void initFromPortal(String sessionId, int groupId, int repositoryId) {
//		LoginService.Connect.getInstance().initFromPortal(sessionId, groupId, repositoryId, new AsyncCallback<InfoUser>() {
//
//			@Override
//			public void onFailure(Throwable arg0) {
//				// rootPanelLogin.add(new LoginPanel(loginPanel, waitPanel,
//				// imgWebAppName, fromPortal));
//				rootPanelLogin.add(new ConnexionPanel(waitPanel, loginPanel, imgWebAppName, fromPortal, isComplete));
//			}
//
//			@Override
//			public void onSuccess(InfoUser result) {
//				if (result != null) {
//					loginPanel.login(result);
//				}
//				else {
//					// rootPanelLogin.add(new LoginPanel(loginPanel, waitPanel,
//					// imgWebAppName, fromPortal));
//					rootPanelLogin.add(new ConnexionPanel(waitPanel, loginPanel, imgWebAppName, fromPortal, isComplete));
//				}
//			}
//
//		});
//	}

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
}
