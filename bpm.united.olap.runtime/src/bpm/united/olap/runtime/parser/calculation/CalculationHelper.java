package bpm.united.olap.runtime.parser.calculation;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import bpm.mdx.parser.ANTLRStringStreamCaseInsensitive;
import bpm.mdx.parser.CalculatedMeasureLexer;
import bpm.mdx.parser.CalculatedMeasureParser;
import bpm.mdx.parser.CalculatedMeasureTree;
import bpm.mdx.parser.CalculatedMeasureParser.calculatedmeasure_return;
import bpm.mdx.parser.result.CalculatedItem;
import bpm.mdx.parser.result.NodeEvaluator;
import bpm.mdx.parser.result.TermItem;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.aggregation.CalculatedAggregation;
import bpm.united.olap.api.model.aggregation.ClassicAggregation;
import bpm.united.olap.api.model.aggregation.ILevelAggregation;
import bpm.united.olap.api.model.aggregation.LastAggregation;
import bpm.united.olap.api.model.impl.CalculatedMeasure;
import bpm.united.olap.api.model.impl.ComplexMeasure;
import bpm.united.olap.api.model.impl.DynamicMeasure;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.model.impl.ProjectionMeasureCondition;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RuntimeFactory;
import bpm.united.olap.api.runtime.calculation.AverageCalculation;
import bpm.united.olap.api.runtime.calculation.CountCalculation;
import bpm.united.olap.api.runtime.calculation.DistinctCountCalculation;
import bpm.united.olap.api.runtime.calculation.FirstCalculation;
import bpm.united.olap.api.runtime.calculation.ICalculation;
import bpm.united.olap.api.runtime.calculation.LastCalculation;
import bpm.united.olap.api.runtime.calculation.MaximumCalculation;
import bpm.united.olap.api.runtime.calculation.MinimumCalculation;
import bpm.united.olap.api.runtime.calculation.SumCalculation;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * An helper class to find a cell calculation
 * @author Marc Lanquetin
 *
 */
public class CalculationHelper {

	public static String SUM = "sum";
	public static String AVG = "avg";
	public static String MIN = "min";
	public static String MAX = "max";
	public static String COUNT = "count";
	public static String COUNTD = "distinct-count";
	public static String LAST = "last";
	public static String FIRST = "first";
	
	public static IVanillaLogger logger;
	
	/**
	 * Return the calculation type for a cell
	 * @param cell
	 * @param mes
	 * @param defs 
	 * @return
	 */
	public static ICalculation getCalculation(DataCell cell, Measure mes, List<ElementDefinition> defs, IVanillaLogger logger) {
		
		CalculationHelper.logger = logger;
		
		if(mes instanceof ProjectionMeasure) {
			return findProjectionCalculation(cell, (ProjectionMeasure) mes, defs, logger);
		}
		
		else if(mes instanceof ComplexMeasure) {
			if(((ComplexMeasure) mes).getType().equals(ComplexMeasure.BASICFORMULA)) {
				ComplexMeasure mesc = (ComplexMeasure) mes;
				ICalculation calculation = getCalculation(cell, ((MdxSet)((ArrayList)mesc.getLeftItem()).get(0)).getMeasure(), defs, logger);
				calculation.setIsPercentile(true);
				return calculation;
			}
		}
		
		else if(mes instanceof DynamicMeasure) {
			return findDynamicAggregation(cell, (DynamicMeasure) mes, defs);
		}
		
		else {
			if(mes instanceof CalculatedMeasure) {
				return findCalculatedAggregation(cell, (CalculatedMeasure)mes, defs);
			}
			else {
				return findClassicAggregation(cell, mes, mes.getCalculationType());
			}
		}
		
		return null;
	}
	
	private static ICalculation findProjectionCalculation(DataCell cell, ProjectionMeasure mes, List<ElementDefinition> defs, IVanillaLogger logger2) {
		
		RuntimeFactory factory = RuntimeFactory.eINSTANCE;
		
		DataCell c = factory.createDataCell();
		ICalculation calc = CalculationHelper.getCalculation(c, mes.getMeasure(), defs, CalculationHelper.logger);
		ProjectionCalculation res = new ProjectionCalculation(cell, mes, calc, logger);
		
		return res;
		
	}

