package metadata.client.wizards.measure;

import java.util.ArrayList;
import java.util.List;

import metadataclient.Activator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.complex.FmdtDimension;
import bpm.metadata.resource.complex.measures.impl.AggregationOperator;
import bpm.metadata.resource.complex.measures.impl.ConditionOperator;
import bpm.metadata.resource.complex.measures.impl.Dimension;
import bpm.metadata.resource.complex.measures.impl.DimensionFunctionOperator;
import bpm.metadata.resource.complex.measures.impl.DimensionLevel;
import bpm.metadata.resource.complex.measures.impl.MathOperator;
import bpm.metadata.resource.complex.measures.impl.Measure;

public class MeasureContentProvider implements ITreeContentProvider{

	private SQLDataSource dataSource;
	
	public Object[] getElements(Object inputElement) {
		dataSource = (SQLDataSource)inputElement;
		return MeasureGroupOperators.groups;
	}

	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	public Object[] getChildren(Object parentElement) {
		if (parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.MATH]){
			return MathOperator.operators;
		}
		else if (parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.AGGREGATION]){
			return AggregationOperator.operators;
		}
		else if (parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.FILTERS]){
			return ConditionOperator.operators;
		}
		else if (parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS_FUNCTIONS]){
			return DimensionFunctionOperator.operators;
		}
		else if (parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.MEASURE]){
			List l = dataSource.getDataStreams();
			return l.toArray(new Object[l.size()]);
		}
		else if (parentElement == MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS]){
			List<Dimension> l = new ArrayList<Dimension>();
			
			for(IResource r : Activator.getDefault().getModel().getResources()){
				if (!(r instanceof FmdtDimension)){
					continue;
				}
				FmdtDimension dim = (FmdtDimension)r;
				
				if (dim.getDataSource() == dataSource){
					l.add(new Dimension(dim));
				}
			}
			return l.toArray(new Object[l.size()]);
		}
		else if (parentElement instanceof IDataStream){
			List<Measure> l = new ArrayList<Measure>();
			
			for(IDataStreamElement e : ((IDataStream)parentElement).getElements()){
				if (e.getType().getParentType() == IDataStreamElement.Type.MEASURE){
					l.add(new Measure(e));
				}
			}
			
			return l.toArray(new Object[l.size()]);
		}
		else if (parentElement instanceof Dimension){
			List l = new ArrayList();
			int i = 0;
			for(IDataStreamElement e : ((Dimension)parentElement).getDimension().getLevels()){
				l.add(new DimensionLevel(((Dimension)parentElement).getDimension(), i++));
			}
			
			return l.toArray(new Object[l.size()]);
		}
		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof MathOperator){
			return MeasureGroupOperators.groups[MeasureGroupOperators.MATH];
		}
		else if (element instanceof Measure){
			
			return ((Measure)element).getField().getDataStream();
		}
		else if (element instanceof AggregationOperator){
			return MeasureGroupOperators.groups[MeasureGroupOperators.AGGREGATION];
		}
		else if (element instanceof ConditionOperator){
			return MeasureGroupOperators.groups[MeasureGroupOperators.FILTERS];
		}
		else if (element instanceof IDataStream){
			return MeasureGroupOperators.groups[MeasureGroupOperators.MEASURE];
		}
		else if (element instanceof Dimension){
			return MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS];
		}
		else if (element instanceof DimensionFunctionOperator){
			return MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS_FUNCTIONS];
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element == MeasureGroupOperators.groups[MeasureGroupOperators.MATH]){
			return true;
		}
		else if (element == MeasureGroupOperators.groups[MeasureGroupOperators.MEASURE]){
			return !dataSource.getDataStreams().isEmpty();		
		}
		else if (element == MeasureGroupOperators.groups[MeasureGroupOperators.AGGREGATION]){
			return true;
		}
		else if (element == MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS]){
			return true;
		}
		else if (element == MeasureGroupOperators.groups[MeasureGroupOperators.DIMENSIONS_FUNCTIONS]){
			return true;
		}
		
		else if (element == MeasureGroupOperators.groups[MeasureGroupOperators.FILTERS]){
			return true;
		}
		else if (element instanceof IDataStream){
			for(IDataStreamElement e : ((IDataStream)element).getElements()){
				if (e.getType().getParentType() == IDataStreamElement.Type.MEASURE){
					return true;
				}
			}
		}
		else if (element instanceof Dimension){
			return ((Dimension)element).getDimension().getLevels().size() > 0;
		}
		return false;
	}

}
