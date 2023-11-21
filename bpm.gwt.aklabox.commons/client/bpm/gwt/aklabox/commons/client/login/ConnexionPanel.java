package bpm.gwt.aklabox.commons.client.login;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.loading.IWait;
import bpm.gwt.aklabox.commons.client.services.LoginService;
import bpm.gwt.aklabox.commons.client.utils.ToolsGWT;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;
import bpm.gwt.aklabox.commons.shared.CommonConstants;
import bpm.gwt.aklabox.commons.shared.InfoConnection;
import bpm.gwt.aklabox.commons.shared.InfoUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ConnexionPanel extends Composite implements KeyDownHandler {

	private static final String FORM_ID = "login-form";
	private static final String USER_ID = "login-username";
	private static final String PASSWORD_ID = "login-password";

	private static AkladConnectionUiBinder uiBinder = GWT.create(AkladConnectionUiBinder.class);

	interface AkladConnectionUiBinder extends UiBinder<Widget, ConnexionPanel> {
	}

	interface MyStyle extends CssResource {
		String txtUser();

		String panelForm();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel mainPanel, localePanel;

	@UiField
	SimplePanel formPanel;

	@UiField
	Label lblMessage;

	@UiField
	Image imgPlatformLogo;

	@UiField
	CheckBox cbRememberMe;
	
	@UiField
	Button btnLogin;

	private ILogin loginPanel;
	private IWait waitPanel;

	private FormPanel loginForm;
	private TextBox txtLogin, txtPassword;

	private CookieManager cookieManager = new CookieManager();

	public ConnexionPanel(IWait waitPanel, ILogin loginPanel, ImageResource imgWebApp, boolean fromPortal) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.loginPanel = loginPanel;

		txtLogin = TextBox.wrap(DOM.getElementById(USER_ID));
		txtLogin.addStyleName(style.txtUser());
		txtLogin.getElement().setAttribute("placeholder", LabelsConstants.lblCnst.Email());
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
		loginForm.addSubmitHandler(new SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				connect();
			}
		});
		formPanel.setWidget(loginForm);

		localePanel.add(new LocalePicker());

		loadLogo(imgWebApp);
		initCookie();

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				btnLogin.setFocus(true);
			}
		});
	}

	private void loadLogo(final ImageResource imgWebApp) {
		LoginService.Connect.getService().getConnectionInformations(new AsyncCallback<InfoConnection>() {

			@Override
			public void onSuccess(InfoConnection result) {
				if (result.getPlatform().getId() != 0) {
					imgPlatformLogo.setUrl(ToolsGWT.getRightPath(result.getPlatform().getLogo()));
				}
				else {
					imgPlatformLogo.setResource(imgWebApp);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				imgPlatformLogo.setResource(imgWebApp);
			}
		});
	}

	private void initCookie() {
		if (cookieManager.hasCookie()) {
			txtLogin.setText(Cookies.getCookie(CommonConstants.COOKIE_EMAIL));
			cbRememberMe.setValue(true);
		}
		else {
			txtLogin.setText("");
		}
	}

	@UiHandler("btnLogin")
	void onLogin(ClickEvent e) {
		login();
	}

	@UiHandler("btnLogin")
	public void onFocusKeyDown(KeyDownEvent event) {
		manageKeyDownEvent(event);
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		manageKeyDownEvent(event);
	}

	private void manageKeyDownEvent(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			onLogin(null);
			event.preventDefault();
		}
	}

	private void login() {
		loginForm.submit();
	}

	private void connect() {
		for (Widget w : mainPanel) {
			if (w instanceof HTMLPanel) {
				w.removeStyleName(CommonConstants.CSS_ERROR);
			}
		}

		String login = txtLogin.getText();
		String password = txtPassword.getText();

		if (login.isEmpty()) {
			lblMessage.setText(LabelsConstants.lblCnst.EmptyEmailField());
			mainPanel.addStyleName(CommonConstants.CSS_ERROR);
			txtLogin.addStyleName(CommonConstants.CSS_ERROR);
		}
		else if (password.isEmpty()) {
			lblMessage.setText(LabelsConstants.lblCnst.EmptyPasswordField());
			mainPanel.addStyleName(CommonConstants.CSS_ERROR);
			txtPassword.addStyleName(CommonConstants.CSS_ERROR);
		}
//		else if (!login.matches(CommonConstants.REGEX_EMAIL)) {
//			lblMessage.setText(LabelsConstants.lblCnst.InvalidEmail());
//			mainPanel.addStyleName(CommonConstants.CSS_ERROR);
//			txtLogin.addStyleName(CommonConstants.CSS_ERROR);
//		}
		else {
			txtLogin.getParent().removeStyleName(CommonConstants.CSS_ERROR);
			txtPassword.getParent().removeStyleName(CommonConstants.CSS_ERROR);
			mainPanel.removeStyleName(CommonConstants.CSS_ERROR);
			connectUser(login, password, cbRememberMe.getValue());
		}
	}

	private void connectUser(final String login, String password, final boolean isRemember) {
		waitPanel.showWaitPart(true);

		LoginService.Connect.getService().login(login, password, new AsyncCallback<InfoUser>() {

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);
				
				String message = caught.getMessage();
				if (message.substring(0, 3).equals("Inv")) {
					txtPassword.getParent().addStyleName(CommonConstants.CSS_ERROR);
				}
				else {
					txtLogin.getParent().addStyleName(CommonConstants.CSS_ERROR);
				}

				if (message.contains("doesn't exist</authentication>")) {
					txtLogin.getParent().addStyleName(CommonConstants.CSS_ERROR);
					lblMessage.setText(LabelsConstants.lblCnst.UserNotExist());
				}
				else if (message.contains("<error><password></password></error>")) {
					txtPassword.getParent().addStyleName(CommonConstants.CSS_ERROR);
					lblMessage.setText(LabelsConstants.lblCnst.WrongPassword());
				}
				else {
					lblMessage.setText(message);
				}

				mainPanel.addStyleName(CommonConstants.CSS_ERROR);
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onSuccess(InfoUser result) {
				waitPanel.showWaitPart(false);

				cookieManager.setCookie(isRemember, login);

				loginPanel.login(result);
			}
		});
	}
}
