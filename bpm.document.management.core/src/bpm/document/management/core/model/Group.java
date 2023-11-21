package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Group implements Serializable {

	private static final long serialVersionUID = 1L;

	private int groupId = 0;
	private String groupName = "";
	private String groupDescription = "";
	private boolean readWrite = false;
	private boolean noAccess = false;
	private boolean readAccess = false;
	private boolean writeAccess = false;
	private int noOfUsers = 0;
	private Date creationDate = new Date();
	private String groupMail = "";
	
	private List<IObject> childs;

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public boolean isReadWrite() {
		return readWrite;
	}

	public void setReadWrite(boolean readWrite) {
		this.readWrite = readWrite;
	}

	public boolean isNoAccess() {
		return noAccess;
	}

	public void setNoAccess(boolean noAccess) {
		this.noAccess = noAccess;
	}

	public boolean isReadAccess() {
		return readAccess;
	}

	public void setReadAccess(boolean readAccess) {
		this.readAccess = readAccess;
	}

	public boolean isWriteAccess() {
		return writeAccess;
	}

	public void setWriteAccess(boolean writeAccess) {
		this.writeAccess = writeAccess;
	}

	public int getNoOfUsers() {
		return noOfUsers;
	}

	public void setNoOfUsers(int noOfUsers) {
		this.noOfUsers = noOfUsers;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getGroupMail() {
		return groupMail;
	}

	public void setGroupMail(String groupMail) {
		this.groupMail = groupMail;
	}
	
	public List<IObject> getChilds() {
		return childs;
	}
	
	public void setChilds(List<IObject> childs) {
		this.childs = childs;
	}

	@Override
	public String toString() {
		return groupName != null ? groupName : "";
	}
}
