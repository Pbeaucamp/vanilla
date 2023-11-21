package bpm.united.olap.runtime.query.improver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IQuery;

import bpm.mdx.parser.ANTLRStringStreamCaseInsensitive;
import bpm.mdx.parser.CalculatedMeasureLexer;
import bpm.mdx.parser.CalculatedMeasureParser;
import bpm.mdx.parser.CalculatedMeasureTree;
import bpm.mdx.parser.CalculatedMeasureParser.calculatedmeasure_return;
import bpm.mdx.parser.result.CalculatedItem;
import bpm.mdx.parser.result.NodeEvaluator;
import bpm.mdx.parser.result.ProjectionItem;
import bpm.mdx.parser.result.TermItem;
import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.aggregation.CalculatedAggregation;
import bpm.united.olap.api.model.aggregation.ClassicAggregation;
import bpm.united.olap.api.model.aggregation.ILevelAggregation;
import bpm.united.olap.api.model.aggregation.LastAggregation;
import bpm.united.olap.api.model.impl.CalculatedMeasure;
import bpm.united.olap.api.model.impl.ClosureLevel;
import bpm.united.olap.api.model.impl.ComplexMeasure;
import bpm.united.olap.api.model.impl.DynamicMeasure;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.DataStorage;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.api.runtime.model.IDataLocator;
import bpm.united.olap.runtime.model.ImprovedDataLocator;
import bpm.united.olap.runtime.model.SnowFlakesHierarchyExtractor;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.logging.IVanillaLogger;

public abstract class  AbstractQueryImprover {
	
	
	private List<Member> members = new ArrayList<Member>();
	private List<Level> usedLevel = new ArrayList<Level>();
	private List<DataObjectItem> levelColumns = new ArrayList<DataObjectItem>();
	private List<DataObjectItem> orderColumns = new ArrayList<DataObjectItem>();
	private HashMap<DataObjectItem, List<Object>> parentFilters = new HashMap<DataObjectItem, List<Object>>();
	private List<Relation> relations = new ArrayList<Relation>();
	private HashMap<DataObjectItem, Integer> levelIndexes = new HashMap<DataObjectItem, Integer>();
//	private HashMap<DataObjectItem, Level> levelFkMapping = new HashMap<DataObjectItem, Level>();
	
	private HashMap<Level, DataObjectItem> levelFkMapping = new HashMap<Level, DataObjectItem>();
	
	private HashMap<DataObjectItem, Integer> measureIndexes = new HashMap<DataObjectItem, Integer>();
	private HashMap<DataObjectItem, String> measureAggregations = new HashMap<DataObjectItem, String>();
	
	
	private IVanillaLogger logger;
	
	private String improvedQuery;
	private IDataLocator improvedDataLocator;
	private IRuntimeContext runtimeContext;

	
	public AbstractQueryImprover(IVanillaLogger logger, IRuntimeContext runtimeContext) {
		this.logger = logger;
		this.runtimeContext = runtimeContext;
	}
	/**
	 * Find index and aggregation type for each dataObjectItem used by the measure
	 * @param measure
	 * @param cubeInstance
	 * @param measureIndexes
	 * @param measureAggregations
	 * @param possibleId
	 */
	protected void setMeasureInformations(Measure measure, ICubeInstance cubeInstance, HashMap<DataObjectItem, Integer> measureIndexes, HashMap<DataObjectItem, String> measureAggregations, DataCellIdentifier2 possibleId) {
	
		if(!measureIndexes.containsKey(measure.getUname())) {
			if(measure instanceof ComplexMeasure) {
				ComplexMeasure mesc = (ComplexMeasure) measure;
				setMeasureInformations(((MdxSet)((ArrayList)mesc.getLeftItem()).get(0)).getMeasure(), cubeInstance, measureIndexes, measureAggregations, possibleId);
			}
			else if(measure instanceof CalculatedMeasure) {
				CalculatedMeasure calcMes = (CalculatedMeasure) measure;
				for(Measure mes : calcMes.getItems()) {
					setMeasureInformations(mes, cubeInstance, measureIndexes, measureAggregations, possibleId);
				}
			}
			else if(measure instanceof DynamicMeasure) {
				setDynamicMeasureInformations((DynamicMeasure) measure, cubeInstance, measureIndexes, measureAggregations, possibleId);
			}
			else if(measure instanceof ProjectionMeasure) {
				ProjectionMeasure projMes = (ProjectionMeasure) measure;
				int index = cubeInstance.getDataLocator().getResultSetIndex(projMes.getMeasure());
				measureIndexes.put(projMes.getMeasure().getItem(), index);
				measureAggregations.put(projMes.getMeasure().getItem(), projMes.getMeasure().getCalculationType());
			}
			else {
				int index = cubeInstance.getDataLocator().getResultSetIndex(measure);
				measureIndexes.put(measure.getItem(), index);
				measureAggregations.put(measure.getItem(), measure.getCalculationType());
			}
		}
		
	}
	
