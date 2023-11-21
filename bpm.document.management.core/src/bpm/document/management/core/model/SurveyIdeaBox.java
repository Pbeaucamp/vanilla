package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SurveyIdeaBox implements Serializable{
	private static final long serialVersionUID = 1L;

	
	private int id=0;
	private String name="";
	private boolean multipleChoice=false;
	private int authorId;
	private Date creationDate;
	private Date closeDate;
	private int subjectId;
	private List<ChoiceIdeaBox> choice = new ArrayList<ChoiceIdeaBox>();
	
	public List<ChoiceIdeaBox> getChoice() {
		return choice;
	}

	public void setChoice(List<ChoiceIdeaBox> choice) {
		this.choice = choice;
	}

	public SurveyIdeaBox() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void addChoice(ChoiceIdeaBox c){
		choice.add(c);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public int getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

	public boolean isMultipleChoice() {
		return multipleChoice;
	}

	public void setMultipleChoice(boolean multipleChoice) {
		this.multipleChoice = multipleChoice;
	}




	



	
}
