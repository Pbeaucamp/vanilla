package bpm.metadata.layer.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.Ordonable;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.query.SqlQueryGenerator;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IVanillaContext;

public class SQLBusinessTable extends AbstractBusinessTable {

	/**
	 * do not use
	 */
	public SQLBusinessTable(){}
	
	public SQLBusinessTable(String name){
		super(name);
	}

	@Override
	public List<List<String>> executeQuery(IVanillaContext vanillaCtx, int numRows) throws Exception {
		//just for unique datasource
		String query = getQuery(vanillaCtx);
		List<IDataSource> l = getDataSources();
		if (l.size() != 1){
			throw new Exception("more than one datasource not supported yet");
		}
		
		List<List<String>> result = l.get(0).executeQuery(l.get(0).getConnections(null).get(0).getName(), query, numRows);
		
		
		
		
		return result;
	}
	
	public SQLConnection getDefaultConnection() throws Exception{
		List<IDataSource> l = getDataSources();
		if (l.size() != 1){
			throw new Exception("more than one datasource not supported yet");
		}
		return (SQLConnection)l.get(0).getConnections(null).get(0);

	}
	
	
	/**
	 * get all datasources taking part of the businesstable
	 * @return
	 */
	private List<IDataSource> getDataSources(){
		List<IDataSource> list = new ArrayList<IDataSource>();
		
		for(IDataStreamElement c : columns.values()){
			if (!list.contains(c.getDataStream().getDataSource())){
				list.add(c.getDataStream().getDataSource());
			}
		}
		
		for(IBusinessTable t : childs){
			for(IDataSource ds : ((SQLBusinessTable)t).getDataSources()){
				if (!list.contains(ds)){
					list.add(ds);
				}
			}
		}
		
		return list;
	}


	public String getQuery(IVanillaContext vanillaCtx) throws Exception {
		String pName = "__internal_dummy_package";
		
		BusinessPackage p = new BusinessPackage();
		p.setName(pName);
		p.addBusinessTable(this);
		List<IDataStreamElement> cols = new ArrayList<IDataStreamElement>();
		cols.addAll(getColumns());
		
		getModel().addBusinessPackage(p);
		IQuery query = SqlQueryBuilder.getQuery("none", cols, new HashMap<ListOfValue, String>(), new ArrayList<AggregateFormula>(), new ArrayList<Ordonable>(), new ArrayList<IFilter>(), new ArrayList<Prompt>());
		
		try{
			String s = p.getQuery(null, vanillaCtx,query, new ArrayList<List<String>>());
			getModel().removePackage(pName);
			return s;
		}catch(Exception ex){
			ex.printStackTrace();
			getModel().removePackage(pName);
			throw new Exception("Error when generating SQL query, " + ex.getMessage(), ex);
		}
		
	}


	public String getQueryFor(List<String> keys) throws Exception{
		
		List<IDataStreamElement> columns = new ArrayList<IDataStreamElement>();
		for(String s : keys){
			
			for(IDataStreamElement e : getColumns()){
				if (e.getName().equals(s)){
					columns.add(e);
					break;
				}
			}
			
		}
		
		IQuery query = SqlQueryBuilder.getQuery(
				"none", 
				columns, 
				new HashMap<ListOfValue, String>(), 
				Collections.EMPTY_LIST, 
				Collections.EMPTY_LIST, 
				Collections.EMPTY_LIST, 
				Collections.EMPTY_LIST);

		BusinessModel m = getModel();
		BusinessPackage pack = new BusinessPackage();
		pack.setName("_dummy_for_field_browse");
		try{
			pack.addBusinessTable(this);
			m.addBusinessPackage(pack);
			
			EffectiveQuery eff = SqlQueryGenerator.getQuery(null, null, pack, (QuerySql)query, "none", false, new HashMap<Prompt, List<String>>());
			return eff.getGeneratedQuery();
		}finally{
			m.removePackage(pack.getName());
		}
		
		
		//XXX: jjust for simple BusinessTable(a unique datasource)
//		StringBuffer buf = new StringBuffer();
//		buf.append("select ");
//		
//		boolean first = true;
//		
//		//select elements
//		for(String s : keys){
//			if (first){
//				first = false;
//				
//			}
//			else{
//				buf.append(", ");
//			}
//			if (columns.get(s) instanceof ICalculatedElement){
//				buf.append(((ICalculatedElement)columns.get(s)).getFormula() + " AS " + columns.get(s).getName());
//			}
//			else{
//				if (columns.get(s).getOrigin().getName().contains(columns.get(s).getOrigin().getTable().getName() + ".")){
//					buf.append("`" + columns.get(s).getDataStream().getName() + "`" + "." + columns.get(s).getOrigin().getShortName());	
//				}
//				else{
//					buf.append(columns.get(s).getDataStream().getName() +"`" +  "." + columns.get(s).getOrigin().getShortName());
//				}
//				
//			}
//			
//		}
//		
//		//from
//		buf.append(" from ");
//		first = true;
//		List<IDataStream> involvedTables = getTables(columns.values()); 
//		
//				
//		for(IDataStream t : involvedTables){
//			if (first){
//				first = false;
//				
//			}
//			else{
//				buf.append(", ");
//			}
//			
//			buf.append(t.getOrigin().getName() + " `" + t.getName() + "`");
//
//		}
//		
//		//where
//		
//		first = true;
//		for(Relation r : getRelations(involvedTables.get(0).getDataSource(), involvedTables)){
//			if (first){
//				first = false;
//				buf.append(" where ");
//			}
//			else{
//				buf.append(" AND ");
//			}
//			buf.append(r.getWhereClause());
//		}
//		
//		
//		
//		
//		return buf.toString();
	}

	
	/**
	 * test if the BusinessTable is on an unique DataSource
	 * @return
	 */
	private boolean isTableSimple(){
		IDataSource ds = null;
		
		for(IDataStreamElement c : columns.values()){
			if (ds == null){
				ds = c.getDataStream().getDataSource();
			}
			
			if (ds != c.getDataStream().getDataSource()){
				return false;
			}
		}
		
		return true;
		
	}


