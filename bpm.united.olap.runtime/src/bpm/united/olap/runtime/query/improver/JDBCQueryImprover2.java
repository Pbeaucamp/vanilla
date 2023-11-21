package bpm.united.olap.runtime.query.improver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.modelbase.sql.query.QuerySelect;
import org.eclipse.datatools.modelbase.sql.query.QuerySelectStatement;
import org.eclipse.datatools.modelbase.sql.query.QueryStatement;
import org.eclipse.datatools.modelbase.sql.query.ResultColumn;
import org.eclipse.datatools.modelbase.sql.query.TableExpression;
import org.eclipse.datatools.modelbase.sql.query.TableInDatabase;
import org.eclipse.datatools.modelbase.sql.query.ValueExpressionColumn;
import org.eclipse.datatools.modelbase.sql.query.helper.StatementHelper;
import org.eclipse.datatools.sqltools.parsers.sql.SQLParserException;
import org.eclipse.datatools.sqltools.parsers.sql.SQLParserInternalException;
import org.eclipse.datatools.sqltools.parsers.sql.query.SQLQueryParserManager;
import org.eclipse.datatools.sqltools.parsers.sql.query.SQLQueryParserManagerProvider;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.ClosureLevel;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.parser.calculation.CalculationHelper;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * A class to improve a JDBC query
 * This will use group by and aggregations
 * @author Marc Lanquetin
 *
 */
public class JDBCQueryImprover2 extends AbstractQueryImprover implements IQueryImprover {

	public JDBCQueryImprover2(IVanillaLogger logger, IRuntimeContext runtimeContext) {
		super( logger,runtimeContext);
	}
	
