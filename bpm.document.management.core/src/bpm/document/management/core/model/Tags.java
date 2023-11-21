package bpm.document.management.core.model;

import java.io.Serializable;

public class Tags implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int tagId=0;
	private int fileId=0;
	private String fileType="";
	private String tagWord="";
	private int thesaurusId;
	
	public Tags(int fileId, String fileType, String tagWord) {
		super();
		this.fileId = fileId;
		this.fileType = fileType;
		this.tagWord = tagWord;
	}
	public Tags() {
		super();
	}
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getTagWord() {
		return tagWord;
	}
	public void setTagWord(String tagWord) {
		this.tagWord = tagWord;
	}
	public int getThesaurusId() {
		return thesaurusId;
	}
	public void setThesaurusId(int thesaurusId) {
		this.thesaurusId = thesaurusId;
	}
}
