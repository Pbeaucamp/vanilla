package bpm.metadata.layer.logical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.metadata.IFiltered;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.ITable;
import bpm.metadata.misc.Type;
import bpm.metadata.resource.IFilter;
import bpm.metadata.tools.Log;

/**
 * Thie interface provide an abstraction on logical Table
 * All Table type(sql, xls, xml ...) must implements IDataStream
 * @author LCA
 *
 */
public abstract class IDataStream implements IFiltered{
	public static final int UNDEFINED = 0;
	public static final int FACT_TABLE = 1;
	public static final int DIMENSION_TABLE = 2;
	public static final int CLOSURE_TABLE = 3;
	public static final String[] TYPES_NAMES = new String[]{"Undefined", "Fact Table", "Dimension Table", "Closure Table"};
	
	/**
	 * Custom Type set be the designer
	 */
	private Type customType;
	private int positionX = 10;
	private int positionY = 10;
	protected String name;
	protected String description = "";
	protected int outputLength = 100;
	protected int weight;
	protected int type;
	protected IDataSource dataSource;
	
	protected ITable origin;
	
	protected String originName;
	
	protected HashMap<String, IDataStreamElement> columns = new LinkedHashMap<String, IDataStreamElement>();
	
	
//	protected HashMap<String, Boolean> granted = new HashMap<String, Boolean>();
	
	protected HashMap<String, List<IFilter>> filters = new HashMap<String, List<IFilter>>();
	
	/*
	 * to avoid loosing a filter if no group is mapped to it
	 */
	protected List<IFilter> backupFilters = new ArrayList<IFilter>();
	
	/**
	 * those filters are always used regardless of the user Group context
	 * usefull to avoid using SQL query for exemple
	 */
	protected List<IFilter> genericFilter = new ArrayList<IFilter>();
	
	protected IDataStream(){}
	
	public abstract boolean isMeasure();
	public void addFilter(IFilter f){
		if (!backupFilters.contains(f)){
			backupFilters.add(f);
		}
		
	}
	
	/**
	 * add a Generic Filter which will be used for very users regardless of their groups
	 * @param f
	 * @throws Exception
	  */
	public boolean addGenericFilter(IFilter f){
		for(IFilter _f : genericFilter){
			if (_f.getName().equals(f.getName())){
				return false;
			}
			if (_f.equals(f)){
				return false;
			}
		}
		
		if (f == null){
			return false;
		}
		genericFilter.add(f);
		return true;
	}
	
	/**
	 * remove f fro the existing genericsFilters
	 * @param f
	 */
	public void removeGenericFilter(IFilter f){
		genericFilter.remove(f);
	}
	
	/**
	 * 
	 * @return the GenericFilters createdOn this object
	 */
	public List<IFilter> getGenericFilters(){
		return new ArrayList<IFilter>(genericFilter);
	}
	
	public void addFilter(String groupName, IFilter f){
		
		for(String s : filters.keySet()){
			if (s.equals(groupName)){
				filters.get(s).add(f);
				return;
			}
		}
//		if (filters.get(groupName) == null){
			filters.put(groupName, new ArrayList<IFilter>());
			filters.get(groupName).add(f);
//		}
			
		if (!backupFilters.contains(f)){
			backupFilters.add(f);
		}
		
	}
	
	public void removeFilter(String groupName, IFilter f){
		for(String key : filters.keySet()){
			if (key.equals(groupName)){
				filters.get(key).remove(f);
			}
		}
		if (!backupFilters.contains(f)){
			backupFilters.add(f);
		}
	}
	
	public void removeFilter(IFilter f){
		
		for(String s : filters.keySet()){
			if (filters.get(s) != null){
				filters.get(s).remove(f);
			}
		}
		backupFilters.remove(f);
//		for(List<IFilter> l : filters.values()){
//			l.remove(f);
//		}
	}
	
	public List<IFilter> getFilterFor(String groupName){
		if (filters.get(groupName) == null){
			filters.put(groupName, new ArrayList<IFilter>());
		}
		
		return filters.get(groupName);
	}
	
