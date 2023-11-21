package bpm.gwt.workflow.commons.client;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserPanel extends PopupPanel {

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
	Image imgUser, imgAbout, btnAdmin;
	
	@UiField
	Label lblFullName, lblEmail, lblName, lblUserEmail, lblRepository, lblGroup;
	
	private TopToolbar topToolbar;

	public UserPanel(TopToolbar topToolbar, InfoUser infoUser) {
		setWidget(uiBinder.createAndBindUi(this));
		this.topToolbar = topToolbar;

		fillDataUser(infoUser.getUser());
		
		panelUser.addStyleName(VanillaCSS.PANEL_USER);
		panelBottom.addStyleName(VanillaCSS.PANEL_USER_BOTTOM);
		
		if(!infoUser.getUser().isSuperUser()) {
			btnAdmin.removeFromParent();
		}
		
		this.setGlassStyleName(style.glass());
	}
	
	public void fillDataUser(User user){
//		lblFullName.setText(portalUser.getName());
//		lblEmail.setText(portalUser.getBusinessMail() == null || portalUser.getBusinessMail().isEmpty() ? LabelsConstants.lblCnst.UnknownMail() : portalUser.getBusinessMail());
//		
//		lblName.setText(portalUser.getName());
//		lblUserEmail.setText(portalUser.getBusinessMail() == null || portalUser.getBusinessMail().isEmpty() ? LabelsConstants.lblCnst.UnknownMail() : portalUser.getBusinessMail());
//		lblRepository.setText(rep.getName());
//		lblGroup.setText(group.getName());
		
//		lblNumMaj.setText("2");
	}
	
	@UiHandler("btnAdmin")
	public void onAdminClick(ClickEvent event) {

	}
	
//	@UiHandler("btnMaj")
//	public void onMajClick(ClickEvent event) {
//		MessageHelper.openMessageDialog("Update", "Update are not available yet.");
//	}
	
	@UiHandler("btnLogout")
	public void onLogoutClick(ClickEvent event) {
		topToolbar.out(event);
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
