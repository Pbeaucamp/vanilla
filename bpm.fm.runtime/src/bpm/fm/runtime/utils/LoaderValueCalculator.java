package bpm.fm.runtime.utils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fm.api.model.utils.LoaderMetricValue;
import bpm.fm.runtime.FreeMetricsManagerComponent;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.Formula;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.Ordonable;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.query.SqlQueryGenerator;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.tools.MetadataLoader;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;

public class LoaderValueCalculator extends ValueCalculator {

	public LoaderValueCalculator(FreeMetricsManagerComponent component) {
		super(component);
	}
	
	public LoaderDataContainer getLoaderData(Metric metric, Date date) throws Exception {
		return getLoaderData(metric, date, null);
	}
	
	public LoaderDataContainer getLoaderData(Metric metric, Date firstDate, Date endDate) throws Exception {
		
		LoaderDataContainer container = new LoaderDataContainer();
		container.setMetric(metric);
		
		//for each axis get axisInfo and levelMembers
		for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
			AxisInfo axisInfo = generateAxisInfo(factAxis.getAxis());
			container.addAxisInfo(axisInfo);
		}
		
		//get the existing metric values
		List<LoaderMetricValue> values = generateValues(metric, firstDate, endDate, container.getAxisInfos());
		container.setValues(values);
		
