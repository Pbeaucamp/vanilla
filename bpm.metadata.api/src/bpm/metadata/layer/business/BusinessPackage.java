package bpm.metadata.layer.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import bpm.metadata.ISecurizable;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.IQueryExecutor;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.query.SqlQueryGenerator;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.metadata.tools.Log;
import bpm.vanilla.platform.core.IVanillaContext;

public class BusinessPackage implements IBusinessPackage, ISecurizable{
	private HashMap<String, IBusinessTable> businessTables = new HashMap<String, IBusinessTable>();
	private HashMap<String, IResource> resources = new HashMap<String, IResource>();
	private List<IBusinessTable> order = new ArrayList<IBusinessTable>();
	
	private List<String> orderString = new ArrayList<String>();
	
	private String name;
	private String description = "";
	
	private BusinessModel model;
	private boolean explorable = true;
	
	private HashMap<String, Boolean> granted = new HashMap<String, Boolean>();
	
	
	private HashMap<Locale, String> outputName = new HashMap<Locale, String>();
	
	private List<String> firstExplorable = new ArrayList<String>();
	private List<SavedQuery> savedQueries = new ArrayList<SavedQuery>();
	
	public String getOuputName(Locale l){
		return outputName.get(l) != null && !outputName.get(l).equals("") ? outputName.get(l) : name;
	}
	
	public String getOuputName(){
		return outputName.get(Locale.getDefault()) != null && !outputName.get(Locale.getDefault()).equals("") ? outputName.get(Locale.getDefault()) : name;
	}
	
