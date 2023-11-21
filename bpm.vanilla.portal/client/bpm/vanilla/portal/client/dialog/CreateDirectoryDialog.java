package bpm.vanilla.portal.client.dialog;

import java.util.Date;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.services.BiPortalService;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CreateDirectoryDialog extends AbstractDialogBox {

	private static CreateDirectoryDialogUiBinder uiBinder = GWT
			.create(CreateDirectoryDialogUiBinder.class);

	interface CreateDirectoryDialogUiBinder extends
			UiBinder<Widget, CreateDirectoryDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	Label lblName, lblCreationDate, lblComment;
	
	@UiField
	TextBox txtName, txtCreationDate;

	@UiField
	TextArea txtComment;

	private ContentDisplayPanel mainPanel;
	private int parentId;
	
	public CreateDirectoryDialog(ContentDisplayPanel mainPanel, int parentId) {
		super(ToolsGWT.lblCnst.Folder(), false, true);
		this.mainPanel = mainPanel;
		this.parentId = parentId;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(ToolsGWT.lblCnst.Ok(), okHandler, ToolsGWT.lblCnst.Cancel(), cancelHandler);
		
		lblName.setText(ToolsGWT.lblCnst.Name());
		lblCreationDate.setText(ToolsGWT.lblCnst.CreationDate());
		lblComment.setText(ToolsGWT.lblCnst.Comment());
		
		txtCreationDate.setText(new Date().toString());
		txtCreationDate.setEnabled(false);
	}
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			CreateDirectoryDialog.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			String name = txtName.getText();
			String comment = txtComment.getValue();
			
			if(name != null && !name.isEmpty()){
				
				biPortal.get().showWaitPart(true);
				
				BiPortalService.Connect.getInstance().addPersoDialog(parentId, name, comment, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						CreateDirectoryDialog.this.hide();
						
						biPortal.get().showWaitPart(false);
						mainPanel.refreshDataPanels();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						biPortal.get().showWaitPart(false);
						
						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
					}
				});
			}
			else {
				MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.NameMustBeFill());
			}
		}
	};
}
