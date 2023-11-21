package bpm.workflow.runtime.model.activities;

import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;

/**
 * Error activity (not implemented)
 * @author Charles MARTIN
 *
 */
public class ErrorActivity extends AbstractActivity {
	private static int number = 0;
	private String typeact ="inter";
	
	public ErrorActivity(){
		number++;
	}
	
	public ErrorActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}
	
	public String getTypeact() {
		return typeact;
	}

	public void setTypeact(String typeact) {
		this.typeact = typeact;
	}

	public IActivity copy() {
		ErrorActivity a = new ErrorActivity();
		a.setName("copy of " + name);
		return a;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
	}
	
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("erroractivity");
		if(!this.getTypeact().equalsIgnoreCase("")){
			e.addElement("typeact").setText(this.getTypeact());
			}
		
		return e;
	}
	
	public void decreaseNumber() {
		number--;
	}

	@Override
	public void execute() throws Exception {
		
		
	}
	
}
