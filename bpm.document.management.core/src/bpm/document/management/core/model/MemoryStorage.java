package bpm.document.management.core.model;

import java.io.Serializable;

public class MemoryStorage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int storageMemId=0;
	private String memory="";
	private float pricePerDollar=0;
	
	public int getStorageMemId() {
		return storageMemId;
	}
	public void setStorageMemId(int storageMemId) {
		this.storageMemId = storageMemId;
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
