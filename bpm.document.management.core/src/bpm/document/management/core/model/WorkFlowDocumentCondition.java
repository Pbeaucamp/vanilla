package bpm.document.management.core.model;

import java.io.Serializable;

public class WorkFlowDocumentCondition implements Serializable{

	private static final long serialVersionUID = 1L;

	private int conId=0;
	private int workflowId=0;
	private boolean alertOne=false;
	private boolean alertTwo=false;
	private boolean alertThree=false;
	private boolean alertFour=false;
	private boolean alertFive=false;
	private boolean alertComment=false;
	
	public int getConId() {
		return conId;
	}
	public void setConId(int conId) {
		this.conId = conId;
	}
	public int getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}
	public boolean isAlertOne() {
		return alertOne;
	}
	public void setAlertOne(boolean alertOne) {
		this.alertOne = alertOne;
	}
	public boolean isAlertTwo() {
		return alertTwo;
	}
	public void setAlertTwo(boolean alertTwo) {
		this.alertTwo = alertTwo;
	}
	public boolean isAlertThree() {
		return alertThree;
	}
	public void setAlertThree(boolean alertThree) {
		this.alertThree = alertThree;
	}
	public boolean isAlertFour() {
		return alertFour;
	}
	public void setAlertFour(boolean alertFour) {
		this.alertFour = alertFour;
	}
	public boolean isAlertFive() {
		return alertFive;
	}
	public void setAlertFive(boolean alertFive) {
		this.alertFive = alertFive;
	}
	public boolean isAlertComment() {
		return alertComment;
	}
	public void setAlertComment(boolean alertComment) {
		this.alertComment = alertComment;
	}
	
	
	
}
