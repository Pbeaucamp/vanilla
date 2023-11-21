package bpm.document.management.core.model;

import java.io.Serializable;

public class DocKeywords implements Serializable{

	private static final long serialVersionUID = 1L;

	private int DK_Id=0;
	private int docId=0;
	private int keywordId=0;
	
	public int getDK_Id() {
		return DK_Id;
	}
	public void setDK_Id(int dK_Id) {
		DK_Id = dK_Id;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getKeywordId() {
		return keywordId;
	}
	public void setKeywordId(int keywordId) {
		this.keywordId = keywordId;
	}
	
}
