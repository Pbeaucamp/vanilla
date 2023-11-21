package bpm.document.management.core.model;

import java.io.Serializable;

public class ProposedTag implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int proposedTagId=0;
	private String proposedWord="";
	private int thesaurusId;
	
	public int getProposedTagId() {
		return proposedTagId;
	}
	public void setProposedTagId(int proposedTagId) {
		this.proposedTagId = proposedTagId;
	}
	public String getProposedWord() {
		return proposedWord;
	}
	public void setProposedWord(String proposedWord) {
		this.proposedWord = proposedWord;
	}
	public int getThesaurusId() {
		return thesaurusId;
	}
	public void setThesaurusId(int thesaurusId) {
		this.thesaurusId = thesaurusId;
	}
	
	
}
