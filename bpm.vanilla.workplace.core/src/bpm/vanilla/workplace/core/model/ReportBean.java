package bpm.vanilla.workplace.core.model;

import java.util.ArrayList;
import java.util.List;

public class ReportBean {
	private int directoryItemId;
	private String itemName;
	private List<String> files = new ArrayList<String>();

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

	public void setDirectoryItemId(String directoryItemId) {
		this.directoryItemId = Integer.parseInt(directoryItemId);
	}

	public List<String> getFiles() {
		return files;
	}

	public void addFile(String s) {
		files.add(s);
	}
}