	public List<IFilter> getFilters(){
		List<IFilter> l = new ArrayList<IFilter>();
		
		for(List<IFilter> lf : filters.values()){
			for(IFilter f : lf){
				if (!l.contains(f)){
					l.add(f);
				}
			}
		}
		
		for(IFilter f : backupFilters){
			if (!l.contains(f)){
				l.add(f);
			}
		}
//		List<IFilter> l = new ArrayList<IFilter>();
//		for(String s : filters.keySet()){
//			for(IFilter f : filters.get(s)){
//				boolean contained = false;
//				
//				for(IFilter lf : l){
//					if (lf == f){
//						contained = true;
//						break;
//					}
//				}
//				
//				if (!contained){
//					l.add(f);
//				}
//			}
//		}
		return l;
	}
	
//	public boolean isGrantedFor(String groupName){
//		Boolean b = granted.get(groupName);
//		if (b != null){
//			return b;
//		}
//		return true;
//	}
//	
//	public void setGranted(String groupName, boolean value) {
//		this.granted.put(groupName, value);
//	}
//	
//	public void setGranted(String groupName, String value) {
//		this.granted.put(groupName, Boolean.parseBoolean(value));
//	}
//	public HashMap<String, Boolean> getGrants(){
//		return granted;
//	}
	
	protected IDataStream(ITable origine){
		this.origin = origine;
		this.name = origine.getName();
	}
	
