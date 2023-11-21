package bpm.map.viewer.web.client.panel;

import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.map.viewer.web.client.Bpm_map_viewer_web;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.images.Images;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class TopToolbar extends Composite {
	

	private static TopToolbarUiBinder uiBinder = GWT.create(TopToolbarUiBinder.class);

	interface TopToolbarUiBinder extends UiBinder<Widget, TopToolbar> {
	}

	interface MyStyle extends CssResource {
		String imgLoggedUser();
		String btnSelected();
		String styleImgWebapps();
		String styleImgMinimal();
		String imgLogo();
	}

	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel toolbar, panelFullInfo, panelMinimalInfo;
	
	@UiField
	Image imgLogo;
	
	@UiField
	HTML lblLoggedAs;
	

	private MainPanel mainPanel;
	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);

	public TopToolbar(MainPanel mainPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;

		loadingTheme();

		/// provisoire
		String contextUrl = GWT.getHostPageBaseURL();
		imgLogo.setUrl(contextUrl + "cssHelper?loadLoginCss=true&fileName=" + "VanillaMap_Logo.png");
		imgLogo.addStyleName(style.imgLogo());
	}
	
	private void loadingTheme() {
	//	bpm.gwt.commons.client.utils.ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&cssFileName=kpi_theme.css");
	}


//	public void refreshImageForGroup(int groupId) {
//		LoginService.Connect.getInstance().getGroupImg(groupId, new AsyncCallback<String>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				caught.printStackTrace();
//			}
//
//			@Override
//			public void onSuccess(String result) {
//				if (result != null && !result.isEmpty()) {
//					imgLogo.setUrl(result);
//					imgLogo.addStyleName(style.imgLogo());
//				}
//			}
//		});
//	}


	@UiHandler("imgAbout")
	public void openHelp(ClickEvent event) {
//		AboutDialog about = new AboutDialog();
//		about.center();
	}

	@UiHandler("imgOut")
	public void out(ClickEvent event) {
		LoginService.Connect.getInstance().disconnectUser(new AsyncCallback<Void>() {

			public void onFailure(Throwable arg0) {
				deconnect();
			}

			public void onSuccess(Void arg0) {
				deconnect();
			}
		});
	}
	
	private void deconnect() {
		try {
			String sessionID = Cookies.getCookie(CommonConstants.SID);
			if (sessionID != null) {
				Cookies.removeCookie(CommonConstants.SID, "/");
			}
		} catch(Exception e) { }
			
//		String urlDeconnect = Bpm_map_viewer_web.get().getInfoUser().getUrlDeconnect();
//		if(urlDeconnect == null || urlDeconnect.isEmpty()) {
			final String url = GWT.getHostPageBaseURL();
			bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
//		}
//		else {
//			bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(urlDeconnect);
//		}
	}
	
	

	
}
