package bpm.document.management.core.model;

import java.io.Serializable;

public class PEStorage implements Serializable{

	private static final long serialVersionUID = 1L;

	private int initialStorage=0;
	private int storageIncrease=0;
	private int storageLimit=0;
	
	public PEStorage(){}
	
	public PEStorage(int initStorage, int sIncrease, int storeLimit) {
		this.initialStorage = initStorage;
		this.storageIncrease = sIncrease;
		this.storageLimit = storeLimit;
	}
	public int getInitialStorage() {
		return initialStorage;
	}
	public void setInitialStorage(int initialStorage) {
		this.initialStorage = initialStorage;
	}
	public int getStorageIncrease() {
		return storageIncrease;
	}
	public void setStorageIncrease(int storageIncrease) {
		this.storageIncrease = storageIncrease;
	}
	public int getStorageLimit() {
		return storageLimit;
	}
	public void setStorageLimit(int storageLimit) {
		this.storageLimit = storageLimit;
	}
}
