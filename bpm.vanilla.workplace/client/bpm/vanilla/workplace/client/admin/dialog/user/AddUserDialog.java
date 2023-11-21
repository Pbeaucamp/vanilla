package bpm.vanilla.workplace.client.admin.dialog.user;

import bpm.vanilla.workplace.client.admin.panel.user.PanelUserInformation;
import bpm.vanilla.workplace.client.services.AdminService;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AddUserDialog extends DialogBox {

	private static AddUserDialogUiBinder uiBinder = GWT
			.create(AddUserDialogUiBinder.class);

	interface AddUserDialogUiBinder extends UiBinder<Widget, AddUserDialog> {
	}
	
	@UiField
	SimplePanel panelUser;

	@UiField
	Button btnCancel, btnConfirm;
	
	private PanelUserInformation panelInfos;
	
	public AddUserDialog() {
		this.setWidget(uiBinder.createAndBindUi(this));
		this.setText("Add a user");
		
		panelInfos = new PanelUserInformation(null, true);
		panelUser.setWidget(panelInfos);
	}
	
	@UiHandler("btnConfirm")
	public void onConfirmClick(ClickEvent event){
		PlaceWebUser userToCreate = panelInfos.getUser();
		if(userToCreate != null){
			AdminService.Connect.getInstance().createUser(userToCreate, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					AddUserDialog.this.hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	}
	
	@UiHandler("btnCancel")
	public void onCancelClick(ClickEvent event){
		this.hide();
	}
}