	public void setOutputName(Locale l, String value){
		outputName.put(l, value);
	}
	public void setOutputName(String country, String language, String value){
		outputName.put(new Locale(language, country), value);
	}
	

	
	public BusinessPackage(){}
	
	
	public boolean isGrantedFor(String groupName){
//		Boolean b = granted.get(groupName);
		
		if (groupName == null){
			return true;
		}
		for(String s : granted.keySet()){
			if (groupName.equals(s)){
				return granted.get(s);
			}
		}
		
//		if (b != null){
//			return b;
//		}
//		return true;
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
	 * return the BusinessPackage name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * set the BusinessPackage name
	 */
	public void setName(String name) {
		if (model != null){
			model.removePackage(this.getName());
		}
		
		this.name = name;
		
		if (model != null){
			model.addBusinessPackage(this);
		}
		
	}

	/**
	 * return the BusinessPackage description
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<IBusinessTable> getBusinessTables(String groupName) {
		if (groupName.equals("none")){
			return new ArrayList<IBusinessTable>(getOrderedTables(groupName));
		}
		
		List<IBusinessTable> l = new ArrayList<IBusinessTable>();
		for(IBusinessTable b : getOrderedTables(groupName)){
			if (b instanceof AbstractBusinessTable && ((AbstractBusinessTable)b).isGrantedFor(groupName)){
				l.add(b);
			}
		}
		return l;
		
	}

	public List<IResource> getResources() {
		return new ArrayList<IResource>(resources.values());
	}
	
	public void addBusinessTable(IBusinessTable table){
		if (table == null){
			return;
		}
		businessTables.put(table.getName(), table);
		if (!order.contains(table)){
			order.add(table);
		}
		
		firstExplorable.add(table.getName());
		
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
//			order(businessTableName, Integer.parseInt(position));
			int pos = Integer.parseInt(position);
			for(int i= orderString.size(); i<=pos; i++){
				orderString.add(null);
			}
			
			orderString.set(pos, businessTableName);
		}catch(NumberFormatException e){
			
		}
	}
	
	
	public List<IBusinessTable> getOrderedTables(String groupName){
		List<IBusinessTable> c = new ArrayList<IBusinessTable>();
		for(IBusinessTable b : businessTables.values()){
			if (groupName.equals("none") || (b instanceof AbstractBusinessTable && ((AbstractBusinessTable)b).isGrantedFor(groupName))){
				c.add(b);
			}
		}
		
		List<IBusinessTable> res = new ArrayList<IBusinessTable>();
		
		for(IBusinessTable t : order){
			if (t != null && c.contains(t)){
				res.add(t);
			}
		}
		
//		java.util.Collections.sort(res, new Comparator<IBusinessTable>() {
//
//			@Override
//			public int compare(IBusinessTable o1, IBusinessTable o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
		
		return res;
	}
	
	public void removeBusinessTable(IBusinessTable table){
		businessTables.remove(table.getName());
		order.remove(table);
		firstExplorable.remove(table);
	}
	
	public void addResource(IResource resource){
		if (resource == null){
			return;
		}
		resources.put(resource.getName(), resource);
	}
	
	public void removeResource(IResource r){
		resources.remove(r.getName());
	}
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <businessPackage>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <description>" + description + "</description>\n");
		buf.append("            <explorable>" + explorable + "</explorable>\n");
		
		for(IBusinessTable s : businessTables.values()){
			buf.append("            <businessTableName>" + s.getName() + "</businessTableName>\n");
		}
		firstExplorable = new ArrayList<String>(new HashSet<String>(firstExplorable));
		for(String s : firstExplorable){
			buf.append("            <explorableTables>" + s + "</explorableTables>\n");
		}
		
		
		for(IBusinessTable t : order){
			buf.append("            <order>\n");
			buf.append("                <businessTableName>" + t.getName() + "</businessTableName>\n");
			buf.append("                <position>" + order.indexOf(t) + "</position>\n");
			buf.append("            </order>\n");
		}
		
		for(IResource s : resources.values()){
			buf.append("            <resourceName>" + s.getName() + "</resourceName>\n");
		}
		
		if(savedQueries != null && !savedQueries.isEmpty()) {
			buf.append("            <savedQueries>\n");
			for(SavedQuery s : savedQueries) {
				buf.append(s.getXml());
			}
			buf.append("            </savedQueries>\n");
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
		
		buf.append("        </businessPackage>\n");
		
		return buf.toString();
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
	
	/**
	 * dont use this, just for digester
	 * @param name
	 */
	public void addBusinessTableName(String name){
		if (name == null){
			return;
		}
		businessTables.put(name, null);
	}

	public List<String> getBusinessTableName() {
		return new ArrayList<String>(businessTables.keySet());
	}

	public List<String> getResourceName() {
		return new ArrayList<String>(resources.keySet());
	}

	public IBusinessTable getBusinessTable(String groupName, String name) {
		if (groupName.equals("none")){
			return businessTables.get(name);
		}
		
		if (businessTables.get(name) != null && ((AbstractBusinessTable)businessTables.get(name)).isGrantedFor(groupName)){
			return businessTables.get(name);
		}
		
		for(IBusinessTable tab : businessTables.values())  {
			IBusinessTable sub = lookForBusinessTableNamed(tab, groupName, name);
			if(sub != null) {
				return sub;
			}
		}
		return null;

	}


	private IBusinessTable lookForBusinessTableNamed(IBusinessTable table, String groupName, String tableName) {
		if (table.getName().equals(tableName)){
			return table;
		}
		
		IBusinessTable result = null;
		for(IBusinessTable t : table.getChilds(groupName)){
			result = lookForBusinessTableNamed(t, groupName, tableName);
			if (result != null){
				return result;
			}
		}
		return result;
	}

	public IResource getResourceByName(String name) {
		return resources.get(name);
	}


	private List<List<String>> executeQuery(Integer vanillaGroupWeight, IVanillaContext vanillaCtx,/*int limit, boolean isDictinct, */String connectionName, /*String groupName, List<IDataStreamElement> select,
			HashMap<ListOfValue, String> condition, List<AggregateFormula> aggs,
			List<Ordonable> orderBy, List<IFilter> filters,*/ QuerySql fmdtQuery, HashMap<Prompt, List<String>> prompts) throws Exception {
	
		//check if the columns come from the same datasource
		boolean isMonoDataSource = isMonoDataSource(fmdtQuery.getSelect());//isMonoDataSource(select);
		
		//just for unique datasource
		EffectiveQuery q = SqlQueryGenerator.getQuery(vanillaGroupWeight, vanillaCtx, this, fmdtQuery, fmdtQuery.getGroupName(), false, prompts);//getQuery(isDictinct, false, groupName, select, condition, aggs, orderBy, filters, new ArrayList<Formula>(), prompts);
		String query = q.getGeneratedQuery();
		
		List<IDataSource> l = getDataSources(fmdtQuery.getGroupName());
		if (l.size() != 1){
			MetaDataException e = new MetaDataException("Multiple DataSource Query not supported yet");
			Log.error("more than one datasource not supported yet", e);
			throw e;
		}
		int i = 0, j = 0, m=0;
		
		if (/*select*/ fmdtQuery.getSelect() != null){
			i = /*select*/ fmdtQuery.getSelect().size();
		}
		if (/*aggs*/ fmdtQuery.getAggs() != null){
			j = /*aggs*/ fmdtQuery.getAggs().size();
		}
		if (/*aggs*/ fmdtQuery.getFormulas() != null){
			m = /*aggs*/ fmdtQuery.getFormulas().size();
		}
		
		boolean[] flags = new boolean[i + j + m];
		for(int k = 0; k<i; k++){
			flags[k] = /*select*/ fmdtQuery.getSelect().get(k).isVisibleFor(fmdtQuery.getGroupName());
		}
		for(int k = i; k<i+j; k++){
			flags[k] = /*aggs*/ fmdtQuery.getAggs().get(k - i).getCol().isVisibleFor(fmdtQuery.getGroupName());
		}
		for(int k = i+j; k<i+j+m; k++){
			flags[k] = true;
		}
		
		//System.out.println( "query :" + query);

		List<List<String>> result = l.get(0).executeQuery(connectionName, query, flags, fmdtQuery.getLimit());
		
		return result;

	}
	
	
	/**
	 * get all datasources taking part of the businesstable
	 * @return
	 */
	public List<IDataSource> getDataSources(String groupName){
		List<IDataSource> list = new ArrayList<IDataSource>();
		
		for(IBusinessTable t : businessTables.values()){
//			if (t instanceof OLAPBusinessTable){
//				list.add(((OLAPBusinessTable)t).getOlapDataSource());
//			}
//			else{
				for(IDataStreamElement c : t.getColumns(groupName)){
					if (!list.contains(c.getDataStream().getDataSource())){
						list.add(c.getDataStream().getDataSource());
					}
				}
//			}
			
		}
		
		
		return list;
	}


	
	
	
	/**
	 * return all relations for the given tables
	 * @param tables
	 * @return
	 */
	private List<Relation> getRelations(List<IDataStream> tables){
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
	
	
	
	public void setBusinessModel(BusinessModel model){
		this.model = model;
	}
	
	public BusinessModel getBusinessModel(){
		return model;
	}
	
	
	/**
	 * 
	 * @param cols
	 * @return
	 */
	private boolean isMonoDataSource(List<IDataStreamElement> cols){
		return getDataSources("").size() > 1;
	}
	
	@Override
	public EffectiveQuery evaluateQuery(IVanillaContext vanillaCtx, IQuery query, List<List<String>> promptValues) throws Exception{
		return evaluateQuery(vanillaCtx, query, promptValues, true);
	}
	public String getQuery(Integer vanillaGroupWeight, IVanillaContext vanillaCtx, IQuery query, List<List<String>> promptValues) throws Exception {
		if (query instanceof QuerySql){
			QuerySql q = (QuerySql)query;
			
			if (promptValues.size() != q.getPrompts().size()){
				throw new Exception("The prompt values havent the same size than the number of Prompt in the query");
			}
			
			
			HashMap<Prompt, List<String>> values = new HashMap<Prompt, List<String>>();
			
			for(int i = 0; i< q.getPrompts().size(); i++){
				List<String> vals = null;
				if(promptValues.get(i) == null) {
					vals = new ArrayList<String>();
				}
				else {
					vals = promptValues.get(i);
				}
				values.put(q.getPrompts().get(i), promptValues.get(i));
			}
			EffectiveQuery eq =  SqlQueryGenerator.getQuery(vanillaGroupWeight, vanillaCtx, this, q, q.getGroupName(), false, values);
						//getQuery(q.getDistinct(), false, q.getGroupName(), new ArrayList<IDataStreamElement>(q.getSelect()), q.getCondition(), q.getAggs(), q.getOrderBy(), q.getFilters(), q.getFormulas(), values);
			
			String s = eq.getGeneratedQuery();
			Logger.getLogger("bpm.metadata.api").debug("generated SQL Query : " + s);
			return s;
		}
		
		return null;
//		else{
//			QueryOlap q = (QueryOlap)query;
//			OLAPDataSource ds = (OLAPDataSource)getDataSources("").get(0);
//			return q.getQuery(((OlapConnection)ds.getConnection()).getCubeName());
//		}
	}
	
	public List<List<String>> executeQuery(Integer vanillaGroupWeight, IVanillaContext vanillaCtx, String connectionName, IQuery query, List<List<String>> promptValues) throws Exception{
		if (query instanceof QuerySql && isOnOlapDataSource()){
			throw new Exception("Query must be an QuerySql on this package");
		}
//		if (query instanceof QueryOlap && !isOnOlapDataSource()){
//			throw new Exception("Query must be an QueryOlap on this package");
//		}
		
		if (query instanceof QuerySql){
			QuerySql q = (QuerySql)query;
			
			if (promptValues.size() != q.getPrompts().size()){
				throw new Exception("The prompt values havent the same size than the number of Prompt in the query");
			}
			
			
			HashMap<Prompt, List<String>> values = new HashMap<Prompt, List<String>>();
			
			for(int i = 0; i< q.getPrompts().size(); i++){
				values.put(q.getPrompts().get(i), promptValues.get(i));
			}
			
			
//			return execu,teQuery(q.getLimit(), q.getDistinct(), connectionName, q.getGroupName(), q.getSelect(), q.getCondition(), q.getAggs(), q.getOrderBy(), q.getFilters(), values);
			return executeQuery(vanillaGroupWeight, vanillaCtx, connectionName, q, values);
		}
		
		return null;
//		else{
//			OLAPDataSource ds = (OLAPDataSource)getDataSources("").get(0);
//			
//			return ds.executeQuery(connectionName, query);
//			
//		}
	
	}
	

	public boolean isMonoDataSource(){
		return getDataSources("none").size() == 1;
	}
	
	public boolean isOnOlapDataSource(){
		return false;//getDataSources("").size() == 1 && getDataSources("").get(0) instanceof OLAPDataSource;
		
	}

	@Override
	public List<List<String>> executeQuery(int limit, String connectionName, String query) throws Exception {
		List<IDataSource> l = getDataSources("none");
		if (l.size() != 1){
			MetaDataException e = new MetaDataException("Multiple DataSource Query not supported yet");
			Log.error("more than one datasource not supported yet", e);
			throw e;
		}
		
		List<List<String>> result = l.get(0).executeQuery(connectionName, query, limit);
		
		return result;

	}

	@Override
	public Integer countQuery(int limit, String connectionName, String query) throws Exception {
		List<IDataSource> l = getDataSources("none");
		if (l.size() != 1){
			MetaDataException e = new MetaDataException("Multiple DataSource Query not supported yet");
			Log.error("more than one datasource not supported yet", e);
			throw e;
		}
		
		return l.get(0).countQuery(connectionName, query, limit);
	}

//	public OLAPResult executeOlapQuery(QueryOlap query) throws Exception {
//		if (!isOnOlapDataSource()){
//			throw new Exception("cannot execute an OLAPQuery on other DataSource than Olap ones");
//		}
////		OLAPDataSource ds = (OLAPDataSource)getDataSources("").get(0);
//			
////		return ds.executeOlapQuery(query);
//		return null;
//	
//
//	}

//	public String getDynamicalQuery(IQuery query) throws Exception {
//		if (query instanceof QuerySql){
//			QuerySql q = (QuerySql)query;
//			
//			HashMap<Prompt, List<String>> values = new HashMap<Prompt, List<String>>();
//			
//			for(Prompt p : q.getPrompts()){
//				values.put(p, new ArrayList<String>());
//			}
//			return SqlQueryGenerator.getQuery(this, q, q.getGroupName(), true, values);
////			return getQuery(q.getDistinct(), true, q.getGroupName(), q.getSelect(), q.getCondition(), q.getAggs(), q.getOrderBy(), q.getFilters(), q.getFormulas(), values);
//		}
//		else{
//			QueryOlap q = (QueryOlap)query;
//			OLAPDataSource ds = (OLAPDataSource)getDataSources("").get(0);
//			return q.getQuery(((OlapConnection)ds.getConnection()).getCubeName());
//		}
//	}

	public IConnection getConnection(String groupName, String name) throws Exception{
		System.out.println("GetConnection");
		for(IDataSource ds : getDataSources("none")){
			System.out.println(ds.getName());
			for(IConnection c : ds.getConnections(groupName)){
//				if (c.getName().equals(name)){
					return c;
//				}
			}
		}
//		name = null;
		if (name == null){
			 if (!getDataSources("none").isEmpty()){
				 try{
					 return getDataSources("none").get(0).getConnections("none").get(0);
				 }catch(Exception ex){
					 ex.printStackTrace();
				 }
			 }
		}
		throw new Exception("The connection " + name + " do nogt exist in the DataSource " + getName());
		
	}

	public List<String> getConnectionsNames(String groupName) {
		List<String> l = new ArrayList<String>();
		
		for(IDataSource ds : getDataSources(groupName)){
			l.addAll(ds.getConnectionNames(groupName));
		}
		
		return l;
	}

//	public HashMap<String, List<List<String>>> burst(IVanillaContext vanillaCtx, List<String> groupName, String connectionName,
//			IQuery query, List<List<String>> promptValues) throws Exception {
//		
//		HashMap<String, List<List<String>>> result = new HashMap<String, List<List<String>>>();
//		
//		QuerySql q = (QuerySql)query;
//		
//		for(String s : groupName){
//			try{
//				IQuery qq = SqlQueryBuilder.getQuery(s, q.getSelect(), q.getCondition(), q.getAggs(), q.getOrderBy(), q.getFilters(), q.getPrompts());
//								
//				if (promptValues.size() != q.getPrompts().size()){
//					throw new Exception("The prompt values havent the same size than the number of Prompt in the query");
//				}
//				
//				result.put(s, executeQuery(vanillaCtx, connectionName, qq, promptValues));
//			}catch (Exception e){
//				
//				e.printStackTrace();
////				//System.out.println("Error for while bursting on group " + s);
//			}
//			
//		}
//		
//		
//		return result;
//	}




	

	
//	
	


	
	
	
	public Relation getFirst(HashMap<Relation, Integer> nb, HashMap<IDataStream, Integer> nbStr){
		List<Relation> possible = new ArrayList<Relation>();
		Relation first = null;
		
		for(Relation r : nb.keySet()){
			if (nb.get(r) == 1 && (nbStr.get(r.getLeftTable()) == 1 || nbStr.get(r.getRightTable()) == 1)){
				possible.add(r);
			}
		}
		
		for(Relation r : possible){
			int score = nbStr.get(r.getLeftTable()) + nbStr.get(r.getRightTable());
			if (first == null || score < nbStr.get(first.getLeftTable()) + nbStr.get(first.getRightTable())){
				//System.out.println("score = " + score);
				first = r;
				//System.out.println("first = " + first.getName());
			}
			else if (score == nbStr.get(first.getLeftTable()) + nbStr.get(first.getRightTable())){
				
				if (r.getJoins().get(0).getOuter() == Join.LEFT_OUTER){
					if (nbStr.get(r.getLeftTable()) < nbStr.get(first.getLeftTable())){
						first = r;
					}
				}
				else if (r.getJoins().get(0).getOuter() == Join.RIGHT_OUTER){
					if (nbStr.get(r.getLeftTable()) > nbStr.get(first.getLeftTable())){
						first = r;
					}
				}
				
			}
		}
		return first;
	}
	
	
	
	public Relation getLast(HashMap<Relation, Integer> nb, HashMap<IDataStream, Integer> nbStr){
		List<Relation> possible = new ArrayList<Relation>();
		Relation last = null;
		
		for(Relation r : nb.keySet()){
			if (nb.get(r) == 1 && (nbStr.get(r.getLeftTable()) == 1 || nbStr.get(r.getRightTable()) == 1)){
				possible.add(r);
			}
		}
		
		for(Relation r : possible){
			int score = nbStr.get(r.getLeftTable()) + nbStr.get(r.getRightTable());
			if (last == null || score > nbStr.get(last.getLeftTable()) + nbStr.get(last.getRightTable())){
				//System.out.println("score = " + score);
				last = r;
				//System.out.println("last = " + last.getName());
			}
			else if (score == nbStr.get(last.getLeftTable()) + nbStr.get(last.getRightTable())){
				
				if (r.getName().compareTo(last.getName()) < 0){
					last = r;
				}
			}
			
			
		}
		return last;
	}
	
	

	public Integer getOrderPosition(String tablename) {
		for(int i = 0 ; i < orderString.size(); i++){
			if (orderString.get(i).equals(tablename)){
				return i;
			}
		}
		return null;
	}

	/**
	 * remove empty Key from businessTables HashMap
	 */
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

	public List<IBusinessTable> getExplorableTables(String groupName) {
		List<IBusinessTable> l = new ArrayList<IBusinessTable>();
		
		for(IBusinessTable t : getBusinessTables(groupName)){
			if (t.isDrillable()){
				System.out.println("add drillable table " + t.getName());
				l.add(t);
//				getChildsExplorable(groupName, t, l);
			}
		}
		
		return l;
	}
	
	private void getChildsExplorable(String groupName, IBusinessTable parent, List<IBusinessTable> l) {
		for(IBusinessTable t : parent.getChilds(groupName)){
			if (t.isDrillable()){ 
				l.add(t);
				getChildsExplorable(groupName, t, l);
			}
		}
	}
	
	public boolean isExplorable(){
		return explorable;
	}
	
	public void setExplorable(boolean isExplorable){
		this.explorable = isExplorable;
	}
	
	public void setExplorable(String isExplorable){
		this.explorable = Boolean.parseBoolean(isExplorable);
	}
	
	public List<IBusinessTable> getFirstAccessibleTables(String groupName){
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
	
	public void addAccessible(String t){
		if (t == null){
			return;
		}
		firstExplorable.add(t);
	}
	
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
	
	public void removeAccessible(IBusinessTable t){
		firstExplorable.remove(t);
	}

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

	public List<IBusinessTable> getExplorableTables() {
		List<IBusinessTable> l = new ArrayList<IBusinessTable>();
		for(IBusinessTable t : businessTables.values()){
			if (t != null && t.isDrillable()){
				l.add(t);
			}
		}
		
		return l;
	}

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
	
	
	public HashMap<IBusinessTable, List<Relation>> getLinkedBusinessTables(String groupName, IBusinessTable current) {
		List<Relation> rels  = new ArrayList<Relation>();
		HashMap<IBusinessTable, List<Relation>> map = new HashMap<IBusinessTable, List<Relation>>();
		
		List<IDataStream> ds = new ArrayList<IDataStream>();
		for(IBusinessTable t : getBusinessTables(groupName)){
			for(IDataStreamElement e : t.getColumns(groupName)){
				if (!ds.contains(e.getDataStream())){
					ds.add(e.getDataStream());
				}
			}
		}
		
		
		rels = getRelations(ds);
		
		
		Collection<IBusinessTable> explorables = getBusinessTables(groupName);
		for(IBusinessTable t : explorables){
			if (t == current){
				continue;
			}
			map.put(t, new ArrayList<Relation>());
			
			
			List<IDataStream> _ds = new ArrayList<IDataStream>(ds);
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

	public List<IResource> getResources(String groupName) {
		List<IResource> l = new ArrayList<IResource>();
		for(IResource r : resources.values()){
			if (groupName.equals("none") || r.isGrantedFor(groupName)){
				l.add(r);
			}
		}
		return l;
	}

	@Override
	public IQueryExecutor getQueryExecutor() {
		
		return null;
	}

	@Override
	public void addSavedQuery(SavedQuery savedQuery) {
		if(savedQueries == null) {
			savedQueries = new ArrayList<SavedQuery>();
		}
		savedQueries.add(savedQuery);
	}

	@Override
	public List<SavedQuery> getSavedQueries() {
		return savedQueries;
	}

	@Override
	public void removeSavedQuery(SavedQuery savedQuery) {
		if(savedQueries != null) {
			savedQueries.remove(savedQuery);
		}
	}

	@Override
	public EffectiveQuery evaluateQuery(IVanillaContext vanillaCtx, IQuery query, List<List<String>> promptValues, boolean removePromptWithoutValues) throws Exception {
		if (query instanceof QuerySql){
			QuerySql q = (QuerySql)query;
			
			if (promptValues.size() != q.getPrompts().size()){
				throw new Exception("The prompt values havent the same size than the number of Prompt in the query");
			}
			
			
			HashMap<Prompt, List<String>> values = new HashMap<Prompt, List<String>>();
			
			for(int i = 0; i< q.getPrompts().size(); i++){
				values.put(q.getPrompts().get(i), promptValues.get(i));
			}
			EffectiveQuery s =  SqlQueryGenerator.getQuery(null, vanillaCtx, this, q, q.getGroupName(), false, values, removePromptWithoutValues);
						//getQuery(q.getDistinct(), false, q.getGroupName(), new ArrayList<IDataStreamElement>(q.getSelect()), q.getCondition(), q.getAggs(), q.getOrderBy(), q.getFilters(), q.getFormulas(), values);
			
			
			Logger.getLogger(getClass()).debug("generated SQL Query : " + s);
			return s;
		}
		
		return null;
	}

}
