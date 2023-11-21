package bpm.gateway.core.transformations.gid;

import bpm.gateway.core.transformations.SelectionTransformation;

public class SelectNode extends GidNode<SelectionTransformation>{

	public SelectNode(SelectionTransformation transformation) {
		super(transformation);
	}

	@Override
	public Query evaluteQuery() {
		Query query = new Query();
		
		for(GidNode child : getChilds()){
			Query q = child.evaluteQuery();
			
			for(Integer i : getTransformation().getOutputedFor(child.getTransformation())){
				query.addSelect(q.getSelect().get(i));
			}
			
			for(String s : q.getFrom()){
				query.addFrom(s);
			}
			
			for(String s : q.getGroups()){
				query.addGroup(s);
			}
			
			for(String s : q.getWhere()){
				query.addWhere(s);
			}
		}
		return query;
	}

	
}
