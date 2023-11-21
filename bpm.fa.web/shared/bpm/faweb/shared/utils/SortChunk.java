package bpm.faweb.shared.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.shared.infoscube.ItemCube;

public class SortChunk {

	private String uname;
	private List<ArrayList<ItemCube>> items = new ArrayList<ArrayList<ItemCube>>();
	private int startIndex;
	private String label;

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public List<ArrayList<ItemCube>> getItems() {
		return items;
	}

	public void setItems(List<ArrayList<ItemCube>> items) {
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
