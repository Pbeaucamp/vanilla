package bpm.architect.web.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.architect.web.client.panels.DataVizDesignPanel;
import bpm.architect.web.client.services.ArchitectService;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.gwt.commons.client.loading.RootWaitPanel;
import bpm.gwt.commons.client.login.ILogin;
import bpm.gwt.commons.client.login.LoginHelper;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Architect extends RootWaitPanel implements EntryPoint, ILogin {

	public void onModuleLoad() {
		DataVizDesignPanel.exportStaticMethod();
		
		RootPanel.get("loading").setVisible(false);
		RootPanel.get("Login").setVisible(true);

		loadingCss();
		initSession();
	}
	
	private void initSession() {
		ArchitectService.Connect.getInstance().initSession(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				String actualUrl = Window.Location.getQueryString();
				LoginHelper loginHelper = new LoginHelper(RootPanel.get("Login"), Architect.this, Architect.this, "vanilla_webapps_welcome_datapreparation.png", true, true);
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
				try {
					result.add(result.get(0).substring(0, result.get(0).indexOf("freedashboardRuntime") + 21) + "Ol465/ol.js");
				} catch(Exception e) {
				}
				ToolsGWT.addScriptFiles(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	@Override
	public void login(final InfoUser infoUser, Keycloak keycloak) {
		RootPanel.get("Login").getElement().getStyle().setDisplay(Display.NONE);
		
		final Map<String, String> properties = parseUrl(Window.Location.getQueryString());
		if(properties != null && properties.containsKey("bpm.vanilla.dataprep.id")) {
			
			ArchitectService.Connect.getInstance().getDataPreparations(new GwtCallbackWrapper<List<DataPreparation>>(null, false, false) {
				@Override
				public void onSuccess(List<DataPreparation> result) {
					int dpId = Integer.parseInt(properties.get("bpm.vanilla.dataprep.id"));
					for(DataPreparation dp : result) {
						if(dpId == dp.getId()) {
							Widget mainPanel = new DataVizDesignPanel(infoUser, null, dp);
							RootPanel.get().clear();
							RootPanel.get().add(mainPanel);
							break;
						}
					}
				}
			}.getAsyncCallback());
			
			
		}
		else {
			Widget mainPanel = new MainPanel(infoUser, keycloak);
			RootPanel.get().add(mainPanel);
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