		return container;
	}
	
	public List<LoaderDataContainer> getLoaderDataForAxes(List<Metric> metrics, List<Axis> axes, Date startDate, Date endDate) throws Exception {
		
		List<LoaderDataContainer> results = new ArrayList<>();
		
		for(Metric metric : metrics) {
			List<FactTableAxis> toRemove = new ArrayList<>();
			LoaderDataContainer container = new LoaderDataContainer();
			container.setMetric(metric);
			for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				if(!axes.contains(factAxis.getAxis())) {
					toRemove.add(factAxis);
				}
				else {
					AxisInfo axisInfo = generateAxisInfo(factAxis.getAxis());
					container.addAxisInfo(axisInfo);
				}
			}
			((FactTable)metric.getFactTable()).getFactTableAxis().removeAll(toRemove);
			
			List<LoaderMetricValue> values = generateValues(metric, startDate, endDate, container.getAxisInfos());
			container.setValues(values);
			results.add(container);
		}
		
		return results;
	}

	private List<LoaderMetricValue> generateValues(Metric metric, Date date, Date endDate,List<AxisInfo> axisInfos) throws Exception {
		
		Datasource ds = ((FactTable)metric.getFactTable()).getDatasource();
		
		
		valueTableName = ((FactTable)metric.getFactTable()).getTableName();
		String valueColumn = ((FactTable)metric.getFactTable()).getValueColumn();
		String valueDateColumn = ((FactTable)metric.getFactTable()).getDateColumn();
		
		String periodicity = ((FactTable)metric.getFactTable()).getPeriodicity();
		String aggregator = metric.getOperator();
		
		StringBuffer buf = new StringBuffer();
		
		int indexValue = 1;
		int indexDate = 2;
		
		int indexObjectif = -1;
		int indexMaximum = -1;
		int indexMinimum = -1;
		
		int lastIndex = 2;
		String query = null;
		IBusinessPackage pack = null;
		
		HashMap<FactTableAxis, Integer> axisIndexes = new HashMap<>();
		
		if(ds.getObject() instanceof DatasourceJdbc) {
			DatasourceJdbc dsJdbc = (DatasourceJdbc) ds.getObject();
			
			//!---------------------------------------------------------------------------------------
			//Select part
			//!---------------------------------------------------------------------------------------
			//metric value part
			buf.append("Select \n");
			buf.append("	" + getOperatorQueryPart(aggregator) + "(" + valueTableName + "." + valueColumn + "),\n");
			buf.append("	" + valueTableName + "." + valueDateColumn + "\n");
			
			//metric objectives if needed
			boolean hasObjectives = false;
			if(((FactTable)metric.getFactTable()).getObjectives().getTableName() != null && !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty()) {
				objectiveTableName = ((FactTable)metric.getFactTable()).getObjectives().getTableName();
				buf.append("	," + getOperatorQueryPart(aggregator) + "(" + ((FactTable)metric.getFactTable()).getObjectives().getTableName()  + "." + ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn()  + "),\n");
				indexObjectif = 3;
				buf.append("	" + getOperatorQueryPart(aggregator) + "(" + ((FactTable)metric.getFactTable()).getObjectives().getTableName()  + "." + ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn()  + "),\n");
				indexMaximum = 4;
				buf.append("	" + getOperatorQueryPart(aggregator) + "(" + ((FactTable)metric.getFactTable()).getObjectives().getTableName()  + "." + ((FactTable)metric.getFactTable()).getObjectives().getMinColumn()  + ")\n");
				indexMinimum = 5;
				
				lastIndex = 5;
				
				hasObjectives = true;
			}
			
			
			
			//axis keys
			for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				Level lastLevel = axis.getAxis().getChildren().get(axis.getAxis().getChildren().size()-1);
				
				buf.append("	," + lastLevel.getTableName()  + "." + lastLevel.getColumnId()  + "\n");
				lastIndex++;
				axisIndexes.put(axis, lastIndex);
			}
			
			//!---------------------------------------------------------------------------------------
			//From part
			//!---------------------------------------------------------------------------------------
			//Add the metric table
			buf.append("From \n");
			buf.append("	" + ((FactTable)metric.getFactTable()).getTableName() + " " + valueTableName + " \n");
			
			//look if the other tables are different, if yes, add them
			if(hasObjectives) {
				if(!((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(valueTableName)) {
					buf.append("	," + ((FactTable)metric.getFactTable()).getObjectives().getTableName() + "\n");
				}
			}
			
			for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				Level lastLevel = axis.getAxis().getChildren().get(axis.getAxis().getChildren().size()-1);
				if(!lastLevel.getTableName().equals(valueTableName) && !lastLevel.getTableName().equals(((FactTable)metric.getFactTable()).getObjectives().getTableName())) {
					buf.append("	," + lastLevel.getTableName() + "\n");
				}
			}
			
			//!---------------------------------------------------------------------------------------
			//Where part
			//!---------------------------------------------------------------------------------------
			
			//relation between tables
			buf.append("Where \n");
			
			//link with objectives
			boolean needToLinkObjective = false;
			if(hasObjectives) {
				if(!((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(valueTableName)) {
					//link the fact and objectives
					needToLinkObjective = true;
					if(((DatasourceJdbc)ds.getObject()).getDriver().equals("com.mysql.jdbc.Driver")) {
						buf.append("	cast(" + valueTableName + "." + valueDateColumn + " as char(50)) = cast(" + ((FactTable)metric.getFactTable()).getObjectives().getTableName() + "." + ((FactTable)metric.getFactTable()).getObjectives().getDateColumn() + " as char(50)) AND \n");
					}
					else {
						buf.append("	cast(" + valueTableName + "." + valueDateColumn + " as varchar(50)) = cast(" + ((FactTable)metric.getFactTable()).getObjectives().getTableName() + "." + ((FactTable)metric.getFactTable()).getObjectives().getDateColumn() + " as varchar(50)) AND \n");
					}
				}
			}
			//link with axis
			boolean first = true;
			for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				if(first) {
					first = false;
				}
				else {
					buf.append("	AND ");
				}
				Level lastLevel = factAxis.getAxis().getChildren().get(factAxis.getAxis().getChildren().size() - 1);

				if(((DatasourceJdbc)ds.getObject()).getDriver().equals("com.mysql.jdbc.Driver")) {
					buf.append("	cast(" + valueTableName + "." + factAxis.getColumnId() + " as char(50)) = cast(" + lastLevel.getTableName() + "." + lastLevel.getColumnId() + " as char(50))\n");
					if(needToLinkObjective) {
						buf.append("	AND cast(" + ((FactTable)metric.getFactTable()).getObjectives().getTableName() + "." + factAxis.getObjectiveColumnId() + " as char(50)) = cast(" + lastLevel.getTableName() + "." + lastLevel.getColumnId() + " as char(50))\n");
					}
				}
				else {
					buf.append("	cast(" + valueTableName + "." + factAxis.getColumnId() + " as varchar(50)) = cast(" + lastLevel.getTableName() + "." + lastLevel.getColumnId() + " as varchar(50))\n");
					if(needToLinkObjective) {
						buf.append("	AND cast(" + ((FactTable)metric.getFactTable()).getObjectives().getTableName() + "." + factAxis.getObjectiveColumnId() + " as varchar(50)) = cast(" + lastLevel.getTableName() + "." + lastLevel.getColumnId() + " as varchar(50))\n");
					}
				}
			}
			
			//where conditions
			//date condition
			buf.append(" AND ");
			NonStandardSgbdHandler.generateDateFilter(periodicity, date, endDate, valueDateColumn, buf, false, dsJdbc.getDriver(), this, true);
			
			//!---------------------------------------------------------------------------------------
			//Group by part
			//!---------------------------------------------------------------------------------------
			buf.append("Group by \n");
			buf.append("	" + valueTableName + "." + valueDateColumn + "\n");
			for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				Level lastLevel = axis.getAxis().getChildren().get(axis.getAxis().getChildren().size()-1);
				
				buf.append("	," + lastLevel.getTableName()  + "." + lastLevel.getColumnId()  + "\n");
			}
			
			//!---------------------------------------------------------------------------------------
			//Execute Query
			//!---------------------------------------------------------------------------------------
			
			query = buf.toString();
			
		}
		else {
			//XXX
			indexValue = 2;
			indexDate = 1;
			lastIndex = 1;
			int valCols = 1;
			DatasourceFmdt dsFmdt = (DatasourceFmdt) ds.getObject();
			
			pack = MetadataLoader.loadMetadata(dsFmdt);
			
			//generate date filters
			FmdtValueCalculator calc = new FmdtValueCalculator(component);
			calc.findColumnsInPackage(pack, metric, null);
			calc.createDateColumnsAndFilters(date, endDate, metric, false);
			List<IFilter> filters = calc.filterDates;
			
			//add column to select
			List<AggregateFormula> aggs = new ArrayList<>();
			AggregateFormula valueAgg = new AggregateFormula(getOperatorQueryPart(metric.getOperator()).toUpperCase(), calc.colValue, ALIAS_VALUE);
			aggs.add(valueAgg);
			
			if(calc.colObj != null) {
				AggregateFormula objAgg = new AggregateFormula(getOperatorQueryPart(metric.getOperator()).toUpperCase(), calc.colObj, ALIAS_OBJECTIVE);
				aggs.add(objAgg);
				indexObjectif = 3;
				valCols++;
			}
			if(calc.colMin != null) {
				AggregateFormula objAgg = new AggregateFormula(getOperatorQueryPart(metric.getOperator()).toUpperCase(), calc.colMin, ALIAS_MIN);
				aggs.add(objAgg);
				indexMinimum = 4;
				valCols++;
			}
			if(calc.colMax != null) {
				AggregateFormula objAgg = new AggregateFormula(getOperatorQueryPart(metric.getOperator()).toUpperCase(), calc.colMax, ALIAS_MAX);
				aggs.add(objAgg);
				indexMaximum = 5;
				valCols++;
			}
			
			List<IDataStreamElement> select = new ArrayList<>();
			List<Ordonable> orderBy = new ArrayList<>();
			
			select.add(calc.colDate);
			orderBy.add(calc.colDate);
			
			//levels
			for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				Level lastLevel = axis.getAxis().getChildren().get(axis.getAxis().getChildren().size()-1);
				
				IDataStreamElement label = null;
				LOOP:for (IBusinessTable table : pack.getBusinessTables("none")) {
					for (IDataStreamElement elem : table.getColumns("none")) {
						if(elem.getName().equals(lastLevel.getColumnId())) {
							label = elem;
							break LOOP;
						}
					}
				}
				
				//XXX
				
				lastIndex++;
				axisIndexes.put(axis, lastIndex);
				select.add(label);
				orderBy.add(label);
			}
			
			indexValue = lastIndex + 1;
			if(valCols > 1) {
				indexObjectif = lastIndex + 2;
				indexMinimum = lastIndex + 3;
				indexMaximum = lastIndex + 4;
			}
			
			IQuery fmdtQuery = SqlQueryBuilder.getQuery(
					"none", 
					select, 
					new HashMap<ListOfValue, String>(), 
					aggs,
					orderBy, 
					filters, 
					new ArrayList<Prompt>(), 
					new ArrayList<Formula>(), 
					new ArrayList<RelationStrategy>());
			
			EffectiveQuery effQuery = SqlQueryGenerator.getQuery(null, null, pack, (QuerySql)fmdtQuery, "none", false, new HashMap<Prompt, List<String>>());
			
			query = effQuery.getGeneratedQuery().replace("`", "\"");
			
			
			//XXX
		}
		
		System.out.println(query);
		
		List<LoaderMetricValue> values = new ArrayList<>();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if(ds.getObject() instanceof DatasourceJdbc) {
				connection = ConnectionManager.getInstance().getJdbcConnection((DatasourceJdbc)ds.getObject());
			}
			else {
				DatasourceFmdt dsFmdt = (DatasourceFmdt) ds.getObject();
				
				connection = ((SQLConnection)pack.getConnection("none", "Default")).getJdbcConnection();
			}
			
			stmt = connection.prepareQuery(query);
			
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				
				//get the value
				LoaderMetricValue val = new LoaderMetricValue();
				
				double value = rs.getDouble(indexValue);
				Date valueDate = rs.getDate(indexDate);
				val.setValue(value);
				val.setDate(valueDate);
				val.setMetric(metric);
				
				//look for objectives
				if(indexObjectif > -1) {
					double obj = rs.getDouble(indexObjectif);
					double min = rs.getDouble(indexMinimum);
					double max = rs.getDouble(indexMaximum);
					val.setMaximum(max);
					val.setMinimum(min);
					val.setObjective(obj);
				}
				
				//look for members
				for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
					int index = axisIndexes.get(axis);
					String id = rs.getString(index);
					
					for(AxisInfo info : axisInfos) {
						if(info.getAxis().equals(axis.getAxis())) {
							LevelMember member = info.getMemberKeys().get(id);
							val.addMember(member);
							break;
						}
					}
					
				}
				values.add(val);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An error occured while executing the query : " + query, e);
		} finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(connection != null) {
				ConnectionManager.getInstance().returnJdbcConnection(connection);
			}
		}

		
		return values;
	}
	
	protected void generateDateFilter(String periodicity, Date startDate, Date endDate, String valueDateColumn, StringBuffer buf, boolean getPrevious) {
		switch (periodicity) {
		case FactTable.PERIODICITY_YEARLY:
			if (getPrevious) {
				buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'%Y') <= " + (startDate.getYear() + 1900) + " and \n");
				buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'%Y') >= " + (startDate.getYear() + 1900 - 12) + "\n");
			}
			else {
				if(endDate != null) {
					String start = dateFormat.format(startDate);
					String end = dateFormat.format(endDate);
					
					String sqlDateFormat = "%Y-%m-%d";
					
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'" + sqlDateFormat + "') <= '" + end + "' and \n");		
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'" + sqlDateFormat + "') >= '" + start + "' \n");		
					
				}
				else {
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + "\n");
				}
			}
			
			break;
		case FactTable.PERIODICITY_MONTHLY:
		case FactTable.PERIODICITY_BIANNUAL:
		case FactTable.PERIODICITY_QUARTERLY:
			if (getPrevious) {
				String stringDate = dateFormat.format(startDate);
				buf.append("	" + valueTableName + "." + valueDateColumn + " <= '" + stringDate + "' and \n");
				buf.append("	" + valueTableName + "." + valueDateColumn + " >= date_sub('" + stringDate + "', INTERVAL 12 MONTH)\n");
				
			}
			else {
				if(endDate != null) {
					String start = dateFormat.format(startDate);
					String end = dateFormat.format(endDate);
					
					String sqlDateFormat = "%Y-%m-%d";
					
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'" + sqlDateFormat + "') <= '" + end + "' and \n");		
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'" + sqlDateFormat + "') >= '" + start + "' \n");		
					
				}
				else {
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + " and \n");
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1) + "\n");
				}
			}

			break;
		case FactTable.PERIODICITY_DAILY:
		case FactTable.PERIODICITY_WEEKLY:
			if (getPrevious) {
				String stringDate = dateFormat.format(startDate);
				buf.append("	" + valueTableName + "." + valueDateColumn + " <= '" + stringDate + "' and \n");
				buf.append("	" + valueTableName + "." + valueDateColumn + " >= date_sub('" + stringDate + "', INTERVAL 12 DAY)\n");
			}
			else {
				if(endDate != null) {
					String start = dateFormat.format(startDate);
					String end = dateFormat.format(endDate);
					
					String sqlDateFormat = "%Y-%m-%d";
					
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'" + sqlDateFormat + "') <= '" + end + "' and \n");		
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'" + sqlDateFormat + "') >= '" + start + "' \n");		
					
				}
				else {
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'%Y') = " + (startDate.getYear() + 1900) + " and \n");
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'%m') = " + (startDate.getMonth() + 1) + " and \n");
					buf.append("	date_format(" + valueTableName + "." + valueDateColumn + ",'%d') = " + startDate.getDate() + "\n");
				}
			}

			break;

		default:
			break;
		}
	}
	
	public List<LevelMember> getLevelValues(Level level) throws Exception {
		String query = createLevelQuery(level, true);
		//execute query
		List<LevelMember> result = new ArrayList<LevelMember>();
		
		Datasource ds = level.getDatasource();
		DatasourceJdbc dsJdbc = (DatasourceJdbc) ds.getObject();
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(dsJdbc);
			
			stmt = connection.prepareQuery(query);
			
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				String id = rs.getString(1);
				String label = rs.getString(2);
				
				//create the levelMember
				LevelMember member = null;
				member = new LevelMember();
				member.setLabel(label);
				member.setValue(id);
				
				member.setLevel(level);
				
				result.add(member);
				
			}
		} finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(connection != null) {
				ConnectionManager.getInstance().returnJdbcConnection(connection);
			}
		}
		return result;
	}

	public AxisInfo generateAxisInfo(Axis factAxis) throws Exception {
		
		AxisInfo info = new AxisInfo();
		info.setAxis(factAxis);
		
		//we execute one query per level
		//if the levels are in the same table, we need to do a distinct query on the parent levels
		
		List<LevelMember> previousMembers = new ArrayList<>();
		
		for(Level lvl : factAxis.getChildren()) {
			
			//if its the last level, we need to create the key map
			boolean isLast = false;
			boolean needDistinct = false;
			if(factAxis.getChildren().indexOf(lvl) == factAxis.getChildren().size() - 1) {
				isLast = true;
			}
			else {
				//look if the query need a distinct
				if(lvl.getTableName().equals(factAxis.getChildren().get(factAxis.getChildren().indexOf(lvl)+1).getTableName())) {
					needDistinct = true;
				}
			}
			
			
			
			//execute query
			Datasource ds = lvl.getDatasource();
			ResultSet rs = null;
			VanillaJdbcConnection connection = null;
			VanillaPreparedStatement stmt = null;
			try {
				
				if(ds.getObject() instanceof DatasourceJdbc) {
					DatasourceJdbc dsJdbc = (DatasourceJdbc) ds.getObject();
					
					connection = ConnectionManager.getInstance().getJdbcConnection(dsJdbc);
					//generate the query
					String query = createLevelQuery(lvl, false);
					stmt = connection.prepareQuery(query);
					
					rs = stmt.executeQuery();
				}
				else if(ds.getObject() instanceof DatasourceFmdt) {
					DatasourceFmdt dsFmdt = (DatasourceFmdt) ds.getObject();
					
					IBusinessPackage pack = MetadataLoader.loadMetadata(dsFmdt);
					
					connection = ((SQLConnection)pack.getConnection("none", "Default")).getJdbcConnection();
					//generate the query
					QuerySql query = createFMDTLevelQuery(lvl, false, pack);
					EffectiveQuery effQuery = SqlQueryGenerator.getQuery(null, null, pack, query, "none", false, new HashMap<Prompt, List<String>>());
					
					String queryToExecute = effQuery.getGeneratedQuery().replace("`", "\"");
					
					stmt = connection.prepareQuery(queryToExecute);
					
					rs = stmt.executeQuery();
				}
				
				HashMap<String, LevelMember> members = new HashMap<>();
				
				while(rs.next()) {
					String id = rs.getString(1);
					String label = rs.getString(2);
					String parentid = "";
					try {
						parentid = rs.getString(3);
					} catch(Exception e) {
						
					}
					
					//create the levelMember
					LevelMember member = null;
					
					boolean add = false;
					
					if(needDistinct) {
						member = members.get(label);
						if(member == null) {
							member = new LevelMember();
							member.setLabel(label);
							member.setValue(id);
							add = true;
						}
						member.addKey(id);
						members.put(label, member);
					}
					else {
						member = new LevelMember();
						member.setLabel(label);
						member.setValue(id);
						members.put(id, member);
						add = true;
					}
					
					member.setLevel(lvl);
					
					//find the parent
					//if no parent, add as root
					if(previousMembers == null || previousMembers.isEmpty()) {
						if(add) {
							info.addMember(member);
						}
					}
					else {
						//if in the same table as the previous members
						if(add) {
							if(lvl.getTableName().equals(factAxis.getChildren().get(factAxis.getChildren().indexOf(lvl)-1).getTableName())) {
								for(LevelMember parent : previousMembers) {
									if(parent.getKeys().contains(id)) {
										parent.addChild(member);
										break;
									}
								}
							}
							
							//if in different tables than previous members
							else {
								String parentId = rs.getString(3);
								for(LevelMember parent : previousMembers) {
									if(parent.getValue().equals(parentId)) {
										parent.addChild(member);
										break;
									}
								}
							}
						}
					}
					
					if(isLast) {
						info.addMemberKey(id, member);
					}

				}
				if(!isLast) {
					previousMembers = new ArrayList<>(members.values());
				}
				
			} catch (Exception e) {
				e.printStackTrace();
//				throw new Exception("An error occured while executing the query : " + query, e);
			} finally {
				if(rs != null) {
					rs.close();
				}
				if(stmt != null) {
					stmt.close();
				}
				if(connection != null) {
					ConnectionManager.getInstance().returnJdbcConnection(connection);
				}
			}
			
		}
		
		return info;
	}

	private QuerySql createFMDTLevelQuery(Level lvl, boolean b, IBusinessPackage pack) {
		QuerySql sql = new QuerySql();
		
		IDataStreamElement id = null;
		IDataStreamElement label = null;
		IDataStreamElement parentId = null;
		
		LOOP:for (IBusinessTable table : pack.getBusinessTables("none")) {
			for (IDataStreamElement elem : table.getColumns("none")) {
				if(elem.getName().equals(lvl.getColumnId())) {
					id = elem;
				}
				if(elem.getName().equals(lvl.getColumnName())) {
					label = elem;
				}
				if(lvl.getParentColumnId() != null && !lvl.getParentColumnId().isEmpty() && lvl.getParentColumnId().equals(elem.getName())) {
					parentId = elem;
				}
				if(id != null && label != null && parentId != null) {
					break LOOP;
				}
			}
		}
		
		sql.getSelect().add(id);
		sql.getSelect().add(label);
		if(parentId != null) {
			sql.getSelect().add(parentId);
		}
		
		List<Ordonable> orders = new ArrayList<>();
		orders.add(id);
		orders.add(label);
		if(parentId != null) {
			orders.add(parentId);
		}
		
		sql.setOrberBy(orders);
		
		sql.setDistinct(true);
		
		return sql;
	}

	private String createLevelQuery(Level lvl, boolean distinct) {
		
		StringBuilder buf = new StringBuilder();
		
		buf.append("Select ");
		
		if(distinct) {
			buf.append(" distinct ");
		}
		
		buf.append("\n");
		
		buf.append("	" + lvl.getColumnId() + ",\n");
		buf.append("	" + lvl.getColumnName() + "\n");
		if(lvl.getParentColumnId() != null && !lvl.getParentColumnId().isEmpty()) {
			buf.append("	," + lvl.getParentColumnId() + " \n");
		}
		
		buf.append("From " + lvl.getTableName());
		
		buf.append(" Order by " + lvl.getColumnName());
		
		return buf.toString();
	}
	
	private String getOperatorQueryPart(String operator) {
		if(operator == null || operator.isEmpty()) {
			return "sum";
		}
		
		if(operator.equals(Metric.AGGREGATORS.get(Metric.AGGREGATOR_SUM)) || operator.equals(Metric.AGGREGATORS.get(Metric.AGGREGATOR_COUNT))
				 || operator.equals(Metric.AGGREGATORS.get(Metric.AGGREGATOR_DISTINCT_COUNT))) {
			return operator;
		}
		else if(operator.equals(Metric.AGGREGATORS.get(Metric.AGGREGATOR_MINIMUM))) {
			return "min";
		}
		else if(operator.equals(Metric.AGGREGATORS.get(Metric.AGGREGATOR_MAXIMUM))) {
			return "max";
		}
		else if(operator.equals(Metric.AGGREGATORS.get(Metric.AGGREGATOR_AVERAGE))) {
			return "avg";
		}
		
		return operator;
		
	}
}
