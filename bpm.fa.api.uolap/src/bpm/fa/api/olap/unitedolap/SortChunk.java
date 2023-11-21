package bpm.fa.api.olap.unitedolap;

import java.util.ArrayList;
import java.util.List;

import bpm.fa.api.item.Item;

public class SortChunk {

	private String uname;
	private List<ArrayList<Item>> items = new ArrayList<ArrayList<Item>>();
	private int startIndex;
	private String label;

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public List<ArrayList<Item>> getItems() {
		return items;
	}

	public void setItems(List<ArrayList<Item>> items) {
		this.items = items;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public void setLabel(String label) {
		this.label = label;
		
	}

	public String getLabel() {
		return label;
	}

}
