package bpm.fm.runtime.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fm.api.model.utils.LoaderMetricValue;
import bpm.fm.runtime.FreeMetricsManagerComponent;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;

public class LoaderValueUpdater {

	public static List<Exception> updateValues(LoaderDataContainer dataContainer, FreeMetricsManagerComponent freeMetricsManagerComponent, String date) {
		List<Exception> listException = new ArrayList<Exception>();
		
		List<LoaderMetricValue> toInsert = new ArrayList<>();
		List<LoaderMetricValue> toUpdate = new ArrayList<>();
		List<LoaderMetricValue> toDelete = new ArrayList<>();
		
		for(LoaderMetricValue val : dataContainer.getValues()) {
			if(val.isDeleted() && !val.isNew()) {
				toDelete.add(val);
			}
			else if(val.isNew()) {
				val.setMetric(dataContainer.getMetric());
				toInsert.add(val);
			}
			else if(val.isUpdated()){
				toUpdate.add(val);
			}
		}
		
		//change the date depending on period
		
		try {
			doInsert(toInsert, freeMetricsManagerComponent, date);
		} catch (Exception e) {
			e.printStackTrace();
			listException.add(e);
			
		}
		if(((FactTable)dataContainer.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_HOURLY)) {
			date = date.substring(0, date.length() - 3);
		}
		else if(((FactTable)dataContainer.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_DAILY)) {
			date = date.substring(0, date.length() - 6);
		}
		else if(((FactTable)dataContainer.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_MONTHLY)) {
			date = date.substring(0, date.length() - 9);
		}
		else if(((FactTable)dataContainer.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_YEARLY)) {
			date = date.substring(0, date.length() - 12);
		}
		
		try {
			doUpdate(toUpdate, freeMetricsManagerComponent, date);
		} catch (Exception e) {
			e.printStackTrace();
			listException.add(e);
		}
		
		try {
			doDelete(toDelete, freeMetricsManagerComponent, date);
		} catch (Exception e) {
			e.printStackTrace();
			listException.add(e);
		}
		return listException;
	}

