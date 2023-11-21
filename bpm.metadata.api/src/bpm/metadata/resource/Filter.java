package bpm.metadata.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.ISecurizable;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;

public class Filter implements IFilter , ISecurizable{
	
	private String name = "";
	private String dataStreamName, dataStreamElementName;
	
	private IDataStreamElement origin;
	private HashMap<Locale, String> outputName = new HashMap<Locale, String>();
	public Filter(){
		
	}
	
	public String getOutputName(Locale l){
		return outputName.get(l) != null ? outputName.get(l) : getName() ;
	}
	
	public String getOutputName(){
		return outputName.get(Locale.getDefault()) != null ? outputName.get(Locale.getDefault()) : getName();
	}
	
	public void setOutputName(Locale l, String value){
		outputName.put(l, value);
	}
	public void setOutputName(String country, String language, String value){
		outputName.put(new Locale(language, country), value);
	}
	
	private HashMap<String, Boolean> granted = new HashMap<String, Boolean>();
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
	@Deprecated
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
	
	private List<String> values = new ArrayList<String>();
	

	public void addValue(String s){
		values.add(s);
	}
	
	public void setValues(List<String> values){
		this.values = values;
	}
	
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public List<String> getValues(){
		return values;
	}
	
	public void setOrigin(IDataStreamElement origin){
		this.origin = origin;
		
		//to avoid error
		if (origin != null){
			dataStreamElementName = origin.getName();
			dataStreamName = origin.getDataStream().getName();
		}
		
	}
	
	public IDataStreamElement getOrigin(){
		return origin;
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <filter>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <dataStreamElement>" + origin.getName() + "</dataStreamElement>\n");
		buf.append("            <dataStream>" + origin.getDataStream().getName() + "</dataStream>\n");
		
		for(String s : values){
			buf.append("            <value>" + s.replace("&" , "&amp;") + "</value>\n");
		}
		
		
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
		
		buf.append("        </filter>\n");
		
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

	@Override
	public boolean equals(Object obj) {
		if (obj != null &&!(obj instanceof Filter)){
			return false;
		}
		if (this == obj){
			return true;
		}
		Filter f = (Filter)obj;
		if ( name.equals(f.getName()) && dataStreamElementName.equals(f.getDataStreamElementName()) 
				&&
			dataStreamName.equals(f.getDataStreamName())
			&&
			f.getValues().size() == values.size()){
			boolean flag = true;
			for(String s : values){
				boolean found = false;
				for (String _s : f.getValues()){
					if (_s.equals(s)){
						found = true;
						break;
					}
					
					flag = found && flag;
				}
			}
			
			return flag;
			
			
		}
		return false;
	}

	public String getSqlWhereClause() {
		StringBuffer buf = new StringBuffer();
		
		if (getOrigin() instanceof ICalculatedElement){
			buf.append(((ICalculatedElement)getOrigin()).getFormula() + " in (" );
		}
		else{
			buf.append("`" + getOrigin().getDataStream().getName() + "`." + getOrigin().getOrigin().getShortName() + " in (" );
		}
		
		
		boolean in = true;
		for(String s : getValues()){
			if (in){
				in = false;
			}
			else{
				buf.append(",");
			}
			try {
				
				if (getOrigin() instanceof ICalculatedElement){
					String  className = ((ICalculatedElement)getOrigin()).getJavaClassName();
					if (String.class.getName().equals(className) ||
						Date.class.getName().equals(className) ||
						java.sql.Timestamp.class.getName().equals(className) ||
						java.sql.Time.class.getName().equals(className) ||
						java.sql.Date.class.getName().equals(className)){
						
						buf.append("'" + s + "'");
					}
					else{
						buf.append(s);
					}
					
				}
				else{
					if (getOrigin().getOrigin().getClassName().equals(String.class.getName())||
						getOrigin().getOrigin().getClassName().equals(Date.class.getName()) ||
						getOrigin().getOrigin().getClassName().equals(java.sql.Timestamp.class.getName()) ||
						getOrigin().getOrigin().getClassName().equals(java.sql.Time.class.getName()) ||

						getOrigin().getOrigin().getClassName().equals(java.sql.Date.class.getName())){
						buf.append("'" + s + "'");
					}
					else{
						buf.append(s);
					}

				}
				
				
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		buf.append(") ");
		
		return buf.toString();
	}
	
	
}
