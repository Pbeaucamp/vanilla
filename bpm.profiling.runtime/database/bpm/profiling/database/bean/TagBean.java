package bpm.profiling.database.bean;

import java.util.Date;

public class TagBean {
	private int id;
	private Integer resultConditionId;
	private Integer resultId;
	private Date creation;
	private Date modification;
	private String creator;
	private String content;
	
	

	private int ruleSetId;
	private int fieldIndice;
	
	
	public int getFieldIndice() {
		return fieldIndice;
	}

	public void setFieldIndice(int fieldIndice) {
		this.fieldIndice = fieldIndice;
	}

	public int getRuleSetId(){
		return ruleSetId;
	}
	
	public void setRuleSetId(int ruleSetId){
		this.ruleSetId = ruleSetId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Integer getResultConditionId() {
		return resultConditionId;
	}
	public void setResultConditionId(Integer resultConditionId) {
		this.resultConditionId = resultConditionId;
	}
	public Integer getResultId() {
		return resultId;
	}
	public void setResultId(Integer resultId) {
		this.resultId = resultId;
	}
	public Date getCreation() {
		return creation;
	}
	public void setCreation(Date creation) {
		this.creation = creation;
	}
	public Date getModification() {
		return modification;
	}
	public void setModification(Date modification) {
		this.modification = modification;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
