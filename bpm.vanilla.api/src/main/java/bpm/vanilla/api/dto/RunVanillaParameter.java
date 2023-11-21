package bpm.vanilla.api.dto;

import java.util.List;

import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;

public class RunVanillaParameter {

	private int groupId;
	private int repositoryId;

	private int itemId;
	private String outputName;
	private String format;
	
	private List<VanillaGroupParameter> groupParams;
	private List<String> mails;

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public String getOutputName() {
		return outputName;
	}
	
	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}

	public List<VanillaGroupParameter> getGroupParams() {
		return groupParams;
	}
	
	public void setGroupParams(List<VanillaGroupParameter> groupParams) {
		this.groupParams = groupParams;
	}

	public List<String> getMails() {
		return mails;
	}
	
	public void setMails(List<String> mails) {
		this.mails = mails;
	}
}
