package bpm.fm.runtime.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import bpm.fm.api.model.utils.AxisQueries;
import bpm.fm.api.model.utils.MetricSqlQueries;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;

public class DatabaseElementSqlGenerator {

	/**
	 * Generate the create Scripts for the metric
	 * @param metric 
	 * @return A map with the factTable/factTableObjective/axis and the corresponding sql.
	 * @throws Exception
	 */
	public static MetricSqlQueries generateCreateTableSql(Metric metric) throws Exception {
		
		if(!(((FactTable)metric.getFactTable()).getDatasource().getObject() instanceof DatasourceJdbc)) {
			throw new Exception("The metric datasource is not on JDBC");
		}
		
		MetricSqlQueries sqlCreateQueries = new MetricSqlQueries(metric);
		Datasource datasource = ((FactTable)metric.getFactTable()).getDatasource();
		DatasourceJdbc datasourceJdbc = (DatasourceJdbc) datasource.getObject();
		if(datasourceJdbc.getDriver().contains("mysql")) {
			generateFactQuery(metric, sqlCreateQueries);
		}
		else {
			generatePostgresQuery(metric, sqlCreateQueries);
		}
		
		return sqlCreateQueries;
	}

	private static void generatePostgresQuery(Metric metric, MetricSqlQueries sqlCreateQueries) throws Exception {
		StringBuilder factQuery = new StringBuilder();
		
		Datasource datasource = ((FactTable)metric.getFactTable()).getDatasource();
		DatasourceJdbc datasourceJdbc = (DatasourceJdbc) datasource.getObject();
		VanillaJdbcConnection factTableConnection = null;
		try {
			factTableConnection = ConnectionManager.getInstance().getJdbcConnection(datasourceJdbc);
			
			//if the factTable doesn't exists
			if(needCreate(factTableConnection, ((FactTable)metric.getFactTable()).getTableName())) {
				
				factQuery.append("Create table \"");
				factQuery.append(((FactTable)metric.getFactTable()).getTableName());
				factQuery.append("\" (\n");
				
				//create an auto-incremented id
				factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getTableName() + "_aiid\" serial primary key,\n");
				
				//add the metric column
				factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getValueColumn() + "\" decimal,\n");
				factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getDateColumn() + "\" timestamp NOT NULL\n");
				
				//add the objectives values
				if(((FactTable)metric.getFactTable()).getObjectives() != null && ((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(((FactTable)metric.getFactTable()).getTableName())) {
					factQuery.append("	,\"" + ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn() + "\" decimal,\n");
					factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getMinColumn() + "\" decimal,\n");
					factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn() + "\" decimal\n");
					if(((FactTable)metric.getFactTable()).getObjectives().getTolerance() != null && !((FactTable)metric.getFactTable()).getObjectives().getTolerance().isEmpty()) {
						factQuery.append("	,\"" + ((FactTable)metric.getFactTable()).getObjectives().getTolerance() + "\" decimal\n");	
					}
				}
				else {
					generatePostgresObjectivesQuery(metric, sqlCreateQueries, factTableConnection);
				}
				
				//add the axis foreign
				if(((FactTable)metric.getFactTable()).getFactTableAxis() != null && !((FactTable)metric.getFactTable()).getFactTableAxis().isEmpty()) {
					for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {						
						factQuery.append("	,\"" + factAxis.getColumnId() + "\" varchar(45) NOT NULL\n");					
					}
				}
				
				factQuery.append(")");
				
