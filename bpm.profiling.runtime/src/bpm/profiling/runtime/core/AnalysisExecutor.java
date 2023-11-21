package bpm.profiling.runtime.core;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.profiling.database.AnalysisManager;
import bpm.profiling.database.Helper;
import bpm.profiling.database.bean.AnalysisConditionResult;
import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.database.bean.AnalysisResultBean;
import bpm.profiling.database.bean.RuleSetBean;


public class AnalysisExecutor {

	private static final String lowValueQuery = "select MIN($column) from $table";
	private static final String lowValueCountQuery = "select COUNT(*) from $table where $column=(select MIN($column) from $table)";
	private static final String hightValueQuery = "select MAX($column) from $table";
	private static final String hightValueCountQuery = "select COUNT(*) from $table where $column=(select MAX($column) from $table)";
	private static final String avgValueQuery = "select AVG($column) from $table";
	private static final String distinctCountQuery = "select  COUNT(DISTINCT $column) from $table ";
	private static final String zeroCountQuery = "select COUNT(*) from $table where $column=0";
	private static final String blankCountQuery = "select COUNT(*) from $table where $column=''";
	private static final String nullCountQuery = "select COUNT(*) from $table where $column IS NULL";
	private static final String countQuery = "select COUNT(*) from $table";
	
	private static final String[] queries = new String[]{
		lowValueQuery, lowValueCountQuery,hightValueQuery,hightValueCountQuery,avgValueQuery,
		distinctCountQuery, zeroCountQuery, blankCountQuery,nullCountQuery , countQuery  
	};
	
