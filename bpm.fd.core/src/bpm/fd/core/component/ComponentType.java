package bpm.fd.core.component;

import java.util.HashMap;
import java.util.Map;

public enum ComponentType {
	CHART(0),
	FILTER(1),
	SLICER(2),
	DATA_GRID(3),
	GAUGE(4),
	REPORT(5),
	DASHLET(6),
	OLAP_VIEW(7),
	MAP(8),
	MARKDOWN(9),
	LABEL(10),
	IMAGE(11),
	URL(12),
	BUTTON(13),
	CLOCK(14),
	COMMENT(15),
	DIV(16),
	STACKABLE_CELL(17),
	DRILL_STACKABLE_CELL(18),
	DASHBOARD(19),
	KPI_CHART(20), 
	RCHART(21),
	DATAVIZ(22),
	D4C(23),
	KPI(24),
	DYNAMIC_LABEL(25),
	FLOURISH(26);
	
	private int type;

	private static Map<Integer, ComponentType> map = new HashMap<Integer, ComponentType>();
	static {
        for (ComponentType serverType : ComponentType.values()) {
            map.put(serverType.getType(), serverType);
        }
    }
	
	private ComponentType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public static ComponentType valueOf(int serverType) {
        return map.get(serverType);
    }
}