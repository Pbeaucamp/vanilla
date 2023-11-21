package bpm.document.management.core.model;

import java.io.Serializable;

public class KeywordResults implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id=0;
	private int keywordId=0;
	private int docId=0;
	private String type="";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getKeywordId() {
		return keywordId;
	}
	public void setKeywordId(int keywordId) {
		this.keywordId = keywordId;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
}
