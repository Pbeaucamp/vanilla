package bpm.document.management.core.model;

import java.io.Serializable;

public class Warnings implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int warningId=0;
	private int userId=0;
	private boolean disabledWarn=false;
	
	public int getWarningId() {
		return warningId;
	}
	public void setWarningId(int warningId) {
		this.warningId = warningId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public boolean isDisabledWarn() {
		return disabledWarn;
	}
	public void setDisabledWarn(boolean disabledWarn) {
		this.disabledWarn = disabledWarn;
	}	
}
