package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class Downloads implements Serializable {

	private static final long serialVersionUID = 1L;

	private int downloadId = 0;
	
	private String fileName;
	private String filePath = "";
	private String email = "";
	
	private Date creationDate = new Date();
	
	private int docId = 0;
	private int folderId = 0;

	public int getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(int downloadId) {
		this.downloadId = downloadId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	public String getFileName() {
		return fileName != null ? fileName : "";
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
