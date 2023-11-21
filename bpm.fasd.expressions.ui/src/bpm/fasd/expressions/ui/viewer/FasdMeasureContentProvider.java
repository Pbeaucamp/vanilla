package bpm.fasd.expressions.ui.viewer;

import java.util.ArrayList;
import java.util.List;

import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPSchema;

import bpm.fasd.expressions.api.model.FasdDimension;
import bpm.fasd.expressions.api.model.FasdMeasure;
import bpm.fasd.expressions.api.model.FormatingOperator;
import bpm.studio.expressions.core.measures.impl.Dimension;
import bpm.studio.expressions.core.measures.impl.Measure;
import bpm.studio.expressions.ui.measure.MeasureContentProvider;

public class FasdMeasureContentProvider extends MeasureContentProvider{

	public static final String FORMATING_FUNCTIONS = "Formating Functions";

	
	private Dimension[] dimensions;
	private Measure[] measures;
	
	public FasdMeasureContentProvider(OLAPSchema o){
		super(true, false, true, true);
		
		dimensions = new Dimension[o.getDimensions().size()];
		for(int i = 0; i < dimensions.length; i++){
			dimensions[i] = new Dimension(new FasdDimension(o.getDimensions().get(i)));
		}
		
		measures = new Measure[o.getMeasures().size()];
		
		
		for(int i = 0; i < measures.length; i++){
			measures[i] = new FasdMeasure(o.getMeasures().get(i));
		}
	}
	@Override
	public Dimension[] getDimensions() {
		return dimensions;
	}

	@Override
	public Measure[] getMeasures() {
		return measures;
	}

	@Override
	public boolean hasDimensions() {
		return dimensions.length > 0;
	}

	@Override
	public boolean hasMeasures() {
		return measures.length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] roots = super.getElements(inputElement);
		
		List<Object> l = new ArrayList<Object>();
		
		for(Object o : roots){
			l.add(o);
		}
		
		l.add(FORMATING_FUNCTIONS);
		
		return l.toArray(new Object[l.size()]);
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement == FORMATING_FUNCTIONS){
			return FormatingOperator.operators;
		}
		return super.getChildren(parentElement);
	}
	
	@Override
	public boolean hasChildren(Object element) {
		if (element == FORMATING_FUNCTIONS){
			return FormatingOperator.operators.length > 0;
		}
		return super.hasChildren(element);
	}
}
