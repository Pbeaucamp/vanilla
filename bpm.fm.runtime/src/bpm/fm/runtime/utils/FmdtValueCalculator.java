package bpm.fm.runtime.utils;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.MetricValue;
import bpm.fm.runtime.FreeMetricsManagerComponent;
import bpm.metadata.birt.oda.runtime.ConnectionPool;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.ICalculatedElement;
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
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.tools.MetadataLoader;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class FmdtValueCalculator extends ValueCalculator {

	protected IDataStreamElement colValue, colObj, colMin, colMax;
	protected IDataStreamElement colDate;
	private IDataStreamElement colTolerance;
	
	private List<LevelData> axis = new ArrayList<LevelData>();
	
	private List<ICalculatedElement> columnDates;
	protected List<IFilter> filterDates;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
	
	public FmdtValueCalculator(FreeMetricsManagerComponent component) {
		super(component);
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
	
	public List<MetricValue> executeQuery(Date startDate, Date endDate, Level level, Metric metric, boolean getPrevious, int groupId) throws Exception {

		Datasource ds = ((FactTable) metric.getFactTable()).getDatasource();
		DatasourceFmdt dsFmdt = (DatasourceFmdt) ds.getObject();

		Date start = new Date();
		String groupName = "none";
		if(groupId > -1) {
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL),
					dsFmdt.getUser(),
					dsFmdt.getPassword());
			Group group = vanillaApi.getVanillaSecurityManager().getGroupById(groupId);
			groupName = group.getName();
		}
		
		IBusinessPackage pack = loadMetadata(dsFmdt, groupName);//MetadataLoader.loadMetadata(dsFmdt);
		System.out.println("load fmdt : " + dsFmdt.getItemId() + " -> " + (new Date().getTime() - start.getTime()));

		findColumnsInPackage(pack, metric, level);
		System.out.println("find columns -> " + (new Date().getTime() - start.getTime()));

		createDateColumnsAndFilters(startDate, endDate, metric, getPrevious);
		System.out.println("create columns -> " + (new Date().getTime() - start.getTime()));

		List<MetricValue> result = new ArrayList<MetricValue>();
		
		List<AggregateFormula> aggs = new ArrayList<>();
		AggregateFormula valueAgg = new AggregateFormula(getOperatorQueryPart(metric.getOperator()).toUpperCase(), colValue, ALIAS_VALUE);
		aggs.add(valueAgg);
		
		boolean asObjective = false;
		
		if(colObj != null) {
			asObjective = true;
			AggregateFormula objAgg = new AggregateFormula(getOperatorQueryPart(metric.getOperator()).toUpperCase(), colObj, ALIAS_OBJECTIVE);
			aggs.add(objAgg);
		}
		if(colMin != null) {
			AggregateFormula objAgg = new AggregateFormula(getOperatorQueryPart(metric.getOperator()).toUpperCase(), colMin, ALIAS_MIN);
			aggs.add(objAgg);
		}
		if(colMax != null) {
			AggregateFormula objAgg = new AggregateFormula(getOperatorQueryPart(metric.getOperator()).toUpperCase(), colMax, ALIAS_MAX);
			aggs.add(objAgg);
		}
		
		List<IDataStreamElement> select = new ArrayList<>();
		List<Ordonable> orderBy = new ArrayList<>();
		for(ICalculatedElement el : columnDates) {
			select.add(el);
			orderBy.add(el);
		}
		if(colTolerance != null) {
			colTolerance.setName(ALIAS_TOLERANCE);
			select.add(colTolerance);
		}

		for(LevelData l : axis) {
			AggregateFormula objAgg = new AggregateFormula("MIN", l.getId(), "colid" + l.getLevel().getName());
			aggs.add(objAgg);
			select.add(l.getLabel());
			orderBy.add(l.getLabel());
			if(l.getGeo() != null) {
				select.add(l.getGeo());
				orderBy.add(l.getGeo());
			}
		}
		 
		IQuery query = SqlQueryBuilder.getQuery(
				"none", 
				select, 
				new HashMap<ListOfValue, String>(), 
				aggs,
				orderBy, 
				filterDates, 
				new ArrayList<Prompt>(), 
				new ArrayList<Formula>(), 
				new ArrayList<RelationStrategy>());

		
		EffectiveQuery effQuery = SqlQueryGenerator.getQuery(null, null, pack, (QuerySql)query, groupName, false, new HashMap<Prompt, List<String>>());
		
		String queryToExecute = effQuery.getGeneratedQuery().replace("`", "\"");
		
		Logger.getLogger(FmdtValueCalculator.class.getName()).debug(queryToExecute);
		
		VanillaJdbcConnection valueConnection = ((SQLConnection)pack.getConnection("none", "Default")).getJdbcConnection();
		
		result = executeQuery(valueConnection, level, queryToExecute, asObjective, metric, startDate, endDate, getPrevious, groupId);
		
		ConnectionManager.getInstance().returnJdbcConnection(valueConnection);
		
		return result;
	}
	
	private IBusinessPackage loadMetadata(DatasourceFmdt dsFmdt, String groupName) throws Exception {
		
		Properties p = new Properties();
		p.setProperty("REPOSITORY_ID", String.valueOf(dsFmdt.getRepositoryId()));
		p.setProperty("URL", String.valueOf(dsFmdt.getUrl()));
		p.setProperty("DIRECTORY_ITEM_ID", String.valueOf(dsFmdt.getItemId()));
		p.setProperty("GROUP_NAME", groupName);
		p.setProperty("USER", dsFmdt.getUser());
		p.setProperty("PASSWORD", dsFmdt.getPassword());
		
		Collection<IBusinessModel> c = ConnectionPool.getConnection(p);
		
		for(IBusinessModel m : c){
			if (m.getName().equals(dsFmdt.getBusinessModel())){
				for(IBusinessPackage pa : m.getBusinessPackages(groupName)){
					if (pa.getName().equals(dsFmdt.getBusinessPackage())){
						return pa;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected void getAxisValues(MetricValue value, Level lastLevel, ResultSet resultSet) throws Exception {
		if(lastLevel != null) {
			int lastLevelIndex = lastLevel.getParent().getChildren().indexOf(lastLevel);
			for(int i = 0 ; i <= lastLevelIndex ; i++) {
				Level level = lastLevel.getParent().getChildren().get(i);
				for(LevelData data : axis) {
					if(data.getLevel().getId() == level.getId()) {
						
						
						String val = resultSet.getString(data.label.getOuputName());
						String key = "";
						if(level.getParent().isOnOneTable()) {
							key = resultSet.getString(data.label.getOuputName());
						}
						else {
							key = resultSet.getString("colid" + data.getLevel().getName());
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
							member.setGeoId(resultSet.getString(data.geo.getOuputName()));
						}
						
						value.add(member);
						
						
						break;
					}
				}

			}
		}
	}

	protected void createDateColumnsAndFilters(Date startDate, Date endDate, Metric metric, boolean getPrevious) {
		columnDates = new ArrayList<ICalculatedElement>();
		filterDates = new ArrayList<IFilter>();

		String colDateName = colDate.getDataStream().getName() + ".";
		String col = colDate.getOriginName();
		if(colDate instanceof ICalculatedElement) {
			colDateName = "(" + ((ICalculatedElement)colDate).getFormula() + ")";
		}
		else {
			if(colDate.getOriginName().contains(".")) {
				col = colDate.getOriginName().substring(colDate.getOriginName().lastIndexOf(".") + 1, colDate.getOriginName().length());
			}
			colDateName += col;
		}
		
		
		switch (((FactTable) metric.getFactTable()).getPeriodicity()) {
		case FactTable.PERIODICITY_YEARLY:
			
			ICalculatedElement year = new ICalculatedElement("Extract(YEAR From " + colDateName + ")");
			year.setOrigin(colDate.getOrigin());
			year.setDataStream(colDate.getDataStream());
			year.setName(ALIAS_YEAR);
			year.setClassType(2);
			columnDates.add(year);

			if(getPrevious) {
				ComplexFilter yearF = new ComplexFilter();
				yearF.setOrigin(year);
				yearF.setOperator("BETWEEN");
				yearF.setValue((startDate.getYear() + 1900 - 12) + "");
				yearF.setValue((startDate.getYear() + 1900) + "");
				
				filterDates.add(yearF);
			}
			
			else if(endDate != null) {
				ComplexFilter yearF = new ComplexFilter();
				yearF.setOrigin(year);
				yearF.setOperator("BETWEEN");
				yearF.setValue((startDate.getYear() + 1900) + "");
				yearF.setValue((endDate.getYear() + 1900) + "");
				
				filterDates.add(yearF);
			}
			else {
				ComplexFilter yearF = new ComplexFilter();
				yearF.setOrigin(year);
				yearF.setOperator("BETWEEN");
				yearF.setValue((startDate.getYear() + 1900 - 1) + "");
				yearF.setValue((startDate.getYear() + 1900) + "");
				
				filterDates.add(yearF);
			}
			
			break;
		case FactTable.PERIODICITY_BIANNUAL:
		case FactTable.PERIODICITY_MONTHLY:
		case FactTable.PERIODICITY_QUARTERLY:
	
			year = new ICalculatedElement("Extract(YEAR From " + colDateName + ")");
			year.setName(ALIAS_YEAR);
			year.setOrigin(colDate.getOrigin());
			year.setDataStream(colDate.getDataStream());
			year.setClassType(2);
			columnDates.add(year);
			ICalculatedElement month = new ICalculatedElement("Extract(MONTH From " + colDateName + ")");
			month.setName(ALIAS_MONTH);
			month.setOrigin(colDate.getOrigin());
			month.setDataStream(colDate.getDataStream());
			month.setClassType(2);
			columnDates.add(month);

			if(getPrevious) {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				Date minD = new Date(startDate.getYear(), startDate.getMonth() - 12, 1, 0 , 0 , 0);
				dateF.setValue(sdf.format(minD));
				Date maxD = new Date(startDate.getYear(), startDate.getMonth() + 1, 1, 0 , 0 , -1);
				dateF.setValue(sdf.format(maxD));
				
				filterDates.add(dateF);
			}
			
			else if(endDate != null) {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				dateF.setValue(sdf.format(startDate));
				dateF.setValue(sdf.format(endDate));
				
				filterDates.add(dateF);
			}
			else {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				Date minD = new Date(startDate.getYear(), startDate.getMonth() - 1, 1, 0 , 0 , 0);
				dateF.setValue(sdf.format(minD));
				Date maxD = new Date(startDate.getYear(), startDate.getMonth() + 1, 1, 0 , 0 , -1);
				dateF.setValue(sdf.format(maxD));
				
				filterDates.add(dateF);
			}
			
			break;
		case FactTable.PERIODICITY_WEEKLY:
		case FactTable.PERIODICITY_DAILY:

			year = new ICalculatedElement("Extract(YEAR From " + colDateName + ")");
			year.setName(ALIAS_YEAR);
			year.setOrigin(colDate.getOrigin());
			year.setDataStream(colDate.getDataStream());
			year.setClassType(2);
			columnDates.add(year);
			month = new ICalculatedElement("Extract(MONTH From " + colDateName + ")");
			month.setName(ALIAS_MONTH);
			month.setOrigin(colDate.getOrigin());
			month.setDataStream(colDate.getDataStream());
			month.setClassType(2);
			columnDates.add(month);
			ICalculatedElement day = new ICalculatedElement("Extract(DAY From " + colDateName + ")");
			day.setName(ALIAS_DAY);
			day.setOrigin(colDate.getOrigin());
			day.setDataStream(colDate.getDataStream());
			day.setClassType(2);
			columnDates.add(day);

			if(getPrevious) {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				Date minD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate() - 12, 0 , 0 , 0);
				dateF.setValue(sdf.format(minD));
				Date maxD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate() + 1, 0 , 0 , -1);
				dateF.setValue(sdf.format(maxD));
				
				filterDates.add(dateF);
			}
			
			else if(endDate != null) {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				dateF.setValue(sdf.format(startDate));
				dateF.setValue(sdf.format(endDate));
				
				filterDates.add(dateF);
			}
			else {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				Date minD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate() - 1, 0 , 0 , 0);
				dateF.setValue(sdf.format(minD));
				Date maxD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate() + 1, 0 , 0 , -1);
				dateF.setValue(sdf.format(maxD));
				
				filterDates.add(dateF);
			}
			
			
			break;
		case FactTable.PERIODICITY_HOURLY:
			
			year = new ICalculatedElement("Extract(YEAR From " + colDateName + ")");
			year.setName(ALIAS_YEAR);
			year.setOrigin(colDate.getOrigin());
			year.setDataStream(colDate.getDataStream());
			year.setClassType(2);
			columnDates.add(year);
			month = new ICalculatedElement("Extract(MONTH From " + colDateName + ")");
			month.setName(ALIAS_MONTH);
			month.setOrigin(colDate.getOrigin());
			month.setDataStream(colDate.getDataStream());
			month.setClassType(2);
			columnDates.add(month);
			day = new ICalculatedElement("Extract(DAY From " + colDateName + ")");
			day.setName(ALIAS_DAY);
			day.setOrigin(colDate.getOrigin());
			day.setDataStream(colDate.getDataStream());
			day.setClassType(2);
			columnDates.add(day);
			ICalculatedElement hour = new ICalculatedElement("Extract(HOUR From " + colDateName + ")");
			hour.setName(ALIAS_HOUR);
			hour.setOrigin(colDate.getOrigin());
			hour.setDataStream(colDate.getDataStream());
			hour.setClassType(2);
			columnDates.add(hour);

			if(getPrevious) {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				Date minD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), startDate.getHours() - 12, 0 , 0);
				dateF.setValue(sdf.format(minD));
				Date maxD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), startDate.getHours() + 1, 0 , -1);
				dateF.setValue(sdf.format(maxD));
				
				filterDates.add(dateF);
			}
			
			else if(endDate != null) {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				dateF.setValue(sdf.format(startDate));
				dateF.setValue(sdf.format(endDate));
				
				filterDates.add(dateF);
			}
			else {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				Date minD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), startDate.getHours() - 1, 0 , 0);
				dateF.setValue(sdf.format(minD));
				Date maxD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), startDate.getHours() + 1, 0 , -1);
				dateF.setValue(sdf.format(maxD));
				
				filterDates.add(dateF);
			}
			
			break;
		case FactTable.PERIODICITY_MINUTE:

			year = new ICalculatedElement("Extract(YEAR From " + colDateName + ")");
			year.setName(ALIAS_YEAR);
			year.setOrigin(colDate.getOrigin());
			year.setDataStream(colDate.getDataStream());
			year.setClassType(2);
			columnDates.add(year);
			month = new ICalculatedElement("Extract(MONTH From " + colDateName + ")");
			month.setName(ALIAS_MONTH);
			month.setOrigin(colDate.getOrigin());
			month.setDataStream(colDate.getDataStream());
			month.setClassType(2);
			columnDates.add(month);
			day = new ICalculatedElement("Extract(DAY From " + colDateName + ")");
			day.setName(ALIAS_DAY);
			day.setOrigin(colDate.getOrigin());
			day.setDataStream(colDate.getDataStream());
			day.setClassType(2);
			columnDates.add(day);
			hour = new ICalculatedElement("Extract(HOUR From " + colDateName + ")");
			hour.setName(ALIAS_HOUR);
			hour.setOrigin(colDate.getOrigin());
			hour.setDataStream(colDate.getDataStream());
			hour.setClassType(2);
			columnDates.add(hour);
			ICalculatedElement minute = new ICalculatedElement("Extract(MINUTE From " + colDateName + ")");
			minute.setName(ALIAS_MINUTE);
			minute.setOrigin(colDate.getOrigin());
			minute.setDataStream(colDate.getDataStream());
			minute.setClassType(2);
			columnDates.add(minute);

			if(getPrevious) {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				Date minD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), startDate.getHours(), startDate.getMinutes() - 12, 0);
				dateF.setValue(sdf.format(minD));
				Date maxD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), startDate.getHours(), startDate.getMinutes() + 1, -1);
				dateF.setValue(sdf.format(maxD));
				
				filterDates.add(dateF);
			}
			
			else if(endDate != null) {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				dateF.setValue(sdf.format(startDate));
				dateF.setValue(sdf.format(endDate));
				
				filterDates.add(dateF);
			}
			else {
				ComplexFilter dateF = new ComplexFilter();
				dateF.setOrigin(colDate);
				dateF.setOperator("BETWEEN");
				Date minD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), startDate.getHours(), startDate.getMinutes() - 1, 0);
				dateF.setValue(sdf.format(minD));
				Date maxD = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), startDate.getHours(), startDate.getMinutes() + 1, -1);
				dateF.setValue(sdf.format(maxD));
				
				filterDates.add(dateF);
			}
			
			break;
		}
		
	}

	protected void findColumnsInPackage(IBusinessPackage pack, Metric metric, Level level) {
		for (IBusinessTable table : pack.getBusinessTables("none")) {
			for (IDataStreamElement elem : table.getColumns("none")) {
				checkColumn(elem, metric, level);
			}
		}
	}

	private void checkColumn(IDataStreamElement elem, Metric metric, Level lastLevel) {
		// value
		String value = ((FactTable) metric.getFactTable()).getValueColumn();
		String date = ((FactTable) metric.getFactTable()).getDateColumn();
		if (elem.getName().equals(value)) {
			colValue = elem;
		}
		else if (elem.getName().equals(date)) {
			colDate = elem;
		}

		// obj
		if (((FactTable) metric.getFactTable()).getObjectives() != null) {
			String objCol = ((FactTable) metric.getFactTable()).getObjectives().getObjectiveColumn();
			String minCol = ((FactTable) metric.getFactTable()).getObjectives().getMinColumn();
			String maxCol = ((FactTable) metric.getFactTable()).getObjectives().getMaxColumn();
			String tolCol = ((FactTable) metric.getFactTable()).getObjectives().getTolerance();

			if (elem.getName().equals(objCol)) {
				colObj = elem;
			}
			else if (elem.getName().equals(minCol)) {
				colMin = elem;
			}
			else if (elem.getName().equals(maxCol)) {
				colMax = elem;
			}
			else if (elem.getName().equals(tolCol)) {
				colTolerance = elem;
			}

		}

		// axis
		if (lastLevel != null) {	
			int lastLevelIndex = lastLevel.getParent().getChildren().indexOf(lastLevel);
			for(int i = 0 ; i <= lastLevelIndex ; i++) {
				Level level = lastLevel.getParent().getChildren().get(i);
				
				int index = -1;
				LevelData t = new LevelData();
				t.setLevel(level);
				index = axis.indexOf(t);
				LevelData data = new LevelData();
				data.setLevel(level);
				if(index > -1) {
					data = axis.get(index);
				}
				
				if (elem.getName().equals(level.getColumnName())) {
					data.setLabel(elem);
				}
				if (elem.getName().equals(level.getColumnId())) {
					data.setId(elem);
				}				
				if (elem.getName().equals(level.getGeoColumnId())) {
					data.setGeo(elem);
				}
				if(index < 0) {
					axis.add(data);
				}
			}
		}
	}

	private class LevelData {
		private Level level;
		private IDataStreamElement label;
		private IDataStreamElement id;
		private IDataStreamElement geo;

		public Level getLevel() {
			return level;
		}

		public void setLevel(Level level) {
			this.level = level;
		}

		public IDataStreamElement getLabel() {
			return label;
		}

		public void setLabel(IDataStreamElement label) {
			this.label = label;
		}

		public IDataStreamElement getId() {
			return id;
		}

		public void setId(IDataStreamElement id) {
			this.id = id;
		}

		public IDataStreamElement getGeo() {
			return geo;
		}

		public void setGeo(IDataStreamElement geo) {
			this.geo = geo;
		}
		
		@Override
		public boolean equals(Object obj) {
			return level.getId() == ((LevelData)obj).getLevel().getId();
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}

}
