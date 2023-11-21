package bpm.document.management.core.model;

import java.io.Serializable;

public class RMFolder implements Serializable {

	private static final long serialVersionUID = 1L;

	protected int id;
	protected int folderId;
	private int enterpriseId;
	private String description;
	private long lockDays;
	private long suspendDays;
	private long transferDays;
	private long destroyDays;
	private int userId;

	public RMFolder() {
		// TODO Auto-generated constructor stub
	}

	public RMFolder(String description, long lockDays, long suspendDays, long transferDays, long destroyDays) {
		super();
		this.description = description;
		this.lockDays = lockDays;
		this.suspendDays = suspendDays;
		this.transferDays = transferDays;
		this.destroyDays = destroyDays;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	public int getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(int enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getLockDays() {
		return lockDays;
	}

	public void setLockDays(long lockDays) {
		this.lockDays = lockDays;
	}

	public long getSuspendDays() {
		return suspendDays;
	}

	public void setSuspendDays(long suspendDays) {
		this.suspendDays = suspendDays;
	}

	public long getTransferDays() {
		return transferDays;
	}

	public void setTransferDays(long transferDays) {
		this.transferDays = transferDays;
	}

	public long getDestroyDays() {
		return destroyDays;
	}

	public void setDestroyDays(long destroyDays) {
		this.destroyDays = destroyDays;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
