package bpm.workflow.runtime.model.activities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;


/**
 * Activity of a loop (Dummy hook)
 * @author Charles MARTIN
 *
 */
public class LoopGlobale extends AbstractActivity implements PropertyChangeListener{

	private static int number = 0;
	
	public LoopGlobale(){
		number++;
	}

	public LoopGlobale(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("loop");

		return e;
	}
	
	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		
	}



	public IActivity copy() {
		LoopGlobale a = new LoopGlobale();
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
