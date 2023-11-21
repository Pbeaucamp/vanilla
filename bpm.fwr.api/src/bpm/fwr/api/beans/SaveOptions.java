package bpm.fwr.api.beans;

import java.io.Serializable;

public class SaveOptions implements Serializable {
	
	private String name;
	private String comment;
	private String internalVersion;
	private String publicVerson;
	
	private String group;
	
	private boolean privateItem = false;
	
	private Integer directoryItemid;
	private int directoryId;

	public SaveOptions() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getInternalVersion() {
		return internalVersion;
	}

	public void setInternalVersion(String internalVersion) {
		this.internalVersion = internalVersion;
	}

	public String getPublicVerson() {
		return publicVerson;
	}

	public void setPublicVerson(String publicVerson) {
		this.publicVerson = publicVerson;
	}

	public boolean isPrivateItem() {
		return privateItem;
	}

	public void setPrivateItem(boolean privateItem) {
		this.privateItem = privateItem;
	}

	public void setPrivateItem(String privateItem) {
		if (privateItem.equalsIgnoreCase("true")) {
			this.privateItem = true;
		}
		else {
			this.privateItem = false;
		}
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}

	public void setDirectoryId(String directoryId) {
		this.directoryId = Integer.parseInt(directoryId);
	}

	public String getXml(String spaceBefore) {
		StringBuffer buf = new StringBuffer(spaceBefore + "<saveOptions>\n");
		buf.append(spaceBefore + "    <name>" + this.name + "</name>\n");
		buf.append(spaceBefore + "    <comment>" + this.comment + "</comment>\n");
		buf.append(spaceBefore + "    <internalVersion>" + this.internalVersion + "</internalVersion>\n");
		buf.append(spaceBefore + "    <publicVerson>" + this.publicVerson + "</publicVerson>\n");
		buf.append(spaceBefore + "    <privateItem>" + this.privateItem + "</privateItem>\n");
		buf.append(spaceBefore + "    <group>" + this.group + "</group>\n");
		buf.append(spaceBefore + "    <directoryId>" + this.directoryId + "</directoryId>\n");
		buf.append(spaceBefore + "</saveOptions>\n");

		return buf.toString();
	}

	public void setDirectoryItemid(Integer directoryItemid) {
		this.directoryItemid = directoryItemid;
	}

	public Integer getDirectoryItemid() {
		return directoryItemid;
	}

}
