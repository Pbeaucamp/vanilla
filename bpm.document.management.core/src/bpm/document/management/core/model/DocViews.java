package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class DocViews implements Serializable {

	private static final long serialVersionUID = 1L;

	private int dViewsId = 0;
	private int docId = 0;
	private String email = "";
	private Date viewedDate = new Date();

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getViewedDate() {
		return viewedDate;
	}

	public void setViewedDate(Date viewedDate) {
		this.viewedDate = viewedDate;
	}

	public int getdViewsId() {
		return dViewsId;
	}

	public void setdViewsId(int dViewsId) {
		this.dViewsId = dViewsId;
	}
}