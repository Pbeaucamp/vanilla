package bpm.gwt.commons.client.popup;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.utils.TypeShare;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class SharePopup extends PopupPanel {

	private static ViewsPopupUiBinder uiBinder = GWT.create(ViewsPopupUiBinder.class);

	interface ViewsPopupUiBinder extends UiBinder<Widget, SharePopup> {
	}
	
	interface MyStyle extends CssResource {
		String labelType();
		String labelTypeEnd();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;
	
	@UiField
	Label btnAklabox, btnMail, btnAir, btnCmis, btnCkan, btnDataprep, btnWebReport, btnArchitect;
	
	private IShare share;
	
	public SharePopup(IShare share, boolean isConnectedToAklabox) {
		if (isConnectedToAklabox) {
			buildContent(share, TypeShare.EMAIL, TypeShare.EXPORT, TypeShare.AKLABOX);
		}
		else {
			buildContent(share, TypeShare.EMAIL, TypeShare.EXPORT);
		}
	}
	
	public SharePopup(IShare share, TypeShare... types) {
		buildContent(share, types);
	}
	
	private void buildContent(IShare share, TypeShare... types) {
		setWidget(uiBinder.createAndBindUi(this));
		this.share = share;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		if (!contains(TypeShare.EMAIL, types)) {
			btnMail.removeFromParent();
		}
		
		if (!contains(TypeShare.AKLABOX, types)) {
			btnAklabox.removeFromParent();
		}
		
		if (!contains(TypeShare.AIR, types)) {
			btnAir.removeFromParent();
		}
		
		if (!contains(TypeShare.CMIS, types)) {
			btnCmis.removeFromParent();
		}
		
		if (!contains(TypeShare.CKAN, types)) {
			btnCkan.removeFromParent();
		}
		
		if (!contains(TypeShare.DATAPREPARATION, types)) {
			btnDataprep.removeFromParent();
		}
		
		if (!contains(TypeShare.WEB_REPORT, types)) {
			btnWebReport.removeFromParent();
		}
		
		if (!contains(TypeShare.ARCHITECT, types)) {
			btnArchitect.removeFromParent();
		}
 		
		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}
	
	private boolean contains(TypeShare myType, TypeShare... types) {
		if (types != null) {
			for (TypeShare type : types) {
				if (type == myType) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@UiHandler("btnAklabox")
	public void onAklaboxClick(ClickEvent event) {
		hide();
		share.openShare(TypeShare.AKLABOX);
	}
	
	@UiHandler("btnMail")
	public void onMailClick(ClickEvent event) {
		hide();
		share.openShare(TypeShare.EMAIL);
	}
	
	@UiHandler("btnAir")
	public void onAirClick(ClickEvent event) {
		hide();
		share.openShare(TypeShare.AIR);
	}
	
	@UiHandler("btnCmis")
	public void onCmisClick(ClickEvent event) {
		hide();
		share.openShare(TypeShare.CMIS);
	}

	@UiHandler("btnCkan")
	public void onCkanClick(ClickEvent event) {
		hide();
		share.openShare(TypeShare.CKAN);
	}
	
	@UiHandler("btnDataprep")
	public void onDataPrepClick(ClickEvent event) {
		hide();
		share.openShare(TypeShare.DATAPREPARATION);
	}
	
	@UiHandler("btnWebReport")
	public void onWebReportClick(ClickEvent event) {
		hide();
		share.openShare(TypeShare.WEB_REPORT);
	}
	
	@UiHandler("btnArchitect")
	public void onArchitectClick(ClickEvent event) {
		hide();
		share.openShare(TypeShare.ARCHITECT);
	}
} 
