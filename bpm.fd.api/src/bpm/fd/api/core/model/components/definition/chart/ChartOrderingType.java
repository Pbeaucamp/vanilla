package bpm.fd.api.core.model.components.definition.chart;

import java.util.ArrayList;
import java.util.List;

public enum ChartOrderingType {
	CATEGORY_ASC, CATEGORY_DESC,CATEGORY_LABEL_ASC, CATEGORY_LABEL_DESC, VALUE_ASC, VALUE_DESC;
	
	public static List<ChartOrderingType> getOrderTypes() {
		List<ChartOrderingType> types = new ArrayList<ChartOrderingType>();
		types.add(CATEGORY_ASC);
		types.add(CATEGORY_DESC);
		types.add(CATEGORY_LABEL_ASC);
		types.add(CATEGORY_LABEL_DESC);
		types.add(VALUE_ASC);
		types.add(VALUE_DESC);
		return types;
	}
}
