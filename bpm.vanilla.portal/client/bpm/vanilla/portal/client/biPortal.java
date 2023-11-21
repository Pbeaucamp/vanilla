/*
 * Copyright 2007 BPM-conseil.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package bpm.vanilla.portal.client;

import java.util.ArrayList;
import java.util.List;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.Notification;
import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ChangePasswordDialogOld;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.services.exception.SecurityException;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.portal.client.panels.BackgroundReportNotificationPanel;
import bpm.vanilla.portal.client.panels.CommentsNotificationPanel;
import bpm.vanilla.portal.client.panels.MainPanel;
import bpm.vanilla.portal.client.services.AdminService;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.services.SecurityService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

public class biPortal extends RootWaitPanel implements EntryPoint, ILogin {
	private static biPortal instance;

	private PortailRepositoryDirectory contentRepository;
	private InfoUser infoUser;
	private Keycloak keycloak;

	private List<String> icons;
	
	private int nbCheck = 1;
	private int currentCheck;

	public static biPortal get() {
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
		SecurityService.Connect.getInstance().initSession(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				String actualUrl = Window.Location.getQueryString();
				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), biPortal.this, biPortal.this, "vanilla.png", true, true);
				loginHelper.loginToApplication(actualUrl);
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

//				ExceptionManager.getInstance().handleException(caught, "The Web Application is not available. Please, check the log for more informations.");
				ExceptionManager.getInstance().handleException(new SecurityException(SecurityException.ERROR_TYPE_SESSION_EXPIRED, "Session is expired."), "The Web Application is not available. Please, check the log for more informations.");
			}
		});
	}

	@Override
	public void login(InfoUser infoUser, Keycloak keycloak) {
		this.infoUser = infoUser;
		this.keycloak = keycloak;

		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);

		SecurityService.Connect.getInstance().initHubSession(infoUser, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				buildContent();
				
				// check is the user have to change is password
				LoginService.Connect.getInstance().isUserChangePassword(new GwtCallbackWrapper<Boolean>(biPortal.this, true, true) {

					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							ChangePasswordDialogOld dial = new ChangePasswordDialogOld("", false);
							dial.center();
						}
					}
				}.getAsyncCallback());
			}
		}.getAsyncCallback());
	}

	private void buildContent() {
		MainPanel mainPanel = new MainPanel();
		RootPanel.get().add(mainPanel);

		Window.addCloseHandler(closeHandler);

		checkItemsOnOpen(mainPanel);
		checkMyWatchlist();
		checkReportBackgrounds(mainPanel, 1);
		checkComments(mainPanel);
	}

	public void checkItemsOnOpen(final MainPanel mainPanel) {
		showWaitPart(true);

		BiPortalService.Connect.getInstance().getOpenItems(new AsyncCallback<PortailRepositoryDirectory>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				showWaitPart(false);

				ExceptionManager.getInstance().handleException(caught, bpm.vanilla.portal.client.utils.ToolsGWT.lblCnst.Error());
			}

			@Override
			public void onSuccess(PortailRepositoryDirectory item) {
				infoUser.setOpenOnStartup(item);

				if (item != null && item.getItems() != null) {
					for (IRepositoryObject obj : item.getItems()) {
						mainPanel.getContentDisplayPanel().openViewer(obj);
					}
				}
			}

		});
	}

	public void checkMyWatchlist() {
		showWaitPart(true);

		BiPortalService.Connect.getInstance().getMyWatchList(new AsyncCallback<PortailRepositoryDirectory>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				showWaitPart(false);

				ExceptionManager.getInstance().handleException(caught, bpm.vanilla.portal.client.utils.ToolsGWT.lblCnst.Error());
			}

			@Override
			public void onSuccess(PortailRepositoryDirectory item) {
				infoUser.setMyWatchlist(item);
			}
		});
	}

	public void checkReportBackgrounds(MainPanel mainPanel, int nbCheck) {
		this.nbCheck = nbCheck;
		this.currentCheck = 0;
		
		checkReportBackgrounds(mainPanel);
	}

	private void checkReportBackgrounds(final MainPanel mainPanel) {
		infoUser.setWaitReportBackground(false);
		currentCheck++;
		
		ReportingService.Connect.getInstance().getBackgroundReports(15, new GwtCallbackWrapper<List<IRepositoryObject>>(this, false, false) {
			
			@Override
			public void onSuccess(List<IRepositoryObject> result) {
				List<ReportBackground> newReports = new ArrayList<ReportBackground>();

				if (result != null) {
					for (IRepositoryObject object : result) {
						ReportBackground report = (ReportBackground) object;
						if (report.isNew()) {
							newReports.add(report);
						}
						else if (report.isRunning()) {
							infoUser.setWaitReportBackground(true);
						}
					}
				}

				if (newReports != null && !newReports.isEmpty()) {
					BackgroundReportNotificationPanel backgroundNotificationPanel = new BackgroundReportNotificationPanel(mainPanel.getNavigation(), newReports);
					
					Notification notification = new Notification(backgroundNotificationPanel);
					notification.show(-1);
				}

				PortailRepositoryDirectory reportBackgrounds = new PortailRepositoryDirectory(ToolsGWT.lblCnst.ReportBackgrounds());
				infoUser.setReportBackgrounds(reportBackgrounds);
				
				if (currentCheck < nbCheck || infoUser.isWaitReportBackground()) {
					Timer timer = new Timer() {
						@Override
						public void run() {
							checkReportBackgrounds(mainPanel);
						}
					};

					timer.schedule(30000);
				}
			}
		}.getAsyncCallback());
	}

	public void checkComments(final MainPanel mainPanel) {
		ReportingService.Connect.getInstance().getPendingItemsToComment(new GwtCallbackWrapper<List<PortailRepositoryItem>>(this, false, false) {
			
			@Override
			public void onSuccess(List<PortailRepositoryItem> result) {
				if (result != null && !result.isEmpty()) {
					CommentsNotificationPanel notificationPanel = new CommentsNotificationPanel(mainPanel.getContentDisplayPanel(), result);
					
					Notification notification = new Notification(notificationPanel);
					notification.show(-1);
				}
			}
		}.getAsyncCallback());
	}

//	public void onLogout() {
//		this.infoUser = null;
//		this.contentRepository = null;
//
//		String url = Window.Location.getHref();
//		bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
//	}

	private CloseHandler<Window> closeHandler = new CloseHandler<Window>() {

		@Override
		public void onClose(CloseEvent<Window> event) {
			AdminService.Connect.getInstance().close(new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(Void arg0) {
				}
			});
		}
	};

	private void loadingCss() {
		bpm.gwt.commons.client.utils.ToolsGWT.setLinkHref("loginCss", "cssHelper?loadLoginCss=true&fileName=login.css");
		bpm.gwt.commons.client.utils.ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=vanilla_theme_default.css");
		bpm.gwt.commons.client.utils.ToolsGWT.setLinkHref("segmentCss", "cssHelper?loadLoginCss=true&fileName=semantic.min.css");

		CommonService.Connect.getInstance().getCommonJavascriptFiles(new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				List<String> toRm = new ArrayList<String>();
				for(String res : result) {
					if(res.contains("OpenLayers")) {
						toRm.add(res);
					}
				}
				result.removeAll(toRm);
				result.add(result.get(0).substring(0, result.get(0).indexOf("freedashboardRuntime") + 21) + "Ol465/ol.js");
				bpm.gwt.commons.client.utils.ToolsGWT.addScriptFiles(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});

		CommonService.Connect.getInstance().getIcons(new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				icons = result;
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	public native void doRedirect(String url)/*-{
		$wnd.open(url);
	}-*/;

	public InfoUser getInfoUser() {
		return infoUser;
	}

	public void setInfoUser(InfoUser infoUser) {
		this.infoUser = infoUser;
	}

	public PortailRepositoryDirectory getContentRepository() {
		return contentRepository;
	}

	public void setContentRepository(PortailRepositoryDirectory contentRepository) {
		this.contentRepository = contentRepository;
	}

	public List<String> getIcons() {
		return icons;
	}

	public Keycloak getKeycloak() {
		return keycloak;
	}
}