	/**
	 * execute an analysis for a simpleColumn 
	 * @param connection
	 * @param contentBean
	 * @return
	 */
	private static AnalysisResultBean executeAnalysis(Date currentDate, Connection connection, AnalysisContentBean contentBean) throws Exception{

		/*
		 * look for the table and the column
		 */
		List<Table> tables = null;
		if (connection.getTables().isEmpty()){
			tables = connection.connect();
		}else{
			tables = connection.getTables();
		}
		
		
		
		Table table = null;
		Column column = null;
		
		for(Table t : tables){
			if (t.getLabel().equals(contentBean.getTableName())){
				table = t;
				
				for(Column c : t.getColumns()){
					if (c.getLabel().equals(contentBean.getColumnName())){
						column = c;
						break;
					}
				}
				break;
				
			}
		}
		
		
		
		/*
		 * prepare queries
		 */
		List<String> preparedQueries = prepareQueries(table, column);
		
		
		/*
		 * detect if the column is a String
		 */
		boolean isString = false;
		
		if (column.getType().contains("CHAR") ||column.getType().contains("char") || column.getType().contains("text")
				|| column.getType().contains("TEXT") || column.getType().contains("DATE") || column.getType().contains("date")||
				column.getType().contains("time") || column.getType().contains("TIME") || column.getType().contains("STRING") ||
				column.getType().contains("string")){
			isString  = true;
		}
		//TODO
		
		/*
		 * open Connection
		 */
		VanillaJdbcConnection sock = connection.getJdbcConnection();
		VanillaPreparedStatement stmt = sock.createStatement();
		AnalysisResultBean result = new AnalysisResultBean();
		try {
			
			
			
			result.setCreation(currentDate);
			result.setAnalysisContentId(contentBean.getId());
			result.setDataType(column.getType());
			int totalCount = 0;
			
			for(int i = 0; i < preparedQueries.size(); i++){
				
				try{
					if (i == 4 && isString){
						continue;
					}
					System.out.print(preparedQueries.get(i) + " = ");
					ResultSet rs = stmt.executeQuery(preparedQueries.get(i));
					rs.next();
					System.out.println(rs.getObject(1));
					switch(i){
					case 0:
				
						if (rs.getObject(1) == null){
							result.setLowValue(null);
						}
						else{
							result.setLowValue(rs.getObject(1).toString());
						}
						break;
					case 1:
						if (rs.getObject(1) == null){
							result.setLowValueCount(null);
						}
						else{
							result.setLowValueCount(rs.getInt(1));
						}
						break;
					case 2: 
						if (rs.getObject(1) == null){
							result.setHightValue(null);
						}
						else{
							result.setHightValue(rs.getObject(1).toString());
						}
						break;
					case 3: 
						if (rs.getObject(1) == null){
							result.setHightValueCount(null);
						}
						else{
							result.setHightValueCount(rs.getInt(1));
						}
						break;
					case 4 : 
						 if (!isString){
							 result.setAvgValue(rs.getDouble(1));
								break; 
						 }
						
					case 5: 
						result.setDistinctCount(rs.getInt(1));
						break;
					case 6:
						if (!isString){
							result.setZeroCount(rs.getInt(1));
							break;
						}
						
					case 7:
						if (isString){
							result.setBlankCount(rs.getInt(1));
							break;
						}
						
					case 8:
						result.setNullCount(rs.getInt(1));
						break;
					case 9:
						totalCount = rs.getInt(1);
						
						
						if (isString){
							if (result.getBlankCount() != null && totalCount != 0){
								result.setBlankPercent(result.getBlankCount()/ (double) totalCount);
							}
							
						}
						else{
							if (result.getZeroCount() != null && totalCount != 0){
								result.setZeroPercent(result.getZeroCount()/ (double) totalCount);
							}
								
						}
						if (result.getNullCount() != null && totalCount != 0){
							result.setNullPercent((double)result.getNullCount() / (double) totalCount);
						}
						break;
					}
					
					
					rs.close();
					
					
					
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}
		finally {
			stmt.close();
			ConnectionManager.getInstance().returnJdbcConnection(sock);
		}
		result.setAnalysisContentId(contentBean.getId());
		return result;
		
	}
	
	
	
	private static List<String> prepareQueries(Table table, Column column){
		List<String> result = new ArrayList<String>();
		
		
		
		for(int i = 0; i < queries.length; i++){
			String s = new String(queries[i].replace("$column", column.getName()).replace("$table", table.getName()));
			result.add(s);
		}
		return result;
	}
	
	
	/**
	 * execute an analysis for a simpleColumn 
	 * @param connection
	 * @param contentBean
	 * @return
	 */
	public static HashMap<AnalysisContentBean, AnalysisResultBean> executeFullAnalysis(Date currentDate, Connection connection, AnalysisInfoBean infoBean) throws Exception{
		
		HashMap<AnalysisContentBean, AnalysisResultBean> results = new HashMap<AnalysisContentBean, AnalysisResultBean>();
		AnalysisManager manager = Helper.getInstance().getAnalysisManager();
		
		for(AnalysisContentBean content : manager.getAllAnalysisContentFor(infoBean)){
			
			
			try{
				results.put(content,executeAnalysis(currentDate, connection, content));
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		
		return results;
	}



	private static List<AnalysisConditionResult> executeForRuleSet(Date currentDate, Connection connection, AnalysisContentBean contentBean, RuleSetBean ruleSet) throws Exception{
		List<AnalysisConditionResult> results = new ArrayList<AnalysisConditionResult>();
		
		
		/*
		 * look for the table and the column
		 */
		List<Table> tables = null;
		if (connection.getTables().isEmpty()){
			tables = connection.connect();
		}else{
			tables = connection.getTables();
		}
		
		
		
		Table table = null;
		Column column = null;
		
		for(Table t : tables){
			if (t.getLabel().equals(contentBean.getTableName())){
				table = t;
				
				for(Column c : t.getColumns()){
					if (c.getLabel().equals(contentBean.getColumnName())){
						column = c;
						break;
					}
				}
				break;
				
			}
		}
		
		
		boolean isString = false;
		
		if (column.getType().contains("CHAR") ||column.getType().contains("char") || column.getType().contains("text")
				|| column.getType().contains("TEXT") || column.getType().contains("DATE") || column.getType().contains("date")||
				column.getType().contains("time") || column.getType().contains("TIME") || column.getType().contains("STRING") ||
				column.getType().contains("string")){
			isString  = true;
		}
		
		
		/*
		 * prepare queries
		 */
		
		AnalysisManager manager = Helper.getInstance().getAnalysisManager();
		VanillaJdbcConnection sock = connection.getJdbcConnection();
		VanillaPreparedStatement stmt = sock.createStatement();
		
		
		List<Condition> allConditions = manager.getConditionForRuleSet(ruleSet); 
		
		for(Condition cond : allConditions){
			
			AnalysisConditionResult condRes = new AnalysisConditionResult();
			condRes.setConditionId(cond.getId());
			condRes.setDate(currentDate);
			/*
			 * validCount
			 */
			String validCount = new String(queries[9]);
			validCount += cond.getSql(isString);
			validCount = validCount.replace("$column", column.getName()).replace("$table", table.getName());
			ResultSet rs = stmt.executeQuery(validCount);
			rs.next();
			condRes.setValidCount(rs.getInt(1));
			rs.close();
			
			/*
			 * validPercent
			 */
			String totalCount = new String(queries[9]);
			totalCount = countQuery.replace("$column", column.getName()).replace("$table", table.getName());
			
			rs = stmt.executeQuery(totalCount);
			rs.next();
			int totalCountValue = rs.getInt(1);
			rs.close();
			
			condRes.setValidCountPercent((double)condRes.getValidCount() / (double) totalCountValue);
			
			/*
			 * distcin validCount
			 */
			String distinctValidCount = new String(distinctCountQuery);
			distinctValidCount += cond.getSql(isString);
			distinctValidCount = distinctValidCount.replace("$column", column.getName()).replace("$table", table.getName());
			
			rs = stmt.executeQuery(distinctValidCount);
			rs.next();
			condRes.setDistinctValidCount(rs.getInt(1));
			rs.close();
			
			String distinctTotalCount = new String(distinctCountQuery);
			distinctTotalCount = distinctCountQuery.replace("$column", column.getName()).replace("$table", table.getName());
			
			rs = stmt.executeQuery(distinctTotalCount);
			rs.next();
			int totalDistinctCountValue = rs.getInt(1);
			rs.close();
			
			condRes.setDictinctValidPercent((double)condRes.getDistinctValidCount() / (double) totalDistinctCountValue);

			results.add(condRes);
		}
		
		

		StringBuffer buf = new StringBuffer();
		
		boolean first = true;
		for(Condition c : allConditions){
			
			if (first){
				first = false;
				buf.append(" where ");
			}
			else{
				buf.append(ruleSet.getLogicalOperator() == RuleSetBean.AND ? " AND " : " OR ");
			}
			
			buf.append(c.getSql(isString).substring(6));
			
		}
		
		AnalysisConditionResult condRes = new AnalysisConditionResult();
		condRes.setDate(currentDate);
		condRes.setConditionId(0);
		
		String validCount = new String(queries[9]);
		validCount +=buf.toString();
		validCount = validCount.replace("$column", column.getName()).replace("$table", table.getName());
		
		ResultSet rs = stmt.executeQuery(validCount);
		rs.next();
		condRes.setValidCount(rs.getInt(1));
		rs.close();

		
		String totalCount = new String(queries[9]);
		totalCount = countQuery.replace("$column", column.getName()).replace("$table", table.getName());
		
		rs = stmt.executeQuery(totalCount);
		rs.next();
		int totalCountValue = rs.getInt(1);
		rs.close();
		
		condRes.setValidCountPercent((double)condRes.getValidCount() / (double) totalCountValue);

		
		/*
		 * distcin validCount
		 */
		String distinctValidCount = new String(distinctCountQuery);
		distinctValidCount +=buf.toString();
		distinctValidCount = distinctValidCount.replace("$column", column.getName()).replace("$table", table.getName());
		
		rs = stmt.executeQuery(distinctValidCount);
		rs.next();
		condRes.setDistinctValidCount(rs.getInt(1));
		rs.close();
		
		String distinctTotalCount = new String(distinctCountQuery);
		distinctTotalCount = distinctCountQuery.replace("$column", column.getName()).replace("$table", table.getName());
		
		rs = stmt.executeQuery(distinctTotalCount);
		rs.next();
		int totalDistinctCountValue = rs.getInt(1);
		rs.close();
		
		condRes.setDictinctValidPercent((double)condRes.getDistinctValidCount() / (double) totalDistinctCountValue);

		condRes.setConditionId(-1);
		results.add(condRes);
		condRes.setRuleSetId(ruleSet.getId());	

		
		
		
		stmt.close();
		ConnectionManager.getInstance().returnJdbcConnection(sock);
		
		return results;
	}

	
	public static HashMap<AnalysisContentBean, HashMap<RuleSetBean, List<AnalysisConditionResult>>> executeAnalysisForConditions(Date currentDate, Connection connection, AnalysisInfoBean infoBean) throws Exception{
		HashMap<AnalysisContentBean, HashMap<RuleSetBean, List<AnalysisConditionResult>>> results = new HashMap<AnalysisContentBean, HashMap<RuleSetBean, List<AnalysisConditionResult>>>();
		AnalysisManager manager = Helper.getInstance().getAnalysisManager();
		for(AnalysisContentBean content : manager.getAllAnalysisContentFor(infoBean)){
			
			results.put(content, new HashMap<RuleSetBean, List<AnalysisConditionResult>>());
			
			for(RuleSetBean rsb : manager.getRuleSetsFor(content)){
				results.get(content).put(rsb, executeForRuleSet(currentDate, connection, content, rsb));
			}
			
		}
		
		
		return results;
	}
	
}
