package bpm.metadata.layer.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.IFiltered;
import bpm.metadata.ISecurizable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IFilter;
import bpm.vanilla.platform.core.IVanillaContext;

public abstract class AbstractBusinessTable implements IBusinessTable, ISecurizable, IFiltered{
	
	private String parentName ;
	
	
	protected HashMap<String, IDataStreamElement> columns = new LinkedHashMap<String, IDataStreamElement>();
	protected List<IDataStreamElement> order = new ArrayList<IDataStreamElement>();
	protected HashMap<String, List<String>> orderString = new HashMap<String, List<String>>();
	protected String description = "";
	protected String name;
	protected BusinessModel model;
	// used for the loading process
	
	protected HashMap<String, List<String>> columnsName = new HashMap<String, List<String>>(); 
	protected HashMap<String, Boolean> granted = new HashMap<String, Boolean>();
	protected HashMap<Locale, String> outputName = new HashMap<Locale, String>();
	
	protected HashMap<String, List<IFilter>> filters = new HashMap<String, List<IFilter>>();

	protected IBusinessTable parent = null;
	protected List<IBusinessTable> childs = new ArrayList<IBusinessTable>();
	
	protected int positionX = 10;
	protected int positionY = 10;
	
	protected boolean isDrillable = true;
	
	protected boolean isEditable = false;
	
	/**
	 * a BusinessTable Drillable may be explored from a client
	 * @param isDrillable
	 */
	public void setIsDrillable(boolean isDrillable){
		this.isDrillable = isDrillable;
	}
	
	
	public void setIsDrillable(String isDrillable){
		this.isDrillable = Boolean.parseBoolean(isDrillable);
	}
	
	/**
	 * 
	 * @return the idDrillable attribute
	 * 
	 * a BusinessTable Drillable may be explored from a client
	 */
	public boolean isDrillable(){
		return isDrillable;
	}
	
	public void removeColumn(IDataStreamElement col){
		columns.remove(col.getName());
		order.remove(col);
	}
	
	
	public void addFilter(String groupName, IFilter f){
		if (filters.get(groupName) == null){
			filters.put(groupName, new ArrayList<IFilter>());
		}
		filters.get(groupName).add(f);
	}
	
	public void removeFilter(String groupName, IFilter f){
		filters.get(groupName).remove(f);
		
	}
	
	public void removeFilter(IFilter f){
		for(List<IFilter> l : filters.values()){
			l.remove(f);
		}
	}
	
	
	public List<IDataStreamElement> getOrders(){
		return order;
	}
	
	public List<IFilter> getFilterFor(String groupName){
		if (filters.get(groupName) == null){
			filters.put(groupName, new ArrayList<IFilter>());
		}
		
		return filters.get(groupName);
	}
	
	public List<IFilter> getFilters(){
		
		List<IFilter> l = new ArrayList<IFilter>();
		for(String s : filters.keySet()){
			for(IFilter f : filters.get(s)){
				boolean contained = false;
				
				for(IFilter lf : l){
					if (lf.equals(f)){
						contained = true;
					}
				}
				
				if (!contained){
					l.add(f);
				}
			}
		}
		return l;
	}
	
	
	public String getParentName(){
		return parentName;
	}
	
	public void setParentName(String name){
		this.parentName = name;
	}
	
	public void addChild(IBusinessTable child){
		((AbstractBusinessTable)child).setParent(this);
		if (child.getModel() == null){
			((AbstractBusinessTable)child).setBusinessModel(getModel());
		}
		
		childs.add(child);
	}
	
	private void setParent(IBusinessTable parent){
		this.parent = parent;
	}
	
	public List<IBusinessTable> getChilds(String groupName){
		List<IBusinessTable> l = new ArrayList<IBusinessTable>();
		
		for(IBusinessTable t : childs){
			if (groupName.equals("none") || t.isGrantedFor(groupName)){
				l.add(t);
			}
		}
		
		return l;
	}
	
	public IBusinessTable getChildNamed(String name, String groupName){
		for(IBusinessTable t : getChilds(groupName)){
			if (t.getName().equals(name.replace(" ", ""))){
				return t;
			}
		}
		
		return null;
	}
	
	
	public void removeChild(IBusinessTable t){
		childs.remove(t);
	}
	
