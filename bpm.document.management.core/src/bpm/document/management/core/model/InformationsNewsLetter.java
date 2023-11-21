package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class InformationsNewsLetter implements Serializable{
	private static final long serialVersionUID = 1L;

	
	private int id=0;
	private String author,text,name;
	private Date sendDate;
	private int docId;
	private Documents doc;
	private String listUser = "";
	
	public InformationsNewsLetter() {
	}
	
	public InformationsNewsLetter(String name,int docId,String author,String text,Date sendDate,String listUser){
		this.setAuthor(author);
		this.setText(text);
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getListUser() {
		return listUser;
	}

	public void setListUser(String listUser) {
		this.listUser = listUser;
	}

	public Documents getDoc() {
		return doc;
	}

	public void setDoc(Documents doc) {
		this.doc = doc;
	}

}
