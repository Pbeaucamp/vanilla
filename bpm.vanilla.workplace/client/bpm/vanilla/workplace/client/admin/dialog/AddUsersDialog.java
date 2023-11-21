package bpm.vanilla.workplace.client.admin.dialog;

import java.util.ArrayList;
import java.util.List;

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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class AddUsersDialog extends DialogBox {

	private static AddUsersDialogUiBinder uiBinder = GWT
			.create(AddUsersDialogUiBinder.class);

	interface AddUsersDialogUiBinder extends
			UiBinder<Widget, AddUsersDialog> {
	}

	@UiField
	ListBox lstPackage;
	
	@UiField
	Button btnConfirm, btnCancel;
	
	private int packageId;
	private List<PlaceWebUser> users;

	@SuppressWarnings("deprecation")
	public AddUsersDialog(int packageId, List<PlaceWebUser> users) {
		this.setWidget(uiBinder.createAndBindUi(this));
		
		this.setText("Add users to the package.");
		
		this.packageId = packageId;
		this.users = users;
		
		lstPackage.setMultipleSelect(true);
		
		for(PlaceWebUser user : users){
			lstPackage.addItem(user.getName(), String.valueOf(user.getId()));
		}
	}

	@UiHandler("btnConfirm")
	public void onConfirmClick(ClickEvent e) {
		List<PlaceWebUser> selectedUsers = new ArrayList<PlaceWebUser>();
		for(int i=0; i<lstPackage.getItemCount(); i++){
			boolean selected = lstPackage.isItemSelected(i);
			if(selected){
				int userId = Integer.parseInt(lstPackage.getValue(i));
				for(PlaceWebUser user : users){
					if(user.getId() == userId){
						selectedUsers.add(user);
						break;
					}
				}
			}
		}
		
		if(!selectedUsers.isEmpty()){
			AdminService.Connect.getInstance().allowUserForPackage(packageId, selectedUsers, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					AddUsersDialog.this.hide();
					
					String infos = "Packages added with success.";
					
					DialogInformation dialInfo = new DialogInformation(infos);
					dialInfo.setGlassEnabled(true);
					dialInfo.center();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					AddUsersDialog.this.hide();
					
					caught.printStackTrace();
					
					String infos = caught.getMessage();
					
					DialogInformation dialInfo = new DialogInformation(infos);
					dialInfo.setGlassEnabled(true);
					dialInfo.center();
					
				}
			});
		}
		else {
			this.hide();
		}
	}

	@UiHandler("btnCancel")
	public void onCancelClick(ClickEvent e) {
		this.hide();
	}
}
