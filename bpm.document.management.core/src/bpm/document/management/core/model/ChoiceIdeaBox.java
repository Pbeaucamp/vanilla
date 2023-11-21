package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ChoiceIdeaBox implements Serializable{
	private static final long serialVersionUID = 1L;

	
	private int id=0;
	private String name="";
	private int surveyId;
	
	public ChoiceIdeaBox() {
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


	public int getSurveyId() {
		return surveyId;
	}


	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}






	



	
}
