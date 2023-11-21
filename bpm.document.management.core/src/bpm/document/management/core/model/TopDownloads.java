package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class TopDownloads implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;
	private int docId;
	private int downloads =0;
	private String type;
	private int userId;
	private Date downloadDate;
	private String email;
	private String docName;
	
	public TopDownloads() {
		// TODO Auto-generated constructor stub
	}

	

	public TopDownloads(int docId, int downloads, String type, int userId, Date downloadDate, String email, String docName) {
		super();
		this.docId = docId;
		this.downloads = downloads;
		this.type = type;
		this.userId = userId;
		this.downloadDate = downloadDate;
		this.email = email;
		this.docName = docName;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	

	public int getDownloads() {
		return downloads;
	}

	public void setDownloads(int downloads) {
		this.downloads = downloads;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public void addDownloads(){
		this.downloads = this.downloads + 1;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getDownloadDate() {
		return downloadDate;
	}

	public void setDownloadDate(Date downloadDate) {
		this.downloadDate = downloadDate;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getDocName() {
		return docName;
	}



	public void setDocName(String docName) {
		this.docName = docName;
	}
	
	
	
}
