package bpm.mdm.model.supplier;

import java.io.Serializable;

import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DocumentItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	
	private int contractId;
	private int itemId;
	
	private RepositoryItem item;
	
	private boolean input;
	
	public DocumentItem() { }
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getContractId() {
		return contractId;
	}
	
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	
	public int getItemId() {
		return item != null ? item.getId() : itemId;
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public RepositoryItem getItem() {
		return item;
	}
	
	public void setItem(RepositoryItem item) {
		this.item = item;
	}
	
	public boolean isInput() {
		return input;
	}
	
	public void setInput(boolean input) {
		this.input = input;
	}
	
}
