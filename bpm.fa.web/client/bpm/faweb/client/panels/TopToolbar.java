package bpm.faweb.client.panels;

import java.util.List;
import java.util.Stack;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dialog.OpenViewDialog;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.infoscube.ItemView;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;
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
	}

	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel toolbar, panelFullInfo, panelMinimalInfo;
	
	@UiField
	Image imgOut, imgUser;
	
	@UiField
	HTML lblLoggedAs;
	
	private MainPanel mainPanel;
	private InfoUser infoUser;
	private Keycloak keycloak;

	public TopToolbar(MainPanel mainPanel, InfoUser infoUser, Keycloak keycloak) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.infoUser = infoUser;
		this.keycloak = keycloak;
		
		loadingTheme();
		
//		String vanillaCss = infoUser.getUser().getVanillaTheme();
//		if(vanillaCss != null && !vanillaCss.isEmpty()){
//			for(int i=0; i<VanillaCSS.CSS_FILE_NAMES.length; i++){
//				if(VanillaCSS.CSS_FILE_NAMES[i][1].equals(vanillaCss)){
//					loadingTheme();
//					break;
//				}
//			}
//		}
		loadUserInfo(infoUser.getUser());
		
		addStyleName(VanillaCSS.MENU_HEAD);
	}

	private void loadingTheme() {
		bpm.gwt.commons.client.utils.ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&cssFileName=");
	}
	
	public void loadUserInfo(User user) {
		lblLoggedAs.setHTML("You are logged as <b>" + user.getLogin() + "</b>");
		lblLoggedAs.setVisible(false);
		
		LoginService.Connect.getInstance().getUserImg(user.getId(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				
				imgUser.setUrl(FaWebImage.INSTANCE.user_profile().getSafeUri());
				imgUser.addStyleName(style.imgLoggedUser());
			}

			@Override
			public void onSuccess(String result) {
				if(result != null && !result.isEmpty()){
					imgUser.setUrl(result);
				}
				else {
					imgUser.setUrl(FaWebImage.INSTANCE.user_profile().getSafeUri());
				}
				imgUser.addStyleName(style.imgLoggedUser());
			}
		});
	}
	
	@UiHandler("imgOpen")
	public void onOpenClick(ClickEvent event) {
		mainPanel.getMh().setBefore(new Stack());
		mainPanel.showOpenDialog(-1);
		mainPanel.getDisplayPanel().getCubeViewerTab().clearFilters();
	}
	
	@UiHandler("imgOpenView")
	public void onOpenView(ClickEvent event) {
		mainPanel.showWaitPart(true);
		
		FaWebService.Connect.getInstance().setViewsService(mainPanel.getKeySession(), new AsyncCallback<List<ItemView>>() {

			@Override
			public void onSuccess (List<ItemView> listItemView){
				OpenViewDialog dial = new OpenViewDialog(mainPanel, listItemView);
				dial.center();

				mainPanel.showWaitPart(false);
			}

			@Override
			public void onFailure (Throwable ex){
				ex.printStackTrace();
				
				mainPanel.showWaitPart(false);
			}
		});
	}

	@UiHandler("imgUser")
	public void onUserProfilClick(ClickEvent event) {
		UserPanel userPanel = new UserPanel(infoUser);
		userPanel.show();
	}

	@UiHandler("imgOut")
	public void onLogoutClick(ClickEvent event) {
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
		
		String url = GWT.getHostPageBaseURL();
		bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
	}

	public void loadComment(int fasdId, final MainPanel parent) {
		mainPanel.showWaitPart(true);

		FaWebService.Connect.getInstance().canComment(mainPanel.getKeySession(), fasdId, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				ExceptionManager.getInstance().handleException(caught, "Unable to know if we can comment this item.");

				parent.showWaitPart(false);

//				imgComment.setVisible(false);
			}

			@Override
			public void onSuccess(Boolean result) {
				parent.showWaitPart(false);

				if (result != null) {
//					imgComment.setVisible(result);
				}
			}
		});
	}
}