	/**
	 * return the name of the dataSource
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	
	/**
	 * return the Description of the dataSource
	 * @return
	 */
	public String getDescription(){
		return description;
	}
	
	
	/**
	 * the size for the output datas 
	 * XXX : should be placed in the Column interface
	 * @return
	 */
	public int getOutputLength(){
		return outputLength;
	}
	
	
	/**
	 * return the type of the Table(dim, fasct, closure...)
	 * @return
	 */
	public int getType(){
		return type;
	}
	
	
	public int getWeight(){
		return weight;
	}
	
	
	public Collection<IDataStreamElement> getElements(){
		List<IDataStreamElement> orders = new ArrayList<IDataStreamElement>();
		
		orders.addAll(columns.values());
		
		Collections.sort(orders, new Comparator<IDataStreamElement>() {
			@Override
			public int compare(IDataStreamElement o1, IDataStreamElement o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		return orders;
	}
	
	public void addColumn(IDataStreamElement e){
		columns.put(e.getName(), e);
		e.setDataStream(this);
	}
	
	public ITable getOrigin(){
		return origin;
	}

	public void setName(String name) {
		List<Relation> relsInvolving = new ArrayList<Relation>();
		if (dataSource != null){
			for(Relation r : dataSource.getRelations()){
				if (r.isUsingTable(this)){
					relsInvolving.add(r);
				}
			}
			dataSource.remove(this);
		}
		
		this.name = name.replace(" ", "");
		if (dataSource != null){
			dataSource.add(this);
			for(Relation r : relsInvolving){
				dataSource.addRelation(r);
			}
		}
		
		
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOutputLength(int outputLength) {
		this.outputLength = outputLength;
	}

	public void setOutputLength(String outputLength) {
		try{
			this.outputLength = Integer.parseInt(outputLength);
		}catch(NumberFormatException e){
			Log.error("Error while setting IDataStream outputLength", e);
		}
		
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public void setWeight(String weight) {
		try{
			this.weight = Integer.parseInt(weight);
		}catch(NumberFormatException e){
			Log.error("Error while setting IDataStream outputLength", e);
		}
		
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setType(String type){
		try{
			this.type = Integer.parseInt(type);
		}catch(NumberFormatException e){
			Log.error("Error while setting IDataStream outputLength", e);
		}
	}
	
	public void setOrigin(ITable origin) {
		this.origin = origin;
	}
	
	public IDataStreamElement getElementNamed(String name){
		return columns.get(name.replace(" ", ""));
	}
	
	public IColumn getElementOrigin(String name) {
		for(IColumn col : getOrigin().getColumns()) {
			if(col.getName().equals(name))  {
				return col;
			}
		}
		return null;
	}
	
	public IDataSource getDataSource(){
		return dataSource;
	}
	
	/**
	 * should'nt be used
	 * @param dataSource
	 */
	public void setDataSource(IDataSource dataSource){
		this.dataSource = dataSource;
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("        <dataStream>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <description>" + description + "</description>\n");
		buf.append("            <outputLength>" + outputLength + "</outputLength>\n");
		buf.append("            <weight>" + weight + "</weight>\n");
		buf.append("            <type>" + type + "</type>\n");
		buf.append("            <positionX>" + positionX + "</positionX>\n");
		buf.append("            <positionY>" + positionY + "</positionY>\n");
		if (customType != null) {
			buf.append("            " + customType.getXml());
		}
		
		if (this instanceof SQLDataStream){
			SQLDataStream ds = (SQLDataStream)this;
			if (ds.getSQLType() == SQLDataStream.SQL_QUERY || (ds.getSql() != null && !ds.getSql().trim().equals(""))){
				buf.append("            <sql>" + ds.getSql().replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;") + "</sql>\n");
			}
			else{
				if (origin == null){
					buf.append("            <origin>" + originName + "</origin>\n");
				}
				else{
					buf.append("            <origin>" + origin.getName() + "</origin>\n");
				}
				
			}
		}
		else{
			if (origin == null){
				buf.append("            <origin>" + originName + "</origin>\n");
			}
			else{
				buf.append("            <origin>" + origin.getName() + "</origin>\n");
			}
//			buf.append("            <origin>" + origin.getName() + "</origin>\n");
		}
		
		
		for(IDataStreamElement c: columns.values()){
			buf.append(c.getXml());
		}
		
		
//		//grants
//		for(String s : granted.keySet()){
//			buf.append("            <grant>\n");
//			    buf.append("                <groupName>" + s + "</groupName>\n");
//			    buf.append("                <isGranted>" + granted.get(s) + "</isGranted>\n");
//			buf.append("            </grant>\n");
//		}
		
		//filters
//		for(String s : filters.keySet()){
//			if (filters.get(s).size() > 0){
//				
//				buf.append("            <filters>\n");
//				buf.append("                <groupName>" + s + "</groupName>\n");
//				for(IFilter f : filters.get(s)){
//					buf.append(f.getXml());
//				}
//				buf.append("            </filters>\n");
//			}
//			
//		}
		List l = getFilters();
		for(IFilter f : getFilters()){
			buf.append("            <filters>\n");
			boolean contained = false;
			for(String s : filters.keySet()){
				for(IFilter _f : filters.get(s)){
					if (_f == f){
						contained = true;
						buf.append("                <groupName>" + s + "</groupName>\n");
					}
				}
				
			}
			
//			if (contained){
				buf.append(f.getXml());
//			}
			
			buf.append("            </filters>\n");
		
		}
		
		
		/*
		 * the genericfilters
		 */
		buf.append("            <genericFilters>\n");
		for(IFilter f : getGenericFilters()){
			buf.append(f.getXml());
		}
		buf.append("            </genericFilters>\n");
		
		buf.append("        </dataStream>\n");
		return buf.toString();
	}

	
	/**
	 * just used by Digester to establish reference
	 * @param originName
	 */
	public void setOriginName(String originName) {
		this.originName = originName;
	}
	
	public void addCalculatedElement(ICalculatedElement e){
		columns.put(e.getName(), e);
		e.setDataStream(this);
	}

	public String getOriginName() {
		return originName;
	}
	
	public void removeDataStreamElement(IDataStreamElement e){
		columns.remove(e.getName());
	}
	
	/**
	 * to avoid a change on the .equals filter Method that could generate some problems
	 * @return true if the filter is applyed for the givenGroup
	 * @param f
	 */
	public boolean isFilterApplyedToGroup(String groupName, IFilter f){
		Collection<IFilter> sec = null;
		
		for(String s : filters.keySet()){
			if (s.equals(groupName)){
				sec = filters.get(s);
			}
		}
		if (sec == null){
			return false;
		}
		
		for(IFilter _f : sec){
			if (_f == f){
				return true;
			}
		}
		
		return false;
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
	
	public void setCustomType(Type type) {
		this.customType = type;
	}
	
	public Type getCustomType() {
		return this.customType;
	}
	
}
