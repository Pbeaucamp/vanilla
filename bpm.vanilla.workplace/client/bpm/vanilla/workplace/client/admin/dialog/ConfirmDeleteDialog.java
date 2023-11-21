package bpm.vanilla.workplace.client.admin.dialog;

import bpm.vanilla.workplace.client.services.AdminService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmDeleteDialog extends DialogBox {

	public enum DeleteType {
		USER,
		PROJECT,
		PACKAGE
	};
	
	private static ConfirmDeleteDialogUiBinder uiBinder = GWT
			.create(ConfirmDeleteDialogUiBinder.class);

	interface ConfirmDeleteDialogUiBinder extends
			UiBinder<Widget, ConfirmDeleteDialog> {
	}

	@UiField
	Button btnConfirm, btnCancel;

	private DeleteType type;
	private Integer userId;
	private Integer projectId;
	private Integer packageId;
	
	public ConfirmDeleteDialog(Integer userId, Integer projectId, Integer packageId, 
			String message, DeleteType type) {
		this.setWidget(uiBinder.createAndBindUi(this));
		this.userId = userId;
		this.projectId = projectId;
		this.packageId = packageId;
		this.type = type;
		
		this.setText(message);
	}

	@UiHandler("btnConfirm")
	public void onClick(ClickEvent e) {
		if(type == DeleteType.USER){
			AdminService.Connect.getInstance().deleteUser(userId, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					ConfirmDeleteDialog.this.hide();
				
					String htmlResult = "The user has been deleted.";
					
					DialogInformation dialInfo = new DialogInformation(htmlResult);
					dialInfo.setGlassEnabled(true);
					dialInfo.center();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					ConfirmDeleteDialog.this.hide();
					
					caught.printStackTrace();
					
					String htmlResult = caught.getMessage();
					
					DialogInformation dialInfo = new DialogInformation(htmlResult);
					dialInfo.setGlassEnabled(true);
					dialInfo.center();
				}
			});
		}
		else if(type == DeleteType.PACKAGE){
			AdminService.Connect.getInstance().deletePackage(userId, projectId, packageId, 
					new AsyncCallback<Void>() {
				
				@Override
				public void onFailure(Throwable caught) {
					ConfirmDeleteDialog.this.hide();
					
					caught.printStackTrace();
					
					DialogInformation info = new DialogInformation("An error happend");
					info.setGlassEnabled(true);
					info.center();
				}

				@Override
				public void onSuccess(Void result) {
					ConfirmDeleteDialog.this.hide();
					
					DialogInformation info = new DialogInformation("The package has been deleted.");
					info.setGlassEnabled(true);
					info.center();
				}
			});
		}
		else if(type == DeleteType.PROJECT){
			AdminService.Connect.getInstance().deleteProject(userId, projectId, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					ConfirmDeleteDialog.this.hide();
					
					caught.printStackTrace();
					
					DialogInformation info = new DialogInformation("An error happend");
					info.setGlassEnabled(true);
					info.center();
				}

				@Override
				public void onSuccess(Void result) {
					ConfirmDeleteDialog.this.hide();
					
					DialogInformation info = new DialogInformation("The package has been deleted.");
					info.setGlassEnabled(true);
					info.center();
				}
			});
		}
	}
	
	@UiHandler("btnCancel")
	public void onCancelClick(ClickEvent e){
		this.hide();
	}

}
