package bpm.vanilla.portal.client.panels;

import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.IChangeGroup;
import bpm.gwt.commons.client.dialog.ConnectionAklaboxDialog;
import bpm.gwt.commons.client.dialog.ILoginAklabox;
import bpm.gwt.commons.client.popup.GroupPopup;
import bpm.gwt.commons.client.popup.IChangeTheme;
import bpm.gwt.commons.client.popup.ThemePopup;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.dialog.ProfileDialog;
import bpm.vanilla.portal.client.dialog.admin.AdminDialog;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.services.SecurityService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserPanel extends PopupPanel implements IChangeGroup, IChangeTheme, ILoginAklabox {

	private static UserInfoExpendPanelUiBinder uiBinder = GWT
			.create(UserInfoExpendPanelUiBinder.class);

	interface UserInfoExpendPanelUiBinder extends UiBinder<Widget, UserPanel> {
	}
	
	interface MyStyle extends CssResource {
		String imgUser();
		String imgAbout();
		String glass();
		String connectedAklabox();
		String noConnectedAklabox();
		String noConfigurationAklabox();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel panelBottom, panelUser;
	
	@UiField
	Image imgUser, imgAbout, btnAdmin, btnEditAklabox, btnDisconnectAklabox;
	
	@UiField
	Label lblFullName, lblEmail, lblName, lblUserEmail, lblRepository, lblGroup;
	
	@UiField
	Button btnAklabox;
	
	private TopToolbar topToolbar;
	private List<Group> availableGroups;
	
	public UserPanel(TopToolbar topToolbar, List<Group> availableGroups) {
		setWidget(uiBinder.createAndBindUi(this));
		this.topToolbar = topToolbar;
		this.availableGroups = availableGroups;
		
		InfoUser infoUser = biPortal.get().getInfoUser();
		
		imgAbout.setUrl(PortalImage.INSTANCE.about_me().getSafeUri());
		imgAbout.addStyleName(style.imgAbout());
		
		fillDataUser(biPortal.get().getInfoUser().getUser(), biPortal.get().getInfoUser().getRepository(), biPortal.get().getInfoUser().getGroup());
		findImageForUser(biPortal.get().getInfoUser().getUser().getId());
		
		panelUser.addStyleName(VanillaCSS.PANEL_USER);
		panelBottom.addStyleName(VanillaCSS.PANEL_USER_BOTTOM);
		
		if(!infoUser.getUser().isSuperUser()) {
			btnAdmin.removeFromParent();
		}
		
		refreshAklaboxButton(infoUser);
		
		this.setGlassStyleName(style.glass());
	}
	
	private void refreshAklaboxButton(InfoUser infoUser) {
		if(infoUser.isConnected(IRepositoryApi.AKLABOX)) {
			btnAklabox.setStyleName(style.connectedAklabox());
			btnAklabox.addStyleName(VanillaCSS.PANEL_USER_BOTTOM);
			btnAklabox.setText("Aklabox - " + ToolsGWT.lblCnst.OpenAklabox() + " " + infoUser.getUser().getAklaboxLogin());
			
			btnEditAklabox.setVisible(true);
			btnDisconnectAklabox.setVisible(true);
		}
		else if(infoUser.canAccess(IRepositoryApi.AKLABOX)) {
			btnAklabox.setStyleName(style.noConnectedAklabox());
			btnAklabox.setText(ToolsGWT.lblCnst.ConnectToAklabox());
			
			btnEditAklabox.setVisible(false);
			btnDisconnectAklabox.setVisible(false);
		}
		else {
			btnAklabox.setStyleName(style.noConfigurationAklabox());
			btnAklabox.setEnabled(false);
			btnAklabox.setText(ToolsGWT.lblCnst.NeedConfigureAklabox());
			
			btnEditAklabox.setVisible(false);
			btnDisconnectAklabox.setVisible(false);
		}
	}

	public void fillDataUser(User portalUser, Repository rep, Group group){
		lblFullName.setText(portalUser.getName());
		lblEmail.setText(portalUser.getBusinessMail() == null || portalUser.getBusinessMail().isEmpty() ? ToolsGWT.lblCnst.UnknownMail() : portalUser.getBusinessMail());
		
		lblName.setText(portalUser.getName());
		lblUserEmail.setText(portalUser.getBusinessMail() == null || portalUser.getBusinessMail().isEmpty() ? ToolsGWT.lblCnst.UnknownMail() : portalUser.getBusinessMail());
		lblRepository.setText(rep.getName());
		lblGroup.setText(group.getName());
		
//		lblNumMaj.setText("2");
	}
	
	private void findImageForUser(int userId) {
		LoginService.Connect.getInstance().getUserImg(userId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				
				imgUser.setUrl(PortalImage.INSTANCE.user_profile().getSafeUri());
				imgUser.addStyleName(style.imgUser());
			}

			@Override
			public void onSuccess(String result) {
				if(result != null && !result.isEmpty()){
					imgUser.setUrl(result);
					imgUser.addStyleName(style.imgUser());
				}
				else {
					imgUser.setUrl(PortalImage.INSTANCE.user_profile().getSafeUri());
					imgUser.addStyleName(style.imgUser());
				}
			}
		});
	}
	
	@Override
	public void changeGroup(Group group) {
		biPortal.get().showWaitPart(true);
		
		SecurityService.Connect.getInstance().changeCurrentGroup(biPortal.get().getInfoUser(), group, new AsyncCallback<InfoUser>() {
			
			@Override
			public void onFailure(Throwable caught) {
				biPortal.get().showWaitPart(false);
				
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedChangeGroup());
			}
			
			@Override
			public void onSuccess(InfoUser infoUser) {
				biPortal.get().showWaitPart(false);
				
				biPortal.get().setInfoUser(infoUser);

				topToolbar.refreshDataPanels();
				
				topToolbar.refreshImageForGroup(biPortal.get().getInfoUser().getGroup().getId());
				topToolbar.refreshImageWebapp(false);
			}
		});
	}

	@Override
	public void changeTheme(String themeValue) { 
		bpm.gwt.commons.client.utils.ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&cssFileName=" + themeValue);
	
		biPortal.get().getInfoUser().getUser().setVanillaTheme(themeValue);
	}
	
	@UiHandler("btnAklabox")
	public void onAklaboxClick(ClickEvent event) {
		InfoUser infoUser = biPortal.get().getInfoUser();
		if(infoUser.isConnected(IRepositoryApi.AKLABOX)) {
			biPortal.get().showWaitPart(true);
			
			String locale = LocaleInfo.getCurrentLocale().getLocaleName();
			
			LoginService.Connect.getInstance().connectToAklabox(infoUser.getUrl(IRepositoryApi.AKLABOX), locale, new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
					biPortal.get().showWaitPart(false);
					
					caught.printStackTrace();
					
					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedConnectAklabox());
				}

				@Override
				public void onSuccess(String url) {
					biPortal.get().showWaitPart(false);
					ToolsGWT.doRedirect(url);
				}
			});
		}
		else if(infoUser.canAccess(IRepositoryApi.AKLABOX)) {
			ConnectionAklaboxDialog dial = new ConnectionAklaboxDialog(this, null, null);
			dial.center();
		}
	}

	@Override
	public void connect(String login, String password) {
		biPortal.get().showWaitPart(true);
		
		LoginService.Connect.getInstance().connectToAklabox(biPortal.get().getInfoUser(), login, password, new AsyncCallback<InfoUser>() {

			@Override
			public void onFailure(Throwable caught) {
				biPortal.get().showWaitPart(false);
				
				caught.printStackTrace();
				
				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedConnectAklabox());
			}

			@Override
			public void onSuccess(InfoUser result) {
				biPortal.get().showWaitPart(false);
				
				biPortal.get().setInfoUser(result);
				
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), ToolsGWT.lblCnst.SuccessConnectAklabox());
				
				refreshAklaboxButton(result);
			}
		});
	}
	
	@UiHandler("btnEditAklabox")
	public void onEditAklaboxClick(ClickEvent event) {
		User user = biPortal.get().getInfoUser().getUser();
		
		ConnectionAklaboxDialog dial = new ConnectionAklaboxDialog(this, user.getAklaboxLogin(), user.getAklaboxPassword());
		dial.center();
	}
	
	@UiHandler("btnDisconnectAklabox")
	public void onDisconnectAklaboxClick(ClickEvent event) {
		LoginService.Connect.getInstance().disconnectFromAklabox(biPortal.get().getInfoUser(), new AsyncCallback<InfoUser>() {

			@Override
			public void onFailure(Throwable caught) {
				biPortal.get().showWaitPart(false);
				
				caught.printStackTrace();
				
				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedDisconnectAklabox());
			}

			@Override
			public void onSuccess(InfoUser result) {
				biPortal.get().showWaitPart(false);
				
				biPortal.get().setInfoUser(result);
				
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), ToolsGWT.lblCnst.SuccessDisconnectAklabox());
				
				refreshAklaboxButton(result);
			}
		});
	}
	
	@UiHandler("btnEdit")
	public void onEditClick(ClickEvent event) {
		ProfileDialog dial = new ProfileDialog(biPortal.get().getInfoUser().getUser());
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				findImageForUser(biPortal.get().getInfoUser().getUser().getId());
				topToolbar.loadUserInfo(biPortal.get().getInfoUser().getUser());
			}
		});
		dial.center();
	}
	
	@UiHandler("btnGroup")
	public void onGroupClick(ClickEvent event){
		GroupPopup displayPopup = new GroupPopup(this, availableGroups);
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.show();
	}
	
	@UiHandler("btnTheme")
	public void onThemeClick(ClickEvent event) {
		ThemePopup displayPopup = new ThemePopup(this);
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.show();
	}
	
	@UiHandler("btnAdmin")
	public void onAdminClick(ClickEvent event) {
		
		biPortal.get().showWaitPart(true);
		
		BiPortalService.Connect.getInstance().showAllRepository(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				biPortal.get().showWaitPart(false);
				
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, "");
			}

			@Override
			public void onSuccess(Boolean result) {
				AdminDialog dialAdmin = new AdminDialog(biPortal.get().getInfoUser().getUser(), availableGroups, result);
				dialAdmin.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						topToolbar.refreshImageForGroup(biPortal.get().getInfoUser().getGroup().getId());
					}
				});
				dialAdmin.center();
				
				biPortal.get().showWaitPart(false);
			}
		});
	}
	
//	@UiHandler("btnMaj")
//	public void onMajClick(ClickEvent event) {
//		MessageHelper.openMessageDialog("Update", "Update are not available yet.");
//	}
	
	@UiHandler("btnLogout")
	public void onLogoutClick(ClickEvent event) {
		LoginService.Connect.getInstance().disconnectUser(new AsyncCallback<Void>() {

			public void onFailure(Throwable arg0) {
				String url = GWT.getHostPageBaseURL();
				bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
			}

			public void onSuccess(Void arg0) {
				String url = GWT.getHostPageBaseURL();
				bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
			}

		});
	}
	
	@Override
	public void show() {
		setGlassEnabled(true);
		setAnimationEnabled(true);
		setAutoHideEnabled(true);
		
		super.show();
	
		getElement().getStyle().setRight(0, Unit.PX);
		getElement().getStyle().setTop(60, Unit.PX);
		getElement().getStyle().clearLeft();
	}
}
