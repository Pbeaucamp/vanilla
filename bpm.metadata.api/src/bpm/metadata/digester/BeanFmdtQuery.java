package bpm.metadata.digester;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import bpm.metadata.MetaData;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataStreamElement;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.Formula;
import bpm.metadata.query.Ordonable;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.query.UnitedOlapQuery;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;

public class BeanFmdtQuery {
	private String repositoryUrl;
	private int directoryItemId = 0;
	private String businessModelName = "";
	private String businessPackageName = "";
	private List<Agg> aggs = new ArrayList<Agg>();
	private List<Select> select = new ArrayList<Select>();
	private List<String> prompt = new ArrayList<String>();
	private List<FilterBean> filters = new ArrayList<FilterBean>();
	private List<Select> orderBy = new ArrayList<Select>();
	private List<String> filterNames = new ArrayList<String>();
	private List<RelationBean> relationsBeans = new ArrayList<RelationBean>();

	private List<Formula> formulas = new ArrayList<Formula>();

	private HashMap<String, List<String>> promptDatas = new HashMap<String, List<String>>();

	private List<SqlQueryFilter> queryFilters = new ArrayList<SqlQueryFilter>();
	private List<String> additionalResourceDefinitions = new ArrayList<String>();

	private List<String> relationStrategies = new ArrayList<String>();

	private boolean distinct;
	private boolean hideNull;
	private int limit;

