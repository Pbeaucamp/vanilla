package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Map;

public class ScanMetaItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Documents doc;
	private Map<String, String> metaInfos;
	private boolean isBlankPage = false;
	public ScanMetaItem() {
		super();
	}
	public Documents getDoc() {	return doc;}
	public void setDoc(Documents doc) {	this.doc = doc;	}
	public Map<String, String> getMetaInfos() {	return metaInfos;}
	public void setMetaInfos(Map<String, String> metaInfos) {this.metaInfos = metaInfos;}
	public boolean isBlankPage() {
		return isBlankPage;
	}
	public void setBlankPage(boolean isBlankPage) {
		this.isBlankPage = isBlankPage;
	}
	
	
}
