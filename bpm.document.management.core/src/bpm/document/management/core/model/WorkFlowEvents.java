package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class WorkFlowEvents  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int workFlowEventId=0;
	private int workFlowId=0;
	private int receiver=0;
	private int sender=0;
	private String workFlowEvent="";
	private Date workFlowEventDate= new Date();
	
	public int getWorkFlowEventId() {
		return workFlowEventId;
	}
	public void setWorkFlowEventId(int workFlowEventId) {
		this.workFlowEventId = workFlowEventId;
	}
	public int getWorkFlowId() {
		return workFlowId;
	}
	public void setWorkFlowId(int workFlowId) {
		this.workFlowId = workFlowId;
	}
	public String getWorkFlowEvent() {
		return workFlowEvent;
	}
	public void setWorkFlowEvent(String workFlowEvent) {
		this.workFlowEvent = workFlowEvent;
	}
	public Date getWorkFlowEventDate() {
		return workFlowEventDate;
	}
	public void setWorkFlowEventDate(Date workFlowEventDate) {
		this.workFlowEventDate = workFlowEventDate;
	}
	public int getReceiver() {
		return receiver;
	}
	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}
	public int getSender() {
		return sender;
	}
	public void setSender(int sender) {
		this.sender = sender;
	}
	
	
}
