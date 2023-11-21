package bpm.document.management.core.model;

import java.io.Serializable;

public class RMField implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int rmFolderId;
	private String item;
	private String type;
	
	public RMField() {
		// TODO Auto-generated constructor stub
	}

	public RMField(String item, String type) {
		super();
		this.item = item;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRmFolderId() {
		return rmFolderId;
	}

	public void setRmFolderId(int rmFolderId) {
		this.rmFolderId = rmFolderId;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
