package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.List;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.panels.UsersMailSelectionPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class UsersMailSelectionDialog extends AbstractDialogBox {

	private static UsersMailSelectionDialogUiBinder uiBinder = GWT.create(UsersMailSelectionDialogUiBinder.class);

	interface UsersMailSelectionDialogUiBinder extends UiBinder<Widget, UsersMailSelectionDialog> {
	}
	
	@UiField
	SimplePanel mainPanel;
	
	private UsersMailSelectionPanel userSelectionPanel;
	
	private boolean isConfirm;

	public UsersMailSelectionDialog() {
		super(LabelsConstants.lblCnst.UsersSelection(), false, true);
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Ok(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		this.userSelectionPanel = new UsersMailSelectionPanel(null, null, null);
		mainPanel.setWidget(userSelectionPanel);
	}

	public boolean isConfirm() {
		return isConfirm;
	}
	
	public List<String> getSelectedMails() {
		return userSelectionPanel.getSelectedMails();
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (userSelectionPanel.hasUsersSelected()) {
				isConfirm = true;
				
				hide();
			}
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			isConfirm = false;
			
			hide();
		}
	};

	@Override
	public int getThemeColor() {
		return 0;
	}
}
