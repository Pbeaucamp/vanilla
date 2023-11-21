package bpm.fm.runtime.dao;

import java.sql.ResultSet;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.fm.api.model.AbstractFactTable;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.CalculatedFactTable;
import bpm.fm.api.model.CalculatedFactTableMetric;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.FactTableObjectives;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.MetricLinkedItem;
import bpm.fm.api.model.MetricMap;

public class FreeMetricsJdbc {
	
	private String url;
	private String login;
	private String password;
	private String driverClass;

	private FreeMetricsDao dao;

	public FreeMetricsJdbc(String url, String login, String password, String driverClass, FreeMetricsDao dao) {
		this.url = url;
		this.login = login;
		this.password = password;
		this.driverClass = driverClass;
		this.dao = dao;
		
		dao.setFreeMetricsJdbc(this);
	}
	
	public List<Metric> getMetrics(List<Integer> metricId) throws Exception {
		
		StringBuilder buf = new StringBuilder();
		
		buf.append("select ");
		buf.append("id, ");
		buf.append("metric_name, ");
		buf.append("creation_date, ");
		buf.append("metric_description, ");
		buf.append("etl_name, ");
		buf.append("owner, ");
		buf.append("responsible, ");
		buf.append("report_id, ");
		buf.append("metric_operator, ");
		buf.append("metric_direction, ");
		buf.append("metric_type, ");
		buf.append("linked_item_id ");
		buf.append("from fm_metric ");
		if(metricId != null) {
			StringBuilder metricIds = new StringBuilder();
			metricIds.append("(");
			boolean first = true;
			for(Integer i : metricId) {
				if(first) {
					first = false;
				}
				else {
					metricIds.append(",");
				}
				metricIds.append(i);
			}
			buf.append("where id in " + metricIds + ")");	
		}
		
		String query = buf.toString();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<Metric> metrics = null;
		
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			
			stmt = connection.prepareQuery(query);
			rs = stmt.executeQuery();
		
			metrics = JdbcToObjectConverter.convertToMetric(rs);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
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
		
		for(Metric m : metrics) {
			m.setFactTable(getFactTableByMetric(m));			
			//m.setAlerts(getMetricAlerts(m));
//			m.setMetricActions(getMetricActions(m.getId()));
//			m.setMaps(getMaps(m.getId()));
//			m.setLinkedItems(getLinkedItems(m.getId()));
		}
		
		
		return metrics;
	}
	
	private List<MetricLinkedItem> getLinkedItems(int metricId) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		buf.append("select ");
		buf.append("id, ");
		buf.append("metric_id, ");
		buf.append("item_id, ");
		buf.append("item_name, ");
		buf.append("item_type ");
		buf.append("from fm_metric_linked_item ");
		buf.append("where metric_id = " + metricId);
		
		String query = buf.toString();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<MetricLinkedItem> linked = null;
		
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			
			stmt = connection.prepareQuery(query);
			rs = stmt.executeQuery();
		
			linked = JdbcToObjectConverter.convertToLinked(rs);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
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
		
		return linked;
	}

