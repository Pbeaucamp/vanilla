package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScanMetaObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1884639165343524210L;

	
	private Map<String, String> scannerInfos;
	private List<ScanMetaItem> items = new ArrayList<ScanMetaItem>();
	
	public ScanMetaObject() {
		super();
	}
	
	public ScanMetaObject(Map<String, String> scannerInfos, List<ScanMetaItem> items) {
		super();
		this.scannerInfos = scannerInfos;
		this.items = items;
	}

	public Map<String, String> getScannerInfos() {
		return scannerInfos;
	}
	public void setScannerInfos(Map<String, String> scannerInfos) {
		this.scannerInfos = scannerInfos;
	}
	public List<ScanMetaItem> getItems() {
		return items;
	}
	public void setItems(List<ScanMetaItem> items) {
		this.items = items;
	}

	
}
