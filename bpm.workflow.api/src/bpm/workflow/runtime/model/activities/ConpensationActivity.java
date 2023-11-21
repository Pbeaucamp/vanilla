package bpm.workflow.runtime.model.activities;

import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;

/**
 * Compensation activity (not implemented)
 * @author Charles MARTIN
 *
 */
public class ConpensationActivity extends AbstractActivity {

	private String typeact = "inter";
	private static int number = 0;
	
	public String getTypeact() {
		return typeact;
	}

	public void setTypeact(String typeact) {
		this.typeact = typeact;
	}

	public ConpensationActivity(){
		number++;
	}
	
	public ConpensationActivity(String name) {

		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	
	}

	public IActivity copy() {
		ConpensationActivity a = new ConpensationActivity();
		a.setName("copy of " + name);
		return a;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
	}
	
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("conpensActivity");
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
