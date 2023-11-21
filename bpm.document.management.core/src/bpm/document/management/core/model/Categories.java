package bpm.document.management.core.model;

import java.io.Serializable;

public class Categories implements Serializable{

	private static final long serialVersionUID = 1L;

	private int categoryId=0;
	private String categoryName="";
	private String categoryDescription="";
	
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryDescription() {
		return categoryDescription;
	}
	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}
	
	
}
