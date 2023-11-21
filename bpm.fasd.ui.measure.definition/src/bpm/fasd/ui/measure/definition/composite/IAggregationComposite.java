package bpm.fasd.ui.measure.definition.composite;

import org.fasd.olap.OLAPMeasure;

public interface IAggregationComposite {

	OLAPMeasure getMeasure();
	
	boolean canFinish();
}
