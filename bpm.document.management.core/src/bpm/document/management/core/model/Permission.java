package bpm.document.management.core.model;

import java.io.Serializable;

public class Permission implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ShareType {
		SHARE, DOCUMENTARY_SPACE
	}

	private int id;

	private int permissionItemId;
	private int userId;

	private ShareType shareType;
	private ItemRight right;
	
	private User user;

	public Permission() { }

	public Permission(User user, ShareType shareType, ItemRight right) {
		this.userId = user.getUserId();
		this.user = user;
		this.shareType = shareType;
		this.right = right;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPermissionItemId() {
		return permissionItemId;
	}

	public void setPermissionItemId(int permissionItemId) {
		this.permissionItemId = permissionItemId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public ShareType getShareType() {
		return shareType;
	}

	public void setShareType(ShareType shareType) {
		this.shareType = shareType;
	}

	public ItemRight getRight() {
		return right;
	}

	public void setRight(ItemRight right) {
		this.right = right;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
