package bpm.gateway.core.transformations.gid;

import bpm.gateway.core.transformations.AggregateTransformation;
import bpm.gateway.core.transformations.utils.Aggregate;

public class AggregationNode extends GidNode<AggregateTransformation>{

	public AggregationNode(AggregateTransformation transformation) {
		super(transformation);
	}

	@Override
	public Query evaluteQuery() {
		Query q1 = getChilds().get(0).evaluteQuery();
		
		Query query = new Query();
		
		
		for(Integer i : getTransformation().getGroupBy()){
			query.addGroup(q1.getSelect().get(i));
			query.addSelect(q1.getSelect().get(i));
		}
		
		for(Aggregate agg : getTransformation().getAggregates()){
			String f = " "+ Aggregate.FUNCTIONS[agg.getFunction()] + 
						" (" + q1.getSelect().get(getTransformation().getStreamElementPositionByName(agg.getStreamElementName()))+ ")";
			
			query.addSelect(f);
			
		}
		
		
		
		for(String s : q1.getFrom()){
			query.addFrom(s);
		}
		
		for(String s : q1.getWhere()){
			query.addWhere(s);
		}
		
		// not sure about this one
		for(String s : q1.getGroups()){
			query.addGroup(s);
		}
		
		return query;
	}

}
