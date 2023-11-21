package bpm.gwt.workflow.commons.client;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.popup.IChangeView;
import bpm.gwt.commons.client.popup.ViewsPopup;
import bpm.gwt.commons.client.popup.ViewsPopup.TypeView;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.service.UpdateService;
import bpm.gwt.workflow.commons.client.utils.Tools;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopToolbar extends Composite implements IChangeView {

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
	HTMLPanel toolbar, separator, panelFullInfo, panelMinimalInfo;

	@UiField
	Image imgWebapp, imgView, imgLogo, btnMaj;

	@UiField
	HTML lblLoggedAs;

	@UiField
	Label txtTitleWebapp, lblNumMaj;

	private IWait waitPanel;
	private ILogout logout;
	private IChangeView changeView;
	// private InfoUser infoUser;

	private TypeView selectedTypeView = TypeView.CLASSIC_VIEW;

	private String updateManagerUrl;
	private boolean hasUpdate = false;

	public TopToolbar(String titleApplication, ImageResource imgWebapp, IWait waitPanel, IChangeView changeView, ILogout logout, InfoUser infoUser) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.changeView = changeView;
		this.logout = logout;
		this.updateManagerUrl = infoUser.getUpdateManagerUrl();

		loadingTheme();

		loadUserInfo(infoUser.getUser());

		txtTitleWebapp.setText(titleApplication);
		txtTitleWebapp.setVisible(false);
		
		if (imgWebapp != null) {
			this.imgWebapp.setResource(imgWebapp);
		}

		if (!infoUser.getUser().isSuperUser()) {
			btnMaj.removeFromParent();
		}

		checkUpdate(true, false);
	}

	private void checkUpdate(final boolean fromStart, final boolean update) {
		waitPanel.showWaitPart(true);

		UpdateService.Connect.getInstance().checkUpdates(updateManagerUrl, new GwtCallbackWrapper<UpdateInformations>(waitPanel, true) {

			@Override
			public void onSuccess(final UpdateInformations result) {
				if (result != null) {
					updateMaj(result.hasUpdate());

					if ((result.hasUpdate() && !fromStart) || update) {
						//TODO: Update
//						UpdateDialog dial = new UpdateDialog(waitPanel, result);
//						dial.center();
					}
					else if (!fromStart) {
						StringBuffer buf = new StringBuffer();
						buf.append(LabelsCommon.lblCnst.ApplicationUpToDate() + "<br/>");
						// buf.append(LabelsConstants.lblCnst.YourApplicationVersion()
						// + " : " + result.getActualVersion());

						MessageHelper.openMessageDialog(LabelsCommon.lblCnst.Information(), buf.toString());
					}
				}
				else {
					updateMaj(false);
				}
			}
		}.getAsyncCallback());
	}

	private void updateMaj(boolean hasUpdate) {
		lblNumMaj.setText(hasUpdate ? "1" : "0");
		this.hasUpdate = hasUpdate;

		if (hasUpdate) {
			btnMaj.setResource(Images.INSTANCE.ic_system_update_black_18dp());
		}
		else {
			btnMaj.setResource(Images.INSTANCE.ic_autorenew_black_18dp());
		}
	}

	private void loadingTheme() {
		Tools.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&cssFileName=vanilla_theme_air");
	}

	public void loadUserInfo(User user) {
		lblLoggedAs.setHTML(LabelsCommon.lblCnst.ConnectedAs() + " <b>" + user.getLogin() + "</b>");
		lblLoggedAs.setVisible(false);
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

	// @UiHandler("imgUser")
	// public void openUserPanel(ClickEvent event) {
	// UserPanel userPanel = new UserPanel(this, infoUser);
	// userPanel.show();
	// }

	@UiHandler("btnMaj")
	public void onMaj(ClickEvent event) {
		checkUpdate(false, hasUpdate);
	}

	@UiHandler("imgOut")
	public void out(ClickEvent event) {
		logout.onLogout();
	}

	@Override
	public void switchView(TypeView typeView) {
		this.changeView.switchView(typeView);
		this.selectedTypeView = typeView;

		switch (typeView) {
		case CLASSIC_VIEW:
			loadClassicToolbar();
			break;
		case FULL_SCREEN_VIEW:
			loadMinimalToolbar();
			break;

		default:
			break;
		}
	}

	private void loadMinimalToolbar() {
		imgView.setResource(Images.INSTANCE.ic_menu_view_small());
		imgView.removeStyleName(style.styleImgWebapps());
		imgView.addStyleName(style.styleImgMinimal());

		toolbar.setHeight("20px");

		imgLogo.setVisible(false);
		panelFullInfo.setVisible(false);
		separator.setVisible(false);
		panelMinimalInfo.setVisible(true);

		lblLoggedAs.setVisible(true);

		txtTitleWebapp.setVisible(true);
		imgWebapp.setVisible(false);
	}

	private void loadClassicToolbar() {
		imgView.setResource(Images.INSTANCE.ic_menu_view());
		imgView.addStyleName(style.styleImgWebapps());
		imgView.removeStyleName(style.styleImgMinimal());

		toolbar.setHeight("60px");

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
}
