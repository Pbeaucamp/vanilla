package bpm.faweb.client.panels;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.images.FaWebImage;
import bpm.gwt.commons.client.popup.IChangeTheme;
import bpm.gwt.commons.client.popup.ThemePopup;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserPanel extends PopupPanel implements IChangeTheme {

	private static UserInfoExpendPanelUiBinder uiBinder = GWT
			.create(UserInfoExpendPanelUiBinder.class);

	interface UserInfoExpendPanelUiBinder extends UiBinder<Widget, UserPanel> {
	}
	
	interface MyStyle extends CssResource {
		String imgUser();
		String imgAbout();
		String glass();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel panelBottom, panelUser;
	
	@UiField
	Image imgUser, imgAbout;
	
	@UiField
	Label lblFullName, lblEmail, lblName, lblUserEmail, lblRepository, lblGroup;
	
	private InfoUser infoUser;
	
	public UserPanel(InfoUser infoUser) {
		setWidget(uiBinder.createAndBindUi(this));
		this.infoUser = infoUser;
		
		imgAbout.setUrl(FaWebImage.INSTANCE.about_me().getSafeUri());
		imgAbout.addStyleName(style.imgAbout());
		
		fillDataUser(infoUser.getUser(), infoUser.getRepository(), infoUser.getGroup());
		findImageForUser(infoUser.getUser().getId());
		
		panelUser.addStyleName(VanillaCSS.PANEL_USER);
		panelBottom.addStyleName(VanillaCSS.PANEL_USER_BOTTOM);

		this.setGlassStyleName(style.glass());
	}
	
	public void fillDataUser(User portalUser, Repository rep, Group group){
		lblFullName.setText(portalUser.getName());
		lblEmail.setText(portalUser.getBusinessMail() == null || portalUser.getBusinessMail().isEmpty() ? FreeAnalysisWeb.LBL.UnknownMail() : portalUser.getBusinessMail());
		
		lblName.setText(portalUser.getName());
		lblUserEmail.setText(portalUser.getBusinessMail() == null || portalUser.getBusinessMail().isEmpty() ? FreeAnalysisWeb.LBL.UnknownMail() : portalUser.getBusinessMail());
		lblRepository.setText(rep.getName());
		lblGroup.setText(group.getName());
	}
	
	private void findImageForUser(int userId) {
		LoginService.Connect.getInstance().getUserImg(userId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				
				imgUser.setUrl(FaWebImage.INSTANCE.user_profile().getSafeUri());
				imgUser.addStyleName(style.imgUser());
			}

			@Override
			public void onSuccess(String result) {
				if(result != null && !result.isEmpty()){
					imgUser.setUrl(result);
					imgUser.addStyleName(style.imgUser());
				}
				else {
					imgUser.setUrl(FaWebImage.INSTANCE.user_profile().getSafeUri());
					imgUser.addStyleName(style.imgUser());
				}
			}
		});
	}
	
	@UiHandler("btnTheme")
	public void onThemeClick(ClickEvent event) {
		ThemePopup displayPopup = new ThemePopup(this);
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.show();
	}

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
	public void changeTheme(String themeValue) {
		bpm.gwt.commons.client.utils.ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&cssFileName=" + themeValue);
	
		infoUser.getUser().setVanillaTheme(themeValue);
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
