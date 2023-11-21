package bpm.fwr.client;

import java.util.HashMap;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.fwr.client.I18N.WysiwygLabelConstants;
import bpm.fwr.client.services.FwrServiceConnection;
import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class Bpm_fwr extends RootWaitPanel implements EntryPoint, ILogin {

	public final static WysiwygLabelConstants LBLW = (WysiwygLabelConstants) GWT.create(WysiwygLabelConstants.class);

	private static Bpm_fwr instance;

	private static final String METADATAID = "metadataid";
	private static final String MODEL_NAME = "modelName";
	private static final String PACKAGE_NAME = "packagename";
	private static final String QUERY_NAME = "queryname";
	private static final String FORMAT_REPORT = "formatreport";
	
	private InfoUser infoUser;
	private Keycloak keycloak;

	private int metadataID;
	private String modelName;
	private String packageName;
	private String queryName;
	private Boolean formatted=false;

	public static Bpm_fwr getInstance() {
		return instance;
	}

	public void onModuleLoad() {
		instance = this;

		RootPanel.get("loading").setVisible(false);
		RootPanel.get("Login").setVisible(true);

		loadingTheme();
		initSession();

		loadingTheme();
	}

	private void initSession() {
		FwrServiceConnection.Connect.getInstance().initSession(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				String actualUrl = Window.Location.getQueryString();
				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), Bpm_fwr.this, Bpm_fwr.this, "fwr.png", false, true);
				parseUrlToOpenCube(actualUrl);
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
	public void login(InfoUser infoUser, Keycloak keycloak) {
		this.infoUser = infoUser;
		this.keycloak = keycloak;

		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);
		WysiwygPanel panel;
		if (!modelName.isEmpty() && modelName.length() > 0 && !packageName.isEmpty() && packageName.length() > 0 && !queryName.isEmpty() && queryName.length() > 0)
			panel = new WysiwygPanel(false, infoUser, metadataID, modelName, packageName, queryName, formatted, keycloak);
		else
			panel = new WysiwygPanel(false, infoUser, keycloak);
		RootPanel.get().add(panel);
	}

	public InfoUser getInfoUser() {
		return infoUser;
	}

	private void loadingTheme() {
		ToolsGWT.setLinkHref("loginCss", "cssHelper?loadLoginCss=true&fileName=login.css");
		ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&fileName=vanilla_theme_default.css");
		ToolsGWT.setLinkHref("segmentCss", "cssHelper?loadLoginCss=true&fileName=semantic.min.css");
	}

	public void onLogout() {
		this.infoUser = null;
		
		if (keycloak != null) {
			keycloak.logout();
		}

		String url = Window.Location.getHref();
		bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
	}

	private void parseUrlToOpenCube(String actualUrl) {

		HashMap<String, String> props = parseUrl(actualUrl);

		modelName = "";
		packageName = "";
		queryName = "";
		if (props.get(METADATAID) != null && props.get(MODEL_NAME) != null && props.get(PACKAGE_NAME) != null && props.get(QUERY_NAME) != null) {
			try {
				metadataID = Integer.parseInt(props.get(METADATAID));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelName = props.get(MODEL_NAME);
			packageName = props.get(PACKAGE_NAME);
			queryName = props.get(QUERY_NAME);
			if (props.get(FORMAT_REPORT) != null){
				formatted=true;
			}
		}
	}

	private HashMap<String, String> parseUrl(String location) {
		HashMap<String, String> props = new HashMap<String, String>();
		try {
			location = location.split("\\?")[1];
			String[] couples = location.split("&");

			String[] tmp;

			for (int i = 0; i < couples.length; i++) {
				tmp = couples[i].split("=");
				String val = URL.decodeQueryString(tmp[1]);
				props.put(tmp[0], val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return props;
	}
}
