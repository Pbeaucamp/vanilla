package bpm.united.olap.runtime.parser.function;

import java.util.List;

import bpm.mdx.parser.result.NodeEvaluator;
import bpm.mdx.parser.result.TopCountItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.calculation.ICalculation;
import bpm.united.olap.runtime.parser.MdxEvaluator;
import bpm.united.olap.runtime.parser.calculation.TopCountCalculation;

/**
 * Evaluate TopCount and BottomCount function
 * @author Marc Lanquetin
 *
 */
public class TopCountEvaluator implements IFunctionEvaluator {

	private MdxEvaluator evaluator;
	
	public TopCountEvaluator(MdxEvaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	@Override
	public List<MdxSet> evaluate(NodeEvaluator node) throws Exception {
		
		TopCountItem item = (TopCountItem) node;
		
		List<MdxSet> sets = evaluator.evaluateItem(item.getSet());
		int count = item.getCount();
		boolean isTop = item.isTop();
		
		Measure measure = null;
		if(item.getMeasure() != null) {
			List<MdxSet> mes = evaluator.evaluateItem(item.getMeasure());
			measure = mes.get(0).getMeasure();
		}
		
		ICalculation calcul = new TopCountCalculation(sets, count, measure, isTop);
		
		evaluator.addComplexCalculation(calcul);
		
		return sets;
	}

}
