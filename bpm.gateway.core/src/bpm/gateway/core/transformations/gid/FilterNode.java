package bpm.gateway.core.transformations.gid;

import java.util.Arrays;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.Filter;
import bpm.gateway.core.transformations.utils.Condition;

public class FilterNode extends GidNode<Filter> {

	public FilterNode(Filter transformation) {
		super(transformation);
	}

	@Override
	public Query evaluteQuery() {
		Query q1 = getChilds().get(0).evaluteQuery();
		
		Query query = new Query(q1);
		
		//add where conditions
		try {
			Filter filter = getTransformation();
			for(Condition c : filter.getConditions()) {
				String name = c.getStreamElementName();
				if(name.contains("::")) {
					name = name.substring(name.indexOf("::") + 2 , name.length());
				}
				int colIndex = filter.getDescriptor(getTransformation()).getElementIndex(name);
				int colType = filter.getDescriptor(getTransformation()).getStreamElements().get(colIndex).type;

				String where = name + c.getOperator() + c.getValue();
				int index = Arrays.asList(Condition.OPERATORS).indexOf(c.getOperator());
				switch(index) {
					case Condition.NULL:
						where = name + "is null";
						break;
					case Condition.IN:
						where = name + c.getOperator() + "(" + c.getValue(colType) + ")";
					case Condition.CONTAINS:
						where = name + "LIKE '%" + c.getValue() + "%'";
						break;
					case Condition.ENDSWIDTH:
						where = name + "LIKE '%" + c.getValue() + "'";
						break;
					case Condition.STARTSWIDTH:
						where = name + "LIKE '" + c.getValue() + "%'";
						break;
					case Condition.DIFFERENT:
					case Condition.EQUAL:
					case Condition.GREATER_EQ_THAN:
					case Condition.GREATER_THAN:
					case Condition.LESSER_EQ_THAN:
					case Condition.LESSER_THAN:
						where = name + c.getOperator() + c.getValue(colType);
						break;
					
				}
				
				query.addWhere(where);
			}
		} catch(ServerException e) {
			e.printStackTrace();
		}
		
		return query;
	}

}
