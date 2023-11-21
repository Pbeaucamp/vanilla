package bpm.united.olap.api.model.impl;

import bpm.united.olap.api.datasource.DataObjectItem;

public class ClosureHierarchy extends HierarchyImpl {

	private DataObjectItem parentItem;
	private DataObjectItem childItem;
	private DataObjectItem item;
	private DataObjectItem orderItem;
	private boolean levelsCreated = false;
	
	public DataObjectItem getParentItem() {
		return parentItem;
	}
	
	public void setParentItem(DataObjectItem parentItem) {
		this.parentItem = parentItem;
	}
	
	public DataObjectItem getChildItem() {
		return childItem;
	}
	
	public void setChildItem(DataObjectItem childItem) {
		this.childItem = childItem;
	}

	public DataObjectItem getItem() {
		return item;
	}

	public void setItem(DataObjectItem item) {
		this.item = item;
	}

	public DataObjectItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(DataObjectItem orderItem) {
		this.orderItem = orderItem;
	}

	public void setLevelsCreated(boolean levelsCreated) {
		this.levelsCreated = levelsCreated;
	}

	public boolean isLevelsCreated() {
		return levelsCreated;
	}
	
	public ClosureHierarchy() {
		isClosureHierarchy = true;
	}
}
