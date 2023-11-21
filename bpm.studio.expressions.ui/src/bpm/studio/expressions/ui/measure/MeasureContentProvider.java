package bpm.studio.expressions.ui.measure;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.studio.expressions.core.measures.impl.AggregationOperator;
import bpm.studio.expressions.core.measures.impl.ConditionOperator;
import bpm.studio.expressions.core.measures.impl.Dimension;
import bpm.studio.expressions.core.measures.impl.DimensionFunctionOperator;
import bpm.studio.expressions.core.measures.impl.DimensionLevel;
import bpm.studio.expressions.core.measures.impl.MathOperator;
import bpm.studio.expressions.core.measures.impl.Measure;
import bpm.studio.expressions.core.model.IField;



public abstract class MeasureContentProvider implements ITreeContentProvider{

	
	public Object[] getElements(Object inputElement) {
		List<String> l = new ArrayList<String>();
		
		if (includeMath){
			l.add(MeasureGroupOperators.groups[MeasureGroupOperators.MATH]);
		}
		
		if (includeAggregations){
			l.add(MeasureGroupOperators.groups[MeasureGroupOperators.AGGREGATION]);
		}
		
		if (includeDimFunctions){
			l.add(MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS_FUNCTIONS]);
		}
		
		if (includeFilter){
			l.add(MeasureGroupOperators.groups[MeasureGroupOperators.FILTERS]);
		}
		
		l.add(MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS]);
		
		l.add(MeasureGroupOperators.groups[MeasureGroupOperators.MEASURE]);
		
		
		return l.toArray(new String[l.size()]);
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

	
	public abstract Measure[] getMeasures();
	public abstract boolean hasMeasures();
	public abstract Dimension[] getDimensions();
	public abstract boolean hasDimensions();
	
	
	protected boolean includeMath;
	protected boolean includeAggregations;
	protected boolean includeFilter;
	protected boolean includeDimFunctions;
	
	public MeasureContentProvider(boolean includeMath, boolean includeAggregations, boolean includeFilter, boolean includeDimFunctions){
		this.includeMath = includeMath;
		this.includeAggregations = includeAggregations;
		this.includeFilter = includeFilter;
		this.includeDimFunctions = includeDimFunctions;
	}
	
	public Object[] getChildren(Object parentElement) {
		if (includeMath && parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.MATH]){
			return MathOperator.operators;
		}
		else if (includeAggregations && parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.AGGREGATION]){
			return AggregationOperator.operators;
		}
		else if (includeFilter && parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.FILTERS]){
			return ConditionOperator.operators;
		}
		else if (includeDimFunctions && parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS_FUNCTIONS]){
			return DimensionFunctionOperator.operators;
		}
		else if (parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.MEASURE]){
			
			return getMeasures();
		}
		else if (parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS]){
			return getDimensions();
		}
		
		else if (parentElement instanceof Dimension){
			List l = new ArrayList();
			int i = 0;
			for(IField e : ((Dimension)parentElement).getDimension().getLevels()){
				l.add(new DimensionLevel(((Dimension)parentElement).getDimension(), i++));
			}
			
			return l.toArray(new Object[l.size()]);
		}
		return null;
	}

	public Object getParent(Object element) {
		if (includeMath && element instanceof MathOperator){
			return MeasureGroupOperators.groups[MeasureGroupOperators.MATH];
		}
		else if (element instanceof Measure){
			
			return ((Measure)element).getField().getParent();
		}
		else if (includeAggregations && element instanceof AggregationOperator){
			return MeasureGroupOperators.groups[MeasureGroupOperators.AGGREGATION];
		}
		else if (includeFilter && element instanceof ConditionOperator){
			return MeasureGroupOperators.groups[MeasureGroupOperators.FILTERS];
		}
		else if (element instanceof IField){
			return MeasureGroupOperators.groups[MeasureGroupOperators.MEASURE];
		}
		else if (element instanceof Dimension){
			return MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS];
		}
		else if (includeDimFunctions && element instanceof DimensionFunctionOperator){
			return MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS_FUNCTIONS];
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element == MeasureGroupOperators.groups[MeasureGroupOperators.MATH]){
			return true;
		}
		else if (element == MeasureGroupOperators.groups[MeasureGroupOperators.MEASURE]){
			return hasMeasures();		
		}
		else if (element == MeasureGroupOperators.groups[MeasureGroupOperators.AGGREGATION]){
			return true;
		}
		else if (element == MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS]){
			return hasDimensions();
		}
		else if (element == MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS_FUNCTIONS]){
			return true;
		}
		
		else if (element == MeasureGroupOperators.groups[MeasureGroupOperators.FILTERS]){
			return true;
		}
		else if (element instanceof IField){
			return false;
		}
		else if (element instanceof Dimension){
			return ((Dimension)element).getDimension().getLevels().size() > 0;
		}
		return false;
	}

}
