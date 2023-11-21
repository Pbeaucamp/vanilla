package bpm.document.management.core.model;

import java.io.Serializable;

public class UserStorage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int storageUserId=0;
	private String memory="";
	private float pricePerDollar=0;

	public int getStorageUserId() {
		return storageUserId;
	}
	public void setStorageUserId(int storageUserId) {
		this.storageUserId = storageUserId;
	}
	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	public float getPricePerDollar() {
		return pricePerDollar;
	}
	public void setPricePerDollar(float pricePerDollar) {
		this.pricePerDollar = pricePerDollar;
	}

	
}
