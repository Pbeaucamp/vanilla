package bpm.gateway.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;

public class GatewayStepLog {
	private String stepName;
	private List<String> message = new ArrayList<String>();
	private String buffered;
	private String readed;
	private String processed;
	private String state;
	private String start;
	private String stop;
	private String duration;
	
	private boolean containsError = false;
	private boolean containsWarnings = false;
	
	public final String getStepName() {
		return stepName;
	}
	public final void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public final List<String> getMessage() {
		return message;
	}
	public final void addMessage(int priority, String message) {
		this.message.add(message);
		
		if (priority == Level.ERROR_INT){
			containsError = true;
		}
		
		if (priority == Level.WARN_INT){
			containsWarnings = true;
		}
	}
	
	public boolean isContainingErrors(){
		return containsError;
	}
	public boolean isContainingWarnings(){
		return containsWarnings;
	}
	
	public final String getBuffered() {
		return buffered;
	}
	public final void setBuffered(String buffered) {
		this.buffered = buffered;
	}
	public final String getReaded() {
		return readed;
	}
	public final void setReaded(String readed) {
		this.readed = readed;
	}
	public final String getProcessed() {
		return processed;
	}
	public final void setProcessed(String processed) {
		this.processed = processed;
	}
	public final String getState() {
		return state;
	}
	public final void setState(String state) {
		this.state = state;
	}
	public final String getStart() {
		return start;
	}
	public final void setStart(String start) {
		this.start = start;
	}
	public final String getStop() {
		return stop;
	}
	public final void setStop(String stop) {
		this.stop = stop;
	}
	public final String getDuration() {
		return duration;
	}
	public final void setDuration(String duration) {
		this.duration = duration;
	}
	
	
}
