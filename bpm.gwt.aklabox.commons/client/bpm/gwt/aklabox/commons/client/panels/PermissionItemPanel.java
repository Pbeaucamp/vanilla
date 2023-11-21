package bpm.gwt.aklabox.commons.client.panels;

import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemType;
import bpm.document.management.core.model.ItemRight;
import bpm.document.management.core.model.PermissionItem;
import bpm.document.management.core.model.Tree;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.CustomCheckbox;
import bpm.gwt.aklabox.commons.client.customs.LabelDateBox;
import bpm.gwt.aklabox.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PermissionItemPanel extends Composite {

	private static PermissionItemPanelUiBinder uiBinder = GWT.create(PermissionItemPanelUiBinder.class);

	interface PermissionItemPanelUiBinder extends UiBinder<Widget, PermissionItemPanel> {
	}
	
	@UiField
	CustomCheckbox chkPrint, chkEmail, chkCheckout, chkSeeVersions, chkDelete, chkLock, chkSuspend;
	
	@UiField
	LabelDateBox dbBeginLock, dbEndLock, dbBeginSuspend, dbEndSuspend;

	private PermissionItem permissionItem;
	
	public PermissionItemPanel(IObject item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.permissionItem = item.getPermissionItem() != null ? item.getPermissionItem() : new PermissionItem(item.getId(), item instanceof Tree ? ItemType.FOLDER : ItemType.DOCUMENT, ItemRight.READ_DOWNLOAD_WRITE);
		
		chkPrint.setValue(permissionItem.isCanPrint());
		chkEmail.setValue(permissionItem.isCanDelete());
		chkCheckout.setValue(permissionItem.isCanCheckout());
		chkSeeVersions.setValue(permissionItem.isCanSeeVersions());
		chkDelete.setValue(permissionItem.isCanDelete());
		
		chkLock.setValue(permissionItem.getLockDate() != null);
		dbBeginLock.setValue(permissionItem.getLockDate());
		dbEndLock.setValue(permissionItem.getUnlockDate());
		
		chkSuspend.setValue(permissionItem.getHideDate() != null);
		dbBeginSuspend.setValue(permissionItem.getHideDate());
		dbEndSuspend.setValue(permissionItem.getUnhideDate());
	}
	
	@UiHandler("chkLock")
	public void onLock(ValueChangeEvent<Boolean> event) {
		if(!chkLock.getValue() && permissionItem.isLockFromParent()){
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.ItemLockedFromParent());
			return;
		}
		updateUi();
	}
	
	@UiHandler("chkSuspend")
	public void onSuspend(ValueChangeEvent<Boolean> event) {
		updateUi();
	}
	
	private void updateUi() {
		dbBeginLock.setEnabled(chkLock.getValue());
		dbEndLock.setEnabled(chkLock.getValue());
		
		dbBeginSuspend.setEnabled(chkSuspend.getValue());
		dbEndSuspend.setEnabled(chkSuspend.getValue());
	}

	public void applySettings(PermissionItem permissionItem) {
		permissionItem.setCanPrint(chkPrint.getValue());
		permissionItem.setCanDelete(chkEmail.getValue());
		permissionItem.setCanCheckout(chkCheckout.getValue());
		permissionItem.setCanSeeVersions(chkSeeVersions.getValue());
		permissionItem.setCanDelete(chkDelete.getValue());
		
		if (chkLock.getValue()) {
			permissionItem.setLockDate(dbBeginLock.getValue());
			permissionItem.setUnlockDate(dbEndLock.getValue());
		}
		else {
			permissionItem.setLockDate(null);
			permissionItem.setUnlockDate(null);
		}

		if (chkSuspend.getValue()) {
			permissionItem.setHideDate(dbBeginSuspend.getValue());
			permissionItem.setUnhideDate(dbEndSuspend.getValue());
		}
		else {
			permissionItem.setHideDate(null);
			permissionItem.setUnhideDate(null);
		}
	}
}
