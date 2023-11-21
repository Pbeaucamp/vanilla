package bpm.gateway.core.transformations.gid;

import bpm.gateway.core.transformations.SortElement;
import bpm.gateway.core.transformations.SortTransformation;

public class SortNode extends GidNode<SortTransformation>{

	public SortNode(SortTransformation transformation) {
		super(transformation);
	}

	@Override
	public Query evaluteQuery() {
		Query q = getChilds().get(0).evaluteQuery();
		
		Query query = new Query(q);
		for(SortElement es : getTransformation().getSorts()){
			try{
				String ord = " " + es.getColumnSort().split("::")[1] + " ";
				if (es.isType()){
					ord = ord + " ASC";
				}
				else{
					ord = ord + " DESC";
				}
				
				query.addOrder(ord);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		return query;
	}
	
}
