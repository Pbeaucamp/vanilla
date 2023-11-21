package bpm.metadata.layer.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.olap.UnitedOlapDatasource;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.IQueryExecutor;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.query.UnitedOlapQuery;
import bpm.metadata.query.UnitedOlapQueryExecutor;
import bpm.metadata.query.UnitedOlapQueryGenerator;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IVanillaContext;

public class UnitedOlapBusinessPackage implements IBusinessPackage {
	private HashMap<String, IBusinessTable> businessTables = new HashMap<String, IBusinessTable>();
	private HashMap<String, IResource> resources = new HashMap<String, IResource>();
	private List<IBusinessTable> order = new ArrayList<IBusinessTable>();
	
	private List<String> orderString = new ArrayList<String>();
	
	private String name;
	private String description = "";
	
	private BusinessModel model;
	private boolean explorable = false;
	
	private HashMap<String, Boolean> granted = new HashMap<String, Boolean>();
	
	
	private HashMap<Locale, String> outputName = new HashMap<Locale, String>();
	
	private List<String> firstExplorable = new ArrayList<String>();
	
	
	@Override
	public List<List<String>> executeQuery(Integer vanillaGroupWeight, IVanillaContext vanillaCtx, String connectionName, IQuery query, List<List<String>> promptsValues) throws Exception {
		
		UnitedOlapDatasource datasource = (UnitedOlapDatasource) getDataSources("none").get(0);
		
		//return datasource.executeQuery(connectionName, UnitedOlapQueryGenerator.getQuery(vanillaCtx, this, (UnitedOlapQuery) query, ((UnitedOlapQuery)query).getGroupName(), false, null), 0);
		return datasource.executeQuery(connectionName, (UnitedOlapQuery) query, 0, vanillaCtx, promptsValues, this);
	}

	@Override
	public List<List<String>> executeQuery(int limit, String connectionName, String query) throws Exception {
		IDataSource datasource = getDataSources("none").get(0);
		
		return datasource.executeQuery(connectionName, query, limit);
	}
	
	@Override
	public Integer countQuery(int limit, String connectionName, String query) throws Exception {
		IDataSource datasource = getDataSources("none").get(0);
		
		return datasource.countQuery(connectionName, query, limit);
	}
	
	public void addBusinessTableName(String name){
		if (name == null){
			return;
		}
		businessTables.put(name, null);
	}

	@Override
	public IBusinessModel getBusinessModel() {
		return model;
	}

	@Override
	public IBusinessTable getBusinessTable(String groupName, String name) {
		if (groupName.equals("none")){
			return businessTables.get(name);
		}
		
		if (businessTables.get(name) != null && ((AbstractBusinessTable)businessTables.get(name)).isGrantedFor(groupName)){
			return businessTables.get(name);
		}
		return null;
	}

	@Override
	public List<IBusinessTable> getBusinessTables(String groupName) {
		if (groupName.equals("none")){
			return new ArrayList<IBusinessTable>(businessTables.values());
		}
		
		List<IBusinessTable> l = new ArrayList<IBusinessTable>();
		for(IBusinessTable b : businessTables.values()){
			if (b instanceof AbstractBusinessTable && ((AbstractBusinessTable)b).isGrantedFor(groupName)){
				l.add(b);
			}
		}
		return l;
	}

	@Override
	public IConnection getConnection(String groupName, String name) throws Exception {
		for(IDataSource ds : getDataSources("none")){
			
			/*
			 * do not touch this
			 * in case of a UnitedOlap datasource, it is not possbible to 
			 * use alternate connections!!!!
			 * 
			 * ---> let the comments!!!!!
			 */
			for(IConnection c : ds.getConnections(groupName)){
//				if (c.getName().equals(name)){
					return c;
//				}
			}
		}
		
		throw new Exception("The connection " + name + " do not exist in the DataSource " + getName());
	}

