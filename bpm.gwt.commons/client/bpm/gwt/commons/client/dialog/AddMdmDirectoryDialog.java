package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.mdm.model.supplier.MdmDirectory;

public class AddMdmDirectoryDialog extends AbstractDialogBox {

	private static AddDirectoryDialogUiBinder uiBinder = GWT.create(AddDirectoryDialogUiBinder.class);

	interface AddDirectoryDialogUiBinder extends UiBinder<Widget, AddMdmDirectoryDialog> {
	}
	
	@UiField
	LabelTextBox txtName;
	
	@UiField
	LabelTextArea txtDescription;

	private IWait waitPanel;
	
	private Integer parentId;
	private MdmDirectory directory;
	
	public AddMdmDirectoryDialog(IWait waitPanel, Integer parentId, MdmDirectory directory) {
		super(LabelsConstants.lblCnst.AddDirectory(), false, true);
		this.waitPanel = waitPanel;
		this.parentId = parentId;
		this.directory = directory;

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
			
			if (directory == null) {
				directory = new MdmDirectory();
			}
			directory.setName(name);
			directory.setDescription(description);
			if (parentId != null) {
				directory.setParentId(parentId);
			}
			
			CommonService.Connect.getInstance().saveMdmDirectory(directory, new GwtCallbackWrapper<MdmDirectory>(waitPanel, true, true) {

				@Override
				public void onSuccess(MdmDirectory result) {
					AddMdmDirectoryDialog.this.hide();
				}
			}.getAsyncCallback());
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			AddMdmDirectoryDialog.this.hide();
		}
	};
}
