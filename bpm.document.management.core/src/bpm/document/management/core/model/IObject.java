package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public interface IObject extends Serializable {
	
	public static final String FOLDER_TYPE = "Folders";
	
	public enum ItemType {
		DOCUMENT,
		FOLDER,
		ALL
	}
	
	/**
	 * 
	 * This enumeration allows to get the content according to the selected ItemTreeType
	 * PRIVATE = Private items for a user
	 * DOCUMENTARY_SPACE_ROOT = Root folder for the enterprise (parentId)
	 * DOCUMENTARY_SPACE = Items in a folder (parentId) included in a Documentary Space
	 * SHARED_WITH_ME = Items shared with me
	 *
	 */
	public enum ItemTreeType {
		MY_DOCUMENTS,
		ENTERPRISE,
		SHARED_WITH_ME,
		RECYCLE
		//Disable for now
//		MAIL_MANAGEMENT
	}
	
//	ENTERPRISE,
//	MY_DOCUMENTS,
//	PUBLIC,
//	SHARED_BY_ME,
//	SHARED_WITH_ME,
//	SHARED_WITH_GROUPS,
//	WORKSPACE,
//	STARRED,
//	ADDED_RECENTLY,
//	VIEWED_RECENTLY,
//	MOST_VIEWED,
//	VALIDATE_DOC,
//	RECYCLE, 
//	PERSONAL,
//	MAIL_MANAGEMENT;
	
	public int getId();

	public String getName();

	public Integer getParentId();

	public String getTreeType();

	public int getUserId();

	public String getTreeItemType();

	public int getFileSize();

	public String getAuthorName();

	public Date getCreationDate();
	
	public void setParent(Tree parent);
	
	public Tree getParent();
	
	public Enterprise getEnterpriseParent();
	
	public PermissionItem getPermissionItem();
	
	public void setPermissionItem(PermissionItem permissionItem);
	
	public boolean isFromEnterprise();
	
	public void setFromEnterprise(boolean isFromEnterprise);
	
	public boolean isShare();
	
	
	
}
