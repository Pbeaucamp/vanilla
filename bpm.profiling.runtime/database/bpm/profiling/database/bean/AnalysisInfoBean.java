package bpm.profiling.database.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnalysisInfoBean {
	private int id;
	private String name;
	private String description;
	private Date creation;
	private Date modification;
	private String creator;
	private int connectionId;
	
	
	private List<Date> oldRunnings = new ArrayList<Date>();
	
	public List<Date> getOldRuns(){
		return oldRunnings;
	}
	
	public void addOldRun(Date date){
		oldRunnings.add(date);
	}
	
	
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public int getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(int connectionId) {
		this.connectionId = connectionId;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
	
}
