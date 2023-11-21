package bpm.workflow.runtime.model.activities;

import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;

/**
 * Dummy activity
 * @author Charles MARTIN
 *
 */
public class DummyActivity extends AbstractActivity {

	/**
	 * do not use, for XML parsing only
	 */
	public DummyActivity(){
		
	}

	public IActivity copy() {
		DummyActivity a = new DummyActivity();
		a.setName("copy of " + name);
		return a;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
	}
	
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("dummyActivity");
		
		
		return e;
	}
	
	public void decreaseNumber() {
		
	}

	@Override
	public void execute() throws Exception {
		//This don't do anything. Only use to parse the model.
	}
	
}
