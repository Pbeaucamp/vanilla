package bpm.gateway.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * 
 * A common class that should heritated fro almost all objects in the model
 * @author LCA
 *
 */
public abstract class GatewayObject {

	protected String name ="";
	protected String description = "";
	
	private PropertyChangeSupport listeners;
	
	
	/**
	 * add a listener 
	 * @param listener
	 */
	protected void addPropertyChangeListener(PropertyChangeListener listener){
		listeners.addPropertyChangeListener(listener);
	}
	
	/**
	 * remove the listener
	 * @param listener
	 */
	protected void removePropertyChangeListener(PropertyChangeListener listener){
		listeners.removePropertyChangeListener(listener);
		
		
		
	}
	
	
	/**
	 * @return the object name
	 */
	public String getName(){
		return name;
	}
	
	
	
	
	public void setName(String name) {
		this.name = name;
		
		if (this instanceof Transformation && ((Transformation)this).getDocument() != null){
			
			((Transformation)this).getDocument().checkAndUpdateName((Transformation)this);
		}
		
		
	}




	public final void setDescription(String description) {
		this.description = description;
	}




	/**
	 * 
	 * @return the Object Description
	 */
	public String getDescription(){
		return description;
	}
}