	private void setDynamicMeasureInformations(DynamicMeasure measure, ICubeInstance cubeInstance, HashMap<DataObjectItem, Integer> measureIndexes, HashMap<DataObjectItem, String> measureAggregations, DataCellIdentifier2 possibleId) {
		for(ElementDefinition elem : possibleId.getIntersections()) {
			if(elem instanceof Member) {
				Member mem = (Member) elem;
				
				if(mem.getParentLevel() != null) {
				
					for(ILevelAggregation agg : measure.getAggregations()) {
						if(mem.getParentLevel().getUname().equals(agg.getLevel())) {
						
							setAggregationInformations(agg, cubeInstance, measureIndexes, measureAggregations, possibleId);
						}
					}
				}
			}
		}
		
		if(measure.getCalculatdFormula() != null && !measure.getCalculatdFormula().equals("")  && (measure.getLastDimensionName() == null || measure.getLastDimensionName().equals(""))) {
			CalculatedAggregation calc = new CalculatedAggregation();
			calc.setFormula(measure.getCalculatdFormula());
			setAggregationInformations(calc, cubeInstance, measureIndexes, measureAggregations, possibleId);
		}
		else {
			int index = cubeInstance.getDataLocator().getResultSetIndex(measure);
			measureIndexes.put(measure.getItem(), index);
			measureAggregations.put(measure.getItem(), measure.getCalculationType());
		}
	}

