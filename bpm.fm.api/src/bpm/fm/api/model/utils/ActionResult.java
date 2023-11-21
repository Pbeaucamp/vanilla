package bpm.fm.api.model.utils;

import java.io.Serializable;

public class ActionResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_NOT_STARTED = "Not started";
	public static final String STATUS_NOT_FINISHED = "Not finished";
	public static final String STATUS_FINISHED = "Finished";
	
	private int health = 0;
	private String status;

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
