package bpm.metadata.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.MetaDataException;
import bpm.metadata.layer.business.PathFinder;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.pathfinder.Path;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.scripting.Script;
import bpm.metadata.scripting.Variable;
import bpm.metadata.scripting.VariableComputer;
import bpm.metadata.scripting.VariableType;
import bpm.metadata.tools.Log;
import bpm.vanilla.platform.core.IVanillaContext;

/**
 * Helper class to generate Sql query
 * 
 * @author ludo
 * 
 */
public class SqlQueryGenerator {

	public static EffectiveQuery getQuery(Integer vanillaGroupWeight, IVanillaContext vanillaCtx, IBusinessPackage businessPack, QuerySql query, String groupName, boolean dynamic, Map<Prompt, List<String>> prompts) throws MetaDataException {
		return getQuery(vanillaGroupWeight, vanillaCtx, businessPack, query, groupName, dynamic, prompts, true);
	}
	
	/**
	 * 
	 * If the queryWeigt > vanillaGroupWeight, a RuntimeException will be thrown
	 * 
	 * @param vanillaGroupWeight
	 *            : the VanillaGroup mdt Max weight(if null, the query will be generated whatever is the Query Weight)
	 * @param vanillaCtx
	 * @param businessPack
	 * @param query
	 * @param groupName
	 * @param dynamic
	 * @param prompts
	 * @return
	 * @throws MetaDataException
	 */
	public static EffectiveQuery getQuery(Integer vanillaGroupWeight, IVanillaContext vanillaCtx, IBusinessPackage businessPack, QuerySql query, String groupName, boolean dynamic, Map<Prompt, List<String>> prompts, boolean removePromptWithoutValues) throws MetaDataException {

		//if prompt has no value we remove it from the query
		if(removePromptWithoutValues) {
			List<Prompt> promptsWithoutVal = new ArrayList<Prompt>();
			for(Prompt p : prompts.keySet()) {
				if(prompts.get(p) == null || prompts.get(p).isEmpty()) {
					promptsWithoutVal.add(p);
				}
				else if(prompts.get(p).size() == 1 && prompts.get(p).get(0) == null) {
					promptsWithoutVal.add(p);
				}
			}
			for(Prompt p : promptsWithoutVal) {
				prompts.remove(p);
			}
		}
		
		boolean isOracle = false;
		boolean isDrill = false;
		boolean isPostgres = false;
		int totalQueryWeight = 0;
		boolean isDistinct = query.getDistinct();
		List<IDataStreamElement> select = new ArrayList<IDataStreamElement>(query.getSelect());
		HashMap<ListOfValue, String> condition = query.getCondition();
		List<AggregateFormula> aggs = new ArrayList<AggregateFormula>(query.getAggs());
		List<Ordonable> orderBy = new ArrayList<Ordonable>(query.getOrderBy());
		List<IFilter> filters = new ArrayList<IFilter>(query.getFilters());
		List<Formula> formulas = new ArrayList<Formula>(query.getFormulas());

		List<IDataSource> ds = ((BusinessPackage) businessPack).getDataSources(groupName);
		
		if(ds.size() != 1) {
			MetaDataException e = new MetaDataException("Multiple DataSource Query not supported yet");
			Log.error("more than one datasource not supported yet", e);
			System.out.println("Datasources : ");
			for(IDataSource d : ds) {
				System.out.println(d.getName());
			}
			throw e;
		}
		
		else {
			IDataSource d = ds.get(0);
			if(d instanceof SQLDataSource) {
				if(((SQLConnection)((SQLDataSource)d).getConnection()).getDriverName().startsWith("jdbc:oracle") || ((SQLConnection)((SQLDataSource)d).getConnection()).getDriverName().contains("Oracle") || ((SQLConnection)((SQLDataSource)d).getConnection()).getDriverName().contains("oracle")) {
					isOracle = true;
				}
				else if(((SQLConnection)((SQLDataSource)d).getConnection()).getDriverName().startsWith("jdbc:drill:zk") || ((SQLConnection)((SQLDataSource)d).getConnection()).getDriverName().contains("Drill") || ((SQLConnection)((SQLDataSource)d).getConnection()).getDriverName().contains("drill")) {
					isDrill = true;
				}
				else if(((SQLConnection)((SQLDataSource)d).getConnection()).getDriverName().startsWith("jdbc:postgresql") || ((SQLConnection)((SQLDataSource)d).getConnection()).getDriverName().contains("Postgres") || ((SQLConnection)((SQLDataSource)d).getConnection()).getDriverName().contains("postgres")) {
					isPostgres = true;
				}
			}
		}

		// get all IDataStream
		List<IDataStream> involvedTables = new ArrayList<IDataStream>();
		for(IDataStreamElement elem : select) {
			
			if(elem instanceof ICalculatedElement) {
				String _f = ((ICalculatedElement)elem).getFormula();
				for(IDataStreamElement e : getAllColumns((BusinessPackage) businessPack, groupName)) {
					if(e instanceof ICalculatedElement) {
						if(((ICalculatedElement)elem).getFormula().replace("`", "").contains(e.getDataStream().getOriginName() + "." + e.getOriginName())) {
							if(!involvedTables.contains(e.getDataStream())) {
								involvedTables.add(e.getDataStream());
							}
							_f = _f.replace(e.getDataStream().getName() + "." + e.getName(), ((ICalculatedElement) e).getFormula());
						}
					}
					else {
						try {
							if(((ICalculatedElement)elem).getFormula().replace("`", "").contains(e.getDataStream().getName() + "." + e.getOrigin().getName().substring(e.getOrigin().getName().lastIndexOf(".") + 1))) {
								if(!involvedTables.contains(e.getDataStream())) {
									involvedTables.add(e.getDataStream());
								}
								_f = _f.replace(e.getDataStream().getName() + "." + e.getOrigin().getShortName(), "`" + e.getDataStream().getName() + "`." + e.getOrigin().getShortName());
							}
							if(_f.contains(e.getDataStream().getOriginName())) {
								_f = _f.replace(e.getDataStream().getOriginName(), e.getDataStream().getName());
							}
						} catch (Exception e1) {
							Logger.getLogger(SqlQueryGenerator.class).debug("elem : " + elem.getName() + " -- " + ((ICalculatedElement)elem).getFormula());
							Logger.getLogger(SqlQueryGenerator.class).debug("e " + e.getName() + " -- " + e.getOrigin());
							Logger.getLogger(SqlQueryGenerator.class).debug("e " + e.getDataStream().getName() + " -- " + e.getOuputName());
							e1.printStackTrace();
						}
					}

				}
				((ICalculatedElement)elem).setFormula(_f);
			}
			
			if(!involvedTables.contains(elem.getDataStream()) && !(elem instanceof ICalculatedElement)) {
				involvedTables.add(elem.getDataStream());
			}
		}
		if(aggs != null) {
			for(AggregateFormula e : aggs) {
				for(String s : e.getInvolvedDataStreamNames()) {
					IDataStream t = ds.get(0).getDataStreamNamed(s);
					if(t != null && !involvedTables.contains(t)) {
						involvedTables.add(t);
					}
				}

			}
		}

		// add the tables coming from lov if there arent presents
		if(condition != null) {
			for(ListOfValue l : condition.keySet()) {
				if(!involvedTables.contains(l.getOrigin().getDataStream())) {
					involvedTables.add(l.getOrigin().getDataStream());
				}
			}
		}
		// add the tables coming Filters
		if(filters != null) {
			for(IFilter l : filters) {
				if(!involvedTables.contains(l.getOrigin().getDataStream())) {
					involvedTables.add(l.getOrigin().getDataStream());
				}
			}
		}

		// add the tables coming Prompts
		if(prompts != null) {
			for(Prompt l : prompts.keySet()) {
				if(l.getGotoDataStreamElement() != null) {
					if(!involvedTables.contains(l.getGotoDataStreamElement().getDataStream())) {
						involvedTables.add(l.getGotoDataStreamElement().getDataStream());
					}
				}
			}
		}

		List<IDataStreamElement> allCols = getAllColumns((BusinessPackage) businessPack, groupName);
		int _c = 0;
		for(Formula f : formulas) {
			String _f = f.getFormula();
			for(IDataStreamElement e : allCols) {
				if(e instanceof ICalculatedElement) {
					if(f.getFormula().contains(e.getOutputName())) {
						if(!involvedTables.contains(e.getDataStream())) {
							involvedTables.add(e.getDataStream());
						}
						_f = _f.replace(e.getDataStream().getName() + "." + e.getName(), ((ICalculatedElement) e).getFormula());
					}
				}
				else {
					try {
						if(f.getFormula().replace("`", "").contains(e.getDataStream().getName() + "." + e.getOrigin().getShortName())) {
							if(!involvedTables.contains(e.getDataStream())) {
								involvedTables.add(e.getDataStream());
							}
							_f = _f.replace(e.getDataStream().getName() + "." + e.getOrigin().getShortName(), "`" + e.getDataStream().getName() + "`." + e.getOrigin().getShortName());
						}
					} catch(Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
			for(String s : f.getDataStreamInvolved()) {
				IDataStream t = ds.get(0).getDataStreamNamed(s);
				if(t != null && !involvedTables.contains(t)) {
					involvedTables.add(t);
				}
			}
			ICalculatedElement c = new ICalculatedElement();
			c.setFormula(_f);
			if(f.getName() == null || "".equals(f.getName())) {
				c.setName("formula_" + _c);

			}
			else {
				c.setName(f.getName());
			}

			_c++;
			select.add(c);
		}

		StringBuffer buf = new StringBuffer();
		buf.append("select ");

		if(isDistinct) {
			buf.append("distinct ");
		}

		boolean first = true;

		List<IDataStream> tableFromQuery = new ArrayList<IDataStream>();

		// select elements
		for(IDataStreamElement c : select) {
			// check if the col come from query table
			try {
				if(!(c instanceof ICalculatedElement) && c.getOrigin().getTable().isQuery()) {
					if(!tableFromQuery.contains(c.getDataStream())) {
						tableFromQuery.add(c.getDataStream());
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}

			if(first) {
				first = false;

			}
			else {
				buf.append(", ");
			}

			if(c.isVisibleFor(groupName)) {
				
				boolean isClob = false;
				if(c instanceof IDataStreamElement) {
					try {
						if(((IDataStreamElement)c).getOrigin() != null && ((IDataStreamElement)c).getOrigin().getJavaClass() != null && ((IDataStreamElement)c).getOrigin().getJavaClass().getName().contains(".Object")) {
							isClob = true;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				if(c instanceof ICalculatedElement) {
					try {
						if(((ICalculatedElement)c).getClassType() == ICalculatedElement.OBJECT) {
							isClob = true;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
				if(isClob && isOracle) {
					if(c instanceof ICalculatedElement) {
						buf.append("TO_CHAR(substr(" +((ICalculatedElement)c).getFormula() + ", 1, 4000))");
					}
					else {
						buf.append("TO_CHAR(substr(`" + c.getDataStream().getName() + "`." + c.getOrigin().getShortName() + ", 1, 4000))");
					}
				}
				else {
				
					if(c instanceof ICalculatedElement) {
						buf.append(((ICalculatedElement) c).getFormula() + " AS `" + c.getOutputName() + "`");
	
					}
					else {
						buf.append("`" + c.getDataStream().getName() + "`." + c.getOrigin().getShortName() + " AS `" + c.getOutputName() + "`");
					}
				}
			}
			else {
				buf.append("'XXXXXX'" + " AS `" + c.getOuputName() + "`");
			}

		}

		// aggs
		if(aggs != null) {
			for(AggregateFormula f : aggs) {
				if(first) {
					first = false;

				}
				else {
					buf.append(", ");
				}

				if(!(f.getCol() instanceof ICalculatedElement) && f.getCol().getOrigin().getTable().isQuery()) {
					if(!tableFromQuery.contains(f.getCol().getDataStream())) {
						tableFromQuery.add(f.getCol().getDataStream());
					}
				}
				else if(f.getCol() instanceof ICalculatedElement && (f.getCol().getDataStream() != null && f.getCol().getDataStream().getOrigin().isQuery())) {
					if(!tableFromQuery.contains(f.getCol().getDataStream())) {
						tableFromQuery.add(f.getCol().getDataStream());
					}
				}
				if(f.getCol() instanceof ICalculatedElement) {
					if(f.getFunction().toLowerCase().contains("distinct")) {
						buf.append(" COUNT(DISTINCT (" + ((ICalculatedElement) f.getCol()).getFormula() + "))" + " AS `" + f.getOutputName() + "`");
					}
					else {
						buf.append(f.getFunction() + "(" + ((ICalculatedElement) f.getCol()).getFormula() + ")" + " AS `" + f.getOutputName() + "`");
					}
					
				}
				else {
					if(f.getFunction().toLowerCase().contains("distinct")) {
						buf.append("COUNT(DISTINCT(`" + f.getCol().getDataStream().getName() + "`." + f.getCol().getOrigin().getShortName() + "))" + " AS `" + f.getOutputName() + "`");
					}
					else {
						buf.append(f.getFunction() + "(`" + f.getCol().getDataStream().getName() + "`." + f.getCol().getOrigin().getShortName() + ")" + " AS `" + f.getOutputName() + "`");
					}
				}
			}
		}

		// from
		buf.append(" from ");
		first = true;

		// check for doublons
		List<IDataStream> toKeep = new ArrayList<IDataStream>();
		for(IDataStream t : involvedTables) {
			boolean keep = true;
			for(IDataStream k : toKeep) {
				if(k == t) {
					keep = false;
					break;
				}
			}
			if(keep /* && t.getOrigin().isQuery() */) {
				toKeep.add(t);
			}
		}
		
		// outer joins....
		Path p = new PathFinder(((BusinessPackage) businessPack).getBusinessModel().getRelations(), toKeep, query.getRelationStrategies()).getPath();

		List<IDataStream> toRemove = new ArrayList<IDataStream>();
		if(p != null) {
			for(Relation r : p.getRelations()) {
				if(r.isOuterJoin() && toKeep.contains(r.getLeftTable())) {
					toKeep.remove(r.getLeftTable());
				}
				if(r.isOuterJoin() && toKeep.contains(r.getRightTable())) {
					toKeep.remove(r.getRightTable());
				}

				if(!involvedTables.contains(r.getLeftTable())) {
					totalQueryWeight += r.getLeftTable().getWeight();
				}
			}

		}

		toKeep.removeAll(toRemove);

		List<Relation> outerJoins = new ArrayList<Relation>();
		List<IDataStream> dataStreamsUsedInOuterJoins = new ArrayList<IDataStream>();
		if(p != null) {

			for(Relation r : p.getRelations()) {
				if(r.isOuterJoin()) {
					outerJoins.add(r);
				}
			}
			

			//for(Path l : extractPaths(outerJoins, query.getRelationStrategies())) {
				List<Relation> o = new ArrayList<Relation>(p.getRelations());
				List<Relation> ordered = new ArrayList<Relation>();
				boolean b = _order(ordered, o);
				
				List<Relation> nonOuter = new ArrayList<Relation>();
				for(Relation no : ordered) {
					if(!no.isOuterJoin()) {
						if(outerJoins.size() > 1) {
							outerJoins.add(no);
							toKeep.remove(no.getRightTable());
							toKeep.remove(no.getLeftTable());
						}
						else {
							nonOuter.add(no);
						}
					}
				}
				
				ordered.removeAll(nonOuter);
				
				if(!ordered.isEmpty()){
					if(first) {
						first = false;
					}
					else {
						buf.append(", ");
					}
					
	
					
					buf.append(getOuter(groupName, ordered, dataStreamsUsedInOuterJoins));
	
					for(Relation r : ordered) {
						if(!dataStreamsUsedInOuterJoins.contains(r.getLeftTable())) {
							dataStreamsUsedInOuterJoins.add(r.getLeftTable());
						}
						if(!dataStreamsUsedInOuterJoins.contains(r.getRightTable())) {
							dataStreamsUsedInOuterJoins.add(r.getRightTable());
						}
					}
				}
			//}

		}

		for(IDataStream t : toKeep) {

			if(!tableFromQuery.contains(t) && !(buf.toString().substring(buf.toString().indexOf(" from ")).contains(" " + t.getOrigin().getName() + " `" + t.getName() + "` ") || buf.toString().substring(buf.toString().indexOf(" from ")).contains("(" + t.getOrigin().getName() + " `" + t.getName() + "`"))) {
				if(first) {
					first = false;
				}
				else {
					buf.append(", ");
				}
				if(t.getOrigin().isQuery()) {
					buf.append("(" + t.getOrigin().getName() + ") `" + t.getName() + "`");
				}
				else {
					// alias the table
//					buf.append(t.getOrigin().getName() + " `" + t.getName() + "`");
					//XXX Shitty trick for that postgres pile of shit again
					if(Character.isDigit(t.getOrigin().getName().replace("public.", "").charAt(0)) || t.getOrigin().getName().replace("public.", "").contains("-")) {
						buf.append("\"" + t.getOrigin().getName().replace("public.", "") + "\"" + " `" + t.getName() + "`");
					}
					else {
						buf.append(t.getOrigin().getName() + " `" + t.getName() + "`");
					}
					
				}

			}
		}

		// get tables involved in relations
		if(p != null) {
			for(IDataStream t : p.getUsedTables()) {
				if(tableFromQuery.contains(t)) {
					continue;
				}
				else if(t instanceof SQLDataStream && ((SQLDataStream)t).getSQLType() == SQLDataStream.SQL_QUERY) {
					continue;
				}
				String sql = ((SQLDataStream) t).getSql();

				if(sql != null) {
					if(sql.contains(" from ")) {
						sql = sql.substring(sql.indexOf(" from ") + 6);// , sql.indexOf(" where "));
					}
					else {
						sql = sql.substring(sql.indexOf(" FROM ") + 6);// , sql.indexOf(" where "));
					}
					if(sql.contains(" where ")) {
						sql = sql.substring(0, sql.indexOf(" where "));
					}
					if(sql.contains(" WHERE ")) {
						sql = sql.substring(0, sql.indexOf(" WHERE "));
					}

					if(sql.contains(" having ")) {
						sql = sql.substring(0, sql.indexOf(" having "));
					}
					if(sql.contains(" HAVING ")) {
						sql = sql.substring(0, sql.indexOf(" HAVING "));
					}

					if(sql.contains(" group by ")) {
						sql = sql.substring(0, sql.indexOf(" group by "));
					}
					if(sql.contains(" GROUP BY ")) {
						sql = sql.substring(0, sql.indexOf(" GROUP BY "));
					}

					if(sql.contains(" order by ")) {
						sql = sql.substring(0, sql.indexOf(" order by "));
					}
					if(sql.contains(" ORDER BY ")) {
						sql = sql.substring(0, sql.indexOf(" ORDER BY "));
					}

					if(sql.contains(" union ")) {
						sql = sql.substring(0, sql.indexOf(" union "));
					}
					if(sql.contains(" UNION ")) {
						sql = sql.substring(0, sql.indexOf(" UNION "));
					}
				}
				else {
					//dont know what to do
					sql = t.getOrigin().getName() + " `" + t.getName() + "`";
				}

				String[] split = sql.split(",");

				for(String s : split) {
					if(!buf.toString().substring(buf.toString().indexOf(" from ") + 5).contains(s)) {
						if(first) {
							first = false;
						}
						else {
							buf.append(", ");
						}
						//dont know what to do
						buf.append(s);
					}
				}
			}
		}

		// table from path based on SQL handwrited and in tableFromquery
		if(p != null) {
			for(IDataStream t : p.getUsedTables()) {
				if(tableFromQuery.contains(t) || (t instanceof SQLDataStream && ((SQLDataStream)t).getSQLType() == SQLDataStream.SQL_QUERY)) {
					boolean skip = false;

					for(Relation r : p.getRelations()) {
						if((r.isOuterJoin() || dataStreamsUsedInOuterJoins.contains(t)) && r.isUsingTable(t)) {
							skip = true;
							break;
						}
					}
					if(skip) {
						continue;
					}

					if(first) {
						first = false;
					}
					else {
						buf.append(", ");
					}
				
					buf.append("(" + ((SQLDataStream) t).getSql() + ") `" + t.getName() + "` ");
				}
			}
		}
		// get tables name from tableFromQuery
		for(IDataStream t : tableFromQuery) {
			if(p != null && p.contains(t)) {
				continue;
			}
			
			String sql = ((SQLDataStream) t).getSql();
//			if(buf.indexOf("(" + sql + ") `" + t.getName() + "` ") < 0) {
				if(first) {
					first = false;
				}
				else {
					buf.append(", ");
				}
				buf.append("(" + sql + ") `" + t.getName() + "` ");
//			}

		}

		// where

		first = true;

		if(buf.toString().endsWith(" from ")) {
			buf.replace(buf.toString().lastIndexOf(" from "), buf.toString().lastIndexOf(" from ") + 6, " ");
		}
		p = new PathFinder(((BusinessPackage) businessPack).getBusinessModel().getRelations(), involvedTables, query.getRelationStrategies()).getPath();
		int outers = 0;
		if(p != null) {
			for(Relation r : p.getRelations()) {
				if(r.isOuterJoin()) {
					outers++;
				}
			}
		}
		if(p != null && outers < 2) {

			List<String> whereClauses = new ArrayList<String>();
			boolean c = false;

			String[] froms = null;

			if(buf.toString().contains(" where ")) {
				froms = buf.substring(buf.indexOf(" from ") + 6, buf.indexOf(" where ")).split(", ");
			}
			else {
				froms = buf.substring(buf.indexOf(" from ") + 6).replace(", ", "]").split("]");
			}
			List<String> mis = new ArrayList<String>();

			for(String s : froms) {
				mis.add(s);
			}

			for(Relation r : p.getRelations()) {
				if(r.isOuterJoin()) {
					continue;
				}
				boolean lf = false;
				boolean rf = false;
				for(String s : mis) {

					if(s.trim().equals("`" + r.getLeftTable().getName() + "`")) {
						lf = true;
					}

					if(s.trim().equals("`" + r.getRightTable().getName() + "`")) {
						rf = true;
					}

				}

				if(!lf) {
					int _i = buf.indexOf(" from ") + 6;
					int _sz = buf.length();
					String s = buf.toString().substring(_i);

					if(((SQLDataStream) r.getLeftTable()).getSql() != null) {
						if(!s.contains("(" + ((SQLDataStream) r.getLeftTable()).getSql() + ") `" + r.getLeftTable().getName() + "`")) {
							s = "(" + ((SQLDataStream) r.getLeftTable()).getSql() + ") `" + r.getLeftTable().getName() + "`" + ", " + s;
						}
						else {
							s = buf.toString().substring(0, _i) + s;

						}
					}
					else {
						s = r.getLeftTable().getOrigin().getName() + ", " + s;

						s = buf.toString().substring(0, _i) + s;

						if(!(buf.toString().contains(" " + r.getLeftTable().getOrigin().getName() + " ") || buf.toString().contains("(" + r.getLeftTable().getOrigin().getName() + " "))) {
							buf = new StringBuffer();
							buf.append(s);
							mis.add("`" + r.getLeftTable().getName() + "`");
						}
					}

				}

				if(!rf) {
					int _i = buf.indexOf(" from ") + 6;
					int _sz = buf.length();
					String s = buf.toString().substring(_i);

					if(((SQLDataStream) r.getRightTable()).getSql() != null) {
						if(!s.contains("(" + ((SQLDataStream) r.getRightTable()).getSql() + ") `" + r.getRightTable().getName() + "`")) {
							s = "(" + ((SQLDataStream) r.getRightTable()).getSql() + ") `" + r.getRightTable().getName() + "`" + ", " + s;
						}
						else {
							s = buf.toString().substring(0, _i) + s;
						}
					}
					else {
						s = r.getRightTable().getOrigin().getName() + ", " + s;

						s = buf.toString().substring(0, _i) + s;

						if(!(buf.toString().contains(" " + r.getRightTable().getOrigin().getName() + " ") || buf.toString().contains("(" + r.getRightTable().getOrigin().getName() + " "))) {
							buf = new StringBuffer();
							buf.append(s);
							mis.add("`" + r.getLeftTable().getName() + "`");
						}
					}
				}

				for(String s : whereClauses) {
					if(s.equals(r.getWhereClause())) {
						c = true;
						break;
					}
				}
				if(!c && first) {
					first = false;
					buf.append(" where ");
				}
				else {
					buf.append(" AND ");
				}
				buf.append(r.getWhereClause());
				whereClauses.add(r.getWhereClause());
			}
		}

		// insert tables needed for where clauses

		// where clause for LovS
		if(condition != null) {
			for(ListOfValue l : condition.keySet()) {
				if(first) {
					first = false;
					buf.append(" where ");
				}
				else {
					buf.append(" AND ");
				}

				if(l.getOrigin() instanceof ICalculatedElement) {
					buf.append(((ICalculatedElement) l.getOrigin()).getFormula() + "=");

					try {
						String classe = ((ICalculatedElement) l.getOrigin()).getJavaClassName();
						if(classe.equals(String.class.getName()) || classe.equals(Date.class.getName()) || classe.equals(java.sql.Timestamp.class.getName()) || classe.equals(java.sql.Time.class.getName()) || classe.equals(java.sql.Date.class.getName())) {
							buf.append("'" + condition.get(l) + "'");
						}
						else {
							buf.append(condition.get(l));
						}
					} catch(Exception e1) {
						e1.printStackTrace();
					}
				}
				else {
					buf.append(l.getOrigin().getName() + "." + l.getOrigin().getOrigin().getShortName() + "=");

					try {
						if(l.getOrigin().getOrigin().getClassName().equals(String.class.getName()) || l.getOrigin().getOrigin().getClassName().equals(Date.class.getName()) || l.getOrigin().getOrigin().getClassName().equals(java.sql.Timestamp.class.getName()) || l.getOrigin().getOrigin().getClassName().equals(java.sql.Time.class.getName()) || l.getOrigin().getOrigin().getClassName().equals(java.sql.Date.class.getName())) {
							buf.append("'" + condition.get(l) + "'");
						}
						else {
							buf.append(condition.get(l));
						}
					} catch(Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}

		// where clause for Prompts
		if(prompts != null) {
			for(Prompt l : prompts.keySet()) {
				if(first) {
					first = false;
					buf.append(" where ");
				}
				else {
					buf.append(" AND ");
				}
				
				if(l.getGotoDataStreamElement() != null) {
				
					if(l.getGotoDataStreamElement() instanceof ICalculatedElement) {
						if(l.getOperator().equals("IN") || l.getOperator().equals("LIKE") || l.getOperator().equals("BETWEEN")) {
							buf.append(((ICalculatedElement) l.getGotoDataStreamElement()).getFormula() + " " + l.getOperator());
						}
						else {
							buf.append(((ICalculatedElement) l.getGotoDataStreamElement()).getFormula() + l.getOperator());
						}
		
					}
					else {
						if(l.getOperator().equals("IN") || l.getOperator().equals("LIKE") || l.getOperator().equals("BETWEEN")) {
							buf.append("`" + l.getGotoDataStreamElement().getDataStream().getName() + "`." + l.getGotoDataStreamElement().getOrigin().getShortName() + " " + l.getOperator());
						}
						else {
							buf.append("`" + l.getGotoDataStreamElement().getDataStream().getName() + "`." + l.getGotoDataStreamElement().getOrigin().getShortName() + l.getOperator());
						}
		
					}
		
					try {
						boolean fValue = true;
						for(String s : prompts.get(l)) {
		
							if(fValue) {
								fValue = false;
								if(l.getOperator().equals("IN")) {
									buf.append("(");
								}
								else if(l.getOperator().equals("LIKE")) {
									buf.append(" ");
								}
								else if(l.getOperator().equals("BETWEEN")) {
									buf.append(" ");
								}
							}
							else {
								if(l.getOperator().equals("BETWEEN")) {
									buf.append(" AND ");
								}
								else {
									buf.append(",");
								}
							}
		
							if(!(l.getGotoDataStreamElement() instanceof ICalculatedElement) && (l.getGotoDataStreamElement().getOrigin().getClassName().equals(String.class.getName()) || l.getGotoDataStreamElement().getOrigin().getClassName().equals(Date.class.getName()) || l.getGotoDataStreamElement().getOrigin().getClassName().equals(java.sql.Timestamp.class.getName()) || l.getGotoDataStreamElement().getOrigin().getClassName().equals(java.sql.Time.class.getName()) || l.getGotoDataStreamElement().getOrigin().getClassName().equals(java.sql.Date.class.getName()))) {
								if(dynamic) {
									buf.append("'\"+" + l.getName() + "+\"'");
								}
								else {
		
									Class<?> c = String.class;
									if(l.getGotoDataStreamElement() instanceof ICalculatedElement) {
										try {
											c = Class.forName(((ICalculatedElement) l.getGotoDataStreamElement()).getJavaClassName());
										} catch(Exception ex) {
		
										}
									}
									else {
										try {
											c = Class.forName(l.getGotoDataStreamElement().getOrigin().getClassName());
										} catch(Exception ex) {
		
										}
		
									}
									if(Number.class.isAssignableFrom(c) && !l.getOperator().equals("LIKE")) {
										buf.append(s);
									}
									else {
										
										try {
											
											if (StringUtils.containsIgnoreCase(s, "select") && StringUtils.containsIgnoreCase(s, "from"))
											{
											    buf.append(s);
											    break;
											}
											else {
												buf.append("'" + s + "'");
											}
										} catch (Exception e1) {
											buf.append("'" + s + "'");
										}
									}
		
								}
		
							}
							else {
								if(dynamic) {
									buf.append("'\"+" + l.getName() + "+\"'");
								}
								else {
									Class<?> c = String.class;
									if(l.getGotoDataStreamElement() instanceof ICalculatedElement) {
										try {
											c = Class.forName(((ICalculatedElement) l.getGotoDataStreamElement()).getJavaClassName());
										} catch(Exception ex) {
		
										}
									}
									else {
										try {
											c = Class.forName(l.getGotoDataStreamElement().getOrigin().getClassName());
										} catch(Exception ex) {
		
										}
									}
		
									if(Number.class.isAssignableFrom(c) && !l.getOperator().equals("LIKE")) {
										buf.append(s);
									}
									else {
										try {
											
											if (StringUtils.containsIgnoreCase(s, "select") && StringUtils.containsIgnoreCase(s, "from"))
											{
											    buf.append(s);
											    break;
											}
											else {
												buf.append("'" + s + "'");
											}
										} catch (Exception e1) {
											buf.append("'" + s + "'");
										}
									}
		
								}
							}
						}
						if(fValue) {
							if(!(l.getGotoDataStreamElement() instanceof ICalculatedElement) && (l.getGotoDataStreamElement().getOrigin().getClassName().equals(String.class.getName()) || l.getGotoDataStreamElement().getOrigin().getClassName().equals(Date.class.getName()) || l.getGotoDataStreamElement().getOrigin().getClassName().equals(java.sql.Timestamp.class.getName()) || l.getGotoDataStreamElement().getOrigin().getClassName().equals(java.sql.Time.class.getName()) || l.getGotoDataStreamElement().getOrigin().getClassName().equals(java.sql.Date.class.getName()))) {
								if(dynamic) {
									buf.append("'\"+" + l.getName() + "+\"'");
								}
								else {
		
								}
		
							}
							else {
								if(dynamic) {
									buf.append("'\"+" + l.getName() + "+\"'");
								}
								else {
		
								}
							}
		
						}
						if(l.getOperator().equals("IN")) {
							buf.append(")");
						}
		
					} catch(Exception e1) {
						buf.append("''");
						e1.printStackTrace();
					}
				}
				else {
					if(l.getGotoSql() != null && !l.getGotoSql().isEmpty()) {
					
						String pmp = l.getGotoSql();
						if(prompts.get(l) != null && prompts.get(l).size() > 0) {
						
							if(l.getPromptType().equals("java.lang.String") || l.getOperator().equals("LIKE")) {
								pmp = pmp.replace("?", "'" + prompts.get(l).get(0) + "'");
							}
							else {
								pmp = pmp.replace("?", prompts.get(l).get(0));
							}
						}
						
						buf.append(pmp);
					}
				}
			}

		}

		// where clause for Filters
		if(filters != null) {
			for(IFilter l : filters) {
				if(first) {
					first = false;
					buf.append(" where ");
				}
				else {
					buf.append(" AND ");
				}

				buf.append(l.getSqlWhereClause());

			}
		}

		// we detect from which businessTable the select columns come from
		// then we add the security filters in the query
		//
		for(IDataStreamElement e : select) {
			for(IBusinessTable t : businessPack.getBusinessTables(groupName)) {
				if(!t.getFilterFor(groupName).isEmpty() && t.getColumns(groupName).contains(e)) {
					for(IFilter l : t.getFilterFor(groupName)) {
						if(l.getOrigin() == e) {
							String filterClause = l.getSqlWhereClause();

							if(buf.toString().contains(filterClause)) {
								continue;
							}
							if(first) {
								first = false;
								buf.append(" where ");
							}
							else {
								buf.append(" AND ");
							}

							buf.append(filterClause);
						}

					}

				}
			}
		}
		// end debug

		// where from DataStream's filters
		for(IDataStream stream : involvedTables) {
			boolean outerJoinForStream = false;
			for(IFilter l : stream.getFilterFor(groupName)) {

				// do not take the filter on stream used by outer Joins
				// because the filters will be generated in the join clauses
				for(Relation r : outerJoins) {
					if(r.isUsingTable(stream)) {
						outerJoinForStream = true;
						break;
					}
				}
				if(outerJoinForStream) {
					break;
				}
				String filterClause = l.getSqlWhereClause();

				if(buf.toString().contains(filterClause)) {
					continue;
				}
				if(first) {
					first = false;
					buf.append(" where ");
				}
				else {
					buf.append(" AND ");
				}

				buf.append(filterClause);

			}

			if(outerJoinForStream) {
				continue;
			}
			for(IFilter l : stream.getGenericFilters()) {
				String filterClause = l.getSqlWhereClause();

				if(buf.toString().contains(filterClause)) {
					continue;
				}
				if(first) {
					first = false;
					buf.append(" where ");
				}
				else {
					buf.append(" AND ");
				}

				buf.append(filterClause);

			}
		}

		/*
		 * not really that is requested get Filters from DataSTream that are coming from Path and not present in involved relations
		 */

		if(p != null) {
			List<IDataStream> _treated = new ArrayList<IDataStream>();
			for(IDataStreamElement e : p.getUsedItems()) {
				if(!involvedTables.contains(e.getDataStream()) && !_treated.contains(e.getDataStream())) {
					_treated.add(e.getDataStream());

					for(IFilter l : e.getDataStream().getFilterFor(groupName)) {
						String filterClause = l.getSqlWhereClause();

						if(buf.toString().contains(filterClause)) {
							continue;
						}
						if(first) {
							first = false;
							buf.append(" where ");
						}
						else {
							buf.append(" AND ");
						}

						buf.append(filterClause);

					}
					for(IFilter l : e.getDataStream().getGenericFilters()) {
						String filterClause = l.getSqlWhereClause();

						if(buf.toString().contains(filterClause)) {
							continue;
						}
						if(first) {
							first = false;
							buf.append(" where ");
						}
						else {
							buf.append(" AND ");
						}

						buf.append(filterClause);

					}
				}
			}

		}

		// group by
		first = true;

		if(aggs != null && !aggs.isEmpty()) {
			for(IDataStreamElement e : select) {
				boolean isClob = false;
				if(e instanceof IDataStreamElement) {
					try {
						if(((IDataStreamElement)e).getOrigin() != null && ((IDataStreamElement)e).getOrigin().getJavaClass() != null && ((IDataStreamElement)e).getOrigin().getJavaClass().getName().contains(".Object")) {
							isClob = true;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				if(e instanceof ICalculatedElement) {
					try {
						if(containsAggregation((ICalculatedElement)e)) {
							continue;
						}
						if(((ICalculatedElement)e).getClassType() == ICalculatedElement.OBJECT) {
							isClob = true;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				if(first) {
					first = false;
					buf.append(" group by ");
				}
				else {
					buf.append(", ");
				}

				//Because why the fuck would Microsoft respect SQL standards ???
				//Oracle is better. You're not allowed to put clob in group by but adding the clob in the group by is mandatory.
				//Yep.
				if(isClob && isOracle) {
					if(e instanceof ICalculatedElement) {
						buf.append("TO_CHAR(substr(" +((ICalculatedElement)e).getFormula() + ", 1, 4000))");
					}
					else {
						buf.append("TO_CHAR(substr(`" + e.getDataStream().getName() + "`." + e.getOrigin().getShortName() + ", 1, 4000))");
					}
				}
				else {
					if(e instanceof ICalculatedElement) {

						buf.append(((ICalculatedElement)e).getFormula());
					}
					
	//				if(e instanceof ICalculatedElement) {
	//					buf.append("`" + e.getOutputName() + "`");
	//				}
					else {
						buf.append("`" + e.getDataStream().getName() + "`." + e.getOrigin().getShortName());
					}
				}
			}
		}

		// order by
		first = true;
		if(orderBy != null) {
			ORDER:for(Ordonable e : orderBy) {
				if(e instanceof IDataStreamElement) {
					try {
						for(AggregateFormula f : aggs) {
							if(((IDataStreamElement)e).getName().equals(f.getCol().getName())) {
								continue ORDER;
							}
						}
						
						if(((IDataStreamElement)e).getOrigin() != null && ((IDataStreamElement)e).getOrigin().getJavaClass() != null 
								&& (((IDataStreamElement)e).getOrigin().getJavaClass().getName().contains(".Object") 
										|| (((IDataStreamElement)e).getOrigin() instanceof SQLColumn && ((SQLColumn)((IDataStreamElement)e).getOrigin()).getSqlType().equals("LONG")))) {
							continue;
						}
					} catch (Exception e1) {
//						e1.printStackTrace();
					}
				}
				if(e instanceof ICalculatedElement) {
					try {
						if(((ICalculatedElement)e).getClassType() == ICalculatedElement.OBJECT) {
							continue;
						}
						else if(containsAggregation((ICalculatedElement)e)) {
							continue;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				if(first) {
					first = false;
					buf.append(" order by ");
				}
				else {
					buf.append(", ");
				}

				buf.append("`" + e.getOutputName() + "`");

			}
		}

		boolean limitFound = false;

		for(IDataStream t : tableFromQuery) {
			String sql = ((SQLDataStream) t).getSql();

			int end = sql.indexOf(" limit ");
			if(end == -1) {
				end = sql.indexOf(" LIMIT ");
			}

			if(end != -1) {
				limitFound = true;
			}

			if(sql.contains(" group by ") && (!buf.toString().contains(" group by ") && !buf.toString().contains(" GROUP BY "))) {
				buf.append(sql.substring(sql.indexOf(" group by ")));

			}
			else if(sql.contains(" GROUP BY ") && (!buf.toString().contains(" group by ") && !buf.toString().contains(" GROUP BY "))) {
				buf.append(sql.substring(sql.indexOf(" GROUP BY ")));
			}

			if(sql.contains(" order by ") && (!buf.toString().contains(" order by ") && !buf.toString().contains(" ORDER BY "))) {
				buf.append(sql.substring(sql.indexOf(" order by ")));

			}
			else if(sql.contains(" ORDER BY ") && (!buf.toString().contains(" order by ") && !buf.toString().contains(" ORDER BY "))) {
				buf.append(sql.substring(sql.indexOf(" ORDER BY ")));
			}

		}

		/*
		 * execute scripts
		 */
		HashMap<Variable, String> variables = new HashMap<Variable, String>();
		List<Script> scriptWithVariables = new ArrayList<>();
		for(Script script : businessPack.getBusinessModel().getModel().getScripts()) {
			if(script.getDefinition().indexOf("{$", script.getDefinition().indexOf("=")) > 0) {
				scriptWithVariables.add(script);
				continue;
			}
			HashMap<Variable, String> v = new VariableComputer().computeVariablesValues(vanillaCtx, businessPack.getBusinessModel().getModel().getVariables(), script, groupName, new HashMap<Variable, String>());
			for(Variable k : v.keySet()) {
				variables.put(k, v.get(k));
			}
		}
		for(Script script : scriptWithVariables) {
			HashMap<Variable, String> v = new VariableComputer().computeVariablesValues(vanillaCtx, businessPack.getBusinessModel().getModel().getVariables(), script, groupName, variables);
			for(Variable k : v.keySet()) {
				variables.put(k, v.get(k));
			}
		}

		String queryString = buf.toString();
		/*
		 * replace Variables Symbols by their values
		 */

		for(Variable v : variables.keySet()) {
			if(v.getType() == VariableType.String) {
				if(!variables.get(v).startsWith("(")) {
					queryString = queryString.replace(v.getSymbol(), " '" + variables.get(v) + "' ");
				}
				else {
					queryString = queryString.replace(v.getSymbol(), " " + variables.get(v) + " ");
				}
			}
			else {
				queryString = queryString.replace(v.getSymbol(), " " + variables.get(v) + " ");
			}
		}

		for(IDataStream t : involvedTables) {
			totalQueryWeight += t.getWeight();
		}

		if(query.getLimit() > 0) {
			if(isOracle) {
				if(StringUtils.containsIgnoreCase(queryString, "where ")) {
					queryString = queryString.replaceAll("(?i)where ", "where ROWNUM <= " + query.getLimit() + " and ");
				}
				else {
					int orderIndex = queryString.toLowerCase().indexOf("order by");
					int groupIndex = queryString.toLowerCase().indexOf("group by");
					
					if(groupIndex == -1 && orderIndex == -1) {
						queryString = queryString + " where ROWNUM < " + query.getLimit();
					}
					else if(groupIndex == -1) {
						queryString = queryString.replaceAll("(?i)order by ", "where ROWNUM <= " + query.getLimit() + " order by ");
					}
					else {
						queryString = queryString.replaceAll("(?i)group by ", "where ROWNUM <= " + query.getLimit() + " group by ");
					}
				}
				
			}
			else {
				queryString += " limit " + query.getLimit();
			}
		}

		/*
		 * check the Query Weight
		 */
		if(vanillaGroupWeight != null) {
			if(vanillaGroupWeight < totalQueryWeight) {
				throw new FmdtQueryRuntimeException("The FMDT Query Weight is superior to the Vanilla Group Weight. It cannot be executed.\n The VanillaGroup Weight should be at least " + (totalQueryWeight + 1) + " for the Group " + groupName, queryString, totalQueryWeight);
			}
		}
		
		try {
			IConnection con = ds.get(0).getConnections(groupName).get(0);
			if(con instanceof SQLConnection) {
				String jdbcPrefix = ((SQLConnection)con).getJdbcPrefix();
				queryString = NonCompliantDriverQueryModifier.editQuery(queryString, jdbcPrefix);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if(isDrill) {
			queryString = queryString.replace("\"", "");
		}
		
		return new EffectiveQuery(queryString, totalQueryWeight);
	}
	
	private static boolean containsAggregation(ICalculatedElement e) {
		
		for(String agg : IDataStreamElement.MEASURE_DEFAULT_BEHAVIOR) {
			if(e.getFormula().toLowerCase().contains(agg.toLowerCase() + "(")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param groupName
	 * @return all columns present in the table
	 */
	private static List<IDataStreamElement> getAllColumns(IBusinessTable table, String groupName) {
		List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
		l.addAll(table.getColumns(groupName));
		for(IBusinessTable t : table.getChilds(groupName)) {
			l.addAll(getAllColumns(t, groupName));
		}

		return l;
	}

	/**
	 * 
	 * @param groupName
	 * @return all columns present in the package
	 */
	private static List<IDataStreamElement> getAllColumns(BusinessPackage pack, String groupName) {
		List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
		for(IBusinessTable t : pack.getBusinessTables(groupName)) {
			l.addAll(getAllColumns(t, groupName));

		}
		return l;
	}

	private static List<Path> extractPaths(List<Relation> outer, List<RelationStrategy> relationStrategies) {
		List<Path> paths = new ArrayList<Path>();
		/*
		 * test if a path exists between the relations
		 */
		List<IDataStream> toKeep = new ArrayList<IDataStream>();
		for(Relation r : outer) {
			if(!toKeep.contains(r.getLeftTable())) {
				toKeep.add(r.getLeftTable());
			}
			if(!toKeep.contains(r.getRightTable())) {
				toKeep.add(r.getRightTable());
			}
		}
		Path p = new PathFinder(outer, toKeep, relationStrategies).getPath();
		if(p == null) {
			return paths;
		}
		if(p.getRelations().size() == outer.size()) {
			paths.add(p);

		}
		else {
			paths.add(p);
			List<Relation> o = new ArrayList<Relation>(outer);
			o.removeAll(p.getRelations());
			paths.addAll(extractPaths(o, relationStrategies));
		}
		return paths;

	}

	private static List<Relation> order(List<Relation> l) {
		HashMap<Relation, Integer> nb = new HashMap<Relation, Integer>();
		HashMap<IDataStream, Integer> nbStr = new HashMap<IDataStream, Integer>();

		for(Relation r : l) {
			if(nb.get(r) == null) {
				nb.put(r, 1);
			}
			else {
				nb.put(r, nb.get(r) + 1);
			}

			if(nbStr.get(r.getLeftTable()) == null) {
				nbStr.put(r.getLeftTable(), 1);
			}
			else {
				nbStr.put(r.getLeftTable(), nbStr.get(r.getLeftTable()) + 1);
			}
			if(nbStr.get(r.getRightTable()) == null) {
				nbStr.put(r.getRightTable(), 1);
			}
			else {
				nbStr.put(r.getRightTable(), nbStr.get(r.getRightTable()) + 1);
			}

		}

		Relation first = getFirst(nb, nbStr);
		Relation last = getLast(nb, nbStr, first);
		if(first == last) {
			last = null;
		}

		List<Relation> remaining = new ArrayList<Relation>(l);
		remaining.remove(first);
		remaining.remove(last);

		List<Relation> result = new ArrayList<Relation>();

		if(first != null) {
			result.add(first);
		}

		order(result, remaining);

		for(Relation r : remaining) {
			result.add(r);
		}

		if(last != null) {
			result.add(last);
		}

		for(int i = 1; i < remaining.size(); i++) {

			if(nb.get(result.get(i)) == 1 && (nbStr.get(result.get(i).getLeftTable()) == 1 || nbStr.get(result.get(i).getRightTable()) == 1)) {
				Relation buf = result.get(0);
				result.set(0, result.get(i));
				result.set(i, buf);

			}
		}

		return result;

	}

	private static List<Relation> order(List<Relation> current, List<Relation> remaining) {
		if(remaining.isEmpty()) {
			return current;
		}

		Relation _blast = null;
		if(current.size() > 1) {
			current.get(current.size() - 2);
		}
		Relation toInsert = null;

		for(Relation r : remaining) {

			if(_blast != null && (r.isUsingTable(_blast.getLeftTable()) || r.isUsingTable(_blast.getRightTable()))) {
				toInsert = r;
				break;
			}
		}
		if(toInsert != null) {
			current.add(current.size() - 1, toInsert);
			current.remove(null);
		}
		else {
			return current;
		}

		remaining.remove(toInsert);
		return order(current, remaining);

	}

	private static Relation getLast(HashMap<Relation, Integer> nb, HashMap<IDataStream, Integer> nbStr, Relation first) {
		List<Relation> possible = new ArrayList<Relation>();
		Relation last = null;

		for(Relation r : nb.keySet()) {
			if(nb.get(r) == 1 && (nbStr.get(r.getLeftTable()) == 1 || nbStr.get(r.getRightTable()) == 1)) {
				if(r != first) {
					possible.add(r);
				}

			}
		}

		for(Relation r : possible) {
			int score = nbStr.get(r.getLeftTable()) + nbStr.get(r.getRightTable());
			if(last == null || score > nbStr.get(last.getLeftTable()) + nbStr.get(last.getRightTable())) {
				last = r;
			}
			else if(score == nbStr.get(last.getLeftTable()) + nbStr.get(last.getRightTable())) {

				if(r.getName().compareTo(last.getName()) < 0) {
					last = r;
				}
			}

		}
		return last;
	}

	private static Relation getFirst(HashMap<Relation, Integer> nb, HashMap<IDataStream, Integer> nbStr) {
		List<Relation> possible = new ArrayList<Relation>();
		Relation first = null;

		for(Relation r : nb.keySet()) {
			if(nb.get(r) == 1 && (nbStr.get(r.getLeftTable()) == 1 || nbStr.get(r.getRightTable()) == 1)) {
				possible.add(r);
			}
		}

		for(Relation r : possible) {
			int score = nbStr.get(r.getLeftTable()) + nbStr.get(r.getRightTable());
			if(first == null || score < nbStr.get(first.getLeftTable()) + nbStr.get(first.getRightTable())) {
				first = r;
			}
			else if(score == nbStr.get(first.getLeftTable()) + nbStr.get(first.getRightTable())) {

				if(r.getJoins().get(0).getOuter() == Join.LEFT_OUTER) {
					if(nbStr.get(r.getLeftTable()) < nbStr.get(first.getLeftTable())) {
						first = r;
					}
				}
				else if(r.getJoins().get(0).getOuter() == Join.RIGHT_OUTER) {
					if(nbStr.get(r.getLeftTable()) > nbStr.get(first.getLeftTable())) {
						first = r;
					}
				}

			}
		}
		return first;
	}

	public static boolean checkQuery(IBusinessPackage businessPack, QuerySql query, String groupName, boolean dynamic, HashMap<Prompt, List<String>> prompts) throws MetaDataException {

		List<IDataStreamElement> select = new ArrayList<IDataStreamElement>(query.getSelect());
		HashMap<ListOfValue, String> condition = query.getCondition();
		List<IFilter> filters = new ArrayList<IFilter>(query.getFilters());
		List<Formula> formulas = new ArrayList<Formula>(query.getFormulas());

		// get filters from businessTables
		for(IBusinessTable t : businessPack.getBusinessTables(groupName)) {
			for(IFilter f : t.getFilterFor(groupName)) {
				filters.add(f);
			}
		}

		List<IDataSource> ds = ((BusinessPackage) businessPack).getDataSources(groupName);
		if(ds.size() != 1) {
			MetaDataException e = new MetaDataException("Multiple DataSource Query not supported yet");
			Log.error("more than one datasource not supported yet", e);
			throw e;
		}

		// get all IDataStream
		List<IDataStream> involvedTables = new ArrayList<IDataStream>();
		for(IDataStreamElement e : select) {
			if(!involvedTables.contains(e.getDataStream())) {
				involvedTables.add(e.getDataStream());
			}
		}

		// add the tables coming from lov if there arent presents
		if(condition != null) {
			for(ListOfValue l : condition.keySet()) {
				if(!involvedTables.contains(l.getOrigin().getDataStream())) {
					involvedTables.add(l.getOrigin().getDataStream());
				}
			}
		}
		// add the tables coming Filters
		if(filters != null) {
			for(IFilter l : filters) {
				if(!involvedTables.contains(l.getOrigin().getDataStream())) {
					involvedTables.add(l.getOrigin().getDataStream());
				}
			}
		}

		// add the tables coming Prompts
		if(prompts != null) {
			for(Prompt l : prompts.keySet()) {
				if(l.getGotoDataStreamElement() != null) {
					if(!involvedTables.contains(l.getGotoDataStreamElement().getDataStream())) {
						involvedTables.add(l.getGotoDataStreamElement().getDataStream());
					}
				}
			}
		}

		/*
		 * formulas - getInvolvedTables - build dummys ICalculatedElement - add them to select list 
		 */

		List<IDataStreamElement> allCols = getAllColumns((BusinessPackage) businessPack, groupName);
		int _c = 0;
		for(Formula f : formulas) {
			String _f = f.getFormula();
			for(IDataStreamElement e : allCols) {
				if(e instanceof ICalculatedElement) {
					if(f.getFormula().contains(e.getOutputName())) {
						if(!involvedTables.contains(e.getDataStream())) {
							involvedTables.add(e.getDataStream());
						}
						_f = _f.replace(e.getDataStream().getName() + "." + e.getName(), ((ICalculatedElement) e).getFormula());
					}
				}
				else {
					if(f.getFormula().contains(e.getDataStream().getName() + "." + e.getOriginName())) {
						if(!involvedTables.contains(e.getDataStream())) {
							involvedTables.add(e.getDataStream());
						}
						_f = _f.replace(e.getDataStream().getName() + "." + e.getName(), "`" + e.getDataStream().getName() + "`." + e.getOrigin().getShortName());
					}
				}

			}
			for(String s : f.getDataStreamInvolved()) {
				IDataStream t = ds.get(0).getDataStreamNamed(s);
				if(t != null && !involvedTables.contains(t)) {
					involvedTables.add(t);
				}
			}
			ICalculatedElement c = new ICalculatedElement();
			c.setFormula(_f);
			if(f.getName() == null || "".equals(f.getName())) {
				c.setName("formula_" + _c);

			}
			else {
				c.setName(f.getName());
			}

			_c++;
			select.add(c);
		}

		// check for doublons
		List<IDataStream> toKeep = new ArrayList<IDataStream>();
		for(IDataStream t : involvedTables) {
			boolean keep = true;
			for(IDataStream k : toKeep) {
				if(k == t) {
					keep = false;
					break;
				}
			}
			if(keep) {
				toKeep.add(t);
			}
		}

		// outer joins....

		Path p = new PathFinder(((BusinessPackage) businessPack).getBusinessModel().getRelations(), toKeep, query.getRelationStrategies()).getPath();

		List<IDataStream> toRemove = new ArrayList<IDataStream>();
		if(p != null) {
			for(Relation r : p.getRelations()) {
				if(r.isOuterJoin() && toKeep.contains(r.getLeftTable())) {
					toKeep.remove(r.getLeftTable());
				}
				if(r.isOuterJoin() && toKeep.contains(r.getRightTable())) {
					toKeep.remove(r.getRightTable());
				}
			}
		}

		toKeep.removeAll(toRemove);

		if(p != null) {
			List<Relation> outerJoins = new ArrayList<Relation>();
			for(Relation r : p.getRelations()) {
				if(r.isOuterJoin()) {
					outerJoins.add(r);
				}
			}

			for(Path l : extractPaths(outerJoins, query.getRelationStrategies())) {
				List<Relation> o = order(l.getRelations());
				int index = 0;

				try {
					checkOuter(index, o, groupName);
				} catch(Exception ex) {
					ex.printStackTrace();
					throw new MetaDataException("Unable to generate a valid query for outerjoins path : \n\n{" + l.toString() + "}\n\nCheck the involved relations cardinalities." + ex.getMessage());
				}

			}

		}

		p = new PathFinder(((BusinessPackage) businessPack).getBusinessModel().getRelations(), involvedTables, query.getRelationStrategies()).getPath();
		return true;
	}

	private static boolean checkOuter(int pos, List<Relation> chain, String groupName) throws Exception {

		/*
		 * detect linearity
		 */
		HashMap<IDataStream, Integer> linearityDetection = new HashMap<IDataStream, Integer>();

		IDataStream maxStream = null;

		for(Relation r : chain) {
			if(linearityDetection.get(r.getLeftTable()) == null) {
				linearityDetection.put(r.getLeftTable(), 1);
			}
			else {
				linearityDetection.put(r.getLeftTable(), linearityDetection.get(r.getLeftTable()) + 1);
			}

			if(linearityDetection.get(r.getRightTable()) == null) {
				linearityDetection.put(r.getRightTable(), 1);
			}
			else {
				linearityDetection.put(r.getRightTable(), linearityDetection.get(r.getRightTable()) + 1);
			}

			if(maxStream == null || linearityDetection.get(r.getLeftTable()) > linearityDetection.get(maxStream)) {
				maxStream = r.getLeftTable();
			}
			if(maxStream == null || linearityDetection.get(r.getRightTable()) > linearityDetection.get(maxStream)) {
				maxStream = r.getRightTable();
			}
		}

		/*
		 * the path is not linear : - we got a star all is ok for the query - we got line and some banches, caridnality will be checked
		 */
		if(linearityDetection.get(maxStream) > 2) {

			for(IDataStream s : linearityDetection.keySet()) {
				if(s != maxStream) {
					if(linearityDetection.get(s) > 1) {
						throw new Exception("The query cannot be generated. Alias on the DataStream " + s.getName() + " could be a solution.");
					}
				}
			}
		}

		if(pos == chain.size() - 1) {
			if(chain.get(pos) == null) {
				return true;
			}

		}
		else {

			if(pos + 1 < chain.size()) {
				if(chain.get(pos) == null || chain.get(pos + 1) == null) {
					return checkOuter(pos + 1, chain, groupName);
				}
			}

			return checkOuter(pos + 1, chain, groupName);

		}

		return false;
	}

	private static boolean _order(List<Relation> ordered, List<Relation> remaining) {
		if(remaining.size() == 0) {
			return true;
		}

		List<Relation> possibles = ordered.isEmpty() ? new ArrayList<Relation>(remaining) : getPossibles(ordered, remaining);

		if(possibles.size() == 0) {
			return false;
		}

		for(Relation r : possibles) {
			ordered.add(r);
			remaining.remove(r);

			boolean result = _order(ordered, remaining);
			if(!result) {
				ordered.remove(r);
				remaining.add(r);
			}
			else if(result && remaining.isEmpty()) {
				return true;
			}
		}

		return remaining.size() == 0;

	}

	private static String getTableString(IDataStream t) {
		SQLDataStream _t = (SQLDataStream) t;
		return _t.getSql() != null ? "(" + _t.getSql() + ") `" + _t.getName() + "`" : _t.getOrigin().getName() + " `" + _t.getName() + "`";
	}

	private static String getOuter(String groupName, List<Relation> l, List<IDataStream> forbidenForFirst) {
		StringBuffer buf = new StringBuffer();

		int currentPos = 0;
		for(Relation r : l) {
			if(currentPos == 0) {
				if(forbidenForFirst.contains(r.getLeftTable()) && forbidenForFirst.contains(r.getRightTable())) {
					buf.append("/* warning on " + r.getName() + ":\n");
					buf.append("leftTable " + r.getLeftTableName() + " and rightTable " + r.getRightTableName() + " has already been used");
					buf.append("*/\n");
					return buf.toString();
				}
				else if(forbidenForFirst.contains(r.getLeftTable())) {
					buf.append(getTableString(r.getRightTable()));
					buf.append(getJoinType(r, true));
					buf.append(getTableString(r.getLeftTable()));
					buf.append("on(" + getJoinOnStatement(groupName, r) + ")");
				}

				else {
					buf.append(getTableString(r.getLeftTable()));
					buf.append(getJoinType(r, false));
					buf.append(getTableString(r.getRightTable()));
					buf.append("on(" + getJoinOnStatement(groupName, r) + ")");
				}

			}
			else {

				boolean targetRight = false;
				boolean targetLeft = false;
				for(int i = currentPos - 1; i >= 0; i--) {
					if(l.get(i).isUsingTable(r.getLeftTable()) && !forbidenForFirst.contains(r.getLeftTable())) {
						targetLeft = true;
					}
					else if(l.get(i).isUsingTable(r.getRightTable()) && !forbidenForFirst.contains(r.getRightTable())) {
						targetRight = true;
					}
				}

				buf.append(getJoinType(r, targetRight));

				if(targetLeft && targetRight) {

					buf.append(getTableString(r.getRightTable()) + "on(" + getJoinOnStatement(groupName, r) + ")");

				}
				else if(targetLeft) {
					buf.append(getTableString(r.getRightTable()) + "on(" + getJoinOnStatement(groupName, r) + ")");
				}
				else if(targetRight) {
					buf.append(getTableString(r.getLeftTable()) + "on(" + getJoinOnStatement(groupName, r) + ")");
				}
				else {
//					buf.append(getTableString(r.getLeftTable()) + "on(" + getJoinOnStatement(groupName, r) + ")");
					buf.append("/* warning on " + r.getName() + "\n");
					buf.append("leftTable " + r.getLeftTableName() + " and rightTable " + r.getRightTableName() + " has already been used");
					buf.append("*/\n");
				}

			}
			currentPos++;
		}
		return buf.toString();
	}

	private static List<Relation> getPossibles(List<Relation> sources, List<Relation> remaining) {
		List<Relation> l = new ArrayList<Relation>();
		if(sources.isEmpty()) {
			l.addAll(remaining);
		}
		else {
			for(Relation r : remaining) {
				for(Relation source : sources) {
					if(r.getLeftTable().getName().equals(source.getLeftTable().getName()) || r.getRightTable().getName().equals(source.getLeftTable().getName()) || r.getLeftTable().getName().equals(source.getRightTable().getName()) || r.getRightTable().getName().equals(source.getRightTable().getName())) {
						l.add(r);
						break;
					}
				}

			}
		}

		return l;
	}

	private static String getJoinOnStatement(String groupName, Relation r) {

		StringBuffer clause = new StringBuffer();
		boolean first = true;
		for(Join j : r.getJoins()) {
			if(first) {
				first = false;
			}
			else {
				clause.append(" AND ");
			}
			
			if(j.getOnStatement() != null && !j.getOnStatement().isEmpty() && !j.getOnStatement().equals("null")) {
				clause.append(j.getOnStatement());
			}
			else {
				if(j.getLeftElement() instanceof ICalculatedElement) {
					clause.append(((ICalculatedElement)j.getLeftElement()).getFormula());
				}
				else {
					clause.append("`" + j.getLeftElement().getDataStream().getName() + "`." + j.getLeftElement().getOrigin().getShortName());
				}
				clause.append("=");
				if(j.getRightElement() instanceof ICalculatedElement) {
					clause.append(((ICalculatedElement)j.getRightElement()).getFormula());
				}
				else {
					clause.append("`" + j.getRightElement().getDataStream().getName() + "`." + j.getRightElement().getOrigin().getShortName());
				}
			}

		}

		List<IFilter> filters = new ArrayList<IFilter>();
		filters.addAll(r.getLeftTable().getFilterFor(groupName));
		filters.addAll(r.getLeftTable().getGenericFilters());
		filters.addAll(r.getRightTable().getFilterFor(groupName));
		filters.addAll(r.getRightTable().getGenericFilters());

		for(IFilter f : filters) {
			if(first) {
				first = false;
			}
			else {
				clause.append(" AND ");
			}
			String filterClause = f.getSqlWhereClause();
			clause.append(filterClause);
		}

		return clause.toString();
	}

	private static String getJoinType(Relation r, boolean tabesInverted) {
		StringBuffer clause = new StringBuffer();
		if(!r.isOuterJoin()) {
			clause.append(" INNER JOIN ");
		}
		else {
			if(r.getJoins().get(0).getOuter() == Join.LEFT_OUTER) {
				if(tabesInverted) {
					clause.append(" RIGHT OUTER JOIN ");
				}
				else {
					clause.append(" LEFT OUTER JOIN ");
				}
	
			}
			else if(r.getJoins().get(0).getOuter() == Join.RIGHT_OUTER) {
				if(tabesInverted) {
					clause.append(" LEFT OUTER JOIN ");
				}
				else {
					clause.append(" RIGHT OUTER JOIN ");
				}
	
			}
			else {
				clause.append(" FULL OUTER JOIN ");
			}
		}
		return clause.toString();
	}
}
