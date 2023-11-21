package bpm.vanilla.portal.client.panels;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.LocaleInfo;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.UpdateDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.dialog.AboutDialog;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.popup.ViewsPopup;
import bpm.vanilla.portal.client.popup.ViewsPopup.TypeView;
import bpm.vanilla.portal.client.utils.ToolsGWT;

// See AbstractVanillaManager.java to enable the CHECK_LICENSE
public class TopToolbar extends Composite {

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
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel toolbar, separator, panelImgWebapps, panelFullInfo, panelMinimalInfo;

	@UiField
	Image imgWebapp, imgView, imgFWR, imgFDW, imgFAW, imgMetadata, imgArchitect, imgDataPrep, imgAir, imgFMUW, imgFMLW, imgFMDesigner, imgFMKpiMap, imgUser, imgLogo, btnMaj;

	@UiField
	HTML lblLoggedAs;

	@UiField
	Label txtTitleWebapp, lblNumMaj/*, lblLicense*/;

	private MainPanel mainPanel;

	private TypeView selectedTypeView = TypeView.CLASSIC_VIEW;

	private boolean imgFawebPresent = true;
	private boolean imgFwrPresent = true;
	private boolean imgMetadataPresent = true;
	private boolean imgArchitectPresent = true;
	private boolean imgDataPrepPresent = true;
	private boolean imgAirPresent = true;
	private boolean imgFdPresent = true;
	private boolean imgFmloaderPresent = true;
	private boolean imgFmuserPresent = true;
	private boolean imgFmDesignerPresent = true;
	private boolean imgFmKpiMapPresent = true;
	private boolean imgUpdatePresent = true;

	private UpdateInformations updateInfos;

	private InfoUser infoUser;

	public TopToolbar(MainPanel mainPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.infoUser = biPortal.get().getInfoUser();

		loadingTheme();

		refreshImageForGroup(biPortal.get().getInfoUser().getGroup().getId());
		refreshImageWebapp(true);
		loadUserInfo(biPortal.get().getInfoUser().getUser());

		txtTitleWebapp.setVisible(false);

		btnMaj.setVisible(false);
		if (infoUser.canAccess(IRepositoryApi.UPDATE_MANAGER)) {
			checkUpdate();
		}

//		if (ConnexionPanel.CHECK_LICENSE) {
//			lblLicense.setVisible(true);
//			lblLicense.setText("Evalution version (30-04-2018)");
//		}
	}

	private void loadingTheme() {
		bpm.gwt.commons.client.utils.ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&cssFileName=");
	}

