package bpm.document.management.core.model;

import java.io.Serializable;

public class DocPages implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int pageId=0;
	private int docId=0;
	private int pageNumber=0;
	private String imagePath="";
	private int docVersion=0;
	private String ocrResult="";
	
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getDocVersion() {
		return docVersion;
	}
	public void setDocVersion(int docVersion) {
		this.docVersion = docVersion;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getOcrResult() {
		if (ocrResult == null) {
			return "";			
		}else{
			return ocrResult;			
		}
	}

	public void setOcrResult(String ocrResult) {
		this.ocrResult = ocrResult;
	}
	
}
