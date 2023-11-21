package bpm.gwt.commons.shared.viewer;

import bpm.vanilla.platform.core.repository.IRepositoryObject;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DisplayItem implements IRepositoryObject, IsSerializable{
	
	private static final long serialVersionUID = 1L;
	
	/*
	 * This key is used to retrieve the report in the reporting component
	 */
	private String key;
	private String outputFormat;

	//Comment Part
	private boolean isCommentable = false;
	private Integer itemId;
	private Integer type;
	
	
	public DisplayItem() { }

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setCommentable(boolean isCommentable) {
		this.isCommentable = isCommentable;
	}
	
	public boolean isCommentable() {
		return isCommentable;
	}
	
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	
	public Integer getItemId() {
		return itemId;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}
	
	public Integer getType() {
		return type;
	}

	@Override
	public String getName() {
		if(getKey() != null && getKey().contains("vanilla_files")){
			return "Item";
		}
		else {
			String[] names = getKey().split("_");
			return names[0];
		}
	}

	@Override
	public int getId() {
		return itemId;
	}
}
