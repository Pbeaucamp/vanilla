
package bpm.workflow.runtime.resources;

import bpm.workflow.runtime.model.activities.CalculationActivity;


/**
 * Script used for a Calculation activity
 * @author CAMUS, MARTIN
 *
 */
public class Script {

	private String name;
	
	private String scriptFunction = "";
	private CalculationActivity owner;
	
	private int type;
	
	/**
	 * Set the type of the result
	 * @param type
	 */
	public void setType(String type){
		this.type = Integer.parseInt(type);
	}
	
	/**
	 * Set the type of the result
	 * @param type
	 */
	public void setType(int type){
		this.type = type;
		

	}
	
	/**
	 * 
	 * @return the type of the result
	 */
	public int getType(){
		return type;
	}
	
	/**
	 * 
	 * @return the name of the script ( = name of the resulting variable)
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the calculation activity which owns this script
	 * @param c : calculation activity
	 */
	protected void setOwner(CalculationActivity c){
		
		owner = c;
	}
	
	/**
	 * Set the name of the script
	 * @param name
	 */
	public void setName(String name) {
		boolean changed = this.name != null && !this.name.equals(name);
		this.name = name;
		
		if (changed && owner != null){
			owner.refreshDescriptor();
		}
	}

	/**
	 * 
	 * @return the string to execute
	 */
	public String getScriptFunction() {
		return scriptFunction;
	}

	/**
	 * Set the string to execute
	 * @param scriptFunction
	 */
	public void setScriptFunction(String scriptFunction) {
		this.scriptFunction = scriptFunction;
	}
	
	
}
