package bpm.fasd.expressions.api.model;

import java.util.List;

import bpm.studio.expressions.core.measures.IOperator;
import bpm.studio.expressions.core.measures.impl.Dimension;
import bpm.studio.expressions.core.measures.impl.DimensionLevel;
import bpm.studio.expressions.core.measures.impl.Measure;
import bpm.studio.expressions.core.measures.impl.MeasureParser;

public class FasdParser extends MeasureParser{

	public FasdParser(List<Measure> availableMeasures,List<DimensionLevel> levels, List<Dimension> dimensions) {
		super(availableMeasures, levels, dimensions);

	}

	
	@Override
	protected List<IOperator> getOperators() {
		List<IOperator> l = super.getOperators();
		
		for(FormatingOperator op : FormatingOperator.operators){
			l.add(op);
		}
		return l;
	}
}
