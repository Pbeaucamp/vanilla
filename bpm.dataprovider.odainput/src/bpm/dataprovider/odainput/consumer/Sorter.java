package bpm.dataprovider.odainput.consumer;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class Sorter {

	public static class ComparableList<T> extends ArrayList implements List, Comparable{

		/**
		 * 
		 */
		public ComparableList() {
			super();
			
		}

		/**
		 * @param c
		 */
		public ComparableList(Collection c) {
			super(c);
			
		}

		/**
		 * @param initialCapacity
		 */
		public ComparableList(int initialCapacity) {
			super(initialCapacity);
			
		}

		public int compareTo(Object o) {
			List l = (List)o;
			int result = 0;
			for( int i = 0; i < size(); i++){
				result = ((Comparable)get(i)).compareTo((Comparable)l.get(i));
				if (result != 0){
					return result;
				}
			}
			return result;
		}
		
	}
	/**
	 * read the ResuktSet and sort its rows 
	 * @param resultSet
	 * @param orderIndex
	 * @return
	 * @throws OdaException 
	 */
	protected static List<List<Object>> sort(IResultSet resultSet, final int orderIndex) throws OdaException{
		List<ComparableList<Object>> rows = new ArrayList<ComparableList<Object>>();
		
		IResultSetMetaData rsmd = resultSet.getMetaData();
		
		while( resultSet.next()){
			ComparableList<Object> row = new ComparableList<Object>(rsmd.getColumnCount());
			for(int i = 1; i <= rsmd.getColumnCount(); i++){
				Object val = null;
				
				
				int columnType = resultSet.getMetaData().getColumnType(i);
				switch(columnType){
				
				
				case Types.BIGINT:
				case Types.SMALLINT:
				case Types.TINYINT:
				case Types.INTEGER:
					val = resultSet.getInt(i);
					break;
				case Types.DATE:
					val = resultSet.getDate(i);
					break;
				case Types.TIMESTAMP:
					val = resultSet.getTimestamp(i);
					break;
				case Types.TIME:
					val = resultSet.getTime(i);
					break;
					
				case Types.FLOAT:
				case Types.DOUBLE:
				case Types.REAL:
				case Types.NUMERIC:
					val = resultSet.getDouble(i);
					break;
					
				case Types.DECIMAL:
					val = resultSet.getBigDecimal(i);
					
					break;
				
				default:
					val = resultSet.getString(i);
				}
				
				row.add(val);
			}
			rows.add(row);
		}
		
		Collections.sort(rows, new Comparator<ComparableList<Object>>() {

			public int compare(ComparableList<Object> o1, ComparableList<Object> o2) {
				if (o1 == null){
					if (o2 == null){
						return 0;
					}
					else{
						return 1;
					}
				}
				else if (o2 == null){
					if (o1 == null){
						return 0;
					}
					else{
						return -1;
					}
				}
				else{
					if (orderIndex > -1){
						try{
							return ((Comparable)o1.get(orderIndex - 1)).compareTo((Comparable)o2.get(orderIndex - 1));
						}catch(Exception ex){
							return -1;
						}
					}
					else{
						return -1;
					}
					
				}
			}
		});
		List<List<Object>> r = (List)rows;
		return r;
	}


	
}
