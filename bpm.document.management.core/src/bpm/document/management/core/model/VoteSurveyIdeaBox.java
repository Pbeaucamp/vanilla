package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VoteSurveyIdeaBox implements Serializable{
	private static final long serialVersionUID = 1L;

	public static enum resultVote {
		  OK ("OK"),
		  HasAlreadyVoted ("Has Already Voted"),
		  Error("error");
		   
		  private String name = "";
		   
		  //Constructeur
		  resultVote(String name){
		    this.name = name;
		  }
		   
		  public String toString(){
		    return name;
		  }
	}
	
	private int id=0;
	private int idUser=0;
	private int idChoice;
	private int idSurvey;
	
	public VoteSurveyIdeaBox() {
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getIdChoice() {
		return idChoice;
	}


	public void setIdChoice(int idChoice) {
		this.idChoice = idChoice;
	}


	public int getIdUser() {
		return idUser;
	}


	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}


	public int getIdSurvey() {
		return idSurvey;
	}


	public void setIdSurvey(int idSurvey) {
		this.idSurvey = idSurvey;
	}







	



	
}
