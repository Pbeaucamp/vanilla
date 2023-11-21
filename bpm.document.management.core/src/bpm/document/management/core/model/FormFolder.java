package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class FormFolder implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public enum FormFolderType {
		FOLDER,
		ENTERPRISE
	}

	private int id;
	private int folderId;
	private int formId;
	private int userId;
	private boolean activate;
	private Date creationDate = new	Date();
	
	public FormFolder() {
		// TODO Auto-generated constructor stub
	}

	public FormFolder(int folderId, int formId, int userId, boolean activate) {
		super();
		this.folderId = folderId;
		this.formId = formId;
		this.userId = userId;
		this.activate = activate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean isActivate() {
		return activate;
	}

	public void setActivate(boolean activate) {
		this.activate = activate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	
	
}