	private static ICalculation findCalculatedAggregation(DataCell cell, CalculatedMeasure calcMes, List<ElementDefinition> defs) {
		List<ICalculation> calculations = new ArrayList<ICalculation>();
		
		RuntimeFactory factory = RuntimeFactory.eINSTANCE;
		
		if(calcMes.getItems() != null && !calcMes.getItems().isEmpty()) {
			for(Measure mes : calcMes.getItems()) {
				DataCell c = factory.createDataCell();
				ICalculation calc = CalculationHelper.getCalculation(c, mes, defs, CalculationHelper.logger);
				calculations.add(calc);
			}
		}
		
		CalculatedMeasureCalculation res = new CalculatedMeasureCalculation(cell, calcMes, calculations, calcMes.getCalculatdFormula(), logger);
		
		return res;
	}

	/**
	 * Find the correct aggregation for a classic measure
	 * @param cell
	 * @param mes
	 * @param aggregator
	 * @return
	 */
	private static ICalculation findClassicAggregation(DataCell cell, Measure mes, String aggregator) {
		if(aggregator.equals(SUM)) {
			return new SumCalculation(cell, mes);
		}
		else if(aggregator.equals(AVG)) {
			return new AverageCalculation(cell, mes);
		}
		else if(aggregator.equals(COUNT)) {
			return new CountCalculation(cell, mes);
		}
		else if(aggregator.equals(COUNTD)) {
			return new DistinctCountCalculation(cell, mes);
		}
		else if(aggregator.equals(MAX)) {
			return new MaximumCalculation(cell, mes);
		}
		else if(aggregator.equals(MIN)) {
			return new MinimumCalculation(cell, mes);
		}
		else if(aggregator.equals(LAST)) {
			return new LastCalculation(cell, mes);
		}
		else if(aggregator.equals(FIRST)) {
			return new FirstCalculation(cell, mes);
		}
		return null;
	}

	/**
	 * Find the aggregation for the actual level or use the default one
	 * @param cell
	 * @param mes
	 * @param defs
	 * @return
	 */
	private static ICalculation findDynamicAggregation(DataCell cell, DynamicMeasure mes, List<ElementDefinition> defs) {
		
		ILevelAggregation aggreg = null;
		
		for(ElementDefinition elem : defs) {
			
			for(ILevelAggregation agg : mes.getAggregations()) {
				
				if(elem instanceof Member) {
					Member member = (Member) elem;
					
					if(member.getParentLevel().getUname().equals(agg.getLevel())) {
						
						//Classic aggregation
						if(agg instanceof ClassicAggregation) {
							
							ICalculation calc = findClassicAggregation(cell, mes, ((ClassicAggregation)agg).getAggregator());
							calc.setItem(((ClassicAggregation)agg).getOrigin());
							return calc;
						}
						
						//First/Last aggregation
						else if(agg instanceof LastAggregation) {
							ICalculation calc = findClassicAggregation(cell, mes, ((ClassicAggregation)agg).getAggregator());
							calc.setItem(((LastAggregation)agg).getOrigin());
							return calc;
							
						}
						
						//Calculated aggregation
						else if(agg instanceof CalculatedAggregation) {
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
							
							List<ICalculation> calculations = new ArrayList<ICalculation>();
							for(NodeEvaluator node : item.getItems()) {
								if(node instanceof TermItem) {
									
									String measur = ((TermItem)node).getUname();
									
									for(Measure m : mes.getParentSchema().getMeasures()) {
										if(m.getUname().equals(measur)) {
											DataCell c = RuntimeFactory.eINSTANCE.createDataCell();
											ICalculation calc = CalculationHelper.getCalculation(c, m, defs, logger);
											calculations.add(calc);
										}
									}
									
								}
							}
							
							return new CalculatedMeasureCalculation(cell, mes, calculations, formula, logger);
							
						}
					}
					
				}
			}
			
		}
		
		if(aggreg == null) {
			if(mes.getCalculatdFormula() != null && ! mes.getCalculatdFormula().equals("")  && (mes.getLastDimensionName() == null || mes.getLastDimensionName().equals(""))) {
				List<ICalculation> calculations = new ArrayList<ICalculation>();
				
				RuntimeFactory factory = RuntimeFactory.eINSTANCE;
				
				for(Measure me : mes.getItems()) {
					DataCell c = factory.createDataCell();
					ICalculation calc = CalculationHelper.getCalculation(c, me, defs, CalculationHelper.logger);
					calculations.add(calc);
				}
				
				CalculatedMeasureCalculation res = new CalculatedMeasureCalculation(cell, mes, calculations, mes.getCalculatdFormula(), logger);
				
				return res;
			}
			else {
				return findClassicAggregation(cell, mes, mes.getCalculationType());
			}
		}
		
		return null;
	}

	
}
