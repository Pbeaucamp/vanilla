package bpm.fd.runtime.engine.datas.helper;

import java.awt.Point;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;

import bpm.fd.api.core.model.components.definition.IComponentDataFilter;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.DatasLimit;
import bpm.fd.api.core.model.datas.filters.ValueFilter;
import bpm.fd.api.core.model.datas.filters.ValueFilter.Type;


public class Aggregationer {

	private HashMap<Object, List<Object>> current = new HashMap<Object, List<Object>> ();
	private List<List<Object>> values = new ArrayList<List<Object>>();
	private HashMap<Object, List<Integer>> number = new HashMap<Object, List<Integer>>();
	private HashMap<Object, List<List<Object>>> insertedDistinctValues = new HashMap<Object, List<List<Object>>>();
	
	
	
	private Object extractResultSetValue(int columnType, IResultSet resultSet, int colIndex) throws Exception{
		switch(columnType){
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.INTEGER:
			
			return resultSet.getInt(colIndex);
			
			
		case Types.FLOAT:
		case Types.DOUBLE:
			return resultSet.getDouble(colIndex);
			
		case Types.DECIMAL:
			return resultSet.getBigDecimal(colIndex);
			
		case Types.DATE:
			return resultSet.getDate(colIndex);
		case Types.TIME:
			return resultSet.getTime(colIndex);
		case Types.TIMESTAMP :
			return resultSet.getTimestamp(colIndex);
		default:
			return resultSet.getString(colIndex);
		}
	}
	/**
	 * 
	 * @param resultSet
	 * @param groupIndex
	 * @param groupLabel
	 * @param valueIndex
	 * @param orderIndex
	 * @param aggregator
	 * @param limt{Type, limitSize}
	 * @return
	 * @throws Exception
	 */
	public List<List<Object>> performAggregation(
			IResultSet resultSet, 
			int groupIndex, 
			Integer groupLabel, 
			int valueIndex, 
			int orderIndex, 
			int aggregator, 
			int[] limits,
			String filterString) throws Exception{
		
		current = new HashMap<Object, List<Object>> ();
		values = new ArrayList<List<Object>>();
		insertedDistinctValues = new HashMap<Object, List<List<Object>>>();
		number = new HashMap<Object, List<Integer>>();
		
		List<List<IComponentDataFilter>> filters = parseFilterString(filterString, 1);
		
		
		
		
		while(resultSet.next()){
			Object key = resultSet.getString( groupIndex );
						
			if (key == null){
				continue;
			}
			// add value
			int columnType = resultSet.getMetaData().getColumnType(valueIndex);
			
			Object value = extractResultSetValue(columnType, resultSet, valueIndex);
			
			
			
			addAggregationValue(aggregator,  value, key, 0);
			
			//add Group value
			columnType = resultSet.getMetaData().getColumnType(groupIndex);
			
			
			if (current.get(key).size() < 2){
				current.get(key).add(extractResultSetValue(columnType, resultSet, groupIndex));

			}
			
			if (current.get(key).size() < 3){
				if (groupLabel == null){
					current.get(key).add(extractResultSetValue(Types.VARCHAR, resultSet, groupIndex));
//							resultSet.getString(groupIndex));
				}
				else{
					current.get(key).add(extractResultSetValue(Types.VARCHAR, resultSet, groupLabel));
//					current.get(key).add(resultSet.getString(groupLabel));
				}
				
			}
			
			if (orderIndex >= 0 && current.get(key).size() < 4){
				current.get(key).add(extractResultSetValue(resultSet.getMetaData().getColumnType(orderIndex), resultSet, orderIndex));
//				current.get(key).add(resultSet.getString(orderIndex));
			}
		}
		
		
		resultSet.close();
		
		int pos = 0;
		if (aggregator == DataAggregation.AGG_AVG){
			for(Object l : current.keySet()){
				
				
				if (current.get(l).get(pos) instanceof Double){
					current.get(l).set(pos, (Double)((Double)current.get(l).get(pos) / number.get(l).get(0).doubleValue()));
				}
				else if (current.get(l).get(pos) instanceof Float){
					current.get(l).set(pos, (Float)((Float)current.get(l).get(pos) / number.get(l).get(0).floatValue()));
				}
				else if (current.get(l).get(pos) instanceof Integer){
					current.get(l).set(pos, (Double)(((Integer)current.get(l).get(pos)).doubleValue() / number.get(l).get(0).doubleValue()));
				}
				else if (current.get(l).get(pos) instanceof Long){
					current.get(l).set(pos, (Double)(((Long)current.get(l).get(pos)).doubleValue() / number.get(l).get(0).doubleValue()));
				}
				else if (current.get(l).get(pos) instanceof BigDecimal){
					current.get(l).set(pos, (Double)(((BigDecimal)current.get(l).get(pos)).doubleValue() / number.get(l).get(0).doubleValue()));
				}
			}
		}
		
		int valuesGroupIndex = 0;
		for(Object l : current.keySet()){
			
			// filter application
			boolean discard = false;
			if (!filters.isEmpty()){
				for(IComponentDataFilter f : filters.get(0)){
					if (! f.isSatisfied(current.get(l).get(0))){
						Logger.getLogger(Aggregationer.class).info("Row discarded because a filter is not satisfied");
						discard = true;
						break;
					}
				}
			}
			
			if (discard){
				continue;
			}
			
			List<Object> row = new ArrayList<Object>();
			row.add(current.get(l).get(2));
			
			row.add(current.get(l).get(0));
			row.add(l);

			row.add(current.get(l).get(3));
			valuesGroupIndex = row.size() - 1;
			values.add(row);
		}
		
		
		if (limits != null && limits[0] != DatasLimit.LIMIT_NONE){
			
			if (limits[0] == DatasLimit.LIMIT_BOTTOM){
				reorder(1, true);
			}
			else{
				reorder(1, false);
			}
			List<List<Object>> l = new ArrayList<List<Object>>(limits[1]);
			for (int i = 0; i < limits[1] ; i++){
				if (i < values.size() )
				l.add(values.get(i));
			}
			
			values = l;
//			
		}
		
		reorder(3, true);
		
		return values;
	}
	
	
	


