package bpm.document.management.core.model;

import java.io.Serializable;

public class LocaleTasks implements Serializable{
	private static final long serialVersionUID = 1L;

	private int localTypeId=0;
	private int localeId=0;
	private int tasksId=0;
	private String word="";
	private int userId=0;
	
	public int getLocalTypeId() {
		return localTypeId;
	}
	public void setLocalTypeId(int localTypeId) {
		this.localTypeId = localTypeId;
	}
	public int getLocaleId() {
		return localeId;
	}
	public void setLocaleId(int localeId) {
		this.localeId = localeId;
	}
	public int getTasksId() {
		return tasksId;
	}
	public void setTasksId(int tasksId) {
		this.tasksId = tasksId;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
}
