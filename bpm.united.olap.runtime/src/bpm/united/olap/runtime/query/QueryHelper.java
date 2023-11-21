package bpm.united.olap.runtime.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.mdx.parser.ANTLRStringStreamCaseInsensitive;
import bpm.mdx.parser.CalculatedMeasureLexer;
import bpm.mdx.parser.CalculatedMeasureParser;
import bpm.mdx.parser.CalculatedMeasureParser.calculatedmeasure_return;
import bpm.mdx.parser.CalculatedMeasureTree;
import bpm.mdx.parser.result.CalculatedItem;
import bpm.mdx.parser.result.NodeEvaluator;
import bpm.mdx.parser.result.TermItem;
import bpm.united.olap.api.data.IExternalQueryIdentifier;
import bpm.united.olap.api.data.UnitedOlapQueryIdentifier;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.aggregation.CalculatedAggregation;
import bpm.united.olap.api.model.aggregation.ClassicAggregation;
import bpm.united.olap.api.model.aggregation.ILevelAggregation;
import bpm.united.olap.api.model.aggregation.LastAggregation;
import bpm.united.olap.api.model.impl.CalculatedMeasure;
import bpm.united.olap.api.model.impl.ClosureLevel;
import bpm.united.olap.api.model.impl.ComplexMeasure;
import bpm.united.olap.api.model.impl.DynamicMeasure;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.model.impl.ProjectionMeasureCondition;
import bpm.united.olap.api.result.DrillThroughIdentifier;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.ResultFactory;
import bpm.united.olap.api.result.ResultLine;
import bpm.united.olap.api.result.impl.ValueResultCell;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RuntimeFactory;
import bpm.united.olap.api.runtime.calculation.FirstCalculation;
import bpm.united.olap.api.runtime.calculation.LastCalculation;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.api.runtime.model.IDataLocator;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.data.AdvancedDataMapper;
import bpm.united.olap.runtime.data.AdvancedDataMapper2;
import bpm.united.olap.runtime.data.IDataMapper;
import bpm.united.olap.runtime.parser.calculation.CalculatedMeasureCalculation;
import bpm.united.olap.runtime.parser.calculation.CalculationHelper;
import bpm.united.olap.runtime.parser.calculation.ProjectionCalculation;
import bpm.united.olap.runtime.tools.OdaInputOverrider;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class QueryHelper implements IQueryHelper {
	
	protected OdaInput input;
	protected ICacheServer server;
	protected IVanillaLogger logger;
	protected boolean isInStreamMode = false;
	
	protected String query, effectiveQuery;
	protected IRuntimeContext runtimeContext;
	
	protected IQuery odaQuery;
	private static ScriptEngine mgr;
	
	public QueryHelper(IVanillaLogger logger, IRuntimeContext runtimeContext) {
		this.logger = logger;
		this.runtimeContext = runtimeContext;
	}
	
	public void setCacheServer(ICacheServer server) {
		this.server = server;
	}

	protected void createInput(Datasource datasource) {
		if(input == null || !datasource.getName().equals(input.getName())) {
			input = new OdaInput();
			input.setDatasourcePublicProperties(datasource.getPublicProperties());
			input.setDatasourcePrivateProperties(datasource.getPrivateProperties());
			input.setOdaExtensionDataSourceId(datasource.getDatasourceExtensionId());
			input.setName(datasource.getDatasourceExtensionId());
			input = OdaInputOverrider.override(this.input, runtimeContext);
		}
		
		
		
		logger.debug("input : ");
		logger.debug("Datasource public properties : " + input.getDatasourcePublicProperties().toString());
		logger.debug("Datasource private properties" + input.getDatasourcePrivateProperties().toString());
		logger.debug("Oda datasource extension Id : " + input.getOdaExtensionDataSourceId());
		logger.debug("Oda extension Id : " + input.getName());
	}
	
	
	
	
	protected IQuery getOdaQuery(OdaInput input, String query) throws Exception {
		input.setQueryText(query);
		
		//For this shitty postgres....
		if(input.getDatasourcePublicProperties().get("odaURL") != null && ((String)input.getDatasourcePublicProperties().get("odaURL")).contains("postgresql")) {
			input.getDatasourcePublicProperties().put("odaAutoCommit", "true");
		}
		else {
			input.getDatasourcePublicProperties().put("odaAutoCommit", "false");
		}
		
		IQuery queryOda = bpm.dataprovider.odainput.consumer.QueryHelper.buildquery(input);
			
		if(input.getOdaExtensionDataSourceId().equals("org.eclipse.birt.report.data.oda.jdbc")) {
			if(isInStreamMode) {
				if(((String)input.getDatasourcePublicProperties().get("odaURL")).contains("mysql")) {
					queryOda.setProperty("rowFetchSize", Integer.MIN_VALUE + "");
				}
				else {
					queryOda.setProperty("rowFetchSize", 1 + "");
				}
			}
			else {
				if(((String)input.getDatasourcePublicProperties().get("odaURL")).contains("mysql")) {
					queryOda.setProperty("rowFetchSize", 10000 + "");
				}
				else {
					queryOda.setProperty("rowFetchSize", 10000 + "");
				}
			}
		}
			
		return queryOda;
	}

	
	public synchronized String prepareQuery(ICubeInstance cubeInstance) throws Exception{
		if (query != null){
			return query;
		}

		query = cubeInstance.getCube().getFactTable().getQueryText();
		createInput(cubeInstance.getCube().getFactTable().getParent());
		input.setQueryText(query);
		
		

		if(input.getOdaExtensionDataSourceId().equals("org.eclipse.birt.report.data.oda.jdbc")) {
			
			if(input.getDatasourcePublicProperties().get("odaURL") != null && ((String)input.getDatasourcePublicProperties().get("odaURL")).contains("postgresql")) {
				input.getDatasourcePublicProperties().put("odaAutoCommit", "true");
			}
			else {
				input.getDatasourcePublicProperties().put("odaAutoCommit", "false");
			}
		}
		
		odaQuery = getOdaQuery(input, query);
		effectiveQuery = odaQuery.getEffectiveQueryText();
		return query;
	}
	
	
	@Override
	public DataStorage executeQuery(ICubeInstance cubeInstance, DataStorage storage, int limit) throws Exception {
		
		if (query == null){
			Logger.getLogger(getClass()).warn("QueryHelper has not preparedQuery, null runtime context is used to prepare Query");
			prepareQuery(cubeInstance);
		}
		
		executeNoImprovedQuery(cubeInstance, storage, limit);
		
		clearTablesInMemory();
		
		return storage;
	}
	public String getEffectiveQuery(){
		return effectiveQuery;
	}
	
	private void cleanStorage(DataStorage storage) {
		logger.debug("Clean datastorage after improvement failed");
		for(DataCell cell : storage.getDataCells()) {
			cell.clean();
		}
	}

	@Override
	public void setSqlStreamMode(boolean isInStreamMode) {
		this.isInStreamMode = isInStreamMode;
	}

	@Override
	public void clearTablesInMemory() {
	}
	
	protected void executeNoImprovedQuery(ICubeInstance cubeInstance, DataStorage storage, int limit) throws Exception {
		if(limit > 0) {
			odaQuery.setMaxRows(limit);
		}
		
//		if (query == null){
			prepareQuery(cubeInstance);
			odaQuery.close();
			bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(odaQuery);
			bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(odaQuery);
			input.setQueryText(query);
			odaQuery = getOdaQuery(input, query);
//		}

		

		
		IResultSet rs = OdaQueryRunner.runQuery(odaQuery);
		

		IDataMapper mapper = new AdvancedDataMapper(storage, cubeInstance, cubeInstance.getDataLocator(), runtimeContext);

		try {
			IResultSetMetaData mtd = rs.getMetaData();
			
			List<List<Object>> datas = new ArrayList<List<Object>>();
			
			while(rs.next()) {
				
				List<Object> row = new ArrayList<Object>();
				for(int i = 1; i <= mtd.getColumnCount(); i++){
					row.add(rs.getString(i));
				}
				
				//test if the where clause is respected
				boolean whereRespected = false;
 				List<Member> wheres = storage.getDataCells().get(0).getIdentifier2().getWhereMembers();
				if(wheres != null && wheres.size() > 0) {
					whereRespected = mapper.checkWhereClause(rs, wheres);
				}
				else {
					whereRespected = true;
				}
					
				//map values if the where clause is respected
				if(whereRespected) {
					for(DataCellIdentifier2 possibleId : storage.getPossibleIds().keySet()) {
						
						if(isPossibleId(possibleId, cubeInstance, rs)) {
							DataCell cell = mapper.mapData(rs, possibleId);
														
							if(cell != null && !cell.isCalculated()) {
								Measure mes = cell.getIdentifier2().getMeasure();
								
								HashMap<String, Double> values = findMeasureValues(mes, cell, rs, cubeInstance, cubeInstance.getDataLocator());
								
								if(values != null) {
									for(String uname : values.keySet()) {
										cell.addValue(uname, values.get(uname));
									}
									if(mes instanceof ProjectionMeasure) {
										
										((ProjectionCalculation)cell.getCalculation()).lookForFormula(findLastLevelMembers(cubeInstance, rs));
									}
									cell.getCalculation().makeCalculDuringQuery(false);
								}
							}
						}
					}
				}
			}
			
			
			//TODO: To remove and all references
			if (datas != null) {
				for (List<Object> rows : datas) {
					StringBuffer buf = new StringBuffer();
					if (rows != null) {
						boolean first = true;
						for (Object item : rows) {
							if (!first) {
								buf.append(",");
							}
							first = false;
							buf.append(item.toString());
						}
					}
					else {
						buf.append("--------------- Empty line -------------");
					}
					System.out.println(buf.toString());
				}
			}
		} catch (Exception e) {
			//close connection and remove query
			odaQuery.close();
			rs.close();
			
			bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(odaQuery);
			bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(odaQuery);
			e.printStackTrace();
		}
		
		//close connection and remove query
		odaQuery.close();
		rs.close();
		
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(odaQuery);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(odaQuery);
		
		cubeInstance.clearLevelDatasInMemory();
	}


	protected boolean isPossibleId(DataCellIdentifier2 possibleId, ICubeInstance cubeInstance, IResultSet rs) throws Exception {
		
		for(ElementDefinition elem : possibleId.getIntersections()) {
			
			if(elem instanceof Member) {
			
				Member member = (Member) elem;
				if(member.getParentLevel() != null && member.getParentLevel() instanceof ClosureLevel) {
					int fkIndex = cubeInstance.getDataLocator().getResultSetIndex(member);
					String fk = rs.getString(fkIndex + 1);
					
					String[] memberParts = member.getUname().split("\\.");
					String[] hieraParts = member.getParentLevel().getParentHierarchy().getUname().split("\\.");
					
					int levelIndex = memberParts.length - hieraParts.length - 2;
					String mbName = cubeInstance.getHierarchyExtractor(member.getParentLevel().getParentHierarchy()).getMemberName(fk, levelIndex, runtimeContext);
					if(mbName == null) {
						return false;
					}
				}
			}
		}
		
		return true;
	}

	/**
	 * Find values for a measure
	 * @param mes
	 * @param cell
	 * @param rs
	 * @param cubeInstance
	 * @return an hashmap with measure uname as key and measure value as value
	 * @throws Exception
	 */
	public HashMap<String, Double> findMeasureValues(Measure mes, DataCell cell, IResultSet rs, ICubeInstance cubeInstance, IDataLocator dataLocator) throws Exception {
		
		//find values for percent
		if(mes instanceof ComplexMeasure) {
			ComplexMeasure mesc = (ComplexMeasure) mes;
			return findMeasureValues(((MdxSet)((ArrayList)mesc.getLeftItem()).get(0)).getMeasure(), cell, rs, cubeInstance, dataLocator);
		}
		
		else if(mes instanceof ProjectionMeasure) {
			return findBasicMeasureValue(((ProjectionMeasure)mes).getMeasure(), cell, rs, cubeInstance, dataLocator);
		}
		
		//find values for a calculatedMeasure
		else if(mes instanceof CalculatedMeasure) {
			CalculatedMeasure calcMes = (CalculatedMeasure) mes;
			HashMap<String, Double> values = new HashMap<String, Double>();
			if(calcMes.getItems() != null && !calcMes.getItems().isEmpty()) {
				for(Measure m : calcMes.getItems()) {
					values.putAll(findMeasureValues(m, cell, rs, cubeInstance, dataLocator));
				}
			}
			return values;
			
		}
		
		//find the calculation for the level and the associated value
		else if(mes instanceof DynamicMeasure) {
			DynamicMeasure dynMes = (DynamicMeasure) mes;
			HashMap<String, Double> values = new HashMap<String, Double>();
			
			for(ElementDefinition def : cell.getIdentifier2().getIntersections()) {
				if(def instanceof Member) {
					Member member = (Member) def;
					for(ILevelAggregation agg : dynMes.getAggregations()) {
						if(agg.getLevel().equals(member.getParentLevel().getUname())) {
							if(agg instanceof CalculatedAggregation) {
								CalculatedMeasureCalculation calcAgg = (CalculatedMeasureCalculation) cell.getCalculation();
								for(Measure m : calcAgg.getMeasures()) {
									values.putAll(findMeasureValues(m, cell, rs, cubeInstance, dataLocator));
								}
								return values;
							}
							else if(agg instanceof ClassicAggregation) {
								Integer measureValueIndex = dataLocator.getResultSetIndexInFactTable(((ClassicAggregation)agg).getOrigin());
								Double val = rs.getDouble(measureValueIndex + 1);
								values.put(mes.getUname(), val);
								return values;
							}
							else if(agg instanceof LastAggregation) {
								String dimensionName = ((LastAggregation)agg).getRelatedDimension();
								for(ElementDefinition elem : cell.getIdentifier2().getIntersections()) {
									if(elem instanceof Member) {
										Member mem = (Member) elem;
										if(mem.getParentHierarchy().getParentDimension().getName().equals(dimensionName)) {
											
											Member lastMember = findLastFirstMember(mem, cubeInstance, rs, dataLocator);
											
											if(cell.getCalculation() instanceof LastCalculation) {
												if(((LastCalculation)cell.getCalculation()).validateDate(lastMember)) {
													Integer measureValueIndex = dataLocator.getResultSetIndexInFactTable(((LastAggregation)agg).getOrigin());
													Double val = rs.getDouble(measureValueIndex + 1);
													values.put(mes.getUname(), val);
													return values;
												}
											}
											else if(cell.getCalculation() instanceof FirstCalculation) {
												if(((FirstCalculation)cell.getCalculation()).validateDate(lastMember)) {
													Integer measureValueIndex = dataLocator.getResultSetIndexInFactTable(((LastAggregation)agg).getOrigin());
													Double val = rs.getDouble(measureValueIndex + 1);
													values.put(mes.getUname(), val);
													return values;
												}
											}
											break;
										}
									}
								}
								
								return null;
							}
						}
					}
				}
			}
			
			return findBasicMeasureValue(dynMes, cell, rs, cubeInstance, dataLocator);
		}
		
		//find the value for a basic aggregation
		else {
			
			return findBasicMeasureValue(mes, cell, rs, cubeInstance, dataLocator);
			
		}
	}

	protected HashMap<String, Double> findBasicMeasureValue(Measure mes, DataCell cell, IResultSet rs, ICubeInstance cubeInstance, IDataLocator dataLocator) throws Exception {
		if(mes.getCalculatdFormula() != null && !mes.getCalculatdFormula().equals("") && (mes.getLastDimensionName() == null || mes.getLastDimensionName().equals(""))) {
			
			HashMap<String, Double> values = new HashMap<String, Double>();
			
			CalculatedItem item = null;
			try {
				CharStream input = new ANTLRStringStreamCaseInsensitive(mes.getCalculatdFormula());
				CalculatedMeasureLexer lexer = new CalculatedMeasureLexer(input);
				TokenStream tok = new CommonTokenStream(lexer);
				CalculatedMeasureParser parser = new CalculatedMeasureParser(tok);
				calculatedmeasure_return ret = parser.calculatedmeasure();
				CommonTree tree = (CommonTree) ret.getTree();
				
				CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(tree);
				CalculatedMeasureTree walker = new CalculatedMeasureTree(nodeStream);
				item = walker.result();
				item.setFormula(mes.getCalculatdFormula());
			} catch (RecognitionException e) {
				logger.error("Cannot parse the calculated measure : " + mes.getCalculatdFormula(), e);
				e.printStackTrace();
			}
			
			for(NodeEvaluator node : item.getItems()) {
				if(node instanceof TermItem) {
					
					String measur = ((TermItem)node).getUname();
					
					for(Measure m : mes.getParentSchema().getMeasures()) {
						if(m.getUname().equals(measur)) {
							List<Integer> indexes = findMeasureIndexes(m, cubeInstance, cell.getIdentifier2(), dataLocator);
							for(Integer i : indexes) {
								values.put(m.getUname(), rs.getDouble(i + 1));
							}
						}
					}
					
				}
			}
			
			return values;
			
		}
		else if(mes.getCalculationType().equalsIgnoreCase("last") || mes.getCalculationType().equalsIgnoreCase("first")) {
			String dimensionName = mes.getLastDimensionName();
			for(ElementDefinition elem : cell.getIdentifier2().getIntersections()) {
				if(elem instanceof Member) {
					Member mem = (Member) elem;
					if(mem.getParentHierarchy().getParentDimension().getName().equals(dimensionName)) {
						
						Member lastMember = findLastFirstMember(mem, cubeInstance, rs, dataLocator);
						
						if(cell.getCalculation() instanceof LastCalculation) {
							if(((LastCalculation)cell.getCalculation()).validateDate(lastMember)) {
								Integer measureValueIndex = dataLocator.getResultSetIndex(mes);
								Double val = rs.getDouble(measureValueIndex + 1);
								HashMap<String, Double> values = new HashMap<String, Double>();
								values.put(mes.getUname(), val);
								return values;
							}
						}
						else if(cell.getCalculation() instanceof FirstCalculation) {
							if(((FirstCalculation)cell.getCalculation()).validateDate(lastMember)) {
								Integer measureValueIndex = dataLocator.getResultSetIndex(mes);
								Double val = rs.getDouble(measureValueIndex + 1);
								HashMap<String, Double> values = new HashMap<String, Double>();
								values.put(mes.getUname(), val);
								return values;
							}
						}
						break;
					}
				}
			}
			return null;
		}
		else {
			Integer measureValueIndex = null;
			if(mes instanceof ProjectionMeasure) {
				measureValueIndex = dataLocator.getResultSetIndex(((ProjectionMeasure)mes).getMeasure());
			}
			else {
				measureValueIndex = dataLocator.getResultSetIndex(mes);
			}
			Double val;
			try {
				val = rs.getDouble(measureValueIndex + 1);
			} catch (Exception e) {
				val = 1.0;
			}
			HashMap<String, Double> values = new HashMap<String, Double>();
			values.put(mes.getUname(), val);
			return values;
		}
	}

	private Member findLastFirstMember(Member mem, ICubeInstance cubeInstance, IResultSet rs, IDataLocator dataLocator) throws Exception {
		String uniqueName = mem.getUname();
		
		int actualLevelIndex = -1;
		if(mem.getParentLevel() != null) {
			actualLevelIndex = mem.getParentLevel().getParentHierarchy().getLevels().indexOf(mem.getParentLevel());
		}
		
		for(int i = actualLevelIndex + 1 ; i < mem.getParentHierarchy().getLevels().size() ; i++) {
			String fk = "";
			if(mem.getParentLevel() == null) {
				int fkIndex = dataLocator.getResultSetIndex(mem.getParentHierarchy().getLevels().get(i)) + 1;
				fk = rs.getString(fkIndex);
			}
			else {
				int fkIndex = dataLocator.getResultSetIndex(mem) + 1;
				fk = rs.getString(fkIndex);
			}
			String mbname = cubeInstance.getHierarchyExtractor(mem.getParentHierarchy()).getMemberName(fk, i, runtimeContext);
			uniqueName += ".[" + mbname + "]";
			
		}

		return cubeInstance.getHierarchyExtractor(mem.getParentHierarchy()).getMember(uniqueName, runtimeContext);
	}

	@Override
	public IVanillaLogger getLogger() {
		return logger;
	}

	@Override
	public OlapResult advancedDrillthrough(ICubeInstance cubeInstance, DrillThroughIdentifier dataCellId, Projection projection) throws Exception {
		//recreate a datacellidentifier from the drillthroughid
		
		String realMeasure = dataCellId.getMeasure().replace("_projection", "");
		dataCellId.setMeasure(realMeasure);
		
		DataCellIdentifier2 identifier = recreateDataCellIdentifier(cubeInstance, dataCellId);
		
		String query = null;
		Schema sh = cubeInstance.getSchema();

		query = cubeInstance.getFactTable().getQueryText();
		createInput(cubeInstance.getFactTable().getParent());
		
		input.setQueryText(query);
		if(input.getOdaExtensionDataSourceId().equals("org.eclipse.birt.report.data.oda.jdbc")) {
			input.getDatasourcePublicProperties().put("odaAutoCommit", "false");
		}
		
		IQuery queryOda = getOdaQuery(input, query);
		
		OlapResult result = ResultFactory.eINSTANCE.createOlapResult();
		
		IDataMapper mapper = new AdvancedDataMapper(null, cubeInstance, cubeInstance.getDataLocator(), runtimeContext);
		
		IResultSet rs = OdaQueryRunner.runQuery(queryOda);
		
		ResultLine firstLine = ResultFactory.eINSTANCE.createResultLine();
		boolean firstLineCreated = false;
		
		try {
			while(rs.next()) {
				
				if(mapper.checkWhereClause(rs, identifier.getWhereMembers())) {
					
					//test if the line is valid
					boolean isValidLine = true;
					for(ElementDefinition elem : identifier.getIntersections()) {
						
						if(elem instanceof Member && ((Member)elem).getParentLevel() != null) {
							Member actualMember = (Member) elem;
							
							if(actualMember.getParentLevel() instanceof ClosureLevel) {
								String[] memberParts = actualMember.getUname().split("\\.");
								String[] hieraParts = actualMember.getParentLevel().getParentHierarchy().getUname().split("\\.");
								
								int levelIndex = memberParts.length - hieraParts.length - 2;
								for(int j = 0 ; j < levelIndex + 1 ; j++) {
									int res = mapper.compareClosureMemberWithResultSetValue(actualMember, rs, levelIndex);
									if(res != 0) {
										isValidLine = false;
										break;
									}
								}
							}
							
							else {
								int levelIndex = actualMember.getParentLevel().getParentHierarchy().getLevels().indexOf(actualMember.getParentLevel());
								
								for(int i = 0 ; i < levelIndex + 1 ; i++) {
									int res = mapper.compareMemberWithResultSetValue(actualMember, i, rs);
									if(res != 0) {
										isValidLine = false;
										break;
									}
								}
							}
							if(!isValidLine) {
								break;
							}
						}
					}
					
					//create the result line
					if(isValidLine) {
						ResultLine line = ResultFactory.eINSTANCE.createResultLine();
						
						for(ElementDefinition elem : identifier.getIntersections()) {
							if(elem instanceof Member) {
							
								Member member = (Member) elem;
								Hierarchy hiera = member.getParentHierarchy();
								
								for(int i = 0 ; i < hiera.getLevels().size() ; i++) {
									
									int fkIndex = cubeInstance.getDataLocator().getResultSetIndex(hiera.getLevels().get(i));
									String fk = rs.getString(fkIndex + 1);
									
									String lvlName = hiera.getLevels().get(i).getName();
									String lvlValue = cubeInstance.getHierarchyExtractor(hiera).getMemberName(fk, i, runtimeContext);
									
									if(!firstLineCreated) {
										ValueResultCell firstLineCell = new ValueResultCell(null, lvlName, "");
										firstLine.getCells().add(firstLineCell);
									}
									
									ValueResultCell lineCell = new ValueResultCell(null, lvlValue, "");
									line.getCells().add(lineCell);
								}
								
							}
						}
						
						List<Integer> fkIndexes = null;
						
						
//						if(identifier.getMeasure() instanceof ProjectionMeasure) {
//							fkIndexes = findMeasureIndexes(((ProjectionMeasure)identifier.getMeasure()).getMeasure(), cubeInstance, identifier, cubeInstance.getDataLocator());
//						}
//						else {
							fkIndexes = findMeasureIndexes(identifier.getMeasure(), cubeInstance, identifier, cubeInstance.getDataLocator());
//						}
						
						
						for(Integer fkIndex : fkIndexes) {
							if(fkIndex != null) {
								String mesVal = rs.getString(fkIndex + 1);
								

								List<Member> lastLevelMembers = findLastLevelMembers(cubeInstance,rs);
								for(ProjectionMeasure m : projection.getProjectionMeasures()) {
									if(m.getUname().equals(realMeasure)) {
										mesVal = calculProjectionValue(m, mesVal, lastLevelMembers, cubeInstance);
									}
								}

								
								if(!firstLineCreated) {
									ValueResultCell firstLineCell = new ValueResultCell(null, identifier.getMeasure().getName(), "");
									firstLine.getCells().add(firstLineCell);
								}
								
								ValueResultCell lineCell = new ValueResultCell(null, mesVal, "");
								line.getCells().add(lineCell);
							}
							
							else {
								continue;
							}
						}
						
						if(!firstLineCreated) {
							result.getLines().add(firstLine);
							firstLineCreated = true;
						}
						result.getLines().add(line);
					}
				}
				
			}
		} catch (Exception e) {
			queryOda.close();
			rs.close();
			
			bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
			bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
			e.printStackTrace();
		}
		
		queryOda.close();
		rs.close();
		
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
		
		cubeInstance.clearLevelDatasInMemory();
		
		return result;
	}
	
	
	@Override
	public OlapResult advancedDrillthrough(ICubeInstance cubeInstance, DrillThroughIdentifier drillThroughId) throws Exception {
	
		//recreate a datacellidentifier from the drillthroughid
		DataCellIdentifier2 identifier = recreateDataCellIdentifier(cubeInstance, drillThroughId);
		
		String query = null;
		Schema sh = cubeInstance.getSchema();

		query = cubeInstance.getFactTable().getQueryText();
		createInput(cubeInstance.getFactTable().getParent());
		
		input.setQueryText(query);
		if(input.getOdaExtensionDataSourceId().equals("org.eclipse.birt.report.data.oda.jdbc")) {
			input.getDatasourcePublicProperties().put("odaAutoCommit", "false");
		}
		
		IQuery queryOda = getOdaQuery(input, query);
		
		OlapResult result = ResultFactory.eINSTANCE.createOlapResult();
		
		IDataMapper mapper = new AdvancedDataMapper(null, cubeInstance, cubeInstance.getDataLocator(), runtimeContext);
		
		IResultSet rs = OdaQueryRunner.runQuery(queryOda);
		
		ResultLine firstLine = ResultFactory.eINSTANCE.createResultLine();
		boolean firstLineCreated = false;
		
		try {
			while(rs.next()) {
				
				if(mapper.checkWhereClause(rs, identifier.getWhereMembers())) {
					
					//test if the line is valid
					boolean isValidLine = true;
					for(ElementDefinition elem : identifier.getIntersections()) {
						
						if(elem instanceof Member && ((Member)elem).getParentLevel() != null) {
							Member actualMember = (Member) elem;
							
							if(actualMember.getParentLevel() instanceof ClosureLevel) {
								String[] memberParts = actualMember.getUname().split("\\.");
								String[] hieraParts = actualMember.getParentLevel().getParentHierarchy().getUname().split("\\.");
								
								int levelIndex = memberParts.length - hieraParts.length - 2;
								for(int j = 0 ; j < levelIndex + 1 ; j++) {
									int res = mapper.compareClosureMemberWithResultSetValue(actualMember, rs, levelIndex);
									if(res != 0) {
										isValidLine = false;
										break;
									}
								}
							}
							
							else {
								int levelIndex = actualMember.getParentLevel().getParentHierarchy().getLevels().indexOf(actualMember.getParentLevel());
								
//								for(int i = 0 ; i < levelIndex + 1 ; i++) {
									int res = mapper.compareMemberWithResultSetValue(actualMember, levelIndex, rs);
									if(res != 0) {
										isValidLine = false;
										break;
									}
//								}
							}
							if(!isValidLine) {
								break;
							}
						}
					}
					
					//create the result line
					if(isValidLine) {
						ResultLine line = ResultFactory.eINSTANCE.createResultLine();
						
						for(ElementDefinition elem : identifier.getIntersections()) {
							if(elem instanceof Member) {
							
								Member member = (Member) elem;
								Hierarchy hiera = member.getParentHierarchy();
								
								for(int i = 0 ; i < hiera.getLevels().size() ; i++) {
									
									int fkIndex = cubeInstance.getDataLocator().getResultSetIndex(hiera.getLevels().get(i));
									String fk = rs.getString(fkIndex + 1);
									
									String lvlName = hiera.getLevels().get(i).getName();
									String lvlValue = cubeInstance.getHierarchyExtractor(hiera).getMemberName(fk, i, runtimeContext);
									
									if(!firstLineCreated) {
										ValueResultCell firstLineCell = new ValueResultCell(null, lvlName, "");
										firstLine.getCells().add(firstLineCell);
									}
									
									ValueResultCell lineCell = new ValueResultCell(null, lvlValue, "");
									line.getCells().add(lineCell);
								}
								
							}
						}
						
						List<Integer> fkIndexes = null;
						if(identifier.getMeasure() instanceof ProjectionMeasure) {
							fkIndexes = findMeasureIndexes(((ProjectionMeasure)identifier.getMeasure()).getMeasure(), cubeInstance, identifier, cubeInstance.getDataLocator());
						}
						else {
							fkIndexes = findMeasureIndexes(identifier.getMeasure(), cubeInstance, identifier, cubeInstance.getDataLocator());
						}
						
						
						for(Integer fkIndex : fkIndexes) {
							if(fkIndex != null) {
								String mesVal = rs.getString(fkIndex + 1);
								
								if(identifier.getMeasure() instanceof ProjectionMeasure) {
									List<Member> lastLevelMembers = findLastLevelMembers(cubeInstance,rs);
									mesVal = calculProjectionValue((ProjectionMeasure)identifier.getMeasure(), mesVal, lastLevelMembers, cubeInstance);
								}
								
								if(!firstLineCreated) {
									ValueResultCell firstLineCell = new ValueResultCell(null, identifier.getMeasure().getName(), "");
									firstLine.getCells().add(firstLineCell);
								}
								
								ValueResultCell lineCell = new ValueResultCell(null, mesVal, "");
								line.getCells().add(lineCell);
							}
							
							else {
								continue;
							}
						}
						
						if(!firstLineCreated) {
							result.getLines().add(firstLine);
							firstLineCreated = true;
						}
						result.getLines().add(line);
					}
				}
				
			}
		} catch (Exception e) {
			queryOda.close();
			rs.close();
			
			bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
			bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
			e.printStackTrace();
		}
		
		queryOda.close();
		rs.close();
		
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
		
		cubeInstance.clearLevelDatasInMemory();
		
		return result;
	}

	private String calculProjectionValue(ProjectionMeasure measure, String mesVal, List<Member> members, ICubeInstance cubeInstance) {
		String formula = null;
		int minimum = -1;
		ProjectionMeasureCondition actualCondition = null;
		
		LOOK:for(ProjectionMeasureCondition cond : measure.getConditions()) {
			int ecart = 0;
			for(Member mem : cond.getMembers()) {
				boolean ok = false;
				
				String[] condPart = mem.getUname().split("\\]\\.\\[");
				int actualEcart = -1;
				for(Member m : members) {
					String[] actualPart = m.getUname().split("\\]\\.\\[");
					
					if(!condPart[0].equals(actualPart[0])) {
						continue;
					}
					
					if(condPart.length < actualPart.length) {
						if(m.getUname().startsWith(mem.getUname())) {
							ecart += actualPart.length - condPart.length;
							actualEcart = ecart;
							ok = true;
							break;
						}
					}
					else {
						if(mem.getUname().startsWith(m.getUname())) {
							ecart += condPart.length - actualPart.length;
							actualEcart = ecart;
							ok = true;
							break;
						}
					}
				}
				
				if(!ok) {
					continue LOOK;
				}
				
				if(actualEcart == -1) {
					String hiera = condPart[0];
					for(Dimension dim : cubeInstance.getCube().getDimensions()) {
						for(Hierarchy hi : dim.getHierarchies()) {
							if(hi.getUname().startsWith(hiera)) {
								ecart += hi.getLevels().size();
								break;
							}
						}
					}
				}
			}
			
			if(minimum >= 0) {
				if(minimum > ecart) {
					minimum = ecart;
					actualCondition = cond;
				}
			}
			else {
				minimum = ecart;
				actualCondition = cond;
			}
			
		}
		
		if(actualCondition != null) {
			formula = actualCondition.getFormula();
		}
		else {
			formula = measure.getFormula();
		}
		
		if(formula != null) {
			if(mgr == null) {
				mgr = new ScriptEngineManager().getEngineByName("JavaScript");
			}
			formula = formula.replace(measure.getUname(), mesVal);
			String result = "";
			try {
				while(formula.endsWith("+") || formula.endsWith("-") || formula.endsWith("/") || formula.endsWith("*")) {
					formula = formula.substring(0, formula.length() - 1);
				}
				result = mgr.eval(formula).toString();
			} catch (Exception e) {
	//			logger.warn("Error when parsing expression : " + formula,e);
			}
			if(result != null && !result.equals("")) {
				return result;
			}
		}
		return mesVal;
	}

	private List<Member> findLastLevelMembers(ICubeInstance cubeInstance, IResultSet rs) throws OdaException, Exception {
		List<Member> lastLevelMembers = new ArrayList<Member>();
		for(Dimension dim : cubeInstance.getCube().getDimensions()) {
			Level level = dim.getHierarchies().get(0).getLevels().get(0);
			String uname = cubeInstance.getHierarchyExtractor(dim.getHierarchies().get(0)).getRootMember().getUname();
			
			int levelIndex = 0;
			
			while(level != null) {
				int index = cubeInstance.getDataLocator().getResultSetIndex(level);
				uname += ".[" + cubeInstance.getHierarchyExtractor(dim.getHierarchies().get(0)).getMemberName(rs.getString(index +1), levelIndex, runtimeContext) + "]";
				
				level = level.getSubLevel();
				levelIndex++;
			}
			lastLevelMembers.add(cubeInstance.getHierarchyExtractor(dim.getHierarchies().get(0)).getMember(uname, runtimeContext));
		}
		return lastLevelMembers;
	}

	protected DataCellIdentifier2 recreateDataCellIdentifier(ICubeInstance cubeInstance, DrillThroughIdentifier drillThroughId) throws Exception {
		DataCellIdentifier2 identifier = RuntimeFactory.eINSTANCE.createDataCellIdentifier2();
		
		//find the intersection members
		LOOK:for(String inter : drillThroughId.getIntersections()) {
			for(Dimension dim : cubeInstance.getCube().getDimensions()) {
				for(Hierarchy hiera : dim.getHierarchies()) {
					if(inter.startsWith(hiera.getUname())) {
						Member mem = cubeInstance.getHierarchyExtractor(hiera).getMember(inter, runtimeContext);
						identifier.addIntersection(mem);
						continue LOOK;
					}
				}
			}
		}
		
		//find the measure
		for(Measure mes : cubeInstance.getCube().getMeasures()) {
			if(mes.getUname().equals(drillThroughId.getMeasure())) {
				identifier.setMeasure(mes);
				break;
			}
		}
		
		//find where elements
		LOOK:for(String where : drillThroughId.getWheres()) {
			for(Dimension dim : cubeInstance.getCube().getDimensions()) {
				for(Hierarchy hiera : dim.getHierarchies()) {
					if(where.startsWith(hiera.getUname())) {
						Member mem = cubeInstance.getHierarchyExtractor(hiera).getMember(where, runtimeContext);
						identifier.addWhereMember(mem);
						continue LOOK;
					}
				}
			}
		}
		
		return identifier;
	}

	/**
	 * Find a list of resultSetIndex for a measure
	 * @param measure
	 * @param cubeInstance
	 * @param identifier
	 * @return
	 */
	private List<Integer> findMeasureIndexes(Measure measure, ICubeInstance cubeInstance, DataCellIdentifier2 identifier, IDataLocator dataLocator) {
		
		List<Integer> indexes = new ArrayList<Integer>();
		
		if(measure instanceof CalculatedMeasure) {
			CalculatedMeasure calcMes = (CalculatedMeasure) measure;
			for(Measure mes : calcMes.getItems()) {
				indexes.addAll(findMeasureIndexes(mes, cubeInstance, identifier, dataLocator));
			}
		}
		else if(measure instanceof DynamicMeasure) {
			
			DynamicMeasure dynMes = (DynamicMeasure)measure;
			LOOK:for(ElementDefinition def : identifier.getIntersections()) {
				if(def instanceof Member) {
					Member member = (Member) def;
					for(ILevelAggregation agg : dynMes.getAggregations()) {
						if(agg.getLevel().equals(member.getParentLevel().getUname())) {
							if(agg instanceof CalculatedAggregation) {
								CalculatedAggregation calcAgg = (CalculatedAggregation) agg;
								String formula = calcAgg.getFormula();
								CalculatedItem item = null;
								try {
									CharStream input = new ANTLRStringStreamCaseInsensitive(formula);
									CalculatedMeasureLexer lexer = new CalculatedMeasureLexer(input);
									TokenStream tok = new CommonTokenStream(lexer);
									CalculatedMeasureParser parser = new CalculatedMeasureParser(tok);
									calculatedmeasure_return ret = parser.calculatedmeasure();
									CommonTree tree = (CommonTree) ret.getTree();
									
									CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(tree);
									CalculatedMeasureTree walker = new CalculatedMeasureTree(nodeStream);
									item = walker.result();
									item.setFormula(formula);
								} catch (RecognitionException e) {
									logger.error("Cannot parse the calculated measure : " + formula, e);
									e.printStackTrace();
								}
								
								for(NodeEvaluator node : item.getItems()) {
									if(node instanceof TermItem) {
										
										String measur = ((TermItem)node).getUname();
										
										for(Measure m : measure.getParentSchema().getMeasures()) {
											if(m.getUname().equals(measur)) {
												indexes.addAll(findMeasureIndexes(m, cubeInstance, identifier, dataLocator));
											}
										}
										
									}
								}
							}
							else if(agg instanceof LastAggregation) {
								Integer measureValueIndex = cubeInstance.getDataLocator().getResultSetIndexInFactTable(((LastAggregation)agg).getOrigin());
								indexes.add(measureValueIndex);
							}
							else if(agg instanceof ClassicAggregation) {
								Integer measureValueIndex = cubeInstance.getDataLocator().getResultSetIndexInFactTable(((ClassicAggregation)agg).getOrigin());
								indexes.add(measureValueIndex);
							}
							
							break LOOK;
						}
					}
				}
			}
			
		}
		else {
			indexes.add(cubeInstance.getDataLocator().getResultSetIndex(measure));
		}
		
		return indexes;
	}

	@Override
	public OlapResult drillthrough(ICubeInstance cubeInstance, DrillThroughIdentifier dtId) throws Exception {
		
		DataCellIdentifier2 identifier = recreateDataCellIdentifier(cubeInstance, dtId);
		
		String query = null;
		Schema sh = cubeInstance.getSchema();//storage.getUsedDimensions().get(0).getDataObject().getParent().getParent();
		
		query = cubeInstance.getCube().getFactTable().getQueryText();
		createInput(cubeInstance.getCube().getFactTable().getParent());

		
		input.setQueryText(query);
		if(input.getOdaExtensionDataSourceId().equals("org.eclipse.birt.report.data.oda.jdbc")) {
			input.getDatasourcePublicProperties().put("odaAutoCommit", "false");
		}
		
		IQuery queryOda = getOdaQuery(input, query);
		
		OlapResult result = ResultFactory.eINSTANCE.createOlapResult();
		
		IDataMapper mapper = new AdvancedDataMapper(null, cubeInstance, cubeInstance.getDataLocator(), runtimeContext);
		
		IResultSet rs = OdaQueryRunner.runQuery(queryOda);
		
		ResultLine firstLine = ResultFactory.eINSTANCE.createResultLine();
		boolean firstLineCreated = false;
		
		try {
			while(rs.next()) {
				
				if(mapper.checkWhereClause(rs, identifier.getWhereMembers())) {
					
					//test if the line is valid
					boolean isValidLine = true;
					for(ElementDefinition elem : identifier.getIntersections()) {
						
						if(elem instanceof Member && ((Member)elem).getParentLevel() != null) {
							Member actualMember = (Member) elem;
							
							if(actualMember.getParentLevel() instanceof ClosureLevel) {
								String[] memberParts = actualMember.getUname().split("\\.");
								String[] hieraParts = actualMember.getParentLevel().getParentHierarchy().getUname().split("\\.");
								
								int levelIndex = memberParts.length - hieraParts.length - 2;
								for(int j = 0 ; j < levelIndex + 1 ; j++) {
									int res = mapper.compareClosureMemberWithResultSetValue(actualMember, rs, levelIndex);
									if(res != 0) {
										isValidLine = false;
										break;
									}
								}
							}
							
							else {
								int levelIndex = actualMember.getParentLevel().getParentHierarchy().getLevels().indexOf(actualMember.getParentLevel());
								
								for(int i = 0 ; i < levelIndex + 1 ; i++) {
									int res = mapper.compareMemberWithResultSetValue(actualMember, i, rs);
									if(res != 0) {
										isValidLine = false;
										break;
									}
								}
							}
							if(!isValidLine) {
								break;
							}
						}
					}
					
					//create the result line
					if(isValidLine) {
						ResultLine line = ResultFactory.eINSTANCE.createResultLine();
						
						for(int i = 1 ; i <= rs.getMetaData().getColumnCount() ; i++) {
							String value = rs.getString(i);
							line.getCells().add(new ValueResultCell(null, value, ""));
							
							if(!firstLineCreated) {
								String name = rs.getMetaData().getColumnName(i);
								firstLine.getCells().add(new ValueResultCell(null, name, ""));
							}
						}
						
						if(!firstLineCreated) {
							result.getLines().add(firstLine);
							firstLineCreated = true;
						}
						result.getLines().add(line);
					}
				}
				
			}
		} catch (Exception e) {
			queryOda.close();
			rs.close();
			
			bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
			bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
			e.printStackTrace();
		}
		
		queryOda.close();
		rs.close();
		
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
		
		cubeInstance.clearLevelDatasInMemory();
		
		return result;
	}
	
	protected boolean hasLastMeasure(DataStorage storage) {
		
		for(Measure mes : storage.getMeasures()) {
			if(mes.getCalculationType() != null && mes.getCalculationType().equals(CalculationHelper.LAST)) {
				return true;
			}
		}
 		
		return false;
	}


	public OlapResult executeFmdtQuery(IExternalQueryIdentifier identifier, ICubeInstance cubeInstance) throws Exception {
		
		UnitedOlapQueryIdentifier realIdentifier = new UnitedOlapQueryIdentifier(identifier, cubeInstance, runtimeContext);
		
		query = cubeInstance.getFactTable().getQueryText();
		createInput(cubeInstance.getFactTable().getParent());
		
		input.setQueryText(query);
		if(input.getOdaExtensionDataSourceId().equals("org.eclipse.birt.report.data.oda.jdbc")) {
			input.getDatasourcePublicProperties().put("odaAutoCommit", "false");
		}
		
		IQuery queryOda = getOdaQuery(input, query);
		
		OlapResult result = ResultFactory.eINSTANCE.createOlapResult();
		
		IDataMapper mapper = new AdvancedDataMapper(null, cubeInstance, cubeInstance.getDataLocator(), runtimeContext);
		
		IResultSet rs = OdaQueryRunner.runQuery(queryOda);
		
		//create the first line with column labels
		ResultLine firstLine = ResultFactory.eINSTANCE.createResultLine();
		
		for(ElementDefinition elem : realIdentifier.getSelects()) {
			if(elem instanceof Level) {
				Level lvl = (Level) elem;
				ValueResultCell cell = new ValueResultCell(null, lvl.getUname(), "");
				firstLine.getCells().add(cell);
			}
			else if(elem instanceof Measure) {
				Measure lvl = (Measure) elem;
				ValueResultCell cell = new ValueResultCell(null, lvl.getUname(), "");
				firstLine.getCells().add(cell);
			}
		}
		
		result.getLines().add(firstLine);
		
		try {
			while(rs.next()) {
				
				//verify filter conditions
				if(mapper.checkWhereClause(rs, realIdentifier.getWheres())) {
					
					ResultLine line = ResultFactory.eINSTANCE.createResultLine();
					
					for(ElementDefinition elem : realIdentifier.getSelects()) {
						if(elem instanceof Level) {
						
							Level lvl = (Level) elem;
							
							int fkIndex = cubeInstance.getDataLocator().getResultSetIndex(lvl);
							String fk = rs.getString(fkIndex + 1);
							
							String lvlName = lvl.getName();
							
							int i = -1;
							if(lvl instanceof ClosureLevel) {
								i = 0;
							}
							else {
								i = lvl.getParentHierarchy().getLevels().indexOf(lvl);
							}
							
							String lvlValue = cubeInstance.getHierarchyExtractor(lvl.getParentHierarchy()).getMemberName(fk, i, runtimeContext);
							
							ValueResultCell lineCell = new ValueResultCell(null, lvlValue, "");
							line.getCells().add(lineCell);
							
						}
						
						else if(elem instanceof Measure) {
							Measure mes = (Measure) elem;
							
							//FIXME : Look if its not a classic aggregation
							
//							List<Integer> fkIndexes = findMeasureIndexes(mes, cubeInstance, identifier, cubeInstance.getDataLocator());
							
							int fkIndex = cubeInstance.getDataLocator().getResultSetIndex(mes);
							
							Double mesVal = rs.getDouble(fkIndex + 1);
							String mesValF = rs.getString(fkIndex + 1); //TODO : apply formatter
							ValueResultCell lineCell = new ValueResultCell(mesVal, mesValF, "");
							line.getCells().add(lineCell);
						}
					}
					
					result.getLines().add(line);
				}
				
			}
		
		} catch (Exception e) {
			queryOda.close();
			rs.close();
			
			bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
			bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
			e.printStackTrace();
		}
		
		queryOda.close();
		rs.close();
		
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
		
		cubeInstance.clearLevelDatasInMemory();
		
		return result;
	}


}
