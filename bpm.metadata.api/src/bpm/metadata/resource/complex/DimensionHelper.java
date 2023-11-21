package bpm.metadata.resource.complex;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.metadata.layer.business.PathFinder;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;

public class DimensionHelper {

	/**
	 * @param dim
	 * @param levelNumber
	 * @return sqlqury
	 * @throws Exception
	 */
	private String getQuery(FmdtDimension dim, int levelNumber) throws Exception{
		/*
		 * create query
		 */
		
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT DISTINCT ");
		
		IDataStreamElement field = dim.getLevels().get(levelNumber);
		
		if (field instanceof ICalculatedElement){
			buf.append(((ICalculatedElement)field).getFormula());
			buf.append(" FROM " + field.getDataStream().getOrigin().getName());
		}
		else{
			buf.append(field.getOrigin().getName());
			buf.append(" FROM " + field.getOrigin().getTable().getName());
		}
		
		

		
		return buf.toString();
		
	}
	
	
	private String getQuery(FmdtDimension dim, int levelNumber, List<Object> previousValues) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer buf = new StringBuffer();
		/*
		 * check if it is snowflakes
		 */
		boolean snowFlakes = false;
		for(int i = 0; i < levelNumber && !snowFlakes; i++){
			if (dim.getLevels().get(i).getDataStream() != dim.getLevels().get(levelNumber).getDataStream()){
				snowFlakes = true;
				break;
			}
		}
		
