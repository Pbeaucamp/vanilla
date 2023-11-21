package bpm.gateway.runtime2.tools;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.OutputStream;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class RuntimeAppender extends WriterAppender {

	public static final String DEBUG = "DEBUG";
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";
	public static final String WARN = "WARN";
	public static final String ENGINE_STATE = "engineState";
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private OutputStream outputStream;
	
	public void fireEgineStateEvent(String message){
		getListeners().firePropertyChange(ENGINE_STATE, null, message);
	}
	
	public void fireStatEvent(String message){
		getListeners().firePropertyChange("stat", null, message);
	}
	
	public RuntimeAppender(Layout layout, OutputStream os) {
		super(layout, os);
		this.outputStream = os;
		
	}

	@Override
	public void append(LoggingEvent event) {
		super.append(event);
		
		if (event.getLevel() == Level.INFO){
			getListeners().firePropertyChange(INFO, null, event.getMessage());
		}
		
		if (event.getLevel() == Level.ERROR){
			getListeners().firePropertyChange(ERROR, null, event.getMessage());
		}
		
		if (event.getLevel() == Level.WARN){
			getListeners().firePropertyChange(WARN, null, event.getMessage());
		}
		
		if (event.getLevel() == Level.DEBUG){
			getListeners().firePropertyChange(DEBUG, null, event.getMessage());
		}
		
	}

	
	
	
	/**
	 * add a listener 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		
		for(PropertyChangeListener p : listeners.getPropertyChangeListeners()){
			if (p == listener){
				return;
			}
		}
		
		listeners.addPropertyChangeListener(listener);
	}
	
	/**
	 * remove the listener
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener){
		listeners.removePropertyChangeListener(listener);
	}
	
	/**
	 * 
	 * @return the listeners
	 */
	public PropertyChangeSupport getListeners(){
		return listeners;
	}

	@Override
	protected void closeWriter() {
		if (outputStream == System.out){
			return;
		}
		super.closeWriter();
	
	}
}