	public IBusinessTable getParent(){
		return parent;
	}
	
	
	
	
	public String getOuputName(Locale l){
		for(Locale k : outputName.keySet()){
			if (k == l || k.getLanguage().equals(l.getLanguage())){
				if(outputName.get(k) != null && !outputName.get(k).equals("")) {
					return outputName.get(k);
				}
			}
		}
		
		if (outputName.get(l) == null || outputName.get(l).equals("")){
			return name;
		}
		return outputName.get(l);
	}
	
	public String getOuputName(){
		
		return getOuputName(Locale.getDefault());
	}
	
	public void setOutputName(Locale l, String value){
		
		for(Locale k : outputName.keySet()){
			if (l.getLanguage().equals(k.getLanguage())){
				outputName.put(new Locale(l.getLanguage()), value);
				return;
			}
		}
		outputName.put(new Locale(l.getLanguage()), value);
		
	}
	public void setOutputName(String country, String language, String value){
		setOutputName(new Locale(language), value);
	}
	
	protected AbstractBusinessTable(String name){
		this.name = name;
	}
	
	protected AbstractBusinessTable(){}
	
	
	
	public boolean isFilterApplyedToGroup(String groupName, IFilter e) {
		
		return getFilterFor(groupName).contains(e);
	}
	public boolean isGrantedFor(String groupName){
		
		for(String s : granted.keySet()){
			if (groupName.equals(s)){
				return granted.get(s);
			}
		}
		return false;

	}
	
