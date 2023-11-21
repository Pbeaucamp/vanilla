package bpm.fd.api.core.model.components.definition;

import java.util.ArrayList;
import java.util.List;

public enum OrderingType {
	NONE, ASC, DESC;
	
	public static List<OrderingType> getOrderTypes() {
		List<OrderingType> types = new ArrayList<OrderingType>();
		types.add(NONE);
		types.add(ASC);
		types.add(DESC);
		return types;
	}
}
