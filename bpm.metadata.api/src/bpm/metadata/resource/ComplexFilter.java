package bpm.metadata.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.ISecurizable;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;

public class ComplexFilter implements IFilter, ISecurizable{
	public static final  String[] OPERATORS = new String[]{"=", "<", "<=", ">", ">=", "!=", "<>", "IN", "NOT IN", "LIKE", "BETWEEN"};
	
	
	private String name = "";
	private String dataStreamName, dataStreamElementName;
	private String operator = "=";
	private List<String> value = new ArrayList<String>();
	
	private IDataStreamElement origin;
	private HashMap<String, Boolean> granted = new HashMap<String, Boolean>();
	private HashMap<Locale, String> outputName = new HashMap<Locale, String>();
	public ComplexFilter(){
		
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
	
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		
		if (this.operator.equals("IN") && !operator.equals("IN")){
			
		}
		
		this.operator = operator;
	}

	public List<String> getValue() {
		return new ArrayList<String>(value);
	}

	/**
	 * empty the values
	 */
	public void clearValue(){
		value.clear();
	}
	
	public void setValue(String value) {
		if (operator.equals("IN") || operator.equals("NOT IN") || this.value.size() == 0){
			this.value.add(value);
		}
		else if (operator.equals("BETWEEN")){
			if (this.value.size() < 2){
				this.value.add(value);
			}
			else{
				this.value.clear();
				this.value.add(value);
			}
		}
		else{
			this.value.clear();
			this.value.add(value);
			
		}
		
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
	
		
	public void setName(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

		
	public void setOrigin(IDataStreamElement origin){
		this.origin = origin;
		this.dataStreamName = origin.getDataStream().getName();
		this.dataStreamElementName = origin.getName();
	}
	
	public IDataStreamElement getOrigin(){
		return origin;
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <complexFilter>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <dataStreamElement>" + origin.getName() + "</dataStreamElement>\n");
		buf.append("            <dataStream>" + origin.getDataStream().getName() + "</dataStream>\n");

		buf.append("            <operator>" + operator.replace(">", "&gt;").replace("<", "&lt;") + "</operator>\n");
		
		for(String v : value){
			buf.append("            <value>" + v.replace("&" , "&amp;") + "</value>\n");
		}
		
		
		
		//grants
		buf.append("        <groupNames>");
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
		
		buf.append("        </complexFilter>\n");
		
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
		if (obj != null &&!(obj instanceof ComplexFilter)){
			return false;
		}
		ComplexFilter f = (ComplexFilter)obj;
		
		if ( f== this){
			return true;
		}
		
		
		if (name.equals(f.getName()) && dataStreamElementName.equals(f.getDataStreamElementName()) 
				&&
			dataStreamName.equals(f.getDataStreamName())){
			
			for(String s : value){
				boolean found = false;
				for(String _s : f.getValue()){
					if (s.equals(_s)){
						found = true;
						break;
					}
				}
				if (found == false){
					return false;
				}
				
			}
			
			return true;
			
			
		}
		return false;
	}

	public String getSqlWhereClause() {
		StringBuffer buf = new StringBuffer();
		
		if (getOrigin() instanceof ICalculatedElement){
			
			if (operator.equals("IN") || operator.equals("BETWEEN") || operator.equals("LIKE") || operator.equals("NOT IN")){
				buf.append(((ICalculatedElement)getOrigin()).getFormula() + " " + getOperator() );
			}
			else{
				buf.append(((ICalculatedElement)getOrigin()).getFormula() +  getOperator() );
			}
			
			
			try {
				if (((ICalculatedElement)getOrigin()).getJavaClassName().equals(String.class.getName())||
						((ICalculatedElement)getOrigin()).getJavaClassName().equals(Date.class.getName()) ||
						((ICalculatedElement)getOrigin()).getJavaClassName().equals(java.sql.Timestamp.class.getName()) ||
						((ICalculatedElement)getOrigin()).getJavaClassName().equals(java.sql.Time.class.getName()) ||

						((ICalculatedElement)getOrigin()).getJavaClassName().equals(java.sql.Date.class.getName())){
					buf.append(getValueForSql(true) );
				}
				else{
					buf.append(getValueForSql(false));
				}
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
		}
		else{
			if (operator.equals("IN") || operator.equals("BETWEEN") || operator.equals("LIKE") || operator.equals("NOT IN")){
				buf.append("`" + getOrigin().getDataStream().getName() + "`." + getOrigin().getOrigin().getShortName() + " " + getOperator() );
			}
			
			else{
				buf.append("`"  + getOrigin().getDataStream().getName() + "`." + getOrigin().getOrigin().getShortName() + getOperator() );
			}
			
			try {
				if (getOrigin().getOrigin().getClassName().equals(String.class.getName())||
					getOrigin().getOrigin().getClassName().equals(Date.class.getName()) ||
					getOrigin().getOrigin().getClassName().equals(java.sql.Timestamp.class.getName()) ||
					getOrigin().getOrigin().getClassName().equals(java.sql.Time.class.getName()) ||

					getOrigin().getOrigin().getClassName().equals(java.sql.Date.class.getName())){
					buf.append(getValueForSql(true) );
				}
				else{
					buf.append(getValueForSql(false));
				}
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}

		}
		return buf.toString();
	}
	
	private String getValueForSql(boolean useQuote){
		StringBuffer buf = new StringBuffer();
		boolean isBetween = operator.equals("BETWEEN");
		
		if (operator.equals("LIKE")){
			buf.append(" '" + value.get(0) + "' ");
			return buf.toString();
		}
		
		if (isBetween && value.size() > 1){
			if (useQuote){

				buf.append(" '" + value.get(0) + "' AND '" + value.get(1) + "' ");
			}
			else{
				buf.append(" " + value.get(0) + " AND " + value.get(1) + " ");
			}
			return buf.toString();
		}
		
		if (operator.equals("IN")  || operator.equals("NOT IN")){
			
			
			boolean first = true;
			
			for(String s : value){
				if (first){
					first = false;
					buf.append("(");
				}
				else{
					buf.append(", ");
				}
				if (!useQuote){
					buf.append(s);
				}
				else{
					buf.append("'" + s + "'");
				}

			}
			buf.append(")");
			return buf.toString();
			
		}
		else{
			try{
				if (useQuote){
					return "'" + getValue().get(0) + "'";
				}
				else{
					return getValue().get(0);
				}
				
			}catch(IndexOutOfBoundsException e){
				if (useQuote){
					return "''";
				}
				return "";
			}
			
		}
	}
}
