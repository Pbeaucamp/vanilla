package bpm.workflow.runtime.model.activities;

import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;

/**
 * Link activity (not implemented)
 * @author Charles MARTIN
 *
 */
public class LinkActivity extends AbstractActivity {

	/**
	 * do not use, for XML parsing only
	 */
	private String typeact ="inter";
	
	public String getTypeact() {
		return typeact;
	}

	public void setTypeact(String typeact) {
		this.typeact = typeact;
	}

	public LinkActivity(){
		
	}

	public IActivity copy() {
		LinkActivity a = new LinkActivity();
		a.setName("copy of " + name);
		return a;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
	}
	
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("linkActivity");
		if(!this.getTypeact().equalsIgnoreCase("")){
			e.addElement("typeact").setText(this.getTypeact());
			}
		
		return e;
	}
	
	public void decreaseNumber() {
	
	}

	@Override
	public void execute() throws Exception {
		//Do nothing
	}
	
}
