package bpm.fm.runtime.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.CalculatedFactTable;
import bpm.fm.api.model.CalculatedFactTableMetric;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.utils.ActionResult;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.MetricValue;
import bpm.fm.runtime.FreeMetricsManagerComponent;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

public class ValueCalculator {
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	protected static final String ALIAS_VALUE = "value";
	protected static final String ALIAS_OBJECTIVE = "objective";
	protected static final String ALIAS_MIN = "minimum";
	protected static final String ALIAS_MAX = "maximum";
	protected static final String ALIAS_YEAR = "dateyear";
	protected static final String ALIAS_MONTH = "datemonth";
	protected static final String ALIAS_DAY = "dateday";
	protected static final String ALIAS_HOUR = "datehour";
	protected static final String ALIAS_MINUTE = "dateminute";
	protected static final String ALIAS_TOLERANCE = "tolerance";
	
	protected String valueTableName = "";
	protected String objectiveTableName = "";

	protected FreeMetricsManagerComponent component;
	
	private static ScriptEngine mgr;
	
	static {
		mgr = new ScriptEngineManager().getEngineByName("JavaScript");
	}

	public ValueCalculator(FreeMetricsManagerComponent component) {
		this.component = component;
	}
	
	public HashMap<Metric, List<MetricValue>> getValues(final Date date, final Axis axis, final boolean getPrevious, final int groupId) throws Exception {
		final ConcurrentHashMap<Metric, List<MetricValue>> result = new ConcurrentHashMap<Metric, List<MetricValue>>();
		
		final AtomicInteger metricFinished = new AtomicInteger(0);
		List<Metric> metrics = new ArrayList<>();
		Date global = new Date();
		if(date != null) {
		
			metrics = component.getMetrics();
			for(final Metric metric : metrics) {
				Thread th = new Thread() {
					@Override
					public void run() {
						Date start = new Date();
						List<MetricValue> values = null;
						try {
							values = getValues(date, axis, metric, getPrevious, groupId);
							metricFinished.incrementAndGet();
						} catch(Exception e1) {
							e1.printStackTrace();
							metricFinished.incrementAndGet();
						}
						if(values != null) {
							//check the actions and calculate them if needed
							for(MetricValue val : values) {
								try {
									val.setActionResults(ActionHandler.handleAction(val));
//									metricFinished.incrementAndGet();
								} catch(Exception e) {
									e.printStackTrace();
//									metricFinished.incrementAndGet();
								}
							}
							
							if(values.isEmpty()) {
								MetricValue val = new MetricValue();
								val.setMetric(metric);
								val.setDate(date);
								val.setDummy(true);
								values.add(val);
							}
							
							result.put(metric, values);
						}
						System.out.println(metric.getName() + " time : " + (new Date().getTime() - start.getTime()));
					}
				};
				th.start();
			}
			
		}
		
		while(metrics.size() > metricFinished.intValue()) {
			Thread.sleep(100);
		}
		
		System.out.println("Total time : " + (new Date().getTime() - global.getTime()));
		
		return new HashMap<>(result);
	}
	
	private void createFakes(MetricValue metricValue) {
		MetricAction action = new MetricAction();
		action.setName("Regulation of airplane engines");
		
		action.setStartDate(createDate(2014, 3, 1));
		action.setEndDate(createDate(2014, 7, 1));
		
		ActionResult res = new ActionResult();
		res.setHealth(0);
		res.setStatus("Still in progress");
		
		MetricAction action2 = new MetricAction();
		action2.setName("Car noise regulation");
		
		action2.setStartDate(createDate(2014, 5, 1));
		action2.setEndDate(createDate(2014, 6, 1));
		
		ActionResult res2 = new ActionResult();
		res2.setHealth(-1);
		res2.setStatus("Ended");
		
		MetricAction action3 = new MetricAction();
		action3.setName("Installation of noise barriers");
		
		action3.setStartDate(createDate(2014, 4, 1));
		action3.setEndDate(createDate(2014, 6, 1));
		
		ActionResult res3 = new ActionResult();
		res3.setHealth(1);
		res3.setStatus("Ended");
		
		MetricAction action4 = new MetricAction();
		action4.setName("Discount for electric cars");
		
		action4.setStartDate(createDate(2014, 3, 1));
		action4.setEndDate(createDate(2014, 7, 1));
		
		ActionResult res4 = new ActionResult();
		res4.setHealth(0);
		res4.setStatus("Ended");
		
		metricValue.getChildren().get(1).getActionResults().put(action, res);
		metricValue.getChildren().get(1).getActionResults().put(action3, res3);
		metricValue.getChildren().get(2).getActionResults().put(action2, res2);
		metricValue.getChildren().get(2).getActionResults().put(action4, res4);
	}

