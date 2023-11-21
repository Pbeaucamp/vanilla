package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.List;

import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.panels.UsersSelectionWithGroupPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class UsersSelectionWithGroupDialog extends AbstractDialogBox {

	private static UsersSelectionWithGroupDialogUiBinder uiBinder = GWT.create(UsersSelectionWithGroupDialogUiBinder.class);

	interface UsersSelectionWithGroupDialogUiBinder extends UiBinder<Widget, UsersSelectionWithGroupDialog> {
	}
	
	@UiField
	SimplePanel panelContent;
	
	private UsersSelectionWithGroupPanel usersPanel;
	
	private boolean isConfirm = false;

	public UsersSelectionWithGroupDialog(Enterprise enterprise) {
		super(LabelsConstants.lblCnst.UsersSelection(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		this.usersPanel = new UsersSelectionWithGroupPanel(enterprise, true);
		panelContent.add(usersPanel);
	
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}
	
	public void clearSelection() {
		isConfirm = false;
		usersPanel.clearSelection();
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			isConfirm = true;
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			isConfirm = false;
			hide();
		}
	};
	
	public boolean isConfirm() {
		return isConfirm;
	}
	
	public List<User> getSelectedUsers() {
		return usersPanel.getSelectedUsers();
	}

	@Override
	public int getThemeColor() {
		return 0;
	}

}
