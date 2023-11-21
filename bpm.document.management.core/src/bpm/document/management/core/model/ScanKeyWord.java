package bpm.document.management.core.model;

import java.io.Serializable;

public class ScanKeyWord implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String word;
	private int idScanDocumentType;
	private boolean isChorusField;
	private int idLov;
	
	public ScanKeyWord() {
		super();
	}

	public ScanKeyWord(String word, int idScanDocumentType,	boolean isChorusField, int idLov) {
		super();
		this.word = word;
		this.idScanDocumentType = idScanDocumentType;
		this.isChorusField = isChorusField;
		this.idLov = idLov;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getIdScanDocumentType() {
		return idScanDocumentType;
	}

	public void setIdScanDocumentType(int idScanDocumentType) {
		this.idScanDocumentType = idScanDocumentType;
	}

	public boolean getIsChorusField() {
		return isChorusField;
	}

	public void setIsChorusField(boolean isChorusField) {
		this.isChorusField = isChorusField;
	}

	public int getIdLov() {
		return idLov;
	}

	public void setIdLov(int idLov) {
		this.idLov = idLov;
	}

	
}
