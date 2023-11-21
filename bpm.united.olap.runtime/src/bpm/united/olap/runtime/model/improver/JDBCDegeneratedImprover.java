package bpm.united.olap.runtime.model.improver;

import java.awt.Point;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.modelbase.sql.query.QuerySelect;
import org.eclipse.datatools.modelbase.sql.query.QuerySelectStatement;
import org.eclipse.datatools.modelbase.sql.query.QueryStatement;
import org.eclipse.datatools.modelbase.sql.query.ResultColumn;
import org.eclipse.datatools.modelbase.sql.query.TableExpression;
import org.eclipse.datatools.modelbase.sql.query.helper.StatementHelper;
import org.eclipse.datatools.sqltools.parsers.sql.query.SQLQueryParserManager;
import org.eclipse.datatools.sqltools.parsers.sql.query.SQLQueryParserManagerProvider;

import bpm.united.olap.api.model.Level;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class JDBCDegeneratedImprover extends DegeneratedHierarchyLevelImprover{

	@Override
	public IQuery improveQuery(IQuery query, OdaInput odaInput) throws Exception{
		List<Point> usedIndex = getUsedIndex();
		
		StringBuilder b = new StringBuilder(); 
		b.append("select distinct ");
		/*IResultSetMetaData rsmd = query.getMetaData();
		
		boolean first = true;
		int count=0;
		for(int i = 0; i < usedIndex.size(); i++){
			if (first){
				first = false;
			}
			else{
				b.append(", ");
			}
			b.append(rsmd.getColumnName((int) (usedIndex.get(i).getX() + 1)));
			usedIndex.get(i).y = count++;
		}
*/
		
		SQLQueryParserManager sqlMgr = SQLQueryParserManagerProvider.getInstance().getParserManager(null, null);
		QueryStatement aggStmt = null;
		List columns = null;
		try {
			aggStmt = sqlMgr.parseQuery(odaInput.getQueryText()).getQueryStatement();
			TableExpression tabl = (TableExpression) StatementHelper.getTablesForStatement((QuerySelectStatement)aggStmt).get(0);
			QuerySelect sel = StatementHelper.getQuerySelectForTableReference(tabl);
			
			
			if (sel.getSelectClause().size() > 0){
				boolean first = true;
				int count=0;
				for(int i = 0; i < usedIndex.size(); i++){
					if (first){
						first = false;
					}
					else{
						b.append(", ");
					}
					ResultColumn  col = (ResultColumn)sel.getSelectClause().get((int) (usedIndex.get(i).getX() ));
					//the damned ResultColumn.getSQL() convert all the SQL in upper case, 
					//so we need to extract it from the ODAInput queryTxt
					int start = odaInput.getQueryText().toUpperCase().indexOf(col.getSQL().toUpperCase());
					if (start > 0){
						b.append(odaInput.getQueryText().substring(start, start + col.getSQL().length()));
					}
					else{
						//XXX
						//this case occurs when this fucking sql parser add some spaces at the getSQL method
						//people who wrote this thing should be hung and gutted alive
						b.append(col.getSQL());
					}
					
					usedIndex.get(i).y = count++;
				}
			}
			else {
				IResultSetMetaData rsmd = query.getMetaData();
				boolean first = true;
				int count=0;
				for(int i = 0; i < usedIndex.size(); i++){
					if (first){
						first = false;
					}
					else{
						b.append(", ");
					}
					
					String colname = rsmd.getColumnName(usedIndex.get(i).x + 1);
					b.append(colname);
					
//					ResultColumn  col = (ResultColumn)sel.getSelectClause().get((int) (usedIndex.get(i).getX() ));
//					//the damned ResultColumn.getSQL() convert all the SQL in upper case, 
//					//so we need to extract it from the ODAInput queryTxt
//					int start = odaInput.getQueryText().toUpperCase().indexOf(col.getSQL().toUpperCase());
//					if (start > 0){
//						b.append(odaInput.getQueryText().substring(start, start + col.getSQL().length()));
//					}
//					else{
//						//XXX
//						//this case occurs when this fucking sql parser add some spaces at the getSQL method
//						//people who wrote this thing should be hung and gutted alive
//						b.append(col.getSQL());
//					}
					
					usedIndex.get(i).y = count++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		
		String sqlQuery = odaInput.getQueryText();
		b.append(sqlQuery.substring(sqlQuery.toLowerCase().replace("\n", " ").indexOf(" from ")));
		
		
		
		
//		/*
//		 * compute offset
//		 */
//		int count = 0;
//		for(int i = 0; i < rsmd.getColumnCount(); i++){
//			boolean added = false;
//			for(Point p : usedIndex){
//				if (p.x == i){
//					p.y = count;
//					added = true;
//				}
//			}
//			
//			if (added){
//				count++;
//			}
//			
//		}

		/*
		 * apply offsets
		 */
		
		for(Level l : levelIndex.keySet()){
			levelIndex.get(l).update(usedIndex);
		}
		
		/*
		 * create new Query
		 */
		OdaInput input = new OdaInput();
		input.setDatasetPrivateProperties((Properties)odaInput.getDatasetPrivateProperties().clone());
		input.setDatasetPublicProperties((Properties)odaInput.getDatasetPublicProperties().clone());
		input.setDatasourcePrivateProperties((Properties)odaInput.getDatasourcePrivateProperties().clone());
		input.setDatasourcePublicProperties((Properties)odaInput.getDatasourcePublicProperties().clone());
		input.setOdaExtensionDataSourceId(odaInput.getOdaExtensionDataSourceId());
		input.setOdaExtensionId(odaInput.getOdaExtensionId());
		
		input.setQueryText(b.toString());
		
		
		/*
		 * close given query
		 */
		query.close();
//		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(query);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(query);

		
		return getOdaQuery(input);
	}

}
