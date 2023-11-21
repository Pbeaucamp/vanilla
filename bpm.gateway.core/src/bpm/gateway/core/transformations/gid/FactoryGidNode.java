package bpm.gateway.core.transformations.gid;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.AggregateTransformation;
import bpm.gateway.core.transformations.Filter;
import bpm.gateway.core.transformations.SelectionTransformation;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.core.transformations.SortTransformation;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;

public class FactoryGidNode {

	public GidNode createNode(Transformation tr) throws Exception{
		if (tr instanceof DataBaseInputStream){
			return new ExtractNode((DataBaseInputStream)tr);
		}
		else if (tr instanceof SimpleMappingTransformation){
			return new MappingNode((SimpleMappingTransformation)tr);
		}
		else if (tr instanceof SortTransformation){
			
			return new SortNode((SortTransformation)tr);
		}
		else if (tr instanceof AggregateTransformation){
			return new AggregationNode((AggregateTransformation)tr);
		}
		else if (tr instanceof SelectionTransformation){
			return new SelectNode((SelectionTransformation)tr);
		}
		else if (tr instanceof Filter){
			return new FilterNode((Filter)tr);
		}
		
		throw new  Exception(tr.getClass().getSimpleName() + " step not supported in ELT mode");
	}
}