	private Date createDate(int y, int m, int d) {
		Date date = new Date();
		date.setDate(d);
		date.setMonth(m - 1);
		date.setYear(y - 1900);
		return date;
	}

	private boolean isCorrespondingValues(MetricValue value1, MetricValue value2) {
		if(value1.getDate().equals(value2.getDate())) {
			if(value1.getAxis() != null) {
				for(int i = 0 ; i < value1.getAxis().size() ; i++) {
					try {
						if(!value1.getAxis().get(i).equals(value2.getAxis().get(i))) {
							return false;
						}
					} catch (Exception e) {
						return false;
					}
				}
				return true;
			}
			return true;
		}
		return false;
	}
	
	public List<MetricValue> getValues(Date startDate, Date endDate, Axis axis, Metric metric, boolean getPrevious, int groupId) {
		Level level = null;
		if(axis != null) {
			level = axis.getChildren().get(axis.getChildren().size() - 1);
		}
		return getValues(startDate, endDate, level, metric, getPrevious, null, groupId);
	}
	
	public List<MetricValue> getValues(Date date, Axis axis, Metric metric, boolean getPrevious, int groupId) throws Exception {
		
		if(!metric.getMetricType().equals(Metric.METRIC_TYPES.get(Metric.TYPE_CLASSIC))) {
			HashMap<Metric, List<MetricValue>> results = new HashMap<>();
			for(CalculatedFactTableMetric m : ((CalculatedFactTable)metric.getFactTable()).getMetrics()) {
				List<MetricValue> values = getValues(date, null, axis, m.getMetric(), getPrevious, groupId);
				results.put(m.getMetric(), values);
			}
			
			//take one metric as reference
			List<MetricValue> calcValues = new ArrayList<>();
			if(results != null && !results.isEmpty()) { 
				
				List<MetricValue> values = results.get(results.keySet().iterator().next());
				for(MetricValue val : values) {
					List<MetricValue> subs = new ArrayList<>();
					for(CalculatedFactTableMetric m : ((CalculatedFactTable)metric.getFactTable()).getMetrics()) {
						//find the value
						for(MetricValue v : results.get(m.getMetric())) {
							if(isCorrespondingValues(val, v)) {
								subs.add(v);
								break;
							}
						}
					}
					
					if(subs.size() == ((CalculatedFactTable)metric.getFactTable()).getMetrics().size()) {
					
						MetricValue calc = new MetricValue();
						calc.setMetric(metric);
						calc.setDate(val.getDate());
						calc.setAxis(val.getAxis());
						calc.setChildren(subs);
						if(metric.getMetricType().equals(Metric.METRIC_TYPES.get(Metric.TYPE_CALCULATED))) {
							String formulaVal = ((CalculatedFactTable)metric.getFactTable()).getCalculation();
							String formulaObj = ((CalculatedFactTable)metric.getFactTable()).getCalculation();
							String formulaMin = ((CalculatedFactTable)metric.getFactTable()).getCalculation();
							String formulaMax = ((CalculatedFactTable)metric.getFactTable()).getCalculation();
							for(CalculatedFactTableMetric m : ((CalculatedFactTable)metric.getFactTable()).getMetrics()) {
								for(MetricValue v : subs) {
									if(v.getMetric().equals(m.getMetric())) {
										
										formulaVal = formulaVal.replace("${" + m.getMetric().getName() + "}", ""+v.getValue());
										formulaObj = formulaObj.replace("${" + m.getMetric().getName() + "}", ""+v.getObjective());
										formulaMin = formulaMin.replace("${" + m.getMetric().getName() + "}", ""+v.getMinimum());
										formulaMax = formulaMax.replace("${" + m.getMetric().getName() + "}", ""+v.getMaximum());
									}
								}
							}
							String result = "";
							try {
								result = mgr.eval(formulaVal).toString();
								calc.setValue(Double.parseDouble(result));
							} catch(Exception e) {
								
							}
							try {
								result = mgr.eval(formulaObj).toString();
								calc.setObjective(Double.parseDouble(result));
							} catch(Exception e) {
								
							}
							try {
								result = mgr.eval(formulaMin).toString();
								calc.setMinimum(Double.parseDouble(result));
							} catch(Exception e) {
								
							}
							try {
								result = mgr.eval(formulaMax).toString();
								calc.setMaximum(Double.parseDouble(result));
							} catch(Exception e) {
								
							}
						}
						
						calcValues.add(calc);
					}
				}
			}
			
			return calcValues;
			
		}
		
		return getValues(date, null, axis, metric, getPrevious, groupId);
	}

