package bpm.profiling.runtime.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Result {
	private List<String> labels = new ArrayList<String>();
	private List<Object> values = new ArrayList<Object>();
	private int id;
	private int queryId;

	private Date date;
	
	public int getQueryId() {
		return queryId;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Result(int queryId, List<String> labels, List<Object> values){
		this.labels = labels;
		this.values = values;
		this.queryId = queryId;
	}
	
	
	public List<String> getLabels(){
		return labels;
	}
	


	public List<Object> getValues() {
		return values;
	}
}
