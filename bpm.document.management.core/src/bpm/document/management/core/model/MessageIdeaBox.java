package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class MessageIdeaBox implements Serializable{
	private static final long serialVersionUID = 1L;

	
	private int id=0;
	private int idSubject;
	private String response;
	private String author;
	private Date creationDate;
	
	
	public MessageIdeaBox() {
	}
	
	public MessageIdeaBox(int idSubject,String author,String response,Date creationDate){
		this.setResponse(response);
		this.setAuthor(author);
		this.setCreationDate(creationDate);
		this.setIdSubject(idSubject);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}



	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getIdSubject() {
		return idSubject;
	}

	public void setIdSubject(int idSubject) {
		this.idSubject = idSubject;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
