package bpm.es.web.client;

import bpm.es.web.client.panels.MainPanel;
import bpm.es.web.client.services.AdminService;
import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class EsWeb extends RootWaitPanel implements EntryPoint, ILogin {

	public enum Layout {
		MOBILE, TABLET, COMPUTER;
	}

	private static EsWeb instance;
	private MainPanel mainPanel;

	private Layout layout;

	public static EsWeb getInstance() {
		return instance;
	}

	public void onModuleLoad() {
		instance = this;

		RootPanel.get("loading").setVisible(false);
		RootPanel.get("Login").setVisible(true);

		loadingCss();
		initSession();

		manageLayout();
		Window.addResizeHandler(new ResizeHandler() {

			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					manageLayout();
				}
			};

			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.cancel();
				resizeTimer.schedule(250);
			}
		});
	}

	private void initSession() {
		showWaitPart(true);

		AdminService.Connect.getInstance().initSession(new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				String actualUrl = Window.Location.getQueryString();
				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), EsWeb.this, EsWeb.this, "lyon_enfance_logo.png", true, true);
				loginHelper.loginToApplication(actualUrl);

				// TODO: REMOVE
				// login(null);
			}
		}.getAsyncCallback());
	}

	private void loadingCss() {
		ToolsGWT.setLinkHref("loginCss", "cssHelper?loadLoginCss=true&fileName=login.css");
		ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=vanilla_theme_default.css");
		ToolsGWT.setLinkHref("segmentCss", "cssHelper?loadLoginCss=true&fileName=semantic.min.css");
	}

	@Override
	public void login(InfoUser infoUser, Keycloak keycloak) {
		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);
		this.mainPanel = new MainPanel(infoUser, keycloak);
		RootPanel.get().add(mainPanel);
	}

	private void manageLayout() {
		Layout newLayout = null;
		if (Window.getClientWidth() < 1024) {
			newLayout = Layout.MOBILE;
		}
		else if (Window.getClientWidth() < 1025) {
			newLayout = Layout.TABLET;
		}
		else {
			newLayout = Layout.COMPUTER;
		}
		if (newLayout != layout) {
			this.layout = newLayout;
			if (mainPanel != null) {
				mainPanel.manageLayout(layout);
			}
		}
	}

	public Layout getLayout() {
		return layout;
	}
}
