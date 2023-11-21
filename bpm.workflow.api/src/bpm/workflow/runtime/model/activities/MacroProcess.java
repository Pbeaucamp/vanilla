package bpm.workflow.runtime.model.activities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;


/**
 * Activity of a macro process (Dummy hook)
 * @author Charles MARTIN
 *
 */
public class MacroProcess extends AbstractActivity implements PropertyChangeListener{

	
	private String type;

	/**
	 * 
	 * @return the type of the macro process
	 */
	public String getType() {
		return type;
	}

/**
 * Set the type of the macro process
 * @param type
 */
	public void setType(String type) {
		this.type = type;
	}


	public MacroProcess(){
		
	}


	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();

		e.setName("MacroProcess");
		e.addElement("type").setText(type);
		return e;
	}
	
	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
	}

	public void propertyChange(PropertyChangeEvent arg0) {
	}



	public IActivity copy() {
		MacroProcess a = new MacroProcess();
		a.setName("copy of " + name);
		return a;
	}
	
	public void decreaseNumber() {
		
	}

	@Override
	public void execute() throws Exception {
		//Do nothing
	}

}
