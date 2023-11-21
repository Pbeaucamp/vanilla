package bpm.gwt.aklabox.commons.client.handlers;

import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.PermissionItem;
import bpm.gwt.aklabox.commons.client.tree.IActionManager;
import bpm.gwt.aklabox.commons.client.tree.IActionManager.ItemAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DirectoryMenu extends PopupPanel {

	public enum TypeCommand {
		NEW_FOLDER,
		NEW_DOCUMENT,
		UPDATE_FOLDER,
		DELETE_FOLDER,
		DOWNLOAD_FOLDER
	}
	
	private static DirectoryMenuUiBinder uiBinder = GWT.create(DirectoryMenuUiBinder.class);

	interface DirectoryMenuUiBinder extends UiBinder<Widget, DirectoryMenu> {
	}
	
	@UiField
	HTMLPanel panelMenu;
	
	@UiField
	MenuItem btnAddFolder, btnAddItem, btnUpdateFolder, btnDeleteFolder, btnDownloadFolder;

	private IActionManager actionManager;
	
	private boolean canShow = true;
	
	public DirectoryMenu(IActionManager actionManager, IObject item) {
		setWidget(uiBinder.createAndBindUi(this));

//		Enterprise enterpriseParent = options.getEnterprise();
//		Tree folderParent = options.getSelectedFolder();
	//IObject selectedItem = options.getItem();
		
		buildContent(actionManager, item);
	}
	

//	public DirectoryMenu(ExplorerPanel explorerPanel, User user, ItemTreeType type, DocumentTreeItem<Tree> treeItem) {
//		setWidget(uiBinder.createAndBindUi(this));
//		
//		Enterprise enterpriseParent = treeItem.getEnterpriseParent();
//		Tree folderParent = treeItem.getParentFolder();
//		
//		buildContent(explorerPanel, user, type, enterpriseParent, folderParent, treeItem.getItem());
//	}

	private void buildContent(IActionManager actionManager, IObject selectedItem) {
		this.actionManager = actionManager;
		
		PermissionItem permissionItem = selectedItem.getPermissionItem();

		boolean addFolder = permissionItem != null ? permissionItem.canAddFolder() : true;
		boolean uploadDoc = permissionItem != null ? permissionItem.canUploadDoc() : true;
		boolean edit = permissionItem != null ? permissionItem.canEdit() : true;
		boolean download = permissionItem != null ? permissionItem.canDownload() : true;
		boolean deleteItem = permissionItem != null ? permissionItem.canDeleteItem() : true;
		
//		if (folderParent != null) {
//			addFolder = true;
//			uploadDoc = true;
//		}
//		
//		if (type != ItemTreeType.RECYCLE) {
//			if (selectedItem != null) {
//				edit = true;
//				download = true;
//				deleteItem = true;
//			}
//		}
//
//		switch (type) {
//		case ENTERPRISE:
//			if (enterpriseParent == null || !enterpriseParent.isCanReadWrite()) {
//				if (folderParent != null) {
//					addFolder = false;
//					uploadDoc = false;
//				}
//
//				edit = false;
//				download = false;
//				deleteItem = false;
//			}
//			break;
//		case MY_DOCUMENTS:
//			break;
////		case PUBLIC:
////			break;
////		case SHARED_BY_ME:
////			break;
//		case SHARED_WITH_ME:
////		case SHARED_WITH_GROUPS:
//			break;
////		case WORKSPACE:
////			break;
////		case STARRED:
////			break;
////		case ADDED_RECENTLY:
////			break;
////		case VIEWED_RECENTLY:
////			break;
////		case MOST_VIEWED:
////			break;
////		case VALIDATE_DOC:
////			break;
//		case RECYCLE:
//			break;
////		case PERSONAL:
////			addFolder = false;
////			download = false;
////			uploadDoc = false;
////			break;
////		case MAIL_MANAGEMENT:
////			addFolder = false;
////			break;
//
//		default:
//			break;
//		}
//
//		/* KMO 09/2016 sroits users */
//		if (user.getUserType().equals(AklaboxConstant.READER)) {
//			addFolder = false;
//			uploadDoc = false;
//
//			edit = false;
//			deleteItem = false;
//		}
//		else if (user.getUserType().equals(AklaboxConstant.LOADER)) {
//			addFolder = false;
//			uploadDoc = false;
//
//			edit = false;
//			deleteItem = false;
//		}
//		else if (user.getUserType().equals(AklaboxConstant.PERSONAL_EDITION)) {
//			edit = false;
//		}
//		else if (user.getUserType().equals(AklaboxConstant.AUTHOR)) {
//		}
//		else if (user.getUserType().equals(AklaboxConstant.ADMIN)) {
//		}
//		else if (user.getUserType().equals(AklaboxConstant.PLATFORM_MANAGER)) {
//		}
//		else if (user.getUserType().equals(AklaboxConstant.CAMPAIGN_DISTRIBUTION)) {
//		}
//		else if (user.getUserType().equals(AklaboxConstant.RECORD_MANAGER)) {
//		}
//		else if (user.getUserType().equals(AklaboxConstant.ADMIN_SPACE)) {
//		}
//		else if (user.getUserType().equals(AklaboxConstant.EXTERNAL)) {
//			addFolder = false;
//			uploadDoc = false;
//			edit = false;
//			deleteItem = false;
//		}
		
//		//Test if folder/document is frozen
//		if (selectedItem != null || folderParent != null) {
//			PermissionItem permissionItem = selectedItem.getPermissionItem();
//			if (permissionItem.isLock()) {
//				addFolder = false;
//				uploadDoc = false;
//				edit = false;
//				deleteItem = false;
//			}
//			
////			PermissionItem frozen = null;
////			if (selectedItem != null && selectedItem instanceof Documents) {
////				frozen = ((Documents) selectedItem).getParent().getFrozeDocument();
////			}
////			else if (selectedItem != null && selectedItem instanceof Tree) {
////				frozen = ((Tree) selectedItem).getFrozeDocument();
////			}
////			
////			if (folderParent != null) {
////				frozen = frozen != null && frozen.isFrozen() ? frozen : folderParent.getFrozeDocument();
////			}
////			
////			if (frozen != null && frozen.isFrozen()) {
////				addFolder = false;
////				uploadDoc = false;
////				edit = false;
////				deleteItem = false;
////			}
//		}

		/* ------------------------ */
		setVisibility(btnAddFolder, addFolder);
		setVisibility(btnAddItem, uploadDoc);
		setVisibility(btnUpdateFolder, edit);
		setVisibility(btnDeleteFolder, deleteItem);
		setVisibility(btnDownloadFolder, download);

		btnAddFolder.setScheduledCommand(new DirectoryCommand(selectedItem, TypeCommand.NEW_FOLDER));
		btnAddItem.setScheduledCommand(new DirectoryCommand(selectedItem, TypeCommand.NEW_DOCUMENT));
		btnUpdateFolder.setScheduledCommand(new DirectoryCommand(selectedItem, TypeCommand.UPDATE_FOLDER));
		btnDeleteFolder.setScheduledCommand(new DirectoryCommand(selectedItem, TypeCommand.DELETE_FOLDER));
		btnDownloadFolder.setScheduledCommand(new DirectoryCommand(selectedItem, TypeCommand.DOWNLOAD_FOLDER));
		
		if (!addFolder && !uploadDoc && !edit && !deleteItem && !download) {
			this.canShow = false;
		}
	}
	
	public boolean canShow() {
		return canShow;
	}

	private void setVisibility(MenuItem widget, boolean visible) {
		widget.setVisible(visible);
	}
	
	public class DirectoryCommand implements Command {
		
		private IObject item;
		private TypeCommand command;

		public DirectoryCommand(IObject item, TypeCommand command) {
			this.item = item;
			this.command = command;
		}

		@Override
		public void execute() {
			hide();
			
			switch (command) {
			case NEW_FOLDER:
				actionManager.launchAction(item, ItemAction.ADD_FOLDER);
				break;
			case NEW_DOCUMENT:
				actionManager.launchAction(item, ItemAction.UPLOAD);
				break;
			case UPDATE_FOLDER:
				actionManager.launchAction(item, ItemAction.EDIT);
				break;
			case DELETE_FOLDER:
				actionManager.launchAction(item, ItemAction.DELETE);
				break;
			case DOWNLOAD_FOLDER:
				actionManager.launchAction(item, ItemAction.DOWNLOAD_FOLDER);
				break;

			default:
				break;
			}
		}

	}
}