	private void generateLinksBetweenValueAndObjective(Metric metric, StringBuffer buf) {
		if(((FactTable)metric.getFactTable()).getObjectives().getTableName() != null && !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty()  && !((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(valueTableName)) {
			for(FactTableAxis fta : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				buf.append("	cast(" + valueTableName + ".\"" + fta.getColumnId() + "\" as char(50)) = cast("  + objectiveTableName + ".\"" + fta.getObjectiveColumnId() + "\" as char(50)) and \n");
			}
		}
	}

	protected void generateAxisRelation(Metric metric, Level lastLevel, StringBuffer buf) {
		if(lastLevel != null) {		
			FactTableAxis axisFact = null;
			for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				if(factAxis.getAxisId() == lastLevel.getParent().getId()) {
					axisFact = factAxis;
					break;
				}
			}
			if(axisFact != null) {
				//link the axis and handle the multiTable axis
				
				Level linkLevel = lastLevel.getParent().getChildren().get(lastLevel.getParent().getChildren().size() - 1);

				if(!linkLevel.getTableName().equals(valueTableName)) {
					if(((DatasourceJdbc)linkLevel.getDatasource().getObject()).getDriver().equals("com.mysql.jdbc.Driver")) {
						buf.append("	cast(" + valueTableName + ".\"" + axisFact.getColumnId() + "\" as char(50)) = cast(" + linkLevel.getTableName() + ".\"" + linkLevel.getColumnId() + "\" as char(50)) and \n");
					}
					else {
						buf.append("	cast(" + valueTableName + ".\"" + axisFact.getColumnId() + "\" as varchar(50)) = cast(" + linkLevel.getTableName() + ".\"" + linkLevel.getColumnId() + "\" as varchar(50)) and \n");
					}
				}
				if(axisFact.getObjectiveColumnId() != null && !axisFact.getObjectiveColumnId().isEmpty()) {
					if(!linkLevel.getTableName().equals(objectiveTableName)) {
						if(((DatasourceJdbc)linkLevel.getDatasource().getObject()).getDriver().equals("com.mysql.jdbc.Driver")) {
							buf.append("	cast(" + objectiveTableName + ".\"" + axisFact.getObjectiveColumnId() + "\" as char(50)) = cast(" + linkLevel.getTableName() + ".\"" + linkLevel.getColumnId() + "\" as char(50)) and \n");
						}
						else {
							buf.append("	cast(" + objectiveTableName + ".\"" + axisFact.getObjectiveColumnId() + "\" as varchar(50)) = cast(" + linkLevel.getTableName() + ".\"" + linkLevel.getColumnId() + "\" as varchar(50)) and \n");
						}
					}
				}
				
				
				Level previousLevel = null;
				int lastLevelIndex = lastLevel.getParent().getChildren().indexOf(lastLevel.getParent().getChildren().get(lastLevel.getParent().getChildren().size() - 1));

				for(int i = lastLevelIndex ; i >= 0 ; i--) {
					Level level = lastLevel.getParent().getChildren().get(i);
					if(previousLevel == null) {
//						previousLevel = level;
					}
					else if(!previousLevel.getTableName().equals(level.getTableName())){
						if(((DatasourceJdbc)linkLevel.getDatasource().getObject()).getDriver().equals("com.mysql.jdbc.Driver")) {
							buf.append("	cast(" + previousLevel.getTableName() + ".\"" + previousLevel.getParentColumnId() + "\" as char(50)) = cast(" + level.getTableName() + ".\"" + level.getColumnId() + "\" as char(50)) and \n");
						}
						else {
							buf.append("	cast(" + previousLevel.getTableName() + ".\"" + previousLevel.getParentColumnId() + "\" as varchar(50)) = cast(" + level.getTableName() + ".\"" + level.getColumnId() + "\" as varchar(50)) and \n");
						}
					}
					
					previousLevel = level;
				}
			}
		}
	}

	protected void generateAxisFromPart(Level lastLevel, StringBuffer buf) {
		if(lastLevel != null) {		
			List<String> usedTables = new ArrayList<>();
			usedTables.add(valueTableName);
			if(objectiveTableName != null && !objectiveTableName.isEmpty()) {
				usedTables.add(objectiveTableName);
			}
			
			int lastLevelIndex = lastLevel.getParent().getChildren().indexOf(lastLevel.getParent().getChildren().get(lastLevel.getParent().getChildren().size() - 1));

			for(int i = 0 ; i <= lastLevelIndex ; i++) {
				Level level = lastLevel.getParent().getChildren().get(i);
				if(!usedTables.contains(level.getTableName())) {
					buf.append("	,");
					buf.append(level.getTableName() + "\n");
					usedTables.add(level.getTableName());
				}
			}
		}
	}

	protected void generateAxisOrderBy(Level lastLevel, StringBuffer buf) {
		if(lastLevel != null) {		
			int lastLevelIndex = lastLevel.getParent().getChildren().indexOf(lastLevel);
			for(int i = 0 ; i <= lastLevelIndex ; i++) {
				Level level = lastLevel.getParent().getChildren().get(i);
				buf.append("	,");
				buf.append(level.getTableName() + ".\"" + level.getColumnName() + "\"\n");
			}
		}
	}

	protected void generateAxisGroupBy(Level lastLevel, StringBuffer buf) {
		if(lastLevel != null) {		
			int lastLevelIndex = lastLevel.getParent().getChildren().indexOf(lastLevel);
			for(int i = 0 ; i <= lastLevelIndex ; i++) {
				Level level = lastLevel.getParent().getChildren().get(i);
				buf.append("	,");
				buf.append(level.getTableName() + ".\"" + level.getColumnName() + "\"\n");
//				buf.append("," + level.getTableName() + "." + level.getColumnId() + "\n");
				if(level.getGeoColumnId() != null && !level.getGeoColumnId().isEmpty()) {
					buf.append("," + level.getTableName() + ".\"" + level.getGeoColumnId() + "\"\n");
				}
			}
		}
	}

	protected void generateAxisSelect(Level lastLevel, StringBuffer buf) {
		if(lastLevel != null) {
			
			int lastLevelIndex = lastLevel.getParent().getChildren().indexOf(lastLevel);
			for(int i = 0 ; i <= lastLevelIndex ; i++) {
				Level level = lastLevel.getParent().getChildren().get(i);
				buf.append("	,");
				String table = null;
				if(level.getTableName().equals(valueTableName)) {
					
					table = valueTableName;
				}
				else {
					if(objectiveTableName != null && !objectiveTableName.isEmpty() && objectiveTableName.equals(level.getTableName())) {
						table = objectiveTableName;
					}
					else {
						table = level.getTableName();
					}
				}
				buf.append(table + ".\"" + level.getColumnName() + "\",\n");
				if(lastLevel.getParent().isOnOneTable()) {
					buf.append(table + ".\"" + level.getColumnName() + "\"\n");
				}
				else {
					buf.append("min(" + table + ".\"" + level.getColumnId() + "\") as " + level.getColumnId() + "\n");
				}
				if(level.getGeoColumnId() != null && !level.getGeoColumnId().isEmpty()) {
					buf.append("," + table + ".\"" + level.getGeoColumnId() + "\"\n");
				}
			}
		}
	}

	protected List<MetricValue> executeQuery(VanillaJdbcConnection valueConnection, Level lastLevel, String buf, boolean asObjectves, Metric metric, Date startDate, Date endDate, boolean getPrevious, int groupId) throws Exception {
		VanillaPreparedStatement statement = valueConnection.prepareQuery(buf.toString());
		
		ResultSet resultSet = statement.executeQuery();
		
		List<MetricValue> result = new ArrayList<>();
		
		MetricValue previous = null;
		
		while(resultSet.next()) {
			MetricValue value = new MetricValue();
			
			double val = resultSet.getDouble(ALIAS_VALUE);
			BigDecimal bd = new BigDecimal(val);
			bd = bd.setScale(2,  RoundingMode.HALF_UP);
			val = bd.doubleValue();
			
			if(asObjectves) {
				try {
					double obj = resultSet.getDouble(ALIAS_OBJECTIVE);
					bd = new BigDecimal(obj);
					bd = bd.setScale(2,  RoundingMode.HALF_UP);
					obj = bd.doubleValue();
					value.setObjective(obj);
				} catch(Exception e1) {
				}
				
				try {
					double max = resultSet.getDouble(ALIAS_MAX);
					bd = new BigDecimal(max);
					bd = bd.setScale(2,  RoundingMode.HALF_UP);
					max = bd.doubleValue();
					value.setMaximum(max);
				} catch(Exception e1) {
				}
				
				try {
					double min = resultSet.getDouble(ALIAS_MIN);
					bd = new BigDecimal(min);
					bd = bd.setScale(2,  RoundingMode.HALF_UP);
					min = bd.doubleValue();
					value.setMinimum(min);
				} catch(Exception e1) {
				}
				
				try {
					double tolerance = resultSet.getDouble(ALIAS_TOLERANCE);
					bd = new BigDecimal(tolerance);
					bd = bd.setScale(2,  RoundingMode.HALF_UP);
					tolerance = bd.doubleValue();
					value.setTolerance(tolerance);
				}catch(Exception e) {
					
				}
				
			}
			int year = resultSet.getInt(ALIAS_YEAR);
			int month;
			try {
				month = resultSet.getInt(ALIAS_MONTH);
			} catch (Exception e) {
				month = 1;
			}
			int day;
			try {
				day = resultSet.getInt(ALIAS_DAY);
			} catch (Exception e) {
				day = 1;
			}
			
			int hour;
			try {
				hour = resultSet.getInt(ALIAS_HOUR);
			} catch (Exception e) {
				hour = 0;
			}
			
			int minutes;
			try {
				minutes = resultSet.getInt(ALIAS_MINUTE);
			} catch (Exception e) {
				minutes = 0;
			}
			
			value.setValue(val);

			value.setDate(new Date(year - 1900, month - 1, day, hour, minutes));
			
			getAxisValues(value, lastLevel, resultSet);
			
			value.setMetric(metric);
			
			//calculate tendancy
			if(previous != null && lastLevel == null && endDate == null) {
				if(metric.getDirection().equals(Metric.DIRECTIONS.get(Metric.DIRECTION_BOTTOM))) {
					value.setTendancy((int)(previous.getValue() - value.getValue()));
				}
				else if(metric.getDirection().equals(Metric.DIRECTIONS.get(Metric.DIRECTION_TOP))){
					value.setTendancy((int)(value.getValue() - previous.getValue()));
				}
				else {
					int actu = Math.abs((int)(value.getValue() - value.getObjective()));
					int prev = Math.abs((int)(previous.getValue() - previous.getObjective()));
					value.setTendancy(prev - actu);
				}
			}
			previous = value;
			if((getPrevious || isCorresponding(value, startDate, endDate)) || endDate != null) {
				result.add(value);
				
				//check if the value has alerts
				
				if(!component.getJdbcManager().isPostgres()) {
				
					List<AlertRaised> alerts = component.getRaisedAlerts(value);
					value.setRaised(alerts);
				}
			}
			
		}
		
		resultSet.close();
		statement.close();
		
		return result;
	}

	@SuppressWarnings("deprecation")
	private boolean isCorresponding(MetricValue value, Date startDate, Date endDate) {
		//FIXME I think we need to do 2 different things if there's hour/minutes or not
		if(endDate != null) {
			if(value.getDate().after(startDate) && value.getDate().before(endDate)) {
				return true;
			}
			else if(value.getDate().equals(startDate) || value.getDate().equals(endDate)) {
				return true;
			}
		}
		else {
			if(((FactTable)value.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_YEARLY)) {
				if(value.getDate().getYear() == startDate.getYear()) {
					return true;
				}
			}
			else if(((FactTable)value.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_MONTHLY)) {
				if(value.getDate().getYear() == startDate.getYear() && value.getDate().getMonth() == startDate.getMonth()) {
					return true;
				}
			}
			else if(((FactTable)value.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_DAILY)) {
				if(value.getDate().getYear() == startDate.getYear() && value.getDate().getMonth() == startDate.getMonth() && value.getDate().getDate() == startDate.getDate()) {
					return true;
				}
			}
			else if(((FactTable)value.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_HOURLY)) {
				if(value.getDate().getYear() == startDate.getYear() && value.getDate().getMonth() == startDate.getMonth() && value.getDate().getDate() == startDate.getDate() && value.getDate().getHours() == startDate.getHours()) {
					return true;
				}
			}
			else if(((FactTable)value.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_MINUTE)) {
				if(value.getDate().getYear() == startDate.getYear() && value.getDate().getMonth() == startDate.getMonth() && value.getDate().getDate() == startDate.getDate() && value.getDate().getHours() == startDate.getHours() && value.getDate().getMinutes() == startDate.getMinutes()) {
					return true;
				}
			}
		}
		return false;
	}

	protected void getAxisValues(MetricValue value, Level lastLevel, ResultSet resultSet) throws Exception {
		if(lastLevel != null) {
			int lastLevelIndex = lastLevel.getParent().getChildren().indexOf(lastLevel);
			for(int i = 0 ; i <= lastLevelIndex ; i++) {
				Level level = lastLevel.getParent().getChildren().get(i);
				String val = resultSet.getString(level.getColumnName());
				String key = "";
				if(level.getParent().isOnOneTable()) {
					key = resultSet.getString(level.getColumnName());
				}
				else {
					key = resultSet.getString(level.getColumnId());
				}
				
				if(key == null) {
					key = "null";
				}
				if(val == null) {
					val = "null";
				}
				
				LevelMember member = new LevelMember();
				member.setLabel(val);
				member.setLevel(level);
				member.addKey(key);
				if(level.getGeoColumnId() != null && !level.getGeoColumnId().isEmpty()) {
					member.setGeoId(resultSet.getString(level.getGeoColumnId()));
				}
				
				value.add(member);
			}
		}
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

	public String getValueTableName() {
		return valueTableName;
	}

	public String getObjectiveTableName() {
		return objectiveTableName;
	}

	public List<MetricValue> getValues(Date startDate, Date endDate, Level level, bpm.fm.api.model.Metric metric, boolean getPrevious, List<LevelMember> filters, int groupId) {
		try {
			List<MetricValue> values = new ArrayList<MetricValue>();
			
			//create the connexion
			Datasource ds = ((FactTable)metric.getFactTable()).getDatasource();
			
			if(ds.getType() == DatasourceType.JDBC) {
			
				DatasourceJdbc dsJdbc = (DatasourceJdbc) ds.getObject();
				if(ds != null) {
					VanillaJdbcConnection valueConnection = null;
					try {
						valueConnection = ConnectionManager.getInstance().getJdbcConnection(dsJdbc);
						
						//generate the sql query
						StringBuffer buf = new StringBuffer();
						String metricTable = ((FactTable)metric.getFactTable()).getTableName();
						valueTableName = metricTable;
						String objTable = ((FactTable)metric.getFactTable()).getObjectives().getTableName();
						objectiveTableName = objTable;
						String valueColumn = ((FactTable)metric.getFactTable()).getValueColumn();
						String objColumn = ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn();
						String minColumn = ((FactTable)metric.getFactTable()).getObjectives().getMinColumn();
						String maxColumn = ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn();
						
						String tolerance = ((FactTable)metric.getFactTable()).getObjectives().getTolerance();
						
						String valueDateColumn = ((FactTable)metric.getFactTable()).getDateColumn();
						String objDateColumn = ((FactTable)metric.getFactTable()).getObjectives().getDateColumn();
						
						String periodicity = ((FactTable)metric.getFactTable()).getPeriodicity();
						
						String aggregator = getOperatorQueryPart(metric.getOperator());
						
						//String valColumn = ((FactTable)metric.getFactTable()).getValueColumn();
						
						//generate the select part
						buf.append("Select \n");
						buf.append("    " + aggregator + "(" + valueTableName + ".\"" + valueColumn + "\") as " + ALIAS_VALUE + ",\n");
						if(objTable != null && !objTable.isEmpty()) {
							
							if(aggregator.equals(Metric.AGGREGATORS.get(Metric.AGGREGATOR_COUNT)) || aggregator.equals(Metric.AGGREGATORS.get(Metric.AGGREGATOR_DISTINCT_COUNT))) {
								aggregator = "sum";
							}
							
							if(objColumn != null && !objColumn.isEmpty()) {
								buf.append("	" + aggregator + "(" + objectiveTableName + ".\"" + objColumn + "\") as " + ALIAS_OBJECTIVE + ",\n");
							}
							if(maxColumn != null && !maxColumn.isEmpty()) {
								buf.append("	" + aggregator + "(" + objectiveTableName + ".\"" + maxColumn + "\") as " + ALIAS_MAX + ",\n");
							}
							if(minColumn != null && !minColumn.isEmpty()) {
								buf.append("	" + aggregator + "(" + objectiveTableName + ".\"" + minColumn + "\") as " + ALIAS_MIN + ",\n");
							}
							
							if(tolerance != null && !tolerance.isEmpty()) {
								try {
									Integer.parseInt(tolerance);
									buf.append("	" + tolerance + " as " + ALIAS_TOLERANCE + ",\n");
								} catch(Exception e) {
									buf.append("	" + objectiveTableName + ".\"" + tolerance + "\" as " + ALIAS_TOLERANCE + ",\n");
								}
							}
							
						}
						//generate the date part
						NonStandardSgbdHandler.generateSelectDate(periodicity, valueDateColumn, buf, dsJdbc.getDriver(), this);
						
						generateAxisSelect(level, buf);
						
						//generate the from part
						buf.append("From \n");
						buf.append("	" + valueTableName + "\n");
						if(objTable != null && !objTable.isEmpty() && !objTable.equals(valueTableName)) {
							buf.append("	," + objectiveTableName + "\n");
						}
						generateAxisFromPart(level, buf);
						
						//generate the where part
						buf.append("Where \n");
						//generate the relation
						
						if(objDateColumn != null && !objDateColumn.isEmpty() && !objTable.equals(valueTableName)) {
							buf.append(NonStandardSgbdHandler.generateDateRelation(periodicity, valueDateColumn, objDateColumn, dsJdbc.getDriver(), this));
						}
						
						generateLinksBetweenValueAndObjective(metric, buf);
						
						//generate the axis relation
						generateAxisRelation(metric, level, buf);
						
						if(filters != null){
							//generate the levelmember filter
							generateLevelMembersFilter(filters, buf);
						}
						
						//generate the date filter
						List<String> columnsForShitSgbds = NonStandardSgbdHandler.generateDateFilter(periodicity, startDate, endDate, valueDateColumn, buf, getPrevious, dsJdbc.getDriver(), this, false);
						
						
						
						
						//generate the group by
						buf.append("Group by \n");
						switch (periodicity) {
						case FactTable.PERIODICITY_YEARLY:
							if(NonStandardSgbdHandler.shitSgbds.contains(dsJdbc.getDriver())) {
								buf.append("	" + columnsForShitSgbds.get(0) + "\n");
							}
							else {
								buf.append("	" + ALIAS_YEAR + "\n");
							}
							break;
						case FactTable.PERIODICITY_MONTHLY:
						case FactTable.PERIODICITY_BIANNUAL:
						case FactTable.PERIODICITY_QUARTERLY:
							buf.append("	" + ALIAS_YEAR + ",\n");
							buf.append("	" + ALIAS_MONTH + "\n");
							break;
						case FactTable.PERIODICITY_DAILY:
						case FactTable.PERIODICITY_WEEKLY:
							buf.append("	" + ALIAS_YEAR + ",\n");
							buf.append("	" + ALIAS_MONTH + ",\n");
							buf.append("	" + ALIAS_DAY + "\n");
							break;
						case FactTable.PERIODICITY_HOURLY:
							buf.append("	" + ALIAS_YEAR + ",\n");
							buf.append("	" + ALIAS_MONTH + ",\n");
							buf.append("	" + ALIAS_DAY + ",\n");
							buf.append("	" + ALIAS_HOUR + "\n");
							break;
						
						case FactTable.PERIODICITY_MINUTE:
							buf.append("	" + ALIAS_YEAR + ",\n");
							buf.append("	" + ALIAS_MONTH + ",\n");
							buf.append("	" + ALIAS_DAY + ",\n");
							buf.append("	" + ALIAS_HOUR + ",\n");
							buf.append("	" + ALIAS_MINUTE + "\n");
							break;
						
						default:
							break;
						}
						
						if(objTable != null && !objTable.isEmpty() && tolerance != null && !tolerance.isEmpty()) {
							buf.append("	," + ALIAS_TOLERANCE + "\n");
						}
						
						generateAxisGroupBy(level, buf);
						
						//generate the order by
						buf.append("Order by \n");
						switch (periodicity) {
						case FactTable.PERIODICITY_YEARLY:
							buf.append("	" + ALIAS_YEAR);
							break;
						case FactTable.PERIODICITY_MONTHLY:
						case FactTable.PERIODICITY_BIANNUAL:
						case FactTable.PERIODICITY_QUARTERLY:
							buf.append("	" + ALIAS_YEAR + ",\n");
							buf.append("	" + ALIAS_MONTH);
							break;
						case FactTable.PERIODICITY_DAILY:
						case FactTable.PERIODICITY_WEEKLY:
							buf.append("	" + ALIAS_YEAR + ",\n");
							buf.append("	" + ALIAS_MONTH + ",\n");
							buf.append("	" + ALIAS_DAY);
							break;
						case FactTable.PERIODICITY_HOURLY:
							buf.append("	" + ALIAS_YEAR + ",\n");
							buf.append("	" + ALIAS_MONTH + ",\n");
							buf.append("	" + ALIAS_DAY + ",\n");
							buf.append("	" + ALIAS_HOUR);
							break;
						
						case FactTable.PERIODICITY_MINUTE:
							buf.append("	" + ALIAS_YEAR + ",\n");
							buf.append("	" + ALIAS_MONTH + ",\n");
							buf.append("	" + ALIAS_DAY + ",\n");
							buf.append("	" + ALIAS_HOUR + ",\n");
							buf.append("	" + ALIAS_MINUTE);
							break;
	
						default:
							break;
						}
						
						generateAxisOrderBy(level, buf);
						
						Logger.getLogger(ValueCalculator.class.getName()).debug(buf.toString());
						
						//execute the query and create the value objects
						values = executeQuery(valueConnection, level, buf.toString(), objTable != null && !objTable.isEmpty(), metric, startDate, endDate, getPrevious, groupId);
						
					} catch (Exception e) {
						e.printStackTrace();
						throw e;
					} finally {
						ConnectionManager.getInstance().returnJdbcConnection(valueConnection);
					}
					
					return values;
				}
			}
			else {
				return new FmdtValueCalculator(component).executeQuery(startDate, endDate, level, metric, getPrevious, groupId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void generateLevelMembersFilter(List<LevelMember> filters, StringBuffer buf) {
		if(filters.size() > 0) {
			buf.append("(");
			for(LevelMember levelmember : filters) {
				
				if(filters.indexOf(levelmember) == filters.size()-1){
					buf.append(levelmember.getLevel().getTableName() + ".\"" + levelmember.getLevel().getColumnId() + "\" = '" + levelmember.getValue() +"'\n");
				} else {
					buf.append(levelmember.getLevel().getTableName() + ".\"" + levelmember.getLevel().getColumnId() + "\" = '" + levelmember.getValue() + "' or\n");
				}
				
				
			}
			buf.append(") and");
		}
		
	}
	
}
