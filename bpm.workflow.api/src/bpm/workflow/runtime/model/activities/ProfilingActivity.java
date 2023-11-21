package bpm.workflow.runtime.model.activities;

import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.resources.ProfilingQueryObject;

/**
 * This class is for representing a profiling activity.
 * The execution a this task, launch the Query represented by the 
 * profilingQuery object on the profiling database (not used)
 * 
 * @author LCA
 *
 */
public class ProfilingActivity extends AbstractActivity {

	private ProfilingQueryObject profilingQuery;
	private Float value ;
	private static int number = 0;
	
	
	/**
	 * do not use, only for XML parsing
	 */
	public ProfilingActivity(){
		number++;
	}
	
	public ProfilingActivity(String name){
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}
	
	public ProfilingActivity(String name, ProfilingQueryObject profilingQuery){
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		this.profilingQuery = profilingQuery;
	}
	
	
	/**
	 * return the ProfilingQueryObject
	 * @return
	 */
	public ProfilingQueryObject getBiObject(){
		return profilingQuery;
	}
	
	
	public void setBiObject(ProfilingQueryObject biObject){
		this.profilingQuery = biObject;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("profilingActivity");
		
		if(profilingQuery.getXmlNode() != null){
		e.add(profilingQuery.getXmlNode());
		}
		return e;
	}
	
	public void setValue(Float value){
		this.value = value;
	}
	
	public Float getValue(){
		return value;
	}

	public IActivity copy() {
		ProfilingActivity a = new ProfilingActivity();
		a.setName("copy of " + name);
		return a;
	}

	public String getProblems() {
		return "";
	}
	
	public void decreaseNumber() {
		number--;
	}

	@Override
	public void execute() throws Exception {
		//Not used
	}
}