	protected String generateSql(ICubeInstance cubeInstance, DataCellIdentifier2 possibleId, HashMap<DataObjectItem, Integer> measureIndexes, HashMap<DataObjectItem, String> measureAggregations, HashMap<DataObjectItem, List<Object>> mdxWheresKeys){
		StringBuilder b = new StringBuilder();
		
		//select clause
		b.append("select ");
		boolean first = true;
//		for(DataObjectItem i : getLevelColumns()){
//			if (i.getParent().getParent() != cubeInstance.getFactTable().getParent()){
//				//add the FK column from the 
//				continue;
//			}
//			
//			if (first){first=false;}else{b.append(",");}
//			b.append(i.getParent().getName() + "." + i.getName());
//		}
		HashMap<DataObjectItem, String> levelFormulas = new HashMap<DataObjectItem, String>();
		
		for(DataObjectItem i : getLevelColumns()){
			if (first){first=false;}else{b.append(",");}
			SQLQueryParserManager sqlMgr = SQLQueryParserManagerProvider.getInstance().getParserManager(null, null);
			QueryStatement aggStmt = null;
			try {
				aggStmt = sqlMgr.parseQuery(i.getParent().getQueryText()).getQueryStatement();
				TableExpression tabl = (TableExpression) StatementHelper.getTablesForStatement((QuerySelectStatement)aggStmt).get(0);
				QuerySelect sel = StatementHelper.getQuerySelectForTableReference(tabl);
				if (sel.getSelectClause().size() > 0){
					ResultColumn  col = (ResultColumn)sel.getSelectClause().get(i.getParent().getItems().indexOf(i));
					
					int start = i.getParent().getQueryText().toUpperCase().indexOf(col.getSQL().toUpperCase());
					String colVal = null;
					if (start > 0){
//						if(col.getName() == null || col.getName().equals("")) {
//							
//							
//							b.append(i.getParent().getQueryText().substring(start, start + col.getSQL().length()));
							if(col.getSQL().toLowerCase().contains(" as ")) {
								b.append(col.getSQL());
							}
							else {
								b.append(i.getParent().getName() + "." + i.getParent().getQueryText().substring(start + 1 + col.getSQL().lastIndexOf("."), start + col.getSQL().length()));
							}
//							colVal = i.getParent().getName() + "." + i.getParent().getQueryText().substring(start + 1 + col.getSQL().lastIndexOf("."), start + col.getSQL().length());
//						}
//						else {
//							b.append(i.getParent().getName() + "." + col.getName());
//							colVal = i.getParent().getName() + "." + col.getName();
//						}
					}
					else{
						//XXX
						//this case occurs when this fucking sql parser add some spaces at the getSQL method
						//people who wrote this thing should be hung and gutted alive
						b.append(col.getSQL());
					}
					if(col.getSQL().toLowerCase().contains(" as ")) {
						if(colVal != null) {
							levelFormulas.put(i, colVal);
						}
						else {
							levelFormulas.put(i, col.getSQL().substring(0,col.getSQL().toLowerCase().indexOf(" as ")));
						}
					}
					else {
						if(colVal != null) {
							levelFormulas.put(i, colVal);
						}
						else {
//							levelFormulas.put(i, col.getSQL());
							levelFormulas.put(i, i.getParent().getName() + "." + i.getParent().getQueryText().substring(start + 1 + col.getSQL().lastIndexOf("."), start + col.getSQL().length()));
						}
					}
					
					
				}
				else{
					if(getOrderColumns().contains(i)) {
						b.append("max("+i.getParent().getName() + "." + i.getName() + ")");
					}
					else {
						b.append(i.getParent().getName() + "." + i.getName());
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		
		SQLQueryParserManager sqlMgr = SQLQueryParserManagerProvider.getInstance().getParserManager(null, null);
		QueryStatement aggStmt = null;
		List columns = null;
		try {
			aggStmt = sqlMgr.parseQuery(cubeInstance.getFactTable().getQueryText()).getQueryStatement();
			
			columns = StatementHelper.getEffectiveResultColumns((QuerySelectStatement)aggStmt);
			
		} catch (SQLParserException e) {
			e.printStackTrace();
		} catch (SQLParserInternalException e) {
			e.printStackTrace();
		}
		
		List<String> datasetQueryInFrom = new ArrayList<String>();
		
		//add aggregation
		for(DataObjectItem mesUname : measureIndexes.keySet()) {
			String colName = mesUname.getParent().getName() + "." + mesUname.getName();
			String agg = measureAggregations.get(mesUname);
			
			if(first) {
				first = false;
			}
			else {
				b.append(",");
			}
			
			if(columns != null && columns.size() > 0 && columns.size() > measureIndexes.get(mesUname)) {
				TableExpression tabl = (TableExpression) StatementHelper.getTablesForStatement((QuerySelectStatement)aggStmt).get(0);
				QuerySelect sel = StatementHelper.getQuerySelectForTableReference(tabl);
				ResultColumn resCol = StatementHelper.findResultColumnForColumnExpression(sel, ((ValueExpressionColumn) columns.get(measureIndexes.get(mesUname))));
				
				if(resCol.getValueExpr() != null && resCol.getName() != null) {
					if (resCol.getName() == null){
						colName = mesUname.getParent().getName() + "." + resCol.getValueExpr().getName();
					}
					else{
						
						//XXX let's do this
//						colName = mesUname.getId() + "." + resCol.getName();
						
						if(resCol.getLabel() != null) {
							colName = resCol.getLabel();
						}
						else {
							colName = mesUname.getParent().getName() + "." + resCol.getName();
						}
						
						
						String queryFrom = "(" + mesUname.getParent().getQueryText() + ") as " + mesUname.getParent().getName() + " ";
//						String queryFrom = "(" + mesUname.getParent().getQueryText() + ") as " + mesUname.getId() + " ";
						if(!datasetQueryInFrom.contains(queryFrom)) {
							datasetQueryInFrom.add(queryFrom);
						}
					}
					
				}
			}
			
			if(agg.equals(CalculationHelper.LAST) || agg.equals(CalculationHelper.FIRST)) {
				b.append(colName);
			}
			else if(agg.equals(CalculationHelper.COUNTD)) {
				b.append("count (distinct " + colName + ")");
			}
			else {
				b.append(agg + "(" + colName + ")");
			}

		}
		
		//get the different DataObjects
		List<DataObject> tables = new ArrayList<DataObject>();
		
		for(DataObjectItem i : getLevelColumns()){
			if (i.getParent().getParent() != cubeInstance.getFactTable().getParent()){
				continue;
			}
			if (!tables.contains(i.getParent())){
				tables.add(i.getParent());
			}
		}
		for(Relation i : getRelations()){
			if (!tables.contains(i.getLeftItem().getParent())){
				if (i.getLeftItem().getParent().getParent() == cubeInstance.getFactTable().getParent()){
					tables.add(i.getLeftItem().getParent());
				}
				
			}
			if (!tables.contains(i.getRightItem().getParent())){
				if (i.getRightItem().getParent().getParent() == cubeInstance.getFactTable().getParent()){
					tables.add(i.getRightItem().getParent());
				}
				
			}
		}
		
		//XXX let's do this part 2
//		if (!tables.contains(cubeInstance.getFactTable())){
//			tables.add(cubeInstance.getFactTable());
//		}
		if(tables.contains(cubeInstance.getFactTable()) && datasetQueryInFrom.contains("(" + cubeInstance.getFactTable().getQueryText() + ") as " + cubeInstance.getFactTable().getName() + " ")) {
			tables.remove(cubeInstance.getFactTable());
		}
		
		if (!tables.contains(cubeInstance.getFactTable()) && !datasetQueryInFrom.contains("(" + cubeInstance.getFactTable().getQueryText() + ") as " + cubeInstance.getFactTable().getName() + " ")){
			tables.add(cubeInstance.getFactTable());
		}
		
		
		//add from clause
		first = true;
		b.append(" from ");
		
		
		StringBuffer whereConditions = new StringBuffer();
		for(DataObject o : tables){
			if (first){first=false;}else{b.append(", ");}
			SQLQueryParserManager manager = SQLQueryParserManagerProvider.getInstance().getParserManager(null, null);
			try{
				QueryStatement factStmt = manager.parseQuery(o.getQueryText()).getQueryStatement();
				if (StatementHelper.getTablesForStatement(factStmt).size() > 1 ){
					throw new QueryImproverException("Complexe query, forced to generate a subquery");
				}
				else{
					b.append(((TableInDatabase)StatementHelper.getTablesForStatement(factStmt).get(0)).getSourceInfo().getSourceSnippet() + " " + o.getName());
					if (StatementHelper.getSearchCondition(factStmt) != null){
						
						if (whereConditions.length() > 0){
							whereConditions.append(" and ");
						}
						whereConditions.append(StatementHelper.getSearchCondition(factStmt).getSQL());
					}
					
				}
				
				
			}catch(Exception ex){
				b.append("(" + o.getQueryText() + ") " + o.getName());
			}
		}
		
		if(!datasetQueryInFrom.isEmpty()) {
			for(String q : datasetQueryInFrom) {
				if(tables != null && !tables.isEmpty()) {
					b.append(",");
				}
				b.append(q);
			}
		}
		
		//add where clause
		first = true;
		if (whereConditions.length() > 0){
			b.append(" where " + whereConditions.toString());
			first = false;
		}
		for(Relation r : getRelations()){
			if (r.getRightItem().getParent().getParent() != cubeInstance.getFactTable().getParent() || 
				r.getLeftItem().getParent().getParent() != cubeInstance.getFactTable().getParent()){
				continue;
			}
			if (first){b.append(" where ");first=false;}else{b.append(" and ");}
			
			b.append(r.getLeftItem().getParent().getName() + "." + r.getLeftItem().getName());
			b.append("=");
			b.append(r.getRightItem().getParent().getName() + "." + r.getRightItem().getName());
			
		}
		for(DataObjectItem i : getParentFilters().keySet()){
			if (!getParentFilters().get(i).isEmpty()){
				boolean needClosing = false;
				if (first){b.append(" where ");first=false;}else{needClosing = true;b.append(" and (");}
				boolean f = true;
				boolean orNeeded = false;
				
				if(getParentFilters().get(i).contains("NULL")) {
					b.append(i.getParent().getName() + "." + i.getName() + " is null ");
					getParentFilters().get(i).remove("NULL");
					orNeeded = true;
				}
				
				if(!getParentFilters().get(i).isEmpty()) {
 				
					if(orNeeded) {
						b.append("or ");
					}
					
					//XXX
					if(levelFormulas.containsKey(i)) {
						b.append(/*l.getItem().getParent().getName() + "." + */"replace("  + levelFormulas.get(i) + ",'''','') in (");
					}
					else {
						b.append("replace("  + i.getParent().getName() + "." + i.getName() + ",'''','') in(");
					}
					
					for(Object v : getParentFilters().get(i)){
						if (f){f=false;}else{b.append(",");}
						b.append("'");
						if (v.toString().contains("'")){
							b.append(v.toString().replace("'", "''"));
						}
						else{
							b.append(v.toString());
						}
						b.append("'");
					}
					
					for (Member wh : possibleId.getWhereMembers()) {
						if(wh.getParentLevel().getItem().getId().equals(i.getId())) {
							if(!getParentFilters().get(i).contains(wh.getName())) {
								if (f){f=false;}else{b.append(",");}
								b.append("'");
								if (wh.getName().contains("'")){
									b.append(wh.getName().toString().replace("'", "''"));
								}
								else{
									b.append(wh.getName().toString());
								}
								b.append("'");
							}
						}
					}
					
					
					b.append(")");
				}
				if(needClosing) {
					b.append(")");
				}
			}
		}
		//where clause from MDX where
//		boolean firstWhere = true;
		for(DataObjectItem col: mdxWheresKeys.keySet()) {
			if (col.getParent().getParent() != cubeInstance.getFactTable().getParent()){
				continue;
			}
			if (first){b.append(" where ");first=false;}else{b.append(" and ");}
//			if(firstWhere) {
//				firstWhere = false;
//			}
//			else {
//				b.append(" and ");
//			}
			
			//XXX because sqlserver...
			
			if(levelFormulas.containsKey(col)) {
				b.append(/*l.getItem().getParent().getName() + "." + */levelFormulas.get(col) + " in (");
			}
			else {
				int indexOfAlias = col.getParent().getQueryText().indexOf("as " + col.getName());
				
				
				if(indexOfAlias > -1) {
				
					int index = 0;
					int actual = 0;
					
					while(index < indexOfAlias) {
						actual = index;
						index = col.getParent().getQueryText().indexOf(",", index+1);
					}
					
					String maybeTheColumnFormula = col.getParent().getQueryText().substring(actual + 1, indexOfAlias);
					
					b.append(/*l.getItem().getParent().getName() + "." + */maybeTheColumnFormula + " in (");
				}
				else {
//					if(!cubeInstance.getFactTable().getItems().contains(col)) {
						b.append(col.getParent().getName() + "." + col.getName() + " in (");
						
//					}
//					else {
//						b.append(/*l.getItem().getParent().getName() + "." + */col.getName() + " in (");
//					}
				}
			}
			
//			b.append(col.getParent().getName() + "." + col.getName() + " in (");
			
			//XXX Yep sqlserver...
			
			for(Object key : mdxWheresKeys.get(col)) {
				b.append("'" + ((String)key).replace("'", "''") + "'" + ",");
			}
			//remove the last ','
			b.deleteCharAt(b.length() - 1);
			b.append(") ");
		}
		
		
		//add groupBy
		first = true;
		for(Level l : getUsedLevel()){
			if (l.getItem().getParent().getParent() != cubeInstance.getFactTable().getParent()){
				
				for(Level ll : getLevelFkMapping().keySet()){
					if (getLevelFkMapping().get(l) == ll){
						if (first){b.append(" group by ");first=false;}else{b.append(",");}
							b.append(getLevelFkMapping().get(l).getParent().getName() + "." + getLevelFkMapping().get(l).getName());
						break;
					}
				}
				
			}
			else{
				
				
				//we check if the Level is the first that this is not the root Member
				//the root member is only Part of the where clause
				if (l.getParentHierarchy().getLevels().indexOf(l) == 0){
					boolean skipTheLevel = false;
					
					for(ElementDefinition def : possibleId.getIntersections()){
						if (((Member)def).getParentHierarchy() == l.getParentHierarchy() &&
							((Member)def).getParentLevel() == null){
							skipTheLevel = true;
							break;
						}
					}
					if (skipTheLevel){
						continue;
					}
				}
				if (first){b.append(" group by ");first=false;}else{b.append(",");}
				//XXX : LCA commented the table name prefix because 
				//of the level based on calculated column are using alias as name
				
				//XXX Sqlserver is shit : 
				
				if(levelFormulas.containsKey(l.getItem())) {
					b.append(/*l.getItem().getParent().getName() + "." + */levelFormulas.get(l.getItem()));
					
					if(l.getOrderItem() != l.getItem() && levelFormulas.containsKey(l.getOrderItem())) {
						b.append(", " + levelFormulas.get(l.getOrderItem()));
					}
				}
				
				else {
					int indexOfAlias = l.getItem().getParent().getQueryText().indexOf("as " + l.getItem().getName());
					
					
					if(indexOfAlias > -1) {
					
						int index = 0;
						int actual = 0;
						
						while(index < indexOfAlias) {
							actual = index;
							index = l.getItem().getParent().getQueryText().indexOf(",", index+1);
						}
						
						String maybeTheColumnFormula = l.getItem().getParent().getQueryText().substring(actual + 1, indexOfAlias);
						
						b.append(/*l.getItem().getParent().getName() + "." + */maybeTheColumnFormula);
					}
					else {
						b.append(/*l.getItem().getParent().getName() + "." + */l.getItem().getParent().getName() + "." + l.getItem().getName());
//						if(l.getOrderItem() != null && l.getOrderItem() != l.getItem()) {
//							b.append(", " + l.getOrderItem().getName());
//						}
					}
				}
				
				//XXX end SqlServer is shit
				
				if (l instanceof ClosureLevel){
					if (first){b.append(" group by ");first=false;}else{b.append(",");}
					b.append(((ClosureLevel)l).getParentItem().getParent().getName() + "." + ((ClosureLevel)l).getParentItem().getName());
				}
			}
			
		}
		
		return b.toString();
		
	}
	

//	public String getImprovedQuery() {
//		return improvedQuery;
//	}
//	
//	public IDataLocator getImprovedDataLocator() {
//		return improvedDataLocator;
//	}
}
