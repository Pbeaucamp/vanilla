package bpm.es.web.client.dialogs;

import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.panels.UserPanel;
import bpm.es.web.client.panels.UsersPanel;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class UserDialog extends AbstractDialogBox {

	private static UserDialogUiBinder uiBinder = GWT.create(UserDialogUiBinder.class);

	interface UserDialogUiBinder extends UiBinder<Widget, UserDialog> {
	}
	
	@UiField
	UserPanel userPanel;

	private UsersPanel panelParent;
	private boolean edit;
	
	public UserDialog(UsersPanel panelParent, User user) {
		super(Labels.lblCnst.AddUser(), true, true);

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		this.edit = user != null;
		userPanel.loadUser(user);
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (userPanel.isConfirm()) {
				hide();
				panelParent.manageUser(userPanel.getUser(), edit);
			}
			else {
				//TODO: Erreur
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
