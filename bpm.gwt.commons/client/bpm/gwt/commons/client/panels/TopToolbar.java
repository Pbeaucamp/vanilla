package bpm.gwt.commons.client.panels;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.popup.IChangeView;
import bpm.gwt.commons.client.popup.ViewsPopup;
import bpm.gwt.commons.client.popup.ViewsPopup.TypeView;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.vanilla.platform.core.beans.User;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopToolbar extends Composite implements IChangeView {
	
	public enum TypeToolbar {
		PORTAIL,
		ANALYSIS,
		WEBREPORT,
		HYPERVISION,
		WEBMETADATA
	}
	
	private static final int DEFAULT_VIEW_POSITION_TOP = 55;
	private static final int FULL_SCREEN_POSITION_TOP = 15;

	private static TopToolbarUiBinder uiBinder = GWT.create(TopToolbarUiBinder.class);

	interface TopToolbarUiBinder extends UiBinder<Widget, TopToolbar> {
	}

	interface MyStyle extends CssResource {
		String imgLoggedUser();
		String btnSelected();
		String styleImgWebapps();
		String styleImgMinimal();
		String imgLogo();
		String txtTitleWebapp();
	}

	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel toolbar, separator, panelBtn, panelFullInfo, panelMinimalInfo;
	
	@UiField
	Image imgAbout, imgWebapp, imgView, imgUser, imgLogo, btnMaj;
	
	@UiField
	HTML lblLoggedAs;
	
	@UiField
	Label txtTitleWebapp, lblNumMaj;

	private IMainPanel mainPanel;
	private InfoUser infoUser;
	private Keycloak keycloak;
	
	private TypeView selectedTypeView = TypeView.CLASSIC_VIEW;
	private boolean hasUpdate = false;

	public TopToolbar(IMainPanel mainPanel, InfoUser infoUser, String title, ImageResource img, Keycloak keycloak) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.infoUser = infoUser;
		this.keycloak = keycloak;

		loadingTheme();

		refreshImageForGroup(infoUser.getGroup().getId());
		loadUserInfo(infoUser.getUser());
		
		txtTitleWebapp.setVisible(false);
		txtTitleWebapp.setText(title);
		
		imgWebapp.setResource(img);
		imgWebapp.addStyleName(style.txtTitleWebapp());
		
		imgAbout.setVisible(false);
		this.addStyleName(VanillaCSS.MENU_HEAD);
		
		btnMaj.removeFromParent();
	}
	
	public TopToolbar(IMainPanel mainPanel, InfoUser infoUser, String title, ImageResource img, boolean showChangeView, Keycloak keycloak) {
		this(mainPanel, infoUser, title, img, keycloak);
		
		imgView.setVisible(showChangeView);
		separator.setVisible(showChangeView);
	}
	
	public void addButton(ImageResource resource, String title, ClickHandler handler) {
		Image btn = new Image(resource);
		btn.setTitle(title);
		btn.setStyleName(style.styleImgWebapps());
		btn.addClickHandler(handler);
		
		panelBtn.add(btn);
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
				
				imgUser.setUrl(CommonImages.INSTANCE.user_profile().getSafeUri());
				imgUser.addStyleName(style.imgLoggedUser());
			}

			@Override
			public void onSuccess(String result) {
				if(result != null && !result.isEmpty()){
					imgUser.setUrl(result);
				}
				else {
					imgUser.setUrl(CommonImages.INSTANCE.user_profile().getSafeUri());
				}
				imgUser.addStyleName(style.imgLoggedUser());
			}
		});
	}

	public void refreshImageForGroup(int groupId) {
		LoginService.Connect.getInstance().getGroupImg(groupId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(String result) {
				if (result != null && !result.isEmpty()) {
					imgLogo.setUrl(result);
					imgLogo.addStyleName(style.imgLogo());
				}
			}
		});
	}
	
	@UiHandler("imgView")
	public void onChangeViewClick(ClickEvent event) {
		int top = DEFAULT_VIEW_POSITION_TOP;
		
		switch (selectedTypeView) {
		case CLASSIC_VIEW:
			top = DEFAULT_VIEW_POSITION_TOP;
			break;
		case FULL_SCREEN_VIEW:
			top = FULL_SCREEN_POSITION_TOP;
			break;
		case MINIMAL_VIEW:
			top = DEFAULT_VIEW_POSITION_TOP;
			break;
		default:
			break;
		}
		
		ViewsPopup typeViewerPopup = new ViewsPopup(this);
		typeViewerPopup.setPopupPosition(-5, top);
		typeViewerPopup.addCloseHandler(closeMenu);
		typeViewerPopup.show();
		
		imgView.addStyleName(style.btnSelected());
	}
	
	private CloseHandler<PopupPanel> closeMenu = new CloseHandler<PopupPanel>() {

		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			imgView.removeStyleName(style.btnSelected());
		}
	};

	@UiHandler("btnMaj")
	public void onMaj(ClickEvent event) {
		checkUpdate(false, hasUpdate);
	}

	@UiHandler("imgUser")
	public void openUserPanel(ClickEvent event) {
		UserPanel userPanel = new UserPanel(mainPanel, this, infoUser, infoUser.getAvailableGroups());
		userPanel.show();
	}

	@UiHandler("imgAbout")
	public void openHelp(ClickEvent event) {
		mainPanel.showAbout();
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
	
	@Override
	public void switchView(TypeView typeView) {
		this.mainPanel.switchView(typeView);
		this.selectedTypeView = typeView;
		
		switch (typeView) {
		case CLASSIC_VIEW:
			loadClassicToolbar();
			break;
		case FULL_SCREEN_VIEW:
			loadMinimalToolbar();
			break;
		case MINIMAL_VIEW:
			loadMinimalToolbar();
			break;

		default:
			break;
		}
	}

	private void loadMinimalToolbar() {
		imgView.setResource(CommonImages.INSTANCE.ic_menu_view_small());
		imgView.removeStyleName(style.styleImgWebapps());
		imgView.addStyleName(style.styleImgMinimal());
		
		toolbar.setHeight("20px");
		
		panelBtn.setVisible(false);
		imgLogo.setVisible(false);
		panelFullInfo.setVisible(false);
		separator.setVisible(false);
		panelMinimalInfo.setVisible(true);
		
		lblLoggedAs.setVisible(true);
		
		txtTitleWebapp.setVisible(true);
		imgWebapp.setVisible(false);
	}

	private void loadClassicToolbar() {
		imgView.setResource(CommonImages.INSTANCE.ic_menu_view());
		imgView.addStyleName(style.styleImgWebapps());
		imgView.removeStyleName(style.styleImgMinimal());

		toolbar.setHeight("60px");
		
		panelBtn.setVisible(true);
		imgLogo.setVisible(true);
		panelFullInfo.setVisible(true);
		separator.setVisible(true);
		panelMinimalInfo.setVisible(false);

		lblLoggedAs.setVisible(false);
		
		txtTitleWebapp.setVisible(false);
		imgWebapp.setVisible(true);
	}

	public TypeView getTypeView() {
		return selectedTypeView;
	}
	

	private void checkUpdate(final boolean fromStart, final boolean update) {
		mainPanel.showWaitPart(true);

		CommonService.Connect.getInstance().checkUpdates(new GwtCallbackWrapper<UpdateInformations>(mainPanel, true) {

			@Override
			public void onSuccess(final UpdateInformations result) {
				updateMaj(result.hasUpdate());

				if ((result.hasUpdate() && !fromStart) || update) {
//					UpdateDialog dial = new UpdateDialog(mainPanel, result);
//					dial.center();
				}
				else if (!fromStart) {
					StringBuffer buf = new StringBuffer();
					buf.append(LabelsConstants.lblCnst.ApplicationUpToDate() + "<br/>");
//					buf.append(LabelsConstants.lblCnst.YourApplicationVersion() + " : " + result.getActualVersion());

					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), buf.toString());
				}
			}
		}.getAsyncCallback());
	}

	private void updateMaj(boolean hasUpdate) {
		lblNumMaj.setText(hasUpdate ? "1" : "0");
		this.hasUpdate = hasUpdate;

		if (hasUpdate) {
			btnMaj.setResource(CommonImages.INSTANCE.ic_system_update_black_18dp());
		}
		else {
			btnMaj.setResource(CommonImages.INSTANCE.ic_autorenew_black_18dp());
		}
	}
}
