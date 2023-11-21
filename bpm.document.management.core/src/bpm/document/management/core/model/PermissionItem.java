package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

import bpm.document.management.core.model.IObject.ItemType;

public class PermissionItem implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/*
	 * If the ShareType is SHARE
	 * 	EVERYONE means PUBLIC and there is a hash available (publicUrl)
	 * 
	 * If the ShareType is DOCUMENTARY_SPACE
	 * 	EVERYONE means everyone that has the right of the ED has permission on this document
	 */
	public enum ShareStatus {
		EVERYONE,
		BY_USERS
	}

	private int id;

	private ItemType itemType;
	private int itemId;

	private boolean canPrint = true;
	private boolean canEmail = true;
	private boolean canCheckout = true;
	private boolean canSeeVersions = true;
	private boolean canDelete = true;

	private Date lockDate;
	private Date unlockDate;
	//Value not save in DB, set to true if one of his parent folder is lock
	private boolean lockFromParent;

	private Date hideDate;
	private Date unhideDate;
	
	//This is used to restrict permission on a folder or document for users of Documentary Space
	private ShareStatus documentarySpaceStatus;
	//This is used to Share the folder or document to other users than documentary space
	private ShareStatus shareStatus;
	
	//Hash used for public access
	private String hash;
	private String sharePassword;
	
	//Date used to limit the share in time
	private Date sharePeremptionDate;
	
	//If the ShareStatus is set to EVERYONE, we use those right
	private ItemRight right;
	//If the ShareStatus is set to BY_USERS, we use those specific right set for the user
	//This is not saved in database and is set when we get items from DB according to the user
	private ItemRight userRight;


	
	//We define the right for the user at runtime according to parameters
	private boolean canOpen;
	private boolean isLink;
	private boolean isCheckoutByOther;
	private boolean isSelectedItemLock;
	private boolean canEdit;
	private boolean canDownload;
	private boolean canDeleteItem;
	private boolean canAddFolder;
	private boolean canUploadDoc;
	private boolean canCreateDoc;

	private boolean displayWorkflow;
	private boolean showAdress;
	private boolean canCopy;
	private boolean displayRights;
	private boolean displayShare;
	private boolean displayFrozen;
	
	private boolean displayTag;
	private boolean displayMarketOptions;
	private boolean displayFinalMarket;
	private boolean displayRunWorkflow;
	
	private boolean canCloseProject;
	private boolean canArchive;
	private boolean canFinishArchive;
	
	public PermissionItem() {
	}

	public PermissionItem(int itemId, IObject.ItemType itemType, ItemRight right) {
		this.itemId = itemId;
		this.itemType = itemType;
		this.right = right;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * Don't call this, call {@link bpm.document.management.core.model.PermissionItem#getApplicableRight()}
	 */
	public ItemRight getRight() {
		return right;
	}

	public void setRight(ItemRight right) {
		this.right = right;
	}

	public boolean isCanPrint() {
		return canPrint;
	}

	public void setCanPrint(boolean canPrint) {
		this.canPrint = canPrint;
	}

	public boolean isCanEmail() {
		return canEmail;
	}

	public void setCanEmail(boolean canEmail) {
		this.canEmail = canEmail;
	}

	public boolean isCanCheckout() {
		return canCheckout;
	}

	public void setCanCheckout(boolean canCheckout) {
		this.canCheckout = canCheckout;
	}

	public boolean isCanSeeVersions() {
		return canSeeVersions;
	}

	public void setCanSeeVersions(boolean canSeeVersions) {
		this.canSeeVersions = canSeeVersions;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public Date getLockDate() {
		return lockDate;
	}

	public void setLockDate(Date lockDate) {
		this.lockDate = lockDate;
	}

	public Date getUnlockDate() {
		return unlockDate;
	}

	public void setUnlockDate(Date unlockDate) {
		this.unlockDate = unlockDate;
	}
	
	public boolean isLockFromParent() {
		return lockFromParent;
	}
	
	public void setLockFromParent(boolean lockFromParent) {
		this.lockFromParent = lockFromParent;
	}

	public Date getHideDate() {
		return hideDate;
	}

	public void setHideDate(Date hideDate) {
		this.hideDate = hideDate;
	}

	public Date getUnhideDate() {
		return unhideDate;
	}

	public void setUnhideDate(Date unhideDate) {
		this.unhideDate = unhideDate;
	}

	public ShareStatus getDocumentarySpaceStatus() {
		return documentarySpaceStatus;
	}

	public void setDocumentarySpaceStatus(ShareStatus documentarySpaceStatus) {
		this.documentarySpaceStatus = documentarySpaceStatus;
	}

	public ShareStatus getShareStatus() {
		return shareStatus;
	}

	public void setShareStatus(ShareStatus shareStatus) {
		this.shareStatus = shareStatus;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public String getSharePassword() {
		return sharePassword;
	}
	
	public void setSharePassword(String sharePassword) {
		this.sharePassword = sharePassword;
	}
	
	public boolean hasSharePassword() {
		return sharePassword != null && !sharePassword.isEmpty();
	}
	
	public Date getSharePeremptionDate() {
		return sharePeremptionDate;
	}
	
	public void setSharePeremptionDate(Date sharePeremptionDate) {
		this.sharePeremptionDate = sharePeremptionDate;
	}

	/**
	 * Don't call this, call {@link bpm.document.management.core.model.PermissionItem#getApplicableRight()}
	 */
	public ItemRight getUserRight() {
		return userRight;
	}

	public void setUserRight(ItemRight userRight) {
		this.userRight = userRight;
	}
	
	/**
	 * Return the right for the user. If they are not set we use the global itemRight
	 * 
	 * @return ItemRight
	 */
	public ItemRight getApplicableRight() {
		return userRight != null ? userRight : right;
	}
	
	public boolean isHide() {
		if (hideDate != null) {
			Date currentDate = new Date();
			return hideDate.before(currentDate) && (unhideDate == null || unhideDate.after(currentDate));
		}
		return false;
	}
	
	public boolean isLock() {
		if (isLockFromParent()) {
			return true;
		}
		
		if (lockDate != null) {
			Date currentDate = new Date();
			return lockDate.before(currentDate) && (unlockDate == null || unlockDate.after(currentDate));
		}
		return false;
	}
	
	/**
	 * Check if the document is shared and the peremption date is null or after today
	 * 
	 * @return true if the document is shared
	 */
	public boolean isShare() {
		return shareStatus != null && (sharePeremptionDate == null || sharePeremptionDate.after(new Date()));
	}

	/**
	 * Check if the document is public and the peremption date is null or after today
	 * 
	 * @return true if the document is shared
	 */
	public boolean isPublic() {
		return hash != null && (sharePeremptionDate == null || sharePeremptionDate.after(new Date()));
	}

	/**
	 * This method duplicate the object.
	 * It is used when you get the permissionItem from the parent
	 * 
	 * @return PermissionItem
	 */
	public PermissionItem duplicate() {
		PermissionItem permission = new PermissionItem();
		permission.setItemType(itemType);
		permission.setItemId(itemId);
		permission.setCanPrint(canPrint);
		permission.setCanEmail(canEmail);
		permission.setCanCheckout(canCheckout);
		permission.setCanSeeVersions(canSeeVersions);
		permission.setCanDelete(canDelete);
		permission.setLockDate(lockDate);
		permission.setUnlockDate(unlockDate);
		permission.setLockFromParent(lockFromParent);
		permission.setHideDate(hideDate);
		permission.setUnhideDate(unhideDate);
		permission.setDocumentarySpaceStatus(documentarySpaceStatus);
		permission.setShareStatus(shareStatus);
		permission.setHash(hash);
		permission.setSharePassword(sharePassword);
		permission.setSharePeremptionDate(sharePeremptionDate);
		permission.setRight(right);
		permission.setUserRight(userRight);
		return permission;
	}

	
	//We define the right for the user at runtime according to parameters
	public boolean canOpen() {
		return canOpen;
	}

	public void setCanOpen(boolean canOpen) {
		this.canOpen = canOpen;
	}

	public boolean isLink() {
		return isLink;
	}

	public void setLink(boolean isLink) {
		this.isLink = isLink;
	}

	public boolean isCheckoutByOther() {
		return isCheckoutByOther;
	}

	public void setCheckoutByOther(boolean isCheckoutByOther) {
		this.isCheckoutByOther = isCheckoutByOther;
	}

	public boolean isSelectedItemLock() {
		return isSelectedItemLock;
	}

	public void setSelectedItemLock(boolean isSelectedItemLock) {
		this.isSelectedItemLock = isSelectedItemLock;
	}

	public boolean canEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public boolean canDownload() {
		return canDownload;
	}

	public void setCanDownload(boolean canDownload) {
		this.canDownload = canDownload;
	}

	public boolean canDeleteItem() {
		return canDeleteItem;
	}

	public void setCanDeleteItem(boolean canDeleteItem) {
		this.canDeleteItem = canDeleteItem;
	}

	public boolean canAddFolder() {
		return canAddFolder;
	}

	public void setCanAddFolder(boolean canAddFolder) {
		this.canAddFolder = canAddFolder;
	}

	public boolean canUploadDoc() {
		return canUploadDoc;
	}

	public void setCanUploadDoc(boolean canUploadDoc) {
		this.canUploadDoc = canUploadDoc;
	}

	public boolean canCreateDoc() {
		return canCreateDoc;
	}

	public void setCanCreateDoc(boolean canCreateDoc) {
		this.canCreateDoc = canCreateDoc;
	}
	
	public boolean displayWorkflow() {
		return displayWorkflow;
	}
	
	public void setDisplayWorkflow(boolean displayWorkflow) {
		this.displayWorkflow = displayWorkflow;
	}
	
	public boolean showAdress() {
		return showAdress;
	}
	
	public void setShowAdress(boolean showAdress) {
		this.showAdress = showAdress;
	}
	
	public boolean canCopy() {
		return canCopy;
	}
	
	public void setCanCopy(boolean canCopy) {
		this.canCopy = canCopy;
	}
	
	public boolean displayRights() {
		return displayRights;
	}
	
	public void setDisplayRights(boolean displayRights) {
		this.displayRights = displayRights;
	}
	
	public boolean displayShare() {
		return displayShare;
	}
	
	public void setDisplayShare(boolean displayShare) {
		this.displayShare = displayShare;
	}
	
	public boolean displayFrozen() {
		return displayFrozen;
	}
	
	public void setDisplayFrozen(boolean displayFrozen) {
		this.displayFrozen = displayFrozen;
	}

	public boolean displayTag() {
		return displayTag;
	}
	
	public void setDisplayTag(boolean displayTag) {
		this.displayTag = displayTag;
	}
	
	public boolean displayMarketOptions() {
		return displayMarketOptions;
	}
	
	public void setDisplayMarketOptions(boolean displayMarketOptions) {
		this.displayMarketOptions = displayMarketOptions;
	}
	
	public boolean displayFinalMarket() {
		return displayFinalMarket;
	}
	
	public void setDisplayFinalMarket(boolean displayFinalMarket) {
		this.displayFinalMarket = displayFinalMarket;
	}
	
	public boolean canCloseProject() {
		return canCloseProject;
	}
	
	public void setCanCloseProject(boolean canCloseProject) {
		this.canCloseProject = canCloseProject;
	}

	public boolean displayRunWorkflow() {
		return displayRunWorkflow;
	}

	public void setDisplayRunWorkflow(boolean displayRunWorkflow) {
		this.displayRunWorkflow = displayRunWorkflow;
	}

	public boolean isCanArchive() {
		return canArchive;
	}

	public void setCanArchive(boolean canArchive) {
		this.canArchive = canArchive;
	}

	public boolean isCanFinishArchive() {
		return canFinishArchive;
	}

	public void setCanFinishArchive(boolean canFinishArchive) {
		this.canFinishArchive = canFinishArchive;
	}
	
	/**
	 * Return if the current user can read
	 * 
	 * @return true if the user can read
	 */
//	public boolean canRead() {
//		ItemRight right = getApplicableRight();
//		return right != null ? (right == ItemRight.READ || right == ItemRight.READ_DOWNLOAD || right == ItemRight.READ_DOWNLOAD_WRITE) : false;
//	}
	
	/**
	 * Return if the current user can download
	 * 
	 * @return true if the user can download
	 */
//	public boolean canDownload() {
//		ItemRight right = getApplicableRight();
//		return right != null ? (right == ItemRight.READ_DOWNLOAD || right ==ItemRight.READ_DOWNLOAD_WRITE) : false;
//	}
	
	/**
	 * Return if the current user can write
	 * 
	 * @return true if the user can write
	 */
//	public boolean canWrite() {
//		ItemRight right = getApplicableRight();
//		return right != null ? (right == ItemRight.READ_DOWNLOAD_WRITE) : false;
//	}
}