		if (snowFlakes){
			/*
			 * create query
			 */
			
			buf.append("SELECT DISTINCT ");
			
			IDataStreamElement field = dim.getLevels().get(levelNumber);
			
			
			
			if (field instanceof ICalculatedElement){
				buf.append(((ICalculatedElement)field).getFormula());
				buf.append(" FROM " + field.getDataStream().getOrigin().getName());
			}
			else{
				buf.append(field.getOrigin().getName());
				buf.append(" FROM " + field.getOrigin().getTable().getName());
			}
			
			/*
			 * add tables from previous levels
			 */
			for(int i = 0; i < levelNumber; i++){
				int fromIndex = buf.toString().indexOf(" FROM ");
				
				if (dim.getLevels().get(i).getDataStream() != dim.getLevels().get(levelNumber).getDataStream()&& ! buf.toString().substring(fromIndex).contains(dim.getLevels().get(i).getDataStream().getOrigin().getName())){
					buf.append(", ");
					buf.append( dim.getLevels().get(i).getDataStream().getOrigin().getName());
				}
			}
			
			/*
			 * build Where clause
			 */
			buf.append(" WHERE ");
			
			boolean first = true;
			for(int i = 0; i < levelNumber; i++){
				
				if (first){
					first = false;
				}
				else{
					buf.append(" AND ");
				}
				IDataStreamElement f = dim.getLevels().get(i);
				
				if (f instanceof ICalculatedElement){
					buf.append(((ICalculatedElement)f).getFormula());
					
				}
				else{
					if (!f.getOrigin().getName().contains(f.getOrigin().getTable().getName())){
						buf.append(f.getOrigin().getTable().getName() + ".");
					}
					
					buf.append(f.getOrigin().getName());
					
				}
				
				
				 
				if (previousValues.get(i) == null){
					buf.append(" IS NULL ");
				}
				else if (previousValues.get(i) instanceof Date){
					buf.append("=");
					buf.append("'" + sdf.format((Date)previousValues.get(i)) + "'");
				}
				else if (previousValues.get(i) instanceof String){
					buf.append("=");
					buf.append("'" + previousValues.get(i) + "'");
				}
				else{
					buf.append("=");
					buf.append(previousValues.get(i));
				}
			}
			
			List<IDataStream> tables = new ArrayList<IDataStream>();
			for(int i = 0; i <= levelNumber; i++){
				if (!tables.contains(dim.getLevels().get(i).getDataStream())){
					tables.add(dim.getLevels().get(i).getDataStream());
				}
			}
			
			if (tables.size() > 1){
				PathFinder f = new PathFinder(dim.getDataSource().getRelations(), tables, new ArrayList<RelationStrategy>());
				for (Relation r : f.getPath().getRelations()){
					 buf.append(" AND " + r.getWhereClause());
				}
			}
		}
		else{
			/*
			 * create query
			 */
			
			buf.append("SELECT DISTINCT ");
			
			IDataStreamElement field = dim.getLevels().get(levelNumber);
			
			
			
			if (field instanceof ICalculatedElement){
				buf.append(((ICalculatedElement)field).getFormula());
				buf.append(" FROM " + field.getDataStream().getOrigin().getName());
			}
			else{
				buf.append(field.getOrigin().getName());
				buf.append(" FROM " + field.getOrigin().getTable().getName());
			}
			
			/*
			 * build Where clause
			 */
			buf.append(" WHERE ");
			
			boolean first = true;
			for(int i = 0; i < levelNumber; i++){
				
				if (first){
					first = false;
				}
				else{
					buf.append(" AND ");
				}
				IDataStreamElement f = dim.getLevels().get(i);
				
				if (f instanceof ICalculatedElement){
					buf.append(((ICalculatedElement)f).getFormula());
					
				}
				else{
					if (!f.getOrigin().getName().contains(f.getOrigin().getTable().getName())){
						buf.append(f.getOrigin().getTable().getName() + ".");
					}
					
					buf.append(f.getOrigin().getName());
					
				}
				
				
				 
				if (previousValues.get(i) == null){
					buf.append(" IS NULL ");
				}
				else if (previousValues.get(i) instanceof Date){
					buf.append("=");
					buf.append("'" + sdf.format((Date)previousValues.get(i)) + "'");
				}
				else if (previousValues.get(i) instanceof String){
					buf.append("=");
					buf.append("'" + previousValues.get(i) + "'");
				}
				else{
					buf.append("=");
					buf.append(previousValues.get(i));
				}
			}
		}
		
		

		
		return buf.toString();
		
	}
	
	public List<List<Object>> getValues(String connectionName, FmdtDimension dimension, int maxRows) throws Exception{
		VanillaJdbcConnection sock = null;
		for(IConnection c : dimension.getDataSource().getConnections()){
			if (sock == null){
				sock = ((SQLConnection)c).getJdbcConnection();
			}
			if (c.getName().equals("Default")){
				sock = ((SQLConnection)c).getJdbcConnection();
				 
				break;
			}
		}
		
		List<List<Object>> values = getValues(sock, dimension, 0, new ArrayList<Object>(), maxRows);
		if (sock  != null){
			try{
				ConnectionManager.getInstance().returnJdbcConnection(sock);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		return values;
	}
	
	private List<List<Object>> getValues(VanillaJdbcConnection sock, FmdtDimension dim, int lvlNum, List<Object> values, int maxRows) throws Exception{
		String query = null;
		if (values.isEmpty()){
			query = getQuery(dim, lvlNum);
		}
		else{
			query = getQuery(dim, lvlNum, values);
		}
		List<List<Object>> resuts = new ArrayList<List<Object>>();
		List<Object> l = executeQuery(sock, query, maxRows);
		if (lvlNum == dim.getLevels().size() - 1){
			
			for(Object o : l){
				List<Object> _l = new ArrayList<Object>(values);
				_l.add(o);
				resuts.add(_l);
				
				
			}
			
		}
		else{
			for(Object o : l){
				List<Object> _l = new ArrayList<Object>(values);
				_l.add(o);
				resuts.addAll(getValues(sock, dim, lvlNum + 1, _l, maxRows));
			}
		}
		
		return resuts;
	}
	
	private List<Object> executeQuery(VanillaJdbcConnection sock, String query, int maxRows) throws Exception{
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<Object> result = new ArrayList<Object>();
		Exception problem = null;
		try{
			
			
			stmt = sock.createStatement();
			stmt.setMaxRows(maxRows);
			rs = stmt.executeQuery(query);
			
			while(rs.next()){
				result.add(rs.getObject(1));
			}
		}catch(Exception ex){
			ex.printStackTrace();
			problem = ex;
		
		}finally{
			if (rs  != null){
				try{
					rs.close();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			if (stmt  != null){
				try{
					stmt.close();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			
			
		}
		
		
		if (problem != null){
			throw new Exception("Problem when getting Level Values : " + problem.getMessage(), problem);
		}
		return result;
	}
	
}