	private void reorder(int ordinalIndex, boolean asc){
		int currentRow = 0;
		
		while(currentRow != values.size()){
			int index = -1;
			if (asc){
				index = getMinIndex(currentRow, ordinalIndex);
			}
			else{
				index = getMaxIndex(currentRow, ordinalIndex);
			}
			
			swap(currentRow, index);
			currentRow++;
		}
	}
	
	private void swap(int a, int b){
		List<Object> tmp = new ArrayList<Object>(values.get(a));
		values.set(a, values.get(b));
		values.set(b, tmp);
	}
	
	private int getMinIndex(int start, int ordinalIndex){
		Comparable min = null;
		int minRow = start;
		for(int i = start; i < values.size(); i++){
			if (min == null || ((Comparable)values.get(i).get(ordinalIndex)).compareTo(min) == -1){
				min = (Comparable)values.get(i).get(ordinalIndex);
				minRow = i;
			}
		}
		return minRow;
	}
	
	private int getMaxIndex(int start, int ordinalIndex){
		Comparable max = null;
		int maxRow = start;
		for(int i = start; i < values.size(); i++){
			if (max == null || ((Comparable)values.get(i).get(ordinalIndex)).compareTo(max) == 1){
				max = (Comparable)values.get(i).get(ordinalIndex);
				maxRow = i;
			}
		}
		return maxRow;
	}
	
	
	
	
	private void addAggregationValue(int aggregator, Object value, Object key, int valueNumber){
		/*
		 * look if the key already exists
		 */
		
		Object keyExists = null;
		for(Object l : current.keySet()){
			if (l == null){
				continue;
			}
			if (l.equals(key)){
				keyExists = l;
				break;
			}
		}
		
		
		
		boolean newDistinctValue = insertDistinctValue(key, value, valueNumber);	
		if (keyExists != null){
			
			if (aggregator == DataAggregation.AGG_AVG){
				
				if (number.get(key) == null){
					number.put(key, new ArrayList<Integer>());
					
				}
				
				while(number.get(key).size()-1 < valueNumber){
					number.get(key).add(0);
				}
				number.get(key).set(valueNumber, number.get(key).get(valueNumber) + 1);
				performAgg(aggregator, value, current.get(keyExists), valueNumber);
				
			}
			else if ((aggregator == DataAggregation.AGG_COUNT_DISTINCT && newDistinctValue)){
				performAgg(aggregator, value, current.get(keyExists), valueNumber);
				
			}
			else if (aggregator != DataAggregation.AGG_COUNT_DISTINCT){
				performAgg(aggregator, value, current.get(keyExists), valueNumber);
			}
			return;
			
		}
//		else{
		if (current.get(key) == null){
			current.put(key, new ArrayList<Object>());
		}
			
			
			if (aggregator == DataAggregation.AGG_AVG){
				
				if (number.get(key) == null){
					number.put(key, new ArrayList<Integer>());
					
				}
				
				while(number.get(key).size()-1 < valueNumber){
					number.get(key).add(0);
				}
				number.get(key).set(valueNumber, number.get(key).get(valueNumber) + 1);
				
			}
			while(current.get(key).size() <= valueNumber){
				current.get(key).add(null);
			}
//			if (current.get(key).isEmpty()){
//				current.get(key).add(null);
//			}
			if (aggregator == DataAggregation.AGG_COUNT || aggregator == DataAggregation.AGG_COUNT_DISTINCT){
				current.get(key).set(valueNumber, 1);

			}
			else{
				current.get(key).set(valueNumber, value);
			}
			
			
//		}
		
		

		
	}
	
