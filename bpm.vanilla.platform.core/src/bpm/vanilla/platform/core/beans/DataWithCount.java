package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.List;

public class DataWithCount<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<T> items;
	private long itemsCount;
	
	public DataWithCount() {
	}
	
	public DataWithCount(List<T> items, long itemsCount) {
		this.items = items;
		this.itemsCount = itemsCount;
	}
	
	public List<T> getItems() {
		return items;
	}
	
	public long getItemsCount() {
		return itemsCount;
	}

}
