package bpm.united.olap.api.cache.impl;

public class MemoryCacheStatistics {
	private long itemNumbers;
	private long maxSize;
	private long usedSize;
	private int hits;
	private int missed;
	
	public MemoryCacheStatistics(long itemNumbers, long maxSize, long usedSize,
			int hits, int missed) {
		super();
		this.itemNumbers = itemNumbers;
		this.maxSize = maxSize;
		this.usedSize = usedSize;
		this.hits = hits;
		this.missed = missed;
	}

	/**
	 * @return the itemNumbers
	 */
	public long getItemNumbers() {
		return itemNumbers;
	}

	/**
	 * @return the maxSize
	 */
	public long getMaxSize() {
		return maxSize;
	}

	/**
	 * @return the usedSize
	 */
	public long getUsedSize() {
		return usedSize;
	}

	/**
	 * @return the hits
	 */
	public int getHits() {
		return hits;
	}

	/**
	 * @return the missed
	 */
	public int getMissed() {
		return missed;
	}
	
	
	
}
