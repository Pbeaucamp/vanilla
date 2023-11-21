package bpm.gwt.commons.client.login;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.custom.v2.GwtCallbackWrapper;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.services.LoginServiceAsync;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoConnection;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLocale;

public class ConnexionPanel extends Composite implements KeyDownHandler {
	
	// See AbstractVanillaManager.java to enable the CHECK_LICENSE
	//public static final boolean CHECK_LICENSE = false;

	private enum TypeDisplay {
		REPOSITORIES, GROUPS, BOTH
	}

	private static final String PATH_IMAGES = "images/";

	private static final String FORM_ID = "login-form";
	private static final String USER_ID = "login-username";
	private static final String PASSWORD_ID = "login-password";

	private static ConnexionPanelUiBinder uiBinder = GWT.create(ConnexionPanelUiBinder.class);

	interface ConnexionPanelUiBinder extends UiBinder<Widget, ConnexionPanel> {
	}

	interface MyStyle extends CssResource {
		String imgLogo();

		String txtUser();

		String panelForm();

		String imgFlag();

		String displayNone();

		String panelFull();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel mainPanel, panelFlag, fastConnectPanel, panelLogin, panelGrRep, panelRepository, panelGroup;

	@UiField
	SimplePanel formPanel, panelRepositories, panelGroups;

	@UiField
	Image imgLogoVanilla, imgConnectOne, imgConnectTwo, imgConnectThree;

	@UiField
	CheckBox checkStayConnected;

	@UiField
	SubmitButton btnLoad;

	@UiField
	Button btnConnect;

	private IWait waitPanel;
	private ILogin loginPanel;

	private FormPanel loginForm;
	private TextBox txtLogin, txtPassword;

	private InfoUser infoUser;
	private Keycloak keycloak;

	private CustomDatagrid<Group> groupDatagrid;
	private SingleSelectionModel<Group> selectionGroup;

	private CustomDatagrid<Repository> repositoryDatagrid;
	private SingleSelectionModel<Repository> selectionRepository;

	private boolean groupPartComplete = false;
	private boolean repositoryPartComplete = false;

	private List<Repository> repositories;
	private List<Group> groups;

	private boolean loading = false;
	private boolean isComplete;
	
	private ServerNotReadyPanel serverNotReadyPanel;
	private boolean showServerReadyPanel = false;
	
	public ConnexionPanel(IWait waitPanel, ILogin loginPanel, String webappImageName, boolean fromPortal, boolean isComplete) {
		this(waitPanel, loginPanel, webappImageName, fromPortal, isComplete, null);
	}
	
	public ConnexionPanel(IWait waitPanel, ILogin loginPanel, String webappImageName, boolean fromPortal, boolean isComplete, String keyAutoLogin) {
		this(waitPanel, loginPanel, webappImageName, fromPortal, isComplete, keyAutoLogin, null);
	}

	public ConnexionPanel(IWait waitPanel, ILogin loginPanel, String webappImageName, boolean fromPortal, boolean isComplete, String keyAutoLogin, Keycloak keycloak) {
		initWidget(uiBinder.createAndBindUi(this));
		injectLoginFunction(this);
		this.waitPanel = waitPanel;
		this.loginPanel = loginPanel;
		this.isComplete = isComplete;
		this.keycloak = keycloak;

		txtLogin = TextBox.wrap(DOM.getElementById(USER_ID));
		txtLogin.addStyleName(style.txtUser());
		txtLogin.getElement().setAttribute("placeholder", LabelsConstants.lblCnst.Login());
		txtLogin.addKeyDownHandler(this);

		txtPassword = PasswordTextBox.wrap(DOM.getElementById(PASSWORD_ID));
		txtPassword.addStyleName(style.txtUser());
		txtPassword.getElement().setAttribute("placeholder", LabelsConstants.lblCnst.Password());
		txtPassword.addKeyDownHandler(this);

		FlexTable flexConnect = new FlexTable();
		flexConnect.addStyleName(style.panelForm());
		flexConnect.setCellSpacing(5);
		flexConnect.setWidget(0, 0, txtLogin);
		flexConnect.setWidget(1, 0, txtPassword);

		loginForm = FormPanel.wrap(DOM.getElementById(FORM_ID), false);
		loginForm.setVisible(true);
		loginForm.add(flexConnect);

		formPanel.setWidget(loginForm);

		imgLogoVanilla.addStyleName(style.imgLogo());

		fastConnectPanel.setVisible(false);

		loadImage(imgLogoVanilla, webappImageName);
		loadImage(imgConnectOne, "log.png");
		loadImage(imgConnectTwo, "log.png");
		loadImage(imgConnectThree, "log.png");

		if (isComplete) {
			selectionGroup = new SingleSelectionModel<Group>();
			groupDatagrid = new CustomDatagrid<Group>(selectionGroup, 150, LabelsConstants.lblCnst.LogToLoadGroup(), null);
			panelGroups.setWidget(groupDatagrid);

			selectionRepository = new SingleSelectionModel<Repository>();
			repositoryDatagrid = new CustomDatagrid<Repository>(selectionRepository, 150, LabelsConstants.lblCnst.LogToLoadRepository(), null);
			panelRepositories.setWidget(repositoryDatagrid);
		}
		else {
			btnLoad.setText(LabelsConstants.lblCnst.LogIn());
		}


		updateInterface(true, TypeDisplay.BOTH);
		pingServer(keyAutoLogin, fromPortal, isComplete);
	}