	public void loadUserInfo(User user) {
		lblLoggedAs.setHTML(ToolsGWT.lblCnst.LoggedAs() + " <b>" + user.getLogin() + "</b>");
		lblLoggedAs.setVisible(false);

		LoginService.Connect.getInstance().getUserImg(user.getId(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				imgUser.setUrl(PortalImage.INSTANCE.user_profile().getSafeUri());
				imgUser.addStyleName(style.imgLoggedUser());
			}

			@Override
			public void onSuccess(String result) {
				if (result != null && !result.isEmpty()) {
					imgUser.setUrl(result);
				}
				else {
					imgUser.setUrl(PortalImage.INSTANCE.user_profile().getSafeUri());
				}
				imgUser.addStyleName(style.imgLoggedUser());
			}
		});
	}

	public void refreshDataPanels() {
		mainPanel.refreshDataPanels();
	}

	public void refreshImageWebapp(boolean firstLoad) {
		if (!infoUser.canAccess(IRepositoryApi.FWR)) {
			imgFWR.removeFromParent();
			imgFwrPresent = false;
		}
		else if (!firstLoad && !imgFwrPresent) {
			imgFwrPresent = true;
			panelImgWebapps.add(imgFWR);
		}

		if (!infoUser.canAccess(IRepositoryApi.FDWEB)) {
			imgFDW.removeFromParent();
			imgFdPresent = false;
		}
		else if (!firstLoad && !imgFdPresent) {
			imgFdPresent = true;
			panelImgWebapps.add(imgFDW);
		}

		if (!infoUser.canAccess(IRepositoryApi.FAWEB)) {
			imgFAW.removeFromParent();
			imgFawebPresent = false;
		}
		else if (!firstLoad && !imgFawebPresent) {
			imgFawebPresent = true;
			panelImgWebapps.add(imgFAW);
		}

		if (!infoUser.canAccess(IRepositoryApi.FMDT_WEB)) {
			imgMetadata.removeFromParent();
			imgMetadataPresent = false;
		}
		else if (!firstLoad && !imgMetadataPresent) {
			imgMetadataPresent = true;
			panelImgWebapps.add(imgMetadata);
		}

		if (!infoUser.canAccess(IRepositoryApi.ARCHITECT_WEB)) {
			imgArchitect.removeFromParent();
			imgArchitectPresent = false;
		}
		else if (!firstLoad && !imgArchitectPresent) {
			imgArchitectPresent = true;
			panelImgWebapps.add(imgArchitect);
		}

		if (!infoUser.canAccess(IRepositoryApi.DATA_PREPARATION)) {
			imgDataPrep.removeFromParent();
			imgDataPrepPresent = false;
		}
		else if (!firstLoad && !imgDataPrepPresent) {
			imgDataPrepPresent = true;
			panelImgWebapps.add(imgDataPrep);
		}

		if (!infoUser.canAccess(IRepositoryApi.AIR)) {
			imgAir.removeFromParent();
			imgAirPresent = false;
		}
		else if (!firstLoad && !imgAirPresent) {
			imgAirPresent = true;
			panelImgWebapps.add(imgAir);
		}

		if (!infoUser.canAccess(IRepositoryApi.FMUSERWEB)) {
			imgFMUW.removeFromParent();
			imgFmuserPresent = false;
		}
		else if (!firstLoad && !imgFmuserPresent) {
			imgFmuserPresent = true;
			panelImgWebapps.add(imgFMUW);
		}

		if (!infoUser.canAccess(IRepositoryApi.FMLOADERWEB)) {
			imgFMLW.removeFromParent();
			imgFmloaderPresent = false;
		}
		else if (!firstLoad && !imgFmloaderPresent) {
			imgFmloaderPresent = true;
			panelImgWebapps.add(imgFMLW);
		}

		if (!infoUser.canAccess(IRepositoryApi.FMDESIGNER)) {
			imgFMDesigner.removeFromParent();
			imgFmDesignerPresent = false;
		}
		else if (!firstLoad && !imgFmDesignerPresent) {
			imgFmDesignerPresent = true;
			panelImgWebapps.add(imgFMDesigner);
		}

		if (!infoUser.canAccess(IRepositoryApi.FMKPIMAP)) {
			imgFMKpiMap.removeFromParent();
			imgFmKpiMapPresent = false;
		}
		else if (!firstLoad && !imgFmKpiMapPresent) {
			imgFmDesignerPresent = true;
			panelImgWebapps.add(imgFMKpiMap);
		}

		if (!infoUser.canAccess(IRepositoryApi.UPDATE_MANAGER)) {
			btnMaj.removeFromParent();
			imgUpdatePresent = false;
		}
		else if (!firstLoad && !imgUpdatePresent) {
			imgUpdatePresent = true;
			panelImgWebapps.add(btnMaj);
		}
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

	@UiHandler("imgFWR")
	public void openFWR(ClickEvent event) {
		String urlFWR = infoUser.getUrl(IRepositoryApi.FWR);
		openWebApp(urlFWR, false);
	}
	
	@UiHandler("imgDataPrep")
	public void openDataPrep(ClickEvent event) {
		String urlDataPreparation = infoUser.getUrl(IRepositoryApi.DATA_PREPARATION);
		openWebApp(urlDataPreparation, false);
	}

	@UiHandler("imgFDW")
	public void openFDW(ClickEvent event) {
		String urlFDWeb = infoUser.getUrl(IRepositoryApi.FDWEB);
		openWebApp(urlFDWeb, false);
	}

	@UiHandler("imgFAW")
	public void openFAW(ClickEvent event) {
		String urlFAWeb = infoUser.getUrl(IRepositoryApi.FAWEB);
		openWebApp(urlFAWeb, false);
	}

	@UiHandler("imgMetadata")
	public void openMetadata(ClickEvent event) {
		String ulrMetadata = infoUser.getUrl(IRepositoryApi.FMDT_WEB);
		openWebApp(ulrMetadata, false);
	}

	@UiHandler("imgArchitect")
	public void openArchitect(ClickEvent event) {
		String urlArchitect = infoUser.getUrl(IRepositoryApi.ARCHITECT_WEB);
		openWebApp(urlArchitect, false);
	}

	@UiHandler("imgAir")
	public void openAir(ClickEvent event) {
		String urlAir = infoUser.getUrl(IRepositoryApi.AIR);
		openWebApp(urlAir, false);
	}

	@UiHandler("imgFMUW")
	public void openFMUW(ClickEvent event) {
		String urlFMWeb = infoUser.getUrl(IRepositoryApi.FMUSERWEB);
		openWebApp(urlFMWeb, false);
	}

	@UiHandler("imgFMLW")
	public void openFMLW(ClickEvent event) {
		String urlFMWeb = infoUser.getUrl(IRepositoryApi.FMLOADERWEB);
		openWebApp(urlFMWeb, false);
	}

	@UiHandler("imgFMDesigner")
	public void openFMDesigner(ClickEvent event) {
		String urlFMDesigner = infoUser.getUrl(IRepositoryApi.FMDESIGNER);
		openWebApp(urlFMDesigner, false);
	}

	@UiHandler("imgFMKpiMap")
	public void openFMKpiMap(ClickEvent event) {
		String urlFmKpiMap = infoUser.getUrl(IRepositoryApi.FMKPIMAP);
		openWebApp(urlFmKpiMap, false);
	}

	private void openWebApp(String webAppUrl, final boolean isUpdate) {
		String locale = LocaleInfo.getCurrentLocale().getLocaleName();
		CommonService.Connect.getInstance().forwardSecurityUrl(webAppUrl, locale, new GwtCallbackWrapper<String>(null, false) {

			@Override
			public void onSuccess(String url) {
				if (isUpdate) {
					UpdateDialog dial = new UpdateDialog(mainPanel, url);
					dial.center();
				}
				else {
					ToolsGWT.doRedirect(url);
				}
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnMaj")
	public void onMaj(ClickEvent event) {
		if (updateInfos != null && updateInfos.hasUpdate()) {
			String updateManagerUrl = infoUser.getUrl(IRepositoryApi.UPDATE_MANAGER);
			openWebApp(updateManagerUrl, true);
		}
		else if (updateInfos != null && !updateInfos.hasUpdate()) {
			StringBuffer buf = new StringBuffer();
			buf.append(LabelsConstants.lblCnst.ApplicationUpToDate() + "<br/>");

			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), buf.toString());
		}
	}

	@UiHandler("imgUser")
	public void openUserPanel(ClickEvent event) {
		UserPanel userPanel = new UserPanel(this, biPortal.get().getInfoUser().getAvailableGroups());
		userPanel.show();
	}

	@UiHandler("imgAbout")
	public void openHelp(ClickEvent event) {
		AboutDialog about = new AboutDialog();
		about.center();
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
		} catch (Exception e) {
		}
		
		Keycloak keycloak = biPortal.get().getKeycloak();
		if (keycloak != null) {
			keycloak.logout();
		}

		String urlDeconnect = biPortal.get().getInfoUser().getUrlDeconnect();
		if (urlDeconnect == null || urlDeconnect.isEmpty()) {
			String locale = LocaleInfo.getCurrentLocale().getLocaleName();
			
			final String url = GWT.getHostPageBaseURL() + "?locale=" + locale;
			bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
		}
		else {
			bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(urlDeconnect);
		}
	}

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
		imgView.setResource(PortalImage.INSTANCE.ic_menu_view_small());
		imgView.removeStyleName(style.styleImgWebapps());
		imgView.addStyleName(style.styleImgMinimal());

		toolbar.setHeight("20px");

		panelImgWebapps.setVisible(false);
		imgLogo.setVisible(false);
		panelFullInfo.setVisible(false);
		separator.setVisible(false);
		panelMinimalInfo.setVisible(true);

		lblLoggedAs.setVisible(true);

		txtTitleWebapp.setVisible(true);
		imgWebapp.setVisible(false);
	}

	private void loadClassicToolbar() {
		imgView.setResource(PortalImage.INSTANCE.ic_menu_view());
		imgView.addStyleName(style.styleImgWebapps());
		imgView.removeStyleName(style.styleImgMinimal());

		toolbar.setHeight("60px");

		panelImgWebapps.setVisible(true);
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

	private void checkUpdate() {
		CommonService.Connect.getInstance().checkUpdates(new AsyncCallback<UpdateInformations>() {

			@Override
			public void onSuccess(final UpdateInformations result) {
				updateMaj(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				updateMaj(null);
			}
		});
	}

	private void updateMaj(UpdateInformations updateInfos) {
		this.updateInfos = updateInfos;

		if (updateInfos == null) {
			btnMaj.removeFromParent();
			return;
		}

		int nbUpdate = updateInfos.getUpdates() != null ? updateInfos.getUpdates().size() : 0;
		lblNumMaj.setText(String.valueOf(nbUpdate));

		if (updateInfos.hasUpdate()) {
			btnMaj.setVisible(true);
			btnMaj.setResource(CommonImages.INSTANCE.ic_system_update_black_18dp());
		}
		else {
			btnMaj.setResource(CommonImages.INSTANCE.ic_autorenew_black_18dp());
		}
	}
}