	public void setGranted(String groupName, boolean value) {
		for(String k : granted.keySet()){
			if (k.equals(groupName)){
				this.granted.put(k, value);
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
	
	/**
	 * do not use except in digester
	 * @return
	 */
	public HashMap<String, List<String>> getColumnsNames(){
		return columnsName;
	}
	
	
	/**
	 * do not use, just for the digester
	 * @param tableName : dataStreamName
	 * @param colName : the dataStreamElement name
	 */
	public void addColumnName(String tableName, String colName){
		if (columnsName.get(tableName) == null){
			columnsName.put(tableName, new ArrayList<String>());
		}
		columnsName.get(tableName).add(colName);
		
		
		
		
		
	}
	
	public void addOrderName(String tableName, String colName, String position){
		String key = null;
		
		
		if (orderString.get(tableName) == null){
			orderString.put(tableName, new ArrayList<String>());
			key = tableName;
		}
		else{
			for(String k : orderString.keySet()){
				if (k.equals(tableName)){
					key = k;
					break;
				}
			}
		}
		Integer pos = Integer.parseInt(position);
		
		if (pos >= orderString.get(key).size()){
			for(int i = orderString.get(key).size(); i <= pos; i++){
				orderString.get(key).add(null);
			}
		}
		
		orderString.get(key).set(pos, colName);
	}
	
	public void setOrder(List<IDataStreamElement> order) {
		this.order = order;
	}
	
	
	public void order(IDataStreamElement e, Integer pos){
		if (pos == -1){
			
			if (!order.contains(e)){
				order.add(e);
				return;
			}
			else{
				return;
			}
		}
		
		if (pos >= order.size()){
			for(int i = order.size(); i<= pos; i++){
				order.add(null);
			}
		}
		
		IDataStreamElement backup = order.get(pos);
		
		if (backup != null){
			Integer i = order.indexOf(backup);
			
			order.set(pos, e);
			if (i != null && !i.equals(pos)){
				order.set(i, backup);
			}
		}else{
			order.set(order.indexOf(e), null);
			order.set(pos, e);
			
		}
		
		
		
	}
	
	public List<IDataStreamElement> getColumnsOrdered(String groupName){
		List<IDataStreamElement> c = new ArrayList<IDataStreamElement>();
		for(IDataStreamElement e : columns.values()){
			if (e.isGrantedFor(groupName)){
				c.add(e);
			}
		}
		
		List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
		
		for(int i = 0; i < order.size(); i++){
			if (c.contains(order.get(i))){
				l.add(order.get(i));
			}
		}
		
//		Collections.sort(l, new Comparator<IDataStreamElement>() {
//
//			@Override
//			public int compare(IDataStreamElement o1, IDataStreamElement o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
		
		return l;
	}
	
	
	public Integer getOrder(String tableName, String colName){
		for(String k : orderString.keySet()){
			if (k.equals(tableName)){
				for(int i = 0; i < orderString.get(k).size(); i++){
					if (colName.equals(orderString.get(k).get(i))){
						return i; 
					}
				}
			}
		}
		
		return null;
	}
	
	
	public void addColumn(IDataStreamElement col){
		try{
			columns.put(col.getName(), col);
			order(col, -1);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * return a swing TableModel containing the results
	 * @return
	 * @throws Exception 
	 */
	public abstract List<List<String>> executeQuery(IVanillaContext vanillaCtx, int numRows) throws Exception;
	
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
		setOutputName(Locale.getDefault(), name);
	}

	public Collection<IDataStreamElement> getColumns(){
		return getColumnsOrdered("none"); 
		//return columns.values();
	}
	public Collection<IDataStreamElement> getColumns(String groupName) {
		if (groupName.equals("none")){
			return getColumnsOrdered("none"); 
		}
		
		List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
		
		for(IDataStreamElement e : getColumnsOrdered(groupName)){
			if (e.isGrantedFor(groupName)){
				l.add(e);
			}
		}
		
		return l;
	}
	
	public IDataStreamElement getColumn(String groupName, String colName)throws GrantException{
		
		if (columns.get(colName.replace(" ", "")).isGrantedFor(groupName)){
			return columns.get(colName.replace(" ", ""));
		}
		throw new GrantException("Column unavailable for the group " + groupName);
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	
	public void setPositionX(String positionX) {
		try{
			this.positionX = Integer.parseInt(positionX);
		}catch(NumberFormatException e){
			
		}
		
	}
	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}
	public void setPositionY(String positionY) {
		try{
			this.positionY = Integer.parseInt(positionY);
		}catch(NumberFormatException e){
			
		}
		
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("        <businessTable>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <positionX>" + positionX + "</positionX>\n");
		buf.append("            <positionY>" + positionY + "</positionY>\n");
		buf.append("            <description>" + description + "</description>\n");
		buf.append("            <drillable>" + isDrillable + "</drillable>\n");
		buf.append("            <editable>" + isEditable + "</editable>\n");
		
		
		if (parent != null){
			buf.append("            <parent>" + parent.getName() + "</parent>\n");
		}
		
		for(IDataStreamElement c : columns.values()){
			buf.append("            <column>\n");
			buf.append("                <dataStreamName>" + c.getDataStream().getName() + "</dataStreamName>\n");
			buf.append("                <dataStreamElementName>" + c.getName() + "</dataStreamElementName>\n");
			buf.append("            </column>\n");
		}
		
		for(IDataStreamElement c : order){
			if (c == null){
				continue;
			}
			buf.append("            <order>\n");
			buf.append("                <position>" + order.indexOf(c) + "</position>\n");
			buf.append("                <dataStreamName>" + c.getDataStream().getName() + "</dataStreamName>\n");
			buf.append("                <dataStreamElementName>" + c.getName() + "</dataStreamElementName>\n");
			buf.append("            </order>\n");
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
		
		//outputname
		for(Locale l : outputName.keySet()){
			buf.append("            <outputname>\n");
			buf.append("                <country>" + l.getCountry() + "</country>\n");
			buf.append("                <language>" + l.getLanguage() + "</language>\n");
			buf.append("                <value>" + outputName.get(l) + "</value>\n");
			buf.append("            </outputname>\n");
		}
		
		for(IFilter f : getFilters()){
			buf.append("            <filters>\n");
			boolean contained = false;
			for(String s : filters.keySet()){
				if (filters.get(s).contains(f)){
					contained = true;
					buf.append("                <groupName>" + s + "</groupName>\n");
				}
			}
			
			if (contained){
				buf.append(f.getXml());
			}
			
			buf.append("            </filters>\n");
		
		}
		
		buf.append("        </businessTable>\n");
		
		
		for(IBusinessTable b : childs){
			buf.append(b.getXml());;
		}
		
		return buf.toString();
	}

	/**
	 * remove all columns
	 */
	public void removeAll() {
		columns.clear();
		
	}

	public BusinessModel getModel(){
		return model;
	}
	
	public void setBusinessModel(BusinessModel model){
		this.model = model;

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean isEditable() {
		return isDrillable && isEditable;
	}
	
	@Override
	public void setEditable(boolean editable) {
		this.isEditable = editable;
	}
	
	public void setEditable(String editable) {
		this.isEditable = Boolean.parseBoolean(editable);
	}
}

