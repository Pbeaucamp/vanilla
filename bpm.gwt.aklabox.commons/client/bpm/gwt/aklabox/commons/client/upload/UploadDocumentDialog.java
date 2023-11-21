package bpm.gwt.aklabox.commons.client.upload;

import java.util.List;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.TypeProcess;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.CustomButton;
import bpm.gwt.aklabox.commons.client.dialogs.AbstractDialogBox;
import bpm.gwt.aklabox.commons.client.tree.IActionManager;
import bpm.gwt.aklabox.commons.client.upload.FileUploadComposite.IUploadDocumentManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class UploadDocumentDialog extends AbstractDialogBox implements IUploadDocumentManager {

	private static UploadDocumentDialogUiBinder uiBinder = GWT.create(UploadDocumentDialogUiBinder.class);

	interface UploadDocumentDialogUiBinder extends UiBinder<Widget, UploadDocumentDialog> {
	}

	@UiField
	HTMLPanel panelOptions, panelUpload;
	
	@UiField
	CustomButton btnChooseFile;
	
	private FileUploadManager uploader;
	
	private IActionManager actionManager;
	private Tree parent;
	
	private boolean isConfirm = false;

	public UploadDocumentDialog(IActionManager actionManager, String title, Tree parent) {
		super(title, false, true);
		this.actionManager = actionManager;
		this.parent = parent;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		uploader = new FileUploadManager(this, panelUpload, TypeProcess.UPLOAD_ONE_DOCUMENT, false);

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);
	}
	
	@UiHandler("btnChooseFile")
	public void onChooseFile(ClickEvent event) {
		uploader.chooseFile(parent.getId(), true, false);
	}

	@Override
	public void onStartUpload() {
		updateUi(true);
	}
	
	private void updateUi(boolean upload) {
		panelOptions.setVisible(!upload);
		panelUpload.setVisible(upload);
	}

	@Override
	public boolean isUploadAllowed() {
		return parent != null && parent.getPermissionItem() != null && parent.getPermissionItem().canUploadDoc();
	}

	@Override
	public int getParentId() {
		return parent.getId();
	}

	@Override
	public Enterprise getParentEnterprise() {
		return parent.getEnterpriseParent();
	}

	@Override
	public void refreshFolder() {
		if (actionManager != null) {
			actionManager.refresh(false, true, true);
		}
	}

	@Override
	public void onOptions() { }
	
	public boolean isConfirm() {
		return isConfirm;
	}

	@Override
	public void onUploadComplete(IObject newItem) {
		isConfirm = true;
		hide();
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			isConfirm = false;
			hide();
		}
	};
	
	public List<Documents> getDocument() {
		return uploader.getDocuments();
	}

	@Override
	public int getThemeColor() {
		return 0;
	}

	@Override
	public void validateItems() { }
}