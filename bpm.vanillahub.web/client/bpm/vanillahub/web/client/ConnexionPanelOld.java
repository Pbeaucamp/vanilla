package bpm.vanillahub.web.client;

import java.util.Date;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanillahub.core.Constants;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.images.Images;
import bpm.vanillahub.web.client.services.AdminService;

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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ConnexionPanelOld extends Composite implements KeyDownHandler {

	private static final String PATH_IMAGES = "images/";

	private static final String FORM_ID = "login-form";
	private static final String USER_ID = "login-username";
	private static final String PASSWORD_ID = "login-password";

	private static ConnexionPanelUiBinder uiBinder = GWT.create(ConnexionPanelUiBinder.class);

	interface ConnexionPanelUiBinder extends UiBinder<Widget, ConnexionPanelOld> {
	}

	interface MyStyle extends CssResource {
		String imgLogo();

		String txtUser();

		String panelForm();

		String displayNone();

		String panelFull();
		String imgFlag();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel mainPanel, panelLogin, panelFlag;

	@UiField
	SimplePanel formPanel;

	@UiField
	Image imgLogoVanilla;

	@UiField
	CheckBox checkStayConnected;

	@UiField
	SubmitButton btnConnect;

	private Bpm parentPanel;

	private FormPanel loginForm;
	private TextBox txtLogin, txtPassword;

	public ConnexionPanelOld(Bpm parentPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		injectLoginFunction(this);
		this.parentPanel = parentPanel;

		txtLogin = TextBox.wrap(DOM.getElementById(USER_ID));
		txtLogin.addStyleName(style.txtUser());
		txtLogin.getElement().setAttribute("placeholder", "Identifiant");
		txtLogin.addKeyDownHandler(this);

		txtPassword = PasswordTextBox.wrap(DOM.getElementById(PASSWORD_ID));
		txtPassword.addStyleName(style.txtUser());
		txtPassword.getElement().setAttribute("placeholder", "Mot de passe");
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
		
		buildFlags();

		String locale = Window.Location.getParameter("locale");
		if (locale == null) {
			selectLocale("en_US");
		}

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				btnConnect.setFocus(true);
			}
		});
	}

	private static native void injectLoginFunction(ConnexionPanelOld connexionPanel) /*-{
		var connexionPanel = connexionPanel;
		$wnd.doSubmitForm = function() {
			connexionPanel.@bpm.vanillahub.web.client.ConnexionPanelOld::doSubmitForm()();
		};
	}-*/;

	@UiHandler("btnConnect")
	public void onLoadClick(ClickEvent event) {
		loginForm.submit();
	}

	private void doSubmitForm() {
		String login = txtLogin.getText();
		String password = txtPassword.getText();

		parentPanel.showWaitPart(true);

//		AdminService.Connect.getInstance().login(login, password, new AsyncCallback<InfoUser>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				parentPanel.showWaitPart(false);
//				ExceptionManager.getInstance().handleException(caught, "Impossible de se connecter à l'application.");
//			}
//
//			@Override
//			public void onSuccess(InfoUser result) {
//				parentPanel.showWaitPart(false);
//
//				if (checkStayConnected.getValue()) {
//					try {
//						String sessionID = result.getSessionId();
//						Date expires = new Date(System.currentTimeMillis() + Constants.DURATION);
//						Cookies.setCookie(Constants.SID, sessionID, expires, null, "/", false);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//
//				parentPanel.login(result);
//			}
//		});
	}

	@UiHandler("btnForgotPassword")
	public void onForgotClick(ClickEvent event) {
		// DialogForgotPassword dialForgotPassword = new
		// DialogForgotPassword(waitPanel);
		// dialForgotPassword.center();
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), btnConnect);
		}
	}

	private void buildFlags() {
		Image imgFlagEngland = new Image(Images.INSTANCE.flag_united_kingdom());
		imgFlagEngland.setTitle(Labels.lblCnst.English());
		imgFlagEngland.addStyleName(style.imgFlag());
		imgFlagEngland.addClickHandler(new FlagHandler("en_US"));
		panelFlag.add(imgFlagEngland);
		
		Image imgFlagFrench = new Image(Images.INSTANCE.flag_france());
		imgFlagFrench.setTitle(Labels.lblCnst.French());
		imgFlagFrench.addStyleName(style.imgFlag());
		imgFlagFrench.addClickHandler(new FlagHandler("fr_FR"));
		panelFlag.add(imgFlagFrench);
	}

	private void selectLocale(String localeValue) {
		UrlBuilder newUrl = Window.Location.createUrlBuilder();
		newUrl.setParameter("locale", localeValue);
		Window.Location.assign(newUrl.buildString());
	}
	
	private class FlagHandler implements ClickHandler {

		private String locale;

		public FlagHandler(String locale) {
			this.locale = locale;
		}

		@Override
		public void onClick(ClickEvent event) {
			selectLocale(locale);
		}
	}
}