	@Override
	public List<String> getConnectionsNames(String groupName) {
		List<String> l = new ArrayList<String>();
		
		for(IDataSource ds : getDataSources(groupName)){
			l.addAll(ds.getConnectionNames(groupName));
		}
		
		return l;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	public List<IDataSource> getDataSources(String groupName){
		List<IDataSource> list = new ArrayList<IDataSource>();
		
		for(IBusinessTable t : businessTables.values()){
			for(IDataStreamElement c : t.getColumns(groupName)){
				if (!list.contains(c.getDataStream().getDataSource())){
					list.add(c.getDataStream().getDataSource());
				}
			}
		}
		
		return list;
	}

	@Override
	public List<IBusinessTable> getExplorableTables(String groupName) {
		List<IBusinessTable> l = new ArrayList<IBusinessTable>();
		
		for(IBusinessTable t : getBusinessTables(groupName)){
			if (t.isDrillable()){
				l.add(t);
			}
		}
		
		return l;
	}

	@Override
	public List<IBusinessTable> getExplorableTables(String groupName, IBusinessTable currentTable) {
		List<IBusinessTable> explorables = getExplorableTables(groupName);
		List<IBusinessTable> result = new ArrayList<IBusinessTable>();
		
				
		List<Relation> rels  = new ArrayList<Relation>();
		
		if (!currentTable.isDrillable()){
			return result;
		}
		
		List<IDataStream> ds = new ArrayList<IDataStream>();
		for(IDataStreamElement e : currentTable.getColumns(groupName)){
			if (!ds.contains(e.getDataStream())){
				ds.add(e.getDataStream());
			}
		}
		
		rels = getRelations(ds);
		
		/*
		 * look for businessTables drillable used by those relations
		 */
		
		for(IBusinessTable t : explorables){
			if (t == currentTable || !t.isDrillable()){
				continue;
			}
			
			for(Relation r : rels){
				for(IDataStreamElement e : t.getColumns(groupName)){
					if (r.getLeftTable() == e.getDataStream() || r.getRightTable() == e.getDataStream()){
						result.add(t);
					}
				}
			}
		}

		return result;
	}

	private List<Relation> getRelations(List<IDataStream> tables) {
		List<Relation> result = new ArrayList<Relation>();
		
		//got the simple relations
		for(Relation r : model.getRelations()){
			boolean right = false;
			boolean left = false;
			
			for(IDataStream ds : tables){
				if (ds.getName().equals(r.getLeftTable().getName())){
					left = true;
				}
				if (ds.getName().equals(r.getRightTable().getName())){
					right = true;
				}
			}
		
			if (left && right){
				result.add(r);
			}
		}
		
		return result;
	}

	@Override
	public List<IBusinessTable> getFirstAccessibleTables(String groupName) {
		List<IBusinessTable> l = new ArrayList<IBusinessTable>();
		
		for(IBusinessTable t : getBusinessTables(groupName)){
			for(String s : firstExplorable){
				if (s.equals(t.getName())){
					l.add(t);
					break;
				}
			}
			
		}
		return l;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<IBusinessTable> getOrderedTables(String groupName) {
		Collection<IBusinessTable> c = getBusinessTables(groupName);
		
		List<IBusinessTable> res = new ArrayList<IBusinessTable>();
		
		for(IBusinessTable t : order){
			if (t != null && c.contains(t)){
				res.add(t);
			}
		}
		
		return res;
	}

	@Override
	public EffectiveQuery evaluateQuery(IVanillaContext vanillaCtx, IQuery query, List<List<String>> promptValues) throws Exception{
		UnitedOlapQuery q = (UnitedOlapQuery) query;
		if (promptValues != null && promptValues.size() != q.getPrompts().size()){
			throw new Exception("The prompt values havent the same size than the number of Prompt in the query");
		}
		
		
		HashMap<Prompt, List<String>> values = new HashMap<Prompt, List<String>>();
		
		for(int i = 0; i< q.getPrompts().size(); i++){
			values.put(q.getPrompts().get(i), promptValues.get(i));
		}
		return UnitedOlapQueryGenerator.getQuery(null, vanillaCtx, this, (UnitedOlapQuery) query, ((UnitedOlapQuery)query).getGroupName(), false, values);
		

	}
	@Override
	public String getQuery(Integer vanillaGroupWeight, IVanillaContext vanillaCtx, IQuery query, List<List<String>> promptValues) throws Exception {
		UnitedOlapQuery q = (UnitedOlapQuery) query;
		if (promptValues != null && promptValues.size() != q.getPrompts().size()){
			throw new Exception("The prompt values havent the same size than the number of Prompt in the query");
		}
		
		
		HashMap<Prompt, List<String>> values = new HashMap<Prompt, List<String>>();
		
		for(int i = 0; i< q.getPrompts().size(); i++){
			values.put(q.getPrompts().get(i), promptValues.get(i));
		}
		EffectiveQuery eq =  UnitedOlapQueryGenerator.getQuery(vanillaGroupWeight, vanillaCtx, this, (UnitedOlapQuery) query, ((UnitedOlapQuery)query).getGroupName(), false, values);
		return eq.getGeneratedQuery();
	}

	@Override
	public HashMap<IBusinessTable, List<Relation>> getRelationsForExplorablesTable(String groupName, IBusinessTable current) {
		List<Relation> rels  = new ArrayList<Relation>();
		HashMap<IBusinessTable, List<Relation>> map = new HashMap<IBusinessTable, List<Relation>>();
		
		List<IDataStream> ds = new ArrayList<IDataStream>();
		for(IDataStreamElement e : current.getColumns(groupName)){
			if (!ds.contains(e.getDataStream())){
				ds.add(e.getDataStream());
			}
		}
		
		rels = getRelationsForExplore(ds);
		
		
		List<IBusinessTable> explorables = getExplorableTables(groupName);
		for(IBusinessTable t : explorables){
			if (t == current){
				continue;
			}
			map.put(t, new ArrayList<Relation>());
			
			
			List<IDataStream> _ds = new ArrayList<IDataStream>();
			for(IDataStreamElement e : t.getColumns(groupName)){
				if (!ds.contains(e.getDataStream())){
					_ds.add(e.getDataStream());
				}
			}
			
			for(Relation r : rels){
				if (ds.contains(r.getLeftTable()) && _ds.contains(r.getRightTable()) ||
					ds.contains(r.getRightTable()) && _ds.contains(r.getLeftTable())	){
					map.get(t).add(r);
				}
			}
			
			
		}
		
		return map;
	}
	
	/**
	 * return all relations for the given tables
	 * @param tables
	 * @return
	 */
	private List<Relation> getRelationsForExplore(List<IDataStream> tables){
		List<Relation> result = new ArrayList<Relation>();
		
		//got the simple relations
		for(Relation r : model.getRelations()){
			boolean right = false;
			boolean left = false;
			
			for(IDataStream ds : tables){
				if (ds.getName().equals(r.getLeftTable().getName())){
					left = true;
				}
				if (ds.getName().equals(r.getRightTable().getName())){
					right = true;
				}
			}
		
			if (left || right){
				result.add(r);
			}
		}
		return result;
	}

	@Override
	public IResource getResourceByName(String name) {
		return resources.get(name);
	}

	@Override
	public IResource getResourceByName(String groupName, String name) {
		for(IResource r : resources.values()){
			if (r.getName().equals(name)){
				if (r.isGrantedFor(groupName)){
					return r;
				}
			}
		}
		return null;
	}

	@Override
	public List<IResource> getResources() {
		return new ArrayList<IResource>(resources.values());
	}

	@Override
	public List<IResource> getResources(String groupName) {
		List<IResource> l = new ArrayList<IResource>();
		for(IResource r : resources.values()){
			if (r.isGrantedFor(groupName)){
				l.add(r);
			}
		}
		return l;
	}

	@Override
	public boolean isExplorable() {
		return explorable;
	}

	@Override
	public boolean isOnOlapDataSource() {
		return true;
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

	@Override
	public void addBusinessTable(IBusinessTable table) {
		if (table == null){
			return;
		}
		businessTables.put(table.getName(), table);
		if (!order.contains(table)){
			order.add(table);
		}
	}

	@Override
	public void addResource(IResource resource) {
		if (resource == null){
			return;
		}
		resources.put(resource.getName(), resource);
	}

	@Override
	public void cleanBusinessTableContent() {
		List<String> keys = new ArrayList<String>();
		
		for(String s : businessTables.keySet()){
			if (businessTables.get(s) == null){
				keys.add(s);
			}
		}
		
		for(String s : keys){
			businessTables.remove(s);
		}
	}

	@Override
	public List<String> getBusinessTableName() {
		return new ArrayList<String>(businessTables.keySet());
	}

	@Override
	public Integer getOrderPosition(String tablename) {
		for(int i = 0 ; i < orderString.size(); i++){
			if (orderString.get(i).equals(tablename)){
				return i;
			}
		}
		return null;
	}

	@Override
	public List<String> getResourceName() {
		return new ArrayList<String>(resources.keySet());
	}
	
	public void order(String businessTableName, Integer position) {
		try{
			if (position == null){
				position = 0;
			}
			IBusinessTable table = null;
			for(String k : businessTables.keySet()){
				if (k.equals(businessTableName)){
					table = businessTables.get(k);
					break;
				}
			}
			
			if (table == null){
				return;
			}
			
			
			/*
			 * if the tabe already ordered we swap it
			 */
			if (order.contains(table)){
				IBusinessTable backup = order.get(position);
				int index = order.indexOf(table);
				order.set(position, table);
				order.set(index, backup);
				return;
				
			}
			
			if (position < order.size()){
				order.set(position, table);
			}
			else{
				for(int i = order.size(); i<=position; i++){
					order.add(null);
				}
				order.set(position, table);
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	
	}
	
	public void order(String businessTableName, String position){
		try{
			int pos = Integer.parseInt(position);
			for(int i= orderString.size(); i<=pos; i++){
				orderString.add(null);
			}
			
			orderString.set(pos, businessTableName);
		}catch(NumberFormatException e){
			
		}
	}
	
	public void removeResource(IResource r){
		resources.remove(r.getName());
	}

	@Override
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <unitedOlapBusinessPackage>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <description>" + description + "</description>\n");
		buf.append("            <explorable>" + explorable + "</explorable>\n");
		
		for(IBusinessTable s : businessTables.values()){
			buf.append("        <businessTableName>" + s.getName() + "</businessTableName>\n");
		}
		
		for(String s : firstExplorable){
			buf.append("        <explorableTables>" + s + "</explorableTables>\n");
		}
		
		
		for(IBusinessTable t : order){
			buf.append("        <order>\n");
			buf.append("            <businessTableName>" + t.getName() + "</businessTableName>");
			buf.append("            <position>" + order.indexOf(t) + "</position>");
			buf.append("        </order>\n");
		}
		
		for(IResource s : resources.values()){
			buf.append("        <resourceName>" + s.getName() + "</resourceName>\n");
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
		
		
		//outputname
		for(Locale l : outputName.keySet()){
			buf.append("            <outputname>\n");
			buf.append("                <country>" + l.getCountry() + "</country>\n");
			buf.append("                <language>" + l.getLanguage() + "</language>\n");
			buf.append("                <value>" + outputName.get(l) + "</value>\n");
			buf.append("            </outputname>\n");
		}
		
		buf.append("        </unitedOlapBusinessPackage>\n");
		
		return buf.toString();
	}

	@Override
	public boolean isGrantedFor(String groupName) {
		if (groupName == null){
			return true;
		}
		for(String s : granted.keySet()){
			if (groupName.equals(s)){
				return granted.get(s);
			}
		}
		return false;
	}

	@Override
	public void setBusinessModel(BusinessModel model) {
		this.model = model;
	}

	@Override
	public void addAccessible(IBusinessTable t){
		if (t == null){
			return;
		}
		for(String s : firstExplorable){
			if (s.equals(t.getName())){
				return;
			}
			
		}
		firstExplorable.add(t.getName());
	}

	@Override
	public List<IBusinessTable> getExplorableTables() {
		List<IBusinessTable> l = new ArrayList<IBusinessTable>();
		for(IBusinessTable t : businessTables.values()){
			if (t != null && t.isDrillable()){
				l.add(t);
			}
		}
		
		return l;
	}

	@Override
	public List<IBusinessTable> getFirstAccessibleTables() {
		List<IBusinessTable> l = new ArrayList<IBusinessTable>();
		for(String s : firstExplorable){
			
			for(IBusinessTable t : businessTables.values()){
				if (s.equals(t.getName())){
					l.add(t);
					break;
				}
				
			}
			
		}
		return l;
	}

	@Override
	public String getOuputName(Locale l){
		return outputName.get(l);
	}

	@Override
	public void removeAccessible(IBusinessTable t){
		firstExplorable.remove(t);
	}

	@Override
	public void removeBusinessTable(IBusinessTable table){
		businessTables.remove(table.getName());
		order.remove(table);
		firstExplorable.remove(table);
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setExplorable(boolean isExplorable) {
		this.explorable = isExplorable;
	}

	@Override
	public void setExplorable(String isExplorable){
		this.explorable = Boolean.parseBoolean(isExplorable);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public void setOutputName(Locale l, String value){
		outputName.put(l, value);
	}
	
	public boolean isMonoDataSource(){
		return getDataSources("").size() == 1;
	}
	
	/**
	 * dont use this, just for digester
	 * @param name
	 */
	public void addResourceName(String name){
		if (name == null){
			return;
		}
		resources.put(name, null);
	}

	@Override
	public IQueryExecutor getQueryExecutor() {
		return new UnitedOlapQueryExecutor(this);
	}

	@Override
	public void addSavedQuery(SavedQuery savedQuery) {
		
		
	}

	@Override
	public List<SavedQuery> getSavedQueries() {
		
		return null;
	}

	@Override
	public void removeSavedQuery(SavedQuery savedQuery) {
		
		
	}

	@Override
	public EffectiveQuery evaluateQuery(IVanillaContext object, IQuery query, List<List<String>> prompts, boolean removePromptWithoutValues) throws Exception {
		return evaluateQuery(object, query, prompts);
	}
}
