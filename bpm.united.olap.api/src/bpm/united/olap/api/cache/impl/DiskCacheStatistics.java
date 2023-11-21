package bpm.united.olap.api.cache.impl;

public class DiskCacheStatistics {
	private long currentSize;
	private long storageSize;
	
	public DiskCacheStatistics(long currentSize, long storageSize) {
		super();
		this.currentSize = currentSize;
		this.storageSize = storageSize;
	}
	/**
	 * @return the currentSize
	 */
	public long getCurrentSize() {
		return currentSize;
	}
	/**
	 * @return the storageSize
	 */
	public long getStorageSize() {
		return storageSize;
	}
	
	
	
}
