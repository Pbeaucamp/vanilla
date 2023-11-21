package bpm.vanilla.portal.client.dialog;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.portal.client.services.AccessRequestService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class RequestAccessDialog extends AbstractDialogBox {

	private static RequestAccessDialogUiBinder uiBinder = GWT
			.create(RequestAccessDialogUiBinder.class);

	interface RequestAccessDialogUiBinder extends
			UiBinder<Widget, RequestAccessDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	Label lblRequestInfo, lblRequestInfoMsg;
	
	@UiField
	TextArea txtRequestInfoMsg;
	
	private Object dto;

	public RequestAccessDialog(Object dto) {
		super(ToolsGWT.lblCnst.RequestAccess_AskForAccess(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(ToolsGWT.lblCnst.Ok(), okHandler, ToolsGWT.lblCnst.Cancel(), cancelHandler);
		
		this.dto = dto;

		if(dto instanceof PortailRepositoryItem){
			lblRequestInfo.setText(ToolsGWT.lblCnst.RequestAccess_YourAskingForAccessOn() + " : " + ((PortailRepositoryItem)dto).getName());
		}
		else if(dto instanceof DocumentVersionDTO){
			lblRequestInfo.setText(ToolsGWT.lblCnst.RequestAccess_YourAskingForAccessOn() + " : " + ((DocumentVersionDTO)dto).getName());
		}
		lblRequestInfoMsg.setText(ToolsGWT.lblCnst.RequestAccess_MessageToSend() + " : ");
	}

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
	        RequestAccessDialog.this.hide();
		}
	};
	
	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(dto instanceof PortailRepositoryItem){
				AccessRequestService.Connect.getInstance().addAccessRequestOnRepositoryItem((PortailRepositoryItem)dto, 
						txtRequestInfoMsg.getText(), new AsyncCallback<Void>() {
					
					public void onFailure(Throwable caught) {
						RequestAccessDialog.this.hide();
						
						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
					}
					@Override
					public void onSuccess(Void result) {
						RequestAccessDialog.this.hide();
						
						MessageHelper.openMessageDialog(ToolsGWT.lblCnst.RequestAccess_AskForAccess(), 
								ToolsGWT.lblCnst.RequestAccess_RequestSent());
					}
				});
			}
			else if(dto instanceof DocumentVersionDTO){
				AccessRequestService.Connect.getInstance().addAccessRequestGedDocument((DocumentVersionDTO)dto, 
						txtRequestInfoMsg.getText(), new AsyncCallback<Void>() {
					
					public void onFailure(Throwable caught) {
						RequestAccessDialog.this.hide();
						
						caught.printStackTrace();
						
						ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
					}
					@Override
					public void onSuccess(Void result) {
						RequestAccessDialog.this.hide();
						
						MessageHelper.openMessageDialog(ToolsGWT.lblCnst.RequestAccess_AskForAccess(), 
								ToolsGWT.lblCnst.RequestAccess_RequestSent());
					}
				});
			}
		}
	};
}
