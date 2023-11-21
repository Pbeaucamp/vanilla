package bpm.united.olap.runtime.parser;

import java.util.List;

import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.api.runtime.calculation.ICalculation;

/**
 * 
 * @author Marc Lanquetin
 *
 */
public interface IMdxEvaluator {
	
	/**
	 * evaluate the tree resulting from the mdx query parsing
	 * @return
	 */
	RunResult evaluateResultTree() throws Exception ;

	List<ICalculation> getComplexCalculations();
	
}
