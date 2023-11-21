package bpm.metadata.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.ISecurizable;
import bpm.metadata.layer.logical.IDataStreamElement;

public class ListOfValue implements IResource , ISecurizable{

	private String name;
	private String dataStreamName, dataStreamElementName;
	
	private IDataStreamElement origin;
	
	private HashMap<String, Boolean> granted = new HashMap<String, Boolean>();
	
	private HashMap<Locale, String> outputName = new HashMap<Locale, String>();
	public String getOutputName(Locale l){
		return outputName.get(l) != null && !outputName.get(l).equals("") ? outputName.get(l) : getName() ;
	}
	
	public String getOutputName(){
		return outputName.get(Locale.getDefault()) != null && !outputName.get(Locale.getDefault()).equals("") ? outputName.get(Locale.getDefault()) : getName();
	}
	
	public void setOutputName(Locale l, String value){
		outputName.put(l, value);
	}
	public void setOutputName(String country, String language, String value){
		outputName.put(new Locale(language, country), value);
	}
	
	public boolean isGrantedFor(String groupName){
		for(String s : granted.keySet()){
			if (s.equals(groupName)){
				return granted.get(s);
			}
		}
		return false;
	}
	
	public void setGranted(String groupName, boolean value) {
		for(String s : granted.keySet()){
			if (s.equals(groupName)){
				this.granted.put(s, value);
				return;
			}
		}
		this.granted.put(groupName, value);
	}
	
	/**
	 * 
	 * This method is just left for old Metadata model. However it will not be used for new model
	 * 
	 * @param groupName
	 * @param value
	 */
	public void setGranted(String groupName, String value) {
		setGranted(groupName, Boolean.parseBoolean(value));
	}
	
	public void setGroupsGranted(String groups){
		if(!groups.isEmpty()){
			String[] arrayGroups = groups.split(",");
			for(String gr : arrayGroups){
				setGranted(gr, true);
			}
		}
	}
	
	public HashMap<String, Boolean> getGrants(){
		return granted;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public List<String> getValues() throws Exception{
		return origin.getDistinctValues();
	}
	
	public void setOrigin(IDataStreamElement origin){
		this.origin = origin;
	}
	
	public IDataStreamElement getOrigin(){
		return origin;
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <lov>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <dataStreamElement>" + origin.getName() + "</dataStreamElement>\n");
		buf.append("            <dataStream>" + origin.getDataStream().getName() + "</dataStream>\n");
		
		
		
		//grants
		buf.append("            <groupNames>");
		boolean first = true;
		for(String s : granted.keySet()){
			if(granted.get(s)){
				if(first){
					first = false;
				}
				else {
					buf.append(",");
				}
				buf.append(s);
			}
		}
		buf.append("</groupNames>\n");
		
		
		for(Locale l : outputName.keySet()){
			buf.append("            <outputname>\n");
			buf.append("                <country>" + l.getCountry() + "</country>\n");
			buf.append("                <language>" + l.getLanguage() + "</language>\n");
			buf.append("                <value>" + outputName.get(l) + "</value>\n");
			buf.append("            </outputname>\n");
		}
		
		buf.append("        </lov>\n");
		
		return buf.toString();
	}

	public String getDataStreamName() {
		return dataStreamName;
	}

	public void setDataStreamName(String dataStreamName) {
		this.dataStreamName = dataStreamName;
	}

	public String getDataStreamElementName() {
		return dataStreamElementName;
	}

	public void setDataStreamElementName(String dataStreamElementName) {
		this.dataStreamElementName = dataStreamElementName;
	}
	
	
}
