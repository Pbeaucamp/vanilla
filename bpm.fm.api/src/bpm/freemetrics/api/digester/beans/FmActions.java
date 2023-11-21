/**
 * 
 */
package bpm.freemetrics.api.digester.beans;

import java.util.Vector;

/**
 * @author Belgarde
 *
 */
public class FmActions {

	private Vector<FmDigAction> actions= new Vector<FmDigAction>();
	
	public void addFmDigAction(FmDigAction action ) {
		actions.addElement(action);
	}

	public FmDigAction elementAt(int index){
		return (FmDigAction)actions.elementAt(index);
	}
	
	public int count(){
		return actions.size();
	}

	public Vector<FmDigAction> getActions() {
		return actions;
	}
}