	public void addResourceDefinition(String s) {
		try {
			additionalResourceDefinitions.add(URLDecoder.decode(s, "UTF-8"));
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void setDistinct(String v) {
		this.distinct = Boolean.parseBoolean(v);
	}

	public void setLimit(String v) {
		this.limit = Integer.parseInt(v);
	}

	public void addPrompt(String p) {
		prompt.add(p);
	}

	public void addPromptDatas(String promptname, String value) {
		for(String s : promptDatas.keySet()) {
			if(s.equals(promptname)) {
				promptDatas.get(s).add(value);
				return;
			}
		}

		promptDatas.put(promptname, new ArrayList<String>());
		promptDatas.get(promptname).add(value);

	}

	public void addOrder(String dataStreamName, String dataStreamElementName) {

		Select s = new Select();
		s.colName = dataStreamElementName;
		s.tableName = dataStreamName;

		orderBy.add(s);
	}
	
	public void addOrderForm(String name, String formula, String dataStreamNames) {
		Select f = new Select();
		f.colName=name;
		f.tableName=dataStreamNames;
		f.aggregateName=formula;
		//Formula f = new Formula(name, formula, dataStreamNames != null ? Arrays.asList(dataStreamNames.split(",")) : null);
		orderBy.add(f);
	}

	public class RelationBean {
		String leftDataStream, rightDataStream;
		String leftDataStreamElement, rightDataStreamElement;
	}

	public class FilterBean {
		List<String> values = new ArrayList<String>();
		String dataStreamName;
		String dataStreamElement;
		String name;
	}

	public class Agg {
		String function;
		String colname;
		String tablename;
		// List<Select> groupBy = new ArrayList<Select>();
		String outputName;
		boolean isFormulaBased;

		public Agg(boolean isFormulaBased) {
			this.isFormulaBased = isFormulaBased;
		}
	}

	public void addOrder(String aggregateName) {

		Select s = new Select();
		s.aggregateName = aggregateName;
		orderBy.add(s);
	}

	public void addFilter(String filterName) {
		filterNames.add(filterName);
	}

	public void addFilter(List<String> values, String dataStreamName, String dataStreamElementName) {
		FilterBean fd = new FilterBean();
		fd.dataStreamElement = dataStreamElementName;
		fd.dataStreamName = dataStreamName;
		fd.values = values;

		filters.add(fd);
	}

	public void addFormula(String name, String formula, String dataStreamNames) {
		Formula f = new Formula(name, formula, dataStreamNames != null ? Arrays.asList(dataStreamNames.split(",")) : null);
		formulas.add(f);
	}

	public String getBusinessModelName() {
		return businessModelName;
	}

	public void setBusinessModelName(String businessModelName) {
		this.businessModelName = businessModelName;
	}

	public String getBusinessPackageName() {
		return businessPackageName;
	}

	public void setBusinessPackageName(String businessPackageName) {
		this.businessPackageName = businessPackageName;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public List<Agg> getAggs() {
		return aggs;
	}

	public int getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(String id) {
		this.directoryItemId = Integer.parseInt(id);
	}

	public void addSelect(String colName, String tableName) {
		Select s = new Select();
		s.colName = colName;
		s.tableName = tableName;
		select.add(s);
	}

	public void addSelectFromBusinessTable(String colName, String tableName) {
		BusinessTableSelect s = new BusinessTableSelect();
		s.colName = colName;
		s.tableName = tableName;
		select.add(s);
	}

	public void addAgg(String function, String colName, String tableName/* , List groupBy */, String outputName, String formula) {
		Agg s = new Agg(Boolean.valueOf(formula));
		s.colname = colName;
		s.tablename = tableName;
		s.function = function;
		// if(groupBy == null) {
		// groupBy = new ArrayList<Select>();
		// }
		// s.groupBy = groupBy;
		s.outputName = outputName;
		aggs.add(s);
	}

	public List<Select> getSelect() {
		return select;
	}

	private IDataStreamElement lookInsideBusinessTable(IBusinessTable t, String tableName, String colName, String groupName) {
		if("Periode".equals(colName.replace(" ", ""))) {
		}
		for(IDataStreamElement e : t.getColumns(groupName)) {
			if(e.getDataStream().getName().equals(tableName.replace(" ", "")) && e.getName().equals(colName.replace(" ", ""))) {

				return e;
			}
		}

		for(IBusinessTable child : t.getChilds(groupName)) {
			IDataStreamElement o = lookInsideBusinessTable(child, tableName.replace(" ", ""), colName.replace(" ", ""), groupName);
			if(o != null) {
				return o;
			}
		}

		return null;

	}

	public QuerySql getQuerySql(String groupName, IBusinessPackage pack) throws Exception {
		List<IDataStreamElement> select = new ArrayList<IDataStreamElement>();
		List<AggregateFormula> agg = new ArrayList<AggregateFormula>();
		List<Prompt> prompts = new ArrayList<Prompt>();

		for(String s : prompt) {
			IResource r = pack.getResourceByName(s);
			if(s != null && r != null) {

				prompts.add((Prompt) r);

			}
		}

		for(Select s : this.select) {
			boolean found = false;
			if(s instanceof BusinessTableSelect) {
				IBusinessTable t = pack.getBusinessTable(groupName, s.tableName);
				if(t == null) {
					// look recursively in all businessTables

					for(IBusinessTable _t : pack.getBusinessTables(groupName)) {
						t = lookForBusinessTableNamed(_t, groupName, s.tableName);
						if(t != null) {
							break;
						}
					}
					if(t == null) {
						Logger.getLogger(getClass()).warn("Could not find BusinessTable " + s.tableName + " for BusinessTableSelect");
					}

				}
				/*
				 * we cannot look within the businessTables because we dont know the physical name of the dataStream
				 */
				IDataStreamElement e = t.getColumn(groupName, s.colName);
				if(e == null) {
					e = t.getColumn(groupName, t.getName() + "." + s.colName);
				}
				if(e != null) {
					select.add(e);
					found = true;
					continue;
				}

			}

			for(IBusinessTable t : pack.getBusinessTables(groupName)) {

				IDataStreamElement e = lookInsideBusinessTable(t, s.tableName, s.colName, groupName);

				if(e != null) {
					select.add(e);
					found = true;
				}
				int i = 0;
				if(found) {
					break;
				}
			}
		}

		for(Agg s : this.aggs) {

			/**
			 * Ere, correct bug with sub-business tables while looking for aggregates originally corrected in minint branch
			 */
//			if(s.isFormulaBased) {
				AggregateFormula f = null;
						
//			}
//			else {
				for(IBusinessTable t : pack.getBusinessTables(groupName)) {

					IDataStreamElement dsElement = lookInsideBusinessTable(t, s.tablename, s.colname, groupName);

					if(dsElement != null) {
						List<IDataStreamElement> list = new ArrayList<IDataStreamElement>();
						f = new AggregateFormula(s.function, dsElement/* , list */, s.outputName);
						agg.add(f);
						break;
					}
				}
				if(f == null) {
					f = new AggregateFormula(s.function, new Formula(s.outputName, s.colname, Arrays.asList(s.tablename.split(",")))/* , list */, s.outputName);
					agg.add(f);
				}
//			}

		}

		List<IFilter> realFilters = new ArrayList<IFilter>();
		for(String s : filterNames) {
			IResource r = pack.getResourceByName(s);
			if(s != null) {

				realFilters.add((IFilter) r);

			}
		}
		for(FilterBean f : filters) {
			boolean found = false;
			for(IBusinessTable t : pack.getBusinessTables(groupName)) {

				/**
				 * ere corrects potential problem if sub-business tables are present
				 */

				IDataStreamElement dsElement = lookInsideBusinessTable(t, f.dataStreamName, f.dataStreamElement, groupName);

				if(dsElement != null) {
					Filter _f = new Filter();
					_f.setName(f.name);
					_f.setOrigin(dsElement);

					_f.setGranted(groupName, true);

					for(String s : f.values) {
						_f.addValue(s);
					}

					realFilters.add(_f);
					found = true;
					break;
				}

				if(found) {
					break;
				}

			}
		}

		// add filter from relationClauses
		for(RelationBean b : relationsBeans) {

			IDataStreamElement rightCol = null, leftCol = null;

			for(IBusinessTable t : pack.getBusinessTables(groupName)) {
				if(rightCol == null) {
					rightCol = lookInsideBusinessTable(t, b.rightDataStream, b.rightDataStreamElement, groupName);
				}
				if(leftCol == null) {
					leftCol = lookInsideBusinessTable(t, b.leftDataStream, b.leftDataStreamElement, groupName);
				}
			}

			if(rightCol != null && leftCol != null) {
				SqlQueryFilter f = new SqlQueryFilter();
				f.setOrigin(rightCol);

				StringBuilder buf = new StringBuilder();
				buf.append("=");
				if(!(leftCol instanceof ICalculatedElement)) {
					//TODO: Try to change this for postgres. Need to check that it works for others
//					buf.append(leftCol.getOrigin().getTable().getName() + "." + leftCol.getOrigin().getShortName());
					buf.append("`" + leftCol.getDataStream().getName() + "`." + leftCol.getOrigin().getShortName());
					
					//This comes from 6.6 - We should test it
//					if (leftCol instanceof IDataStreamElement) {
//						buf.append(((IDataStreamElement) leftCol).getDataStream().getName() + "." + leftCol.getOrigin().getShortName());
//					}
//					else {
//						buf.append(leftCol.getOrigin().getTable().getName() + "." + leftCol.getOrigin().getShortName());
//					}
				}
				else {

					buf.append(((ICalculatedElement) leftCol).getFormula());
				}

				f.setQuery(buf.toString());
				queryFilters.add(f);
			}
		}

		List<Ordonable> order = new ArrayList<Ordonable>();

		for(Select s : this.orderBy) {
			boolean found = false;
			if(s.colName!=null && s.aggregateName!=null && s.tableName!=null){
			//	Formula fo = new Formula(s.colName, s.aggregateName, s.tableName != null ? Arrays.asList(s.tableName.split(",")) : null);
			//	order.add(fo);
				for (Formula f : formulas){
					if(f.getName().equals(s.colName)&& f.getFormula().equals(s.aggregateName)){
						order.add(f);
						found=true;
						break;
					}
				}	
				if (!found){
					for(AggregateFormula a : agg) {
						if(a.getOutputName().equals(s.colName) && ((ICalculatedElement)a.getCol()).getFormula().equals(s.aggregateName)) {
							order.add(a);
							found = true;
							break;
						}
					}
				}
			}else 
			{
				if(s.aggregateName == null) {
					for(IBusinessTable t : pack.getBusinessTables(groupName)) {
	
						try {
							IDataStreamElement e = lookInsideBusinessTable(t, s.tableName, s.colName, groupName);
							
							IDataStreamElement used = e;
							
//							for(Agg ag : this.aggs) {
//								if(e.getName().equals(ag.colname)) {
//									System.out.println("found");
//									used = new SQLDataStreamElement();
//									used.setBackgroundColor(e.getBackgroundColor());
//									used.setDataStream(e.getDataStream());
//									used.setName(e.getName());
//									used.setOrigin(e.getOrigin());
//									used.setType(e.getType());
//									used.setOutputName(Locale.FRANCE, ag.outputName);
//									used.setOutputName(Locale.FRENCH, ag.outputName);
//									used.setOutputName(Locale.ENGLISH, ag.outputName);
//									used.setOutputName(Locale.US, ag.outputName);
//
//									for (String ss : e.getVisibility().keySet()) {
//										used.setVisible(ss, e.getVisibility().get(ss));
//									}
//
//									for (String ss : e.getGrants().keySet()) {
//										used.setGranted(ss, e.getGrants().get(ss));
//									}
//									break;
//								}


								
//							}
							
							
							if(used != null) {
								order.add(used);
								found = true;
							}
	
							if(found) {
								break;
							}
						} catch(Exception ex) {}
	
					}
				}
				else {
					for(AggregateFormula a : agg) {
						if(a.getOutputName().equals(s.aggregateName)) {
	
							order.add(a);
							found = true;
							break;
						}
					}
				}
	
			}
		}
		for(SqlQueryFilter f : queryFilters) {
			for(IBusinessTable tabl : pack.getBusinessTables(groupName)) {
				IDataStreamElement elem = lookInsideBusinessTable(tabl, f.getDataStreamName(), f.getDataStreamElementName(), groupName);
				if(elem != null) {
					f.setOrigin(elem);
					realFilters.add(f);
					break;
				}
			}
		}
		
		List<RelationStrategy> strategies = new ArrayList<RelationStrategy>();
		for(String strat : relationStrategies) {
			RelationStrategy st = pack.getBusinessModel().getRelationStrategy(strat);
			strategies.add(st);
		}
		
		pack.getBusinessModel().getRelationStrategies();

		QuerySql query = (QuerySql) SqlQueryBuilder.getQuery(groupName, select, null, agg, order, realFilters, prompts, formulas, strategies);
		query.setDistinct(distinct);
		query.setLimit(limit);

		// rebuild additionalResources
		StringBuffer buf = new StringBuffer();
		buf.append("<freeMetaData>");
		for(String s : additionalResourceDefinitions) {
			buf.append(s);
		}
		buf.append("</freeMetaData>");

		MetaDataDigester dig = new MetaDataDigester(new ByteArrayInputStream(buf.toString().getBytes("UTF-8")), null);
		MetaData model = dig.getModel(null, "none");

		for(IResource r : model.getResources()) {
			if(r instanceof SqlQueryFilter) {
				for(IBusinessTable tabl : pack.getBusinessTables(groupName)) {
					IDataStreamElement elem = lookInsideBusinessTable(tabl, ((SqlQueryFilter) r).getDataStreamName(), ((SqlQueryFilter) r).getDataStreamElementName(), groupName);

					if(elem != null) {
						((SqlQueryFilter) r).setOrigin(elem);
						query.addDesignTimeResource(r);
						break;
					}
				}
			}
			else if(r instanceof Prompt) {
				for(IBusinessTable tabl : pack.getBusinessTables(groupName)) {
					IDataStreamElement elem = lookInsideBusinessTable(tabl, ((Prompt) r).getGotoDataStreamName(), ((Prompt) r).getGotoDataStreamElementName(), groupName);

					if(elem != null) {
						((Prompt) r).setOrigin(elem);
						((Prompt) r).setGotoDataStreamElement(elem);
						query.addDesignTimeResource(r);
						break;
					}
				}
			}
			else if(r instanceof ComplexFilter) {
				for(IBusinessTable tabl : pack.getBusinessTables(groupName)) {
					IDataStreamElement elem = lookInsideBusinessTable(tabl, ((ComplexFilter) r).getDataStreamName(), ((ComplexFilter) r).getDataStreamElementName(), groupName);

					if(elem != null) {
						((ComplexFilter) r).setOrigin(elem);
						query.addDesignTimeResource(r);
						break;
					}
				}
			}
		}
		return query;
	}

	private IBusinessTable lookForBusinessTableNamed(IBusinessTable table, String groupName, String tableName) {
		if(table.getName().equals(tableName)) {
			return table;
		}

		IBusinessTable result = null;
		for(IBusinessTable t : table.getChilds(groupName)) {
			result = lookForBusinessTableNamed(t, groupName, tableName);
			if(result != null) {
				return result;
			}
		}
		return result;
	}

	public void addSqlQueryFilter(String query, String dataStreamName, String dataStreamElementName) {
		SqlQueryFilter filter = new SqlQueryFilter();
		filter.setDataStreamElementName(dataStreamElementName);
		filter.setDataStreamName(dataStreamName);
		filter.setQuery(query);

		queryFilters.add(filter);
	}

	public void addRelation(String leftDataStreamName, String leftDataStreamElement, String rightDataStreamName, String rightDataStreamElement) {
		RelationBean r = new RelationBean();
		r.leftDataStream = leftDataStreamName;
		r.rightDataStream = rightDataStreamName;
		r.leftDataStreamElement = leftDataStreamElement;
		r.rightDataStreamElement = rightDataStreamElement;
		relationsBeans.add(r);
	}

	/**
	 * Return a UnitedOlapQuery
	 * 
	 * @param groupName
	 * @param pack
	 * @return
	 * @throws Exception
	 */
	public UnitedOlapQuery getUnitedOlapQuery(String groupName, IBusinessPackage pack) throws Exception {
		List<IDataStreamElement> select = new ArrayList<IDataStreamElement>();
		List<AggregateFormula> agg = new ArrayList<AggregateFormula>();
		List<Prompt> prompts = new ArrayList<Prompt>();

		for(String s : prompt) {
			IResource r = pack.getResourceByName(s);
			if(s != null && r != null) {
				prompts.add((Prompt) r);
			}
		}

		for(Select s : this.select) {
			boolean found = false;

			for(IBusinessTable t : pack.getBusinessTables(groupName)) {

				IDataStreamElement e = lookInsideBusinessTable(t, s.tableName, s.colName, groupName);

				if(e != null) {
					select.add(e);
					found = true;
				}
				if(found) {
					break;
				}
			}
		}

		List<IFilter> realFilters = new ArrayList<IFilter>();
		for(String s : filterNames) {
			IResource r = pack.getResourceByName(s);
			if(s != null) {
				realFilters.add((IFilter) r);
			}
		}

		for(FilterBean f : filters) {
			boolean found = false;
			for(IBusinessTable t : pack.getBusinessTables(groupName)) {

				/**
				 * ere corrects potential problem if sub-business tables are present
				 */

				IDataStreamElement dsElement = lookInsideBusinessTable(t, f.dataStreamName, f.dataStreamElement, groupName);

				if(dsElement != null) {
					Filter _f = new Filter();
					_f.setName(f.name);
					_f.setOrigin(dsElement);

					_f.setGranted(groupName, true);

					for(String s : f.values) {
						_f.addValue(s);
					}

					realFilters.add(_f);
					found = true;
					break;
				}
				if(found) {
					break;
				}
			}
		}

		return new UnitedOlapQuery(select, realFilters, prompts, groupName, hideNull);
	}

	public void setHideNull(boolean hideNull) {
		this.hideNull = hideNull;
	}

	public boolean isHideNull() {
		return hideNull;
	}

	public void setHideNull(String hideNull) {
		this.hideNull = Boolean.parseBoolean(hideNull);
	}

	public void setRelationStrategies(List<String> relationStrategies) {
		this.relationStrategies = relationStrategies;
	}

	public List<String> getRelationStrategies() {
		return relationStrategies;
	}
	
	public void addRelationStrategy(String relationStrategy) {
		relationStrategies.add(relationStrategy);
	}
}
