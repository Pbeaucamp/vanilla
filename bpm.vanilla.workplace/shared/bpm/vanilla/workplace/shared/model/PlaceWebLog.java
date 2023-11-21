package bpm.vanilla.workplace.shared.model;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;


public class PlaceWebLog implements IsSerializable {
	
	public enum LogType {
		CREATION_USER(0),
		LOGIN(1),
		LOGOUT(2),
		CREATE_PROJECT(3),
		EXPORT_PACKAGE(4),
		IMPORT_PACKAGE(5),
		UNKNOWN_TYPE(6),
		UPDATE_USER(7),
		DELETE_USER(8),
		DELETE_PACKAGE(9),
		DELETE_PROJECT(10);
		
		private int type;
		
		private LogType(int logType){
			this.type = logType;
		}
		
		public int getType(){
			return type;
		}
	}

	private int id;
	
	private int type;
	private int userId;
	private Integer projectId;
	private Integer packageId;
	
	private Date date;
	
	public PlaceWebLog() { }
	
	public PlaceWebLog(LogType type, int userId, Date date, Integer projectId, Integer packageId){
		this.setLogType(type);
		this.setUserId(userId);
		this.setDate(date);
		this.setProjectId(projectId);
		this.setPackageId(packageId);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Do not use (for hibernate purpose only): use getLogType()
	 * @return
	 */
	public int getType() {
		return type;
	}
	
	public LogType getLogType() {
		for(LogType logType : LogType.values()){
			if(logType.getType() == type){
				return logType;
			}
		}
		return LogType.UNKNOWN_TYPE;
	}

	/**
	 * Do not user (for hibernate purpose only)
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	public void setLogType(LogType logType){
		this.type = logType.getType();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setPackageId(Integer packageId) {
		this.packageId = packageId;
	}

	public Integer getPackageId() {
		return packageId;
	}
}
