package bpm.update.manager.client;

import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;
import bpm.update.manager.client.services.UpdateService;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class UpdateManager extends RootWaitPanel implements EntryPoint, ILogin {

	@Override
	public void onModuleLoad() {

		RootPanel.get("loading").setVisible(false);
		RootPanel.get("Login").setVisible(true);

		loadingCss();
		initSession();
	}

	private void initSession() {
		UpdateService.Connect.getInstance().initSession(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				String actualUrl = Window.Location.getQueryString();
				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), UpdateManager.this, UpdateManager.this, "updatemanager.png", true, false);
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
		ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=vanilla_theme_default.css");
		ToolsGWT.setLinkHref("segmentCss", "cssHelper?loadLoginCss=true&fileName=semantic.min.css");
	}

	@Override
	public void login(InfoUser infoUser, Keycloak keycloak) {
		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);
		
		UpdatePanel mainPanel = new UpdatePanel(this);
		RootPanel.get().add(mainPanel);
	}
}
