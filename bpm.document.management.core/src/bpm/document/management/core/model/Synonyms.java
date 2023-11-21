package bpm.document.management.core.model;

import java.io.Serializable;

public class Synonyms implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int synonymId=0;
	private String keyword="";
	private String synonymWord="";
	
	public int getSynonymId() {
		return synonymId;
	}
	public void setSynonymId(int synonymId) {
		this.synonymId = synonymId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getSynonymWord() {
		return synonymWord;
	}
	public void setSynonymWord(String synonymWord) {
		this.synonymWord = synonymWord;
	}
}
