package bpm.gwt.commons.shared.analysis;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DrillInformations implements IsSerializable{

	private int key;
	private int size;
	private List<String> columns;
	
	public DrillInformations() { }

	public DrillInformations(int key, List<String> columns, int size) {
		this.key = key;
		this.columns = columns;
		this.size = size;
	}
	
	public int getKey() {
		return key;
	}

	public int getSize() {
		return size;
	}

	public List<String> getColumns() {
		return columns;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
}
