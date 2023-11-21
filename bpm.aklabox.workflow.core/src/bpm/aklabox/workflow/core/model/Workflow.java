package bpm.aklabox.workflow.core.model;

import java.io.Serializable;
import java.util.Date;

public class Workflow implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum TypeRun {
		MANUAL, 
		AUTOMATIC
	}
	
	public enum Type {
		SIMPLE,
		MASTER,
		ORBEON
	}


	private int id;
	private String name;
	private String description;
	private int versionNumber;
	private String status;
	private Date date;
	private TypeRun runType;
	private boolean working;
	private boolean deploy;
	private Type typeWorkflow;
	
	private int masterParent = 0;
	private int linkedFormId = 0;
	
	private Integer reportId;
	
	public Workflow() {
	}
	
	public Workflow(Workflow workflow) {
		super();
		this.id = workflow.getId();
		this.name = workflow.getName();
		this.description = workflow.getDescription();
		this.versionNumber = workflow.getVersionNumber();
		this.status = workflow.getStatus();
		this.date = workflow.getDate();
		this.runType = workflow.getRunType();
		this.working = workflow.isWorking();
		this.deploy = workflow.isDeploy();
		this.typeWorkflow = workflow.getTypeWorkflow();
		this.masterParent = workflow.getMasterParent();
		this.linkedFormId = workflow.getLinkedFormId();
	}

	public Workflow(String name, String description, int versionNumber, String status, Date date, TypeRun type) {
		super();
		this.name = name;
		this.description = description;
		this.versionNumber = versionNumber;
		this.status = status;
		this.date = date;
		this.runType = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Integer getReportId() {
		return reportId;
	}
	
	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	public boolean isDeploy() {
		return deploy;
	}

	public void setDeploy(boolean deploy) {
		this.deploy = deploy;
	}

	public TypeRun getRunType() {
		return runType;
	}

	public void setRunType(TypeRun type) {
		this.runType = type;
	}

	public int getMasterParent() {
		return masterParent;
	}

	public void setMasterParent(int masterParent) {
		this.masterParent = masterParent;
	}

	public Type getTypeWorkflow() {
		return typeWorkflow;
	}

	public void setTypeWorkflow(Type typeWorkflow) {
		this.typeWorkflow = typeWorkflow;
	}

	public int getLinkedFormId() {
		return linkedFormId;
	}

	public void setLinkedFormId(int linkedFormId) {
		this.linkedFormId = linkedFormId;
	}

}
