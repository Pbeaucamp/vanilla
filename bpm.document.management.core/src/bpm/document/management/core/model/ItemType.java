package bpm.document.management.core.model;

import java.io.Serializable;

public class ItemType implements Serializable{

	private static final long serialVersionUID = 1L;

	private int itemTypeId=0;
	private String itemLogo="";
	private String itemName="";
	
	public int getItemTypeId() {
		return itemTypeId;
	}
	public void setItemTypeId(int itemTypeId) {
		this.itemTypeId = itemTypeId;
	}
	public String getItemLogo() {
		return itemLogo;
	}
	public void setItemLogo(String itemLogo) {
		this.itemLogo = itemLogo;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
}
