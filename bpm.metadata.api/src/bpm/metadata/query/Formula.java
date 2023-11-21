package bpm.metadata.query;

import java.util.ArrayList;
import java.util.List;

public class Formula implements Ordonable{
	private String script;
	private String name;
	private List<String> dataStreamInvolved = new ArrayList<String>();
	
	
	public Formula(){}
	public Formula(String name, String formula, List<String> dataStreamInvolved) {
		this.name = name;
		this.script = formula;
		if (dataStreamInvolved != null){
			this.dataStreamInvolved.addAll(dataStreamInvolved);
		}
		
	}
	public List<String> getDataStreamInvolved() {
		return dataStreamInvolved;
	}
	public void addInvolvedDataStream(String name){
		if (name != null){
			for(String s : dataStreamInvolved){
				if (s.equals(name)){
					return;
				}
			}
		}
		dataStreamInvolved.add(name);
	}
	
	public void removeInvolvedDataStream(String name){
		dataStreamInvolved.remove(name);
	}
	/**
	 * @return the formula
	 */
	public String getFormula() {
		return script;
	}
	/**
	 * @param formula the formula to set
	 */
	public void setFormula(String formula) {
		this.script = formula;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		buf.append("<formula>\n");
		buf.append("<name>" + getName() + "</name>\n");
		buf.append("<script>" + getFormula().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</script>\n");
		StringBuffer streams = new StringBuffer();
		
		for(String s : dataStreamInvolved){
			if (streams.length() > 0){
				streams.append(",");
			}
			streams.append(s);	
		}
		buf.append("<dataStreamNames>" + streams.toString() + "</dataStreamNames>\n");
		buf.append("</formula>\n");
		return buf.toString();
	}
	public String getOutputName() {
		return getName();
	}
	public void setInvolvedDatastreams(List<String> involvedDatastreams) {
		dataStreamInvolved = involvedDatastreams;
	}
	
}
