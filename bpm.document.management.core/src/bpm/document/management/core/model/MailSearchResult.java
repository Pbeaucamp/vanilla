package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class MailSearchResult implements Serializable {

	private static final long serialVersionUID = -8432413975558063732L;
	
	private int id;
	private String name;

	private Date creationDate;
	private int nbResults;

	private int userId;

	private MailSearch mailSearch;
	private String searchXml;
	
	public MailSearchResult() { }
	
	public MailSearchResult(String name, int nbResults, int userId, MailSearch mailSearch) {
		this.name = name;
		this.nbResults = nbResults;
		this.userId = userId;
		this.mailSearch = mailSearch;
		this.creationDate = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getNbResults() {
		return nbResults;
	}

	public void setNbResults(int nbResults) {
		this.nbResults = nbResults;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public MailSearch getMailSearch() {
		return mailSearch;
	}

	public void setMailSearch(MailSearch mailSearch) {
		this.mailSearch = mailSearch;
	}

	public String getSearchXml() {
		return searchXml;
	}

	public void setSearchXml(String searchXml) {
		this.searchXml = searchXml;
	}

}
