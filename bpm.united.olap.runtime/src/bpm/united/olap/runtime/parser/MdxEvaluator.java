package bpm.united.olap.runtime.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.log4j.Logger;

import bpm.mdx.parser.ANTLRStringStreamCaseInsensitive;
import bpm.mdx.parser.CalculatedMeasureLexer;
import bpm.mdx.parser.CalculatedMeasureParser;
import bpm.mdx.parser.CalculatedMeasureTree;
import bpm.mdx.parser.LastFunctionLexer;
import bpm.mdx.parser.LastFunctionParser;
import bpm.mdx.parser.LastFunctionTree;
import bpm.mdx.parser.CalculatedMeasureParser.calculatedmeasure_return;
import bpm.mdx.parser.LastFunctionParser.parseFunction_return;
import bpm.mdx.parser.result.AggregateItem;
import bpm.mdx.parser.result.AxisItem;
import bpm.mdx.parser.result.BasicFormulaItem;
import bpm.mdx.parser.result.BasicItem;
import bpm.mdx.parser.result.CalculatedItem;
import bpm.mdx.parser.result.CrossjoinItem;
import bpm.mdx.parser.result.NodeEvaluator;
import bpm.mdx.parser.result.PeriodFunctionItem;
import bpm.mdx.parser.result.ProjectionItem;
import bpm.mdx.parser.result.ProjectionItemCondition;
import bpm.mdx.parser.result.RootItem;
import bpm.mdx.parser.result.TermItem;
import bpm.mdx.parser.result.TopCountItem;
import bpm.mdx.parser.result.UnionItem;
import bpm.mdx.parser.result.WithItem;
import bpm.united.olap.api.constant.MemberFunctionNames;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.CalculatedMeasure;
import bpm.united.olap.api.model.impl.ComplexMeasure;
import bpm.united.olap.api.model.impl.DynamicMeasure;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.model.impl.ProjectionMeasureCondition;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.api.runtime.RuntimeFactory;
import bpm.united.olap.api.runtime.calculation.ICalculation;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.parser.calculation.CalculationHelper;
import bpm.united.olap.runtime.parser.function.CrossjoinEvaluator;
import bpm.united.olap.runtime.parser.function.IFunctionEvaluator;
import bpm.united.olap.runtime.parser.function.TopCountEvaluator;
import bpm.united.olap.runtime.parser.function.UnionEvaluator;
import bpm.united.olap.runtime.sort.HierarchizeResult;
import bpm.united.olap.runtime.sort.ISorterResult;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class MdxEvaluator implements IMdxEvaluator {

	protected RootItem tree;
	
	protected RuntimeFactory factory;
	protected List<ICalculation> complexCalculations = new ArrayList<ICalculation>();
	protected LinkedHashMap<String, List<MdxSet>> withMembers = new LinkedHashMap<String, List<MdxSet>>();
//	private IQueryHelper helper;
	
//	private HashMap<Measure, Properties> lastMeasureProperties = new HashMap<Measure, Properties>();
	protected IVanillaLogger logger;

	protected boolean rowDone;
	protected boolean isMeasureRow;
	
	protected IRuntimeContext runtimeCtx;
	protected ICubeInstance cubeInstance;
	
	protected HashMap<String, ProjectionMeasure> projectionMeasures = new HashMap<String, ProjectionMeasure>();
	
	public MdxEvaluator(RootItem tree, ICubeInstance cubeInstance/*, IQueryHelper helper, */, IVanillaLogger logger, IRuntimeContext runtimeCtx) {
		this.tree = tree;
		this.cubeInstance = cubeInstance;
		factory = RuntimeFactory.eINSTANCE;
//		this.helper = helper;
		this.logger = logger;
		this.runtimeCtx = runtimeCtx;
	}
	
	@Override
	public RunResult evaluateResultTree() throws Exception {
		
		//get the selected cube
		String cubeName = tree.getFrom();
		
		//get the rows and columns
		ISorterResult sort = new HierarchizeResult(logger);
		
		List<List<MdxSet>> results = new ArrayList<List<MdxSet>>();
		
		Date d = new Date();
		
		createWithMembers(tree.getWithItems());
		
		results.add(evaluateAxis(tree.getRow()));
		rowDone = true;
		results.add(evaluateAxis(tree.getCol()));
		
		List<MdxSet> wheres = evaluateAxis(tree.getWhere());
		RunResult runRes = sort.sortResult(results);
		
		d = new Date();
		Logger.getLogger(getClass()).debug("sorting RunResult.....");

		purgeResults(runRes.getMdxSets());

		Logger.getLogger(getClass()).debug("sorted RunResult in " + (new Date().getTime() - d.getTime()));

		runRes.setIsNonEmpty(tree.isNonEmpty());
		
		runRes.setWhereSets(wheres);
		
		runRes.setIsMeasureRow(isMeasureRow);
		
		return runRes;
	}

	/**
	 * create the members from the with part
	 * @param withItems
	 * @throws Exception 
	 */
	protected void createWithMembers(List<NodeEvaluator> withItems) throws Exception {
		
		if(withItems != null && withItems.size() > 0) {
		
			for(NodeEvaluator withItem : withItems) {
				WithItem item = (WithItem) withItem;
				
				String itemName = item.getUname();
				
				if(item.getValue() instanceof ProjectionItem) {
					createProjectionMeasure(itemName, (ProjectionItem)item.getValue());
				}
				
				else {
					List<MdxSet> itemValue = evaluateItem(item.getValue());	
					
					withMembers.put(itemName, itemValue);
				}
			}
			
		}
		
	}
	
	/**
	 * Create a projection measure
	 * @param itemName
	 * @param value
	 * @throws Exception 
	 */
	protected void createProjectionMeasure(String itemName, ProjectionItem value) throws Exception {
		
		Measure originalMeasure = evaluateItem(value.getMeasure()).get(0).getMeasure();
		ProjectionMeasure measure = new ProjectionMeasure(originalMeasure);
		measure.setUname(itemName);
		measure.setName(itemName.split("\\]\\.\\[")[1].replace("[", "").replace("]", ""));
		
		for(ProjectionItemCondition cond : value.getConditions()) {
			
			if(cond.getItems().get(0).getUname().equals("[Default]"))  {
				measure.setFormula(cond.getFormula());
			}
			else {
				ProjectionMeasureCondition c = new ProjectionMeasureCondition();
				c.setFormula(cond.getFormula());
				
				for(TermItem item : cond.getItems()) {
					Member m = (Member) evaluateItem(item).get(0).getElements().get(0);
					c.addMember(m);
				}
				
				measure.addCondition(c);
			}
		}
		
		projectionMeasures.put(itemName, measure);
	}

	/**
	 * find all sets on this axis
	 * @param nodeAxis
	 * @return
	 * @throws Exception 
	 */
	protected List<MdxSet> evaluateAxis(NodeEvaluator nodeAxis) throws Exception {
		if(nodeAxis != null) {
			List<MdxSet> results = new ArrayList<MdxSet>();
			AxisItem axis = (AxisItem) nodeAxis;
	
			for(NodeEvaluator node : axis.getItems()) {
				if(node != null) {
					results.addAll(evaluateItem(node));
				}
			}
			
			return results;
		}
		return null;
	}
	
	
	private long evaluationItemTime = 0;
	private long evaluationItemCalls = 0;
	
	public List<MdxSet> evaluateItem(NodeEvaluator node) throws Exception {
		evaluationItemCalls ++;
		Date d = new Date();
		
		List<MdxSet> result = null;
		
		if(node instanceof TermItem) {
			result = findElements((TermItem)node);
		}
		
		else if(node instanceof BasicFormulaItem) {
			result = evaluateBasicFormula(node);
		}
		
		else if(node instanceof AggregateItem) {
			result = evaluateAggregate(node);
		}
		
		else {
			result = evaluateFunction(node);
		}
		
		evaluationItemTime += (new Date().getTime() - d.getTime());
		
		
		
		return result;
	}
	
	public long getEvaluationItemCalls(){
		return evaluationItemCalls;
	}
	public long getEvaluationItemTime(){
		return evaluationItemTime;
	}

	/**
	 * evaluate an aggregate
	 * @param node
	 * @return
	 * @throws Exception 
	 */
	protected List<MdxSet> evaluateAggregate(NodeEvaluator node) throws Exception {
		
		AggregateItem item = (AggregateItem) node;
		
		List<MdxSet> results = new ArrayList<MdxSet>();
		
		for(NodeEvaluator no : item.getNodes()) {
			results.addAll(evaluateItem(no));
		}
		
		return results;
	}

	/**
	 * evaluate basic formulas such as : +, -, *, /
	 * @param node
	 * @return
	 * @throws Exception 
	 */
	protected List<MdxSet> evaluateBasicFormula(NodeEvaluator node) throws Exception {
		
		BasicFormulaItem item = (BasicFormulaItem) node;
		
		ComplexMeasure mes = new ComplexMeasure();
		mes.setType(ComplexMeasure.BASICFORMULA);
		
		for(NodeEvaluator with : tree.getWithItems()) {
			if(((WithItem)with).getValue().equals(node)) {
				mes.setName(((WithItem)with).getUname());
				mes.setUname(((WithItem)with).getUname());
				mes.setFormat(((WithItem)with).getFormat());
				break;
			}
		}
		
		if(!(item.getLeftItem() instanceof BasicItem)) {
			List<MdxSet> left = evaluateItem(item.getLeftItem());
			mes.setLeftItem(left);
		}
		else {
			mes.setLeftItem(((BasicItem)item.getLeftItem()).getValue());
		}
		if(!(item.getRightItem() instanceof BasicItem)) {
			List<MdxSet> right = evaluateItem(item.getRightItem());
			mes.setRightItem(right);
		}
		else {
			mes.setRightItem(((BasicItem)item.getRightItem()).getValue());
		}
		
		mes.setOperator(item.getOperator());
		
		List<MdxSet> sets = new ArrayList<MdxSet>();
		MdxSet set = factory.createMdxSet();
		set.setMeasure(mes);
		sets.add(set);
		return sets;
	}

	/**
	 * evaluate a function
	 * @param node
	 * @return
	 * @throws Exception 
	 */
	public List<MdxSet> evaluateFunction(NodeEvaluator node) throws Exception {
		IFunctionEvaluator evaluator = null;
		if(node instanceof CrossjoinItem) {
			evaluator = new CrossjoinEvaluator(this);
		}
		else if(node instanceof UnionItem) {
			evaluator = new UnionEvaluator(this);
		}
		
		else if(node instanceof TopCountItem) {
			evaluator = new TopCountEvaluator(this);
		}
		
		return evaluator.evaluate(node);
	}

	/**
	 * find the members or measures
	 * @param node
	 * @return
	 * @throws Exception 
	 */
	public List<MdxSet> findElements(TermItem node) throws Exception {
		
		List<MdxSet> results = new ArrayList<MdxSet>();
		
		String[] unameParts = node.getUname().split("\\.");
		String dimName = unameParts[0];
		
		//find the measure
		if(dimName.equals("[Measures]")) {
			Measure measure = null;
			for(Measure mes : cubeInstance.getCube().getMeasures()) {
				if(mes.getUname().equals(node.getUname())) {
					measure = mes;
					break;
				}
			}
			
			//if the measure is in the cube
			if(measure != null) {
				
				if(measure instanceof CalculatedMeasure) {
					
					if(((CalculatedMeasure)measure).getItems() == null || ((CalculatedMeasure)measure).getItems().size() == 0) {
					
						findCalculatedMeasureElements(measure);
					}
					
					MdxSet set = factory.createMdxSet();
					set.setMeasure(measure);
					results.add(set);
				}
				
				else if(measure instanceof DynamicMeasure && measure.getCalculatdFormula() != null && !measure.getCalculatdFormula().equals("") && (measure.getLastDimensionName() == null || measure.getLastDimensionName().equals(""))) {
					if(((DynamicMeasure)measure).getItems() == null || ((DynamicMeasure)measure).getItems().size() == 0) {
						
						findCalculatedMeasureElements(measure);
					}
					
					MdxSet set = factory.createMdxSet();
					set.setMeasure(measure);
					results.add(set);
				}
				
				else {
					MdxSet set = factory.createMdxSet();
					set.setMeasure(measure);
					results.add(set);
					
					if(measure.getCalculationType().equals(CalculationHelper.LAST) || measure.getCalculationType().equals(CalculationHelper.FIRST)) {
						findLastDefinition(measure);
					}
				}
			}
			
			//if its a calculated measure
			else {
				
				//look in projections
				if(projectionMeasures.containsKey(node.getUname())) {
					MdxSet set = factory.createMdxSet();
					set.setMeasure(projectionMeasures.get(node.getUname()));
					results.add(set);
				}
				
				else {
					results.add(withMembers.get(node.getUname()).get(0));
				}
			}
			
			if(!rowDone) {
				isMeasureRow = true;
			}
			else {
				isMeasureRow = false;
			}
			
		}
		
		//find the members
		else {
			if(withMembers.containsKey(node.getUname())) {
				results.addAll(withMembers.get(node.getUname()));
			}
			
			else {
				for(Dimension dim : cubeInstance.getCube().getDimensions()) {
					//find the right dimension
					if(dim.getUname().equals(dimName + "]")) {
						for(Hierarchy hiera : dim.getHierarchies()) {
							if(hiera.getName().equals(unameParts[1].replace("]", ""))) {
								results.addAll(findMembers(node, hiera));
								break;
							}
						}
						break;
					}
				}
			}
		}
		
		return results;
	}

	/**
	 * Find all the measures used in this calculated measure
	 * @param measure
	 */
	public void findCalculatedMeasureElements(Measure measure) throws Exception {
		
		//XXX Change this shit
		String func = measure.getCalculatdFormula();
//		CharStream input = new ANTLRStringStreamCaseInsensitive(func);
//		CalculatedMeasureLexer lexer = new CalculatedMeasureLexer(input);
//		TokenStream tok = new CommonTokenStream(lexer);
//		CalculatedMeasureParser parser = new CalculatedMeasureParser(tok);
//		calculatedmeasure_return ret = parser.calculatedmeasure();
//		CommonTree tree = (CommonTree) ret.getTree();
//		
//		CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(tree);
//		CalculatedMeasureTree walker = new CalculatedMeasureTree(nodeStream);
//		CalculatedItem item = walker.result();
//		item.setFormula(func);
//		
//		for(NodeEvaluator node : item.getItems()) {
//			if(node instanceof TermItem) {
//				List<MdxSet> sets = evaluateItem(node);
//				for(MdxSet set : sets) {
//					if (set.getMeasure() != null){
//						if(measure instanceof CalculatedMeasure) {
//							((CalculatedMeasure)measure).addItem(set.getMeasure());
//						}
//						else if(measure instanceof DynamicMeasure) {
//							((DynamicMeasure)measure).addItem(set.getMeasure());
//						}
//					}
//
//				}
//			}
//		}
		
		for(Measure mes : cubeInstance.getCube().getMeasures()) {
			if(func.contains(mes.getUname())) {
				if(measure instanceof CalculatedMeasure) {
					((CalculatedMeasure)measure).addItem(mes);
				}
				else if(measure instanceof DynamicMeasure) {
					((DynamicMeasure)measure).addItem(mes);
				}
			}
		}
	}

	protected void findLastDefinition(Measure measure) throws RecognitionException {
//		Properties props = lastMeasureProperties.get(measure);
		
		if(measure.getLastDimensionName() == null) {
		
			String level = "";
			String dimension = "";
			String dimFunc = "";
			boolean last;
			
			CharStream input = new ANTLRStringStreamCaseInsensitive(measure.getCalculatdFormula());
			LastFunctionLexer lexer = new LastFunctionLexer(input);
			TokenStream tok = new CommonTokenStream(lexer);
			LastFunctionParser parser = new LastFunctionParser(tok);
			parseFunction_return ret = parser.parseFunction();
			CommonTree tree = (CommonTree) ret.getTree();
			CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(tree);
			LastFunctionTree walker = new LastFunctionTree(nodeStream);
			PeriodFunctionItem item = walker.result();
			
			if(item.getLevel() instanceof TermItem) {
				level = ((TermItem)item.getLevel()).getUname();
			}
			if(item.getMember() instanceof TermItem) {
				String mem = ((TermItem)item.getMember()).getUname();
				String[] memParts = mem.split("\\.");
				dimension = memParts[0].replace("[", "").replace("]", "");
				dimFunc = memParts[1];
			}
			last = item.isLast();
			
			measure.setLastDimensionName(dimension);
		}
	}

	/**
	 * find the members for this node
	 * @param node
	 * @param hiera
	 * @return
	 * @throws Exception 
	 */
	
	private long findMemberTime = 0;
	private int findMemberCall = 0;
	protected List<MdxSet> findMembers(TermItem node, Hierarchy hiera) throws Exception {
		Date d = new Date();
		findMemberCall++;
		
		List<MdxSet> results = new ArrayList<MdxSet>();
		
		//TODO need to change this to handle "." correctly
		String[] unameParts = node.getUname().replace("[","").replace("]","").split("\\.");
		
		if(unameParts.length <= 1) {
			for(Dimension dim : cubeInstance.getCube().getDimensions()) {
				if(dim.getName().equals(unameParts[0])) {
					MdxSet set = factory.createMdxSet();
				
					set.getElements().add(dim);
					results.add(set);
					findMemberTime += new Date().getTime() - d.getTime();
					return results;
				}
			}
		}

		boolean isFunction = false;
		if(Arrays.asList(MemberFunctionNames.NAMES).contains(unameParts[unameParts.length - 1].toUpperCase())) {
			isFunction = true;
		}
		
		List<Member> resMembers = null;
 		
		
		if (isFunction){
			//find member children
			if (unameParts[unameParts.length - 1].toUpperCase().equals(MemberFunctionNames.CHILDREN)) {
				Member m = cubeInstance.getHierarchyExtractor(hiera).getMember(node.getUname().replace(".children", ""), runtimeCtx);
					
				resMembers = cubeInstance.getHierarchyExtractor(hiera).getChilds(m, runtimeCtx);
			}
			
			//find all the members for the level
			else if(unameParts[unameParts.length - 1].toUpperCase().equals(MemberFunctionNames.MEMBERS)) {

				String lvlUname = node.getUname().substring(0, (node.getUname().length() - 1) - MemberFunctionNames.MEMBERS.length());
				
				Level lookedLevel = null;
				for(Level lvl : hiera.getLevels()) {
					if(lvl.getUname().equals(lvlUname)) {
						lookedLevel = lvl;
						break;
					}
				}
				
				if(lookedLevel == null) {
					throw new Exception("Can't find level " + lvlUname + " in hierarchy " + hiera.getUname());
				}
							
				resMembers = cubeInstance.getHierarchyExtractor(hiera).getLevelMembers(lookedLevel, runtimeCtx);
			}
			
			//find this member
			else{
				Member m = cubeInstance.getHierarchyExtractor(hiera).getMember(node.getUname(), runtimeCtx);
				if (m == null){
					throw new Exception("Member " + node.getUname() + " not found");
				}
			}
			
		}
		else{
			if (resMembers == null){
			resMembers = new ArrayList<Member>();
			}
			resMembers.add(cubeInstance.getHierarchyExtractor(hiera).getMember(node.getUname(), runtimeCtx));
		}
		
		for(Member mem : resMembers) {
			MdxSet set = factory.createMdxSet();
			set.getElements().add(mem);
			results.add(set);
		}
		findMemberTime += new Date().getTime() - d.getTime();
		return results;
	}

	public long getFindMemberCall(){
		return findMemberCall;
	}
	public long getFindMemberTime(){
		return findMemberTime;
	}
	
	public void setComplexCalculations(List<ICalculation> complexCalculations) {
		this.complexCalculations = complexCalculations;
	}

	public List<ICalculation> getComplexCalculations() {
		return complexCalculations;
	}
	
	public void addComplexCalculation(ICalculation complexCalculation) {
		this.complexCalculations.add(complexCalculation);
	}
	
	/**
	 * Used to purge the results. Necessary to avoid doubloons
	 * @param results
	 */
	protected void purgeResults(List<List<MdxSet>> results) {
		
		synchronized (results) {
		
			for(List<MdxSet> axis : results) {
				
				for(int i = 0 ; i < axis.size() ; i++) {
					
					if(isContains(axis.get(i),axis,i)) {
						axis.remove(i);
						i--;
						
					}
				}
				
			}
			
		}
		
	}

	protected boolean isContains(MdxSet set, List<MdxSet> axis, int index) {
		
//		for(int i = 0 ; i < axis.size() ; i++) {
//			if(i == index) {
//				continue;
//			}
			if (axis.size() < index + 2){
				return false;
			}
			MdxSet lookedSet = axis.get(index + 1);
			
			if (set.getMeasure() != lookedSet.getMeasure()){
				return false;
			}
			
			boolean exists = true;
			for(int j = 0 ; j < set.getElements().size() ; j++) {
				if(!(set.getElements().get(j) == lookedSet.getElements().get(j))) {
					exists = false;
					break;
				}
			}
			if(exists && set.getMeasure() == lookedSet.getMeasure()) {
				return true;
			}
//		}
		
		return false;
	}
}