	private static void doDelete(List<LoaderMetricValue> toUpdate, FreeMetricsManagerComponent freeMetricsManagerComponent, String date) throws Exception {
		if(toUpdate == null || toUpdate.isEmpty()) {
			return;
		}
		
		Metric metric = toUpdate.get(0).getMetric();

		Datasource datasource = ((FactTable)metric.getFactTable()).getDatasource();
		DatasourceJdbc dsJdbc = (DatasourceJdbc) datasource.getObject();
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(dsJdbc);
		
		StringBuffer bufValues = new StringBuffer();
		
		for(LoaderMetricValue val : toUpdate) {
			bufValues.append("Delete From "+ ((FactTable)metric.getFactTable()).getTableName() + " \n");
			
			bufValues.append(NonStandardSgbdHandler.generateUpdateDeleteDateFilter(((FactTable)metric.getFactTable()).getDateColumn(), date, dsJdbc.getDriver()));
//			bufValues.append("Where " + ((FactTable)metric.getFactTable()).getDateColumn() + " LIKE '" + date + "%' \n");
			
			for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				for(LevelMember axisInfo : val.getMembers()) {
					if(axis.getAxis().getChildren().get(axis.getAxis().getChildren().size() - 1).getId() == axisInfo.getLevel().getId()) {
						try {
							Integer.parseInt(axisInfo.getValue());
							bufValues.append("and " + axis.getColumnId() + " = " + axisInfo.getValue() + " \n");
						} catch(Exception e) {
							bufValues.append("and " + axis.getColumnId() + " = '" + axisInfo.getValue() + "' \n");
						}
						break;
					}
				}
			}
			
			StringBuffer bufObj = new StringBuffer();
			if(((FactTable)metric.getFactTable()).getObjectives().getTableName() != null && !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty() && !((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(((FactTable)metric.getFactTable()).getTableName())) {
				bufObj.append("Delete From "+ ((FactTable)metric.getFactTable()).getObjectives().getTableName() + " \n");
				
				bufObj.append(NonStandardSgbdHandler.generateUpdateDeleteDateFilter(((FactTable)metric.getFactTable()).getObjectives().getDateColumn(), date, dsJdbc.getDriver()));
//				bufObj.append("Where " + ((FactTable)metric.getFactTable()).getObjectives().getDateColumn() + " LIKE '" + date + "%' \n");
				
				for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
					for(LevelMember axisInfo : val.getMembers()) {
						if(axis.getAxis().getChildren().get(axis.getAxis().getChildren().size() - 1).getId() == axisInfo.getLevel().getId()) {
							try {
								Integer.parseInt(axisInfo.getValue());
								bufObj.append("and " + axis.getObjectiveColumnId() + " = " + axisInfo.getValue() + " \n");
							} catch(Exception e) {
								bufObj.append("and " + axis.getObjectiveColumnId() + " = '" + axisInfo.getValue() + "' \n");
							}
							break;
						}
					}
				}
			}
			System.out.println(bufValues.toString());
			System.out.println(bufObj.toString());
			
			VanillaPreparedStatement stmt = connection.prepareQuery(bufValues.toString());
			stmt.executeUpdate();

			stmt.close();
			if(((FactTable)metric.getFactTable()).getObjectives().getTableName() != null && !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty() && !((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(((FactTable)metric.getFactTable()).getTableName())) {
				stmt = connection.prepareQuery(bufObj.toString());
				stmt.executeUpdate();
				stmt.close();
			}
			

		}
		
		ConnectionManager.getInstance().returnJdbcConnection(connection);
	}

	private static void doUpdate(List<LoaderMetricValue> toUpdate, FreeMetricsManagerComponent freeMetricsManagerComponent, String date) throws Exception {
		
		if(toUpdate == null || toUpdate.isEmpty()) {
			return;
		}
		
		Metric metric = toUpdate.get(0).getMetric();

		Datasource datasource = ((FactTable)metric.getFactTable()).getDatasource();
		DatasourceJdbc dsJdbc = (DatasourceJdbc) datasource.getObject();
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(dsJdbc);
		
		StringBuffer bufValues = new StringBuffer();
		
		for(LoaderMetricValue val : toUpdate) {
			bufValues.append("Update "+ ((FactTable)metric.getFactTable()).getTableName() + " set \n");
			
			bufValues.append(((FactTable)metric.getFactTable()).getValueColumn() + " = " + val.getValue() + " \n");
			
			bufValues.append(NonStandardSgbdHandler.generateUpdateDeleteDateFilter(((FactTable)metric.getFactTable()).getDateColumn(), date, dsJdbc.getDriver()));
//			bufValues.append("Where " + ((FactTable)metric.getFactTable()).getDateColumn() + " LIKE '" + date + "%' \n");
			
			for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				for(LevelMember axisInfo : val.getMembers()) {
					if(axis.getAxis().getChildren().get(axis.getAxis().getChildren().size() - 1).getId() == axisInfo.getLevel().getId()) {
						try {
							Integer.parseInt(axisInfo.getValue());
							bufValues.append("and " + axis.getColumnId() + " = " + axisInfo.getValue() + " \n");
						} catch(Exception e) {
							bufValues.append("and " + axis.getColumnId() + " = '" + axisInfo.getValue() + "' \n");
						}
						break;
					}
				}
			}
			
			StringBuffer bufObj = new StringBuffer();
			if(((FactTable)metric.getFactTable()).getObjectives().getTableName() != null && !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty()) {
				bufObj.append("Update "+ ((FactTable)metric.getFactTable()).getObjectives().getTableName() + " set \n");
				
				bufObj.append(((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn() + " = " + val.getObjective() + ", \n");
				bufObj.append(((FactTable)metric.getFactTable()).getObjectives().getMinColumn() + " = " + val.getMinimum() + ", \n");
				bufObj.append(((FactTable)metric.getFactTable()).getObjectives().getMaxColumn() + " = " + val.getMaximum() + " \n");
				
				bufObj.append(NonStandardSgbdHandler.generateUpdateDeleteDateFilter(((FactTable)metric.getFactTable()).getObjectives().getDateColumn(), date, dsJdbc.getDriver()));
//				bufObj.append("Where " + ((FactTable)metric.getFactTable()).getDateColumn() + " LIKE '" + date + "%' \n");
				
				for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
					for(LevelMember axisInfo : val.getMembers()) {
						if(axis.getAxis().getChildren().get(axis.getAxis().getChildren().size() - 1).getId() == axisInfo.getLevel().getId()) {
							try {
								Integer.parseInt(axisInfo.getValue());
								bufObj.append("and " + axis.getObjectiveColumnId() + " = " + axisInfo.getValue() + " \n");
							} catch(Exception e) {
								bufObj.append("and " + axis.getObjectiveColumnId() + " = '" + axisInfo.getValue() + "' \n");
							}
							break;
						}
					}
				}
			}
			
			System.out.println(bufValues.toString());
			System.out.println(bufObj.toString());
			
			VanillaPreparedStatement stmt = connection.prepareQuery(bufValues.toString());
			stmt.executeUpdate();

			stmt.close();
			if(((FactTable)metric.getFactTable()).getObjectives().getTableName() != null && !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty()) {
				stmt = connection.prepareQuery(bufObj.toString());
				stmt.executeUpdate();
				stmt.close();
			}
			

		}
		
		ConnectionManager.getInstance().returnJdbcConnection(connection);
		
		
	}

	private static void doInsert(List<LoaderMetricValue> toInsert, FreeMetricsManagerComponent freeMetricsManagerComponent, String date) throws Exception {
		if(toInsert == null || toInsert.isEmpty()) {
			return;
		}
		
		Metric metric = toInsert.get(0).getMetric();
		
		//values
		StringBuffer bufValues = new StringBuffer();	
		bufValues.append("insert into " + ((FactTable)metric.getFactTable()).getTableName() + " (");
		String valueCol = ((FactTable)metric.getFactTable()).getValueColumn();
		String dateCol = ((FactTable)metric.getFactTable()).getDateColumn();
		
		bufValues.append(valueCol + ", " + dateCol);
		
		for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
			String columnAxis = axis.getColumnId();
			bufValues.append(", " + columnAxis);
		}
		
		
		
		//objectives
		StringBuffer bufObj = new StringBuffer();
		if(((FactTable)metric.getFactTable()).getObjectives().getTableName() != null && !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty()) {
			String objCol = ((FactTable)metric.getFactTable()).getObjectives().getObjectiveColumn();
			String dateObjCol = ((FactTable)metric.getFactTable()).getObjectives().getDateColumn();
			String minCol = ((FactTable)metric.getFactTable()).getObjectives().getMinColumn();
			String maxCol = ((FactTable)metric.getFactTable()).getObjectives().getMaxColumn();
			if(((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(((FactTable)metric.getFactTable()).getTableName())) {
				bufValues.append("," + objCol + ", " + minCol + ", " + maxCol);
			}
			else {
				bufObj.append("insert into " + ((FactTable)metric.getFactTable()).getObjectives().getTableName() + " (");
				
				bufObj.append(objCol + ", " + dateObjCol + ", " + minCol + ", " + maxCol);
				
				for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
					String columnAxis = axis.getObjectiveColumnId();
					bufObj.append(", " + columnAxis);
				}
				
				bufObj.append(") values \n");
			}
		}
		
		bufValues.append(") values \n");
		
		boolean first = true;
		for(LoaderMetricValue val : toInsert) {
			if(first) {
				first = false;
			}
			else {
				bufValues.append(",");
			}
			bufValues.append("(");
			
			if(((FactTable)metric.getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_YEARLY) || ((FactTable)metric.getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_MONTHLY) || ((FactTable)metric.getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_BIANNUAL) || ((FactTable)metric.getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_QUARTERLY)) {
				String[] datePart = date.split(" ");
				bufValues.append(val.getValue() + ", '" + datePart[0] + "'");
			}
			else {
				bufValues.append(val.getValue() + ", '" + date + "'");
			}
			for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				for(LevelMember axisInfo : val.getMembers()) {
					if(axis.getAxis().getChildren().get(axis.getAxis().getChildren().size() - 1).getId() == axisInfo.getLevel().getId()) {
						try {
							Integer.parseInt(axisInfo.getValue());
							bufValues.append(", " + axisInfo.getValue());
						} catch(Exception e) {
							bufValues.append(", '" + axisInfo.getValue() + "'");
						}
						
						break;
					}
				}
			}
			
			if(((FactTable)metric.getFactTable()).getObjectives().getTableName() != null && !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty()&& !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty() && ((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(((FactTable)metric.getFactTable()).getTableName())) {
				bufValues.append("," + val.getObjective() + "," + val.getMinimum() + "," + val.getMaximum());
			}
			
			bufValues.append(")\n");
		}
		
		if(((FactTable)metric.getFactTable()).getObjectives().getTableName() != null && !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty() && !((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(((FactTable)metric.getFactTable()).getTableName())) {
			//generate the values for objectives
			first = true;
			for(LoaderMetricValue val : toInsert) {
				if(first) {
					first = false;
				}
				else {
					bufObj.append(",");
				}
				bufObj.append("(");
				
				bufObj.append(val.getObjective() + ", '" + date + "', " + val.getMinimum() + ", " + val.getMaximum());
				for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
					for(LevelMember axisInfo : val.getMembers()) {
						if(axis.getAxis().getChildren().get(axis.getAxis().getChildren().size() - 1).getId() == axisInfo.getLevel().getId()) {
							try {
								Integer.parseInt(axisInfo.getValue());
								bufObj.append(", " + axisInfo.getValue());
							} catch(Exception e) {
								bufObj.append(", '" + axisInfo.getValue() + "'");
							}
						}
					}
				}
				
				bufObj.append(")\n");
			}
			
		}
			
		//execute the query		
		System.out.println(bufValues.toString());
		
		Datasource datasource = ((FactTable)metric.getFactTable()).getDatasource();
		DatasourceJdbc dsJdbc = (DatasourceJdbc) datasource.getObject();
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(dsJdbc);
		
		VanillaPreparedStatement stmt = connection.prepareQuery(bufValues.toString());
		stmt.executeUpdate();

		stmt.close();
		if(((FactTable)metric.getFactTable()).getObjectives().getTableName() != null && !((FactTable)metric.getFactTable()).getObjectives().getTableName().isEmpty() && !((FactTable)metric.getFactTable()).getObjectives().getTableName().equals(((FactTable)metric.getFactTable()).getTableName())) {
			System.out.println(bufObj.toString());
			stmt = connection.prepareQuery(bufObj.toString());
			stmt.executeUpdate();
			stmt.close();
		}
		
		ConnectionManager.getInstance().returnJdbcConnection(connection);
	}
	
	
	
	public static String updateValueMember(List<LevelMember> toUpdate) throws Exception {
		String errors=null;
		if(toUpdate == null || toUpdate.isEmpty()) {
			return null;
		}
		
		Datasource datasource = toUpdate.get(0).getLevel().getDatasource();
		DatasourceJdbc dsJdbc = (DatasourceJdbc) datasource.getObject();
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(dsJdbc);
		
		StringBuffer bufValues = new StringBuffer();
		
		for(LevelMember val : toUpdate) {
			try{
				bufValues.delete(0, bufValues.length());
				
				bufValues.append("Update "+ val.getLevel().getTableName() + " set \n");
				if(val.getChildren()!=null && val.getChildren().size()>0 && 
						val.getChildren().get(0).getLevel().getTableName().equals(val.getLevel().getTableName())){
					
						bufValues.append(val.getLevel().getColumnName() + " = '" + val.getLabel());
						if(val.getParent()!=null && val.getLevel().getParentColumnId()!=null && val.getLevel().getParentColumnId().length()>0)
							bufValues.append("', " + val.getLevel().getParentColumnId()+" = '"+val.getParent().getValue()+"' \n");
						else 
							bufValues.append("' \n");
						 
						bufValues.append("Where ");
						for(LevelMember child : val.getChildren()){		
							bufValues.append(val.getLevel().getColumnId() + " = '" + child.getValue()+"' \n");
							bufValues.append("And ");
						}
							bufValues.replace(bufValues.lastIndexOf("And"), bufValues.length(), "");				
				}
				else{
					bufValues.append(val.getLevel().getColumnName() + " = '" + val.getLabel() + "' \n");
					bufValues.append("Where " + val.getLevel().getColumnId() + " = '" + val.getValue()+"' \n");
				}	
				
				System.out.println(bufValues.toString());	
				VanillaPreparedStatement stmt = connection.prepareQuery(bufValues.toString());
				stmt.executeUpdate();
				stmt.close();
				
			}catch(Exception e) {
				if(errors==null)
					errors= e.getMessage() +" : "+ bufValues.toString()+"/n";
				else
					errors+= e.getMessage() +" : "+ bufValues.toString()+"/n";
			}
		}	
		ConnectionManager.getInstance().returnJdbcConnection(connection);
		
		return errors;	
	}
	


	public static String insertValueMember(List<LevelMember> toInsert) throws Exception {
		String errors=null;
		if(toInsert == null || toInsert.isEmpty()) {
			return null;
		}
		
		Datasource datasource = toInsert.get(0).getLevel().getDatasource();
		DatasourceJdbc dsJdbc = (DatasourceJdbc) datasource.getObject();
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(dsJdbc);
		
		StringBuffer bufValues = new StringBuffer();
		
		for(LevelMember val : toInsert) {
			try{
				bufValues.delete(0, bufValues.length());
				
				bufValues.append("INSERT INTO "+ val.getLevel().getTableName() + " (" +val.getLevel().getColumnId() + ", " + val.getLevel().getColumnName());
				
				if(val.getChildren()!=null && val.getChildren().size()>0 && val.getLevel().getTableName().equals(val.getChildren().get(0).getLevel().getTableName())){
					addColChild(bufValues, val.getChildren().get(0), val.getLevel().getColumnId(), val.getLevel().getColumnName());
					bufValues.append(") \n VALUES ('"+ val.getChildren().get(0).getValue()+"', '"+ val.getLabel());
					addValChild(bufValues, val.getChildren().get(0), val.getLevel().getColumnId(), val.getLevel().getColumnName());
					bufValues.append("')");			
				}
				else{
					if(val.getLevel().getParentColumnId()!=null && val.getLevel().getParentColumnId().length()>0)
						bufValues.append(", " + val.getLevel().getParentColumnId()); 	
					bufValues.append(") \n VALUES ('"+ val.getValue()+"', '"+ val.getLabel());
					if(val.getLevel().getParentColumnId()!=null && val.getLevel().getParentColumnId().length()>0)
						bufValues.append("', '" + val.getParent().getValue()); 	
					bufValues.append("')");
				}	
				
				System.out.println(bufValues.toString());		
				VanillaPreparedStatement stmt = connection.prepareQuery(bufValues.toString());
				stmt.executeUpdate();
				stmt.close();
			}catch(Exception e) {
				if(errors==null)
					errors= e.getMessage() +" : "+ bufValues.toString()+"/n";
				else
					errors+= e.getMessage() +" : "+ bufValues.toString()+"/n";
			}
		}	
		ConnectionManager.getInstance().returnJdbcConnection(connection);	
		return errors;
	}

	
	
	public static String deleteValueMember(List<LevelMember> toDelete) throws Exception {
		String errors=null;
		if(toDelete == null || toDelete.isEmpty()) {
			return null;
		}
		
		Datasource datasource = toDelete.get(0).getLevel().getDatasource();
		DatasourceJdbc dsJdbc = (DatasourceJdbc) datasource.getObject();
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(dsJdbc);
		
		StringBuffer bufValues = new StringBuffer();
		
		for(LevelMember val : toDelete) {
			try{
				bufValues.delete(0, bufValues.length());
				if(val.getChildren()!=null && val.getChildren().size()>0){				
					deleteValueMember(val.getChildren());	
				}			
				bufValues.append("Delete From "+ val.getLevel().getTableName() + " Where "+ val.getLevel().getColumnId() + " = '"+ val.getValue()+"'" );		
				
				System.out.println(bufValues.toString());		
				VanillaPreparedStatement stmt = connection.prepareQuery(bufValues.toString());
				stmt.executeUpdate();
				stmt.close();
			} catch(Exception e) {
				if(errors==null)
					errors= e.getMessage() +" : "+ bufValues.toString()+"/n";
				else
					errors+= e.getMessage() +" : "+ bufValues.toString()+"/n";
			}
			
		}	
		ConnectionManager.getInstance().returnJdbcConnection(connection);	
		return errors;
	}

	

	private static void addColChild(StringBuffer bufValues, LevelMember member, String parentColId, String parentColName){
		if(!member.getLevel().getColumnName().equals(parentColId) && !member.getLevel().getColumnName().equals(parentColName))
			bufValues.append(", "+ member.getLevel().getColumnName());
		if(member.getChildren()!=null && member.getChildren().size()>0){
			addColChild(bufValues, member.getChildren().get(0),  parentColId, parentColName);
		}
	}
	
	private static void addValChild(StringBuffer bufValues, LevelMember member, String parentColId, String parentColName){
		if(!member.getLevel().getColumnName().equals(parentColId) && !member.getLevel().getColumnName().equals(parentColName))
			bufValues.append("', '"+ member.getLabel());
		if(member.getChildren()!=null && member.getChildren().size()>0){
			addValChild(bufValues, member.getChildren().get(0), parentColId, parentColName);
		}
	}
	
	
	
}
