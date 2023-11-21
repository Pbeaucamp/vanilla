package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.EmailInfo;
import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.ManagerAction;
import bpm.document.management.core.model.Permission;
import bpm.document.management.core.model.EmailInfo.TypeEmail;
import bpm.document.management.core.model.Permission.ShareType;
import bpm.document.management.core.model.PermissionItem;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.InformationsDialog;
import bpm.gwt.aklabox.commons.client.panels.PermissionItemPanel;
import bpm.gwt.aklabox.commons.client.panels.ShareAndRestrictionPanel;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class PermissionItemDialog extends AbstractDialogBox {

	private static PermissionItemDialogUiBinder uiBinder = GWT.create(PermissionItemDialogUiBinder.class);

	interface PermissionItemDialogUiBinder extends UiBinder<Widget, PermissionItemDialog> {
	}
	
	@UiField
	HTMLPanel panelContent;
	
	private User currentUser;
	
	private PermissionItemPanel permissionItemPanel;
	private ShareAndRestrictionPanel shareAndRestrictionPanel;
	
	private IObject item;
	private ShareType shareType;
	
	private boolean isConfirm = false;

	public PermissionItemDialog(User currentUser, IObject item, Enterprise enterprise, ShareType shareType) {
		super(buildTitle(item, shareType), false, true);
		this.currentUser = currentUser;
		this.item = item;
		this.shareType = shareType;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		if (shareType == null || !(shareType == ShareType.SHARE)) {
			this.permissionItemPanel = new PermissionItemPanel(item);
			panelContent.add(permissionItemPanel);
		}
		
		if (shareType != null) {
			this.shareAndRestrictionPanel = new ShareAndRestrictionPanel(this, this, item.getPermissionItem(), enterprise, shareType);
			panelContent.add(shareAndRestrictionPanel);
		}
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private static String buildTitle(IObject item, ShareType shareType) {
		String itemType = item instanceof Tree ? LabelsConstants.lblCnst.folder() : LabelsConstants.lblCnst.document();
		if (shareType != null && shareType == ShareType.SHARE) {
			return LabelsConstants.lblCnst.ShareOptionsForItem() + " " + itemType + " '" + item.getName() + "'";
		}
		else {
			return LabelsConstants.lblCnst.RightsForItem() + " " + itemType + " '" + item.getName() + "'";
		}
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			final PermissionItem permissionItem = item.getPermissionItem();
			
			boolean savePermission = false;
			List<Permission> permissions = null;
			
			if (permissionItemPanel != null) {
				permissionItemPanel.applySettings(permissionItem);
			}
			
			if (shareAndRestrictionPanel != null) {
				shareAndRestrictionPanel.applySettings(permissionItem);
				
				savePermission = true;
				permissions = shareAndRestrictionPanel.getPermissions();
			}
			
			AklaCommonService.Connect.getService().managePermissionItem(item, permissionItem, ManagerAction.UPDATE, shareType, permissions, savePermission, new GwtCallbackWrapper<PermissionItem>(PermissionItemDialog.this, true, true) {

				@Override
				public void onSuccess(PermissionItem result) {
					item.setPermissionItem(result);
					
					isConfirm = true;
					
					if (shareAndRestrictionPanel != null && permissionItem.getHash() != null) {
						final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Yes(), LabelsConstants.lblCnst.No(), LabelsConstants.lblCnst.WouldYouLikeToSendTheLinkToSomebody(), true);
						dial.center();
						dial.addCloseHandler(new CloseHandler<PopupPanel>() {
							
							@Override
							public void onClose(CloseEvent<PopupPanel> event) {
								if (dial.isConfirm()) {
									sendLink(permissionItem);
								}
								else {
									hide();
								}
							}
						});
					}
					else {
						hide();
					}
				}
			}.getAsyncCallback());
		}
	};

	private void sendLink(final PermissionItem permissionItem) {
		final UsersMailSelectionDialog selectionDial = new UsersMailSelectionDialog();
		selectionDial.center();
		selectionDial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (selectionDial.isConfirm()) {
					List<String> emails = selectionDial.getSelectedMails();
					
					String publicHash = "FolderView?hash=" + permissionItem.getHash();
					
					List<EmailInfo> emailInfos = new ArrayList<>();
					for (String email : emails) {
						User user = new User();
						user.setEmail(email);
						
						emailInfos.add(new EmailInfo(TypeEmail.SHARE_PUBLIC_LINK, user, currentUser, item, permissionItem.getSharePassword(), publicHash));
					}
					
					AklaCommonService.Connect.getService().sendEmails(emailInfos, new GwtCallbackWrapper<Void>(PermissionItemDialog.this, true, true) {

						@Override
						public void onSuccess(Void result) {
							hide();
						}
					}.getAsyncCallback());
				}
				else {
					hide();
				}
			}
		});
	}

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			isConfirm = false;
			
			hide();
		}
	};

	@Override
	public int getThemeColor() {
		return 0;
	}
}
