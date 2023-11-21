package bpm.es.web.client.panels;

import bpm.es.web.client.images.Images;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class TopToolbar extends Composite {

	private static TopToolbarUiBinder uiBinder = GWT.create(TopToolbarUiBinder.class);

	interface TopToolbarUiBinder extends UiBinder<Widget, TopToolbar> {
	}
	
	@UiField
	Image imgUser;

	private MainPanel mainPanel;
	private InfoUser infoUser;
	private Keycloak keycloak;
	
	public TopToolbar(MainPanel mainPanel, InfoUser infoUser, Keycloak keycloak) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.infoUser = infoUser;
		this.keycloak = keycloak;
		
		imgUser.setResource(Images.INSTANCE.img_profile_small());
	}
	
	@UiHandler("imgLogo")
	public void onLogo(ClickEvent event) {
		mainPanel.displayHome();
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
		
		if (keycloak != null) {
			keycloak.logout();
		}
		
		String urlDeconnect = infoUser.getUrlDeconnect();
		if(urlDeconnect == null || urlDeconnect.isEmpty()) {
			final String url = GWT.getHostPageBaseURL();
			bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
		}
		else {
			bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(urlDeconnect);
		}
	}
}
