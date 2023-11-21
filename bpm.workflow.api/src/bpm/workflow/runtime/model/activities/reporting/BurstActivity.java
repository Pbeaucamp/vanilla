package bpm.workflow.runtime.model.activities.reporting;

import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.resources.ReportObject;

/**
 * Burst a report
 * @author CHARBONNIER, MARTIN
 *
 */
public class BurstActivity extends ReportActivity {
	private static int number = 0;
	
	public BurstActivity() {
		super();
		number++;
	}
	
	/**
	 * Create a Burst activity with the specified name
	 * @param name
	 */
	public BurstActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}
	
	/**
	 * Create a Burst activity with the specified name and Report Object
	 * @param name
	 * @param biObject
	 */
	protected BurstActivity(String name, ReportObject biObject) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		super.setReportObject(biObject);
	}

	public IActivity copy() {
		BurstActivity a = new BurstActivity();
		a.setName("copy of " + name);
		if (biObject != null)
			a.setReportObject(biObject);
		if (!groups.isEmpty()) {
			for (String g : groups) {
				a.addGroup(g);
			}
		}
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		return a;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("burstActivity");
		
		if (!groups.isEmpty()) {
			Element gr = DocumentHelper.createElement("groups");
			for (String g : groups) {
				gr.addElement("group").setText(g);
			}
			e.add(gr);
		}
		
		return e;
	}
	
	/**
	 * Add a group for the bursting
	 * @param group
	 */
	public void addGroup(String group) {
		if (!groups.contains(group))
			groups.add(group);
	}
	
	/**
	 * 
	 * @return the group to which the report will be bursted
	 */
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * Remove a group from the burst context
	 * @param group Name
	 */
	public void removeGroup(String s) {
		this.groups.remove(s);			
	}	
	

	
}
