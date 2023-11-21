package bpm.vanilla.portal.client.dialog;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.AccessRequest;
import bpm.vanilla.portal.client.panels.center.AccessRequestPanel;
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

public class RequestAccessResponseDialog extends AbstractDialogBox {

	private static RequestAccessDialogUiBinder uiBinder = GWT
			.create(RequestAccessDialogUiBinder.class);

	interface RequestAccessDialogUiBinder extends
			UiBinder<Widget, RequestAccessResponseDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	Label lblRequestInfo, lblRequestInfoMsg;
	
	@UiField
	TextArea txtRequestInfoMsg;
	
	private AccessRequest dto;
	private boolean approve;
	private AccessRequestPanel requestPanel;
	
	public RequestAccessResponseDialog(AccessRequestPanel requestPanel, AccessRequest dto, String title, boolean approve) {
		super(title, false, true);
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(ToolsGWT.lblCnst.Ok(), okHandler, ToolsGWT.lblCnst.Cancel(), cancelHandler);

		this.requestPanel = requestPanel;
		this.approve = approve;
		this.dto = dto;
		
		if (approve) {
			lblRequestInfo.setText(ToolsGWT.lblCnst.RequestAccess_DialogResponse_ApprovingDemandOnItem() + " : " + dto.getItemName());
		}
		else {
			lblRequestInfo.setText(ToolsGWT.lblCnst.RequestAccess_DialogResponse_RefusingDemandOnItem() + " : " + dto.getItemName());
		}
		
		lblRequestInfoMsg.setText(ToolsGWT.lblCnst.RequestAccess_DialogResponse_MessageDemandOnItem() + " : ");
	}
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
	        RequestAccessResponseDialog.this.hide();
		}
	};
	
	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			dto.setAnswerInfo(txtRequestInfoMsg.getText());
			
			if (approve) {
				AccessRequestService.Connect.getInstance().approveRequest(dto, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						RequestAccessResponseDialog.this.hide();
						
						ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
					}
					
					@Override
					public void onSuccess(Void result) {
						RequestAccessResponseDialog.this.hide();
						
						requestPanel.loadData();
						
						MessageHelper.openMessageDialog(ToolsGWT.lblCnst.RequestAccess_Table_Approve(), 
								ToolsGWT.lblCnst.RequestAccess_DialogResponse_AnswerSent());
						
					}
				});
			}
			else {
				AccessRequestService.Connect.getInstance().refuseRequest(dto, new AsyncCallback<Void>() {
					
					@Override
					public void onFailure(Throwable caught) {
						RequestAccessResponseDialog.this.hide();
						
						ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
					}
					
					@Override
					public void onSuccess(Void result) {
						RequestAccessResponseDialog.this.hide();
						
						requestPanel.loadData();
						
						MessageHelper.openMessageDialog(ToolsGWT.lblCnst.RequestAccess_Table_Refuse(), 
								ToolsGWT.lblCnst.RequestAccess_DialogResponse_AnswerSent());
					}
				});
			}
		}
	};
}