	/**
	 * return tbal list of the given columns
	 * @param columns
	 * @return
	 */
	private List<IDataStream> getTables(Collection<IDataStreamElement> columns){
		List<IDataStream> result = new ArrayList<IDataStream>();
		
		for(IDataStreamElement e : columns){
			if (!result.contains(e.getDataStream())){
				result.add(e.getDataStream());
			}
		}
		return result;
	}

	
	/**
	 * return all relations for the given tables
	 * @param tables
	 * @return
	 */
	private List<Relation> getRelations(IDataSource dataSource, List<IDataStream> tables){
		List<Relation> result = new ArrayList<Relation>();
		
		for(Relation r : dataSource.getRelations()){
			if (tables.contains(r.getLeftTable()) && tables.contains(r.getRightTable()) && !result.contains(r)){
				result.add(r);
			}
		}
		
		
		return result;
	}


	public String getQueryWithLoV(HashMap<ListOfValue, String> lov) {
		//XXX: jjust for simple BusinessTable(a unique datasource)
		StringBuffer buf = new StringBuffer();
		buf.append("select ");
		
		boolean first = true;
		
		//select elements
		for(IDataStreamElement c : columns.values()){
			if (first){
				first = false;
				
			}
			else{
				buf.append(", ");
			}
			if (c instanceof ICalculatedElement){
				buf.append(((ICalculatedElement)c).getFormula());
			}
			else{
				buf.append(c.getOrigin().getTable().getName()  + "." + c.getOrigin().getName());
			}
			
		}
		
		//from
		buf.append(" from ");
		first = true;
		List<IDataStream> involvedTables = getTables(columns.values()); 
		
		//add the tables coming from lov if there arent presents
		for(ListOfValue l : lov.keySet()){
			if (!involvedTables.contains(l.getOrigin())){
				involvedTables.add(l.getOrigin().getDataStream());
			}
		}
		
		
		
		for(IDataStream t : involvedTables){
			if (first){
				first = false;
				
			}
			else{
				buf.append(", ");
			}
			
			buf.append(t.getOrigin().getName());

		}
		
		//where
		
		first = true;
		for(Relation r : getRelations(involvedTables.get(0).getDataSource(), involvedTables)){
			if (first){
				first = false;
				buf.append(" where ");
			}
			else{
				buf.append(" AND ");
			}
			buf.append(r.getWhereClause());
		}
		
		//where clause for LovS
		for(ListOfValue l : lov.keySet()){
			if (first){
				first = false;
				buf.append(" where ");
			}
			else{
				buf.append(" AND ");
			}
			
			buf.append(l.getOrigin().getOrigin().getTable().getName() + 
					"." + l.getOrigin().getOrigin().getName() + "=" + lov.get(l));
		}
		
		
		return buf.toString();
	}

//	@Override
//	public List<List<String>> executeQueryForLoV(
//			HashMap<ListOfValue, String> lov) throws Exception {
//		
//		String query = getQueryWithLoV(lov);
//		List<IDataSource> l = getDataSources();
//		
//		//XXX remove the return null once multyidatabse query is done
//		if (l.size() != 1){
//			System.out.println("more than one datasource not supported yet");
//			return null;
//		}
//		
//		List<List<String>> result = l.get(0).executeQuery(query, null);
//
//		return result;
//	}

//	@Override
//	public List<List<String>> executeFor(IDataStreamElement column) throws Exception {
//		List<String> keys = new ArrayList<String>();
//		keys.add(column.getName());
//		
//		String query = getQueryFor(keys);
//		List<IDataSource> l = getDataSources();
//		
//		//XXX remove the return null once multyidatabse query is done
//		if (l.size() != 1){
//			System.out.println("more than one datasource not supported yet");
//			return null;
//		}
//		
//		List<List<String>> result = l.get(0).executeQuery(query, null);
//
//		return result;
//	}
	
	public void addFilter(List groupNames, IFilter filter){
		for(Object o : groupNames){
			addFilter((String)o, filter);
		}
	}

	public IDataSource getDataSource() {

		List<IDataSource> l = getDataSources();
		if (l.isEmpty()){
			return null;
		}
		return getDataSources().get(0);
	}
}