	private void setAggregationInformations(ILevelAggregation agg, ICubeInstance cubeInstance, HashMap<DataObjectItem, Integer> measureIndexes, HashMap<DataObjectItem, String> measureAggregations, DataCellIdentifier2 possibleId) {
		if(agg instanceof CalculatedAggregation) {
			
			String formula = ((CalculatedAggregation)agg).getFormula();
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
				Logger.getLogger(getClass()).error("Cannot parse the calculated measure : " + formula, e);
				e.printStackTrace();
			}
			
			for(NodeEvaluator node : item.getItems()) {
				if(node instanceof TermItem) {
					String measur = ((TermItem)node).getUname();
					for(Measure m : cubeInstance.getSchema().getMeasures()) {
						if(m.getUname().equals(measur)) {
							setMeasureInformations(m, cubeInstance, measureIndexes, measureAggregations, possibleId);
						}
					}
					
				}
			}
			
		}
		else if(agg instanceof LastAggregation) {
			int index = cubeInstance.getDataLocator().getResultSetIndexInFactTable(((LastAggregation)agg).getOrigin());
			measureIndexes.put(((LastAggregation)agg).getOrigin(), index);
			measureAggregations.put(((LastAggregation)agg).getOrigin(), ((LastAggregation)agg).getAggregator());
		}
		else if(agg instanceof ClassicAggregation) {
			int index = cubeInstance.getDataLocator().getResultSetIndexInFactTable(((ClassicAggregation)agg).getOrigin());
			measureIndexes.put(((ClassicAggregation)agg).getOrigin(), index);
			measureAggregations.put(((ClassicAggregation)agg).getOrigin(), ((ClassicAggregation)agg).getAggregator());
		}
	}

	
	private void extractLevelDataForOtherDataSource(Member member, ICubeInstance cubeInstance, DataCellIdentifier2 dataCellIdentifier) throws QueryImproverException{
		Level lvl = member.getParentLevel();
		if (lvl == null){
			extractRootElementDatas(member, cubeInstance, dataCellIdentifier);
			return;
		}
		Level level = lvl;
		
		while(level != null){
			if (!usedLevel.contains(level)){
				usedLevel.add(level);
			}
			level = level.getParentLevel();
		}
		
		DataObjectItem col = cubeInstance.getFactTable().getItems().get(cubeInstance.getDataLocator().getResultSetIndex(lvl)); 
		if (!levelColumns.contains(col)){
			levelColumns.add(col);
		}
		if (!levelIndexes.containsKey(col)){
			levelIndexes.put(col, levelIndexes.size());	
		}
		
		if((cubeInstance.getHierarchyExtractor(lvl.getParentHierarchy()) instanceof SnowFlakesHierarchyExtractor)) {
			if (!levelIndexes.containsKey(lvl.getItem())){
				levelIndexes.put(lvl.getItem(), levelIndexes.size());
			}
		}
		
		
		
		if (!levelColumns.contains(lvl.getItem())){
			levelColumns.add(lvl.getItem());
		}
//		if (!levelFkMapping.containsKey(col)){
//			levelFkMapping.put(col, lvl);
//		}
		
		for(Level actlvl : usedLevel) {
			DataObjectItem actCol = cubeInstance.getFactTable().getItems().get(cubeInstance.getDataLocator().getResultSetIndex(actlvl));
//			if (!levelFkMapping.containsKey(actCol)){
//				levelFkMapping.put(actCol, actlvl);
//			}
			if (!levelFkMapping.containsKey(actlvl)){
				levelFkMapping.put(actlvl, actCol);
			}
		}
		
		//level
		
		extractParentFilter(member, cubeInstance);
		
	}
	
	/**
	 * @return the levelFkMapping
	 */
