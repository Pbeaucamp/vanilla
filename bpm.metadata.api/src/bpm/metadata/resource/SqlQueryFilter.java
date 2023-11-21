package bpm.metadata.resource;

import java.util.HashMap;
import java.util.Locale;

import bpm.metadata.ISecurizable;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;

/**
 * This Class is a filter that will be applied on the geenrated query
 * 
 * if will be added in the where clause and on the Origin dataStreamElement
 * 
 * ex:
 * 
 * 	Origin = TABLE.COL1
 *  query = "in select(XXX from YYY)
 *  
 *  will generate something like
 *  
 *  select ......... where TABLE.COL1 in select(XXX from YYY) ....
 * 
 * @author LCA
 *
 */
public class SqlQueryFilter implements IFilter, ISecurizable {
	private HashMap<String, Boolean> granted = new HashMap<String, Boolean>();
	private String query;
	private String name = "";
	private IDataStreamElement origin;
	private String dataStreamName, dataStreamElementName;
	private HashMap<Locale, String> outputName = new HashMap<Locale, String>();
	
	public String getDataStreamName() {
		if (origin != null){
			return origin.getDataStream().getName();
		}
		return dataStreamName;
	}

	
	
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
	public void setDataStreamName(String dataStreamName) {
		this.dataStreamName = dataStreamName;
	}

	public String getDataStreamElementName() {
		if (origin !=null){
			return origin.getName();
		}
		return dataStreamElementName;
	}

	public void setDataStreamElementName(String dataStreamElementName) {
		this.dataStreamElementName = dataStreamElementName;
	}

	public IDataStreamElement getOrigin() {
		return origin;
	}
	
	public void setOrigin(IDataStreamElement origin){
		this.origin = origin;
	}

	public String getName() {
//		if (origin != null){
//			return origin.getName() + query;
//		}
		
		return name;
	}
	public void setName(String name){
		this.name = name;
	}

	public String getQuery(){
		return " " + query;
	}
	
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <sqlFilter>\n");
		buf.append("            <name>" + getName() + "</name>\n");
		buf.append("            <dataStreamElement>" + origin.getName() + "</dataStreamElement>\n");
		buf.append("            <dataStream>" + origin.getDataStream().getName() + "</dataStream>\n");
		buf.append("            <query>" + query.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;") + "</query>\n");
		
		
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
		
		buf.append("        </sqlFilter>\n");
		return buf.toString();
	}

	public void setQuery(String query) {
		this.query = query;
		
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null &&!(obj instanceof SqlQueryFilter)){
			return false;
		}
		
		
		SqlQueryFilter f = (SqlQueryFilter)obj;
		if (f == this){
			return true;
		}
		if (origin != null){
			if (origin.getName().equals(f.getOrigin().getName())
					&&
				origin.getDataStream().getName().equals(f.getDataStreamName())
				&&
				f.getQuery().equals(getQuery())){
				
				return true;
				
				
			}
		}
		else if (dataStreamElementName != null){
			if (dataStreamElementName.equals(f.getDataStreamElementName()) 
					&&
				dataStreamName.equals(f.getDataStreamName())
				&&
				f.getQuery().trim().equals(query.trim())){
				
				return true;
				
				
			}
		}
		
		return false;
	}

	public String getSqlWhereClause() {
		StringBuffer buf = new StringBuffer();
		
		if (getOrigin() instanceof ICalculatedElement){
			buf.append(" ((" + ((ICalculatedElement)getOrigin()).getFormula() + ")"	+getQuery()+ ")");
		}
		else{
			buf.append(" (`" + getOrigin().getDataStream().getName() + "`." + getOrigin().getOrigin().getShortName() + getQuery() + ")");
		}
		
		return buf.toString();
	}

	public boolean isGrantedFor(String groupName) {
		for(String key : granted.keySet()){
			if (key.equals(groupName)){
				return granted.get(key);
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
}

