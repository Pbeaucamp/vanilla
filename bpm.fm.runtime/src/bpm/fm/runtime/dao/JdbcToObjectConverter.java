package bpm.fm.runtime.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.AbstractFactTable;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.CalculatedFactTable;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.FactTableObjectives;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.MetricLinkedItem;
import bpm.fm.api.model.MetricMap;

public class JdbcToObjectConverter {

	public static List<Metric> convertToMetric(ResultSet rs) throws Exception {
		List<Metric> metrics = new ArrayList<Metric>();
		
		while (rs.next()) {
			
			Metric metric = new Metric();
			metric.setId(rs.getInt("id"));
			metric.setName(rs.getString("metric_name"));
			metric.setCreationDate(rs.getDate("creation_date"));
			metric.setDescription(rs.getString("metric_description"));
			metric.setEtlName(rs.getString("etl_name"));
			metric.setOwner(rs.getString("owner"));
			metric.setResponsible(rs.getString("responsible"));
			metric.setReportName(rs.getString("report_id"));
			metric.setOperator(rs.getString("metric_operator"));
			metric.setDirection(rs.getString("metric_direction"));
			metric.setMetricType(rs.getString("metric_type"));
			metric.setLinkedItemId(rs.getInt("linked_item_id") != 0 ? rs.getInt("linked_item_id") : null);
			
			metrics.add(metric);		
		}
		
		return metrics;
	}

	public static AbstractFactTable convertToFactTable(ResultSet rs, String metricType) throws Exception {
		if(metricType.equals(Metric.METRIC_TYPES.get(Metric.TYPE_CALCULATED)) || metricType.equals(Metric.METRIC_TYPES.get(Metric.TYPE_KAPLAN))) {
			CalculatedFactTable factTable = new CalculatedFactTable();
			
			while (rs.next()) {
				factTable.setId(rs.getInt("id"));
				factTable.setMetricId(rs.getInt("metric_id"));
				factTable.setCalculation(rs.getString("calculation"));
			}
			
			return factTable;
		}
		else {
			FactTable factTable = new FactTable();
			
			while (rs.next()) {
				factTable.setId(rs.getInt("id"));
				factTable.setMetricId(rs.getInt("metric_id"));
				factTable.setTableName(rs.getString("table_name"));
				factTable.setValueColumn(rs.getString("value_column"));
				factTable.setDatasourceId(rs.getInt("datasource_id"));
				factTable.setDateColumn(rs.getString("date_column"));
				factTable.setPeriodicity(rs.getString("periodicity"));
			}
			
			return factTable;
		}
		
	}

	public static FactTableObjectives convertToObjective(ResultSet rs) throws Exception {
		FactTableObjectives factTableObjectives = new FactTableObjectives();
		
		while (rs.next()) {
			factTableObjectives.setId(rs.getInt("id"));
			factTableObjectives.setFactTableId(rs.getInt("fact_table_id"));
			factTableObjectives.setObjectiveColumn(rs.getString("objective_column"));
			factTableObjectives.setTableName(rs.getString("table_name"));
			factTableObjectives.setMinColumn(rs.getString("min_column"));
			factTableObjectives.setMaxColumn(rs.getString("max_column"));
			factTableObjectives.setDateColumn(rs.getString("date_column"));
			factTableObjectives.setTolerance(rs.getString("tolerance"));
		}
		
		return factTableObjectives;
	}

	public static List<Axis> convertToAxis(ResultSet rs) throws Exception {
		List<Axis> axiss = new ArrayList<Axis>();
		
		while (rs.next()) {
			
			Axis axis = new Axis();
			axis.setId(rs.getInt("id"));
			axis.setName(rs.getString("axis_name"));
			axis.setCreationDate(rs.getDate("creation_date"));
			
			axiss.add(axis);		
		}
		
		return axiss;
	}

	public static List<Level> convertToLevels(ResultSet rs) throws Exception {
		List<Level> levels = new ArrayList<Level>();
		
		while (rs.next()) {
			
			Level level = new Level();
			
			level.setId(rs.getInt("id"));		
			level.setName(rs.getString("level_name"));
			level.setCreationDate(rs.getDate("creation_date"));
			level.setTableName(rs.getString("table_name"));
			level.setColumnName(rs.getString("column_name"));
			level.setColumnId(rs.getString("column_id"));
			level.setDatasourceId(rs.getInt("datasource_id"));
			level.setParentId(rs.getInt("parent_id"));
			level.setLevelIndex(rs.getInt("level_index"));
			level.setParentColumnId(rs.getString("parent_column_id"));
			level.setGeoColumnId(rs.getString("geo_column_id"));
			level.setMapDatasetId(rs.getInt("map_dataset_id")); //kevin
			
			levels.add(level);		
		}
		
		return levels;
	}

