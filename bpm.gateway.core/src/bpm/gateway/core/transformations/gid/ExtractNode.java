package bpm.gateway.core.transformations.gid;

import bpm.gateway.core.transformations.inputs.DataBaseInputStream;

public class ExtractNode extends GidNode<DataBaseInputStream>{

	
	private enum ClauseType{
		select, from, where, group, order;
	}
	
	public ExtractNode(DataBaseInputStream transformation) {
		super(transformation);
	}

	
	private String[] extractClausePart(ClauseType type){
		String query = getTransformation().getDefinition();
		query = query.replace(";", "");
		int start = -1;
		int end = -1;
		switch (type) {
		case select:
			/*
			 * parse Select part
			 */
			start = query.toLowerCase().indexOf("select ") + 7;
			end = query.toLowerCase().indexOf(" from ");
			
			if (start >= 7){
				String select = query.substring(start, end);
				
				return select.split(",");
			}
			

		case from:
			start = query.toLowerCase().indexOf(" from ") + 6;
			
			if (start >= 6){
				if (query.toLowerCase().contains(" where ")){
					end = query.toLowerCase().indexOf(" where ");
				}
				else if (query.toLowerCase().contains(" group by ")){
					end = query.toLowerCase().indexOf(" group by ");
				}
				else if (query.toLowerCase().contains(" order by ")){
					end = query.toLowerCase().indexOf(" order by ");
				}
				
				if (end == -1){
					String from = query.substring(start);
					return from.split(",");
				}
				else{
					String from = query.substring(start, end);
					return from.split(",");
				}
			}
			
			
		case where:
			start = query.toLowerCase().indexOf(" where ") + 7;
			if (start >= 7){
				if (query.toLowerCase().contains(" group by ")){
					end = query.toLowerCase().indexOf(" group by ");
				}
				else if (query.toLowerCase().contains(" order by ")){
					end = query.toLowerCase().indexOf(" order by ");
				}
				
				if (end == -1){
					String from = query.substring(start);
					return from.split(",");
				}
				else{
					String from = query.substring(start, end);
					return from.split(",");
				}
			}
			
		case group:
			start = query.toLowerCase().indexOf(" group by ") + 10;
			
			if (start >= 10){
				if (query.toLowerCase().contains(" order by ")){
					end = query.toLowerCase().indexOf(" order by ");
				}
				
				if (end == -1){
					String from = query.substring(start);
					return from.split(",");
				}
				else{
					String from = query.substring(start, end);
					return from.split(",");
				}
			}

		case order:
			start = query.toLowerCase().indexOf(" order by ") + 10;
			if (start >= 10){
				String order = query.substring(start);
				return order.split(",");
			}
			
			
		}
		return new String[]{};
	}
	
	
	@Override
	public Query evaluteQuery() {
		//XXX we dont take care of this node childs
		// it s not supposed to have some (imrbicated select are not supported for now)
		
		Query query = new Query();
		
			
		/*
		 * parse Select part
		 */
		for(String s : extractClausePart(ClauseType.select)){
			query.addSelect(s.trim());
			// TODO : we should prefix each column by its tableName
		}
		
		/*
		 * parse from part
		 */
					
		for(String s : extractClausePart(ClauseType.from)){
			query.addFrom(s.trim());
		}
		
		
		/*
		 * parse where
		 */
		for(String s : extractClausePart(ClauseType.where)){
			query.addWhere(s.trim());
		}
		
		
		/*
		 * parse GroupBy
		 */
				
		for(String s :extractClausePart(ClauseType.group)){
			query.addGroup(s.trim());
		}
		
		/*
		 * parse OrderBy
		 */
				
		for(String s :extractClausePart(ClauseType.order)){
			query.addOrder(s.trim());
		}
		
		return query;
	}
}
