package bpm.vanilla.portal.client.Listeners;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.vanilla.portal.client.panels.IGedCheck;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class GedMenu extends PopupPanel {
	
	private static GedMenuUiBinder uiBinder = GWT.create(GedMenuUiBinder.class);

	interface GedMenuUiBinder extends UiBinder<Widget, GedMenu> {
	}
	
	@UiField
	HTMLPanel panelMenu;
	
	@UiField
	MenuItem btnCheckin, btnCheckout, btnComeBackToVersion, btnShare, btnPublicUrl;
	
	private IGedCheck gedCheck;
	private DocumentVersionDTO item;
	
	public GedMenu(final IGedCheck gedCheck, final DocumentVersionDTO item) {
		setWidget(uiBinder.createAndBindUi(this));
		this.gedCheck = gedCheck;
		this.item = item;
		
		btnCheckin.setScheduledCommand(checkinCommand);
		btnCheckout.setScheduledCommand(checkoutCommand);
		btnComeBackToVersion.setScheduledCommand(comebackToVersionCommand);
		btnShare.setScheduledCommand(shareCommand);
		btnPublicUrl.setScheduledCommand(publicUrlCommand);

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);
	}
	
	private Command checkinCommand = new Command() {
		
		@Override
		public void execute() {
			GedMenu.this.hide();
			
			gedCheck.checkin(item);
		}
	};
	
	private Command checkoutCommand = new Command() {
		
		@Override
		public void execute() {
			GedMenu.this.hide();
			
			gedCheck.checkout(item);
		}
	};
	
	private Command comebackToVersionCommand = new Command() {
		
		@Override
		public void execute() {
			GedMenu.this.hide();
			
			gedCheck.comeBackToVersion(item);
		}
	};
	
	private Command publicUrlCommand = new Command() {
		
		@Override
		public void execute() {
			GedMenu.this.hide();
			
			gedCheck.displayPublicUrls(item);
		}
	};

	
	private Command shareCommand = new Command() {
		
		@Override
		public void execute() {
			GedMenu.this.hide();
			
			gedCheck.share(item);
		}
	};

}