	public static List<FactTableAxis> convertToFactTableAxis(ResultSet rs) throws Exception {
		List<FactTableAxis> axiss = new ArrayList<FactTableAxis>();
		
		while (rs.next()) {
			
			FactTableAxis axis = new FactTableAxis();
			axis.setId(rs.getInt("id"));
			axis.setFactTableId(rs.getInt("fact_table_id"));
			axis.setAxisId(rs.getInt("axis_id"));
			axis.setColumnId(rs.getString("column_id"));
			axis.setObjectiveColumnId(rs.getString("objective_column_Id"));
			
			axiss.add(axis);
		}
		
		return axiss;
	}

	public static List<MetricMap> convertToMaps(ResultSet rs) throws Exception {
		List<MetricMap> maps = new ArrayList<MetricMap>();
		
		while (rs.next()) {
			
			MetricMap map = new MetricMap();
			
			map.setId(rs.getInt("id"));		
			map.setName(rs.getString("map_name"));
			map.setDesc(rs.getString("map_desc"));
			map.setType(rs.getString("map_type"));
			map.setMetricId(rs.getInt("metric_id"));
			map.setDatasourceId(rs.getInt("datasource_id"));
			map.setColumnZone(rs.getString("zone_column"));
			map.setColumnLatitude(rs.getString("latitude_column"));
			map.setColumnLongitude(rs.getString("longitude_column"));
			map.setTableName(rs.getString("table_name"));
			map.setLevelId(rs.getInt("level_id"));
			
			maps.add(map);
		}
		
		return maps;
	}

	public static List<MetricAction> convertToActions(ResultSet rs)  throws Exception {
		List<MetricAction> actions = new ArrayList<MetricAction>();
		
		while (rs.next()) {
			
			MetricAction action = new MetricAction();
			
			action.setId(rs.getInt("id"));		
			
			action.setName(rs.getString("action_name"));
			action.setDescription(rs.getString("action_description"));
			action.setStartDate(rs.getDate("start_date"));
			action.setEndDate(rs.getDate("end_date"));
			action.setFormula(rs.getString("action_formula"));
			action.setMetricId(rs.getInt("metric_id"));
			
			actions.add(action);
		}
		
		return actions;
	}

//	public static List<MetricAlert> convertToAlerts(ResultSet rs) throws Exception {
//		List<MetricAlert> alerts = new ArrayList<MetricAlert>();
//		
//		while (rs.next()) {
//			
//			MetricAlert alert = new MetricAlert();
//			
//			alert.setId(rs.getInt(1));		
//			alert.setName(rs.getString(2));
//			alert.setDescription(rs.getString(3));
//			alert.setMetricId(rs.getInt(4));
//			alert.setAxisId(rs.getInt(5));
//			alert.setLevelIndex(rs.getInt(6));
//			alert.setLevelValue(rs.getString(7));
//			
//			MetricAlertCondition condition = new MetricAlertCondition();
//			
//			condition.setId(rs.getInt(8));
//			condition.setType(rs.getString(9));
//			condition.setLeftField(rs.getString(10));
//			condition.setOperator(rs.getString(11));
//			condition.setRightField(rs.getString(12));
//			condition.setMissingType(rs.getString(13));
//			condition.setStateType(rs.getString(14));
//			condition.setAlertId(rs.getInt(15));
//			
//			MetricAlertEvent event = new MetricAlertEvent();
//			
//			event.setId(rs.getInt(16));
//			event.setType(rs.getString(17));
//			event.setAlertId(rs.getInt(18));
//			event.setRecipients(rs.getString(19));
//			event.setMessage(rs.getString(20));
//			event.setItemId(rs.getInt(21));
//			
//			alert.setCondition(condition);
//			alert.setEvent(event);
//			
//			alerts.add(alert);
//		}
//		
//		return alerts;
//	}

	public static List<MetricLinkedItem> convertToLinked(ResultSet rs) throws Exception {
		List<MetricLinkedItem> linked = new ArrayList<MetricLinkedItem>();
		
		while (rs.next()) {
			
			MetricLinkedItem item = new MetricLinkedItem();
			
			item.setId(rs.getInt("id"));
			
			item.setMetricId(rs.getInt("metric_id"));
			item.setItemId(rs.getInt("item_id"));
			item.setItemName(rs.getString("item_name"));
			item.setType(rs.getString("item_type"));
			
			linked.add(item);
		}
		
		return linked;
	}

	
	
}