	private void pingServer(final String keyAutoLogin, final boolean fromPortal, final boolean isComplete) {
		CommonService.Connect.getInstance().ping(new GwtCallbackWrapper<Boolean>(null, false, false) {

			@Override
			public void onSuccess(Boolean result) {
				updateServerReadiness(keyAutoLogin, fromPortal, isComplete, result != null && result);
			}
			
			public void onFailure(Throwable t) {
				updateServerReadiness(keyAutoLogin, fromPortal, isComplete, false);
			};
		}.getAsyncCallback());
	}
	
	private void updateServerReadiness(final String keyAutoLogin, final boolean fromPortal, final boolean isComplete, boolean serverIsReady) {
		if (serverIsReady) {
			this.showServerReadyPanel = false;
			if (serverNotReadyPanel != null) {
				RootPanel.get().remove(serverNotReadyPanel);
			}
			
			if (keyAutoLogin != null) {
				testAutoLogin(keyAutoLogin, fromPortal, isComplete);
			}
			else if (keycloak != null) {
				testKeycloakLogin(keycloak, fromPortal, isComplete);
			}
			else {
				testConnectionCas(fromPortal, isComplete);
			}
		}
		else if (!serverIsReady && !showServerReadyPanel) {
			this.showServerReadyPanel = true;
			this.serverNotReadyPanel = new ServerNotReadyPanel();
			RootPanel.get().add(serverNotReadyPanel);
		}

		if (!serverIsReady) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					pingServer(keyAutoLogin, fromPortal, isComplete);
				}
			};
			timer.schedule(10000);
		}
	}

	private void updateInterface(boolean login, TypeDisplay type) {
		if (login) {
			txtLogin.setEnabled(true);
			txtPassword.setEnabled(true);
			checkStayConnected.setEnabled(true);

			panelLogin.removeStyleName(style.displayNone());
			panelGrRep.addStyleName(style.displayNone());

			Scheduler.get().scheduleDeferred(new Command() {
				public void execute() {
					btnLoad.setFocus(true);
				}
			});
		}
		else {
			panelLogin.addStyleName(style.displayNone());
			panelGrRep.removeStyleName(style.displayNone());

			switch (type) {
			case BOTH:
				panelRepository.removeStyleName(style.displayNone());
				panelGroup.removeStyleName(style.displayNone());
				panelRepository.removeStyleName(style.panelFull());
				panelGroup.removeStyleName(style.panelFull());
				break;
			case GROUPS:
				panelRepository.addStyleName(style.displayNone());
				panelGroup.removeStyleName(style.displayNone());
				panelGroup.addStyleName(style.panelFull());
				break;
			case REPOSITORIES:
				panelRepository.removeStyleName(style.displayNone());
				panelGroup.addStyleName(style.displayNone());
				panelRepository.addStyleName(style.panelFull());
				break;
			default:
				break;
			}

			Scheduler.get().scheduleDeferred(new Command() {
				public void execute() {
					btnConnect.setFocus(true);
				}
			});
		}
	}

	private void testConnectionCas(final boolean fromPortal, final boolean isComplete) {
		waitPanel.showWaitPart(true);

		txtLogin.setEnabled(false);
		txtPassword.setEnabled(false);
		checkStayConnected.setEnabled(false);

		LoginService.Connect.getInstance().testConnectionCAS(new AsyncCallback<InfoUser>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				txtLogin.setEnabled(true);
				txtPassword.setEnabled(true);
				checkStayConnected.setEnabled(true);

				loadInformations(fromPortal, null, isComplete);
			}

			@Override
			public void onSuccess(InfoUser infoUser) {
				User user = infoUser != null ? infoUser.getUser() : null;
				if (user != null) {
					txtLogin.setText(user.getLogin());
					txtPassword.setText(user.getPassword());
				}
				loadInformations(fromPortal, infoUser, isComplete);
			}
		});
	}

	private void testAutoLogin(String keyAutoLogin, final boolean fromPortal, final boolean isComplete) {
		waitPanel.showWaitPart(true);

		txtLogin.setEnabled(false);
		txtPassword.setEnabled(false);
		checkStayConnected.setEnabled(false);

		LoginService.Connect.getInstance().testAutoLogin(keyAutoLogin, new AsyncCallback<InfoUser>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				txtLogin.setEnabled(true);
				txtPassword.setEnabled(true);
				checkStayConnected.setEnabled(true);

				loadInformations(fromPortal, null, isComplete);
			}

			@Override
			public void onSuccess(InfoUser infoUser) {
				User user = infoUser != null ? infoUser.getUser() : null;
				if (user != null) {
					txtLogin.setText(user.getLogin());
					txtPassword.setText(user.getPassword());
				}
				loadInformations(fromPortal, infoUser, isComplete);
			}
		});
	}

	private void testKeycloakLogin(Keycloak keycloak, final boolean fromPortal, final boolean isComplete) {
		waitPanel.showWaitPart(true);

		txtLogin.setEnabled(false);
		txtPassword.setEnabled(false);
		checkStayConnected.setEnabled(false);
		
//		JavaScriptObject token = (JavaScriptObject) keycloak.getParsedToken();
		try {
			//Adding token to request
			LoginServiceAsync service = LoginService.Connect.getInstance();
			((ServiceDefTarget) service).setRpcRequestBuilder(new RpcRequestBuilder());
			
			LoginService.Connect.getInstance(keycloak.getToken()).initFromKeycloak(keycloak.getToken(), new AsyncCallback<InfoUser>() {

				@Override
				public void onFailure(Throwable arg0) {
					txtLogin.setEnabled(true);
					txtPassword.setEnabled(true);
					checkStayConnected.setEnabled(true);

					loadInformations(fromPortal, null, isComplete);
				}

				@Override
				public void onSuccess(InfoUser result) {
					User user = result != null ? result.getUser() : null;
					
					if (user != null) {
						txtLogin.setText(user.getLogin());
						txtPassword.setText(user.getPassword());
					}
					loadInformations(fromPortal, result, isComplete);
				}

			});
		} catch (JSONException e) {
			e.printStackTrace();

			txtLogin.setEnabled(true);
			txtPassword.setEnabled(true);
			checkStayConnected.setEnabled(true);

			loadInformations(fromPortal, null, isComplete);
		}
	}

	private void loadRepositories() {
		waitPanel.showWaitPart(true);

		LoginService.Connect.getInstance().getAvailableRepositories(new AsyncCallback<List<Repository>>() {

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);
				repositoryDatagrid.loadItems(new ArrayList<Repository>());

				caught.printStackTrace();

				MessageHelper.openMessageError("Unable to get the available repositories for the user.", caught);
			}

			@Override
			public void onSuccess(List<Repository> availableRepositories) {
				if (availableRepositories == null) {
					availableRepositories = new ArrayList<Repository>();
				}

				repositories = availableRepositories;

				if (groupPartComplete) {
					checkGroupsAndRepositories(groups, availableRepositories);
				}
				else {
					repositoryPartComplete = true;

					waitPanel.showWaitPart(false);
				}
			}
		});
	}

	private void loadGroups() {
		waitPanel.showWaitPart(true);

		LoginService.Connect.getInstance().getAvailableGroups(new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);
				groupDatagrid.loadItems(new ArrayList<Group>());

				caught.printStackTrace();

				MessageHelper.openMessageError("Unable to get the available groups for the user.", caught);
			}

			@Override
			public void onSuccess(List<Group> availableGroups) {
				if (availableGroups == null) {
					availableGroups = new ArrayList<Group>();
				}

				groups = availableGroups;

				if (repositoryPartComplete) {
					checkGroupsAndRepositories(availableGroups, repositories);
				}
				else {
					groupPartComplete = true;

					waitPanel.showWaitPart(false);
				}
			}
		});
	}

	private void checkGroupsAndRepositories(List<Group> groups, List<Repository> repositories) {
		if (groups == null || groups.isEmpty()) {
			waitPanel.showWaitPart(false);

			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NoGroup());
			return;
		}

		if (repositories == null || repositories.isEmpty()) {
			waitPanel.showWaitPart(false);

			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NoRepository());
			return;
		}

		if (repositories.size() == 1 && groups.size() == 1) {
			connect(infoUser, repositories.get(0), groups.get(0));
		}
		else if (repositories.size() == 1 && groups.size() > 1) {
			waitPanel.showWaitPart(false);

			updateInterface(false, TypeDisplay.GROUPS);

			groupDatagrid.loadItems(groups);
		}
		else if (groups.size() == 1 && repositories.size() > 1) {
			waitPanel.showWaitPart(false);

			updateInterface(false, TypeDisplay.REPOSITORIES);

			repositoryDatagrid.loadItems(repositories);
		}
		else {
			waitPanel.showWaitPart(false);

			updateInterface(false, TypeDisplay.BOTH);

			groupDatagrid.loadItems(groups);
			repositoryDatagrid.loadItems(repositories);
		}
	}

	private void loadInformations(boolean fromPortal, final InfoUser infoUser, final boolean isComplete) {
//		if (CHECK_LICENSE) {
//			DateTimeFormat df = DateTimeFormat.getFormat("yyyyMMdd");
//			Date limitDate = df.parse("20180430");
//			if (new Date().after(limitDate)) {
//				waitPanel.showWaitPart(false);
//				
//				btnLoad.setEnabled(false);
//				btnConnect.setEnabled(false);
//				MessageHelper.openMessageError("The licence is expired for this version of Vanilla. Please contact us on our website http://bpm-conseil.com", null);
//				return;
//			}
//		}

		LoginService.Connect.getInstance().getConnectionInformations(fromPortal, new AsyncCallback<InfoConnection>() {

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();

				MessageHelper.openMessageError("Unable to get informations from the server.", caught);
			}

			@Override
			public void onSuccess(InfoConnection infos) {
				waitPanel.showWaitPart(false);

				if (infos.getCustomLogo() != null && !infos.getCustomLogo().isEmpty()) {
					imgLogoVanilla.setUrl(infos.getCustomLogo());
				}

				VanillaLocale firstLocale = null;
				if (infos.getLocales() != null && !infos.getLocales().isEmpty()) {
					for (VanillaLocale locale : infos.getLocales()) {
						if (locale.getLocaleOrder() == 1) {
							firstLocale = locale;
						}
						buildImage(locale);
					}
				}

				String locale = Window.Location.getParameter("locale");
				if (locale == null && firstLocale != null) {
					selectLocale(firstLocale.getLocaleValue());
				}

				if (!infos.isIncludeFastConnect()) {
					fastConnectPanel.removeFromParent();
				}
				else {
					fastConnectPanel.setVisible(true);
				}

				if (infoUser != null && isComplete) {
					loadGroupsAndRepositories(infoUser);
				}
				else if (infoUser != null && !isComplete) {
					connect(infoUser, null, null);
				}
			}
		});
	}

	private void buildImage(VanillaLocale locale) {
		Image imgFlag = new Image(findResource(locale));
		imgFlag.setTitle(locale.getName());
		imgFlag.addStyleName(style.imgFlag());
		imgFlag.addClickHandler(new FlagHandler(locale));
		panelFlag.add(imgFlag);
	}

	private ImageResource findResource(VanillaLocale locale) {
		if (locale.getLocaleValue().equalsIgnoreCase("fr_FR")) {
			return CommonImages.INSTANCE.flag_france();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("en_EN")) {
			return CommonImages.INSTANCE.flag_united_kingdom();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("es_ES")) {
			return CommonImages.INSTANCE.flag_spain();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("hi_IN")) {
			return CommonImages.INSTANCE.flag_india();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("it")) {
			return CommonImages.INSTANCE.flag_italy();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("nl_NL")) {
			return CommonImages.INSTANCE.flag_netherlands();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("pt")) {
			return CommonImages.INSTANCE.flag_portugal();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("th_TH")) {
			return CommonImages.INSTANCE.flag_taiwan();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("zh_CN")) {
			return CommonImages.INSTANCE.flag_china();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("zh_TW")) {
			return CommonImages.INSTANCE.flag_china();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("ms_MY")) {
			return CommonImages.INSTANCE.flag_malaysia();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("ja_JP")) {
			return CommonImages.INSTANCE.flag_japan();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("pl_PL")) {
			return CommonImages.INSTANCE.flag_poland();
		}
		else if (locale.getLocaleValue().equalsIgnoreCase("ru_RU")) {
			return CommonImages.INSTANCE.flag_lithuania();
		}
		return CommonImages.INSTANCE.flag_default();
	}

	private void selectLocale(String localeValue) {
		UrlBuilder newUrl = Window.Location.createUrlBuilder();
		newUrl.setParameter("locale", localeValue);
		Window.Location.assign(newUrl.buildString());
	}

	private static native void injectLoginFunction(ConnexionPanel connexionPanel) /*-{
		var connexionPanel = connexionPanel;
		$wnd.doSubmitForm = function() {
			connexionPanel.@bpm.gwt.commons.client.login.ConnexionPanel::doSubmitForm()();
		};
	}-*/;

	@UiHandler("btnLoad")
	public void onLoadClick(ClickEvent event) {
		loginForm.submit();
	}

	@UiHandler("btnConnect")
	public void onConnectClick(ClickEvent event) {
		Repository repository = null;
		if (repositories != null && repositories.size() == 1) {
			repository = repositories.get(0);
		}
		else {
			repository = selectionRepository.getSelectedObject();
		}

		if (repository == null) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), "You need to select a valid repository.");
			return;
		}

		Group group = null;
		if (groups != null && groups.size() == 1) {
			group = groups.get(0);
		}
		else {
			group = selectionGroup.getSelectedObject();
		}

		if (group == null) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), "You need to select a valid group.");
			return;
		}

		connect(infoUser, repository, group);
	}

	private void doSubmitForm() {
		if (!loading) {
			this.loading = true;
			
			String login = txtLogin.getText();
			String password = txtPassword.getText();
			login(login, password);
		}
	}

	private void loadImage(Image img, String fileName) {
		if (fileName != null && !fileName.isEmpty()) {
			String contextUrl = GWT.getHostPageBaseURL();
			img.setUrl(contextUrl + "cssHelper?loadLoginCss=true&fileName=" + fileName);
		}
	}

	private void loadGroupsAndRepositories(InfoUser infoUser) {
		this.infoUser = infoUser;

		loadRepositories();
		loadGroups();
	}

	private void login(String login, String password) {
		waitPanel.showWaitPart(true);

		LoginService.Connect.getInstance().login(login, password, new AsyncCallback<InfoUser>() {

			@Override
			public void onSuccess(InfoUser infoUser) {
				loading = false;
				waitPanel.showWaitPart(false);

				if (infoUser != null && isComplete) {
					loadGroupsAndRepositories(infoUser);
				}
				else if (infoUser != null) {
					connect(infoUser, null, null);
				}
				else {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), "Unable to connect.");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				loading = false;
				waitPanel.showWaitPart(false);

				caught.printStackTrace();

				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
			}
		});
	}
	
	private void connect(InfoUser infoUser, Repository repository, Group group) {
		waitPanel.showWaitPart(true);

		LoginService.Connect.getInstance().connect(infoUser, repository, group, new AsyncCallback<InfoUser>() {
			@Override
			public void onSuccess(InfoUser infoUser) {
				ConnexionPanel.this.removeFromParent();

				waitPanel.showWaitPart(false);

				loginPanel.login(infoUser, keycloak);

				if (checkStayConnected.getValue()) {
					try {
						String sessionID = infoUser.getSessionId();
						Date expires = new Date(System.currentTimeMillis() + CommonConstants.DURATION);
						Cookies.setCookie(CommonConstants.SID, sessionID, expires, null, "/", false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();

				MessageHelper.openMessageError("Error when trying to init the connect to this application", caught);
			}
		});
	}

	@UiHandler("btnBack")
	public void onBack(ClickEvent event) {
		updateInterface(true, TypeDisplay.BOTH);

		this.groupPartComplete = false;
		this.repositoryPartComplete = false;
	}

	@UiHandler("btnForgotPassword")
	public void onForgotClick(ClickEvent event) {
		PasswordChangeDemandDialog dialForgotPassword = new PasswordChangeDemandDialog(waitPanel);
		dialForgotPassword.center();
	}

	private class FlagHandler implements ClickHandler {

		private VanillaLocale locale;

		public FlagHandler(VanillaLocale locale) {
			this.locale = locale;
		}

		@Override
		public void onClick(ClickEvent event) {
			selectLocale(locale.getLocaleValue());
		}
	}

	@UiHandler("lblConnectOne")
	public void onConnectSystem(ClickEvent event) {
		txtLogin.setText("system");
		txtPassword.setText("system");
		login("system", "system");
	}

	@UiHandler("lblConnectTwo")
	public void onConnectDesigner(ClickEvent event) {
		txtLogin.setText("designer");
		txtPassword.setText("designer");
		login("designer", "designer");
	}

	@UiHandler("lblConnectThree")
	public void onConnectUser(ClickEvent event) {
		txtLogin.setText("user");
		txtPassword.setText("user");
		login("user", "user");
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			if (!groupPartComplete && !repositoryPartComplete) {
				DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), btnLoad);
			}
			else {
				DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), btnConnect);
			}
		}
	}
}
