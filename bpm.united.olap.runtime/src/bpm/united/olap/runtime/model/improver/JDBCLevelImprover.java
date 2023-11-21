package bpm.united.olap.runtime.model.improver;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.modelbase.sql.query.QuerySelect;
import org.eclipse.datatools.modelbase.sql.query.QuerySelectStatement;
import org.eclipse.datatools.modelbase.sql.query.QueryStatement;
import org.eclipse.datatools.modelbase.sql.query.ResultColumn;
import org.eclipse.datatools.modelbase.sql.query.TableExpression;
import org.eclipse.datatools.modelbase.sql.query.helper.StatementHelper;
import org.eclipse.datatools.modelbase.sql.query.util.SQLQuerySourceFormat;
import org.eclipse.datatools.sqltools.parsers.sql.query.SQLQueryParserManager;
import org.eclipse.datatools.sqltools.parsers.sql.query.SQLQueryParserManagerProvider;

import bpm.united.olap.api.model.Level;
import bpm.vanilla.platform.core.beans.data.OdaInput;



public class JDBCLevelImprover extends LevelImprover{

	public JDBCLevelImprover(long cacheDataTimeout) {
		super(cacheDataTimeout);
	}

	@Override
	protected List<Object> improve(Level lvl, IQuery query, OdaInput odaInput,  int mbNameIndex,  int mbParentNameIndex,  int mbOrderIndex, List<Integer> propertiesIndexes, int foreignKeyIndex, int labelIndex) throws Exception{
		try {
			String sqlQuery = odaInput.getQueryText();
			
			SQLQueryParserManager manager = SQLQueryParserManagerProvider.getInstance().getParserManager(null, null);
			QueryStatement factStmt = manager.parseQuery(sqlQuery).getQueryStatement();

			SQLQuerySourceFormat sourceFormat = SQLQuerySourceFormat.copyDefaultFormat() ;
			sourceFormat.setPreserveSourceFormat(true);
			manager.setSourceFormat(sourceFormat);
			List<String> cols = new ArrayList<String>();

			TableExpression tabl = (TableExpression) StatementHelper.getTablesForStatement((QuerySelectStatement)factStmt).get(0);
			QuerySelect sel = StatementHelper.getQuerySelectForTableReference(tabl);
			if (sel.getSelectClause().size() > 0){
				for(Object o : sel.getSelectClause()){
					if ( o instanceof ResultColumn){
						ResultColumn  col = (ResultColumn)o;
						int start = sqlQuery.toUpperCase().indexOf(col.getSQL().toUpperCase());
						if (start > 0){
							String s = sqlQuery.substring(start, start + col.getSQL().length());
							cols.add(s);
						}
						else{
							cols.add(col.getSQL());
						}
						
					}
				}
			}
			
//		for(ValueExpressionColumn c : (List<ValueExpressionColumn>)StatementHelper.getEffectiveResultColumns((QuerySelectStatement)factStmt)){
//			cols.add(c.getName());
//		}
			//StatementHelper inefficient on select * from xXX queries
			if (cols.isEmpty()){
				IResultSetMetaData rsmd = query.getMetaData();
				
				for(int i = 0; i < rsmd.getColumnCount(); i++){
					cols.add(rsmd.getColumnName(i + 1));
				}
			}
			
			
			
			
			List<Object> values = new ArrayList<Object>();
			for(int i = 0; i < 7; i++){
				values.add(null);
			}
			
			List<Point> props = new ArrayList<Point>();
			for(int i = 0; i < cols.size(); i++){
				Point p = new Point();
				p.x = i;
				if (i == mbNameIndex){
					values.set(1, p);
				}
				if (i == mbParentNameIndex){
					values.set(2, p);
				}
				if (i == mbOrderIndex){
					values.set(3, p);
				}
				if (i == foreignKeyIndex){
					values.set(5, p);
				}

				if(i == labelIndex) {
					values.set(6, p);
				}

				for(Integer k : propertiesIndexes){
					if ( i == k){
						props.add(p);

					}
				}
				
			}
			values.set(4, props);
			
			
			List<String> toRemove = getToremove(cols, values);
			int count = 0;
			for(int i = 0; i < cols.size(); i++){
				boolean added = false;
				if (toRemove.contains(cols.get(i))){
					continue;
				}
				
				
				for(Object o : values){
					if (o == null){
						continue;
					}
					if (o instanceof Point){
						if (((Point)o).x == i){
							((Point)o).y = count; 
							added = true;
						}
					}
					else {
						for(Point p : (List<Point>)o){
							if (((Point)p).x == i){
								((Point)p).y = count; 
								added = true;
							}
						}
					}
				}
				if (added){
					count ++;
				}
				
			}
			
			cols.removeAll(toRemove);
			
			
			StringBuilder b = new StringBuilder();
			b.append("select distinct ");
			boolean first = true;
			for(String v : cols){
				if (first){
					first = false;
				}
				else{
					b.append(",");
				}
				b.append(v );
			}
//		b.append(" from ");
			
			b.append(sqlQuery.substring(sqlQuery.toLowerCase().replace("\n", " ").indexOf(" from ")));
			
			Logger.getLogger(getClass()).info("Changed SQL Query {" + sqlQuery + "} into {" + b.toString() + "}");
			
			
			List<Object> results = new ArrayList<Object>();
			
			
			OdaInput input = new OdaInput();
			input.setDatasetPrivateProperties((Properties)odaInput.getDatasetPrivateProperties().clone());
			input.setDatasetPublicProperties((Properties)odaInput.getDatasetPublicProperties().clone());
			input.setDatasourcePrivateProperties((Properties)odaInput.getDatasourcePrivateProperties().clone());
			input.setDatasourcePublicProperties((Properties)odaInput.getDatasourcePublicProperties().clone());
			input.setOdaExtensionDataSourceId(odaInput.getOdaExtensionDataSourceId());
			input.setOdaExtensionId(odaInput.getOdaExtensionId());
			
			input.setQueryText(b.toString());
			
			
			results.add(getOdaQuery(input));
			if (values.get(1) != null){
				results.add(((Point)values.get(1)).y);
			}
			else{
				results.add(-1);
			}
			
			if (values.get(2) != null){
				results.add(((Point)values.get(2)).y);
			}
			else{
				results.add(-1);
			}
			
			if (values.get(3) != null){
				results.add(((Point)values.get(3)).y);
			}
			else{
				results.add(-1);
			}
			
			
			List<Integer> _props = new ArrayList<Integer>();
			for(Point p : (List<Point>)values.get(4)){
				_props.add(p.y);
			}
			results.add(_props);
			
			if (values.get(5) != null){
				results.add(((Point)values.get(5)).y);
			}
			else{
				results.add(-1);
			}
			
			if (values.get(6) != null){
				results.add(((Point)values.get(6)).y);
			}
			else{
				results.add(-1);
			}
			
			
			
			
			
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private List<String> getToremove( List<String> cols, List<Object> values) {
		List<String> r = new ArrayList<String>();
		
		for(int i = 0; i < cols.size(); i++){
			boolean found = false;
			for(Object o : values){
				
				if (o == null){
					continue;
				}
				if (o instanceof Point){
					if (((Point)o).x == i){
						found = true;
						break;
					}
				}
				else{
					
					for(Point p : (List<Point>)o){
						if (((Point)p).x == i){
							found = true;
							break;
						}
					}
					if (found){
						break;
					}
				}
				
				
				
			}
			if (!found){
				r.add(cols.get(i));
			}
		}
		
		return r;
	}

	
	
}
