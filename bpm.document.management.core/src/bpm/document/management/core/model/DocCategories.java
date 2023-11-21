package bpm.document.management.core.model;

import java.io.Serializable;

public class DocCategories implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int DC_Id=0;
	private int docId=0;
	private int categoryId=0;
	
	public int getDC_Id() {
		return DC_Id;
	}
	public void setDC_Id(int dC_Id) {
		DC_Id = dC_Id;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
}
