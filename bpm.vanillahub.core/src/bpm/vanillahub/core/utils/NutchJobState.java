package bpm.vanillahub.core.utils;

public class NutchJobState {
	
	public static final String STATE_FINISH = "FINISHED";
	public static final String STATE_RUNNING = "RUNNING";
	
	public static final String MESSAGE_OK = "OK";

	private String state;
	private String message;
	
	public NutchJobState(String state, String message) {
		this.state = state;
		this.message = message;
	}
	
	public String getState() {
		return state;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isRunning() {
		return state.equalsIgnoreCase(STATE_RUNNING);
	}
}