	private boolean insertDistinctValue(Object key, Object value, int valuesNum){
		boolean found = false;
		
		if (insertedDistinctValues.get(key) == null){
			insertedDistinctValues.put(key, new ArrayList<List<Object>>());
		}
		
		for(int i = insertedDistinctValues.get(key).size() -1; i<=valuesNum; i++){
			insertedDistinctValues.get(key).add(new ArrayList<Object>());
		}
		
		for(Object o : insertedDistinctValues.get(key).get(valuesNum)){
			if ((o == null && value == null) || (o != null && o.equals(value) || (value != null && value.equals(o)))){
				found = true;
				break;
			}
		}
		
		if (!found){
			insertedDistinctValues.get(key).get(valuesNum).add(value);
		}
		return !found;
	}
	
	private void performAgg(int function, Object value, List<Object> target, int valueNumber){
		
		for(int i = target.size()-1; i<valueNumber; i++){
			target.add(null);
		}
//		if (target.isEmpty()){
//			
//		}
		switch(function){
		
		case DataAggregation.AGG_COUNT:
			if (target.get(valueNumber) == null){
				target.set(valueNumber, 1);
			}else {
				target.set(valueNumber, (Integer)target.get(valueNumber) + 1);
			}
			
			break;
		case DataAggregation.AGG_COUNT_DISTINCT:
			if (target.get(valueNumber) == null){
				target.set(valueNumber, 1);
			}else {
				target.set(valueNumber, (Integer)target.get(valueNumber) + 1);
			}
			break;
		case DataAggregation.AGG_MAX:
			if (target.get(valueNumber) == null){
				target.set(valueNumber, value);
			}else if (((Comparable)target.get(valueNumber)).compareTo((Comparable)value) < 0){
				target.set(valueNumber, value);
			}
			break;
		case DataAggregation.AGG_MIN:
			if (target.get(valueNumber) == null){
				target.set(valueNumber, value);
			}else if (((Comparable)target.get(valueNumber)).compareTo((Comparable)value) > 0){
				target.set(valueNumber, value);
			}
			break;
		case DataAggregation.AGG_AVG:	
		case DataAggregation.AGG_SUM:
			if (target.get(valueNumber) == null){
				target.set(valueNumber, value);
			}
			else if (target.get(valueNumber) instanceof Double){
				target.set(valueNumber, (Double)target.get(valueNumber) + (Double)value);
			}
			else if (target.get(valueNumber) instanceof Float){
				target.set(valueNumber, (Float)target.get(valueNumber) + (Float)value);
			}
			else if (target.get(valueNumber) instanceof Integer){
				target.set(valueNumber, (Integer)target.get(valueNumber) + (Integer)value);
			}
			else if (target.get(valueNumber) instanceof Long){
				target.set(valueNumber, (Long)target.get(valueNumber) + (Long)value);
			}
			else if (target.get(valueNumber) instanceof BigDecimal){
				target.set(valueNumber, ((BigDecimal)target.get(valueNumber)).add((BigDecimal)value));
			}
			else {
				target.set(valueNumber, (String)target.get(valueNumber) + (String)value);
			}
			
			break;
		}
		
	}
	
	
	/**
	 * 
	 * @param resultSet
	 * @param categoryIndex
	 * @param serieIndex
	 * @param aggregators : x = field, y = function
	 * @param limit {TYPE, limitSize}
	 * @param labelIndex : the index of the label
	 * @return an array List on the form  : measure1,measure2,...,measureN,key1(,key2),categorieLabel,categorieOrdinalValue
	 * @param filterString : string representation of a filter ex(Inf500.0+xxx;Sup1000.0;...
	 * @throws Exception
	 */
	public List<List<Object>> performAggregation(
			IResultSet resultSet, int categoryIndex, 
			Integer categoryLabel, Integer serieIndex, 
			Integer ordinalIndex, List<Point> aggregators, 
			int[] limits, Integer labelIndex,
			String filterString) throws Exception{
		current = new HashMap<Object, List<Object>> ();
		values = new ArrayList<List<Object>>();
		insertedDistinctValues = new HashMap<Object, List<List<Object>>>();
		number = new HashMap<Object, List<Integer>>();

		if (aggregators.isEmpty()){
			resultSet.close();
			return new ArrayList<List<Object>>();
		}
		IResultSetMetaData rsmd = resultSet.getMetaData();
		List<List<IComponentDataFilter>> filters = parseFilterString(filterString, aggregators.size());
		while(resultSet.next()){
			
			Object key1 = resultSet.getString( categoryIndex );
						
			
			
			List<Object> key = new ArrayList<Object>();
			key.add(key1);
			if (serieIndex != null){
				Object key2 = resultSet.getString( serieIndex );
				key.add(key2);
			}
			int columnType = 0;
			int pos = 0;
			
			
			Object val = null;
			
			for(Point p : aggregators){
				// add value
				columnType = rsmd.getColumnType(p.x);
				val =  extractResultSetValue(columnType, resultSet, p.x);
				
				addAggregationValue(p.y,  val, key, pos);
				pos++;
			}
			
			//add category value
			columnType = rsmd.getColumnType(categoryIndex);
			if (current.get(key).size() < key.size() + aggregators.size()){
				current.get(key).add(extractResultSetValue(columnType, resultSet, categoryIndex));
									
				if (serieIndex != null){
					//add category value
					columnType = resultSet.getMetaData().getColumnType(serieIndex);
					current.get(key).add(extractResultSetValue(columnType, resultSet, serieIndex));
					
				}
			}
			if ( current.get(key).size() < key.size() + aggregators.size() + 1){
				if (categoryLabel != null){
					current.get(key).add(resultSet.getString(categoryLabel));
				}
				else{
					current.get(key).add(resultSet.getString(categoryIndex));
				}
				
			}
//			if (current.get(key).size() < key.size() + aggregators.size() + 2){
//				if (categoryLabel != null){
//					current.get(key).add(resultSet.getString(ordinalIndex));
//				}
//				else{
//					current.get(key).add(resultSet.getString(categoryIndex));
//				}
//				
//			}
		}	
			
		resultSet.close();
		int pos = 0;
		for(Point p : aggregators){
			
			if (p.y == DataAggregation.AGG_AVG){
				for(Object l : current.keySet()){

					if (current.get(l).get(pos) instanceof Double){
						current.get(l).set(pos, (Double)((Double)current.get(l).get(pos) / number.get(l).get(pos).doubleValue()));
					}
					else if (current.get(l).get(pos) instanceof Float){
						current.get(l).set(pos, (Float)((Float)current.get(l).get(pos) / number.get(l).get(pos).floatValue()));
					}
					else if (current.get(l).get(pos) instanceof Integer){
						current.get(l).set(pos, (Double)(((Integer)current.get(l).get(pos)).doubleValue() / number.get(l).get(pos).doubleValue()));
					}
					else if (current.get(l).get(pos) instanceof Long){
						current.get(l).set(pos, (Double)(((Long)current.get(l).get(pos)).doubleValue() / number.get(l).get(pos).doubleValue()));
					}
					else if (current.get(l).get(pos) instanceof BigDecimal){
						current.get(l).set(pos, (Double)(((BigDecimal)current.get(l).get(pos)).doubleValue() / number.get(l).get(pos).doubleValue()));
					}
				}
			}
			pos++;

		}
						
			
		for(Object l : current.keySet()){
			List<Object> row = new ArrayList<Object>();
//			row.add(l);
			for(int i = 0; i < aggregators.size(); i++){
				
				// filter application
				boolean discard = false;
				if (!filters.isEmpty() && filters.size() < i ){
					for(IComponentDataFilter f : filters.get(i)){
						if (! f.isSatisfied(current.get(l).get(i))){
							Logger.getLogger(Aggregationer.class).info("Row discarded because a filter is not satisfied");
							discard = true;
							break;
						}
					}
				}
				if (discard){
					row.add(null);
				}
				else{
					row.add(current.get(l).get(i));
				}
				
			}
			for(int i = 0; i < ((List)l).size(); i++){
				row.add(((List)l).get(i));
			}
			
			//categroeiLabel
//			row.add(((List)l).get(0));
			int k = current.get(l).size();
			row.add(current.get(l).get(k - 1));
			row.add(((List)l).get(0));
			
			//ordinal
//			row.add(current.get(l).get(current.get(l).size() - 1));
			values.add(row);
		}
		
		if (limits != null && limits[0] != DatasLimit.LIMIT_NONE){
			
			if (limits[0] == DatasLimit.LIMIT_BOTTOM){
				reorder(0, true);
			}
			else{
				reorder(0, false);
			}
			List<List<Object>> l = new ArrayList<List<Object>>(limits[1]);
			for (int i = 0; i < limits[1] && i < values.size(); i++){
				l.add(values.get(i));
			}
			
			values = l;
//			
		}
		
		
//		if (serieIndex != null){
		if (!values.isEmpty()){
			reorder(values.get(0).size() - 1, true);
		}
				
//		}
//		else{
//			reorder( aggregators.size() + 2);	
//		}
		
		
		
		return values;
	}
	
	private static List<List<IComponentDataFilter>> parseFilterString(String filterstring, int numAgg){
		List<List<IComponentDataFilter>> l = new ArrayList<List<IComponentDataFilter>>();
		if (filterstring == null || filterstring.isEmpty()){
			return l;
		}
		for(String aggFs : filterstring.split(";")){
			
			List<IComponentDataFilter> _l = new ArrayList<IComponentDataFilter>();
			for(String a : aggFs.split("\\+")){
				Type t = null;
				for(Type _t : Type.values()){
					if (a.startsWith(_t.toString())){
						t = _t;
						break;
					}
				}
				Double val = Double.valueOf(a.replace(t.name(), ""));
				
				if (t != null && val != null){
					ValueFilter f = new ValueFilter();
					f.setType(t);
					f.setValue(val);
					_l.add(f);
				}
				
			}
			l.add(_l);
		}
		
		return l;
	}
}
