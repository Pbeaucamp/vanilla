package bpm.gwt.commons.client.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class AddDirectoryDialog extends AbstractDialogBox {

	private static AddDirectoryDialogUiBinder uiBinder = GWT.create(AddDirectoryDialogUiBinder.class);

	interface AddDirectoryDialogUiBinder extends UiBinder<Widget, AddDirectoryDialog> {
	}
	
	@UiField
	LabelTextBox txtName;
	
	@UiField
	LabelTextArea txtDescription;

	private IWait waitPanel;
	private RepositoryDirectory parent;
	
	public AddDirectoryDialog(IWait waitPanel, RepositoryDirectory parent) {
		super(LabelsConstants.lblCnst.AddDirectory(), false, true);
		this.waitPanel = waitPanel;
		this.parent = parent;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			String name = txtName.getText();
			String description = txtDescription.getText();
			
			if (name.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedName());
				return;
			}
			
			waitPanel.showWaitPart(true);
			CommonService.Connect.getInstance().addDirectory(name, description, parent, new GwtCallbackWrapper<Void>(waitPanel, true) {

				@Override
				public void onSuccess(Void result) {
					AddDirectoryDialog.this.hide();
				}
			}.getAsyncCallback());
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			AddDirectoryDialog.this.hide();
		}
	};
}