//	protected HashMap<DataObjectItem, Level> getLevelFkMapping() {
//		return levelFkMapping;
//	}
	
	protected HashMap<Level, DataObjectItem> getLevelFkMapping() {
		return levelFkMapping;
	}
	private void extractRootElementDatas(Member member, ICubeInstance cubeInstance, DataCellIdentifier2 dataCellIdentifier) throws QueryImproverException{
		
		Level lvl = member.getParentHierarchy().getLevels().get(0);
		
		if (!usedLevel.contains(lvl)){
			usedLevel.add(lvl);
		}
		
		if (lvl.getItem().getParent().getParent() == cubeInstance.getFactTable().getParent()){
						
			
			extractRelations(member.getParentHierarchy(), lvl, cubeInstance);
		}
		else{
			DataObjectItem col = cubeInstance.getFactTable().getItems().get(cubeInstance.getDataLocator().getResultSetIndex(lvl)); 

			if (!levelFkMapping.containsKey(lvl)){
				levelFkMapping.put(lvl, col);
			}
			
			if (!levelColumns.contains(col)){
				levelColumns.add(col);
			}
			if (!levelIndexes.containsKey(col)){
				levelIndexes.put(col, levelIndexes.size());	
			}
			
			
			
			if (!member.isChildsLoaded()){
				try {
					cubeInstance.getHierarchyExtractor(member.getParentHierarchy()).getChilds(member, runtimeContext);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for(Member m : member.getSubMembers()){
				extractParentFilter(m, cubeInstance);
			}
		}
		
		
		
	}

	protected abstract String generateSql(ICubeInstance cubeInstance,
			DataCellIdentifier2 possibleId,
			HashMap<DataObjectItem, Integer> measureIndexes,
			HashMap<DataObjectItem, String> measureAggregations,
			HashMap<DataObjectItem, List<Object>> wheresKeys) throws Exception;
	
	
	public void improveQuery(OdaInput odaInput, IQuery query, DataStorage storage, ICubeInstance cubeInstance, DataCellIdentifier2 possibleId) throws Exception {
		try {
			improvedQuery = null;
			improvedDataLocator = null;
			
			HashMap<Member, Boolean> membersAllreadyPerfomed = new HashMap<Member, Boolean>(); 
			
			for(DataCellIdentifier2 id : storage.getPossibleIds().get(possibleId)){
				//find all foreign keys we have to keep in the query
				for(ElementDefinition elem : id.getIntersections()) {
					if(elem instanceof Member) {
						Member mem = (Member) elem;
						
						if (!members.contains(mem)){
							members.add(mem);
						}
						
					}
				}
				for(Member m : members){
					if (membersAllreadyPerfomed.get(m) != null){
						continue;
					}
					if (m.getParentLevel() == null){
						extractRootElementDatas(m, cubeInstance, id);
					}
					else{
						try{
							extractLevelData(m, cubeInstance, id);
						}catch(QueryImproverException ex){
							logger.warn(ex.getMessage());
							logger.warn("The optimized Query will be using all foreign keys on he fact table because of the heteregenous datas providers");
							extractLevelDataForOtherDataSource(m, cubeInstance, id);
						}
					}
					membersAllreadyPerfomed.put(m, true);
				}
				
				//add the level from the where clause
				for(Member m : id.getWhereMembers()){
					try{
						extractLevelData(m, cubeInstance, id);
					}catch(QueryImproverException ex){
						throw ex;
					}
				}
				
			}
			
			
			
			/*
			 * DataLocator creation
			 */		
			//measureMapping
			setMeasureInformations(possibleId.getMeasure(), cubeInstance, measureIndexes, measureAggregations, possibleId);

			
			//find foreign keys for wheres elements 
			HashMap<DataObjectItem, List<Object>> wheresKeys = new HashMap<DataObjectItem, List<Object>>();
			for(Member elem : possibleId.getWhereMembers()) {
					
				List<Object> keys = cubeInstance.getHierarchyExtractor(elem.getParentHierarchy()).getMemberForeignKeys(elem, runtimeContext);
				if(keys != null && keys.size() > 0) {
					
					DataObjectItem col = cubeInstance.getFactTable().getItems().get(cubeInstance.getDataLocator().getResultSetIndex(elem));
					
					
					if(wheresKeys.get(col) != null) {
						wheresKeys.get(col).addAll(keys);
					}
					else {
						wheresKeys.put(col, keys);
					}
				}
			}
			
			String fmdtQuery = odaInput.getQueryText();
			String sql = generateSql(cubeInstance, possibleId, measureIndexes, measureAggregations, wheresKeys);
			//Alloc fmdt aggregation to levelIndexes
			for(DataObjectItem me : measureIndexes.keySet()) {
				if(measureAggregations.get(me) != null && measureAggregations.get(me).equals("FMDT-AGG")) {
					levelIndexes.put(me, levelIndexes.size());
				}
			}

			HashMap<DataObjectItem, Integer> measureIndexes = new HashMap<DataObjectItem, Integer>();
			int count = 0;
			for(DataObjectItem c : this.measureIndexes.keySet()){
				if (this.measureIndexes.get(c) != null){
					measureIndexes.put(c, levelIndexes.size() + count);
					count++;
				}
			}
			
			for(DataObjectItem i : levelIndexes.keySet()){
				if (i.getParent().getParent() != cubeInstance.getFactTable().getParent()){
					measureIndexes.put(possibleId.getMeasure().getItem(), measureIndexes.get(possibleId.getMeasure().getItem()) - 1);
				}
			}
			
			
			
			improvedQuery = sql;
			improvedDataLocator = new ImprovedDataLocator(measureIndexes, levelIndexes, levelFkMapping);
			

			logger.debug("Transform sql query : ");
			logger.debug("From : " + fmdtQuery);
			logger.debug("To : " + improvedQuery);
		} catch (Exception e) {
			throw new QueryImproverException("Error while improving the query", e);
		}
	}
	
	
	/**
	 * 
	 * @param member
	 * @param cubeInstance
	 * @throws Exception if the Level DataObject (or a previous Level ) is not on a JDBC DataObject
	 */
	private void extractLevelData(Member member, ICubeInstance cubeInstance, DataCellIdentifier2 dataCellIdentifier) throws QueryImproverException{

		if (member.getParentLevel() == null){
			return;
		}
		Level lvl = member.getParentLevel();
		
		while(lvl != null) {

			if (cubeInstance.getFactTable().getParent() != lvl.getItem().getParent().getParent()){
				throw new QueryImproverException("We cannot improve the query because the Level " + lvl.getParentHierarchy().getUname() + "." + lvl.getUname() + " has a different datasource than he fact Table", dataCellIdentifier);
			}
			
			//look if its an aggregate item (for FMDT)
			boolean isFmdtAgg = false;
			if(this instanceof FMDTQueryImprover2) {
				
				try {
					if(((FMDTQueryImprover2)this).isFmdtAggregation(member, cubeInstance, dataCellIdentifier, lvl)) {
						isFmdtAgg = true;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			
			if(!isFmdtAgg) {
				if (levelIndexes.get(lvl.getItem()) == null){
					levelIndexes.put(lvl.getItem(), levelIndexes.size());
				}
			}
			
			else {
				int index = cubeInstance.getDataLocator().getResultSetIndex(lvl);
				measureIndexes.put(lvl.getItem(), index);
				measureAggregations.put(lvl.getItem(), "FMDT-AGG");
			}
			
			if(lvl.getOrderItem() != null) {
				if (levelIndexes.get(lvl.getOrderItem()) == null){
					levelIndexes.put(lvl.getOrderItem(), levelIndexes.size());
				}
				if (!levelColumns.contains(lvl.getOrderItem())){
					levelColumns.add(lvl.getOrderItem());
					orderColumns.add(lvl.getOrderItem());
				}
			}
			
			if (!levelColumns.contains(lvl.getItem())){
				levelColumns.add(lvl.getItem());
			}
			
			if (!usedLevel.contains(lvl)){
				usedLevel.add(lvl);
			}
			lvl = lvl.getParentLevel();
				
		}
		
		if (!levelColumns.contains(member.getParentLevel().getItem())){
			levelColumns.add(member.getParentLevel().getItem());
		}
		if (!usedLevel.contains(member.getParentLevel())){
			usedLevel.add(member.getParentLevel());
		}

		if (member.getParentLevel() instanceof ClosureLevel){
			
			if (levelIndexes.get(((ClosureLevel)member.getParentLevel()).getParentItem()) == null){
				levelIndexes.put(((ClosureLevel)member.getParentLevel()).getParentItem(), levelIndexes.size());
			}
			if (!levelColumns.contains(((ClosureLevel)member.getParentLevel()).getParentItem())){
				levelColumns.add(((ClosureLevel)member.getParentLevel()).getParentItem());
			}
		}
		
	
		
		if (member.getParentMember() != null){
			extractParentFilter(member.getParentMember(), cubeInstance);
		}
		extractRelations(member.getParentHierarchy(), member.getParentLevel(), cubeInstance);

		
	}
	
	
	private void extractRelations(Hierarchy hiera, Level lvl, ICubeInstance cubeInstance){
		DataObject lvlDataObject = null;
		
		List<Relation> path = new ArrayList<Relation>();
		
		if (lvl == null){
			lvlDataObject = hiera.getLevels().get(0).getItem().getParent();
			
		}
		else{
			lvlDataObject = lvl.getItem().getParent();
			
			
			
			//get path between previousLevel and lvlTable
			for(Level l : usedLevel){
				if (l.getParentHierarchy() == hiera && l != lvl && l.getItem().getParent() != lvlDataObject){
					try{
						path.addAll(cubeInstance.getDataLocator().getPath(l.getItem().getParent(), lvlDataObject));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
			
		}
		
		//get path between factTable and lvlTable
		try{
			List<Relation> l = cubeInstance.getDataLocator().getPath(cubeInstance.getFactTable(), lvlDataObject);
			if (l != null){
				path.addAll(l);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		//add the relations
		for(Relation r : path){
			if (!relations.contains(r)){
				relations.add(r);
			}
		}
	
		
	}
	
	/**
	 * put the values FK for the give member within this.parentFilter 
	 */
	private void extractParentFilter(Member member, ICubeInstance cubeInstance){
		if (member == null){
			return;
		}
		if (member.getParentLevel() == null){
			return;
		}
		try{
			DataObjectItem colKey = null; //cubeInstance.getFactTable().getItems().get(cubeInstance.getDataLocator().getResultSetIndex(member));
			
			List<Object> values = new ArrayList<Object>();
			if (!member.getParentHierarchy().getUname().contains("status")){
//				System.out.println();
			}
			if (member.getParentLevel().getItem().getParent().getParent() != cubeInstance.getFactTable().getParent()){
				colKey = cubeInstance.getFactTable().getItems().get(cubeInstance.getDataLocator().getResultSetIndex(member));
				values.addAll(getChildForeignKeys(cubeInstance, member));
			}
			else{
				colKey = member.getParentLevel().getItem();
				if (member.getParentLevel() instanceof ClosureLevel){
					colKey = ((ClosureLevel)member.getParentLevel()).getParentItem();
					values.addAll(getChildForeignKeys(cubeInstance, member));
				}
				else{
//					List<Object> keys = cubeInstance.getHierarchyExtractor(member.getParentHierarchy()).getMemberForeignKeys(member, runtimeContext);
//					if(keys != null && keys.size() > 0) {
//						values.add(keys.get(0).toString());
//					}
//					else {
						values.add(member.getName());
//					}
				}
			}
			
			
			if (parentFilters.get(colKey) == null){
				parentFilters.put(colKey, values);
			}
			else{
				for(Object v : values){
					if (!parentFilters.get(colKey).contains(v)){
						parentFilters.get(colKey).add(v);
					}
				}
			}
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	private List<Object> getChildForeignKeys(ICubeInstance cubeInstance, Member mb) throws Exception{
		List<Object> values = new ArrayList<Object>();
		
		values.addAll(cubeInstance.getHierarchyExtractor(mb.getParentHierarchy()).getMemberForeignKeys(mb, runtimeContext));
		
		
		if (mb.getParentHierarchy().isIsClosureHierarchy()){
			if (!mb.isChildsLoaded()){
				cubeInstance.getHierarchyExtractor(mb.getParentHierarchy()).getChilds(mb, runtimeContext);
			}
			
			for(Member m : mb.getSubMembers()){
				values.addAll(getChildForeignKeys(cubeInstance, m));
			}
		}
		
		return values;
	}
	
	
	public String getImprovedQuery() {
		return improvedQuery;
	}
	
	public IDataLocator getImprovedDataLocator() {
		return improvedDataLocator;
	}
	/**
	 * @return the levelColumns
	 */
	protected List<DataObjectItem> getLevelColumns() {
		return levelColumns;
	}
	/**
	 * @return the parentFilters
	 */
	protected HashMap<DataObjectItem, List<Object>> getParentFilters() {
		return parentFilters;
	}
	/**
	 * @return the usedLevel
	 */
	protected List<Level> getUsedLevel() {
		return usedLevel;
	}
	/**
	 * @return the relations
	 */
	protected List<Relation> getRelations() {
		return relations;
	}
	
	public List<DataObjectItem> getOrderColumns() {
		return orderColumns;
	}
	
}
