package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class SubjectIdeaBox implements Serializable{
	private static final long serialVersionUID = 1L;

	
	private int id=0;
	private String objectSubject;
	private String author,text;
	private Date creationDate;
	private int nbVote=0;
	private int sondageId=-1;
	
	
	public SubjectIdeaBox() {
	}
	
	public SubjectIdeaBox(String objectSubject,String author,String text,Date creationDate){
		this.setObjectSubject(objectSubject);
		this.setAuthor(author);
		this.setText(text);
		this.setCreationDate(creationDate);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getObjectSubject() {
		return objectSubject;
	}

	public void setObjectSubject(String objectSubject) {
		this.objectSubject = objectSubject;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getNbVote() {
		return nbVote;
	}

	public void setNbVote(int nbVote) {
		this.nbVote = nbVote;
	}

	public int getSondageId() {
		return sondageId;
	}

	public void setSondageId(int sondageId) {
		this.sondageId = sondageId;
	}
}