				String metricQuery = factQuery.toString();
				if(usefullQuery(metricQuery)) {
					sqlCreateQueries.setFactQuery(metricQuery);
				}
			}
			
			//if the fact table already exists, find the columns to add
			else {
				List<String> columnNames = new ArrayList<>();
				ResultSet rs = factTableConnection.getMetaData().getColumns(null, null, ((FactTable)metric.getFactTable()).getTableName(), null);
				
				while(rs.next()) {
					columnNames.add(rs.getString(4));
				}
				
				rs.close();
				
				factQuery.append("Alter table \"");
				factQuery.append(((FactTable)metric.getFactTable()).getTableName());
				factQuery.append("\" \n");
				
				boolean columnAdded = false;
				
				//check the metric columns
				if(!columnNames.contains(((FactTable)metric.getFactTable()).getValueColumn())) {
					factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getValueColumn() + "\" decimal\n");
					columnAdded = true;
				}
				if(!columnNames.contains(((FactTable)metric.getFactTable()).getDateColumn())) {
					if(!columnAdded) {
						columnAdded = true;
					}
					else {
						factQuery.append(",");
					}
					factQuery.append("ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getDateColumn() + "\" timestamp NOT NULL\n");
				}
				
				//check the objectives values
				if(((FactTable)metric.getFactTable()).getObjectives() != null && ((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(((FactTable)metric.getFactTable()).getTableName())) {
					
					if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getMaxColumn())) {
						if(!columnAdded) {
							columnAdded = true;
						}
						else {
							factQuery.append(",");
						}
						factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn() + "\" decimal\n");
					}
					if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getMinColumn())) {
						if(!columnAdded) {
							columnAdded = true;
						}
						else {
							factQuery.append(",");
						}
						factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getMinColumn() + "\" decimal\n");
					}
					if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn())) {
						if(!columnAdded) {
							columnAdded = true;
						}
						else {
							factQuery.append(",");
						}
						factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn() + "\" decimal\n");
					}
					
				}
				else {
					generatePostgresObjectivesQuery(metric, sqlCreateQueries, factTableConnection);
				}
				
				//check the axis foreign
				if(((FactTable)metric.getFactTable()).getFactTableAxis() != null && !((FactTable)metric.getFactTable()).getFactTableAxis().isEmpty()) {
					for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {					
						
						if(!columnNames.contains(factAxis.getColumnId())) {
							if(!columnAdded) {
								columnAdded = true;
							}
							else {
								factQuery.append(",");
							}
							factQuery.append(" ADD COLUMN \"" + factAxis.getColumnId() + "\" varchar(45) NOT NULL\n");
						}
						
					}
				}
				
				String metricQuery = factQuery.toString();
				if(usefullQuery(metricQuery)) {
					sqlCreateQueries.setFactQuery(metricQuery);
				}
			}
			
			if(((FactTable)metric.getFactTable()).getFactTableAxis() != null && !((FactTable)metric.getFactTable()).getFactTableAxis().isEmpty()) {
				for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
					generatePostgresAxisQuery(factAxis.getAxis(), sqlCreateQueries);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();	
			throw e;
		} finally {
			ConnectionManager.getInstance().returnJdbcConnection(factTableConnection);
		}
	}

	private static void generatePostgresAxisQuery(Axis axis, MetricSqlQueries sqlCreateQueries) throws Exception {
		
		AxisQueries axisQueries = new AxisQueries(axis);
		
		StringBuilder levelQuery = new StringBuilder();
		
		//Level previousLevel = null;
		List<Level> actualLevels = new ArrayList<Level>();
		boolean first = true;
		boolean needCreate = false;
		for(Level lvl : axis.getChildren()) {
			needCreate = false;
			boolean columnAdded = false;
			List<String> columnNames = new ArrayList<>();
			Datasource datasource = lvl.getDatasource();
			DatasourceJdbc datasourceJdbc = (DatasourceJdbc) datasource.getObject();
			VanillaJdbcConnection lvlConnection = null;
			try {
				lvlConnection = ConnectionManager.getInstance().getJdbcConnection(datasourceJdbc);
				if(needCreate(lvlConnection, lvl.getTableName())) {
					needCreate = true;
				}
				else {
					
					ResultSet rs = lvlConnection.getMetaData().getColumns(null, null, lvl.getTableName(), null);
					
					while(rs.next()) {
						columnNames.add(rs.getString(4));
					}
					
					rs.close();
				}
			
				boolean wasFirst = false;
				if(first) {
					
					if(needCreate) {
					
						levelQuery.append("Create table \"");
						levelQuery.append(lvl.getTableName());
						levelQuery.append("\" (\n");
						
						levelQuery.append("	\"" + lvl.getTableName() + "_aiid\" serial primary key,\n");
						
						levelQuery.append("	\"" + lvl.getColumnId() + "\" int NOT NULL,\n");
					
					}
					else {
						levelQuery.append("Alter table \"");
						levelQuery.append(lvl.getTableName());
						levelQuery.append("\" \n");
						
						if(!columnNames.contains(lvl.getColumnId())) {
							levelQuery.append(" ADD COLUMN \"" + lvl.getColumnId() + "\" int NOT NULL\n");
							columnAdded = true;
						}
					}
					wasFirst = true;
					first = false;
				}
				else {
					
					if(lvl.getParentColumnId() != null && !lvl.getParentColumnId().isEmpty()) {
						if(needCreate) {
							//levelQuery.append("	PRIMARY KEY (\"" + previousLevel.getTableName() + "_aiid\")\n");
						}
						levelQuery.append(")");
						
						String previousQuery = levelQuery.toString();
						if(usefullQuery(previousQuery)) {
							axisQueries.addLevelQuery(actualLevels, previousQuery);
						}
						
						actualLevels = new ArrayList<Level>();
						levelQuery = new StringBuilder();
						
						if(needCreate) {
						
							levelQuery.append("Create table \"");
							levelQuery.append(lvl.getTableName());
							levelQuery.append("\" (\n");
							
							levelQuery.append("	\"" + lvl.getTableName() + "_aiid\" serial primary key,\n");
							
							levelQuery.append("	\"" + lvl.getParentColumnId() + "\" int NOT NULL,\n");
							levelQuery.append("	\"" + lvl.getColumnId() + "\" int NOT NULL,\n");
						}
						
						else {
							levelQuery.append("Alter table \"");
							levelQuery.append(lvl.getTableName());
							levelQuery.append("\" \n");
							
							if(!columnNames.contains(lvl.getColumnId())) {
								levelQuery.append(" ADD COLUMN \"" + lvl.getColumnId() + "\" int NOT NULL\n");
								columnAdded = true;
							}
						}
					}
					
				}
				
				if(needCreate ) {
					if(!wasFirst) {
						levelQuery.append(",");
					}
					levelQuery.append("	\"" + lvl.getColumnName() + "\" varchar(255) NOT NULL\n");
				}
				
				else {
					if(!columnNames.contains(lvl.getColumnName())) {
						if(columnAdded) {
							levelQuery.append(",");
						}
						levelQuery.append(" ADD COLUMN \"" + lvl.getColumnName() + "\" int NOT NULL\n");
					}
				}
	
				//previousLevel = lvl;
				actualLevels.add(lvl);
				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				ConnectionManager.getInstance().returnJdbcConnection(lvlConnection);
			}
		}
		
		if(needCreate) {
			//levelQuery.append("	PRIMARY KEY (\"" + previousLevel.getTableName() + "_aiid\")\n");
		}
		levelQuery.append(")");
		String previousQuery = levelQuery.toString();
		if(usefullQuery(previousQuery)) {
			axisQueries.addLevelQuery(actualLevels, previousQuery);
			sqlCreateQueries.getAxisQueries().add(axisQueries);
		}
	}

	private static void generatePostgresObjectivesQuery(Metric metric, MetricSqlQueries sqlCreateQueries, VanillaJdbcConnection factTableConnection) throws Exception {
		StringBuilder factQuery = new StringBuilder();
		
		if(needCreate(factTableConnection, ((FactTable)metric.getFactTable()).getObjectives().getTableName())) {
					
			factQuery.append("Create table \"");
			factQuery.append(((FactTable)metric.getFactTable()).getObjectives().getTableName());
			factQuery.append("\" (\n");
			
			//create an auto-incremented id
			factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getTableName() + "_aiid\" serial primary key,\n");
			
			//add the objective column
			factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getDateColumn() + "\" timestamp NOT NULL,\n");
			factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn() + "\" decimal,\n");
			factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getMinColumn() + "\" decimal,\n");
			factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn() + "\" decimal\n");
			if(((FactTable)metric.getFactTable()).getObjectives().getTolerance() != null && !((FactTable)metric.getFactTable()).getObjectives().getTolerance().isEmpty()) {
				factQuery.append("	,\"" + ((FactTable)metric.getFactTable()).getObjectives().getTolerance() + "\" decimal\n");	
			}
			
			//add the axis foreign
			if(((FactTable)metric.getFactTable()).getFactTableAxis() != null && !((FactTable)metric.getFactTable()).getFactTableAxis().isEmpty()) {
				for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
					
					factQuery.append("	,\"" + factAxis.getObjectiveColumnId() + "\" varchar(45) NOT NULL\n");
				}
			}
			
			factQuery.append(")");
			
			String objQuery = factQuery.toString();
			if(usefullQuery(objQuery)) {
				sqlCreateQueries.setObjectiveQuery(objQuery);
			}
						
		}
		else {
			List<String> columnNames = new ArrayList<>();
			ResultSet rs = factTableConnection.getMetaData().getColumns(null, null, ((FactTable)metric.getFactTable()).getObjectives().getTableName(), null);
			
			while(rs.next()) {
				columnNames.add(rs.getString(4));
			}
			
			rs.close();
			
			factQuery.append("Alter table \"");
			factQuery.append(((FactTable)metric.getFactTable()).getObjectives().getTableName());
			factQuery.append("\" \n");
			
			boolean columnAdded = false;
			
			if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getMaxColumn())) {
				factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn() + "\" decimal\n");
				columnAdded = true;
			}
			if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getMinColumn())) {
				if(!columnAdded) {
					columnAdded = true;
				}
				else {
					factQuery.append(",");
				}
				factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getMinColumn() + "\" decimal\n");
			}
			if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn())) {
				if(!columnAdded) {
					columnAdded = true;
				}
				else {
					factQuery.append(",");
				}
				factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn() + "\" decimal\n");
			}
			
			if(((FactTable)metric.getFactTable()).getFactTableAxis() != null && !((FactTable)metric.getFactTable()).getFactTableAxis().isEmpty()) {
				for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {					
					
					if(!columnNames.contains(factAxis.getObjectiveColumnId())) {
						if(!columnAdded) {
							columnAdded = true;
						}
						else {
							factQuery.append(",");
						}
						factQuery.append(" ADD COLUMN \"" + factAxis.getObjectiveColumnId() + "\" varchar(45) NOT NULL\n");
					}
					
				}
			}
			
			String objQuery = factQuery.toString();
			
			if(usefullQuery(objQuery)) {
				sqlCreateQueries.setObjectiveQuery(objQuery);
			}
		}
	}

	/**
	 * Generate the sql create query for the metric table
	 * @param metric
	 * @param sqlCreateQueries
	 */
	private static void generateFactQuery(Metric metric, MetricSqlQueries sqlCreateQueries) throws Exception {
		StringBuilder factQuery = new StringBuilder();
		
		Datasource datasource = ((FactTable)metric.getFactTable()).getDatasource();
		DatasourceJdbc datasourceJdbc = (DatasourceJdbc) datasource.getObject();
		VanillaJdbcConnection factTableConnection = null;
		try {
			factTableConnection = ConnectionManager.getInstance().getJdbcConnection(datasourceJdbc);
			
			//if the factTable doesn't exists
			if(needCreate(factTableConnection, ((FactTable)metric.getFactTable()).getTableName())) {
				
				factQuery.append("Create table \"");
				factQuery.append(((FactTable)metric.getFactTable()).getTableName());
				factQuery.append("\" (\n");
				
				//create an auto-incremented id
				factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getTableName() + "_aiid\" int NOT NULL AUTO_INCREMENT,\n");
				
				//add the metric column
				factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getValueColumn() + "\" double,\n");
				factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getDateColumn() + "\" datetime NOT NULL,\n");
				
				//add the objectives values
				if(((FactTable)metric.getFactTable()).getObjectives() != null && ((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(((FactTable)metric.getFactTable()).getTableName())) {
					factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn() + "\" double,\n");
					factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getMinColumn() + "\" double,\n");
					factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn() + "\" double,\n");
				}
				else {
					generateObjectivesQuery(metric, sqlCreateQueries, factTableConnection);
				}
				
				//add the axis foreign
				if(((FactTable)metric.getFactTable()).getFactTableAxis() != null && !((FactTable)metric.getFactTable()).getFactTableAxis().isEmpty()) {
					for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {						
						factQuery.append("	\"" + factAxis.getColumnId() + "\" varchar(45) NOT NULL,\n");					
					}
				}
				
				//define the pk
				factQuery.append("	PRIMARY KEY (\"" + ((FactTable)metric.getFactTable()).getTableName() + "_aiid\")\n");
				
				factQuery.append(")");
				
				String metricQuery = factQuery.toString();
				if(usefullQuery(metricQuery)) {
					sqlCreateQueries.setFactQuery(metricQuery);
				}
			}
			
			//if the fact table already exists, find the columns to add
			else {
				List<String> columnNames = new ArrayList<>();
				ResultSet rs = factTableConnection.getMetaData().getColumns(null, null, ((FactTable)metric.getFactTable()).getTableName(), null);
				
				while(rs.next()) {
					columnNames.add(rs.getString(4));
				}
				
				rs.close();
				
				factQuery.append("Alter table \"");
				factQuery.append(((FactTable)metric.getFactTable()).getTableName());
				factQuery.append("\" \n");
				
				boolean columnAdded = false;
				
				//check the metric columns
				if(!columnNames.contains(((FactTable)metric.getFactTable()).getValueColumn())) {
					factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getValueColumn() + "\" double\n");
					columnAdded = true;
				}
				if(!columnNames.contains(((FactTable)metric.getFactTable()).getDateColumn())) {
					if(!columnAdded) {
						columnAdded = true;
					}
					else {
						factQuery.append(",");
					}
					factQuery.append("ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getDateColumn() + "\" datetime NOT NULL\n");
				}
				
				//check the objectives values
				if(((FactTable)metric.getFactTable()).getObjectives() != null && ((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(((FactTable)metric.getFactTable()).getTableName())) {
					
					if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getMaxColumn())) {
						if(!columnAdded) {
							columnAdded = true;
						}
						else {
							factQuery.append(",");
						}
						factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn() + "\" double\n");
					}
					if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getMinColumn())) {
						if(!columnAdded) {
							columnAdded = true;
						}
						else {
							factQuery.append(",");
						}
						factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getMinColumn() + "\" double\n");
					}
					if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn())) {
						if(!columnAdded) {
							columnAdded = true;
						}
						else {
							factQuery.append(",");
						}
						factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn() + "\" double\n");
					}
					
				}
				else {
					generateObjectivesQuery(metric, sqlCreateQueries, factTableConnection);
				}
				
				//check the axis foreign
				if(((FactTable)metric.getFactTable()).getFactTableAxis() != null && !((FactTable)metric.getFactTable()).getFactTableAxis().isEmpty()) {
					for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {					
						
						if(!columnNames.contains(factAxis.getColumnId())) {
							if(!columnAdded) {
								columnAdded = true;
							}
							else {
								factQuery.append(",");
							}
							factQuery.append(" ADD COLUMN \"" + factAxis.getColumnId() + "\" varchar(45) NOT NULL\n");
						}
						
					}
				}
				
				String metricQuery = factQuery.toString();
				if(usefullQuery(metricQuery)) {
					sqlCreateQueries.setFactQuery(metricQuery);
				}
			}
			
			if(((FactTable)metric.getFactTable()).getFactTableAxis() != null && !((FactTable)metric.getFactTable()).getFactTableAxis().isEmpty()) {
				for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
					generateAxisQuery(factAxis.getAxis(), sqlCreateQueries);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();	
			throw e;
		} finally {
			ConnectionManager.getInstance().returnJdbcConnection(factTableConnection);
		}

	}

	private static boolean usefullQuery(String metricQuery) {
		
		if(metricQuery.toLowerCase().startsWith("alter table")) {
			return metricQuery.contains("ADD COLUMN");
		}
		
		return true;
	}

	private static boolean needCreate(VanillaJdbcConnection connection, String tableName) throws SQLException {
		
		ResultSet rs = connection.getJdbcConnection().getMetaData().getTables(null, null, tableName, null);
		
		while(rs.next()) {
			try {
				String table = rs.getString(3);
				if(table.equals(tableName.toLowerCase())) {
					return false;
				}
			} catch(Exception e) {
				e.printStackTrace();
				rs.close();
				throw e;
			} 
		}
		
		try {
			rs.close();
		} catch (Exception e) {
		}
		
		return true;
	}

	/**
	 * generate the sql create query for the axis
	 * @param axis
	 * @param sqlCreateQueries
	 * @param factTableConnection 
	 * @throws Exception 
	 */
	private static void generateAxisQuery(Axis axis, MetricSqlQueries sqlCreateQueries) throws Exception {
		
		AxisQueries axisQueries = new AxisQueries(axis);
		
		StringBuilder levelQuery = new StringBuilder();
		
		Level previousLevel = null;
		List<Level> actualLevels = new ArrayList<Level>();
		boolean first = true;
		boolean needCreate = false;
		for(Level lvl : axis.getChildren()) {
			needCreate = false;
			boolean columnAdded = false;
			List<String> columnNames = new ArrayList<>();
			Datasource datasource = lvl.getDatasource();
			DatasourceJdbc datasourceJdbc = (DatasourceJdbc) datasource.getObject();
			VanillaJdbcConnection lvlConnection = null;
			try {
				lvlConnection = ConnectionManager.getInstance().getJdbcConnection(datasourceJdbc);
				if(needCreate(lvlConnection, lvl.getTableName())) {
					needCreate = true;
				}
				else {
					
					ResultSet rs = lvlConnection.getMetaData().getColumns(null, null, lvl.getTableName(), null);
					
					while(rs.next()) {
						columnNames.add(rs.getString(4));
					}
					
					rs.close();
				}
			
				
				if(first) {
					
					if(needCreate) {
					
						levelQuery.append("Create table \"");
						levelQuery.append(lvl.getTableName());
						levelQuery.append("\" (\n");
						
						levelQuery.append("	\"" + lvl.getTableName() + "_aiid\" int NOT NULL AUTO_INCREMENT,\n");
						
						levelQuery.append("	\"" + lvl.getColumnId() + "\" int NOT NULL,\n");
					
					}
					else {
						levelQuery.append("Alter table \"");
						levelQuery.append(lvl.getTableName());
						levelQuery.append("\" \n");
						
						if(!columnNames.contains(lvl.getColumnId())) {
							levelQuery.append(" ADD COLUMN \"" + lvl.getColumnId() + "\" int NOT NULL\n");
							columnAdded = true;
						}
					}
					
					first = false;
				}
				else {
					
					if(lvl.getParentColumnId() != null && !lvl.getParentColumnId().isEmpty()) {
						if(needCreate) {
							levelQuery.append("	PRIMARY KEY (\"" + previousLevel.getTableName() + "_aiid\")\n");
						}
						levelQuery.append(")");
						
						String previousQuery = levelQuery.toString();
						if(usefullQuery(previousQuery)) {
							axisQueries.addLevelQuery(actualLevels, previousQuery);
						}
						
						actualLevels = new ArrayList<Level>();
						levelQuery = new StringBuilder();
						
						if(needCreate) {
						
							levelQuery.append("Create table \"");
							levelQuery.append(lvl.getTableName());
							levelQuery.append("\" (\n");
							
							levelQuery.append("	\"" + lvl.getTableName() + "_aiid\" int NOT NULL AUTO_INCREMENT,\n");
							
							levelQuery.append("	\"" + lvl.getParentColumnId() + "\" int NOT NULL,\n");
							levelQuery.append("	\"" + lvl.getColumnId() + "\" int NOT NULL,\n");
						}
						
						else {
							levelQuery.append("Alter table \"");
							levelQuery.append(lvl.getTableName());
							levelQuery.append("\" \n");
							
							if(!columnNames.contains(lvl.getColumnId())) {
								levelQuery.append(" ADD COLUMN \"" + lvl.getColumnId() + "\" int NOT NULL\n");
								columnAdded = true;
							}
						}
					}
					
				}
				
				if(needCreate ) {
					levelQuery.append("	\"" + lvl.getColumnName() + "\" varchar(255) NOT NULL,\n");
				}
				
				else {
					if(!columnNames.contains(lvl.getColumnName())) {
						if(columnAdded) {
							levelQuery.append(",");
						}
						levelQuery.append(" ADD COLUMN \"" + lvl.getColumnName() + "\" int NOT NULL\n");
					}
				}
	
				previousLevel = lvl;
				actualLevels.add(lvl);
				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				ConnectionManager.getInstance().returnJdbcConnection(lvlConnection);
			}
		}
		
		if(needCreate) {
			levelQuery.append("	PRIMARY KEY (\"" + previousLevel.getTableName() + "_aiid\")\n");
		}
		levelQuery.append(")");
		String previousQuery = levelQuery.toString();
		if(usefullQuery(previousQuery)) {
			axisQueries.addLevelQuery(actualLevels, previousQuery);
			sqlCreateQueries.getAxisQueries().add(axisQueries);
		}
	}

	/**
	 * generate the sql create query for the metric objectives
	 * @param metric
	 * @param sqlCreateQueries
	 * @param factTableConnection 
	 */
	private static void generateObjectivesQuery(Metric metric, MetricSqlQueries sqlCreateQueries, VanillaJdbcConnection factTableConnection) throws Exception {
		StringBuilder factQuery = new StringBuilder();
		
		if(needCreate(factTableConnection, ((FactTable)metric.getFactTable()).getObjectives().getTableName())) {
					
			factQuery.append("Create table \"");
			factQuery.append(((FactTable)metric.getFactTable()).getObjectives().getTableName());
			factQuery.append("\" (\n");
			
			//create an auto-incremented id
			factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getTableName() + "_aiid\" int NOT NULL AUTO_INCREMENT,\n");
			
			//add the objective column
			factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getDateColumn() + "\" datetime NOT NULL,\n");
			factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn() + "\" double,\n");
			factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getMinColumn() + "\" double,\n");
			factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn() + "\" double,\n");
			if(((FactTable)metric.getFactTable()).getObjectives().getTolerance() != null && !((FactTable)metric.getFactTable()).getObjectives().getTolerance().isEmpty()) {
				factQuery.append("	\"" + ((FactTable)metric.getFactTable()).getObjectives().getTolerance() + "\" double,\n");	
			}
			
			//add the axis foreign
			if(((FactTable)metric.getFactTable()).getFactTableAxis() != null && !((FactTable)metric.getFactTable()).getFactTableAxis().isEmpty()) {
				for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
					
					factQuery.append("	\"" + factAxis.getObjectiveColumnId() + "\" varchar(45) NOT NULL,\n");
				}
			}
			
			//define the pk
			factQuery.append("	PRIMARY KEY (\"" + ((FactTable)metric.getFactTable()).getObjectives().getTableName() + "_aiid\")\n");
			
			factQuery.append(")");
			
			String objQuery = factQuery.toString();
			if(usefullQuery(objQuery)) {
				sqlCreateQueries.setObjectiveQuery(objQuery);
			}
						
		}
		else {
			List<String> columnNames = new ArrayList<>();
			ResultSet rs = factTableConnection.getMetaData().getColumns(null, null, ((FactTable)metric.getFactTable()).getObjectives().getTableName(), null);
			
			while(rs.next()) {
				columnNames.add(rs.getString(4));
			}
			
			rs.close();
			
			factQuery.append("Alter table \"");
			factQuery.append(((FactTable)metric.getFactTable()).getObjectives().getTableName());
			factQuery.append("\" \n");
			
			boolean columnAdded = false;
			
			if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getMaxColumn())) {
				factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn() + "\" double\n");
				columnAdded = true;
			}
			if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getMinColumn())) {
				if(!columnAdded) {
					columnAdded = true;
				}
				else {
					factQuery.append(",");
				}
				factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getMinColumn() + "\" double\n");
			}
			if(!columnNames.contains(((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn())) {
				if(!columnAdded) {
					columnAdded = true;
				}
				else {
					factQuery.append(",");
				}
				factQuery.append(" ADD COLUMN \"" + ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn() + "\" double\n");
			}
			
			if(((FactTable)metric.getFactTable()).getFactTableAxis() != null && !((FactTable)metric.getFactTable()).getFactTableAxis().isEmpty()) {
				for(FactTableAxis factAxis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {					
					
					if(!columnNames.contains(factAxis.getObjectiveColumnId())) {
						if(!columnAdded) {
							columnAdded = true;
						}
						else {
							factQuery.append(",");
						}
						factQuery.append(" ADD COLUMN \"" + factAxis.getObjectiveColumnId() + "\" varchar(45) NOT NULL\n");
					}
					
				}
			}
			
			String objQuery = factQuery.toString();
			
			if(usefullQuery(objQuery)) {
				sqlCreateQueries.setObjectiveQuery(objQuery);
			}
		}
		
	}

	public static HashMap<String, Exception> executeQueries(MetricSqlQueries queries) throws Exception {
		HashMap<String, Exception> errors = new HashMap<String, Exception>();
	
		//fact and objective queries
		Datasource datasource = ((FactTable)queries.getMetric().getFactTable()).getDatasource();
		DatasourceJdbc datasourceJdbc = (DatasourceJdbc) datasource.getObject();
		VanillaJdbcConnection factTableConnection = null;
		try {
			factTableConnection = ConnectionManager.getInstance().getJdbcConnection(datasourceJdbc);
		
			String factQuery = queries.getFactQuery();
			String objQuery = queries.getObjectiveQuery();
			
			if(factQuery != null && !factQuery.isEmpty()) {
				VanillaPreparedStatement stmt = null;
				try {
					stmt = factTableConnection.prepareQuery(factQuery);
					stmt.executeUpdate();
				} catch(Exception e) {
					Exception basic = new Exception(e.getMessage());
					errors.put(factQuery, basic);
				} finally {
					if(stmt != null) {
						stmt.close();
					}
				}
			}
			if(objQuery != null && !objQuery.isEmpty()) {
				VanillaPreparedStatement stmt = null;
				try {
					stmt = factTableConnection.prepareQuery(objQuery);
					stmt.executeUpdate();
				} catch(Exception e) {
					Exception basic = new Exception(e.getMessage());
					errors.put(objQuery, basic);
				} finally {
					if(stmt != null) {
						stmt.close();
					}
				}
			}
			
		} finally {
			ConnectionManager.getInstance().returnJdbcConnection(factTableConnection);
		}
		
		//axis queries
		for(AxisQueries axisQuery : queries.getAxisQueries()) {
			for(List<Level> lvls : axisQuery.getLevelQueries().keySet()) {
				String query = axisQuery.getLevelQueries().get(lvls);
				
				Datasource ds = lvls.get(0).getDatasource();
				DatasourceJdbc dsJdbc = (DatasourceJdbc) ds.getObject();
				
				VanillaJdbcConnection axisConnection = null;
				
				try {
					axisConnection = ConnectionManager.getInstance().getJdbcConnection(dsJdbc);
				
					VanillaPreparedStatement stmt = null;
					try {
						stmt = axisConnection.prepareQuery(query);
						stmt.executeUpdate();
					} catch(Exception e) {
						Exception basic = new Exception(e.getMessage());
						errors.put(query, basic);
					} finally {
						if(stmt != null) {
							stmt.close();
						}
					}
					
				} finally {
					ConnectionManager.getInstance().returnJdbcConnection(axisConnection);
				}
				
			}
		}
		
		return errors;
	}
	
}