//	private List<MetricAlert> getMetricAlerts(Metric metric) throws Exception {
//		StringBuilder buf = new StringBuilder();
//		
//		buf.append("select ");
//		buf.append("a.id, ");
//		buf.append("a.alert_name, ");
//		buf.append("a.alert_description, ");
//		buf.append("a.metric_id, ");
//		buf.append("a.axis_id, ");
//		buf.append("a.level_index, ");
//		buf.append("a.level_value, ");
//		buf.append("c.id, ");
//		buf.append("c.alert_type, ");
//		buf.append("c.left_field, ");
//		buf.append("c.operator, ");
//		buf.append("c.right_field, ");
//		buf.append("c.missing_type, ");
//		buf.append("c.state_type, ");
//		buf.append("c.alert_id, ");
//		buf.append("e.id, ");
//		buf.append("e.event_type, ");
//		buf.append("e.alert_id, ");
//		buf.append("e.recipients, ");
//		buf.append("e.alert_message, ");
//		buf.append("e.item_id ");
//		buf.append("from fm_alert a, fm_alert_condition c, fm_alert_event e");
//		buf.append(" where metric_id = " + metric.getId());
//		buf.append(" and a.id = c.alert_id");
//		buf.append(" and a.id = e.alert_id");
//		
//		String query = buf.toString();
//		
//		VanillaJdbcConnection connection = null;
//		VanillaPreparedStatement stmt = null;
//		ResultSet rs = null;
//		
//		List<MetricAlert> alerts = null;
//		
//		try {
//			connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
//			
//			stmt = connection.prepareQuery(query);
//			rs = stmt.executeQuery();
//		
//			alerts = JdbcToObjectConverter.convertToAlerts(rs);
//			
//		}
//		finally {
//			if(rs != null) {
//				rs.close();
//			}
//			if(stmt != null) {
//				stmt.close();
//			}
//			if(connection != null) {
//				ConnectionManager.getInstance().returnJdbcConnection(connection);
//			}
//		}
//		
//		for(MetricAlert alert : alerts) {
//			alert.setMetric(metric);
//		}
//		
//		return alerts;
//	}

	private List<MetricAction> getMetricActions(int metricId) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		buf.append("select ");
		buf.append("id, ");
		buf.append("action_name, ");
		buf.append("action_description, ");
		buf.append("start_date, ");
		buf.append("end_date, ");
		buf.append("action_formula, ");
		buf.append("metric_id ");
		buf.append("from fm_metric_action ");
		buf.append("where metric_id = " + metricId);
		
		String query = buf.toString();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<MetricAction> actions = null;
		
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			
			stmt = connection.prepareQuery(query);
			rs = stmt.executeQuery();
		
			actions = JdbcToObjectConverter.convertToActions(rs);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
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
		
		return actions;
	}

	private List<MetricMap> getMaps(int metricId) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		buf.append("select ");
		buf.append("id, ");
		buf.append("map_name, ");
		buf.append("map_desc, ");
		buf.append("map_type, ");
		buf.append("metric_id, ");
		buf.append("datasource_id, ");
		buf.append("zone_column, ");
		buf.append("latitude_column, ");
		buf.append("longitude_column, ");
		buf.append("table_name, ");
		buf.append("level_id ");
		buf.append("from fm_metric_map ");
		buf.append("where metric_id = " + metricId);
		
		String query = buf.toString();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<MetricMap> maps = null;
		
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			
			stmt = connection.prepareQuery(query);
			rs = stmt.executeQuery();
		
			maps = JdbcToObjectConverter.convertToMaps(rs);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
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
		
		for(MetricMap map : maps) {
			map.setLevel(getLevels(null, map.getLevelId()).get(0));
			map.setDatasource(dao.getDatasource(map.getDatasourceId()));
		}
		
		return maps;
	}

	public AbstractFactTable getFactTableByMetric(Metric metric) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		if(metric.getMetricType().equals(Metric.METRIC_TYPES.get(Metric.TYPE_CALCULATED)) || metric.getMetricType().equals(Metric.METRIC_TYPES.get(Metric.TYPE_KAPLAN))) {
			buf.append("select ");
			buf.append("id, ");
			buf.append("metric_id, ");
			buf.append("calculation ");
			buf.append("from fm_fact_table_calc ");
			buf.append("where metric_id = " + metric.getId());
		}
		else {
			buf.append("select ");
			buf.append("id, ");
			buf.append("metric_id, ");
			buf.append("table_name, ");
			buf.append("value_column, ");
			buf.append("datasource_id, ");
			buf.append("date_column, ");
			buf.append("periodicity ");
			buf.append("from fm_fact_table ");
			buf.append("where metric_id = " + metric.getId());
		}
		
		String query = buf.toString();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		AbstractFactTable factTable = null;
		
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			
			stmt = connection.prepareQuery(query);
			rs = stmt.executeQuery();
		
			factTable = JdbcToObjectConverter.convertToFactTable(rs, metric.getMetricType());
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
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
		
		if(metric.getMetricType().equals(Metric.METRIC_TYPES.get(Metric.TYPE_CALCULATED)) || metric.getMetricType().equals(Metric.METRIC_TYPES.get(Metric.TYPE_KAPLAN))) {
			
			CalculatedFactTable table = (CalculatedFactTable) factTable;
			
			List<CalculatedFactTableMetric> metrics = dao.find("From CalculatedFactTableMetric where fact_table_id = " + table.getId());
			for(CalculatedFactTableMetric m :metrics) {
				m.setMetric(dao.getMetric(m.getMetricId()));
			}
			table.setMetrics(metrics);
		}
		else {
			FactTable table = (FactTable) factTable;
			
			try {
				table.setFactTableAxis(getFactTableAxis(table.getId()));
				table.setObjectives(getObjectivesByFactTable(table.getId()));
				table.setDatasource(dao.getDatasource(table.getDatasourceId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return factTable;
	}

	private List<FactTableAxis> getFactTableAxis(int factTableId) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		buf.append("select ");
		buf.append("id, ");
		buf.append("fact_table_id, ");
		buf.append("column_id, ");
		buf.append("objective_column_Id, ");
		buf.append("axis_id ");
		buf.append("from fm_fact_table_axis ");
		buf.append("where fact_table_id = " + factTableId);
		
		String query = buf.toString();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<FactTableAxis> factTableAxis = null;
		
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			
			stmt = connection.prepareQuery(query);
			rs = stmt.executeQuery();
		
			factTableAxis = JdbcToObjectConverter.convertToFactTableAxis(rs);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
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
		
		for(FactTableAxis ax : factTableAxis) {
			ax.setAxis(getAxis(ax.getAxisId()).get(0));
		}
		
		return factTableAxis;
	}

	private FactTableObjectives getObjectivesByFactTable(int factTableId) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		buf.append("select ");
		buf.append("id, ");
		buf.append("fact_table_id, ");
		buf.append("objective_column, ");
		buf.append("table_name, ");
		buf.append("min_column, ");
		buf.append("max_column, ");
		buf.append("date_column, ");
		buf.append("tolerance ");
		buf.append("from fm_fact_table_objectives ");
		buf.append("where fact_table_id = " + factTableId);
		
		String query = buf.toString();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		FactTableObjectives objectives = null;
		
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			
			stmt = connection.prepareQuery(query);
			rs = stmt.executeQuery();
		
			objectives = JdbcToObjectConverter.convertToObjective(rs);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
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
		
		return objectives;
	}
	
	public List<Axis> getAxis() throws Exception {
		return getAxis(null);
	}
	
	public List<Axis> getAxis(Integer axisId) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		buf.append("select ");
		buf.append("id, ");
		buf.append("axis_name, ");
		buf.append("creation_date ");
		buf.append("from fm_axis ");
		if(axisId != null) {
			buf.append("where id = " + axisId);
		}
		
		String query = buf.toString();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<Axis> axis = null;
		
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			
			stmt = connection.prepareQuery(query);
			rs = stmt.executeQuery();
		
			axis = JdbcToObjectConverter.convertToAxis(rs);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
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
		
		for(Axis ax : axis) {
			ax.setChildren(getLevels(ax.getId(), null));
		}
		
		return axis;
	}

	private List<Level> getLevels(Integer axisId, Integer levelId) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		buf.append("select ");
		buf.append("id, ");
		buf.append("level_name, ");
		buf.append("creation_date, ");
		buf.append("table_name, ");
		buf.append("column_name, ");
		buf.append("column_id, ");
		buf.append("datasource_id, ");
		buf.append("parent_id, ");
		buf.append("level_index, ");
		buf.append("parent_column_id, ");
		buf.append("map_dataset_id, "); //kevin
		buf.append("geo_column_id ");
		buf.append("from fm_level ");
		if(axisId != null) {
			buf.append("where parent_id = " + axisId);
		}
		if(axisId == null && levelId != null) {
			buf.append("where id = " + levelId);
		}
		
		String query = buf.toString();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<Level> levels = null;
		
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(url, login, password, driverClass);
			
			stmt = connection.prepareQuery(query);
			rs = stmt.executeQuery();
		
			levels = JdbcToObjectConverter.convertToLevels(rs);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
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
		
		for(Level lvl : levels) {
			lvl.setDatasource(dao.getDatasource(lvl.getDatasourceId()));
		}
		
		return levels;
	}

	public boolean isPostgres() {
		return url.startsWith("jdbc:postgresql://");
	}
}
