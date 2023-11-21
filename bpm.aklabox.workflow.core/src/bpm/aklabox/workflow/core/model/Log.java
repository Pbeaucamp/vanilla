package bpm.aklabox.workflow.core.model;

import java.io.Serializable;

public class Log implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Level {
		ALL,
		INFO, 
		WARNING, 
		DEBUG, 
		ERROR;
	}
	
	private Level level;
	private String message;
	
	public Log() { }
	
	public Log(Level level, String message) {
		this.level = level;
		this.message = message;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return level.toString() + " - " + message;
	}
}
