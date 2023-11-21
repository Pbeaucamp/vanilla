package bpm.united.olap.api.model.impl;

import bpm.united.olap.api.datasource.DataObjectItem;

public class ClosureLevel extends LevelImpl {

	private DataObjectItem parentItem;
	private DataObjectItem childItem;
	
	public void setParentItem(DataObjectItem parentItem) {
		this.parentItem = parentItem;
	}
	
	public DataObjectItem getParentItem() {
		return parentItem;
	}
	
	public void setChildItem(DataObjectItem childItem) {
		this.childItem = childItem;
	}
	
	public DataObjectItem getChildItem() {
		return childItem;
	}
	
}
